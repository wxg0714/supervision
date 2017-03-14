package com.lc8848.supervision.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 自定义分割线
 * Created by wxg on 2016/12/14.
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    public void onDrawOver(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount-1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //以下计算主要用来确定绘制的位置
            final int top = child.getBottom() + params.bottomMargin;

            //绘制分割线
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(1f);
            Path path = new Path();
            path.moveTo(left-2, top);
            path.lineTo(right,top);
            //绘制虚线
            //  PathEffect effects = new DashPathEffect(new float[]{15,15,15,15},5);//此处单位是像素不是dp  注意 请自行转化为dp
            //  paint.setPathEffect(effects);
            c.drawPath(path, paint);


        }
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}