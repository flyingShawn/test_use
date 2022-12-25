package com.grampus.hualauncherkai.Receiver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.FloatWindow.EMMFloatWindowService;
import com.grampus.hualauncherkai.Tools.BootReceiver;
import com.grampus.hualauncherkai.Tools.Tell;
import com.grampus.hualauncherkai.util.StringUtil;

import java.lang.reflect.Method;
import java.util.List;

import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static com.grampus.hualauncherkai.UI.MainActivity.g_bUseHuaWeiMDM;

//import static com.amap.api.location.APSServiceBase.LOCATION;

/**
 * Created by Grampus on 2017/5/11.
 */


public class WifiHub extends BootReceiver
{
    private static final String WIFISSID_UNKNOW = "<unknown ssid>";
//    private static AlertDialog settingWifiDialog; //add by fsy 2021.11.3 警告框弃用
    public static boolean g_isWhiteWifi = true;
    public static String wifiSsid = "<unknown ssid>";
    public static String getWifiSSID(Context context) {
        /*
         *  先通过 WifiInfo.getSSID() 来获取
         */
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String wifiId = info != null ? info.getSSID() : null;
            String result = wifiId != null ? wifiId.trim() : null;
            if (!StringUtil.isEmpty(result)) {
        //        NetDataHub.get().addLog("WifiHub---getWifiSSID-------1---wifi:"+result );
                // 部分机型上获取的 ssid 可能会带有 引号
                if (result.charAt(0) == '"' && result.charAt(result.length() - 1) == '"') {
                    result = result.substring(1, result.length() - 1);
                }
            }
            // 如果上面通过 WifiInfo.getSSID() 来获取到的是 空或者 <unknown ssid>，则使用 networkInfo.getExtraInfo 获取
            if (StringUtil.isEmpty(result) || WIFISSID_UNKNOW.equalsIgnoreCase(result.trim())) {
                NetworkInfo networkInfo = getNetworkInfo(context);
                if (networkInfo.isConnected()) {
                    if (networkInfo.getExtraInfo() != null) {
                        result = networkInfo.getExtraInfo().replace("\"", "");
            //            NetDataHub.get().addLog("WifiHub---getWifiSSID----2--wifi:[" +result+"]");
                    }
                }
            }
            // 如果获取到的还是 空或者 <unknown ssid>，则遍历 wifi 列表来获取
            if (StringUtil.isEmpty(result) || WIFISSID_UNKNOW.equalsIgnoreCase(result.trim())) {
            //    NetDataHub.get().addLog("WifiHub---getWifiSSID------3" );
                result = getSSIDByNetworkId(context);
           }
            return result;
        }
        catch(Exception e) {
            NetDataHub.get().addLog("WifiHub---getWifiSSID error" +e.toString());
        }
        return WIFISSID_UNKNOW;
    }

    public static NetworkInfo getNetworkInfo(Context context){
        try{
        //    NetDataHub.get().addLog("WifiHub---getNetworkInfo ---1" );
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connectivityManager){
                return connectivityManager.getActiveNetworkInfo();
            }
        }catch(Exception e){
            NetDataHub.get().addLog("WifiHub---getNetworkInfo--error" +e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /*  又看了一遍，此处方法和第一种直接获取应该没区别啊，似乎无用。获取不成功也是位置权限没开的原因吧，应该是一样的
     *  遍历wifi列表来获取
     */
    private static String getSSIDByNetworkId(Context context) {
        String ssid = WIFISSID_UNKNOW;
        try {
//            NetDataHub.get().addLog("WifiHub---getSSIDByNetworkId------1" );
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //    wifiManager.setWifiEnabled(true);
            if (wifiManager != null) {

                while (wifiManager.getWifiState() != WIFI_STATE_ENABLED) {
                    Thread.sleep(100);
                    NetDataHub.get().addLog("WifiHub" );
                }


                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int networkId = wifiInfo.getNetworkId();
                //NetDataHub.get().addLog("WifiHub---getSSIDByNetworkId------wifiInfo.getNetworkId："+networkId );

                List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
             //   NetDataHub.get().addLog("WifiHub---getConfiguredNetworks---size:"+configuredNetworks.size() );

                if(configuredNetworks.isEmpty())
                {
                    NetDataHub.get().addLog("WifiHub---getConfiguredNetworks---获取wifi列表为空" );
                }

                for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                    NetDataHub.get().addLog("WifiHub---wifiConfiguration.SSID-:"+wifiConfiguration.SSID+"id"+wifiConfiguration.networkId );
                    if (wifiConfiguration.networkId == networkId) {
                        ssid = wifiConfiguration.SSID;
                        NetDataHub.get().addLog("WifiHub---wifiConfiguration.SSID-==" );
                        break;        //测试注掉
                    }
                }

            }
            return ssid;
        }
        catch(Exception e) {
            NetDataHub.get().addLog("WifiHub---getSSIDByNetworkId--error:"+e.toString() );
        }
        return ssid;
    }



    public static boolean wifiThink(Context context)
    {
        try {
            Tell.log("开始执行wifiThink");
            if (g_bUseHuaWeiMDM)
                return false;//华为MDM的不处理。

            if(NetDataHub.get().bOpenMachineRightNowNoWifiControl == true) {
                NetDataHub.get().addLog("wifiThink------刚开机，此连不上服务器，不进行wifi控制.");
                return false;
            }


            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            String ssid = "";
            String bssid = "";

            //-------add by gwb;2020.9.27
            //Build.VERSION.SDK_INT  这个值好像是手机系统的编译SDK值。add by gwb;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
               // NetDataHub.get().addLog("WifiHub------安卓版本 > 10");
                ssid = getWifiSSID(context);
            }
            else
            {
                //NetDataHub.get().addLog("WifiHub------安卓版本<= 10");
                ssid = wifiInfo.getSSID();//发现在安卓9.0下面是得不到wifi ID的。
                bssid = wifiInfo.getBSSID();
            }

            /*  del by gwb;2020.9.22  这个好像没用吧。
            //申请权限
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Request permission from user
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            } else {//Permission already granted
                if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                    ssid = wifiInfo.getSSID();//Here you can access your SSID
                }
            }*/


            /*--------add by gwb;2020.9.21
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                //Request permission from user
                NetDataHub.get().addLog("wifiThink------没有CHANGE_WIFI_STATE权限，调用申请" );
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, LOCATION);
            }
            else
                NetDataHub.get().addLog("wifiThink------已经有CHANGE_WIFI_STATE权限了，不需要再申请" );

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                //Request permission from user
                NetDataHub.get().addLog("wifiThink------没有ACCESS_WIFI_STATE权限，调用申请" );
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, LOCATION);
            }
            else
                NetDataHub.get().addLog("wifiThink------已经有ACCESS_WIFI_STATE权限了，不需要再申请" );
            //-----------end.
           */

            /*  add by gwb;2020.9.27
            //Build.VERSION.SDK_INT  这个值好像是手机系统的编译SDK值。add by gwb;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                assert cm != null;
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    ssid = info.getExtraInfo();
                    Tell.log("WiFi SSID: " + ssid);
                }
            }*/

            wifiSsid = ssid;
            if (!ssid.equals("<unknown ssid>")) {
                //Tell.log("wifi白名单 当前 " + wifiInfo.getSSID() + " " + wifiInfo.getBSSID());
                Tell.log("wifi白名单 当前 ssid: " + ssid);

                g_isWhiteWifi= true;  //防止上次检测为非白名单后，管理机关闭了策略。
                //String config = Save.getValue(context, "WIFI_APP_WHITE", "0");
                //if (NetDataHub.get() != null && NetDataHub.get().isCtrlWifi() && "0".equals(config)) {
                if (NetDataHub.get() != null && NetDataHub.get().isCtrlWifi()  ) {
                    NetDataHub.get().addLog("WifiHub------wifiThink----ssid:"+ssid);
                    //if (NetDataHub.get().isInWifiList(wifiInfo.getSSID(), wifiInfo.getBSSID(), context)) {
                    if (NetDataHub.get().isInWifiList(ssid,bssid, context)) {//add by gwb;2020.9.27
                        NetDataHub.get().addLog("wifi 在白名单内，不用改变");
                     /*   if(settingWifiDialog != null) 警告框暂时不用了  add by fsy 2021.11.4
                        {
                            Log.w("wifihub","settingWifi---Dialog dismiss");
                            settingWifiDialog.dismiss();
                            return false;
                        }
                        */
                        g_isWhiteWifi= true;
                        if(EMMFloatWindowService.isStart())
                            EMMFloatWindowService.getInstance().reqShow = false;
                        return false;

                    } else {
                    /*
                        int netId = wifiInfo.getNetworkId();
                        NetDataHub.get().addLog("--------wifiInfo.getNetworkId："+netId );
                        boolean bRet=false;//bRet = wifiManager.disableNetwork(netId);
                        wifiManager.removeNetwork(netId);//add by gwb;2020.9.17
                    */
                        g_isWhiteWifi =false;
                        if(EMMFloatWindowService.isStart()) {
                            NetDataHub.get().addLog("EMMFloatWindowService start");//测试1
                            EMMFloatWindowService.getInstance().reqShow = true;
                        }else
                        {
                            if(!g_bUseHuaWeiMDM&&NetDataHub.get().isUseWifiWhite()) {//非华为  没有wifi禁用
                                    new Thread(new Runnable() { // 匿名类的Runnable接口
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(EMMApp.getInstance().mainContext, EMMFloatWindowService.class);
                                            EMMApp.getInstance().mainContext.startService(intent);
                                        }
                                    }).start();
                            }
                            NetDataHub.get().addLog("EMMFloatWindowService not start");//测试1
                        }

                  //      wifiManager.disconnect();
                 //       Intent intent = new Intent();
                 //       intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                  //      context.startActivity(intent);


                        //测试：移除断开"ytsoft"，自动链接 "TP-LINK_3121"
                    //    WifiAutoConnectManager wifiAutoConnectManager =new WifiAutoConnectManager(wifiManager);
                    //    wifiAutoConnectManager.connect("TP-LINK_3121","yangtusoft1",WIFICIPHER_WPA);

/*                        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder()
                                setSsid(ssid)
                                .setBssid(MacAddress.fromString(bssid))
                                .setWpa2Passphrase(password)
                                .build();
                        ;
                        builder.disconnect();*/

                        NetDataHub.get().addLog("wifiThink------阻止非法wifi!  ssid:["  + ssid + "]  ");

//                    wifiManager.setWifiEnabled(false);
//                    wifiManager.removeNetwork(netId);
//                    wifiManager.saveConfiguration();
//                    Looper.prepare();
//                    Tell.toast("断开非法wifi[" + wifiInfo.getSSID() + "]", context);
//                    Looper.loop();

                        return true;
                    }
                }
            }
            else{
                Thread.sleep(1000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Tell.log("wifiThink出错------->" + e.toString());
            NetDataHub.get().addLog("wifiThink------断开非法wifi!异常！！！"+e.toString());
        }
        return false;
    }

    /*  add by fsy 2021.11.3未能解决其他应用上不能弹出的问题， 此处先注掉，不删除。
    private static void SettingWifi(final Context context){
       // Log.d(TAG,"setUpServIp=--------");
        //Intent intent = new Intent(this, ForgetActivity.class);
        //startActivityForResult(intent, 1);

   //     boolean isWifiOk = false;

        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);

        if(settingWifiDialog != null)
        {
            Log.w("wifihub","settingWifi---show");
            settingWifiDialog.show();
            return;
        }

        AlertDialog.Builder builder1=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.activity_wifi_setting,null);

   //     edIP= (EditText) view.findViewById(R.id.et1);

        //builder1.setOnCancelListener()
        //tvSetUp = (ImageButton) findViewById(R.id.id_setup_ip);
        Button button= (Button)view.findViewById(R.id.id_btn_next);
        Log.w("wifihub","settingWifi---444444");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.id_btn_next:
                       {
                           Log.w("wifihub","settingWifi---55555555");
                           Intent intent = new Intent();
                           intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                           context.startActivity(intent);
                       }
                        break;
                    default:
                        break;
                }
                //
            }
        });

        //再次弹出
        Log.w("wifihub","settingWifi---55555");


        settingWifiDialog = builder1.setView(view)
                .setTitle("警告")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .show();//setTitle("设置").

        Log.w("wifihub","settingWifi---7777");

    //    settingWifiDialog.dismiss();
     //  settingWifiDialog.cancel();
    }
*/
    /** 警告框方式，暂时不用，改用悬浮框了，先注掉
     * 此方式效果更好，但未解决在其他应用上不能弹出的问题
     * fsy 2021.11.3
     * @param context
     * @return

    public boolean AlertWiFi(Context context,String ssid){
        try {
            if (NetDataHub.get() != null && NetDataHub.get().isCtrlWifi()) {
                Log.w("wifihub", "AlertWiFi---111111");
                //Tell.log("wifi 需要进行wifi操作白名单");
                //if (NetDataHub.get().isInWifiList(wifiInfo.getSSID(), wifiInfo.getBSSID(), context)) {
                if (NetDataHub.get().isInWifiList(ssid, "", context)) {//add by gwb;2020.9.27
                    //Tell.log("wifi 在白名单内，不用改变");
                    NetDataHub.get().addLog("wifiThink-----wifi 在白名单内，不用改变");
                    if (settingWifiDialog != null) {
                        Log.w("wifihub", "settingWifi---Dialog dismiss");
                        settingWifiDialog.dismiss();
                        return false;
                    }

                } else {
                    Log.w("wifihub", "settingWifi---000000");
                    SettingWifi(context);
                    Log.w("wifihub", "settingWifi---11111111");

                    return true;
                }
            }
            Log.w("wifihub", "AlertWiFi---66666");
        }  catch (Exception e) {
            Log.e("wifihub", "AlertWiFi---error"+e.toString());
        }
        return false;
    }
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //状态断开和连接各一次？
        String action = intent.getAction();

        if (action.equals("android.net.wifi.RSSI_CHANGED"))
        {
            if (wifiThink(context))
            {
                Tell.toast("检测到非法wifi", context);
               // NetDataHub.get().addLog("WifiHub---onReceive-----阻止非法wifi!");
            }
        }
    }
    /**
     * 判断热点是否开启
     *
     * @param context
     * @return
     */
    public static boolean isApOn(Context context) { //add by gwb;2021.2.19
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);

            return (Boolean) method.invoke(wifimanager);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭WiFi
     *
     * @param context
     */
    public static void closeWifi(Context context) {//add by gwb;2021.2.19
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//只有小于指定版本的才可以用。

                WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifimanager.isWifiEnabled()) {
                    wifimanager.setWifiEnabled(false);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 关闭WiFi热点
     */
    public static void closeWifiAp(Context context) {
        if(!NetDataHub.m_bForbitAP)
        {
            NetDataHub.get().addLog("closeWifiAp---m_bForbitAP=false  热点变化不处理！！！");
            return ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//只有小于指定版本的才可以用。
        //if (true) {//只有小于指定版本的才可以用。
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (isApOn(context)) {
                try {
                    NetDataHub.get().addLog("closeWifiAp---开始关闭热点!!!");

                    Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                    method.setAccessible(true);
                    WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
                    Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                    method2.invoke(wifiManager, config, false);
                } catch (Exception e) {
                    NetDataHub.get().addLog("closeWifiAp---关闭热点异常！！！");
                    e.printStackTrace();
                }
            }
            else
                NetDataHub.get().addLog("closeWifiAp---热点已关闭!");
        }
    }


}
