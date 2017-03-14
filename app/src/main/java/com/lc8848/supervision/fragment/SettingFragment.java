package com.lc8848.supervision.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chengtao.autoupdate.AutoUpdate;
import com.chengtao.autoupdate.AutoUpdateDialog;
import com.lc8848.supervision.AppContext;
import com.lc8848.supervision.LoginActivity;
import com.lc8848.supervision.R;
import com.lc8848.supervision.activity.AboutActivity;
import com.lc8848.supervision.activity.UpdatePwdActivity;

/**
 * Created by wxg on 2016/7/19.
 */

public class SettingFragment extends Fragment {

    private  TextView mUserInfo;
    private TextView mUpdatePwd;
    private TextView mLogout;
    private TextView mAbout;
    //private TextView mUpdateApp;
    public SettingFragment() {
    }
    public static SettingFragment newInstance(){
        return new SettingFragment();
    }
    //数据准备
    String json = "{VersionCode:10000, "+
            "VersionName:1.0.0, UpdateMessage:'1.修复BUG<br>2.修复BUG', "+
            "ApkUrl:'http://apk.hiapk.com/appdown/com.ss.android.article.news'}";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_setting,container,false);
//        mUpdateApp=(TextView)rootView.findViewById(R.id.tv_update);
//        //检查更新
//        mUpdateApp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(AppContext.getInstance().getPackageInfo().versionCode == 1.0){
//
//                    Toast.makeText(getActivity(),"你安装的已经是最新版本了",Toast.LENGTH_SHORT).show();
//
//                }else {
//                    //初始化自动更新
//                    AutoUpdate autoUpdate = new AutoUpdate(getActivity(), json);
//                    //实现接口
//                    autoUpdate.setAutoDialogListener(new AutoUpdateDialog.AutoUpdateDialogListener() {
//                        @Override
//                        public void onClick(int status) {
//                            switch (status) {
//                                case AutoUpdateDialog.UPDATE:
//                                Toast.makeText(getActivity(),"正在更新...",Toast.LENGTH_SHORT).show();
//                                    break;
//                                case AutoUpdateDialog.CANCEL:
//
//                                    break;
//                            }
//                        }
//                    });
//
//                }
//
//            }
//        });
        mUpdatePwd=(TextView)rootView.findViewById(R.id.tv_reset_pwd);
        mUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), UpdatePwdActivity.class);
                startActivity(intent);
            }
        });
        mLogout=(TextView)rootView.findViewById(R.id.tv_logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("logout","out");
                startActivity(intent);
                getActivity().finish();
            }
        });
        mUserInfo=(TextView)rootView.findViewById(R.id.tv_myInfo);
        mUserInfo.setText(getString(R.string.my_info, AppContext.getInstance().getSharedPref().getString("username","")));

        mAbout=(TextView)rootView.findViewById(R.id.tv_about);
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }


}
