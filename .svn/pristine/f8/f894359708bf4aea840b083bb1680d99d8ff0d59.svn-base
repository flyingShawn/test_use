package com.grampus.hualauncherkai.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by DGY on 2018/5/21.
 */

public class MyViewPager extends ViewPager {
    private float mPointX;
    private float mPointY;
    private float preX;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointX = ev.getX();
                mPointY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mPointX) > Math.abs(ev.getY() - mPointY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        boolean res = super.onInterceptTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            preX = event.getX();
        } else {
            if( Math.abs(event.getX() - preX)> 4 ) {
                return true;
            } else {
                preX = event.getX();
            }
        }
        return res;
    }*/
}
