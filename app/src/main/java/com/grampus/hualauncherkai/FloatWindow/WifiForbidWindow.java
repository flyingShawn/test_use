package com.grampus.hualauncherkai.FloatWindow;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.util.XClickUtil;

import java.lang.reflect.Field;

import static com.grampus.hualauncherkai.UI.MainActivity.g_bAllowSetting;

public class WifiForbidWindow extends LinearLayout {
    private Context context;
    public static int viewWidth;                //记录小悬浮窗的宽度
    public static int viewHeight;               //记录小悬浮窗的高度
    private static int statusBarHeight;         //记录系统状态栏的高度
    private final String TAG = "EMMFloatWindowSmallView";
    public WifiForbidWindow(Context context) {
        this(context, null);
    }
    Button settingWifi;
    public WifiForbidWindow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setupUI();
    }

    private void setupUI() {
        Log.w(TAG,"EMMFloatWindowSmallView  setupUI");
//
//        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.forbid_wifi_window,this);
        View view = findViewById(R.id.layout_warn);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

/*
        LinearLayout mainLinerLayout = (LinearLayout) this.findViewById(R.id.ll_wifiList);
        TextView textview=new TextView(context);
        StringBuffer ssidList = new StringBuffer();
        try {
            for (int i = 0; i < NetDataHub.wifiList.length(); i++)
            {
                JSONObject o =NetDataHub.wifiList.getJSONObject(i);
                String whitSsid = o.getString("Name1");
                ssidList.append(whitSsid);
                ssidList.append("\n");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        textview.setText(ssidList);
     //   textview.setTextColor(Color.parseColor("#FFAA00"));
        textview.setTextColor(Color.rgb(0, 0, 180));
        textview.setTextSize(23);
        mainLinerLayout.addView(textview);
*/

        settingWifi = findViewById(R.id.id_setting_wifi);

        settingWifi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(XClickUtil.isFastDoubleClick(settingWifi,1000))
                    return;
                EMMFloatWindowService.getInstance().handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EMMForbidWifiWindowManager.removeWifiWarnWindow(context);
                    }
                }, 20);
                g_bAllowSetting = true;
                EMMFloatWindowService.getInstance().reqShow = false;//    跳转过去取消窗口，回来
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//TOP
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                context.startActivity(intent);
            }
        });
    }

/*
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }
*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        try {
//           Intent intent = new Intent(context, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);

            /*
             Log.w(TAG,"EMMFloatWindowSmallView     onTouchEvent  ");
             switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xInView = event.getX();
                    yInView = event.getY();
                    xInScreen = event.getRawX();
                    yInScreen = event.getRawY()-getStatusBarHeight();
                    break;
                case MotionEvent.ACTION_MOVE:
                    xInScreen = event.getRawX();
                    yInScreen = event.getRawY() - getStatusBarHeight();
                    updateViewPosition();
                    break;
                case MotionEvent.ACTION_UP:
                    Point spt = new Point();
                    mWindowManager.getDefaultDisplay().getSize(spt);
                    int screenWidth = spt.x;
                    if (xInScreen < screenWidth/2) {
                        xInScreen = 0;
                    } else {
                        xInScreen = screenWidth;
                    }
                    updateViewPosition();
                    break;

            }*/
        }catch (Exception e)
        {
            System.out.println("updateViewPosition  error :"+e.toString());
            Log.e(TAG,"updateViewPosition  error :"+e.toString());
        }finally {
            return super.onTouchEvent(event);
        }

    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
