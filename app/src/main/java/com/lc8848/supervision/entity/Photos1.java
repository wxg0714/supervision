package com.lc8848.supervision.entity;

import java.io.Serializable;

/**
 * Created by wxg on 2016/10/14.
 */
public class Photos1 implements Serializable {

    private String path;

    public Photos1(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
