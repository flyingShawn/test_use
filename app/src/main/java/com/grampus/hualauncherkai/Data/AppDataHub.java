package com.grampus.hualauncherkai.Data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.grampus.hualauncherkai.Tools.Save;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Grampus on 2017/4/18.
 */

public class AppDataHub
{

    final static private String TAG = "AppDataHub";

    static public final int ItemNum = 4;

    static Context context;

    static String[] offenName = new String[]{"电话", "短信", "联系人", "相机"};

    static ResolveInfo settingApp;

    static public boolean isCanSetting = false;

    static List<List<ResolveInfo>> appPage;//分页之后的App列表
    static List<ResolveInfo> allApps;//所有App
    static ResolveInfo[] offen = new ResolveInfo[ItemNum];//常用App

    public static List<List<ResolveInfo>> getAppPage()
    {
        show();
        return appPage;
    }


    public static ResolveInfo[] getOffen()
    {

        for (int i = 0; i < offen.length; i++)
        {
            if (offen[i] != null)
            {
                if (!NetDataHub.get().isInWhite(offen[i].loadLabel(context.getPackageManager()).toString()))//判断当前为非白名单，不要i--吗？
                {
                    offen[i] = null;
                    offenName[i] = null;
                }
                else
                {

                }
            }
            else
            {

            }
        }

        return offen;
    }


    static public void addSettingApp(ResolveInfo resolveInfo)
    {
        settingApp = resolveInfo;
    }

    public static ResolveInfo getSettingApp()
    {
        return settingApp;
    }

    static public void init(Context _context)
    {
        context = _context;
    }


    static public void setNewOffen(ResolveInfo resolveInfo)
    {
        int i;
        for (i = 0; i < offen.length; i++)
        {
            if (offen[i] == null)
            {
                if (resolveInfo == null)
                {

                    offen[i] = settingApp;
                    offenName[i] = offen[i].loadLabel(context.getPackageManager()).toString();

                    break;
                }
            }
            if (resolveInfo == null)
            {

                if (offen[i] == null)
                {

                    offen[i] = settingApp;
                    offenName[i] = offen[i].loadLabel(context.getPackageManager()).toString();
                    break;
                }
            }
            else
            {
                if (resolveInfo.activityInfo.packageName.equals(offen[i].activityInfo.packageName))
                {
                    offen[i] = settingApp;
                    offenName[i] = offen[i].loadLabel(context.getPackageManager()).toString();

                    break;
                }
            }
        }

        for (int j = 0; j < offenName.length; j++)
        {
            if (offenName[j] == null || offen[j] == null)
            {
                continue;
            }
            String s = offen[j].loadLabel(context.getPackageManager()).toString();
            offenName[j] = s;
            Save.putValue(context, "offen" + j, s);

        }
    }

    static public void loadApps()
    {
        loadOffen();
        loadAppsInfo();
        setEveryPage();
    }



    static public void show()
    {
        for (List<ResolveInfo> one : appPage)
        {
            //Tell.log("show--------------------------begin.");
            for (ResolveInfo _one : one)
            {
                //Tell.log("show------:" +_one.loadLabel(context.getPackageManager()).toString());
            }
            ///Tell.log("show--------------------------end.");
        }

        for (ResolveInfo one : offen)
        {
            if (one != null)
            {
            }
        }
    }

    static public boolean isSettingOffen()
    {

        for (ResolveInfo one : offen)
        {
            if (one == null)
            {
                continue;
            }
            String s = one.activityInfo.packageName;
            if (s.equals(settingApp.activityInfo.packageName))
            {
                return true;
            }
        }
        return false;

    }


    static public void removeSettingOffen()
    {
        for (int i = 0; i < offen.length; i++)
        {
            if (offen[i] != null)
            {
                String s = offen[i].activityInfo.packageName;
                if (s.equals(settingApp.activityInfo.packageName))
                {
                    offen[i] = null;
                    break;
                }
            }
        }


        for (int j = 0; j < offenName.length; j++)
        {
            if (offenName[j] == null || offen[j] == null)
            {
                Save.putValue(context, "offen" + j, "");
                continue;
            }
            String s = offen[j].loadLabel(context.getPackageManager()).toString();
            offenName[j] = s;
            Save.putValue(context, "offen" + j, s);

        }


    }

    static private void loadOffen()
    {
        String isFirstUse = Save.getValue(context, "isFirstUse", "y");
        if (isFirstUse.equals("y"))
        {
            for (int i = 0; i < ItemNum; i++)
            {
                Save.putValue(context, "offen" + i, offenName[i]);
            }
            Save.putValue(context, "isFirstUse", "n");
        }
        else
        {
            String nameTmp;
            for (int i = 0; i < ItemNum; i++)
            {
                nameTmp = Save.getValue(context, "offen" + i, "");
                if (!nameTmp.equals(""))
                {
                    offenName[i] = nameTmp;
                }
                else
                {
                    offenName[i] = null;
                }
            }
        }
    }


    private static void setEveryPage()
    {
        if (appPage == null)
        {
            appPage = new ArrayList<>();
        }
        else
        {
            appPage.clear();
        }
        List<ResolveInfo> onePage = new ArrayList<>();
        int onePageMaxItem = 3;


       /*ResolveInfo setting = new ResolveInfo();
        setting.icon = -1;
        ResolveInfo appStore = new ResolveInfo();
        appStore.icon = -2;
        allApps.add(setting);
        allApps.add(appStore);*/

        if(NetDataHub.isShowAppStore)
            allApps.add(null);
        allApps.add(null);
        for (ResolveInfo oneApp : allApps)
        {
            int i;
            for (i = 0; i < ItemNum; i++)
            {
                if (offenName[i] == null)
                {
                    continue;
                }
                if (oneApp != null && (offenName[i].equals(oneApp.loadLabel(context.getPackageManager()))))//getPackageManager获取安装或未安装的包信息
                {
                    offen[i] = oneApp;
                    break;
                }
            }

            if (i == ItemNum)
            {
                onePage.add(oneApp);
                if (onePageMaxItem == 3 && appPage.size() != 0)//其他页是5行
                {
                    onePageMaxItem = 5;
                }
                if (onePage.size() == ItemNum * onePageMaxItem)//第一页是3行
                {
                    appPage.add(onePage);

                    onePage = new ArrayList<>();
                }
            }

        }
        if (onePage.size() != 0)
        {
            appPage.add(onePage);
        }
        NetDataHub.get().addLog("EMMMain----AppDataHub---显示APP总数:" + allApps.size());
    }


    private static void loadAppsInfo()
    {
        try{

            //查找启动方式为LAUNCHER并且是行为ACTION_MAIN的APP
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (allApps == null)
            {
                allApps = new ArrayList<>();
            }
            else
            {
                allApps.clear();
            }
            List<ResolveInfo> tmp = context.getPackageManager().queryIntentActivities(mainIntent, 0);
            for (ResolveInfo one : tmp)
            {
                String appName = one.loadLabel(context.getPackageManager()).toString();
                //Log.w("EMMAPP1", appName);
                if (NetDataHub.get().isInWhite(appName)&&!"EMM".equals(appName))//emm本身不再显示
                {
                    //Tell.log(one.loadLabel(context.getPackageManager()).toString()+"---加入将要显示的白名单");

                   // Log.w("EMMAPP", appName);
                    allApps.add(one);
                }
            }
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }

    }
    static public int getShowAppCount() {
        if (allApps == null)
        {
            return 0;
        }
        return allApps.size();
    }

}
