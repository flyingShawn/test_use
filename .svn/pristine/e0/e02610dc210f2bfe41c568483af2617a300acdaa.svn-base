package com.grampus.hualauncherkai.FloatWindow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.NetDataHub;

import java.util.Timer;
import java.util.TimerTask;

import static com.grampus.hualauncherkai.R.mipmap.ic_launcher;
import static com.grampus.hualauncherkai.UI.MainActivity.g_bUseHuaWeiMDM;

/***
* @author  fsy
* @date    2022/1/28 10:59
* @return
* @description  悬浮框置顶，提醒wifi不在白名单内
*/
public class EMMFloatWindowService extends Service {
    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    public Handler handler = new Handler();
    private final String TAG = "EMMFloatService";//getClass().getName();

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Timer timer;

    private static EMMFloatWindowService mService = null;

    public static synchronized EMMFloatWindowService getInstance(){
        if(mService == null){
            mService = new EMMFloatWindowService();
        }
        return mService;
    }

    public static boolean isStart() {
        if(g_bUseHuaWeiMDM||!NetDataHub.get().isUseWifiWhite())    //华为不会开启    //没有设置wifi白名单也不会开启
        {
            return false;
        }
        return mService != null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        NetDataHub.get().addLog("EMMFloatWindowService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 3000);
            NetDataHub.get().addLog("EMMFloatWindowService Start new RefreshTask()" );
        }
        mService = this;
        setForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        stopForeground(true);
        mService= null;
    }

    public static boolean reqShow = false;
    //2021.11.3尝试更改，检测到wifi非法，弹出窗口
    private class RefreshTask extends TimerTask {
        @Override
        public void run() {
            //没有悬浮窗显示，则创建悬浮窗。
            try {

            //    NetDataHub.get().addLog("ctr:"+NetDataHub.get().isCtrlWifi()+"|WinShow:"+EMMForbidWifiWindowManager.isWindowShowing()+"|isShow:"+isShow);//测试1
                if (NetDataHub.get().isCtrlWifi()&&(!EMMForbidWifiWindowManager.isWindowShowing() && reqShow)) {
                //    Log.d(TAG, "没有悬浮窗显示，则创建悬浮窗 ");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EMMForbidWifiWindowManager.createWifiWarnWindow(EMMApp.getInstance().mainContext);
                        }
                    }, 500);
                }
                //控制wifi策略关闭或者---!NetDataHub.get().isCtrlWifi()||
                else if (EMMForbidWifiWindowManager.isWindowShowing() && !reqShow || !NetDataHub.get().isCtrlWifi() ) {
                //    Log.d(TAG, "回到EMM了，取消图标 ");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EMMForbidWifiWindowManager.removeWifiWarnWindow(EMMApp.getInstance().mainContext);
                        }
                    }, 50);
                }

                /*
                //为控制wifi,即无策略，或者未使用wifi白名单，为什么要未使用wifi白名单？||!NetDataHub.get().isUseWifiWhite()
                else if (!NetDataHub.get().isCtrlWifi()){
                //    NetDataHub.get().addLog("wifi策略取消");//测试1
                //    Log.d(TAG, "wifi策略取消");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EMMForbidWifiWindowManager.removeSmallWindow(EMMApp.mainContext);
                        }
                    }, 50);
                    onDestroy();//销毁
                }
                */

            }catch (Exception e){
                Log.e(TAG, "RefreshTask error---"+e.toString());
                NetDataHub.get().addLog("RefreshTask error---"+e.toString());
        }
        }
    }

    private void setForeground() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("Foreground_Service",
                        "Foreground_Service", NotificationManager.IMPORTANCE_LOW);
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager == null) {
                    return;
                }
                manager.createNotificationChannel(channel);
                Notification notification =
                        new NotificationCompat.Builder(this, "Foreground_Service")
                                .setContentTitle("wifi白名单策略")
                                .setContentText("开启检测中")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), ic_launcher))
                                .build();
                startForeground(13, notification);
            }
        }catch (Exception e) {
            Log.e(TAG, "setForeground------error:"+e.toString());
        }
    }
}
