package com.bs.youmin.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bs.youmin.R;
import com.bs.youmin.activity.AlbumRankingActivity;
import com.bs.youmin.activity.CreateAlbumActivity;
import com.bs.youmin.adapter.AlbumRankingListAdapter;
import com.bs.youmin.entity.Ip;
import com.bs.youmin.entity.User;
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.imp.ApiImp;
import com.bs.youmin.imp.YmCallBack;
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
public class MyAlbumFragment extends BaseFragment implements View.OnClickListener {

    private ApiImp apiImp;

    private ListView mListView;
    private AlbumRankingListAdapter albumRankingListAdapter;
    private List<YAlbum> mList =  new ArrayList<>();

    private LinearLayout load_more;
    private TextView ranking_load_more_tv;
    private int page = 0;

    private FloatingActionButton fab01Add;

    private YmCallBack callBack;

    public void setCallBack(YmCallBack callBack){
        this.callBack = callBack;
    }

    /**
     * Handler业务处理
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ranking_load_more_tv.setText(getString(R.string.progressbar_load_more));
            load_more.setVisibility(View.GONE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_ranking, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        fab01Add = (FloatingActionButton)view.findViewById(R.id.fab01Add);
        fab01Add.setOnClickListener(this);

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
                Intent intent = new Intent(getActivity(), AlbumRankingActivity.class);
                intent.putExtra("name",mList.get(i).getaName());
                intent.putExtra("aId",mList.get(i).getaId());
                startActivity(intent);
            }
        });
        loadData(true);
    }

    /**
     * 更新列表数据
     */
    public void update() {
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
                    if(callBack != null){
                        callBack.work(0);
                    }
                     showToast("您还没有登录，请先登录");
                }
            }

            @Override
            public void onFailure(Call<List<YAlbum>> call, Throwable t) {
                showToast("网络状况不佳");
                L.i(t.toString());
            }
        });
    }

    /**
     * 解析数据
     * @param list
     * @param loadFirst
     */
    private void parsingJson(List<YAlbum> list,final boolean loadFirst) {
        if(loadFirst){
            mList = new ArrayList<>();
        }
        mList.addAll(list);
        albumRankingListAdapter = new AlbumRankingListAdapter(getActivity(), mList);
        mListView.setAdapter(albumRankingListAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab01Add:
                toGrantAuthorization(new AuthorizationCallBack(){
                    @Override
                    public void work() {
                        Intent intent = new Intent(getActivity(), CreateAlbumActivity.class);
                        startActivityForResult(intent,1);
                    }
                });
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("权限已申请");
            } else {
                showToast("权限已拒绝");
            }
        }else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA){
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i]);
                    if (showRequestPermission) {
                        showToast("权限未申请");
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 权限申请列表
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    List<String> mPermissionList = new ArrayList<>();

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;

    /**
     * 授权
     * @param callBack
     */
    private void toGrantAuthorization(AuthorizationCallBack callBack){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionList.clear();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(getActivity(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
                callBack.work();
            } else {//请求权限方法
                showToast("请允许相关权限");
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(getActivity(), permissions, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
            }
        }else{
            callBack.work();
        }
    }

    interface AuthorizationCallBack{
        void work();
    }

}
