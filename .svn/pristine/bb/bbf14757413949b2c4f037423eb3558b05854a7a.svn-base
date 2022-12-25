package com.grampus.hualauncherkai.Data;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.grampus.hualauncherkai.Tools.DeviceReceiver;
import com.grampus.hualauncherkai.Tools.Save;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlMonet;

public class MonetHub extends BroadcastReceiver
{
    public static boolean isMONETConnected;
    private static Uri uri = Uri.parse("content://telephony/carriers");

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE"))
        {
            //isMONETConnected = getMONETStatus(context);
            //ControlMonet(context);
        }
    }

    public boolean getMONETStatus(Context context)
    {
        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取所有网络连接的信息
        NetworkInfo[] networks = connMgr.getAllNetworkInfo();
        //通过循环将网络信息逐个取出来

        for (int i = 0; i < networks.length; i++)
        {
            NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI)
            {
                if (networks[i].getType() == ConnectivityManager.TYPE_MOBILE)
                {
                    if (networks[i].isConnected())
                    {
                        Toast.makeText(context, "移动数据已连接", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context, "移动数据已断开", Toast.LENGTH_SHORT).show();
                    }
                    return networks[i].isConnected();
                }
            }
        }


        return false;
    }

    public void ControlMonet(Context context)
    {
        if (true)
        {
            closeAPN(context);

//            NetworkInfo[] networks = connMgr.getAllNetworkInfo();
//            for (int i = 0; i < networks.length; i++)
//            {
//                if (networks[i].getType() == ConnectivityManager.TYPE_MOBILE)
//                {
//                    DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//                    ComponentName componentName = new ComponentName(context, DeviceReceiver.class);
//
//                    devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
//                }
//            }
        }
    }


    public void openAPN(Context context)
    {

        List<APN> list = getAPNList(context);
        for (APN apn : list)
        {
            ContentValues cv = new ContentValues();
            cv.put("apn", APNMatchTools.matchAPN(apn.apn));
            cv.put("type", APNMatchTools.matchAPN(apn.type));
            context.getContentResolver().update(uri, cv, "_id=?", new String[]{apn.id});

        }
    }

    public void closeAPN(Context context)
    {
        List<APN> list = getAPNList(context);
        for (APN apn : list)
        {
            ContentValues cv = new ContentValues();
            cv.put("apn", APNMatchTools.matchAPN(apn.apn) + "mdev");
            cv.put("type", APNMatchTools.matchAPN(apn.type) + "mdev");
            context.getContentResolver().update(uri, cv, "_id=?", new String[]{apn.id});

        }
    }

    private List<APN> getAPNList(Context context)
    {
        String tag = "Main.getAPNList()";

        //current不为空表示可以使用的APN
        String[] projection = {"_id,apn,type,current"};
        Cursor cr = context.getContentResolver().query(uri, projection, null, null, null);

        List<APN> list = new ArrayList<APN>();

        while (cr != null && cr.moveToNext())
        {
            Log.d(tag, cr.getString(cr.getColumnIndex("_id")) + "  " + cr.getString(cr.getColumnIndex("apn")) + "  " + cr.getString(cr.getColumnIndex("type")) + "  " + cr.getString(cr.getColumnIndex("current")));
            APN a = new APN();
            a.id = cr.getString(cr.getColumnIndex("_id"));
            a.apn = cr.getString(cr.getColumnIndex("apn"));
            a.type = cr.getString(cr.getColumnIndex("type"));
            list.add(a);
        }
        if (cr != null)
            cr.close();
        return list;
    }

    public static class APN
    {
        String id;
        String apn;
        String type;
    }
}
