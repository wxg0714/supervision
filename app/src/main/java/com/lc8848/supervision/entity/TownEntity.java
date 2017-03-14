package com.lc8848.supervision.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxg on 2016/10/8.
 */

public class TownEntity implements Serializable {

    private String errno;

    private List<Data> data;

    public TownEntity() {
    }

    public TownEntity(List<Data> datas, String errno) {
        this.data = datas;
        this.errno = errno;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public static class Data{
        private String pn_id;
        private String pn_name;

        public Data() {
        }

        public Data(String pn_id, String pn_name) {
            this.pn_id = pn_id;
            this.pn_name = pn_name;
        }

        public String getPn_id() {
            return pn_id;
        }

        public void setPn_id(String pn_id) {
            this.pn_id = pn_id;
        }

        public String getPn_name() {
            return pn_name;
        }

        public void setPn_name(String pn_name) {
            this.pn_name = pn_name;
        }

        @Override
        public String toString() {
            return  pn_id + '.' + pn_name ;
        }
    }


}
