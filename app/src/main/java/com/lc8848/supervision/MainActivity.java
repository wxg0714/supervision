package com.lc8848.supervision;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lc8848.supervision.fragment.MessageFragment;
import com.lc8848.supervision.fragment.ReleaseFragment;
import com.lc8848.supervision.fragment.SettingFragment;
import com.lc8848.supervision.fragment.WaterFragment;


public class MainActivity extends AppCompatActivity {

    private static final String LOGIN_TYPE_ROOT="15";
    private static final String LOGIN_TYPE_NORMAL="13";
    private static final String LOGIN_TYPE_TOWN="12";
    private  RadioGroup mRadioGroup;
    private TextView mTitle;
    private MessageFragment messagesFragment;
    private SettingFragment settingFragment;
    private ReleaseFragment releaseFragment;
    private WaterFragment waterFragment;
    private Fragment mContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        Intent intent=getIntent();
        final String auth=intent.getStringExtra("auth");
        if(auth.equals(LOGIN_TYPE_ROOT)){
            setContentView(R.layout.activity_root_main);
        }else if(auth.equals(LOGIN_TYPE_NORMAL)){
            setContentView(R.layout.activity_normal_main);
        }else if(auth.equals(LOGIN_TYPE_TOWN)){
            setContentView(R.layout.activity_town_main);
        }
        mRadioGroup= (RadioGroup) findViewById(R.id.main_tab_RadioGroup);
        mTitle= (TextView) findViewById(R.id.title);
        initContent();
        if(messagesFragment !=null){
            if(savedInstanceState!=null){
                return;
            }
        }else {
            messagesFragment=MessageFragment.newInstance();
        }
        if(releaseFragment !=null){
            if(savedInstanceState!=null){
                return;
            }
        }else {
            releaseFragment=ReleaseFragment.newInstance();

        }
        if(settingFragment !=null){
            if(savedInstanceState!=null){
                return;
            }

        }else {
            settingFragment=SettingFragment.newInstance();
        }
        if(waterFragment !=null){
            if(savedInstanceState!=null){
                return;
            }

        }else {
            waterFragment=WaterFragment.newInstance();
        }

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(auth.equals("15")) {
                    switch (i) {
                        case R.id.radio_users:
                            switchContent(messagesFragment);
                            break;
                        case R.id.radio_release:
                            switchContent(releaseFragment);
                            break;
                        case R.id.radio_settings:
                            switchContent(settingFragment);
                            break;
                        case R.id.radio_water:
                            switchContent(waterFragment);
                            break;
//                    case R.id.radio_map:
//                        break;
                    }
                }else if(auth.equals("13")){
                    switch (i) {
                        case R.id.radio_users:
                            switchContent(messagesFragment);
                            break;
                        case R.id.radio_release:
                            switchContent(releaseFragment);
                            break;
                        case R.id.radio_settings:
                            switchContent(settingFragment);
                            break;
//                    case R.id.radio_map:
//                        break;
                    }

                }else{

                    switch (i) {
                        case R.id.radio_users:
                            switchContent(messagesFragment);
                            break;
                        case R.id.radio_settings:
                            switchContent(settingFragment);
                            break;
//                    case R.id.radio_map:
//                        break;
                    }



                }

            }
        });

    }


    //重写OnActivityResult方法，以便在Fragment中能接收到返回的数据
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        releaseFragment.onActivityResult(requestCode,resultCode,data);
    }
    private void initContent() {
        if(messagesFragment==null) {
            messagesFragment =  MessageFragment.newInstance();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_contaner, messagesFragment);
        mContent = messagesFragment;
        transaction.commit();

    }
    public void switchContent(Fragment to) {
        if (mContent != to) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(mContent).add(R.id.frame_contaner, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(mContent).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            mContent = to;
        }
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再次点击退出移动督查", Toast.LENGTH_SHORT).show();
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
