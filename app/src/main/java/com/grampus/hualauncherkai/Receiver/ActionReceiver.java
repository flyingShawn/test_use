package com.grampus.hualauncherkai.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.AndroidPolicy;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.service.EMMAccessibilityService;

/**
 * @author fsy
 * @date 2021/12/27 17:13
 */
public class ActionReceiver extends BroadcastReceiver {


    private static ActionReceiver mReceiver;

    public static synchronized ActionReceiver getInstance() {
        if (mReceiver == null) {
            mReceiver = new ActionReceiver();
        }
        return mReceiver;
    }


    private Handler handler;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        try {
            String action = intent.getAction();
            if ("com.grampus.hualauncherkai.action.STOP_RECEIVER".equals(action)) {
                Process.killProcess(Process.myPid());
            } else if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {//add by gwb;2021.2.20  监听热点变化
                //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra("wifi_state", 0);

               // NetDataHub.get().addLog("MainActivity-----onReceive---热点变化---wifi_state:" + state);
                if (state == 13) {
                    WifiHub.closeWifiAp(context);
                }
            }
            else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {

                Log.e("EMMA11y", "亮屏啦，");
                if(AndroidPolicy.NACAddr0.equals(""))
                {
                    Object NACAddrObj = Save.readFile(context, "NACAddr");  //add by fsy 2021.12.20
                    if(NACAddrObj != null)
                    {
                        AndroidPolicy.NACAddr0 = (String) NACAddrObj;
                        Log.w("EMMA11y", "==NACAddr0 = "+AndroidPolicy.NACAddr0);
                    }
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String rs = AndroidPolicy.getNACCheck();
                        NetDataHub.get().addLog("EMMA11yService-----准入访问返回结果:【"+rs+"】");
                    }
                }).start();

            }
            else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {       //黑屏  一定程度上就是默认锁屏

                EMMApp.getInstance().screenLock = true;
                Log.e("EMMA11y", "黑屏  screenLock = true");
                NetDataHub.addLog1("ACTIONoff.screenLock = "+EMMApp.getInstance().screenLock );
            }
            else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {       //解锁
                Log.e("EMMA11y", "解锁  screenLock = false");
                EMMApp.getInstance().screenLock = false;

                NetDataHub.addLog1("ACTION.screenLock = "+EMMApp.getInstance().screenLock );
                if(EMMApp.getInstance().shouldUpdate)
                {
                    Log.w("EMMA11y", "亮屏需要下载更新，");
                    Message message = new Message();
                    message.what = 4;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if(EMMAccessibilityService.isStart()){
                            message.what = 11;
                        }
                    }
                    message.obj = EMMApp.getInstance().servApkVersion;
                    handler.sendMessage(message);
                }
            }
            else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
            //    Log.e("EMMA11y", "锁屏"); //这个好像是关机重启对话框
            }
        }catch(Exception e){
            Log.e("EMMA11y", "ActionReceiver onReceive error:" + e.getMessage());
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
