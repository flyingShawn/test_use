package com.grampus.hualauncherkai.Data;

/**
 * Created by Grampus on 2017/4/19.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

import com.grampus.hualauncherkai.util.DeviceInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.TELEPHONY_SERVICE;

public class SystemDataGet
{

    Activity context;

    JSONObject jsonObject;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public JSONObject get(Activity activity)
    {
        context = activity;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String mtyb = android.os.Build.BRAND;// 手机品牌
        String mtype = android.os.Build.MODEL; // 手机型号
        jsonObject = new JSONObject();

        try
        {
            jsonObject.put("Telmodel", mtype);
            jsonObject.put("screen", getScreen());
            jsonObject.put("cpu", getCpuName());//0
            jsonObject.put("memory", getTotalMemory() + "");//-
            jsonObject.put("storage", getRomTotalSize());//bushirom
            jsonObject.put("SDstorage", getSDTotalSize());
            jsonObject.put("manufacturer", mtyb);
            return jsonObject;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    String getScreen()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return height + "*" + width;
    }

    private String getSDTotalSize()
    {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    private String getRomTotalSize()
    {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    static public String getIp(Context context)
    {
        String sIp = "";
        try
        {
            if(context!=null)
            {
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                //检查Wifi状态
                if (wm.isWifiEnabled())
                {
                    WifiInfo wi = wm.getConnectionInfo();
                    //获取32位整型IP地址
                    int ipAdd = wi.getIpAddress();
                    //把整型地址转换成“*.*.*.*”地址
                    sIp = (ipAdd & 0xFF) + "." +
                            ((ipAdd >> 8) & 0xFF) + "." +
                            ((ipAdd >> 16) & 0xFF) + "." +
                            (ipAdd >> 24 & 0xFF);
                }
            }
            if("0.0.0.0".equals(sIp)||"".equals(sIp))
            {
                sIp = DeviceInfoUtil.getPhoneIp();  //add by fsy 2021.12.09 使用另一种方式获取IP
            }
        }catch (Exception e){
     //     NetDataHub.get().addLog("getIp-----error:"+e.toString());
            sIp = DeviceInfoUtil.getPhoneIp();
        }
        return sIp;
    }


    /**
     * 获取手机内存大小
     *
     * @return
     */
    private String getTotalMemory()
    {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        FileReader localFileReader = null;
        BufferedReader localBufferedReader = null;

        try
        {
            localFileReader = new FileReader(str1);
            localBufferedReader = new BufferedReader(
                    localFileReader);
            String data = localBufferedReader.readLine();
            String result = "";
            for (char c : data.toCharArray())
            {
                if (c > '0' && c < '9')
                {
                    result += c;
                }
            }


            return result + "KB";

        } catch (IOException e)
        {
        } finally
        {
            try
            {
                localBufferedReader.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                localFileReader.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
        return "null";
    }

    /**
     * 获取当前可用内存大小
     *
     * @return
     */
    private String getAvailMemory()
    {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(new Activity().getBaseContext(), mi.availMem);
    }

    private static String getMaxCpuFreq()
    {
        String result = "";
        ProcessBuilder cmd;
        try
        {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1)
            {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex)
        {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim() + "Hz";
    }

    // 获取CPU最小频率（单位KHZ）

    private static String getMinCpuFreq()
    {
        String result = "";
        ProcessBuilder cmd;
        try
        {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1)
            {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex)
        {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim() + "Hz";
    }

    // 实时获取CPU当前频率（单位KHZ）

    private static String getCurCpuFreq()
    {
        String result = "N/A";
        try
        {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim() + "Hz";
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    static public String getLauncherVersion(Context context)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode + "";
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return "null";


    }


    static public String getMacAddress(Context context)
    {

        //return DeviceInfoUtil.getMacAddr(context);
        return DeviceInfoUtil.getWifiMacAddress();
        //return DeviceInfoUtil.getMacAddrKai(context);

        /*String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);


            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
//        return "haha";*/
    }

    private String getCpuName()
    {
        FileReader fr = null;
        BufferedReader br = null;
        try
        {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            String tmp;
            while ((tmp = br.readLine()) != null)
            {
                if (tmp.contains("Hardware"))
                {
                    tmp = tmp.replace("Hardware", "");
                    tmp = tmp.replace(":", "");
                    tmp = tmp.replace("\t", "");
                    return tmp;
                }
            }
            return "null";
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (fr != null)
                try
                {
                    fr.close();
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (br != null)
                try
                {
                    br.close();
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return null;
    }

    static public String getAndroidVersion()
    {
        return android.os.Build.VERSION.RELEASE;
    }


}

