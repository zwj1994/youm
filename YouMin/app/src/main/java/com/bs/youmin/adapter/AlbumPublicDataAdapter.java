package com.bs.youmin.adapter;
/*
 *  项目名：  YouMin
 *  包名：    com.bs.youmin.adapter
 *  文件名:   AlbumRankingDataAdapter
 *  创建者:   ZWJ
 *  创建时间:  2018/03/24 16:50
 *  描述：    全部相册
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
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.entity.YPhoto;
import com.bs.youmin.util.DateUtil;
import com.bs.youmin.util.GlideUtils;

import java.util.List;


public class AlbumPublicDataAdapter extends BaseAdapter {

    public Context mContext;
    private LayoutInflater inflater;
    private List<YAlbum> mList;
    private YAlbum model;

    public AlbumPublicDataAdapter(Context mContext, List<YAlbum> mList) {
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
            view = inflater.inflate(R.layout.activity_public_album_grid_item, null);
            viewHolder.iv_main_grid_icon = (ImageView) view.findViewById(R.id.iv_main_grid_icon);
            viewHolder.tv_main_grid_down = (TextView) view.findViewById(R.id.tv_main_grid_down);
            viewHolder.ll_item_download = (LinearLayout) view.findViewById(R.id.ll_item_download);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        model = mList.get(i);
        GlideUtils.loadImageCrop(mContext, model.getaCover(), viewHolder.iv_main_grid_icon);
        viewHolder.tv_main_grid_down.setText(model.getaName());
        viewHolder.ll_item_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "onClick:" + model.getaDescribe(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    class ViewHolder {
        private ImageView iv_main_grid_icon;
        private TextView tv_main_grid_down;
        private LinearLayout ll_item_download;
    }

}
