package com.grampus.hualauncherkai;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

/**
* @author  fsy
* @date    2022/12/6 16:36
* @return
* @description  这里保存的静态数据都是没有什么分配内存的对象，
* 没有什么需要限制的地方，就不用private和get() set()了,简洁点
*/
public class EMMApp extends Application {

    private static EMMApp mApp;

    public static final String HONOR_PAD_V7 = "HONOR-KRJ2-W09";  //荣耀平板V7    六院
    public static final int REMOTE_PORT = 6671;

    public int screeenWidth;
    public int screenHight;
    public float density;
    public int densityDpi;

    public int equipID ;
    public int manageID ;


    public Context mainContext;
    public boolean settingDesktop = true;
    public boolean screenLock = false;
    public boolean shouldUpdate = false;
    public boolean canSendLog = false;
    public boolean startCapture =false;

    public String serverIp ="";
    public String centerServerIp ="";
    public String macAddr = "";  //macAddr
    public String diskNum = "";  //用第一次保存下来的macAddr当diskNum。没有收到服务器的冲突更改命令，不会修改。
    public String servApkVersion = "";
    public String deviceName = "";

    public int  textColor = -1;//-1  #FFFFFF #000000 Color.parseColor("#000000")
    public int resultCode = -1;
    public Intent resultData =null; //add by fsy 2021.9.20
    public int g_testCount = 0;//测试截屏前几帧打日志用的，平时用都注掉
    public int controlType = 0 ; //默认0，返回都使用辅助功能.


    public static EMMApp getInstance(){
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainContext = getApplicationContext();
        mApp = this;
    }
}
