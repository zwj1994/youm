package com.bs.youmin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bs.youmin.R;
import com.bs.youmin.adapter.AlbumRankingDataAdapter;
import com.bs.youmin.entity.Ip;
import com.bs.youmin.entity.YPhoto;
import com.bs.youmin.imp.ApiImp;
import com.bs.youmin.util.L;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 *  项目名：  YouMin
 *  包名：    com.bs.youmin.activity
 *  文件名:   AlbumRankingActivity
 *  创建者:   ZWJ
 *  创建时间:  2018/03/24 16:04
 *  描述：    相册排名详情
 */
public class AlbumRankingActivity extends BaseActivity {

    private GridView mGridView;
    private TextView loadMore_tv;
    private String name;
    private String aId;
    private ApiImp apiImp;
    private int page = 0;

    private LinearLayout ll_load_more;

    private AlbumRankingDataAdapter albumRankingDataAdapter;
    private List<YPhoto> mList = new ArrayList<>();
    private ArrayList<String>mListBigUrl = new ArrayList<>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ll_load_more.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ralbum_ranking);

        initView();
    }

    private void initView() {

        mGridView = (GridView) findViewById(R.id.mGridView);
        ll_load_more = (LinearLayout) findViewById(R.id.ll_load_more);
        loadMore_tv = (TextView) findViewById(R.id.album_load_more);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");//相册名称
        aId = intent.getStringExtra("aId");  //相册编号

        if (!TextUtils.isEmpty(name)) {
            getSupportActionBar().setTitle(name);
        }

        if (!TextUtils.isEmpty(aId)) {
            loadData(true);
        }

        /**
         * 监听滑动到最后
         */
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (mGridView.getLastVisiblePosition() == mGridView.getCount() - 1) {
                        loadMore_tv.setText(getString(R.string.progressbar_load_more));
                        ll_load_more.setVisibility(View.VISIBLE);
                        loadData(false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        /**
         * Grid Item点击事件监听
         */
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AlbumRankingActivity.this, GalleryActivity.class);
                intent.putExtra("position", i);
                intent.putStringArrayListExtra("bigUrl", mListBigUrl);
                startActivity(intent);
            }
        });
    }

    /**
     * 加载数据
     * @param loadFirst
     */
    private void loadData(final boolean loadFirst) {
        page++;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Ip.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiImp = retrofit.create(ApiImp.class);
        Call<List<YPhoto>> call = apiImp.getAlbumPhoto(aId,page);
        //请求数据
        call.enqueue(new Callback<List<YPhoto>>() {
            @Override
            public void onResponse(Call<List<YPhoto>> call, Response<List<YPhoto>> response) {
                if (response.isSuccessful()) {
                    if(null == response.body() || response.body().isEmpty()){
                        if(!loadFirst){
                            page--;
                            loadMore_tv.setText(getString(R.string.progressbar_not_have_more));
                        }
                    }else {
                        parsingJson(response.body());
                        if(!loadFirst){
                            albumRankingDataAdapter.notifyDataSetChanged();
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(1,300);
                }
            }

            @Override
            public void onFailure(Call<List<YPhoto>> call, Throwable t) {
                L.i(t.toString());
            }
        });
    }

    /**
     * 解析数据
     * @param list
     */
    private void parsingJson(List<YPhoto> list) {
        mList.addAll(list);
        for (YPhoto photo : list){
            mListBigUrl.add(photo.getpBig());
        }
        albumRankingDataAdapter = new AlbumRankingDataAdapter(AlbumRankingActivity.this, mList);
        mGridView.setAdapter(albumRankingDataAdapter);
    }
}
