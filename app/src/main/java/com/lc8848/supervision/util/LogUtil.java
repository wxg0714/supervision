package com.lc8848.supervision.util;

import android.util.Log;

/**
 * 定制日志工具类，控制日志的自由打印
 * Created by wxg on 2016/8/3.
 */

public class LogUtil {
    public  static final int VERBOSE=1;
    public  static final int DEBUG=2;
    public  static final int INFO=3;
    public  static final int WARN=4;
    public  static final int ERROR=5;
    public  static final int NOTHING=6;
    public  static final int LEVEL=VERBOSE;
    public static void v(String tag,String msg){
        if(LEVEL<=VERBOSE){
            Log.v(tag,msg);
        }
    }
    public static void d(String tag,String msg){
        if(LEVEL<=DEBUG){
            Log.d(tag,msg);
        }
    }
    public static void i(String tag,String msg){
        if(LEVEL<=INFO){
            Log.i(tag,msg);
        }
    }
    public static void w(String tag,String msg){
        if(LEVEL<=WARN){
            Log.w(tag,msg);
        }
    }
    public static void e(String tag,String msg){
        if(LEVEL<=ERROR){
            Log.e(tag,msg);
        }
    }





}