package com.bs.youmin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.youmin.R;
import com.bs.youmin.activity.AlbumRankingActivity;
import com.bs.youmin.adapter.AlbumRankingListAdapter;
import com.bs.youmin.entity.Ip;
import com.bs.youmin.entity.User;
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.imp.ApiImp;
import com.bs.youmin.model.ResultStatus;
import com.bs.youmin.util.L;
import com.bs.youmin.util.SaveUserUtil;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 *  项目名：  YouMin
 *  包名：    com.bs.youmin.fragment
 *  文件名:   MyAlbumFragment
 *  创建者:   ZWJ
 *  创建时间:  2018/03/23 19:48
 *  描述：    我的
 */
public class MyAlbumFragment extends Fragment {

    private ApiImp apiImp;

    private ListView mListView;
    private AlbumRankingListAdapter albumRankingListAdapter;
    private List<YAlbum> mList =  new ArrayList<>();

    private LinearLayout load_more;
    private TextView ranking_load_more_tv;
    private int page = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            load_more.setVisibility(View.GONE);
        }
    };

    private LoginFragment.OnButtonClick onButtonClick;//2、定义接口成员变量

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_ranking, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        load_more = (LinearLayout) view.findViewById(R.id.ranking_load_more);
        ranking_load_more_tv = (TextView) view.findViewById(R.id.ranking_load_more_tv);
        mListView = (ListView) view.findViewById(R.id.mListView);

        /**
         * 监听列表滑动到最后
         */
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (mListView.getLastVisiblePosition() == mListView.getCount() - 1) {
                        ranking_load_more_tv.setText(getString(R.string.progressbar_load_more));
                        load_more.setVisibility(View.VISIBLE);
                        loadData(false);
                    }
                }
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
        });

        /**
         * 列表Item单击事件
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), AlbumRankingActivity.class);
                intent.putExtra("name",mList.get(i).getaName());
                intent.putExtra("aId",mList.get(i).getaId());
                startActivity(intent);
            }
        });

        loadData(true);
    }

    /**
     * 加载数据
     * @param loadFirst
     */
    private void loadData(final boolean loadFirst){
        page++;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Ip.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiImp = retrofit.create(ApiImp.class);
        final User user = SaveUserUtil.loadAccount(getActivity());
        Call<List<YAlbum>> call = apiImp.getMyAlbum(page,user.getUsername()+"_"+user.getToken());
        //请求数据
        call.enqueue(new Callback<List<YAlbum>>() {
            @Override
            public void onResponse(Call<List<YAlbum>> call, Response<List<YAlbum>> response) {
                 if (response.isSuccessful()) {
                    if(null == response.body() || response.body().isEmpty()){
                        if(!loadFirst){
                            page--;
                            ranking_load_more_tv.setText(getString(R.string.progressbar_not_have_more));
                        }
                    }else {
                        parsingJson(response.body());
                        if(!loadFirst){
                            albumRankingListAdapter.notifyDataSetChanged();
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(1,300);
                }else if(response.code() == HttpStatus.SC_UNAUTHORIZED){
                    if(onButtonClick!=null){
                        onButtonClick.onClick(load_more);
                    }
                    Toast.makeText(getActivity(), "您还没有登录，请先登录",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<YAlbum>> call, Throwable t) {
                L.i(t.toString());
            }
        });
    }

    //解析
    private void parsingJson(List<YAlbum> list) {
        mList.addAll(list);
        albumRankingListAdapter = new AlbumRankingListAdapter(getActivity(), mList);
        mListView.setAdapter(albumRankingListAdapter);
    }

    //定义接口变量的get方法
    public LoginFragment.OnButtonClick getOnButtonClick() {
        return onButtonClick;
    }
    //定义接口变量的set方法
    public void setOnButtonClick(LoginFragment.OnButtonClick onButtonClick) {
        this.onButtonClick = onButtonClick;
    }
    //1、定义接口
    public interface OnButtonClick{
        public void onClick(View view);
    }
}
