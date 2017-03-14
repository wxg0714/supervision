package com.lc8848.supervision.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc8848.supervision.AppContext;
import com.lc8848.supervision.R;
import com.lc8848.supervision.adapter.PhotoAdapter;
import com.lc8848.supervision.api.SupervisionApi;
import com.lc8848.supervision.cache.ACache;
import com.lc8848.supervision.entity.TownEntity;
import com.lc8848.supervision.entity.TypeEntity;
import com.lc8848.supervision.event.RecyclerItemClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc8848.supervision.event.RecyclerItemClickListener.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MyLocationOverlay;
import com.tianditu.android.maps.TGeoAddress;
import com.tianditu.android.maps.TGeoDecode;

import cz.msebera.android.httpclient.Header;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wxg on 2016/7/19.
 */

public class ReleaseFragment extends Fragment implements TGeoDecode.OnGeoResultListener
{

    private EditText choseTown;
    private EditText choseType;
    private EditText pointDesc;
    private Button mycancel;
    private TextView title;
    private Button mSubmit;
    private EditText inputTown;
    private RadioButton radioYes;
    private RadioButton radioNo;
    private View mLine;
    private EditText editRemark;
    private String strLatitude;
    private String strLongitude;
    private ProgressDialog progress;

    private ACache mCache;//引用缓存
    private Gson mGson;
    private String mToken;
    private  String userId;
    private static SharedPreferences sharedPref;
    private static final int TWO_MINUTES = 1000 * 1 * 2;
    private final int LOACTION_REQUEST_CODE = 1;
    private LocationManager lm;
    private Location myLocation = null;
    //地理位置逆编码，获取具体位置
    private String LocationName = "";
    private   TGeoDecode decode;
   // MyLocationOverlay mMyLocation;


    public ReleaseFragment() {
        sharedPref= AppContext.getInstance().getSharedPref();
        userId=sharedPref.getString("userid", "");
        mToken=sharedPref.getString("token", "");
    }
    public static ReleaseFragment newInstance(){

        return new ReleaseFragment();
    }

    enum RequestCode {
        Button(R.id.button);
        @IdRes
        final int mViewId;
        RequestCode(@IdRes int viewId) {
            mViewId = viewId;
        }
    }
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private TextView mTag;
    ArrayList<String> selectedPhotos = new ArrayList<>();

    List<String> listTown=new ArrayList<>();
    List<String> listType=new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decode = new TGeoDecode(this);
        //创建缓存组件
        mCache=ACache.get(getActivity());
        getTypeList();
        getTownList();
    }

    //获取类型列表
    public void getTypeList(){

        //从缓存中获取数据
        TypeEntity typeObject=(TypeEntity) mCache.getAsObject("typeinfo");
        //如果缓存中有数据就不访问网络
        if(typeObject!=null && typeObject.getData()!=null){

            for(int i=0;i<typeObject.getData().size();i++){
                listType.add( typeObject.getData().get(i).toString());
            }

        }else{
            //否则访问网络将获取数据存入缓存
            SupervisionApi.getTypesList(mToken,mTypeHandler);
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
            Type type = new TypeToken<TypeEntity>() {
            }.getType();
            TypeEntity typeEntity = mGson.fromJson(new String(arg2), type);
            if(typeEntity!=null && typeEntity.getData()!=null) {
                for (int i = 0; i < typeEntity.getData().size(); i++) {
                    listType.add(typeEntity.getData().get(i).toString());
                }
                mCache.put("typeinfo", typeEntity);
            }
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
        if(townObject!=null && townObject.getData()!=null){
            for(int i=0;i<townObject.getData().size();i++){

                listTown.add(townObject.getData().get(i).toString());
                if(i==(townObject.getData().size()-1)){
                    listTown.add("自定义");
                }
            }

        }else{
            //否则访问网络将获取数据存入缓存
            SupervisionApi.getReleaseTowns(mToken,mTownHandler);
        }
    }
    //获取所属单位
    private final AsyncHttpResponseHandler mTownHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            /**
             *用Gson解析返回的类型结果
             */
            mGson=new Gson();
            Type type=new TypeToken<TownEntity>(){}.getType();
            TownEntity townEntity = mGson.fromJson(new String(arg2), type);
            if(townEntity!=null && townEntity.getData()!=null) {
                for (int i = 0; i < townEntity.getData().size(); i++) {

                    listTown.add(townEntity.getData().get(i).toString());
                    if (i == (townEntity.getData().size() - 1)) {
                        listTown.add("自定义");
                    }
                }

                mCache.put("towninfo", townEntity);
            }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_release,container,false);
        //  MapView mMapView = (MapView) rootView.findViewById(R.id.main_mapview);
       //  mMyLocation = new MyLocationOverlay(getActivity(),mMapView);
      // Log.e("location", mMyLocation.toString());
        getLocationInfo();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(getActivity(), selectedPhotos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position<selectedPhotos.size()) {
                    PhotoPreview.builder()
                            .setPhotos(selectedPhotos)
                            .setCurrentItem(position)
                            .setShowDeleteButton(true)
                            .start(getActivity());
                }else{
                    checkPermission(RequestCode.Button);
                }
            }
        }));
        choseType=(EditText)rootView.findViewById(R.id.edit_type);
        choseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundAlpha(0.5f);
                initPopWindow(view , 1);
            }
        });
        choseTown=(EditText)rootView.findViewById(R.id.spinner_town);
        choseTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundAlpha(0.5f);
                initPopWindow(view , 2);
            }
        });
        mTag=(TextView)rootView.findViewById(R.id.tag);
        // locationInfoTextView = (TextView) rootView.findViewById(R.id.txt_placeholder);
        pointDesc=(EditText)rootView.findViewById(R.id.edit_desc);
        inputTown=(EditText)rootView.findViewById(R.id.edit_town);
        mLine=(View)rootView.findViewById(R.id.show_line);
        editRemark=(EditText)rootView.findViewById(R.id.edit_demo);
        radioYes=(RadioButton)rootView.findViewById(R.id.rbyes) ;
        radioNo=(RadioButton)rootView.findViewById(R.id.rbno) ;
        mSubmit=(Button)rootView.findViewById(R.id.btn_save) ;
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validNewPwd()) {
                    showWaitDialog();
                    SupervisionApi.releaseMessages(selectedPhotos, getMessages(), mToken,responseHandler);
                }
            }
        });
        return rootView;
    }

    private void checkPermission(@NonNull RequestCode requestCode) {

        int readStoragePermissionState = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermissionState = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        boolean readStoragePermissionGranted = readStoragePermissionState != PackageManager.PERMISSION_GRANTED;
        boolean cameraPermissionGranted = cameraPermissionState != PackageManager.PERMISSION_GRANTED;


        if (readStoragePermissionGranted || cameraPermissionGranted) {

            if(cameraPermissionGranted){
                Toast.makeText(getActivity(),"相机权限未开启",Toast.LENGTH_SHORT).show();

            }

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

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

                requestPermissions( permissions, requestCode.ordinal());
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
            }

        } else {

            onClick(requestCode.mViewId);
        }
    }

    private void onClick(@IdRes int viewId) {
        switch (viewId) {
            case R.id.button: {
                PhotoPicker.builder()
                        .setShowCamera(true)
                        .setSelected(selectedPhotos)
                        .setPhotoCount(9)
                        .setGridColumnCount(3)
                        .setPreviewEnabled(false)
                        .start(getActivity());
                break;
            }

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    //弹出类型选择框
    private void initPopWindow(View view ,final int type){

        View contentView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT , true);


        mycancel=(Button)contentView.findViewById(R.id.mycancel);
        mycancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundAlpha(1.0f);
                popupWindow.dismiss();
            }
        });
        title=(TextView)contentView.findViewById(R.id.popup_title);
        if(type==1){
            title.setText(R.string.leixing);
        }else{
            title.setText(R.string.xiang);
        }
        ListView listView = (ListView) contentView.findViewById(R.id.list_leixing);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_town_type,type ==1?listType:listTown);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(type==1){
                    choseType.setText(listType.get(i));
                }else{
                    if(listTown.get(i).toString().equals(getActivity().getString(R.string.zidingyi))){
                        mLine.setVisibility(View.VISIBLE);
                        inputTown.setVisibility(View.VISIBLE);
                    }else{
                        mLine.setVisibility(View.GONE);
                        inputTown.setVisibility(View.GONE);
                    }
                    choseTown.setText(listTown.get(i));
                }
                backgroundAlpha(1.0f);
                popupWindow.dismiss();
            }
        });
//
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        //设置点击空白处消失
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);//
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });

        //popupWindow.showAsDropDown(view);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    private void showWaitDialog() {
        progress =ProgressDialog.show(getActivity(),"","正在发布...",true,false);
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
    //字段非空校验
    private boolean validNewPwd() {
        boolean cancel = false;
        if(selectedPhotos.size()<1){
            Toast.makeText(getActivity(),R.string.no_pic,Toast.LENGTH_SHORT).show();
            cancel = true;
        }else if(TextUtils.isEmpty(choseType.getText().toString().trim())){
            Toast.makeText(getActivity(),R.string.no_type,Toast.LENGTH_SHORT).show();
            //   choseType.setError(getString(R.string.no_type));
            //   choseType.requestFocus();
            cancel = true;
        }else if(TextUtils.isEmpty(choseTown.getText().toString().trim())){
            Toast.makeText(getActivity(),R.string.no_town,Toast.LENGTH_SHORT).show();

            cancel = true;
        }else if(TextUtils.isEmpty(pointDesc.getText().toString().trim())){
            Toast.makeText(getActivity(),R.string.no_address,Toast.LENGTH_SHORT).show();
            ;
            cancel = true;
        }else if(inputTown.getVisibility()==View.VISIBLE && TextUtils.isEmpty(inputTown.getText().toString().trim())){
            Toast.makeText(getActivity(),R.string.no_town,Toast.LENGTH_SHORT).show();
            cancel = true;
        }
        return cancel;
    }
    //将各字段值保存为map
    private Map<String, String > getMessages(){
        Map<String, String > map=new HashMap<>();
        map.put("rc_gps_x",strLongitude==null?"-1":strLongitude);
        map.put("rc_gps_y",strLatitude==null?"-1":strLatitude);
        map.put("rc_at_id",choseType.getText().toString().trim());

        if(inputTown.getVisibility()==View.VISIBLE && !TextUtils.isEmpty(inputTown.getText().toString().trim())){
            map.put("rc_pn_id","0."+inputTown.getText().toString().trim());

        }else {
            map.put("rc_pn_id",choseTown.getText().toString().trim());
        }

        map.put("rc_state",radioYes.isChecked()?"1":"0");
        map.put("rc_addr",pointDesc.getText().toString().trim());
        map.put("rc_remark",editRemark.getText().toString().trim());
        map.put("rc_user_id",userId);
        return map;

    }

    private final  AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            if(Integer.parseInt(new String(responseBody))>0){
                Toast.makeText(getActivity(),"发布成功",Toast.LENGTH_SHORT).show();

                choseType.setHint(R.string.leixing);
                choseType.setText("");

                if(inputTown.getVisibility()==View.VISIBLE && !TextUtils.isEmpty(inputTown.getText().toString().trim())){
                    inputTown.setHint(R.string.xiangz);
                    inputTown.setText("");
                }else{
                    choseTown.setHint(R.string.xiang);
                    choseTown.setText("");
                }
                pointDesc.setHint(R.string.miaoshu);
                pointDesc.setText("");
                editRemark.setHint(R.string.beizhu);
                editRemark.setText("");
                //为设置一个空的适配器
                selectedPhotos.clear();
                photoAdapter.notifyDataSetChanged();
                mTag.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(getActivity(),"发布失败，请重新发布"+new String(responseBody),Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(getActivity(),"发布失败，请检查网络！"+new String(responseBody),Toast.LENGTH_SHORT).show();
        }
    };

    //获取定位经纬度
    public void getLocationInfo(){

        lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        int netFlags = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        //如何没有Location权限，则动态申请权限
        if(netFlags!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOACTION_REQUEST_CODE);
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
        }else {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, listener);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, listener);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);

        }

    }
    LocationListener listener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            if (isBetterLocation(location, myLocation))
            {
                //获取纬度
                double lat = location.getLatitude();
                //获取经度
                double lon = location.getLongitude();
                if (myLocation != null)
                {
                    myLocation =location;
                }
                else
                {
                    myLocation =location;
                }
                strLatitude=String.valueOf(lat);
                strLongitude=String.valueOf(lon);
                GeoPoint point = new GeoPoint((int)(Double.parseDouble(strLatitude==null?"0":strLatitude)*1000000),
                        (int)(Double.parseDouble(strLongitude==null?"0":strLongitude)*1000000));
                decode.search(point);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e("onStatusChanged", "onStatusChanged: " + provider);

        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e("onProviderEnabled", "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e("onProviderDisabled", "onProviderDisabled: " + provider);
        }

    };

    protected boolean isBetterLocation(Location location,
                                       Location currentBestLocation)
    {
        if (currentBestLocation == null)
        {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer)
        {
            return true;

        }
        else if (isSignificantlyOlder)
        {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());
        if (isMoreAccurate)
        {
            return true;
        }
        else if (isNewer && !isLessAccurate)
        {
            return true;
        }
        else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
        {
            return true;
        }
        return false;
    }
    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null)
        {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onGeoDecodeResult(TGeoAddress addr, int errCode) {
        if(errCode != 0)
        {

            Toast.makeText(getActivity(), "获取地址失败！", Toast.LENGTH_LONG).show();
            return;
        }
        String address = addr.getAddress();
        pointDesc.setText(address);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }





}
