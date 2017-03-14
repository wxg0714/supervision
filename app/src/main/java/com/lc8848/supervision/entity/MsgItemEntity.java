package com.lc8848.supervision.entity;


import java.io.Serializable;

/**
 * 消息列表项实体
 * Created by wxg on 2016/10/12.
 */

public class MsgItemEntity implements Serializable {
    private int rc_id;
    private String rc_num;
    private String rc_addr;
    private String rc_date;
    private String rc_state;

    public MsgItemEntity() {
    }

    public MsgItemEntity(String rc_addr, String rc_date, int rc_id, String rc_num, String rc_state) {
        this.rc_addr = rc_addr;
        this.rc_date = rc_date;
        this.rc_id = rc_id;
        this.rc_num = rc_num;
        this.rc_state = rc_state;
    }

    public String getRc_addr() {
        return rc_addr;
    }

    public void setRc_addr(String rc_addr) {
        this.rc_addr = rc_addr;
    }

    public String getRc_date() {
        return rc_date;
    }

    public void setRc_date(String rc_date) {
        this.rc_date = rc_date;
    }

    public int getRc_id() {
        return rc_id;
    }

    public void setRc_id(int rc_id) {
        this.rc_id = rc_id;
    }

    public String getRc_num() {
        return rc_num;
    }

    public void setRc_num(String rc_num) {
        this.rc_num = rc_num;
    }

    public String getRc_state() {
        return rc_state;
    }

    public void setRc_state(String rc_state) {
        this.rc_state = rc_state;
    }
}
