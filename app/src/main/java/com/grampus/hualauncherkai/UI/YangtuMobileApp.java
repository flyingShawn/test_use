package com.grampus.hualauncherkai.UI;

import android.app.Application;

public class YangtuMobileApp extends Application
{
    private static final String TAG = "YangtuMobileEncrytApp";

    private static YangtuMobileApp instance;

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        //CrashHandler.getInstance().init(instance);
    }

    @Override
    public void onTerminate()
    {
        // TODO Auto-generated method stub
        super.onTerminate();
    }

    @Override
    public void onLowMemory()
    {
        // TODO Auto-generated method stub
        super.onLowMemory();
    }

    public static YangtuMobileApp getInstance()
    {
        return instance;
    }
}
