package com.grampus.hualauncherkai.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.grampus.hualauncherkai.Data.AppDataHub;
import com.grampus.hualauncherkai.Data.NetCtrlHub;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.amap.AMapLocations;
import com.grampus.hualauncherkai.common.ConfigUtil;
import com.grampus.hualauncherkai.log.LogTrace;
import com.grampus.hualauncherkai.util.DeviceInfoUtil;
import com.grampus.hualauncherkai.util.GPSUtil;
import com.grampus.hualauncherkai.util.OkDroidUtil;
import com.mph.okdroid.response.RawResHandler;

import java.util.List;

//import com.grampus.hualauncherkai.common.Utils;

public class TaskThink extends Service
{
    private static final String TAG = "TaskThink";
    private AMapLocations mapLocations = null;
    private static boolean running = true;



    public TaskThink()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void startThinkActvity()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (running)
                {
                    String now = getRunningActivityName();

                    //LogTrace.i(TAG, "startThinkActvity", "now = " + now);
                    //String config = Save.getValue(getApplicationContext(), "WIFI_APP_WHITE", "0");
                    //if (!AppDataHub.isCanSetting && now.contains("com.android.setting") && "0".equals(config))

                    if (!AppDataHub.isCanSetting && now.contains("com.android.setting") &&
                            !NetCtrlHub.get().getServiceAd().equals(""))
                    {
                        NetDataHub.get().addLog("EMMTaskThink-------2 ");
                   //     System.out.println("屏蔽设置：" + "当前运行" + now + "是设置，需屏蔽");
                        try
                        {
                            //System.out.println("屏蔽设置：" + "准备屏蔽");
                            Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
                            mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                            mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            startActivity(mHomeIntent);
                   //         System.out.println("屏蔽设置：-------------->" + "屏蔽成功");
                        }
                        catch (Exception e)
                        {
                            NetDataHub.get().addLog("EMMTaskThink-----e:"+e.toString());

                        }

                    }
                    else
                    {
                        Log.d("EMMTaskThink","EMMTaskThink-----当前运行:" + now + "不需屏蔽");
                        //NetDataHub.get().addLog("EMMTaskThink-----当前运行:" + now + "不需屏蔽");
                    }

                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        Log.d("EMMTaskThink","startThinkActvity--2-error "+e.toString());
                    }
                }


            }
        }).start();

    }
    /**
     * 获取在最上方的应用包名
     *
     * @param context
     * @return
     */
    public static String getPackageNameOnTop(Context context) {
        LogTrace.i(TAG, "getPackageNameOnTop---","getPackageName()");
        String result;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

            result = runningProcesses.get(0).processName;
            LogTrace.i(TAG,"getPackageNameOnTop---", "Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT + " runningProcesses.size() = " + runningProcesses.size());
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                // 前台程序
//              if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        LogTrace.e(TAG, "getPackageNameOnTop---","在前台的应用名 = " + activeProcess);
                    } else {
                        LogTrace.w(TAG,"getPackageNameOnTop---", "在后台的应用名 = " + activeProcess);
                    }
                }
//              }
            }
        } else {
            LogTrace.i(TAG, "getPackageNameOnTop---","Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            result = componentInfo.getPackageName();
        }

        return result;
    }

    private String getHandSetInfo(){
        String handSetInfo=
                "手机型号:" + android.os.Build.MODEL +
                        ",SDK版本:" + android.os.Build.VERSION.SDK +
                        ",系统版本:" + android.os.Build.VERSION.RELEASE +
                        ",DeviceName:" + android.os.Build.DEVICE+
                        ",DISPLAY:"+android.os.Build.DISPLAY+
                        ",ID:"+android.os.Build.ID+
                        ",PRODUCT:"+android.os.Build.PRODUCT;

        return handSetInfo;

    }
    private String getRunningActivityName()
    {
        //备注：当编译的时候设置Min SdK为16的时候，在安卓4.1版本调用getRunningTasks函数会报错的，需要加<uses-permission android:name="android.permission.GET_TASKS" />才不报错。
        //而当在android 7.0版本上面运行，则不加android.permission.GET_TASKS这个也是可以运行，不报错的。
        try
        {
            //String ss = getHandSetInfo();
            //LogTrace.i(TAG,"getRunningActivityName----->",ss);

            //getPackageNameOnTop(getApplicationContext());

           // android.os.Build.VERSION.RELEASE;
           // String s=test.substring(0,1);
            ///*
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            //List<RunningTaskInfo> runlist = activityManager.getRunningTasks(1);
            //String aa = runlist.toString();
            String runningActivity = activityManager.getRunningTasks(1).get(0).baseActivity.getPackageName();
            //String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            return runningActivity;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

/*
    void initLocationFun()
    {
        try {

            String serviceAd = Save.getValue(this, "service_ad", "");

            if (!serviceAd.equals("")) {
                mapLocations = new AMapLocations(this, serviceAd);
                mapLocations.initLocation();
                mapLocations.startLocation();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

 */

    @Override
    public void onCreate()
    {
        super.onCreate();
    //    LogTrace.i(TAG, "onCreate", "===============================");
        //Log.d("EMMTaskThink","onCreate-------1 ");
        running = true;
        startThinkActvity();

        //初始化定位
        //initLocationFun();//add by gwb;

        /* del by gwb;2020.12.1
        timerGps();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.grampus.hualauncherkai.action.LOCATION_RECEIVER");
        intentFilter.addAction("com.grampus.hualauncherkai.action.START_RECEIVER");
        registerReceiver(locationReceiver, intentFilter);

         */
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        running = false;
        LogTrace.i(TAG, "onDestroy", "===============================");
        //unregisterReceiver(locationReceiver);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }


    private void timerGps()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (running)
                {
                    try
                    {

                        startLocation();

                        Thread.sleep(300000);
                        //Thread.sleep(10000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

    private void startLocation()
    {
        try {
            //Save.putValue(this, "service_ad", "180.102.152.91:16670");
            String serviceAd = Save.getValue(this, "service_ad", "");
            if (!serviceAd.equals("")) {

            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            /* del by gwb;2020.9.14
            String action = intent.getAction();
            if ("com.grampus.hualauncherkai.action.LOCATION_RECEIVER".equals(action))
            {
                if (intent != null)
                {
                    String latitude = intent.getStringExtra("latitude");//纬度
                    String longitude = intent.getStringExtra("longitude");//经度
                    if (latitude != null && longitude != null)
                    {
                        upLoadGPS(latitude, longitude);
                    }
                }
                stopLocation();
            }
            else if ("com.grampus.hualauncherkai.action.START_RECEIVER".equals(action))
            {
                startLocation();
                upLoadLog();
            }
            */
        }

    };

    private void upLoadGPS(String latitude, String longitude)
    {
        String lat;
        String lng;
        /*DPoint sourceLatLng = new DPoint();
        DPoint desLatLng = null;
        sourceLatLng.setLatitude(Double.parseDouble(latitude));
        sourceLatLng.setLongitude(Double.parseDouble(longitude));
        CoordinateConverter converter  = new CoordinateConverter(this);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.BAIDU);
        // sourceLatLng待转换坐标点 DPoint类型
        try {
            converter.coord(sourceLatLng);
            // 执行转换操作
            desLatLng = converter.convert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (desLatLng != null){
            lat = desLatLng.getLatitude()+"";
            lng = desLatLng.getLongitude()+"";
        }else{
            lat = latitude;
            lng = longitude;
        }*/
        double[] latlng = GPSUtil.gcj02_To_Bd09(Double.parseDouble(latitude), Double.parseDouble(longitude));
        lat = latlng[0] + "";
        lng = latlng[1] + "";
        String serviceAd = Save.getValue(this, "service_ad", "");
        //"/showmap.php?device_name=123&IP=192.168.1.230&MAC=00-30-67-A6-AB-BD&longitude=118.8029140176&latitude=32.0647517242";
        if (!serviceAd.equals(""))
        {
            LogTrace.i(TAG, "upLoadGPS------------", "----------------");
            String deviceName = android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL;
            String ip = DeviceInfoUtil.getWifiIp(this);
            String mac = DeviceInfoUtil.getMacAddr(this);
            String url = "http://" + serviceAd + "/" + String.format(ConfigUtil.URL_UPLOAD_GPS, deviceName, ip, mac, lng, lat);
            /*HttpClientUtil.getUrl(this, url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    LogTrace.i(TAG, "upLoadGPS----onFailure", "responseString="+responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    LogTrace.i(TAG, "upLoadGPS----onSuccess", "responseString ="+responseString);
                }
            });*/
            OkDroidUtil.getUrl(this, url, new RawResHandler()
            {
                @Override
                public void onSuccess(int statusCode, String response)
                {
                    LogTrace.i(TAG, "upLoadGPS----onFailure", "responseString=" + response);
                }

                @Override
                public void onFailed(int statusCode, String errMsg)
                {
                    LogTrace.i(TAG, "upLoadGPS----onSuccess", "responseString =" + errMsg);
                }
            });
        }
    }

    private void upLoadLog()
    {
        String serviceAd = Save.getValue(this, "service_ad", "");
        if (!serviceAd.equals(""))
        {
            LogTrace.i(TAG, "upLoadLog------------", "----------------");
            String mac = DeviceInfoUtil.getMacAddr(this);
            String url = "http://" + serviceAd + "/" + String.format(ConfigUtil.URL_UPLOAD_LOG, mac);
            OkDroidUtil.getUrl(this, url, new RawResHandler()
            {
                @Override
                public void onSuccess(int statusCode, String response)
                {
                    LogTrace.i(TAG, "upLoadLog----onFailure", "responseString=" + response);
                }

                @Override
                public void onFailed(int statusCode, String errMsg)
                {
                    LogTrace.i(TAG, "upLoadLog----onSuccess", "responseString =" + errMsg);
                }
            });
        }
    }


}
