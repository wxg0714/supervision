package com.lc8848.supervision;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lc8848.supervision.api.SupervisionApi;
import com.lc8848.supervision.entity.Result;
import com.loopj.android.http.AsyncHttpResponseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    protected static final String TAG = LoginActivity.class.getSimpleName();
    // UI references.
     EditText mNameView;
     EditText mPasswordView;
     CheckBox mAutoLogin;
     Button mLoginButton;

    private String mUserName = "";
    private String mPassword = "";
    private  SharedPreferences sharedPref;
    private  SharedPreferences.Editor mEditor;
    private Gson mGson;
    private Result mResult;
    private  ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        Intent intent=getIntent();
        //SharedPreferences 读取用户登录信息
        sharedPref = AppContext.getInstance().getSharedPref();
        //mEditor 写入用户登录信息
        mEditor=sharedPref.edit();
        mNameView.setText(sharedPref.getString("username",""));
        mPasswordView.setText(sharedPref.getString("password",""));
        mAutoLogin.setChecked(sharedPref.getBoolean("AUTO_LOGIN",false));
        if(mAutoLogin.isChecked()){
            /**
             *如果是初始加载则，自动登录。否则，不能自动登录
             */
            if (intent.getStringExtra("logout")==null){
                handleLogin();
            }
        }

    }
    /**
     * 初始化视图
     */
    private void initView(){
        mNameView= (EditText) findViewById(R.id.name);
        mPasswordView= (EditText) findViewById(R.id.password);
        mAutoLogin= (CheckBox) findViewById(R.id.auto_login);
        mAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAutoLogin.isChecked()) {
                    mEditor.putBoolean("AUTO_LOGIN",true);

                } else {
                    mEditor.putBoolean("AUTO_LOGIN",false);
                }
                mEditor.commit();
            }
        });
        mLoginButton=(Button)findViewById(R.id.sign_in_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

    }
    /**
     * 登录业务逻辑处理
     */
    private void handleLogin() {
        if (prepareForLogin()) {
            return;
        }
        mUserName = mNameView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        showWaitDialog();
        SupervisionApi.login(mUserName, mPassword, mHandler);
    }

    private final AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            /**
             *用Gson解析返回的结果，如果登录成功，则保存用户信息
             **/
            mGson=new Gson();
            mResult=new Result();
            mResult=mGson.fromJson(new String(arg2),Result.class);
            if(mResult.getErrno().equals("0")){
                mEditor.putString("token",mResult.getToken());
                mEditor.putString("auth",mResult.getAuth());
                mEditor.putString("unit",mResult.getUnit());
                mEditor.putString("username", mUserName);
                mEditor.putString("password", mPassword);
                mEditor.putString("userid", mResult.getToken().substring(0,mResult.getToken().indexOf("-")));
                mEditor.commit();


                handleLoginSuccess();
            }else{

                Toast.makeText(LoginActivity.this,"账名或密码错误",Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            Toast.makeText(LoginActivity.this,"网络错误，请检查网络！",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onFinish() {
            super.onFinish();


        }
    };

    private void handleLoginSuccess() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra("auth",mResult.getAuth());
        startActivity(intent);
        progress.dismiss();
        finish();
    }


    private boolean prepareForLogin() {
        mNameView.setError(null);
        mPasswordView.setError(null);
        String name = mNameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        boolean cancel = false;
        if (!AppContext.getInstance().getNetWorkStatue()){
            Toast.makeText(this,"当前没有可用的网络链接",Toast.LENGTH_LONG).show();
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            mNameView.requestFocus();
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
            cancel = true;
        }
        return cancel;
    }

    private void showWaitDialog() {
        progress =ProgressDialog.show(this,"","正在登录...",true,false);
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

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再次点击退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

