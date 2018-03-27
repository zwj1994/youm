package com.bs.youmin.activity;
/*
 *  项目名：  LoveWallpaper 
 *  包名：    com.liuguilin.lovewallpaper.activity
 *  文件名:   AboutActivity
 *  创建者:   LGL
 *  创建时间:  2017/1/16 19:57
 *  描述：    关于
 */

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bs.youmin.R;
import com.bs.youmin.entity.Constants;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private CircleImageView profile_image;
    private ListView mListView;
    private List<String> mList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }


    private void initView() {

        mList.add("开发者:朱文杰");
        mList.add("QQ:767899412");

        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.mListView);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_image:

                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                //不需要
                break;
            case 1:
                Constants.jumpQq(this, "767899412");
                break;
        }
    }
}
