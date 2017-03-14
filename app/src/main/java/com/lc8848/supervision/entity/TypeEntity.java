package com.lc8848.supervision.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxg on 2016/10/8.
 */

public class TypeEntity  implements Serializable {

    private String errno;

    private List<Data> data;

    public TypeEntity() {
    }

    public TypeEntity(List<Data> datas, String errno) {
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
        private String at_id;
        private String at_name;

        public Data() {
        }

        public Data(String at_id, String at_name) {
            this.at_id = at_id;
            this.at_name = at_name;
        }

        public String getAt_id() {
            return at_id;
        }

        public void setAt_id(String at_id) {
            this.at_id = at_id;
        }

        public String getAt_name() {
            return at_name;
        }

        public void setAt_name(String at_name) {
            this.at_name = at_name;
        }


        public String toString(){

            return at_id+"."+at_name;


        }
    }


}
