package com.lc8848.supervision.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息详情实体
 * Created by wxg on 2016/10/13.
 */

public class MsgDetailEntity implements Serializable{

    private List<DetailInfo> data;
    private List<Photos1> photos1;
    private List<Photos1> photos2;
    private AfterDataEntity reply_data;

    public MsgDetailEntity() {
    }

    public List<DetailInfo> getData() {
        return data;
    }

    public void setData(List<DetailInfo> data) {
        this.data = data;
    }

    public List<Photos1> getPhotos1() {
        return photos1;
    }

    public void setPhotos1(List<Photos1> photos1) {
        this.photos1 = photos1;
    }

    public List<Photos1> getPhotos2() {
        return photos2;
    }

    public void setPhotos2(List<Photos1> photos2) {
        this.photos2 = photos2;
    }

    public AfterDataEntity getReply_data() {
        return reply_data;
    }

    public void setReply_data(AfterDataEntity reply_data) {
        this.reply_data = reply_data;
    }
}
