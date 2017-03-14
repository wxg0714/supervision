package com.lc8848.supervision;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.lc8848.supervision.api.SupervisionApi;
import com.lc8848.supervision.entity.Result;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class WelcomeActivity extends Activity {

    private SharedPreferences sharedPref;
    private String mUserName = "";
    private String mPassword = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        //SharedPreferences 读取用户登录信息
        sharedPref = AppContext.getInstance().getSharedPref();
        mUserName=sharedPref.getString("username","");
        mPassword=sharedPref.getString("password","");


        Timer timer=new Timer();
        TimerTask task=new TimerTask(){
            @Override
            public void run() {
                //是否自动登录
                if(sharedPref.getBoolean("AUTO_LOGIN",false) && AppContext.getInstance().getNetWorkStatue()){
                    SupervisionApi.login(mUserName, mPassword, mHandler);
                }else{
                    Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            }
        };
        timer.schedule(task,1000*2);
    }
    private final AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            Gson mGson=new Gson();
            Result mResult=mGson.fromJson(new String(arg2),Result.class);
            if(mResult.getErrno().equals("0")) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("auth",mResult.getAuth());
                startActivity(intent);
            }else{
                Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);

            }
            WelcomeActivity.this.finish();
        }
        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();

        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };


}
