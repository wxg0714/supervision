package com.lc8848.supervision.entity;
import java.io.Serializable;

/**
 * 用户实体类：实现序列化
 * Created by wxg on 2016/8/5.
 */

public class User implements Serializable {
    private  int usId;
    private String usUserName;
    private String usPassword;
    private String usName;
    private String usPhone;
    public User() {  }

    public User(String usUserName, String usPassword, int usId) {
        this.usUserName = usUserName;
        this.usPassword = usPassword;
        this.usId = usId;
    }
   //备用
    public User(int usId, String usPassword, String usUserName, String usName, String usPhone) {
        this.usId = usId;
        this.usPassword = usPassword;
        this.usUserName = usUserName;
        this.usName = usName;
        this.usPhone = usPhone;
    }
    public int getUsId() {
        return usId;
    }

    public void setUsId(int usId) {
        this.usId = usId;
    }

    public String getUsPassword() {
        return usPassword;
    }

    public void setUsPassword(String usPassword) {
        this.usPassword = usPassword;
    }

    public String getUsName() {
        return usName;
    }

    public void setUsName(String usName) {
        this.usName = usName;
    }

    public String getUsPhone() {
        return usPhone;
    }

    public void setUsPhone(String usPhone) {
        this.usPhone = usPhone;
    }

    public String getUsUserName() {
        return usUserName;
    }

    public void setUsUserName(String usUserName) {
        this.usUserName = usUserName;
    }

    public String toString() {

        return "User [us_id=" + usId + ", us_username=" + usUserName + ", us_name=" + usName
                + ", us_photo=" + usPhone  + "]";
    }
}
