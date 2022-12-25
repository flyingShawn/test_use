package com.grampus.hualauncherkai.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;


/**
 * Created by Grampus on 2017/4/18.
 */

public class BootReceiver extends BroadcastReceiver
{
    static Handler handler;

    static public void setHandler(Handler hd)
    {
        handler = hd;
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED"))
        {
            //String packageName = intent.getDataString();
            //System.out.println("安装了:" + packageName + "包名的程序");
            try
            {
                handler.sendEmptyMessage(1);
            }
            catch (Exception e)
            {

            }
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED"))
        {
            //String packageName = intent.getDataString();
            //System.out.println("卸载了:" + packageName + "包名的程序");
            try
            {
                handler.sendEmptyMessage(1);
            }
            catch (Exception e)
            {

            }
        }
    }
}