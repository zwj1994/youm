package com.bs.youmin.adapter;
/*
 *  项目名：  YouMin
 *  包名：    com.bs.youmin.adapter
 *  文件名:   AlbumRankingDataAdapter
 *  创建者:   ZWJ
 *  创建时间:  2018/03/24 16:50
 *  描述：    分类详情
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.youmin.R;
import com.bs.youmin.entity.CategoryGridModel;
import com.bs.youmin.entity.YPhoto;
import com.bs.youmin.util.DateUtil;
import com.bs.youmin.util.GlideUtils;

import java.util.List;


public class AlbumRankingDataAdapter extends BaseAdapter {

    public Context mContext;
    private LayoutInflater inflater;
    private List<YPhoto> mList;
    private YPhoto model;

    public AlbumRankingDataAdapter(Context mContext, List<YPhoto> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_special_grid_item, null);
            viewHolder.iv_main_grid_icon = (ImageView) view.findViewById(R.id.iv_main_grid_icon);
            viewHolder.tv_main_grid_down = (TextView) view.findViewById(R.id.tv_main_grid_down);
            viewHolder.tv_main_grid_create_date = (TextView) view.findViewById(R.id.tv_main_grid_create_date);
            viewHolder.ll_item_download = (LinearLayout) view.findViewById(R.id.ll_item_download);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        model = mList.get(i);
        GlideUtils.loadImageCrop(mContext, model.getpSmall(), viewHolder.iv_main_grid_icon);
        viewHolder.tv_main_grid_down.setText(model.getpDown() + "");
        viewHolder.tv_main_grid_create_date.setText(DateUtil.date2Str(model.getpCreateDate(),DateUtil.FORMAT_YYYYMMDD));
        viewHolder.ll_item_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "onClick:" + model.getpDown(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    class ViewHolder {
        private ImageView iv_main_grid_icon;
        private TextView tv_main_grid_down;
        private LinearLayout ll_item_download;
        private TextView tv_main_grid_create_date;
    }

}
