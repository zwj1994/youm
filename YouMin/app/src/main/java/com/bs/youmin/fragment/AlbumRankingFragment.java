package com.bs.youmin.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bs.youmin.R;
import com.bs.youmin.activity.AlbumRankingActivity;
import com.bs.youmin.adapter.AlbumRankingListAdapter;
import com.bs.youmin.entity.Ip;
import com.bs.youmin.entity.YAlbum;
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
 *  包名：    com.bs.youmin.fragment
 *  文件名:   AlbumRankingFragment
 *  创建者:   ZWJ
 *  创建时间:  2018/03/23 19:48
 *  描述：    相册排行
 */
public class AlbumRankingFragment extends Fragment implements View.OnClickListener{

    private ApiImp apiImp;

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
    private AnimatorSet addBillTranslate4;
    private AnimatorSet addBillTranslate5;
    private AnimatorSet addBillTranslate6;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            load_more.setVisibility(View.GONE);
        }
    };

    public AlbumRankingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_ranking, container, false);
        initView(view);

        setDefaultValues();
        bindEvents();
        return view;
    }

    private void initView(View view) {

        fab01Add = (FloatingActionButton)view.findViewById(R.id.fab01Add);
        rlAddBill = (RelativeLayout)view.findViewById(R.id.rlAddBill);
        rlAddBill.setOnClickListener(this);

        for (int i = 0; i < llId.length;i++){
            ll[i] = (LinearLayout)view.findViewById(llId[i]);
        }
        for (int i = 0;i < fabId.length; i++){
            fab[i] = (FloatingActionButton)view.findViewById(fabId[i]);
        }

        load_more = (LinearLayout) view.findViewById(R.id.ranking_load_more);
        ranking_load_more_tv = (TextView) view.findViewById(R.id.ranking_load_more_tv);
        mListView = (ListView) view.findViewById(R.id.mListView);

        /**
         * 监听列表滑动到最后
         */
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                L.i("============="+i);
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
        Call<List<YAlbum>> call = apiImp.getAlbumRanking(page);
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
}
