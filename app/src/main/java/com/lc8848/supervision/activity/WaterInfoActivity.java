package com.lc8848.supervision.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lc8848.supervision.R;

public class WaterInfoActivity extends AppCompatActivity {

    private ImageView mBackWater;
    private WebView mWaterInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_info);
        mWaterInfoView=(WebView)findViewById(R.id.water_info_view);
        mWaterInfoView.getSettings().setJavaScriptEnabled(true);
        Intent intent=getIntent();
        String strURL=intent.getStringExtra("url");
        mWaterInfoView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWaterInfoView.loadUrl(strURL);

        mBackWater=(ImageView)findViewById(R.id.back_to_water);
        mBackWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
