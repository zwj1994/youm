package com.bs.youmin.fragment;
/*
 *  项目名：  YouMin
 *  包名：    com.bs.youmin.fragment
 *  文件名:   HomePageFragment
 *  创建者:   ZWJ
 *  创建时间:  2018/03/30 9:37
 *  描述：    首页
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.bs.youmin.R;
import com.bs.youmin.activity.AlbumRankingActivity;
import com.bs.youmin.activity.SpecialActivity;
import com.bs.youmin.adapter.AlbumPublicDataAdapter;
import com.bs.youmin.adapter.AlbumRankingDataAdapter;
import com.bs.youmin.adapter.AlbumRankingListAdapter;
import com.bs.youmin.adapter.MainGridAdapter;
import com.bs.youmin.entity.ApiModel;
import com.bs.youmin.entity.Constants;
import com.bs.youmin.entity.Ip;
import com.bs.youmin.entity.MainGridModel;
import com.bs.youmin.entity.WallpaperApiModel;
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.entity.YBanner;
import com.bs.youmin.entity.YPhoto;
import com.bs.youmin.imp.ApiImp;
import com.bs.youmin.util.DateUtil;
import com.bs.youmin.util.GlideUtils;
import com.bs.youmin.util.L;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomePageFragment extends Fragment {

    private ApiImp apiImp;
    private ViewPager mViewPager;
    private GridView mGridView;
    private List<View> mView = new ArrayList<>();

    private List<String> mListUrl = new ArrayList<>();
    private List<String> mListTitle = new ArrayList<>();

    private AlbumPublicDataAdapter albumRankingDataAdapter;
    private List<YAlbum> mList = new ArrayList<>();
    private ArrayList<String>mListBigUrl = new ArrayList<>();

    private LinearLayout publicAlbum_load_more;
    private TextView publicAlbum_load_more_tv;
    private int page = 0;

    /**
     * 轮播
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.HANDLER_AUTO_SHUFFLING:
                    int index = mViewPager.getCurrentItem();
                    if (index >= mView.size()-1) {
                        mViewPager.setCurrentItem(0);
                    }else {
                        mViewPager.setCurrentItem(index + 1);
                    }
                    mHandler.sendEmptyMessageDelayed(Constants.HANDLER_AUTO_SHUFFLING, 3000);
                    break;
                case 1:
                    publicAlbum_load_more.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化视图
     * @param view
     */
    private void initView(View view) {

        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);
        mGridView = (GridView) view.findViewById(R.id.mGridView);
        publicAlbum_load_more = (LinearLayout) view.findViewById(R.id.publicAlbum_load_more);
        publicAlbum_load_more_tv = (TextView) view.findViewById(R.id.publicAlbum_load_more_tv);
        /**
         * 监听列表滑动到最后
         */
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (mGridView.getLastVisiblePosition() == mGridView.getCount() - 1) {
                        publicAlbum_load_more_tv.setText(getString(R.string.progressbar_load_more));
                        publicAlbum_load_more.setVisibility(View.VISIBLE);
                        getPublicAlbumData(false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), AlbumRankingActivity.class);
                intent.putExtra("name",mList.get(i).getaName());
                intent.putExtra("aId",mList.get(i).getaId());
                startActivity(intent);
            }
        });



        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Ip.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();
        apiImp = retrofit.create(ApiImp.class);
        /**
         * 请求轮播图
         */
        Call<List<YBanner>> call = apiImp.getBanner();
        call.enqueue(new Callback<List<YBanner>>() {
            @Override
            public void onResponse(Call<List<YBanner>> call, Response<List<YBanner>> response) {
                if (response.isSuccessful()) {
                    parsingBanner(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<YBanner>> call, Throwable t) {
                L.i(t.toString());
            }
        });

         //请求全部相册
        getPublicAlbumData(true);
    }

    /**
     * 请求全部相册
     * @param loadFirst
     */
    private void getPublicAlbumData(final boolean loadFirst){
        page++;
        Call<List<YAlbum>> call_album = apiImp.allPublicAlbum(page);
        call_album.enqueue(new Callback<List<YAlbum>>() {
            @Override
            public void onResponse(Call<List<YAlbum>> call, Response<List<YAlbum>> response) {
                if (response.isSuccessful()) {
                    if(null == response.body() || response.body().isEmpty()){
                        if(!loadFirst){
                            page--;
                            publicAlbum_load_more_tv.setText(getString(R.string.progressbar_not_have_more));
                        }
                    }else {
                        parsingEveryDay(response.body());
                        if(!loadFirst){
                            albumRankingDataAdapter.notifyDataSetChanged();
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(1,300);
                }
            }
            @Override
            public void onFailure(Call<List<YAlbum>> call, Throwable t) {
                L.i(t.toString());
            }
        });
    }


    /**
     * 解析轮播图
     * @param list
     */
    private void parsingBanner(final List<YBanner> list) {
        if(null != list && !list.isEmpty()){
            for(final YBanner model : list){
                View view = View.inflate(getActivity(), R.layout.fragment_home_pager_banner_item, null);
                ImageView iv_banner_icon = (ImageView) view.findViewById(R.id.iv_banner_icon);
                GlideUtils.loadImageCrop(getActivity(), model.getbIcon(), iv_banner_icon);
                TextView tv_banner_title = (TextView) view.findViewById(R.id.tv_banner_title);
                TextView tv_banner_content = (TextView) view.findViewById(R.id.tv_banner_content);
                tv_banner_title.setText(model.getbName());
                tv_banner_content.setText(model.getbDesc());
                RelativeLayout rl_banner = (RelativeLayout) view.findViewById(R.id.rl_banner);
                rl_banner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SpecialActivity.class);
                        intent.putExtra("name", model.getbName());
                        intent.putExtra("desc", model.getbDesc());
                        intent.putExtra("icon", model.getbIcon());
                        intent.putExtra("url", model.getbUrl());
                        startActivity(intent);
                    }
                });
                mView.add(view);
            }
            mViewPager.setAdapter(new BannerAdapter());
            //mViewPager.setCurrentItem(mListTitle.size() * 100);
            mHandler.sendEmptyMessageDelayed(Constants.HANDLER_AUTO_SHUFFLING, 3000);
        }
    }

    private class BannerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mView.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(mView.get(position));
            return mView.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(mView.get(position));
            //super.destroyItem(container, position, object);
        }
    }

    /**
     * 解析所有公开的相册
     * @param list
     */
    private void parsingEveryDay(List<YAlbum> list) {
        mList.addAll(list);
        for (YAlbum model : list){
            mListBigUrl.add(model.getaCover());
        }
        albumRankingDataAdapter = new AlbumPublicDataAdapter(getActivity(), mList);
        mGridView.setAdapter(albumRankingDataAdapter);
    }
}
