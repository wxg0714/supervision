package com.lc8848.supervision.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc8848.supervision.AppContext;
import com.lc8848.supervision.R;
import com.lc8848.supervision.activity.MessageDetailActivity;
import com.lc8848.supervision.adapter.DropMenuAdapter;
import com.lc8848.supervision.adapter.MsgFragRecycleViewAdapter;
import com.lc8848.supervision.api.SupervisionApi;
import com.lc8848.supervision.entity.FilterUrl;
import com.lc8848.supervision.entity.MsgItemEntity;
import com.lc8848.supervision.widget.RecyclerViewDivider;
import com.lc8848.supervision.widget.RecyclerViewEmptySupport;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;
import cz.msebera.android.httpclient.Header;

/**
 * Created by wxg on 2016/7/19.
 */

public class MessageFragment extends Fragment {

    DropDownMenu dropDownMenu;
    private String str0,str1,str2;
    private static int TAG=0;
    private int lastVisibleItemPosition;
    private MsgItemEntity itemEntity;
    private  String mAuth;
    private String mTown;
    private Gson mGson;
    private String token;
    private static SharedPreferences sharedPref;
    private RecyclerViewEmptySupport recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isLoading = false;
    private Handler handler = new Handler();
    private Context mContext;
    private ArrayList<MsgItemEntity> mData=new ArrayList<>();
    private ArrayList<MsgItemEntity> mDataTemp=new ArrayList<>();
    private MsgFragRecycleViewAdapter adapter;
    public MessageFragment() {
        sharedPref=AppContext.getInstance().getSharedPref();
        mAuth=sharedPref.getString("auth", "");
        mTown=sharedPref.getString("unit", "");
        token=sharedPref.getString("token","");
    }
    public static MessageFragment newInstance(){
        return new MessageFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        adapter=  new MsgFragRecycleViewAdapter(context,mData);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_message,container,false);
        //View noDataView = LayoutInflater.from(mContext).inflate(R.layout.no_data, container, false);
        dropDownMenu=(DropDownMenu)rootView.findViewById(R.id.dropDownMenu);
        swipeRefreshLayout= (SwipeRefreshLayout)rootView.findViewById(R.id.SwipeRefreshLayout) ;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }, 2000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView=(RecyclerViewEmptySupport)rootView.findViewById(R.id.msg_recyclerView) ;
        recyclerView.setLayoutManager(layoutManager);
        //设置分割线
        recyclerView.addItemDecoration(new RecyclerViewDivider());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE);
                        handler.postDelayed(runnableRef, 2000);
                    }
                }

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

            }
        });

        //添加点击事件
        adapter.setOnItemClickListener(new MsgFragRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(),MessageDetailActivity.class);
                itemEntity= mData.get(position);
                intent.putExtra("msgid",itemEntity.getRc_id()+"");
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        initData();
        return rootView;
    }
    public void initData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SupervisionApi.getMessagesList(token,null, str0, str1, str2,TAG ,mMsgListHandler);
            }
        }, 2000);
    }
    public void getData() {
        if (mAuth.equals("12")) {
            SupervisionApi.getMessagesList(token,null, str0, mTown, str2,TAG ,mMsgListHandler);
        }else {
            SupervisionApi.getMessagesList(token,null, str0, str1, str2,TAG ,mMsgListHandler);
        }
    }
    private Runnable runnableRef = new Runnable() {
        public void run() {
            if(mData.size()>0 ) {
                if (mAuth.equals("12")) {
                    SupervisionApi.getMessagesList(token, mData.get(mData.size() - 1).getRc_num(), str0, mTown, str2, TAG, mMsgListHandler);
                } else {
                    SupervisionApi.getMessagesList(token, mData.get(mData.size() - 1).getRc_num(), str0, str1, str2, TAG, mMsgListHandler);
                }
                adapter.changeMoreStatus(adapter.LOADING_MORE);
            }
        }
    };
    private void initFilterDropDownView() {
        String[] titleList;
        if (mAuth.equals("12")){
            titleList = new String[]{"所有消息",mTown, "所有类型"};

        }else {
            titleList = new String[]{"所有消息", "所有单位", "所有类型"};
        }
        dropDownMenu.setMenuAdapter(new DropMenuAdapter(getActivity(), titleList,new FilterDoneListener()));
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initFilterDropDownView();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        FilterUrl.instance().clear();
    }
    class  FilterDoneListener implements  OnFilterDoneListener{
        @Override
        public void onFilterDone(int position, String positionTitle, String urlValue) {
            if (position != 3) {
                dropDownMenu.setPositionIndicatorText(FilterUrl.instance().position, FilterUrl.instance().positionTitle);
            }
            switch (FilterUrl.instance().position){
                case 0:
                    str0=FilterUrl.instance().positionTitle=="所有信息"?null:FilterUrl.instance().positionTitle;
                    break;
                case 1:
                    if (!mAuth.equals("12")) {
                        str1 = FilterUrl.instance().positionTitle == "所有单位" ? null : FilterUrl.instance().positionTitle;
                    }else {
                        str1=mTown;
                    }
                    break;
                case 2:
                    str2=FilterUrl.instance().positionTitle=="所有类型" ? null:FilterUrl.instance().positionTitle;
                    break;
            }
            if (str0 != "" && str0 != null) {
                str0 = sharedPref.getString("userid", "");
            }
            if (str1 != "" && str1 != null) {
                int index=str1.indexOf(".");
                if (index != -1){
                    str1 = str1.substring(0, index).trim();
                }
            }
            if (str2 != "" && str2 != null) {
                int index = str2.indexOf(".");
                if (index != -1){
                    str2 = str2.substring(0, index).trim();
                }
            }
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    SupervisionApi.getMessagesList(token,null, str0, str1, str2,TAG ,mMsgListHandler);
                }
            });

            dropDownMenu.close();
        }
    }

    //获取消息
    private final AsyncHttpResponseHandler mMsgListHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            /**
             *用Gson解析返回的类型结果
             */
            mGson = new Gson();
            mDataTemp = mGson.fromJson(new String(arg2), new TypeToken<List<MsgItemEntity>>() {}.getType());

            if(isLoading ){
                mData.addAll(mDataTemp);
                adapter.changeMoreStatus(adapter.NO_MORE_DATA);
                handler.removeCallbacks(runnableRef);
                adapter.notifyItemRangeInserted(mData.size(),mDataTemp.size());
            }else{
                mData.clear();
                mData.addAll(mDataTemp);
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                adapter.notifyItemChanged(adapter.getItemCount());

            }
            isLoading = false;

        }
        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {

        }

    };
}
