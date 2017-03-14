package com.lc8848.supervision.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lc8848.supervision.R;
import com.lc8848.supervision.entity.Photos1;
import java.util.List;


/**
 * RecycleView加载图片适配器
 * Created by wxg on 2016/10/14.
 */

public class MsgRecycleViewAdapter extends RecyclerView.Adapter<MsgRecycleViewAdapter.ViewHolder>{

    private List<Photos1> mPhotoList;
    private Context mContext;

    public MsgRecycleViewAdapter(List<Photos1> photoList,Context mContext) {
        this.mContext=mContext;
        this.mPhotoList=photoList;
    }

    @Override
    public int getItemCount() {

        return mPhotoList.size();
    }
    @Override
    public MsgRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.__picker_item_photo, parent, false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final MsgRecycleViewAdapter.ViewHolder holder, int position) {
        String path=mPhotoList.get(position).getPath();

        Glide.with(mContext)
                .load(path)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.msg_default)
                .into(holder.ivPhoto);

}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;
        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto   = (ImageView) itemView.findViewById(me.iwf.photopicker.R.id.iv_photo);
            vSelected = itemView.findViewById(me.iwf.photopicker.R.id.v_selected);
            vSelected.setVisibility(View.GONE);
        }
    }
}
