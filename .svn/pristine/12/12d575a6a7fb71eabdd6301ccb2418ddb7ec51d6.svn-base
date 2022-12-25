package com.grampus.hualauncherkai.FloatWindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.grampus.hualauncherkai.Data.NetDataHub;

public class EMMForbidWifiWindowManager {
    private static WifiForbidWindow wifiWarnWindow;
    private static LayoutParams wifiWarnWindowParams;
    private static WindowManager mWindowManager;        //用于控制在屏幕上添加或移除悬浮窗。
    private final static String TAG = "EMMFloatWindowManager";
    public static void createWifiWarnWindow(Context context) {
        try {
            WindowManager windowManager = getWindowManager(context);
            Point spt = new Point();
            windowManager.getDefaultDisplay().getSize(spt);
            int screenWidth = spt.x;
            int screenHeight = spt.y;
            if (wifiWarnWindow == null) {
                wifiWarnWindow = new WifiForbidWindow(context);
               if (wifiWarnWindowParams == null) {
              //     NetDataHub.get().addLog("\n\nsmallWindowParams == null\n");//测试1
                    Log.i(TAG,"wifiWarnWindowParams == null" );
                    wifiWarnWindowParams = new LayoutParams();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {  //安卓4.4以下用这个（待测，并没有相应设备）
                        wifiWarnWindowParams.type = LayoutParams.TYPE_PHONE;
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {     //安卓8以上和8以下的Type要区分好
                       wifiWarnWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;//TYPE_SYSTEM_ERROR
                   }else {
                       wifiWarnWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;//TYPE_SYSTEM_ALERT
                   }

                    wifiWarnWindowParams.format = PixelFormat.RGBA_8888;
                    wifiWarnWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | LayoutParams.FLAG_NOT_FOCUSABLE;
                    wifiWarnWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                    wifiWarnWindowParams.x = screenWidth;
                    wifiWarnWindowParams.y = (int)(screenHeight*0.8);
                    wifiWarnWindowParams.width = WifiForbidWindow.viewWidth;
                    wifiWarnWindowParams.height = WifiForbidWindow.viewHeight;
                    //Log.i(TAG,"smallWindowParams == null" );
                }
                /* smallWindow.setParams(smallWindowParams);*/
                windowManager.addView(wifiWarnWindow, wifiWarnWindowParams);
            }
        }catch (Exception e)
        {
            NetDataHub.get().addLog("updateViewPosition  error :"+e.toString());//测试1
            Log.e(TAG,"updateViewPosition  error :"+e.toString());
        }
    }

    public static void removeWifiWarnWindow(Context context) {
        if (wifiWarnWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(wifiWarnWindow);

            wifiWarnWindow = null;
        }
    }

    public static boolean isWindowShowing() {
        return wifiWarnWindow !=null;
    }

    public static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
}
