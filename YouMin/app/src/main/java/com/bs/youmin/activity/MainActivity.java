package com.bs.youmin.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.youmin.R;
import com.bs.youmin.entity.Constants;
import com.bs.youmin.entity.User;
import com.bs.youmin.fragment.AlbumRankingFragment;
import com.bs.youmin.fragment.HomePageFragment;
import com.bs.youmin.fragment.LoginFragment;
import com.bs.youmin.fragment.MyAlbumFragment;
import com.bs.youmin.fragment.WeatherFragment;
import com.bs.youmin.util.SaveUserUtil;
import com.bs.youmin.view.CustomDialog;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawer;

    private HomePageFragment homePageFragment;
    private AlbumRankingFragment albumRankingFragment;
    private MyAlbumFragment myAlbumFragment;
    private WeatherFragment weatherFragment;
    private LoginFragment loginFragment;

    private CustomDialog dialogShare;
    private LinearLayout ll_share_qq;
    private LinearLayout ll_share_sina;
    private LinearLayout ll_share_wechat;
    private LinearLayout ll_share_more;

    private CircleImageView user_header_img;
    private TextView login_msg;
    private TextView user_sign;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @SuppressLint("NewApi")
    private void initView() {
        dialogShare = new CustomDialog(this, 0, 0, R.layout.dialog_share_item, R.style.Theme_dialog, Gravity.BOTTOM, R.style.pop_anim_style);
        ll_share_qq = (LinearLayout) dialogShare.findViewById(R.id.ll_share_qq);
        ll_share_qq.setOnClickListener(this);
        ll_share_sina = (LinearLayout) dialogShare.findViewById(R.id.ll_share_sina);
        ll_share_sina.setOnClickListener(this);
        ll_share_wechat = (LinearLayout) dialogShare.findViewById(R.id.ll_share_wechat);
        ll_share_wechat.setOnClickListener(this);
        ll_share_more = (LinearLayout) dialogShare.findViewById(R.id.ll_share_more);
        ll_share_more.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        user_header_img = (CircleImageView)view.findViewById(R.id.user_header_img);
        login_msg = (TextView) view.findViewById(R.id.login_msg);
        user_sign = (TextView) view.findViewById(R.id.user_sign);

        navigationView.setNavigationItemSelectedListener(this);
        //显示原本的色彩
        navigationView.setItemIconTintList(null);
        initHomePager();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home_page:
                initHomePager();
                break;
            case R.id.nav_album_ranking:
                initAlbumRanking();
                break;
            case R.id.nav_my_album:
                initMyAlbum();
                break;
            case R.id.nav_weather:
                initWeather();
                break;
            case R.id.nav_share:
                drawer.closeDrawer(GravityCompat.START);
                dialogShare.show();
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //取消提示框
            new AlertDialog.Builder(this)
                    .setMessage("是否退出应用？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);//头部右侧点点点
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            startActivity(new Intent(this, SettingActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * 登录
     */
    private void initLogin(){
        getSupportActionBar().setTitle(getString(R.string.text_login));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
            transaction.add(R.id.main_frame_layout, loginFragment);
        }
        loginFragment.setOnButtonClick(new LoginFragment.OnButtonClick() {
            //3、实现接口对象的方法，
            @Override
            public void onClick(View view) {
                initMyAlbum();
                initUserInfo();
            }
        });
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(loginFragment);
        transaction.commit();
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo(){
        try{
            final User user = SaveUserUtil.loadAccount(MainActivity.this);
            user_sign.setText(user.getSign());
            login_msg.setText(user.getHeaderImg());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 首页
     */
    private void initHomePager() {
        getSupportActionBar().setTitle(getString(R.string.app_name));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (homePageFragment == null) {
            homePageFragment = new HomePageFragment();
            transaction.add(R.id.main_frame_layout, homePageFragment);
        }
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(homePageFragment);
        transaction.commit();
    }

    /**
     * 排名
     */
    private void initAlbumRanking() {
        getSupportActionBar().setTitle(getString(R.string.text_album_ranking));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (albumRankingFragment == null) {
            albumRankingFragment = new AlbumRankingFragment();
            transaction.add(R.id.main_frame_layout, albumRankingFragment);
        }
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(albumRankingFragment);
        transaction.commit();
    }

    /**
     * 我的
     */
    private void initMyAlbum() {
        getSupportActionBar().setTitle(getString(R.string.text_my_album));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (myAlbumFragment == null) {
            myAlbumFragment = new MyAlbumFragment();
            transaction.add(R.id.main_frame_layout, myAlbumFragment);
        }
        myAlbumFragment.setOnButtonClick(new LoginFragment.OnButtonClick() {
            //3、实现接口对象的方法，
            @Override
            public void onClick(View view) {
                initLogin();
                myAlbumFragment = null;
            }
        });
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(myAlbumFragment);
        transaction.commit();
    }

    /**
     * 天气
     */
    private void initWeather() {
        getSupportActionBar().setTitle(getString(R.string.text_weather));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (weatherFragment == null) {
            weatherFragment = new WeatherFragment();
            transaction.add(R.id.main_frame_layout, weatherFragment);
        }
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(weatherFragment);
        transaction.commit();
    }

    /**
     * 隐藏所有Fragment
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (myAlbumFragment != null) {
            transaction.hide(myAlbumFragment);
        }
        if (homePageFragment != null) {
            transaction.hide(homePageFragment);
        }
        if (albumRankingFragment != null) {
            transaction.hide(albumRankingFragment);
        }
        if (weatherFragment != null) {
            transaction.hide(weatherFragment);
        }
        if (loginFragment != null) {
            transaction.hide(loginFragment);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_share_qq:
                Constants.intentStartQQ(this, Constants.shareText);
                break;
            case R.id.ll_share_sina:
                Constants.intentStartSina(this, Constants.shareText);
                break;
            case R.id.ll_share_wechat:
                Constants.intentStartWechat(this,Constants.shareText);
                break;
            case R.id.ll_share_more:
                Constants.intentSystemShare(this, Constants.shareText);
                break;
        }
        dialogShare.dismiss();
    }
}
