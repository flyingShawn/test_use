package com.grampus.hualauncherkai.Data;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.FloatWindow.EMMFloatWindowService;
import com.grampus.hualauncherkai.Receiver.WifiHub;
import com.grampus.hualauncherkai.TcpSock.CLogonSock;
import com.grampus.hualauncherkai.Tools.DeviceReceiver;
import com.grampus.hualauncherkai.Tools.HttpRequest;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.Tools.Tell;
import com.grampus.hualauncherkai.Tools.appUtils;
import com.grampus.hualauncherkai.amap.AMapLocations;
import com.grampus.hualauncherkai.service.EMMAccessibilityService;
import com.grampus.hualauncherkai.util.DeviceInfoUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.Calendar;

import static com.grampus.hualauncherkai.Data.NetDataHub.NACAddr;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlBlueTooth;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlCamera;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlUSB;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlWifi;
import static com.grampus.hualauncherkai.UI.MainActivity.androidv;
import static com.grampus.hualauncherkai.UI.MainActivity.g_bUseHuaWeiMDM;
import static com.grampus.hualauncherkai.UI.MainActivity.szVersionNum;
import static com.grampus.hualauncherkai.util.DeviceInfoUtil.getPhoneIp;
import static com.grampus.hualauncherkai.util.DeviceInfoUtil.getWifiMacAddress;

//import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlInfrared;

/**
 * Created by Grampus on 2017/5/10.
 */


public class NetCtrlHub {

    private static NetCtrlHub netCtrlHub;

    static public NetCtrlHub get() {
        return netCtrlHub;
    }

    private AMapLocations mapLocations = null;

    public String szUploadGPS_Url = "";

    private CLogonSock logonSocketClient = null;

    public CLogonSock getLogonSocketClient() {
        return logonSocketClient;
    }

    public static void init(Activity activity, Handler handler) {
        if (netCtrlHub == null) {
            netCtrlHub = new NetCtrlHub();
            netCtrlHub.activity = activity;
            netCtrlHub.handler = handler;
            netCtrlHub.init();
        }
    }

    Activity activity;
    String serviceAd;
    Handler handler;

    private DevicePolicyManager devicePolicyManager = null;

    private void init() {
        serviceAd = Save.getValue(activity, "service_ad", "");
        timerThink();
    }

    public void setServiceAd(String serviceAd) {
        Save.putValue(activity, "service_ad", serviceAd);
        this.serviceAd = serviceAd;
    }

    public String getServiceAd() {
        return serviceAd;
    }


    JSONArray getWhiteList() {
        try {
            String rs = HttpRequest.httpGet("http://" + serviceAd + "/TelSafeDesk.php?Action=whiteapp" +
                    "&Mac=" + DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum, null);

//            String rs=HttpRequest.httpGet("http://"+serviceAd+"/TelSafeDesk.php?Action=whiteapp" +//没有数据暂用haha测试
//                   "&Mac=haha",null);
            JSONArray jsonArray = new JSONArray(rs);
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            Tell.log("TimerCheck出错");
            return null;
        }
    }

    JSONArray getTimerCheck() {
        try {
            String rs = HttpRequest.httpGet("http://" + serviceAd + "/TelSafeDesk.php?Action=TimerCheck" +
                    "&Mac=" + DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum, null);
            JSONArray jsonArray = new JSONArray(rs);
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            Tell.log("TimerCheck出错" + e.toString());
            return null;
        }
    }

    public static String NACUrl = "";
    public static boolean bHaveGetPolicy = false;//add by gwb;
    /**
     * 2020.04.10 未来修改
     * 添加准入功能
     */
    private static boolean NACCheckFalg = true;

    public String getNACCheck() {
        if (NACCheckFalg) {
            try {
                Calendar calendar = Calendar.getInstance();
                //JSONArray NACChecArray = new JSONArray("");
                //String rs = HttpRequest.httpPost("http://" + NACAddr + "/TimerAction.php?ip=" + DeviceInfoUtil.getPhoneIp() + "&mac=" + getWifiMacAddress(), null, null);
                NACUrl = "http://" + NACAddr + "/TimerAction.php?ip=" + getPhoneIp() + "&mac=" + getWifiMacAddress();

                NetDataHub.get().addLog("\n准入---getNACCheck---【" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "】---------");

                String rs = HttpRequest.httpGet(NACUrl, null);

                NetDataHub.get().addLog("[准入定时执行]:" + NACUrl + "  rs:[" + rs + "]\n");

                Log.w("EMMNACUrl", "返回值：" + rs);
                NACCheckFalg = false;
                return rs;
            } catch (Exception e) {
                e.printStackTrace();
                Tell.log("TimerCheck出错" + e.toString());
                NetDataHub.get().addLog("[准入定时执行---Catch Error]:" + NACUrl + "  " + e.toString() + "\n\n");
                return "";
            }
        } else {
            NACCheckFalg = true;
            return "";
        }
    }

    public String getServerApkVersion()//获取服务器上APK版本号  add by gwb;
    {
        String szVersion = "";

        try {
            String szUrl = "http://" + serviceAd + "/TelSafeDesk.php?Action=getServerApkVersion" + "&Mac=" +
                    DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum;
            //String rs = HttpRequest.httpGet("http://" + serviceAd + "/TelSafeDesk.php?Action=getServerApkVersion" + "&Mac=" + SystemDataGet.getMacAddress(activity), null);
            Log.d("EMM", szUrl);
            String rs = HttpRequest.httpGet(szUrl, null);
            JSONArray jsonArray = new JSONArray(rs);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                if (name.equals("SafeDeskApkVersion")) {
                    if (jsonObject.getString("Value") != null) {
                        szVersion = jsonObject.getString("Value");
                        break;
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return szVersion;
    }

    /**
     * 获取准入IP地址
     * 若成功获取则返回true
     * 若不存在准入IP地址，则返回false
     *
     * @return
     */
    public boolean getNACAddr() {
        try {
            String rs = HttpRequest.httpGet("http://" + serviceAd + "/TelSafeDesk.php?Action=getconfiglist" + "&Mac=" +
                    DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum, null);
            JSONArray jsonArray = new JSONArray(rs);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                if (name.equals("ZRIPSet")) {
                    if (jsonObject.getString("Value") != null) {
                        NetDataHub.get().setNACAddr(jsonObject.getString("Value"));

                        handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();

                        return true;
                    } else {
                        NetDataHub.get().setNACAddr("");

                        handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();

                        return false;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 2020.04.09 未来修改
     * 不仅获取wifi和app白名单的状态，也能获取红外、蓝牙、相机和USB设备的状态
     * 返回值为JSONArray类型的字符串
     *
     * @return
     */
    private String getIsCtrlWifiAndApp() {
        try {

            String rs = HttpRequest.httpGet("http://" + serviceAd + "/TelSafeDesk.php?Action=getconfiglist" + "&Mac=" +
                    DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum, null);
            JSONArray jsonArray = new JSONArray(rs);
            //NetDataHub.get().addLog("http://" + serviceAd + "/TelSafeDesk.php?Action=getconfiglist" + "&Mac=" + SystemDataGet.getMacAddress(activity));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                if (name.equals("wififilterOn")) {
                    boolean tmp;

                    if (jsonObject.getString("Value").equals("0")) {
                        tmp = NetDataHub.get().setCtrlWifi(false);
                    } else {
                        tmp = NetDataHub.get().setCtrlWifi(true);
                    }
                    if (tmp) {
                        //NetDataHub.get().saveWifiListAndWhiteApp();
                    }
                } else if (name.equals("appfilteron")) {
                    boolean tmp;
                    if (jsonObject.getString("Value").equals("0")) {
                        tmp = NetDataHub.get().setCtrlApp(false);
                    } else {
                        tmp = NetDataHub.get().setCtrlApp(true);
                    }
                    if (tmp) {
                        //handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();
                    }
                } else if (name.equals("ForbitLYon")) {
                    boolean tmp;
                    if (jsonObject.getString("Value").equals("0")) {
                        tmp = NetDataHub.get().setCtrlBlueTooth(false);
                    } else {
                        tmp = NetDataHub.get().setCtrlBlueTooth(true);
                    }
                    if (tmp) {
                        //handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();
                    }
                }

//                else if (name.equals("ForbitHWon"))
//                {
//                    boolean tmp;
//                    if (jsonObject.getString("Value").equals("0"))
//                    {
//                        tmp = NetDataHub.get().setCtrlInfrared(false);
//                    }
//                    else
//                    {
//                        tmp = NetDataHub.get().setCtrlInfrared(true);
//                    }
//                    if (tmp)
//                    {
//                        handler.sendEmptyMessage(1);
//                        NetDataHub.get().saveWifiListAndWhiteApp();
//                    }
//                }

                else if (name.equals("ForbitNeton")) {
                    boolean tmp;
                    if (jsonObject.getString("Value").equals("0")) {
                        tmp = NetDataHub.get().setCtrlMonet(false);
                    } else {
                        tmp = NetDataHub.get().setCtrlMonet(true);
                    }
                    if (tmp) {
                        //handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();
                    }
                } else if (name.equals("ForbitUSBon")) {
                    boolean tmp;
                    if (jsonObject.getString("Value").equals("0")) {
                        tmp = NetDataHub.get().setCtrlUSB(false);
                    } else {
                        tmp = NetDataHub.get().setCtrlUSB(true);
                    }
                    if (tmp) {
                        //handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();
                    }
                } else if (name.equals("ForbitCAMon")) {
                    boolean tmp;
                    if (jsonObject.getString("Value").equals("0")) {
                        tmp = NetDataHub.get().setCtrlCamera(false);
                    } else {
                        tmp = NetDataHub.get().setCtrlCamera(true);
                    }
                    if (tmp) {
                        //handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();
                    }
                } else if (name.equals("UseGpsPos")) {
                    if (jsonObject.getString("Value").equals("0")) {
                        NetDataHub.get().setUseGpsPos(false);
                    } else {
                        NetDataHub.get().setUseGpsPos(true);
                    }
                } else if (name.equals("UseAppStore")) {
                    NetDataHub.isShowAppStore = !jsonObject.getString("Value").equals("0");
                } else if (name.equals("ForbitAP"))//add by gwb;2021.2.20  禁用热点。
                {
                    if (jsonObject.getString("Value").equals("1")) {
                        NetDataHub.get().setForbidAP(true);
                    } else {
                        NetDataHub.get().setForbidAP(false);
                    }
                } else if (name.equals("ZRIPSet")) {
                    if (jsonObject.getString("Value") != null) {
                        NetDataHub.get().setNACAddr(jsonObject.getString("Value"));

                        //handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();del by gwb;
                    } else {
                        NetDataHub.get().setNACAddr("");

                        //handler.sendEmptyMessage(1);
                        //NetDataHub.get().saveWifiListAndWhiteApp();del by gwb;
                    }
                }
            }
            return "1";
        } catch (Exception e) {

            Tell.log("TimerCheck出错" + e.toString());
            return null;
        }

    }

    public String getBackGroundPic() {
        try {
            String rs = HttpRequest.httpGet("http://" + serviceAd + "/TelSafeDesk.php?Action=backgroundpic" +
                    "&Mac=" + DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum, null);

            JSONArray jsonArray = new JSONArray(rs);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            return jsonObject.getString("Name3");

        } catch (Exception e) {
            e.printStackTrace();
            Tell.log("getbackgroundpic" + e.toString());
            return null;
        }


    }


    public String upDataSucceful() {
        try {
            String rs = HttpRequest.httpGet("http://" + serviceAd + "/TelSafeDesk.php?Action=PolicyChangeOK" +
                    "&Mac=" + DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum, null);

            return rs;

        } catch (Exception e) {
            e.printStackTrace();
            Tell.log("PolicyChangeOK" + e.toString());
            return null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public String upHardlist() {
        try {
            String rs = HttpRequest.httpPost("http://" + serviceAd + "/TelSafeDesk.php?Action=hardlist" +
                            "&Mac=" + DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum,
                    null, NetDataHub.get().getHardList().toString());

            //NetDataHub.get().addLog("EMMHardlist---rs:"+rs);

            return rs;

        } catch (Exception e) {
            NetDataHub.get().addLog("EMMHardlist---error:" + e.toString());
            e.printStackTrace();
            Tell.log("hardlist出错" + e.toString());
            return null;
        }
    }

    public String upSoftlist() {
        try {
            String rs = HttpRequest.httpPost("http://" + serviceAd + "/TelSafeDesk.php?Action=softlist" +
                            "&Mac=" + DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum,
                    null, NetDataHub.get().getAppList().toString());
            return rs;

        } catch (Exception e) {
            e.printStackTrace();
            Tell.log("hardlist出错" + e.toString());
            return null;
        }
    }


    JSONArray getWifiList() {
        try {
            String rs = HttpRequest.httpGet("http://" + serviceAd + "/TelSafeDesk.php?Action=wifilist" +
                    "&Mac=" + DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum, null);
            JSONArray jsonArray = new JSONArray(rs);
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            Tell.log("getWifiList出错" + e.toString());
            return null;
        }
    }

    /**
     * 获取系统开机时间(精确到秒)
     *
     * @return
     */
    public static long getBootTime() {
        long ut = SystemClock.elapsedRealtime() / 1000;
        if (ut == 0) {
            ut = 1;
        }
        return ut;
    }

    public boolean GetServerPolicy() {
        try {

        /* del by gwb;2020.9.30  现在先不用了。
        String background = getBackGroundPic();
        if (background != null)
        {
            if (NetDataHub.get().getBackgroundpic().equals(background))
            {
                Tell.log("背景图片重复了，不更新");
            }
            else
            {
                try
                {
                    Tell.log("背景图片下载开始");
                    DownFile.downLoadFromUrl(background, "/mnt/sdcard/bg.jpg");
                    Tell.log("背景图片下载成功");

                    Bitmap bitmap = BitmapFactory.decodeFile("/mnt/sdcard/bg.jpg");
                    activity.setWallpaper(bitmap);
                    NetDataHub.get().setBackgroundpic(background);

                }
                catch (Exception e)
                {
                    Tell.log("背景图片下载失败。。" + e.toString());
                }
            }
        }
        else
        {
            Tell.log("背景图片下载失败");
        }
         */

            //是否控制wifi和App
            String isCtrlWifiAndApp = getIsCtrlWifiAndApp();//一定要先得一下开关，再得具体内容。
            if (isCtrlWifiAndApp != null) {
                //app白名单
                JSONArray whiteList = getWhiteList();
                if (whiteList != null) {
                    Tell.log("心跳：获取白名单成功");
                    NetDataHub.get().setWhiteApp(whiteList);
                    //handler.sendEmptyMessage(1);
                }
                //wifi白名单列表
                JSONArray wifiList = getWifiList();
                if (wifiList != null) {
                    NetDataHub.get().setWifiList(wifiList);
                }
                NetDataHub.get().saveWifiListAndWhiteApp();//add by gwb;2020.10.15  一起保存。

                handler.sendEmptyMessage(1);
            } else {
                NetDataHub.get().addLog("GetServerPolicy----调用getIsCtrlWifiAndApp获取策略返回异常!");
                return false;//add by gwb;2020.9.15 此时可能与服务器不通。
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Tell.log("GetServerPolicy catch--!!!!!!!! Error:" + e.toString());//del by gwb;2020.9.15
            //----add by gwb;2020.9.15
            NetDataHub.get().addLog("GetServerPolicy------执行catch 异常!");
            return false;
            //----end.
        }
        return true;
    }

    public String CheckUpdateVersion(boolean bShowMsg) {
        String szVersion = "";
        try {
            szVersion = getServerApkVersion();
            if (szVersion.length() < 1) {
                if (bShowMsg) {
                    Message message = new Message();
                    message.what = 6;
                    NetCtrlHub.get().handler.sendMessage(message);
                    //Toast.makeText(activity, "服务器没有升级包！", Toast.LENGTH_SHORT).show();
                }
                return "";
            }
            Log.w("EMM-Version", szVersionNum + "|服务器上最新版本:" + szVersion);
            if (szVersion.compareToIgnoreCase(szVersionNum) != 0) {
                NetDataHub.get().addLog("CheckUpdateVersion---当前服务器上版本与本地不同，需要升级：" + szVersion);

                if (!bShowMsg) {
                    Message message = new Message();
                    message.what = 4;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (EMMAccessibilityService.isStart()) {
                            message.what = 11;
                        }
                    }
                    EMMApp.getInstance().shouldUpdate = true;
                    EMMApp.getInstance().servApkVersion = szVersion;
                    message.obj = szVersion;
                    Log.w("EMMA11y", "shouldUpdate = true，message.obj =" + message.obj);
                    NetCtrlHub.get().handler.sendMessage(message);

                }

            } else {
                NetDataHub.get().addLog("CheckUpdateVersion---当前已是最新版本:" + szVersion + "，无需升级.");
                if (bShowMsg) {
                    Message message = new Message();
                    message.what = 5;
                    message.obj = szVersion;
                    NetCtrlHub.get().handler.sendMessage(message);
                }
                szVersion = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return szVersion;
    }

    public void ytStopLocation() {
        try {
            // 停止定位
            if (mapLocations != null) {
                mapLocations.stopLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ytStartLocation() {
        try {
            // 停止定位
            if (mapLocations != null) {
                mapLocations.startLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ytInitLocationFun() {
        try {
            if (!serviceAd.equals("") && mapLocations == null) {
                mapLocations = new AMapLocations(activity, serviceAd);
                mapLocations.initLocation();
                mapLocations.startLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void CheckEquipManage() {
        try {

            //设置成功，之后可能要改成不每次都更新
            devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName componentName = new ComponentName(activity, DeviceReceiver.class);

            NetDataHub.get().addLog("CheckEquipManage----begin-----");
//            UsbManager manager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
//            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//            UsbDevice device = deviceList.get("deviceName");
//
//            Intent it = new Intent();
            //    it.setComponent(componentName);
            //     startService(new Intent(this, TaskThink.class));
            //      activity.startService(it); e.printStackTrace();
//             it.setClassName("com.android.systemui",
//                    "com.android.systemui.usb.UsbPermissionActivity");
            UsbSetting usbSetting = new UsbSetting();
//            devicePolicyManager.setDeviceOwner(componentName,0);


            if (devicePolicyManager.isAdminActive(componentName)) {

                if (isCtrlUSB) {
                    NetDataHub.get().addLog("CheckEquipManage----开始USB禁用.");

                    //   UsbPermissionManager usbPermissionManager = new UsbPermissionManager();
                    //    usbPermissionManager.requestPermissionDialog();
                    //     UsbSetting.AllowUseUsb.DisallowUseUsb

                    UsbSetting.DisallowUseUsb();


//                try {
//
//                    Runtime.getRuntime().exec("setprop persist.sys.usb.config none"); //none  mtp  adb
//                    NetDataHub.get().addLog("----------------------------------1");
//                } catch (IOException e) {
//                 //   e.printStackTrace();
//                    NetDataHub.get().addLog("CheckEquipManage----catch error-----1!!");
//                }


//              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                   NetDataHub.get().addLog("11111111111111111111111111111111");
//                    devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);
//                }
                    NetDataHub.get().addLog("----------------------------------2");
                } else {
                    NetDataHub.get().addLog("CheckEquipManage----取消USB禁用!!");
                    UsbSetting.AllowUseUsb();
//                try {
//
//                    Runtime.getRuntime().exec("setprop persist.sys.usb.config mtp,adb");
//                    NetDataHub.get().addLog("----------------------------------3");
//                } catch (IOException e) {
//                  //  e.printStackTrace();
//                    NetDataHub.get().addLog("CheckEquipManage----catch error-----2!!");
//                }


//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);
//                }
                }
            } else {
                NetDataHub.get().addLog("CheckEquipManage----没有设备管理权限!");
            }
        } catch (SecurityException e) {
            NetDataHub.get().addLog("CheckEquipManage----catch error!!");


            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            NetDataHub.get().addLog(sw.toString());
            NetDataHub.get().addLog("CheckEquipManage----catch error22!!");

        }
    }

    private void CheckCenterServOK() {
        String szServerIP = "";
        try {

            String szUrlIP = NetCtrlHub.get().getServiceAd();
            //szUrlIP = "192.168.1.51";
            if (szUrlIP.indexOf(":") > 0)
                szServerIP = szUrlIP.substring(0, szUrlIP.indexOf(":"));
            else
                szServerIP = szUrlIP;

            if (!szServerIP.equals("")) {

                if (logonSocketClient == null) {
                    logonSocketClient = new CLogonSock(InetAddress.getByName(szServerIP), EMMApp.REMOTE_PORT);
                    logonSocketClient.start();
                    logonSocketClient.init(activity, handler);  //让CLogonSock也能发送桌面提示
                } else {
                    logonSocketClient.setRemoteHost(InetAddress.getByName(szServerIP));
                    EMMApp.getInstance().centerServerIp = szServerIP;
                    logonSocketClient.SendTimerCmdToServ();
                }
            } else
                NetDataHub.get().addLog("CheckCenterServOK------中心服务器IP为空！！");
        } catch (Exception e) {
            NetDataHub.get().addLog("CheckCenterServOK------catch Error！！szServerIP:" + szServerIP);
            e.printStackTrace();
        }
    }

    private void CheckGPSPosition() {
        try {
            if (NetDataHub.m_bUseGpsPos) {
                //if (true) {
                NetCtrlHub.get().ytInitLocationFun();
                NetCtrlHub.get().ytStartLocation();
                if (szUploadGPS_Url.length() > 0) {
                    String rs = HttpRequest.httpGet(szUploadGPS_Url, null);//必须放到线程中去执行才会提交成功，新的android这样。
                    szUploadGPS_Url = "";
                }
            } else {
                NetCtrlHub.get().ytStopLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean TimerCheckMain(int k) {
        // Tell.log("TimerCheckMain----->心跳开始");
        //--------add by gwb;2020.9.14
        Calendar calendar = Calendar.getInstance();
        NetDataHub.get().addLog("\nTimerCheckMain---sys:" + Build.VERSION.RELEASE + "------心跳开始--【" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "】---------");
        //------------------------------------------

        //if (!NetCtrlHub.get().getServiceAd().equals("") && 1==0)
        if (!NetCtrlHub.get().getServiceAd().equals("")) {
            Tell.log("心跳：地址存在");

            CheckCenterServOK();//add by gwb;2021.9.14
            try {
                JSONArray timeRs = null;
                try {
                    if (!NetDataHub.get().isProtectSetting()) {
                        if (NetDataHub.get().openCount++ > 19)   //简单处理下，取消系统设置保护后，这里每30s一次的循环走到第20次就会停止
                            NetDataHub.get().setProtectSetting(true);
                    }

                    long bootTime1 = getBootTime(); // 获取系统开机时间(精确到秒)
                    if (bootTime1 > 180) {
                        NetDataHub.get().bOpenMachineRightNowNoWifiControl = false;
                    }

                    //add by gwb;2021.11.23  必须放在最前面，否则下面的可能被阻断。
                    //准入地址不为空
                    if (NACAddr != null && !NACAddr.equals("") && !NACAddr.equals("0") && NACAddr.length() > 3
                            && !NACAddr.equals("0.0.0.0") && !NACAddr.equals("255.255.255.255")) {
                        getNACCheck();
                        NetDataHub.get().addLog("TimerCheckMain---准入地址：" + NACAddr);
                    } else {
                        NetDataHub.get().addLog("TimerCheckMain---准入地址为空：" + NACAddr);
                    }

                    //----------------End.

                    timeRs = getTimerCheck();
                    if (timeRs == null) {
                        NetDataHub.get().addLog("TimerCheckMain----getTimerCheck 失败，直接返回.");

                        /*  del by gwb;2021.2.8  现在不处理了，还得处理恢复控制。直接控死。
                        if(bootTime1<120){
                            NetDataHub.get().bOpenMachineRightNowNoWifiControl = true;
                            NetDataHub.get().addLog("TimerCheckMain---刚开机，网也不通，取消禁用wifi---bootTime1秒:"+bootTime1);
                            if(g_bUseHuaWeiMDM)
                            {
                                NetDataHub.get().clealHuaWeiWifiControl();
                            }
                        }
                        */
                        CheckGPSPosition();//add by gwb;2020.12.9 当没有wifi网，与服务器不通时也要检查GPS位置，但是也提交不了啊？？？？？？？？
                        return false;
                    }

                    //  NetDataHub.get().addLog("EMM---Time---k="+k);
                    //  Tell.toast("k="+k, g_context);
                    Log.d("EMMTimerCheckMain", "----k = " + k);
                    if (k % 4 == 2) {
                        NetCtrlHub.get().CheckUpdateVersion(false); //add by sy 2021.12.24 定期检查版本更新时间缩短
                    }


                    //------add by gwb;2020.9.14  先这样处理，这里最好是通过MD5判断策略有没有变化。
                    if (k == 1 || k % 120 == 0 || NetCtrlHub.bHaveGetPolicy == false) {
                        Tell.log("TimerCheckMain-----强制获取策略。");
                        if (GetServerPolicy()) {

                            // NetCtrlHub.get().CheckUpdateVersion(false);//检查升有版本
                            NetCtrlHub.bHaveGetPolicy = true;
                            NetDataHub.get().addLog("TimerCheckMain----第一次提交资产信息---begin.!");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                NetCtrlHub.get().upHardlist();
                            }
                            NetCtrlHub.get().upSoftlist();
                            NetDataHub.get().addLog("TimerCheckMain----第一次提交资产信息---end.!");
                        } else {
                            NetDataHub.get().addLog("TimerCheckMain----GetServerPolicy 失败，直接返回.");
                            return false;
                        }
                    }

                    if (k > 1)//在1的时候处理，因为太快的话NetDataHub.get()还没有初始化。
                    {
                        if (NetDataHub.m_ManagerLogon) {
                            NetDataHub.m_ManagerLogon = false;

                            NetCtrlHub.get().CheckUpdateVersion(false);//检查升有版本

                            NetDataHub.get().addLog("TimerCheckMain----登陆管理员设置----强制上传资产---begin.!");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                NetCtrlHub.get().upHardlist();
                            }
                            NetCtrlHub.get().upSoftlist();
                            NetDataHub.get().addLog("TimerCheckMain---登陆管理员设置---强制上传资产---end.!");
                        }

                        //add by fsy 2022.4.14 防止之前没获取到，定时再获取
                        DeviceInfoUtil.initDeviceInfo();

//Test                        String pkg = "com.youdao.dict";
                        //应用的主activity类
//                        String cls = "com.youdao.dict.activity.DictSplashActivity";

//                        ComponentName componet = new ComponentName(pkg, cls);
//                        Intent intent = new Intent();
//                        intent.setComponent(componet);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        g_context.startActivity(intent);
//
                    }
                    //-------------End.
                } catch (Exception e) {

                }
                if (timeRs != null) {
                    Tell.log("TimerCheckMain--------心跳：获取心跳信息成功");
                    for (int i = 0; i < timeRs.length(); i++) {
                        JSONObject jsonObject = timeRs.getJSONObject(i);
                        String name = jsonObject.getString("Name");

                        if (name.equals("PolicyChange")) {
                            String value = jsonObject.getString("Value");
                            if (value.equals("1")) {
                                NetDataHub.get().setPolicyChange("1");
                                /*
                                GetServerPolicy();
                                //策略更新成功
                                upDataSucceful();
                                */
                                //---add by gwb;2020.9.15 策略更新成功了，就更新成功状态。
                                if (GetServerPolicy())
                                    upDataSucceful();
                                //----end.
                            }
                        } else {
                            //其他事项
                            NetDataHub.get().setPolicyChange("0");

                            //禁用wifi
                            //getIsCtrlWifiAndApp();  del by gwb;2020.9.15  策略这边理论上不需要再拿了。
                        }
                    }
                }
                /* del by gwb;2021.11.23  在这之前getTimerCheck可能被准入阻断，所以走不到这边
                //准入

                //准入地址不为空
                if (NACAddr != null && !NACAddr.equals("") && !NACAddr.equals("0") && NACAddr.length() > 3
                && !NACAddr.equals("0.0.0.0") && !NACAddr.equals("255.255.255.255"))
                {
                    NACStatuc = getNACCheck();
                }*/

                if (isCtrlWifi) {
                    if (!g_bUseHuaWeiMDM && NetDataHub.get().isUseWifiWhite()) {
                        if (!EMMFloatWindowService.isStart())  //服务未开启且非华为
                            new Thread(new Runnable() { // 匿名类的Runnable接口
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activity, EMMFloatWindowService.class);
                                    activity.startService(intent);
                                }
                            }).start();
                    }
                }
                WifiHub.wifiThink(activity);//add by gwb;2020.9.22

                //    EMMFloatWindowService.getInstance().getWiFiNow(activity);
                WifiHub.closeWifiAp(activity);//add by gwb;2021.2.20  禁用热点。

                //------------add by gwb;2020.12.2 定位处理----------------
                CheckGPSPosition();
                //-------------------------------------

                CheckEquipManage();//add by gwb;2021.7.15
                //-----------------------------------

                //app白名单限制
                //appUtils au2 = new appUtils(activity);  //del by gwb;2020.9.22
                //au2.appControl();

                //是否控制wifi和App
                //String isCtrlWifiAndApp = getIsCtrlWifiAndApp();  del by gwb;2020.9.15  这边策略理论上就不需要再获取一次了。
                //if (isCtrlWifiAndApp != null)
                NetDataHub.get().addLog("EMMMain-----androidv:" + androidv);//测试
                if (androidv >= 222) {///add by gwb;2020.9.24  发现在安卓4的版本上面，调用禁用蓝牙这些会程序报错。 先不用了。
                    //if(1==1){
                    //app白名单限制
                    appUtils au = new appUtils(activity); //add by gwb;加到下面来
                    au.appControl();

                    //设置成功，之后可能要改成不每次都更新
                    devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    ComponentName componentName = new ComponentName(activity, DeviceReceiver.class);

                    if (devicePolicyManager.isAdminActive(componentName)) {
                        NetDataHub.get().addLog("TimerCheckMain-----已获得设备管理权限!");//add by gwb;2020.9.15


                        /* del by gwb;2020.9.15  先不用了。
                        //移动网络
                        if (isCtrlMonet)
                        {
                            devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
                        }
                        else
                        {
                            try
                            {
                                devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
                            }
                            catch (SecurityException e)
                            {

                            }
                        }

                         */


                        //蓝牙
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                if (isCtrlBlueTooth) {

                                    devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_BLUETOOTH);

                                    Tell.log("关闭蓝牙");
                                } else {
                                    devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_BLUETOOTH);
                                    Tell.log("开启蓝牙");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //摄像头
                        try {
                            if (isCtrlCamera) {
                                devicePolicyManager.setCameraDisabled(componentName, true);
                                Tell.log("关闭相机");
                            } else {

                                devicePolicyManager.setCameraDisabled(componentName, false);
                                Tell.log("开启相机");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /* del by gwb;2020.9.15
                        //USB设备
                        if (isCtrlUSB)
                        {
                            devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);
                        }
                        else
                        {
                            try
                            {
                                devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);
                            }
                            catch (SecurityException e)
                            {

                            }
                        }*/
                    } else {
                        //Toast.makeText(activity, "您尚未取得设备管理器权限，部分功能可能无法正常使用", Toast.LENGTH_SHORT).show();
                        NetDataHub.get().addLog("您尚未取得设备管理器权限!");//add by gwb;2020.9.15
                    }
                }

                NetDataHub.get().addAllLog();//策略加入Log
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;

    }


    public void timerThink() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;

                while (true) {
                    i++;//add by gwb;2020.9.14 控制第一次必须从服务器获取策略，因为如果手机第一次安装，管理机不设置是得不到策略的。

                    TimerCheckMain(i);

                    if (i == 2 * 60 * 3)//add by gwb;3小时一个轮回。
                        i = 0;

                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }


}

class UsbSetting {
    final private static String TAG = "UsbSetting";

    public static void AllowUseUsb() {    //允许使用USB
        Command.command("setprop persist.sys.usb.config mtp,adb");
        NetDataHub.get().addLog("CheckEquipManage----允许使用USB!");
    }

    public static void DisallowUseUsb() {   //禁止使用USB
        Command.command("setprop persist.sys.usb.config none");
        NetDataHub.get().addLog("CheckEquipManage----禁止使用USB!");
    }
}

class Command {
    final private static String TAG = "Command";

    public static void command(String com) {
        try {
            Log.i(TAG, "Command : " + com);
            Runtime.getRuntime().exec(com);
            //          NetDataHub.get().addLog("CheckEquipManage---111111111111111111111");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            NetDataHub.get().addLog("CheckEquipManage---" + e.toString());
            e.printStackTrace();
        }
    }
}