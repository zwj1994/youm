package com.bs.youmin.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.youmin.R;
import com.bs.youmin.activity.AlbumRankingActivity;
import com.bs.youmin.activity.CreateAlbumActivity;
import com.bs.youmin.adapter.AlbumRankingListAdapter;
import com.bs.youmin.entity.Ip;
import com.bs.youmin.entity.User;
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.imp.ApiImp;
import com.bs.youmin.model.ResultStatus;
import com.bs.youmin.util.L;
import com.bs.youmin.util.SaveUserUtil;
import com.demievil.library.RefreshLayout;
import com.squareup.picasso.Picasso;

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
public class MyAlbumFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ApiImp apiImp;

    RefreshLayout mRefreshLayout;
    private ListView mListView;
    private AlbumRankingListAdapter albumRankingListAdapter;
    private List<YAlbum> mList =  new ArrayList<>();

    private LinearLayout load_more;
    private TextView ranking_load_more_tv;
    private int page = 0;

    private FloatingActionButton fab01Add;
    private boolean isAdd = false;
    private RelativeLayout rlAddBill;
    private int[] llId = new int[]{R.id.ll01,R.id.ll02,R.id.ll03};
    private LinearLayout[] ll = new LinearLayout[llId.length];
    private int[] fabId = new int[]{R.id.miniFab01,R.id.miniFab02,R.id.miniFab03};
    private FloatingActionButton[] fab = new FloatingActionButton[fabId.length];
    private AnimatorSet addBillTranslate1;
    private AnimatorSet addBillTranslate2;
    private AnimatorSet addBillTranslate3;

//    private TextView tv_more;
//    private ProgressBar pb;
//    View footerLayout;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            tv_more.setEnabled(true);
//            tv_more.setText("加载更多");
//            tv_more.setVisibility(View.GONE);
//            pb.setVisibility(View.GONE);
//            mRefreshLayout.setRefreshing(false);

            ranking_load_more_tv.setText(getString(R.string.progressbar_load_more));
            load_more.setVisibility(View.GONE);
        }
    };

    private LoginFragment.OnButtonClick onButtonClick;//2、定义接口成员变量

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_ranking, container, false);
        initView(view);
        setDefaultValues();
        bindEvents();
        return view;
    }

    private void initView(View view) {
//        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.fragment_myalbum_freshLayout);
        fab01Add = (FloatingActionButton)view.findViewById(R.id.fab01Add);
        fab01Add.setVisibility(View.GONE);
        rlAddBill = (RelativeLayout)view.findViewById(R.id.rlAddBill);
        rlAddBill.setOnClickListener(this);

        for (int i = 0; i < llId.length;i++){
            ll[i] = (LinearLayout)view.findViewById(llId[i]);
        }
        for (int i = 0;i < fabId.length; i++){
            fab[i] = (FloatingActionButton)view.findViewById(fabId[i]);
        }
//        footerLayout = getActivity().getLayoutInflater().inflate(R.layout.list_item_more, null);
//        tv_more = (TextView) footerLayout.findViewById(R.id.text_more);
//        pb = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);

        load_more = (LinearLayout) view.findViewById(R.id.ranking_load_more);
        ranking_load_more_tv = (TextView) view.findViewById(R.id.ranking_load_more_tv);
        mListView = (ListView) view.findViewById(R.id.mListView);
//        mListView.addFooterView(footerLayout);

//        mRefreshLayout.setOnRefreshListener(this);
//        mRefreshLayout.setOnLoadListener(this);

//        mRefreshLayout.setChildView(mListView);
//        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_dark,
//                android.R.color.holo_red_light,
//                android.R.color.holo_orange_dark);

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
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (i == 0) {
                    fab01Add.show();
                } else {
                    fab01Add.hide();
                }
            }
        });


        /**
         * 列表Item单击事件
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(view.getId() == R.id.footer_layout){
//                    tv_more.setVisibility(View.GONE);
//                    pb.setVisibility(View.VISIBLE);
//                    loadData(false);
                }else{
                    Intent intent = new Intent(getActivity(), AlbumRankingActivity.class);
                    intent.putExtra("name",mList.get(i).getaName());
                    intent.putExtra("aId",mList.get(i).getaId());
                    startActivity(intent);
                }

            }
        });

        loadData(true);
    }

    public void update() {
        L.i("==========update");
        loadData(true);
    }

    /**
     * 加载数据
     * @param loadFirst
     */
    private void loadData(final boolean loadFirst){
        if(loadFirst){
            page = 0;
        }
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
                     fab01Add.setVisibility(View.VISIBLE);
                    if(null == response.body() || response.body().isEmpty()){
                        if(!loadFirst){
                            page--;
                            ranking_load_more_tv.setText(getString(R.string.progressbar_not_have_more));
                        }
                    }else {
                        parsingJson(response.body(),loadFirst);
                        if(!loadFirst){
                            albumRankingListAdapter.notifyDataSetChanged();
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(1,300);
                }else if(response.code() == HttpStatus.SC_UNAUTHORIZED){
                     fab01Add.setVisibility(View.GONE);
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
    private void parsingJson(List<YAlbum> list,final boolean loadFirst) {
        if(loadFirst){
            mList = new ArrayList<>();
        }
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

    @Override
    public void onRefresh() {
        loadData(true);
        mRefreshLayout.setLoading(false);
    }


    //1、定义接口
    public interface OnButtonClick{
        public void onClick(View view);
    }


    private void setDefaultValues(){
        addBillTranslate1 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),R.anim.add_bill_anim);
        addBillTranslate2 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),R.anim.add_bill_anim);
        addBillTranslate3 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),R.anim.add_bill_anim);
    }
    private void bindEvents(){
        fab01Add.setOnClickListener(this);
        for (int i = 0;i < fabId.length; i++){
            fab[i].setOnClickListener(this);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlAddBill:
                hideFABMenu();
                break;
            case R.id.fab01Add:
                fab01Add.setImageResource(isAdd ? R.drawable.icon_add_btn:R.drawable.icon_add_click_btn);
                isAdd = !isAdd;
                rlAddBill.setVisibility(isAdd ? View.VISIBLE : View.GONE);
                if (isAdd) {
                    addBillTranslate1.setTarget(ll[0]);
                    addBillTranslate1.start();
                    addBillTranslate2.setTarget(ll[1]);
                    addBillTranslate2.setStartDelay(150);
                    addBillTranslate2.start();
                    addBillTranslate3.setTarget(ll[2]);
                    addBillTranslate3.setStartDelay(200);
                    addBillTranslate3.start();
                }
                break;
            case R.id.miniFab01:
                hideFABMenu();
                break;
            case R.id.miniFab02:
                hideFABMenu();
                break;
            case R.id.miniFab03:
                Intent intent = new Intent(getActivity(), CreateAlbumActivity.class);
                startActivityForResult(intent,1);
                hideFABMenu();
                break;
            default:
                break;
        }
    }
    private void hideFABMenu(){
        rlAddBill.setVisibility(View.GONE);
        fab01Add.setImageResource(R.drawable.icon_add_btn);
        isAdd = false;
    }

//    @Override
//    public void onLoad() {
//        L.i("============onLoad");
//        if (news_size == mlist.size()) {
//            tv_more.setText("数据已加载完毕");
//            tv_more.setEnabled(false);
//            return;
//        }
//        tv_more.setVisibility(View.GONE);
//        pb.setVisibility(View.VISIBLE);
//        ranking_load_more_tv.setText(getString(R.string.progressbar_load_more));
//        load_more.setVisibility(View.VISIBLE);
//        loadData(false);
//    }

    /**
     * 下拉刷新方法
     */
//    @Override
//    public void onRefresh() {
//        loadData(true);
//        mRefreshLayout.setLoading(false);
//    }

}
