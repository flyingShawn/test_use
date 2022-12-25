package com.grampus.hualauncherkai.Tools;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import java.util.Calendar;

public class DigitalClock1 extends android.widget.DigitalClock{
    Calendar mCalendar;
    private final static String mFormat = "yyyy年MM月dd日 EEEE\nhh:mm";//"hh:mm\n  yyyy年M月dd日\n  EEEE";//"EEEE,MMMM-dd-yyyy hh:mm aa";//h:mm:ss aa
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    public DigitalClock1(Context context) {

        super(context);
        initClock(context);
    }

    public DigitalClock1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context){
        Resources r = context.getResources();
        if(mCalendar == null){
            mCalendar = Calendar.getInstance();
        }
        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mFormatChangeObserver);

    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();

        mHandler = new Handler();

        mTicker = new Runnable(){
            @Override
            public void run() {
                if(mTickerStopped){
                    return ;
                }
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                String time=DateFormat.format(mFormat, mCalendar).toString();

                setLineSpacing(0,1f);
                Spannable sp = new SpannableString(time);
                sp.setSpan(new AbsoluteSizeSpan(80,true),0,5,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                //sp.setSpan(new StyleSpan(Typeface.BOLD),0,5,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sp.setSpan(new AbsoluteSizeSpan(15,true),5,time.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                sp.setSpan(new AbsoluteSizeSpan(15,true),0,16,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sp.setSpan(new AbsoluteSizeSpan(70,true),16,time.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sp.setSpan(new StyleSpan(Typeface.BOLD),16,time.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);


                setText(sp);
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }



    private class FormatChangeObserver extends ContentObserver{

        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {

        }
    }
}