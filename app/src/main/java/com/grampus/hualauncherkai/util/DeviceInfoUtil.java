package com.grampus.hualauncherkai.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.log.LogTrace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

public class DeviceInfoUtil
{
    private static final String TAG = "EMMDeviceInfoUtil";

    /**
     * 获取当前可用内存大小
     *
     * @return
     */
    public static String getAvailMemory(Context context)
    {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(context, mi.availMem);
    }

    /**
     * 获得总内存
     *
     * @return
     */
    public static String getTotalMemory(Context context)
    {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null)
            {
                content = line;
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息

        content = content.substring(begin + 1, end).trim();
        mTotal = Long.parseLong(content);
        return Formatter.formatFileSize(context, mTotal * 1024l);
    }

    /**
     * @return
     */
    public static String getSDCardAvailSize(Context context)
    {
        long sdCardInfo = 0l;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSize();
            long availBlocks = sf.getAvailableBlocks();

            sdCardInfo = bSize * availBlocks;//可用大小   
        }
        return Formatter.formatFileSize(context, sdCardInfo);
    }

    /**
     * @return
     */
    public static String getSDCardTotalSize(Context context)
    {
        long sdCardInfo = 0l;
        String state = Environment.getExternalStorageState();
        try
        {
            if (Environment.MEDIA_MOUNTED.equals(state))
            {
                File sdcardDir = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(sdcardDir.getPath());
                long bSize = sf.getBlockSize();
                long bCount = sf.getBlockCount();
                long availBlocks = sf.getAvailableBlocks();

                sdCardInfo = bSize * bCount;//总大小
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
        return Formatter.formatFileSize(context, sdCardInfo);
    }

    public static String getDisplayScreen(Activity activity)
    {

        // 方法1 Android获得屏幕的宽和高
        WindowManager windowManager = activity.getWindowManager();
        Display display1 = windowManager.getDefaultDisplay();
        int screenWidth = display1.getWidth();
        int screenHeight = display1.getHeight();
        return screenHeight + "*" + screenWidth;
    }

    /**
     * 获取全部应用
     * 获取相关信息：
     * PackageInfo mPackageInfo;
     * 获取包名： mPackageInfo.packageName
     * 获取icon： mPackageInfo.getApplicationIcon(applicationInfo);
     * 获取应用名： mPackageInfo.getApplicationLabel(applicationInfo);
     * 获取使用权限：
     * mPackageInfo.getPackageInfo(packageName,PackageManager.GET_PERMISSIONS).requestedPermissions;
     */
    public static void getInstallApps(Context context)
    {

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        //判断是否系统应用
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        for (int i = 0; i < packageInfoList.size(); i++)
        {
            PackageInfo pak = packageInfoList.get(i);
            //判断是否为系统预装的应用
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
            {
                // 第三方应用
                apps.add(pak);
                Log.d("DeviceInfoUtil", "app = " + pak.packageName + "   name = " + pak.applicationInfo.loadLabel(context.getPackageManager()).toString());
            }
            else
            {
                //系统应用
            }
        }
    }

    public static String getWifiIp(Context context)
    {
        if (NetWorkGPSUtil.isWifiConnected(context))
        {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String macAddress = info.getMacAddress();
            String ipAddress = intToIp(info.getIpAddress());
            return ipAddress;
        }
        else
        {
            return null;
        }
    }

//    public static String getWifiMAC(Context context)
//    {
//        if (NetWorkGPSUtil.isWifiConnected(context))
//        {
//            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            String macAddress = info.getMacAddress();
//
//            Save.putValue(context,"mac",macAddress);
//
//            return macAddress;
//        }
//        else
//        {
//            String macAddress=Save.getValue(context,"mac","");
//            return macAddress;
//            //return null;
//        }
//    }

    private static String intToIp(int ip)
    {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }

    /**
    * @author  fsy
    * @date    2022/2/15 10:42
    * @return
    * @description  通过蓝牙获取名称，没有蓝牙则以厂商DEVICE为准；
    */
    public static String getDeviceName(){
  /*  鸿蒙下不能始终获取蓝牙权限，每调一次要申请一次，不用了
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        boolean isOpenOK = false;
        if (bluetoothAdapter == null) {
            Log.e(TAG, "--------------- 不支持蓝牙");
        }
        if(!bluetoothAdapter.isEnabled())
        {
            isOpenOK = bluetoothAdapter.enable();
            if(isOpenOK){
                Log.e(TAG, "--------------- 蓝牙开启成功");
            }else {
                Log.e(TAG, "--------------- 蓝牙开启失败");
            }
        }else if(bluetoothAdapter != null && bluetoothAdapter.isEnabled()){
            Log.e(TAG, "--------------- 蓝牙已开启");
        }else
            Log.e(TAG, "--------------- 蓝牙打开失败");

        if(isOpenOK)
            bluetoothAdapter.disable();
*/
        String szDeviceName = "";
        try{
            if(EMMApp.getInstance().mainContext!=null)
                szDeviceName =Settings.Secure.getString(EMMApp.getInstance().mainContext.getContentResolver(), "bluetooth_name");
        }catch (Exception e){
            Log.e(TAG, "获取蓝牙失败---------"+e.toString());
            if( NetDataHub.get()!=null)
                NetDataHub.get().addLog("获取蓝牙失败 :"+e.toString());
        }
        if(szDeviceName==null||szDeviceName.isEmpty()){
            szDeviceName = android.os.Build.DEVICE;
        }
        Log.e(TAG,"DeviceName: "+szDeviceName);

        if( NetDataHub.get()!=null)
            NetDataHub.get().addLog("getDeviceName :"+szDeviceName);
//      boolean disable = bluetoothAdapter.disable();

        return szDeviceName;
    }

    public static String getPhoneIp()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                    {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress 
                        // instanceof Inet6Address) { 
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    /**
     * 原版自带的获取mac地址的方法
     * 因为可能获取到的是null，所以我不再采用这个方法
     *
     * @param context
     * @return
     */
    public static String getMacAddr(Context context)
    {
        return getWifiMacAddress();
    }


    public static String getWifiMacAddress()
    {
        byte[] mac = null;
        try
        {

            String macAddress = getWireMac();
            if(macAddress!=null)
            {
                return macAddress;
            }

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces != null)
            {
                while (networkInterfaces.hasMoreElements())
                {
                    NetworkInterface intf = networkInterfaces.nextElement();
                    if (intf.getName() != null && intf.getName().equalsIgnoreCase("wlan0"))
                    {
                        mac = intf.getHardwareAddress();
                        break;
                    }
                }
            }

        }
        catch (SocketException ex)
        {
            //AccountLog.e(TAG, "failed to get wifi Mac Address", ex);
        }

        if (mac != null)
        {
            StringBuilder buf = new StringBuilder();
            for (byte aMac : mac)
            {
                buf.append(String.format("%02X:", aMac));
            }
            if (buf.length() > 0)
            {
                buf.deleteCharAt(buf.length() - 1);
            }

            if(!buf.toString().equals(EMMApp.getInstance().macAddr))    //返回前比较mac
            {
                Log.w("EMMmac","与保存的mac不相等,重新保存");
                EMMApp.getInstance().macAddr = buf.toString();//add by fsy;2022.1.24 mac与存储的有不同，重新保存
                Save.fileSave(EMMApp.getInstance().macAddr, EMMApp.getInstance().mainContext, "macAddr");
            }
            return buf.toString();
        }

        //add by fsy 2022.1.20
        //mac为空才会走下面，此时读取保存的文件中的mac地址
        if(EMMApp.getInstance().macAddr.isEmpty())
        {
            if(EMMApp.getInstance().mainContext!=null)
            {
                Object macAd = Save.readFile(EMMApp.getInstance().mainContext, "macAddr");
                if (macAd != null)
                {
                    Log.w("EMMmac","readFile,(String)macAd:"+(String)macAd);
                    EMMApp.getInstance().macAddr = (String)macAd;
                }
            }
        }

        //Log.w("EMMmac","EMMApp:"+EMMApp.getInstance().macAddr);
        return EMMApp.getInstance().macAddr;
    }
    /**
    * @author  fsy
    * @date    2022/1/25 15:27
    * @return
    * @description  获取有线连接的MAC地址，大屏安卓
    */
    public static String getWireMac(){
        String strMacAddress = null;
        try {
//          byte[] b = NetworkInterface.getByName("eth0") .getHardwareAddress();
            NetworkInterface eth0 = NetworkInterface.getByName("eth0");
            if(eth0!=null) {
                byte[] b = eth0.getHardwareAddress();

                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < b.length; i++) {
                    if (i != 0) {
                        buffer.append(':');
                    }
                    //System.out.println("b:"+(b[i]&0xFF));
                    String str = Integer.toHexString(b[i] & 0xFF);
                    buffer.append(str.length() == 1 ? 0 + str : str);
                }

                strMacAddress = buffer.toString().toUpperCase();

                updateMac(strMacAddress);
    //            Log.d("EMMmac","getWireMac:"+strMacAddress);
            }
            else
            {
     //           Log.d("EMMmac","getByName为空");
            }
        } catch (Exception e) {
       //     Log.d("EMMmac","getWireMac error:"+e.toString());
        }
        return strMacAddress;
    }

    /**
    * @author  fsy
    * @date    2022/1/25 15:38
    * @return
    * @description  和旧的MAC地址比较，不同则重新保存
    */
    public static void updateMac(String mac){
        try{
            if(mac.isEmpty())
                return;
            if(!mac.equals(EMMApp.getInstance().macAddr))    //返回前比较mac
            {
                Log.w("EMMmac","与保存的mac不相等,重新保存");
                EMMApp.getInstance().macAddr = mac;//add by fsy;2022.1.24 mac与存储的有不同，重新保存
                if(EMMApp.getInstance().mainContext!=null) {
                    Save.fileSave(EMMApp.getInstance().macAddr, EMMApp.getInstance().mainContext, "macAddr");
                }
            }
        } catch (Exception e) {
            //     Log.d("EMMmac","getWireMac error:"+e.toString());
        }
    }
    /**
     * 2020 04.02 未来修改
     * 这个context有的时候会变成null，原因暂时未知
     * 所以添加一个try catch进行判断
     *
     * @param context
     * @return
     */
    public static String getUUID(Context context)
    {
        try
        {
            String uuid = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
            LogTrace.i(TAG, "getUUID", "ANDROID_ID ===" + uuid);
            if (StringUtil.isEmpty(uuid))
            {
                uuid = getLocalUUID(context);
            }
            return uuid;
        }
        catch (NullPointerException e)
        {
            return "context is null";
        }
    }

    public static String getLocalUUID(Context context)
    {
        String uuid = Save.getValue(context, "generateUUID", "");
        if (StringUtil.isEmpty(uuid))
        {
            uuid = UUID.randomUUID().toString();
            Save.putValue(context, "generateUUID", uuid);
        }
        LogTrace.i(TAG, "getLocalUUID", "uuid ===" + uuid);
        return uuid;
    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context)
    {
        return false;//add by gwb;2020.9.21  现在相同处理。
       /* return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;

        */
    }

    /**
    * @author  fsy
    * @date    2022/2/17 15:53
    * @return
    * @description 设置桌面主题文字的颜色
    */
    public static void getTextColor(){
        try{
            if(EMMApp.getInstance().mainContext!=null)
            {
                String colorItem = Save.getValue(EMMApp.getInstance().mainContext, "TEXT_COLOR", "0");
                if( Integer.parseInt(colorItem) == 0){
                    EMMApp.getInstance().textColor = Color.parseColor("#FFFFFF");//白色
                }else if( Integer.parseInt(colorItem) == 1){
                    EMMApp.getInstance().textColor = Color.parseColor("#000000");//黑色
                }
            }
        }catch (Exception e){
            Log.e("EMMMain","setTextColor-e:"+e.toString());
        }
    }

    /**
    * @author  fsy
    * @date    2022/4/14 18:23
    * @return
    * @description 启动时获取一次，没获取到后面会定时器保证获取。
    */
    public static void initDeviceInfo(){
        try{

            EMMApp.getInstance().deviceName = getDeviceName();
            EMMApp.getInstance().macAddr = getWifiMacAddress();

            if(EMMApp.getInstance().diskNum.isEmpty())//最开始上来必定是空的
            {
                if(EMMApp.getInstance().mainContext!=null)
                {
                    Object diskNum = Save.readFile(EMMApp.getInstance().mainContext, "diskNum");
                    if (diskNum != null)
                    {
                        Log.w("EMMmac","readFile,(String)diskNum:"+diskNum);
                        EMMApp.getInstance().diskNum = (String)diskNum;
                        NetDataHub.get().addLog("initDeviceInfo--从文件中读取到diskNum:"+EMMApp.getInstance().diskNum);
                    }
                    else if(!EMMApp.getInstance().macAddr.isEmpty())//基本上不会为空，不排除可能
                    {
                        EMMApp.getInstance().diskNum = EMMApp.getInstance().macAddr;//这种状态获取成功，保存到文件中
                        Save.fileSave(EMMApp.getInstance().diskNum, EMMApp.getInstance().mainContext, "diskNum");
                        NetDataHub.get().addLog("initDeviceInfo--diskNum = mac:"+EMMApp.getInstance().diskNum);
                    }
                }
            }
        }catch (Exception e){
            Log.e("EMMMain","setTextColor-e:"+e.toString());
        }
    }

}
