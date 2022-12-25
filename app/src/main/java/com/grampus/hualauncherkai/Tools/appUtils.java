package com.grampus.hualauncherkai.Tools;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.util.List;

import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlApp;
import static com.grampus.hualauncherkai.Data.NetDataHub.whiteApp;

import static com.grampus.hualauncherkai.UI.MainActivity.androidv;


public class appUtils
{
    private DevicePolicyManager devicePolicyManager = null;
    private ComponentName componentName = null;
    private Context mContext = null;

    private List<ApplicationInfo> appList = null;
    private PackageManager packageManager = null;

    public static boolean isDeviceOwnerApp = false;

    public appUtils(Context context)
    {
        try {
            mContext = context;

            devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            componentName = new ComponentName(context, DeviceReceiver.class);

            isDeviceOwnerApp = devicePolicyManager.isAdminActive(componentName);

            packageManager = mContext.getPackageManager();
            appList = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        }
        catch(Exception e) {

        }
    }

    public static boolean isApkInDebug(Context context) {//add by gwb;2020.9.24
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
    public String[] getAllAppPackageName()
    {
        //用于存储所有已安装程序的包名
        String[] packageNames = new String[appList.size()];
        //从pinfo中将包名字逐一取出，压入pName list中
        if (appList != null)
        {
            for (int i = 0; i < appList.size(); i++)
            {
                String packName = appList.get(i).packageName;
                packageNames[i] = packName;
            }
        }

        return packageNames;
    }

    public List<ApplicationInfo> getAllAppInfo()
    {
        return appList;
    }

    /**
     * 2020.04.16 未来修改
     * 根据App的包名，冻结这个App
     * 如果是系统程序则不会冻结
     * 如果是白名单中的程序则不会冻结
     *
     * @param appInfo
     */
    public void freezeSingleApp(ApplicationInfo appInfo)
    {
        try {


            //------add by gwb;2020.9.24  setApplicationHidden这个函数在安卓4.0下面调用会报错。
            //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            String a = Build.VERSION.RELEASE;
            String b = android.os.Build.VERSION.RELEASE;

            if(androidv <= 4 )
                return ;

            boolean bRet = false;
            int nRet =isAppNeedControlled(appInfo);
            if(nRet == 2) {//add by gwb;2020.9.24  不需要处理的直接返回。
                return ;
            }
            bRet = nRet == 1;
            //-----------------------------------end.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                System.out.println("freezeSingleApp-----setPackagesSuspended---开始禁用:" + appInfo.packageName );
                devicePolicyManager.setPackagesSuspended(componentName, new String[]{appInfo.packageName},bRet);
            } else {
                devicePolicyManager.setApplicationHidden(componentName, appInfo.packageName,bRet);
            }
        }
        catch(Exception e){
            System.out.println("freezeSingleApp-----调用禁用程序异常！！！！:" + appInfo.packageName);
            e.printStackTrace();
        }
    }

    public int isAppNeedControlled(ApplicationInfo appInfo)
    {
//        if (appInfo.packageName.indexOf("android") != -1)
//        {
//            System.out.println(appInfo.packageName + "是系统程序，无需屏蔽！");
//            return false;
//        }

        try//add by gwb;2020.9.24 加上try catch
        {
            //如果App白名单没有开启，则直接返回false
            if (!isCtrlApp) {
                return 0;
            }

            //是系统程序，不做处理
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                //System.out.println(appInfo.packageName + "是系统程序，无需屏蔽！");
                return 2;
            }

            //不是系统程序
            else {
                //是启动器自身，不需要屏蔽
                if (appInfo.packageName.equalsIgnoreCase(mContext.getApplicationInfo().packageName)) {
                    System.out.println(appInfo.packageName + "是启动器自身，无需屏蔽！");
                    return 2;
                }

                //在白名单中，不需要屏蔽
                try {
                    for (int j = 0; j < whiteApp.size(); j++) {
                        if (appInfo.loadLabel(packageManager).toString().equalsIgnoreCase(whiteApp.get(j))) {
                            System.out.println(appInfo.packageName + "在白名单中，无需屏蔽！");
                            return 0;
                        } else {
                            continue;
                        }
                    }
                } catch (Exception e) {
                    return 0;
                }

                //需要屏蔽
            }
            System.out.println(appInfo.packageName + "需要屏蔽！");
            return 1;
        }
        catch (Exception e){
        }
        return 0;
    }

    public class ProcessInfo
    {
        public String name;
        public String packageName;
        public Drawable icon;
        public long memory;
        public boolean isUser; //true表示用户进程
        public boolean isChecked; //表示当前item是否被勾选
    }

    /**
     * 读取app白名单，对app进行管控
     * 逻辑如下：
     * 1.读取每一个app，并进行判定
     * 2.判定通过后，对其进行屏蔽
     *
     * @return
     */
    public int appControl()
    {
        try {
            if (devicePolicyManager.isAdminActive(componentName)) {
                appList = getAllAppInfo();
                for (int i = 0; i < appList.size(); i++) {
                    freezeSingleApp(appList.get(i));
                }
                return 1;
            } else {
                return -1;
            }
        }
        catch(Exception e){

        }
        return -1;
    }
}
