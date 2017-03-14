package com.lc8848.supervision.entity;

import java.io.Serializable;

/**
 * 数据操作结果实体类
 * Created by wxg on 2016/8/5.
 */

public class Result implements Serializable {
    private String errno;
    private String token;
    private String auth;
    private String unit;


    public Result() {
    }

    public Result(String errno, String token,String auth,String unit) {
        this.errno = errno;
        this.token = token;
        this.auth=auth;
        this.unit=unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
