package com.lc8848.supervision.entity;

import android.text.TextUtils;

/**
 * 描述：
 */
public class FilterUrl {
    private volatile static FilterUrl filterUrl;

    private FilterUrl() {
    }

    public static FilterUrl instance() {
        if (filterUrl == null) {
            synchronized (FilterUrl.class) {
                if (filterUrl == null) {
                    filterUrl = new FilterUrl();
                }
            }
        }
        return filterUrl;
    }

    public String singleListPosition;
    public int position;
    public String positionTitle;

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        if (!TextUtils.isEmpty(singleListPosition)) {
            buffer.append("singleListPosition=");
            buffer.append(singleListPosition);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public void clear() {
        filterUrl = null;
    }


}