package com.lc8848.supervision.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.msebera.android.httpclient.Header;
import me.iwf.photopicker.PhotoPreview;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lc8848.supervision.AppContext;
import com.lc8848.supervision.R;
import com.lc8848.supervision.adapter.MsgRecycleViewAdapter;
import com.lc8848.supervision.api.SupervisionApi;
import com.lc8848.supervision.cache.ACache;
import com.lc8848.supervision.entity.MsgDetailEntity;
import com.lc8848.supervision.event.RecyclerItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

public class MessageDetailActivity extends AppCompatActivity {


    private TextView mTextNum;
    private TextView mTextAddress;
    private TextView mTextTime;
    private TextView mTextType;
    private TextView mTextTown;
    private TextView mTextstatus;
    private TextView mTextAfterInfo;
    private TextView mTextRemark;
    private TextView mTextWaterInfo;
    private Button mBtnChanged;
    private ImageView mImageBack;
    private RecyclerView mBeforePhotos;
    private RecyclerView mAfterPhotos;
    private MsgRecycleViewAdapter recycleViewAdapter1;
    private MsgRecycleViewAdapter recycleViewAdapter2;
    //整改后相关信息
    private LinearLayout mUpdateInfoLayout;
    private TextView mTextAfterTime;
    private TextView mTextAfterPerson;
    //广播接收器
    private BroadcastReceiver broadcastReceiver;
    private ACache mCache;//引用缓存
    private SharedPreferences sharedPref;
    private String token;
    private   MsgDetailEntity msgDetailEntity;
    private Context mContext;
    private   String msgid;
    ArrayList<String> beforePhotos = new ArrayList<>();
    ArrayList<String> afterPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);
        sharedPref= AppContext.getInstance().getSharedPref();;
        token=sharedPref.getString("token","");
        mContext=this;
        final Intent intent=getIntent();
        msgid=intent.getStringExtra("msgid");

        mTextAddress=(TextView)findViewById(R.id.address_desc) ;
        mTextNum=(TextView)findViewById(R.id.num);
        mTextTime=(TextView)findViewById(R.id.time);
        mTextType=(TextView)findViewById(R.id.type);
        mTextTown=(TextView)findViewById(R.id.town);
        mTextstatus=(TextView)findViewById(R.id.status);

        mUpdateInfoLayout=(LinearLayout)findViewById(R.id.update_info);
        mTextAfterInfo=(TextView)findViewById(R.id.after_msg_info);
        mTextAfterTime=(TextView)findViewById(R.id.after_msg_time);
        mTextAfterPerson=(TextView)findViewById(R.id.after_msg_person);


        mTextRemark=(TextView)findViewById(R.id.remark);
        mTextWaterInfo=(TextView)findViewById(R.id.water_info);
        mBtnChanged=(Button)findViewById(R.id.changed);

        mBeforePhotos=(RecyclerView)findViewById(R.id.before_photos);
        mAfterPhotos=(RecyclerView)findViewById(R.id.after_photos);
        mBeforePhotos.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        mAfterPhotos.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        getMsgDetail( msgid);

        mBeforePhotos.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                PhotoPreview.builder()
                        .setPhotos(beforePhotos)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(MessageDetailActivity.this);
            }
        }));

        mAfterPhotos.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(afterPhotos)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(MessageDetailActivity.this);
            }
        }));

        mBtnChanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MessageDetailActivity.this,MsgChangedActivity.class);
                intent1.putExtra("msgid",msgid);
                intent1.putExtra("token",token);
                startActivity(intent1);
            }
        });
        mImageBack=(ImageView)findViewById(R.id.back_to_setting);
        mImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        initBroadcastReceiver();
        super.onResume();
    }
    //定义广播事件，重新加载数据
    public void initBroadcastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver()
        {

            @Override
            public void onReceive(Context arg0, Intent arg1)
            {

                getMsgDetail( msgid);

            }
        };
        IntentFilter filter = new IntentFilter(MsgChangedActivity.action);
        registerReceiver(broadcastReceiver, filter);
    }

    //获取类型列表
    public void getMsgDetail(String msgid){
//
//        //从缓存中获取数据
//        MsgDetailEntity msgDetailEntity=(MsgDetailEntity) mCache.getAsObject("msgdetailinfo");
//        //如果缓存中有数据就不访问网络
//        if(msgDetailEntity!=null){
//
//        }else{
        //否则访问网络将获取数据存入缓存
        SupervisionApi.getMessagesDetail(Integer.parseInt(msgid),token,mMsgDetailHandler);
        // }
    }
    //获取类型
    private final AsyncHttpResponseHandler mMsgDetailHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            /**
             *用Gson解析返回的类型结果
             */
            Gson mGson = new GsonBuilder()
                    .serializeNulls()
                    .create();
            // Log.e("json==",new String(arg2));
            if (arg2!=null && arg2.length>0) {
                msgDetailEntity = mGson.fromJson(new String(arg2), MsgDetailEntity.class);
                mTextNum.setText(msgDetailEntity.getData().get(0).getRc_num());
                mTextTime.setText(msgDetailEntity.getData().get(0).getRc_date());
                mTextType.setText(msgDetailEntity.getData().get(0).getRc_type());
                mTextAddress.setText(msgDetailEntity.getData().get(0).getRc_addr());
                mTextTown.setText(msgDetailEntity.getData().get(0).getPn_name());

                mTextRemark.setText(msgDetailEntity.getData().get(0).getRc_remark());
                String strWater="高锰酸钾：" +(msgDetailEntity.getData().get(0).getAq_cod()==null?"0":msgDetailEntity.getData().get(0).getAq_cod())
                        +"  氨氮："+ (msgDetailEntity.getData().get(0).getAq_nh()==null?"0":msgDetailEntity.getData().get(0).getAq_nh())
                        +"  总磷："+ (msgDetailEntity.getData().get(0).getAq_tp()==null?"0":msgDetailEntity.getData().get(0).getAq_tp());
                mTextWaterInfo.setText(strWater);

                switch (msgDetailEntity.getData().get(0).getRc_state()) {

                    case "0":
                        mTextstatus.setText(R.string.no_process);
                        mTextstatus.setTextColor(getResources().getColor(R.color.no_update));
                        break;
                    case "1":
                        mTextstatus.setText(R.string.need_process);
                        mTextstatus.setTextColor(getResources().getColor(R.color.need_update));
                        mBtnChanged.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        mTextstatus.setText(R.string.processing);
                        mTextstatus.setTextColor(getResources().getColor(R.color.updating));
                        mUpdateInfoLayout.setVisibility(View.VISIBLE);
                        mBtnChanged.setVisibility(View.INVISIBLE);
                        break;
                    case "3":
                        mTextstatus.setText(R.string.processed);
                        mTextstatus.setTextColor(getResources().getColor(R.color.updated));
                        mUpdateInfoLayout.setVisibility(View.VISIBLE);
                        break;
                }
                recycleViewAdapter1 = new MsgRecycleViewAdapter(msgDetailEntity.getPhotos1(), mContext);
                mBeforePhotos.setAdapter(recycleViewAdapter1);
                for (int i = 0; i < msgDetailEntity.getPhotos1().size(); i++) {
                    beforePhotos.add(msgDetailEntity.getPhotos1().get(i).getPath());

                }
                if(msgDetailEntity.getReply_data()!=null){
                    mTextAfterInfo.setText(msgDetailEntity.getReply_data().getRy_content());
                    mTextAfterTime.setText(msgDetailEntity.getReply_data().getRy_date());
                    mTextAfterPerson.setText(msgDetailEntity.getReply_data().getUsername());
                }
                if(msgDetailEntity!=null && msgDetailEntity.getPhotos2()!=null) {

                    if (msgDetailEntity.getPhotos2().size() > 0) {
                        recycleViewAdapter2 = new MsgRecycleViewAdapter(msgDetailEntity.getPhotos2(), mContext);
                        mAfterPhotos.setAdapter(recycleViewAdapter2);
                        for (int i = 0; i < msgDetailEntity.getPhotos2().size(); i++) {
                            afterPhotos.add(msgDetailEntity.getPhotos2().get(i).getPath());
                        }
                    }
                }
            }
        }
        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
        }

    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
