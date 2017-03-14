package com.lc8848.supervision.entity;

import java.io.Serializable;

/**
 * 整改后信息实体
 * Created by wxg on 2016/11/15.
 */

public class AfterDataEntity implements Serializable{

    private String ry_content;
    private String ry_date;
    private String username;

    public AfterDataEntity(String ry_content, String ry_date, String username) {
        this.ry_content = ry_content;
        this.ry_date = ry_date;
        this.username = username;
    }

    public String getRy_content() {
        return ry_content;
    }

    public String getRy_date() {
        return ry_date;
    }

    public String getUsername() {
        return username;
    }
}
