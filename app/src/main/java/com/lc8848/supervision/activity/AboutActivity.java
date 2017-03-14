package com.lc8848.supervision.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lc8848.supervision.R;

public class AboutActivity extends AppCompatActivity {

    private TextView mYewu;
    private TextView mService;
    private ImageView mImageBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mYewu=(TextView)findViewById(R.id.yewu_number);
        mService=(TextView)findViewById(R.id.service_number);
        mYewu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ mYewu.getText().toString()));
                if(intent.resolveActivity(getPackageManager())!=null){

                    startActivity(intent);
                }
            }
        });
        mService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ mService.getText().toString()));
                if(intent.resolveActivity(getPackageManager())!=null){

                    startActivity(intent);
                }
            }
        });
        mImageBack=(ImageView)findViewById(R.id.back_to_it);
        mImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
