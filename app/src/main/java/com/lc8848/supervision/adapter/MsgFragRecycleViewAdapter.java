package com.lc8848.supervision.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import com.lc8848.supervision.R;
import com.lc8848.supervision.entity.MsgItemEntity;
import java.util.ArrayList;


/**
 * RecycleView 消息列表
 * Created by wxg on 2016/10/14.
 */

public class MsgFragRecycleViewAdapter  extends Adapter<ViewHolder>{
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NODATA = 2;
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int  LOADING_MORE = 1;
    //加载完毕
    public static final int  NO_MORE_DATA = 2;

    //上拉加载更多状态-默认为2 不显示
    private int load_more_status = 2;
    private Context mContext;
    private ArrayList<MsgItemEntity> mData=new ArrayList<>();

    public MsgFragRecycleViewAdapter(Context mContext,ArrayList<MsgItemEntity> mdata) {
        this.mContext = mContext;
        this.mData = mdata;

    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mData.size() == 0 ? 0 : mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
            if (position + 1 == getItemCount() ) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_messages, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == TYPE_FOOTER) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent, false);
                return new FootViewHolder(view);
            }
        return null;
    }
    FootViewHolder footViewHolder;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).tvMsgId.setText(mData.get(position).getRc_id()+"");
            ((ItemViewHolder) holder).tvMsgNum.setText(mData.get(position).getRc_num());
            ((ItemViewHolder) holder).tvMsgTime.setText(mData.get(position).getRc_date());
            if(mData.get(position).getRc_addr().length()>16) {
                ((ItemViewHolder) holder).tvMsgDesc.setText(mData.get(position).getRc_addr().substring(0, 20) + "......");
            }else{
                ((ItemViewHolder) holder).tvMsgDesc.setText(mData.get(position).getRc_addr());
            }
            //根据返回的状态值设置不同的状态颜色
            switch (mData.get(position).getRc_state()){
                case "0":
                    ((ItemViewHolder) holder).tvMsgStatus.setText(R.string.no_process);
                    ((ItemViewHolder) holder).tvMsgStatus.setTextColor(mContext.getResources().getColor(R.color.no_update));
                    break;
                case "1":
                    ((ItemViewHolder) holder).tvMsgStatus.setText(R.string.need_process);
                    ((ItemViewHolder) holder).tvMsgStatus.setTextColor(mContext.getResources().getColor(R.color.need_update));
                    break;
                case "2":
                    ((ItemViewHolder) holder).tvMsgStatus.setText(R.string.processing);
                    ((ItemViewHolder) holder).tvMsgStatus.setTextColor(mContext.getResources().getColor(R.color.updating));
                    break;
                case "3":
                    ((ItemViewHolder) holder).tvMsgStatus.setText(R.string.processed);
                    ((ItemViewHolder) holder).tvMsgStatus.setTextColor(mContext.getResources().getColor(R.color.updated));
                    break;
            }
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }
        }else if(holder instanceof FootViewHolder){
             footViewHolder=(FootViewHolder)holder;
        }
    }



    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMsgId;
        private TextView tvMsgNum;
        private TextView tvMsgDesc;
        private TextView tvMsgTime;
        private TextView tvMsgStatus;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvMsgId =(TextView) itemView.findViewById(R.id.tv_msg_id);
            tvMsgNum =(TextView) itemView.findViewById(R.id.tv_msg_num);
            tvMsgDesc =(TextView) itemView.findViewById(R.id.tv_msg_desc);
            tvMsgTime =(TextView) itemView.findViewById(R.id.tv_msg_time);
            tvMsgStatus =(TextView) itemView.findViewById(R.id.tv_msg_status);

        }
    }
    public static class FootViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLoadingMore;
        private ProgressBar progressBar;
        public FootViewHolder(View view) {
            super(view);
            tvLoadingMore=(TextView)view.findViewById(R.id.tv_loading_more);
            progressBar=(ProgressBar)view.findViewById(R.id.foot_progressBar);
        }
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void changeMoreStatus(int status){
        load_more_status = status;
        switch (load_more_status){
            case PULLUP_LOAD_MORE:
                footViewHolder.progressBar.setVisibility(View.VISIBLE);
                footViewHolder.tvLoadingMore.setVisibility(View.VISIBLE);
                footViewHolder.tvLoadingMore.setText("加载更多");
                break;
            case LOADING_MORE:
                footViewHolder.progressBar.setVisibility(View.VISIBLE);
                footViewHolder.tvLoadingMore.setVisibility(View.VISIBLE);
                footViewHolder.tvLoadingMore.setText("正在加载...");
                break;
            case NO_MORE_DATA:
                footViewHolder.progressBar.setVisibility(View.GONE);
                footViewHolder.tvLoadingMore.setVisibility(View.GONE);
                break;
        }



    }
}