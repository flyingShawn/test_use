package com.grampus.hualauncherkai.Tools;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceReceiver extends DeviceAdminReceiver
{
    @Override
    public void onEnabled(Context context, Intent intent)
    {
        super.onEnabled(context, intent);
        Toast.makeText(context, "设备管理器:已激活", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent)
    {
        super.onDisabled(context, intent);
        Toast.makeText(context, "设备管理器:未激活", Toast.LENGTH_SHORT).show();
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent)
    {
        Intent intent1 = context.getPackageManager().getLaunchIntentForPackage("com.android.settings");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
        try
        {
            Thread.sleep(7000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return "This is a onDisableRequested response message";
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent)
    {
        super.onPasswordChanged(context, intent);
        Toast.makeText(context, "设备管理；密码已经改变", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent)
    {
        super.onPasswordFailed(context, intent);
        Toast.makeText(context, "设备管理：改变密码失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent)
    {
        super.onPasswordSucceeded(context, intent);
        Toast.makeText(context, "设备管理；改变密码成功", Toast.LENGTH_SHORT).show();
    }


}
