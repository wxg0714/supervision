package com.lc8848.supervision.entity;

import java.io.Serializable;

/**
 * Created by wxg on 2016/10/14.
 */
public class DetailInfo implements Serializable {

    private int rc_id;
    private String rc_num ;
    private String rc_date;
    private String rc_addr;
    private String rc_state;
    private String rc_remark;
    private String pn_name;
    private String rc_type;
    private String username;
    private String aq_cod;
    private String aq_nh;
    private String aq_tp;

    public DetailInfo(String rc_addr, String aq_tp, String pn_name, String aq_cod,
                      String aq_nh, String username, String rc_type, String rc_state,
                     String rc_remark, String rc_num, int rc_id,
                      String rc_date) {
        this.rc_addr = rc_addr;
        this.aq_tp = aq_tp;
        this.pn_name = pn_name;
        this.aq_cod = aq_cod;
        this.aq_nh = aq_nh;
        this.username = username;
        this.rc_type = rc_type;
        this.rc_state = rc_state;
        this.rc_remark = rc_remark;
        this.rc_num = rc_num;
        this.rc_id = rc_id;
        this.rc_date = rc_date;
    }

    public int getRc_id() {
        return rc_id;
    }

    public String getRc_num() {
        return rc_num;
    }

    public String getRc_addr() {
        return rc_addr;
    }

    public String getPn_name() {
        return pn_name;
    }

    public String getAq_cod() {
        return aq_cod;
    }

    public String getAq_nh() {
        return aq_nh;
    }

    public String getUsername() {
        return username;
    }

    public String getRc_type() {
        return rc_type;
    }

    public String getRc_remark() {
        return rc_remark;
    }

    public String getRc_state() {
        return rc_state;
    }

    public String getRc_date() {
        return rc_date;
    }

    public String getAq_tp() {
        return aq_tp;
    }
}
