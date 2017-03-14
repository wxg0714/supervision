package com.lc8848.supervision.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lc8848.supervision.AppContext;
import com.lc8848.supervision.R;
import com.lc8848.supervision.adapter.PhotoAdapter;
import com.lc8848.supervision.api.SupervisionApi;
import com.lc8848.supervision.event.RecyclerItemClickListener;
import com.lc8848.supervision.fragment.ReleaseFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class MsgChangedActivity extends AppCompatActivity {
    public static final String action = "com.lc8848.supervision.msgchanged.broadcast.action";
    private Button mCancel;
    private Button mSubmit;
    private TextView mTag;
    private EditText mReplay;
    private RecyclerView mChangedRecyler;
    private ImageView mBack;
    private PhotoAdapter photoAdapter;
    private ProgressDialog progress;
    ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_changed);

        final Intent intent=getIntent();
        final String msgid=intent.getStringExtra("msgid");
        final String token=intent.getStringExtra("token");
        mCancel=(Button)findViewById(R.id.btn_cancel);
        mSubmit=(Button)findViewById(R.id.btn_submit);
        mTag=(TextView)findViewById(R.id.changed_tag);
        mBack=(ImageView)findViewById(R.id.back_to_msginfo) ;
        mReplay=(EditText)findViewById(R.id.edit_replay);
        mChangedRecyler=(RecyclerView)findViewById(R.id.recycler_view_changed);
        photoAdapter = new PhotoAdapter(MsgChangedActivity.this, selectedPhotos);
        mChangedRecyler.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        mChangedRecyler.setAdapter(photoAdapter);
        mChangedRecyler.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position < selectedPhotos.size()) {
                            PhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .setShowDeleteButton(true)
                                    .start(MsgChangedActivity.this);
                        } else {
                            checkPermission();
                        }

                    }
                }));


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                if(!prepareForReplay()){
                    SupervisionApi.changedMessages(selectedPhotos,msgid,token,mReplay.getText().toString(),responseHandler);
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgChangedActivity.this.finish();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgChangedActivity.this.finish();
            }
        });

    }
    private final AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            if(Integer.parseInt(new String(responseBody))==0){
                Toast.makeText(MsgChangedActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
//                mReplay.setHint("请输入整改描述");
//                mReplay.setText("");
//
//                //为设置一个空的适配器
//                selectedPhotos.clear();
//                photoAdapter.notifyDataSetChanged();
//                mTag.setVisibility(View.VISIBLE);
                Intent intent = new Intent(action);
                sendBroadcast(intent);
                //强制dialog提前释放，否则会报错
                progress.dismiss();
                MsgChangedActivity.this.finish();
            }else{
                Toast.makeText(MsgChangedActivity.this,"发布失败，请重新发布"+new String(responseBody),Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(MsgChangedActivity.this,"发布失败，请检查网络！"+new String(responseBody),Toast.LENGTH_SHORT).show();
        }
    };
    private void showWaitDialog() {
        progress =ProgressDialog.show(this,"","提交中...",true,false);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    progress.dismiss();//关闭ProgressDialog
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }
    private void checkPermission() {

        int readStoragePermissionState = ContextCompat.checkSelfPermission(MsgChangedActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermissionState = ContextCompat.checkSelfPermission(MsgChangedActivity.this, Manifest.permission.CAMERA);

        boolean readStoragePermissionGranted = readStoragePermissionState != PackageManager.PERMISSION_GRANTED;
        boolean cameraPermissionGranted = cameraPermissionState != PackageManager.PERMISSION_GRANTED;


        if (readStoragePermissionGranted || cameraPermissionGranted) {

            if(cameraPermissionGranted){
                Toast.makeText(MsgChangedActivity.this,"相机权限未开启",Toast.LENGTH_SHORT).show();

            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(MsgChangedActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MsgChangedActivity.this,Manifest.permission.CAMERA)) {

            } else {
                String[] permissions;
                if (readStoragePermissionGranted && cameraPermissionGranted) {
                    permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                } else {
                    permissions = new String[]{
                            readStoragePermissionGranted ? Manifest.permission.READ_EXTERNAL_STORAGE
                                    : Manifest.permission.CAMERA
                    };
                }

                ActivityCompat.requestPermissions(MsgChangedActivity.this,permissions, 1);
                ActivityCompat.shouldShowRequestPermissionRationale(MsgChangedActivity.this,Manifest.permission.CAMERA);
            }

        } else {

            PhotoPicker.builder()
                    .setShowCamera(true)
                    .setSelected(selectedPhotos)
                    .setPhotoCount(9)
                    .setGridColumnCount(3)
                    .start(MsgChangedActivity.this);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            if(selectedPhotos.size()>0) {
                mTag.setVisibility(View.GONE);
            }else{
                mTag.setVisibility(View.VISIBLE);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    private boolean prepareForReplay() {

        boolean cancel = false;

        if(selectedPhotos==null||selectedPhotos.size()==0){

            Toast.makeText(MsgChangedActivity.this,"未上传照片",Toast.LENGTH_SHORT).show();
            cancel = true;
        }
        if (TextUtils.isEmpty(mReplay.getText())) {
            mReplay.setError("描述不能为空");
            mReplay.requestFocus();
            cancel = true;
        }

        return cancel;
    }
}
