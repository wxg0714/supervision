package com.lc8848.supervision;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;
import com.lc8848.supervision.api.ApiHttpClient;
import com.lc8848.supervision.entity.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import java.util.Properties;


/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @author wxg
 * @version 1.0
 * @created 2016/8/3
 */

public class AppContext extends Application {
    private static AppContext instance;

    private  SharedPreferences sharedPref;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static AppContext getInstance() {

        return instance;
    }
    /**
     * 获取用户登录信息
     */
    public SharedPreferences getSharedPref() {
        return getSharedPreferences("UserInfo",Context.MODE_PRIVATE);

    }
    /**
     * 检查网络链接状态
     */
    public boolean getNetWorkStatue(){
        if (((ConnectivityManager) getApplicationContext().
                getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo() == null)
            return  false;
        else
            return true;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }
    /**
     * 获取系统当前时间
     *
     * @return String
     * */
    public String getTime(){

        Time time = new Time();
        time.setToNow();
        int year = time.year;
        int month = time.month;
        int day = time.monthDay;
        int minute = time.minute;
        int hour = time.hour;
        int sec = time.second;

        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+sec;
    }

}
