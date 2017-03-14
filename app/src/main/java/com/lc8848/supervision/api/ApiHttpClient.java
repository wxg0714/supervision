package com.lc8848.supervision.api;


import com.lc8848.supervision.AppContext;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Locale;

import cz.msebera.android.httpclient.client.params.ClientPNames;


public class ApiHttpClient {
    public final static String HOST = "www.ykwsgz.gov.cn";
    private static String BASE_URL = "http://www.ykwsgz.gov.cn/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public ApiHttpClient() {
    }
    public static void get(String partUrl, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteUrl(partUrl), handler);

    }
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);

    }
    public static void post(String partUrl, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteUrl(partUrl), handler);
    }
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void setHttpClient(AsyncHttpClient c) {
        client = c;
        client.addHeader("Accept-Language", Locale.getDefault().toString());
        client.addHeader("Host", HOST);
        client.addHeader("Connection", "Keep-Alive");
        client.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        setUserAgent(ApiClientHelper.getUserAgent(AppContext.getInstance()));
    }
    public static void setUserAgent(String userAgent) {
        client.setUserAgent(userAgent);
    }


}
