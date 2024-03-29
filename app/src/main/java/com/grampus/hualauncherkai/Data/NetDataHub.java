package com.grampus.hualauncherkai.Data;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.UserManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Receiver.WifiHub;
import com.grampus.hualauncherkai.Tools.DeviceReceiver;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.Tools.Tell;
import com.grampus.hualauncherkai.Tools.appUtils;
import com.huawei.android.app.admin.DeviceControlManager;
import com.huawei.android.app.admin.DeviceWifiPolicyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.grampus.hualauncherkai.Data.NetCtrlHub.NACUrl;
import static com.grampus.hualauncherkai.UI.MainActivity.g_bUseHuaWeiMDM;

/**
 * Created by Grampus on 2017/5/10.
 */

public class NetDataHub
{
    private static NetDataHub netDataHub;

    static public NetDataHub get()
    {
        return netDataHub;
    }

    static public NetDataHub init(Activity context)
    {

        if (netDataHub == null)
        {
            netDataHub = new NetDataHub(context);
            netDataHub.readWifiListAndWhiteApp();
            lastWhiteApp = whiteApp;//add by gwb;2021.3.26
            lastShowAppStore = isShowAppStore;//add by gwb;2021.3.26
            lastIsCtrlApp = isCtrlApp;//add by gwb;2021.3.26
        }
        return netDataHub;

    }

    private NetDataHub(Activity context)
    {
        this.context = context;
        whiteApp = new ArrayList<>();
        lastWhiteApp = new ArrayList<>();
        allLog = new StringBuffer();
        appList = new JSONArray();
        isCtrlWifi = false;
        isCtrlApp = false;

    }

    Activity context;


    StringBuffer allLog;
    String policyChange = "0";
    public boolean bOpenMachineRightNowNoWifiControl = false;//add by gwb;




    /**
     * appList格式
     * SoftName
     * VersionNum
     * InstallDate
     * Publisher
     */
    public static JSONArray appList;

    public static JSONArray wifiList;

    /**
     * whiteApp单纯的App白名单列表
     * 里面只有白名单App的名字
     */
    public static List<String> whiteApp;
    public static List<String> lastWhiteApp;//add by gwb;2021.3.25

    public boolean getCanReflashDesk() {
        return canReflashDesk;
    }

    public void setCanReflashDesk(boolean canReflashDesk) {
        this.canReflashDesk = canReflashDesk;
    }

    public boolean isUseWifiWhite() {
        return useWifiWhite;
    }

    public void setUseWifiWhite(boolean useWifiWhite) {
        this.useWifiWhite = useWifiWhite;
    }

    public boolean isProtectSetting() {
        return protectSetting;
    }

    public int openCount= 0;
    public void setProtectSetting(boolean protectSetting) {
        this.protectSetting = protectSetting;
        openCount = 0;
    }

    private boolean canReflashDesk = false;    //add by fsy;2021.11.25
    private boolean useWifiWhite = true;  //add by fsy;2021.11.18
    private boolean protectSetting = true; //默认要禁用设置
    JSONObject hardList;
    public static boolean isCtrlWifi = false;
    public static boolean isCtrlApp = false;
    public static boolean lastIsCtrlApp = false;//add by gwb;2021.3.25

    //public static boolean isCtrlInfrared;
    public static boolean isCtrlMonet = false;
    public static boolean isCtrlBlueTooth= false;
    public static boolean isCtrlCamera= false;
    public static boolean isCtrlUSB= false;
    public static String NACAddr = "";

    public static boolean isShowAppStore = true;
    public static boolean lastShowAppStore = true;//add by gwb;2021.3.25

    public static boolean m_bUseGpsPos = false;
    public static boolean m_bForbitAP = false;
    public int  g_nEquipID = 0;//add by gwb;2021.9.14



    public String backgroundpic;
    public static boolean m_ManagerLogon = false;//add by gwb;2020.9.16


    public void saveWifiListAndWhiteApp()
    {
        Save.fileSave(isCtrlWifi, context, "isCtrlWifi");
        Save.fileSave(isCtrlApp, context, "isCtrlApp");

        //Save.fileSave(isCtrlInfrared, context, "isCtrlInfrared");
        Save.fileSave(isCtrlMonet, context, "isCtrlMonet");
        Save.fileSave(isCtrlBlueTooth, context, "isCtrlBlueTooth");
        Save.fileSave(isCtrlCamera, context, "isCtrlCamera");
        Save.fileSave(isCtrlUSB, context, "isCtrlUSB");

        Save.fileSave(NACAddr, context, "NACAddr");

        Save.fileSave(m_bUseGpsPos, context, "isUseGpsPos");//add by gwb;2021.2.20
        Save.fileSave(m_bForbitAP, context, "isForbitAP");//add by gwb;2021.2.20
        Save.fileSave(isShowAppStore, context, "isUseAppStore");//add by gwb;2021.3.25
        if (wifiList != null)
        {
            Save.fileSave(wifiList.toString(), context, "wifiList");
        }

        Save.fileSave(whiteApp, context, "whiteApp");


    }

    public void readWifiListAndWhiteApp()
    {
        //addLog("版本更新:----2021.2.20-----增加禁用热点.");

        addLog("开机准备读取本地WIFI与App白名单-----begin.");

        boolean bShouldWifiControl = false;
        Object isW = Save.readFile(context, "isCtrlWifi");
        if (isW != null)
        {
            isCtrlWifi = (boolean) isW;
            //addLog("wifi开关读取成功" + isCtrlWifi);
            addLog("WIFI白名单开关读取成功:" + isCtrlWifi);
            bShouldWifiControl = isCtrlWifi;
        }
        else
            bShouldWifiControl = false;

        Object isA = Save.readFile(context, "isCtrlApp");
        if (isA != null)
        {
            //Tell.log("是否控制App读取成功" + isCtrlApp);
            Tell.log("App白名单开关读取成功:" + isCtrlApp);
            isCtrlApp = (boolean) isA;
            addLog("App白名单开关读取成功:" + isCtrlApp);
        }

//        Object isI = Save.readFile(context, "isCtrlInfrared");
//        if (isI != null)
//        {
//            Tell.log("红外设备开关读取成功:" + isCtrlInfrared);
//            isCtrlInfrared = (boolean) isA;
//            addLog("红外设备开关读取成功:" + isCtrlInfrared);
//        }

        Object isM = Save.readFile(context, "isCtrlMonet");
        if (isM != null)
        {
            Tell.log("移动网络开关读取成功:" + isCtrlMonet);
            isCtrlBlueTooth = (boolean) isM;
            addLog("移动网络开关读取成功:" + isCtrlMonet);
        }

        Object isB = Save.readFile(context, "isCtrlBlueTooth");
        if (isB != null)
        {
            Tell.log("蓝牙设备开关读取成功:" + isCtrlBlueTooth);
            isCtrlBlueTooth = (boolean) isB;
            addLog("蓝牙设备开关读取成功:" + isCtrlBlueTooth);
        }

        Object isC = Save.readFile(context, "isCtrlCamera");
        if (isC != null)
        {
            Tell.log("相机设备开关读取成功:" + isCtrlCamera);
            isCtrlCamera = (boolean) isC;
            addLog("相机设备开关读取成功:" + isCtrlCamera);
        }

        Object isU = Save.readFile(context, "isCtrlUSB");
        if (isU != null)
        {
            Tell.log("USB设备开关读取成功:" + isCtrlUSB);
            isCtrlUSB = (boolean) isU;
            addLog("USB设备开关读取成功:" + isCtrlUSB);
        }

        Object isForbitAPValue = Save.readFile(context, "isForbitAP");//add by gwb;2021.2.20
        if (isForbitAPValue != null)
        {
            m_bForbitAP = (boolean) isForbitAPValue;
            addLog("禁用热点:" + m_bForbitAP);
        }

        Object isUseAppStoreObj = Save.readFile(context, "isUseAppStore");//add by gwb;2021.2.20
        if (isUseAppStoreObj != null)
        {
            isShowAppStore = (boolean) isUseAppStoreObj;
            addLog("显示应用商店:" + isShowAppStore);
        }

        Object NACAddrObj = Save.readFile(context, "NACAddr");  //add by fsy 2021.12.20
        if (NACAddrObj != null)
        {
            NACAddr = (String) NACAddrObj;
            AndroidPolicy.NACAddr0 = NACAddr;
            addLog("设置准入IP:" + NACAddr);
            Log.w("EMMNET", "==设置准入IP = "+NACAddr);
        }


        String wifiListString = (String) Save.readFile(context, "wifiList");
        if (wifiListString != null)
        {
            try
            {
                wifiList = new JSONArray(wifiListString);
                //addLog("本地WIFI白名单读取成功");

                //add by gwb;2020.9.14
                if(wifiListString.length()<1)
                    bShouldWifiControl = false;

                //-------add by gwb;2021.2.8  开机的时候也得控制一下，防止开机改wifi
                if(bShouldWifiControl){
                    //wifi白名单列表
                    if (wifiList != null)
                    {
                        addLog("readWifiListAndWhiteApp---APP刚运行--需要控制wifi.");//过滤开关没有开的时候不进行wifi禁用。
                        setWifiList(wifiList);
                        WifiHub.wifiThink(context);
                    }
                }
                //----------------End.

                for (int i = 0; i < wifiList.length(); i++)
                {
                    JSONObject one = wifiList.getJSONObject(i);
                    addLog(one.getString("Name1") + " " + one.getString("Name2"));
                }
                //---------------------end.
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        //----add by gwb;2020.10.15  华为手机处理
        if (g_bUseHuaWeiMDM && bShouldWifiControl == false ){
            addLog("readWifiListAndWhiteApp----进程首次启动--华为手机不需要控制wifi.");//过滤开关没有开的时候不进行wifi禁用。
            clealHuaWeiWifiControl();
        }
        //-------------------end.

        whiteApp = (List<String>) Save.readFile(context, "whiteApp", allLog);
        if (whiteApp == null)
        {
            addLog("本地读取App白名单失败");
        }
        else
        {
            System.out.println(whiteApp);

            StringBuffer sb = new StringBuffer();
            for (String one : whiteApp)
            {
                sb.append(one + "\n");
            }
            addLog("本地读取App白名单列表:\n" + whiteApp.size() + "项\n" + sb.toString());
        }
    }
    public boolean CheckAppHaveChange()//add by gwb;2021.3.26
    {
        try {
            //Log.w("EMMMain","lastWhiteApp:"+lastWhiteApp+"|whiteApp:"+whiteApp);
            if (lastWhiteApp.equals(whiteApp) && (lastShowAppStore == isShowAppStore) && (lastIsCtrlApp == isCtrlApp)) {
                Log.w("EMMMain","CheckAppHaveChange-------不需要刷新桌面");
                return false;
            }
            lastWhiteApp = whiteApp;
            lastShowAppStore = isShowAppStore;
            lastIsCtrlApp = isCtrlApp;
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    /**
     * 2020.04.26 未来修改
     * 重置所有管控的方法
     * 返回值为0，1
     * 0为正常重置
     * 1为重置本地文件时发生错误
     *
     * @param activity
     * @return
     */
    public int resetAllConfig(Activity activity)
    {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(activity, DeviceReceiver.class);

        try
        {
            Save.fileSave(false, context, "isCtrlWifi");
            Save.fileSave(false, context, "isCtrlApp");

            Save.fileSave(false, context, "isCtrlMonet");
            Save.fileSave(false, context, "isCtrlBlueTooth");
            Save.fileSave(false, context, "isCtrlCamera");
            Save.fileSave(false, context, "isCtrlUSB");
            Save.fileSave(false, context, "isForbitAP");

            Save.fileSave("", context, "NACAddr");
            Save.fileSave("1", context, "service_ad");

            isCtrlWifi = false;
            isCtrlApp = false;
            isCtrlMonet = false;
            isCtrlBlueTooth = false;
            isCtrlCamera = false;
            isCtrlUSB = false;
            NACAddr = "";
            m_bUseGpsPos = false;
            m_bForbitAP = false;

            NetCtrlHub.get().setServiceAd("");

            //app白名单限制
            appUtils au = new appUtils(activity);
            au.appControl();
/* del by gwb;2020.9.14
            //移动网络
            try
            {
                devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
            }
            catch (SecurityException e)
            {

            }

 */

            if (Build.VERSION.SDK_INT > 3) {//add by gwb;2020.9.28  下面的就不让处理了，可能会报错。
                return 0;
            }





            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_BLUETOOTH);
                }
            }
            catch (SecurityException e)
            {

            }

            try
            {
                devicePolicyManager.setCameraDisabled(componentName, false);
            }
            catch (SecurityException e)
            {

            }

            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);
                }
            }
            catch (SecurityException e)
            {

            }
        }
        catch (Exception e)
        {
            return 1;
        }
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
            }
        }
        catch (SecurityException e)
        {

        }

        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_BLUETOOTH);
            }
        }
        catch (SecurityException e)
        {

        }

        try
        {
            devicePolicyManager.setCameraDisabled(componentName, false);
        }
        catch (SecurityException e)
        {

        }

        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);
            }
        }
        catch (SecurityException e)
        {

        }
        return 0;
    }

    public String getBackgroundpic()
    {
        if (backgroundpic == null)
        {
            return "null";
        }
        return backgroundpic;
    }

    public boolean isCtrlApp()
    {
        return isCtrlApp;
    }

    public boolean setCtrlApp(boolean ctrlApp)
    {
        if (isCtrlApp == ctrlApp)
        {
            return false;
        }
        else
        {
            isCtrlApp = ctrlApp;
            return true;
        }
    }

    public void setBackgroundpic(String backgroundpic)
    {
        this.backgroundpic = backgroundpic;
    }

    public String getPolicyChange()
    {
        return policyChange;
    }

    public void setPolicyChange(String policyChange)
    {
        this.policyChange = policyChange;
    }

    public boolean isCtrlWifi()
    {
        return isCtrlWifi;
    }

    public boolean setCtrlWifi(boolean ctrlWifi)
    {
        if (ctrlWifi == isCtrlWifi)
        {
            //return false;
            return ctrlWifi;
        }
        else
        {
            isCtrlWifi = ctrlWifi;
            //saveWifiListAndWhiteApp();
            //return true;
            return ctrlWifi;
        }


    }

//    public boolean setCtrlInfrared(boolean ctrlInfrared)
//    {
//        if (ctrlInfrared == isCtrlInfrared)
//        {
//            return false;
//        }
//        else
//        {
//            isCtrlInfrared = ctrlInfrared;
//            saveWifiListAndWhiteApp();
//            return true;
//        }
//    }

    public boolean setCtrlMonet(boolean ctrlMonet)
    {
        if (ctrlMonet == isCtrlMonet)
        {
            return false;
        }
        else
        {
            isCtrlMonet = ctrlMonet;
            //saveWifiListAndWhiteApp();
            return true;
        }
    }

    public boolean setCtrlBlueTooth(boolean ctrlBlueTooth)
    {
        if (ctrlBlueTooth == isCtrlBlueTooth)
        {
            return false;
        }
        else
        {
            isCtrlBlueTooth = ctrlBlueTooth;
            //saveWifiListAndWhiteApp();
            return true;
        }
    }

    public boolean setCtrlCamera(boolean ctrlCamera)
    {
        if (ctrlCamera == isCtrlCamera)
        {
            return false;
        }
        else
        {
            isCtrlCamera = ctrlCamera;
            //saveWifiListAndWhiteApp();
            return true;
        }
    }
    public void setUseGpsPos(boolean bUseGpsPos)
    {
        m_bUseGpsPos = bUseGpsPos;
    }
    public void setForbidAP(boolean bForbitAP)
    {
        m_bForbitAP = bForbitAP;
    }

    public boolean setCtrlUSB(boolean ctrlUSB)
    {
        if (ctrlUSB == isCtrlUSB)
        {
            return false;
        }
        else
        {
            isCtrlUSB = ctrlUSB;
            //saveWifiListAndWhiteApp();
            return true;
        }
    }

    public boolean setNACAddr(String addr)
    {
        if (addr.equals(""))
        {
            return false;
        }
        else
        {
            NACAddr = addr;
            //saveWifiListAndWhiteApp();
            return true;
        }
    }
    public void clearHuaWeiDefaultDesktop(Context conText){
        if (!g_bUseHuaWeiMDM)
            return ;

        try {

            ComponentName component  = new ComponentName(conText, DeviceReceiver.class);

            DeviceControlManager dev = new DeviceControlManager();

            dev.clearDefaultLauncher(component);

            NetDataHub.get().addLog("clearHuaWeiDefaultDesktop-----clearDefaultLauncher----调用成功");

        }
        catch(Exception e){

            e.printStackTrace();

            //System.out.println("setHuaWeiDesktop--catch error---getmessage:"+e.getMessage() + "tostring:" + e.toString());

            NetDataHub.get().addLog("clearDefaultLauncher-----catch error---getmessage:"+e.getMessage() + "tostring:" + e.toString());
        }

    }
    public void setHuaWeiDesktop(Context conText)
    {
        if (!g_bUseHuaWeiMDM)
             return ;

        try {

            //DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) conText.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName componet  = new ComponentName(conText, DeviceReceiver.class);

            DeviceControlManager dev = new DeviceControlManager();

            dev.setDefaultLauncher(componet,"com.grampus.hualauncherkai","com.grampus.hualauncherkai.UI.MainActivity");

            NetDataHub.get().addLog("setHuaWeiDesktop-----setDefaultLauncher----调用成功");

            //--------------------------------------------------

            //-----------------------
            /*DeviceWifiPolicyManager wifiManager = new DeviceWifiPolicyManager();
            ArrayList<String> tmp2 = new ArrayList<String>();;
            tmp2.add("test123");
            boolean bRet = wifiManager.addSSIDToWhiteList(componet, tmp2);
            NetDataHub.get().addLog("测试函数--华为--addSSIDToWhiteList添加到---bRet:" + bRet + "   wifiList:" + tmp2.toString());

            ArrayList<String> tmp3 = wifiManager.getSSIDWhiteList(componet);
            if(tmp3 != null)
                NetDataHub.get().addLog("测试函数--华为--getSSIDWhiteList--获取当前列表:" + tmp3.toString());
             */
            //-----------end.

        }
        catch(Exception e){

            e.printStackTrace();
            g_bUseHuaWeiMDM = false;    //add by fsy 2021.12.22设置华为桌面失败，可能是未认证，取消华为模式。

            addLog("setHuaWeiDesktop-----failed-----强制使用非华为策略----");
            addLog("setHuaWeiDesktop-----catch error---getmessage:"+e.getMessage());
        }

    }
    public void addLog(String data)
    {
        if (allLog != null)
        {
            allLog.append("\n" + data);
            Tell.log("addLog---" + data);
        }
    }
    public static void addLog1(String data)
    {
        if(EMMApp.getInstance().canSendLog){
            data = "\n"+data;
            try {
                Log.w("EMMA11y",data);
                NetCtrlHub.get().getLogonSocketClient().SendTelLogToServ(data);
//                NetDataHub.get().addLog(data);
            }catch(Exception e)
            {
                Log.e("EMMA11y","addLog1 e:"+e.toString());

            }
        }

    }

    public void deleteAllLog()//add by gwb;2020.10.12
    {
        allLog.setLength(0);
    }

    void addAllLog()
    {
        Calendar calendar = Calendar.getInstance();
        allLog.append("------【" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "】---------");

        if (policyChange.equals("1"))
        {
            allLog.append("\n\n\n 策略发生变化了--------policyChange=1");
            allLog.append("\n是否进行wifi白名单监控:").append(isCtrlWifi);
            allLog.append("\n是否进行APP白名单监控:").append(isCtrlApp);
            allLog.append("\n白名单APP：");

            for (String one : whiteApp)
            {
                allLog.append("\n").append(one);
            }
            allLog.append("\n白名单WIFI:");
            for (int i = 0; i < wifiList.length(); i++)
            {
                try
                {
                    JSONObject one = wifiList.getJSONObject(i);
                    allLog.append("\n").append(one.getString("Name1")).append(" ").append(one.getString("Name2"));
                }
                catch (JSONException e)
                {
                    allLog.append("\n获取WIFI白名单日志失败");
                }
            }

        }
        else if (policyChange.equals("0"))
        {
            allLog.append("\n policyChange=0");
            allLog.append("\n是否进行wifi白名单监控:").append(isCtrlWifi);
            allLog.append("\n是否进行APP白名单监控:").append(isCtrlApp);
        }
        allLog.append("\nMac:").append(EMMApp.getInstance().macAddr)
                .append("\nDiskNum:").append(EMMApp.getInstance().diskNum)
                .append("\nZR-URL:").append(NACUrl)
                .append("\n显示GPS位置信息:").append(m_bUseGpsPos)
                .append("\n禁用热点:").append(m_bForbitAP)
                .append("\n显示应用商店:").append(isShowAppStore)
                .append("\n  \n  ");

        /*
        if (allLog.length() > 10000)
        {
            allLog = allLog.delete(0, allLog.length() - 1000);
        }*/
        if (allLog.length() > 10000)//modify by gwb;2020.9.14
        {
            allLog = allLog.delete(0, allLog.length() - 10000);
        }


    }

    public String getAllLog()
    {
        return allLog.toString();
    }


    public void clealHuaWeiWifiControl()//add by gwb;
    {
        if (!g_bUseHuaWeiMDM)
            return ;

        try {
            DeviceWifiPolicyManager wifiManager = new DeviceWifiPolicyManager();
            //ComponentName componet = new ComponentName("com.grampus.hualauncherkai", "com.grampus.hualauncherkai.UI.LoginSetting");
            ComponentName componet = new ComponentName(context, DeviceReceiver.class);//用这个就行，用上面的就不是成功。

            ArrayList<String> tmp = wifiManager.getSSIDWhiteList(componet);
            if(tmp != null) {

                boolean bRet = wifiManager.removeSSIDFromWhiteList(componet,tmp);
                NetDataHub.get().addLog("clealHuaWeiWifiControl--华为--取消wifi控制---bRet:" + bRet + "   wifiList:" + tmp.toString());

            }
            else{
                NetDataHub.get().addLog("clealHuaWeiWifiControl---华为--取消wifi控制---getSSIDWhiteList获取列表为空，无须操作.");
            }

        } catch (Exception e) {
            NetDataHub.get().addLog("clealHuaWeiWifiControl---华为---取消wifi控制--catch error---getmessage:" + e.getMessage() + "tostring:" + e.toString());
            e.printStackTrace();
        }
    }
    public void setHuaWeiWifiControl(JSONArray wifiList)
    {
        if (!g_bUseHuaWeiMDM)
            return ;
        try {
            DeviceWifiPolicyManager wifiManager = new DeviceWifiPolicyManager();
            //ComponentName componet = new ComponentName("com.grampus.hualauncherkai", "com.grampus.hualauncherkai.UI.LoginSetting");
            ComponentName componet = new ComponentName(context, DeviceReceiver.class);//用这个就行，用上面的就不是成功。



            ArrayList<String> newArray = new ArrayList<String>();
            ArrayList<String> tmp = wifiManager.getSSIDWhiteList(componet);
            if (tmp != null) {
                for (int i = 0; i < tmp.size(); i++) {
                    String szwifiName = tmp.get(i);
                    boolean bFind = false;
                    for (int k = 0; k < wifiList.length(); k++) {
                        JSONObject jsonObject = wifiList.getJSONObject(k);
                        String newName = jsonObject.getString("Name1");
                        if (newName.compareToIgnoreCase(szwifiName) == 0) {
                            bFind = true;
                            break;
                        }
                    }
                    if (!bFind) {
                        ArrayList<String> tmpDel = new ArrayList<String>();
                        tmpDel.add(szwifiName);
                        wifiManager.removeSSIDFromWhiteList(componet, tmpDel);//先清理掉。
                    }
                }
            }
            //======================================================================================
            for (int i = 0; i < wifiList.length(); i++) {
                JSONObject jsonObject = wifiList.getJSONObject(i);
                String name = jsonObject.getString("Name1");
                newArray.add(name);
            }

            boolean bRet = wifiManager.addSSIDToWhiteList(componet, newArray);
            NetDataHub.get().addLog("setWifiList--华为--addSSIDToWhiteList添加到---bRet:" + bRet + "   wifiList:" + newArray.toString());


            ArrayList<String> tmp2 = wifiManager.getSSIDWhiteList(componet);
            if (tmp2 != null)
                NetDataHub.get().addLog("setWifiList--华为--getSSIDWhiteList--获取当前列表:" + tmp2.toString());
            else
                NetDataHub.get().addLog("setWifiList--华为--getSSIDWhiteList--获取当前列表为空.");

        } catch (Exception e) {
            NetDataHub.get().addLog("setWifiList---华为--addSSIDToWhiteList---catch error---getmessage:" + e.getMessage() + "tostring:" + e.toString());
            e.printStackTrace();
        }

    }
    public void setWifiList(JSONArray wifiList) {
        //-----add by gwb;2020.10.7   华为手机添加wifi白名单
        if (g_bUseHuaWeiMDM){
            if(!isCtrlWifi)
            {
                clealHuaWeiWifiControl();
            }
            else{
                setHuaWeiWifiControl(wifiList);

            }
        }
        //-----------End.

        NetDataHub.wifiList = wifiList;
        //saveWifiListAndWhiteApp();
    }

    public boolean isInWifiList(String ssid, String mac, Context context)
    {
        NetDataHub.get().addLog("isInWifiList---wifi白名单比对:");

        if (wifiList == null || wifiList.length() == 0)
        {
            Tell.log("isInWifiList---wifi白名单：白名单是空 所以直接通过");
            return true;
        }

        ssid = ssid.replace("\"", "");

        for (int i = 0; i < wifiList.length(); i++)
        {
            try
            {
                JSONObject o = wifiList.getJSONObject(i);

//                String whitSsid="\""+o.getString("Name1")+"\"";
                String whitSsid = o.getString("Name1");
                String whitMac = null;
                if (o.has("Name2"))
                {
                    whitMac = o.getString("Name2");
                }

                if (whitMac != null && !whitMac.equals(""))
                {

                    if (whitMac.equalsIgnoreCase(mac) && whitSsid.equalsIgnoreCase(ssid))
                    {
                        Tell.log("当前wifi信息【" + ssid + "】【" + mac + "】\n白名单信息【" + whitSsid + "】【" + whitMac + "】 在白名单列表中.");
                        NetDataHub.get().addLog("当前wifi信息【" + ssid + "】【" + mac + "】\n白名单信息【" + whitSsid + "】【" + whitMac + "】在白名单列表中.");
                        return true;
                    }
                    else
                    {
                        Tell.log("不是白名单内容 " + whitMac + "!=" + mac);
                        Tell.log("不是白名单内容 " + whitSsid + "!=" + ssid);


                        NetDataHub.get().addLog("不是白名单内容 " + whitSsid + "!=" + ssid);
                        NetDataHub.get().addLog("不是白名单内容 " + whitMac + "!=" + mac);

                    }
                }
                else
                {

                    if (whitSsid.equalsIgnoreCase(ssid))
                    {
                        Tell.log("当前wifi信息【" + ssid + "】【" + mac + "】\n白名单信息【" + whitSsid + "】");
                        NetDataHub.get().addLog("当前wifi信息【" + ssid + "】【" + mac + "】\n白名单信息【" + whitSsid + "】");

                        return true;
                    }

                }


            }
            catch (JSONException e)
            {
                Tell.log("wifi白名单：白名单读取错误，先叫你过去吧");
                NetDataHub.get().addLog("wifi白名单：白名单读取错误，先叫你过去吧");

                return true;
            }


        }


        Tell.log("当前wifi信息【" + ssid + "】【" + mac + "】\n白名单中信息" + wifiList.toString() + "不在白名单中");
        NetDataHub.get().addLog("当前wifi信息【" + ssid + "】【" + mac + "】\n白名单中信息" + wifiList.toString() + "不在白名单中");

        return false;
    }

    public void setWhiteApp(JSONArray array)
    {
        List<String> tmp = new ArrayList<>();
        try
        {
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject jsonObject = array.getJSONObject(i);
                String name = jsonObject.getString("Name1");
                tmp.add(name);
//                Tell.log(name+"载入白名单");
            }
            whiteApp = tmp;
            //saveWifiListAndWhiteApp();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public boolean isInWhite(String name)
    {
        /* del by gwb;2020.10.15  这个好像不需要。
        String config = Save.getValue(context, "WIFI_APP_WHITE", "0");
        if ("1".equals(config))
        {
            return true;
        }
         */
        if (!isCtrlApp)
        {
            return true;
        }
        if (whiteApp == null || whiteApp.size() == 0)
        {
            Tell.log("白名单为空 或者isCtrlApp=false 所以直接放行");
            return true;
        }
        for (String one : whiteApp)
        {
            if (name.equalsIgnoreCase(one))
            {
                Tell.log(one + "在白名单中 放行");

                return true;
            }
        }
        //Tell.log(name + "不在白名单中 禁止！");

        return false;
    }


    public void saveWhiteApp()
    {
        Save.fileSave(whiteApp, context, "whiteApp");
    }


    public void setContext(Activity context)
    {
        this.context = context;
    }

    //需要获取的时候调用一次
    public JSONArray getAppList()
    {
        PackageManager pckMan = context.getPackageManager();

        List<PackageInfo> packageInfo = pckMan.getInstalledPackages(0);
//        Log.w("EMMMME","Packge数量："+packageInfo.size());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        for (PackageInfo pInfo : packageInfo)
        {
            if((pInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0){//只收录非系统应用
                JSONObject jsonObject = new JSONObject();
                File file = new File(pInfo.applicationInfo.sourceDir);
                long modifiedTime = file.lastModified();
                String formatTime = sdf.format(modifiedTime);
//                Log.d("EMMMME","SoftName："+pInfo.applicationInfo.loadLabel(pckMan).toString()+"|"+formatTime+"|"+pInfo.packageName);
                try
                {
                    jsonObject.put("SoftName", pInfo.applicationInfo.loadLabel(pckMan).toString());
                    jsonObject.put("VersionNum", pInfo.versionName);
                    jsonObject.put("InstallDate", formatTime);
                    jsonObject.put("Publisher", pInfo.packageName);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                appList.put(jsonObject);
            }
        }
//        Log.w("EMMMME","appList："+appList.length());
        addLog("上传非系统应用数:" + appList.length());
        return appList;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public JSONObject getHardList()
    {
        SystemDataGet systemDataGet = new SystemDataGet();
        hardList = systemDataGet.get(context);
        Tell.log(hardList.toString());
        return hardList;
    }
    /**
     * 判断手机是否拥有Root权限。
     *
     * @return 有root权限返回true，否则返回false。
     */
    public boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

}
