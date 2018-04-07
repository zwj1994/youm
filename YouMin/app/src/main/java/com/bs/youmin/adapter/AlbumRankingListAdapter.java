package com.bs.youmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.youmin.R;
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.util.GlideUtils;

import java.util.List;

/*
 *  项目名：  YouMin
 *  包名：    com.bs.youmin.adapter
 *  文件名:   YAlbumRankingListAdapter
 *  创建者:   ZWJ
 *  创建时间:  2018/03/24 22:47
 *  描述：    相册排名适配器
 */
public class AlbumRankingListAdapter extends BaseAdapter {

    public Context mContext;
    private LayoutInflater inflater;
    private List<YAlbum> mList;
    private YAlbum model;

    public AlbumRankingListAdapter(Context mContext, List<YAlbum> mList) {
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
            view = inflater.inflate(R.layout.fragment_album_ranking_list_item, null);
            viewHolder.iv_category_list_icon = (ImageView) view.findViewById(R.id.iv_category_list_icon);
            viewHolder.tv_category_love_count = (TextView) view.findViewById(R.id.tv_category_love_count);
            viewHolder.tv_category_list_name = (TextView) view.findViewById(R.id.tv_category_list_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        model = mList.get(i);
        GlideUtils.loadImageCrop(mContext, model.getaCover(), viewHolder.iv_category_list_icon);
        viewHolder.tv_category_list_name.setText(model.getaName());
        viewHolder.tv_category_love_count.setText(model.getLikecount()+"");
        return view;
    }

    class ViewHolder {
        private ImageView iv_category_list_icon;
        private TextView tv_category_list_name;
        private TextView tv_category_love_count;
    }


}
