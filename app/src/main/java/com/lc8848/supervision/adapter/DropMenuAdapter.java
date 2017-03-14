package com.lc8848.supervision.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.FrameLayout;
import com.baiiu.filter.adapter.MenuAdapter;
import com.baiiu.filter.adapter.SimpleTextAdapter;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.baiiu.filter.interfaces.OnFilterItemClickListener;
import com.baiiu.filter.typeview.SingleListView;
import com.baiiu.filter.util.UIUtil;
import com.baiiu.filter.view.FilterCheckedTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc8848.supervision.AppContext;
import com.lc8848.supervision.api.SupervisionApi;
import com.lc8848.supervision.cache.ACache;
import com.lc8848.supervision.entity.FilterUrl;
import com.lc8848.supervision.entity.TownEntity;
import com.lc8848.supervision.entity.TypeEntity;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * author: baiiu
 * date: on 16/1/17 21:14
 * description:
 */
public class DropMenuAdapter implements MenuAdapter {
    private final Context mContext;
    private OnFilterDoneListener onFilterDoneListener;
    private String[] titles;
    private ACache mCache;//引用缓存
    private Gson mGson;
    private  SharedPreferences sharedPref;
    private String token;
    private String auth;

    List<String> listTown=new ArrayList<>();
    List<String> listType=new ArrayList<>();

    public DropMenuAdapter(Context context, String[] titles, OnFilterDoneListener onFilterDoneListener) {
        this.mContext = context;
        this.titles = titles;
        this.onFilterDoneListener = onFilterDoneListener;
        sharedPref= AppContext.getInstance().getSharedPref();;
        token=sharedPref.getString("token","");
        auth=sharedPref.getString("auth","");
        //创建缓存组件
        mCache=ACache.get(context);
        getTypeList();
        getTownList();
    }


    //获取类型列表
    public void getTypeList(){
        TypeEntity typeObject;
        //从缓存中获取数据
        typeObject=(TypeEntity) mCache.getAsObject("typeinfo");
        //如果缓存中有数据就不访问网络
        if(typeObject!=null && typeObject.getData()!=null){
            listType.add("所有类型");
            for(int i=0;i<typeObject.getData().size();i++){

                listType.add(typeObject.getData().get(i).toString());
            }

        }else{
            //否则访问网络将获取数据存入缓存
            SupervisionApi.getTypesList(token,mTypeHandler);
        }


    }

    //获取类型
    private final AsyncHttpResponseHandler mTypeHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            /**
             *用Gson解析返回的类型结果
             */
            mGson = new Gson();
            Type type = new TypeToken<TypeEntity>() {}.getType();
            TypeEntity typeEntity = mGson.fromJson(new String(arg2), type);
            listType.add("所有类型");
            if(typeEntity!=null && typeEntity.getData()!=null) {
                for (int i = 0; i < typeEntity.getData().size(); i++) {

                    listType.add(typeEntity.getData().get(i).toString());
                }
            }
            mCache.put("typeinfo", typeEntity);

        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
        }

    };

    public void getTownList(){

        //从缓存中获取数据
        TownEntity townObject=(TownEntity) mCache.getAsObject("towninfo");
        //如果缓存中有数据就不访问网络
        if(townObject!=null && townObject.getData()!=null ){

            if(!auth.equals("12")) {
                listTown.add("所有单位");

                for (int i = 0; i < townObject.getData().size(); i++) {

                    listTown.add(townObject.getData().get(i).toString());
                }
            }
        }else{
            //否则访问网络将获取数据存入缓存
            SupervisionApi.getTownsList(token,mTownHandler);
        }

    }
    //获取所属单位
    private final AsyncHttpResponseHandler mTownHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            /**
             *用Gson解析返回的类型结果
             */
            mGson = new Gson();
            Type type = new TypeToken<TownEntity>(){}.getType();
            TownEntity townEntity = mGson.fromJson(new String(arg2), type);
            if(townEntity!= null && townEntity.getData()!=null) {
                if(!auth.equals("12")) {
                    listTown.add("所有单位");
                    for (int i = 0; i < townEntity.getData().size(); i++) {

                        listTown.add(townEntity.getData().get(i).toString());
                    }
                }
            }

            mCache.put("towninfo", townEntity);

        }
        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {

        }
        @Override
        public void onFinish() {
            super.onFinish();
        }
    };

    @Override
    public int getMenuCount() {
        return titles.length;
    }

    @Override
    public String getMenuTitle(int position) {
        return titles[position];
    }

    @Override
    public int getBottomMargin(int position) {
        if (position == 3) {
            return 0;
        }

        return UIUtil.dp(mContext, 140);
    }

    @Override
    public View getView(int position, FrameLayout parentContainer) {
        View view = parentContainer.getChildAt(position);

        switch (position) {
            case 0:
                view = createSingleListView();
                break;
            case 1:
                view = createTownSingleListView();
                break;
            case 2:
                view = createTypeSingleListView();
                break;
        }

        return view;
    }

    private View createSingleListView() {
        SingleListView<String> singleListView = new SingleListView<String>(mContext)
                .adapter(new SimpleTextAdapter<String>(null, mContext) {
                    @Override
                    public String provideText(String string) {
                        return string;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        int dp = UIUtil.dp(mContext, 15);
                        checkedTextView.setPadding(dp, dp, 0, dp);
                    }
                })
                .onItemClick(new OnFilterItemClickListener<String>() {
                    @Override
                    public void onItemClick(String item) {
                        FilterUrl.instance().singleListPosition = item;

                        FilterUrl.instance().position = 0;
                        FilterUrl.instance().positionTitle = item;

                        onFilterDone();
                    }
                });

        List<String> list = new ArrayList<>();
        list.add("所有信息" );
        list.add("我的信息" );
        singleListView.setList(list, -1);

        return singleListView;
    }

    private View createTownSingleListView() {
        SingleListView<String> singleListView = new SingleListView<String>(mContext)
                .adapter(new SimpleTextAdapter<String>(null, mContext) {
                    @Override
                    public String provideText(String string) {
                        return string;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        int dp = UIUtil.dp(mContext, 15);
                        checkedTextView.setPadding(dp, dp, 0, dp);
                    }
                })
                .onItemClick(new OnFilterItemClickListener<String>() {
                    @Override
                    public void onItemClick(String item) {
                        FilterUrl.instance().singleListPosition = item;

                        FilterUrl.instance().position = 1;
                        FilterUrl.instance().positionTitle = item;

                        onFilterDone();
                    }
                });


        singleListView.setList(listTown, -1);

        return singleListView;
    }
    private View createTypeSingleListView() {
        SingleListView<String> singleListView = new SingleListView<String>(mContext)
                .adapter(new SimpleTextAdapter<String>(null, mContext) {
                    @Override
                    public String provideText(String string) {
                        return string;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        int dp = UIUtil.dp(mContext, 15);
                        checkedTextView.setPadding(dp, dp, 0, dp);
                    }
                })
                .onItemClick(new OnFilterItemClickListener<String>() {
                    @Override
                    public void onItemClick(String item) {
                        FilterUrl.instance().singleListPosition = item;

                        FilterUrl.instance().position = 2;
                        FilterUrl.instance().positionTitle = item;

                        onFilterDone();
                    }
                });
        singleListView.setList(listType, -1);

        return singleListView;
    }

    private void onFilterDone() {
        if (onFilterDoneListener != null) {
            onFilterDoneListener.onFilterDone(0, null, null);
        }
    }

}
