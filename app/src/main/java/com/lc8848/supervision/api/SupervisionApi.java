package com.lc8848.supervision.api;

import android.content.SharedPreferences;
import android.util.Log;

import com.lc8848.supervision.AppContext;
import com.lc8848.supervision.util.PictureUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 *
 * Created by wxg on 2016/8/4.
 */

public class SupervisionApi {

    /**
     * 登陆
     *
     * @param username
     * @param password
     * @param handler
     */
    public static void login(String username, String password,
                             AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("user", username);
        params.put("passwd", password);
        String loginurl = "Ht/Api/login";//服务器地址
        ApiHttpClient.post(loginurl, params, handler);
    }
    /**
     * 获取所有消息列表
     * @param handler
     */
    public static void getAllMessages(String token,AsyncHttpResponseHandler handler) {
        getMessagesList(token,null,null, null,null,0, handler);
    }
    /**
     * 获取筛选消息列表
     * @param handler
     */
    public static void getMessagesList(String token,String num,String userId,
                                       String townId,String typeId,int bTag,
                                       AsyncHttpResponseHandler handler) {
        String url = "Ht/Api/getList/token/"+token+"/s/"+num+"/b/"+bTag+"/m/"+userId+"/u/"+townId+"/t/"+typeId;//服务器地址
        ApiHttpClient.get(url, handler);
    }

    /**
     * 获取消息明细
     *
     * @param id 消息的id
     * @param handler
     */
    public static void getMessagesDetail(int id,String token, AsyncHttpResponseHandler handler) {
        ApiHttpClient.get("Ht/Api/getInfo/token/"+token+"/id/"+id, handler);
    }

    /**
     * 发布信息
     */
    public static void releaseMessages(ArrayList<String> fileList, Map<String,String> message, String token,AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("message", message);
        if(fileList.size()>0) {
            File[] files = new File[fileList.size()];
            for (int i = 0; i < fileList.size(); i++) {
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
                    String imageFileName = "JPEG_" + timeStamp+"_"+i+ ".jpg";
                    String compressPath= PictureUtil.compressImage(fileList.get(i),imageFileName);
                    files[i] = new File(compressPath);
                }catch (IOException e){

                    e.printStackTrace();
                }

            }
            try {
                params.put("uploadimage[]", files);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         ApiHttpClient.post("Ht/Api/upload/token/"+token, params, handler);
    }

    /**
     * 整改完成信息
     */
    public static void changedMessages(ArrayList<String> fileList, String msgid, String token,String msgContent,AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("ry_rc_id",msgid);
        params.put("ry_content",msgContent);
        if(fileList.size()>0) {
            File[] files = new File[fileList.size()];
            for (int i = 0; i < fileList.size(); i++) {
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
                    String imageFileName = "JPEG_" + timeStamp+"_"+i+ ".jpg";
                    String compressPath= PictureUtil.compressImage(fileList.get(i),imageFileName);
                    files[i] = new File(compressPath);
                }catch (IOException e){

                    e.printStackTrace();
                }

            }
            try {
                params.put("uploadimage[]", files);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ApiHttpClient.post("/Ht/Api/setReply/token/"+token, params, handler);
    }


    /**
     * 修改密码-更新密码
     */
    public static void updateNewPassword(String passwd,String newPwd,String token,AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("passwd",passwd);
        params.put("newPasswd",newPwd);
        ApiHttpClient.post("Ht/Api/changePasswd/token/"+token, params, handler);
    }
    /**
     * 获取列表所有乡镇
     */
    public static void getTownsList(String token,AsyncHttpResponseHandler handler){

        ApiHttpClient.post("Ht/Api/getUnits/token/"+token, null, handler);
    }
    /**
     * 获取发布所有乡镇
     */
    public static void getReleaseTowns(String token,AsyncHttpResponseHandler handler){

        ApiHttpClient.post("/Ht/Api/getSendUnits/token/"+token, null, handler);
    }


    /**
     * 获取所有类型
     */
    public static void getTypesList(String token,AsyncHttpResponseHandler handler){
        ApiHttpClient.post("Ht/Api/getTypes/token/"+token, null, handler);

    }
    /**
     * 检查更新版本
     */
    public static void checkUpdate(AsyncHttpResponseHandler handler) {
        ApiHttpClient.get("Ht/Api/MobileAppVersion.xml", handler);
    }





}
