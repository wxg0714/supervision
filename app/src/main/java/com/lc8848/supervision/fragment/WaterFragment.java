package com.lc8848.supervision.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lc8848.supervision.R;
import com.lc8848.supervision.activity.WaterInfoActivity;

/**
 * Created by wxg on 2016/8/23.
 */

public class WaterFragment extends Fragment {
    private WebView mWaterView;
    private static  final String szURL="http://www.ykwsgz.gov.cn/wx/sz/sz1";
    private static  final String dbsURL="http://www.ykwsgz.gov.cn/wx/sz/sz2";
    private static  final String yysURL="http://www.ykwsgz.gov.cn/wx/sz/sz3";
    private static  final String indexURL="http://www.ykwsgz.gov.cn/wx/sz/sz1";
    private static  final String filterURL="http://www.ykwsgz.gov.cn/wx/sz/sz1/vat";
    private ProgressDialog progress;
    public WaterFragment() {
    }
    public static WaterFragment newInstance(){
        return new WaterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_water,container,false);
        mWaterView=(WebView)rootView.findViewById(R.id.water_view);
        mWaterView.getSettings().setJavaScriptEnabled(true);
        showWaitDialog();
        mWaterView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!(url.equals(szURL)||url.equals(dbsURL)|| url.equals(yysURL)||url.contains(filterURL))) {
                    Intent intent = new Intent(getActivity(), WaterInfoActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }else{
                    view.loadUrl(url);
                }
                return true;
            }
        });
        mWaterView.loadUrl(szURL);
        return rootView;
    }

    private void showWaitDialog() {
        progress = ProgressDialog.show(getActivity(),"","正在加载",true,false);
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
}
