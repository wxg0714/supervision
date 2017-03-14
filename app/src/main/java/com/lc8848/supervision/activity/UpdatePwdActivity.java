package com.lc8848.supervision.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lc8848.supervision.AppContext;
import com.lc8848.supervision.R;
import com.lc8848.supervision.api.SupervisionApi;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class UpdatePwdActivity extends AppCompatActivity {

    private ImageView mBack;
    private EditText mOldPwd;
    private EditText mNewPwd;
    private EditText mRenewPwd;
    private Button mSubmit;
    private SharedPreferences sharedPref;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        sharedPref= AppContext.getInstance().getSharedPref();;
        token=sharedPref.getString("token","");

        mBack=(ImageView)findViewById(R.id.back_to_setting);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mOldPwd=(EditText)findViewById(R.id.lastPassword);
        mNewPwd=(EditText)findViewById(R.id.newPassword);
        mNewPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
            }
        });
        mRenewPwd=(EditText)findViewById(R.id.reNewPassword);
        mSubmit=(Button)findViewById(R.id.btn_update_save);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validNewPwd()){
                    SupervisionApi.updateNewPassword(mOldPwd.getText().toString().trim(),mNewPwd.getText().toString().trim(),token, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (Integer.parseInt(new String (responseBody)) == 0) {
                                mOldPwd.setText("");
                                mNewPwd.setText("");
                                mRenewPwd.setText("");
                                Toast.makeText(UpdatePwdActivity.this, "修改成功,请重新登录", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UpdatePwdActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(UpdatePwdActivity.this,"密码修改失败,请检查网络",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                //执行修改密码操作


            }
        });
    }
    //验证密码
    private boolean validNewPwd() {
        boolean cancel = false;
        if (TextUtils.isEmpty(mOldPwd.getText().toString())){
            mOldPwd.setError(getString(R.string.error_reset_invalid_password));
            mOldPwd.requestFocus();
            cancel = true;
        }
        else if(TextUtils.isEmpty(mNewPwd.getText().toString())){
            mNewPwd.setError(getString(R.string.error_reset_invalid_password));
            mNewPwd.requestFocus();
            cancel = true;
        }
        else if (!mNewPwd.getText().toString().equals(mRenewPwd.getText().toString())) {
            mRenewPwd.setError(getString(R.string.no_equal));
            mRenewPwd.requestFocus();
            cancel = true;
        }
        else if(!AppContext.getInstance().getSharedPref().getString("password","").equals(mOldPwd.getText().toString())){
            mOldPwd.setError(getString(R.string.error_old_pwd));
            mOldPwd.requestFocus();
            cancel = true;
        }
        return cancel;
    }


}
