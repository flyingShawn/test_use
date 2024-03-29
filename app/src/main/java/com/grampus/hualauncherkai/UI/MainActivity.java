package com.grampus.hualauncherkai.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.grampus.hualauncherkai.Data.AppDataHub;
import com.grampus.hualauncherkai.Data.MonetHub;
import com.grampus.hualauncherkai.Data.NetCtrlHub;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.Data.SampleEula;
import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Receiver.ActionReceiver;
import com.grampus.hualauncherkai.Receiver.WifiHub;
import com.grampus.hualauncherkai.TcpSock.CPackOperate;
import com.grampus.hualauncherkai.Tools.DeviceReceiver;
import com.grampus.hualauncherkai.Tools.DownloadUtils.DownloadFile;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.Tools.Tell;
import com.grampus.hualauncherkai.common.VersionInfoBean;
import com.grampus.hualauncherkai.fragment.AppFragment;
import com.grampus.hualauncherkai.service.EMMAccessibilityService;
import com.grampus.hualauncherkai.util.DeviceInfoUtil;
import com.grampus.hualauncherkai.util.FileManagerUtil;
import com.grampus.hualauncherkai.util.HttpDownloaderThread;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity
{

    public static MainActivity mainActivity = null;
    private ActionReceiver mReceiver = null;    //add by fsy 2021.12.24 广播独立成类
    public static Dialog g_dialog = null;       //add by fsy 2021.12.24 系统提示升级框保证唯一

    //    public static boolean isActive = false;
    public static boolean g_bAllowSetting = false;
    private MediaProjectionManager mediaProjectionManager;
    public static final String TAG = "EMMMainActivity";
    private ComponentName mAdminName = null;
    public static int     androidv = 0;//add by gwb;2020.9.24

    public static boolean   g_bUseHuaWeiMDM = false;//add by gwb;
    public static String   szVersionNum = "";//add by gwb;

    //-----add by gwb;2021.2.23 用作隐藏下拉菜单
    CustomViewGroup view ;
    public static final String STATUS_BAR_HEIGHT = "status_bar_height";
    public static final String DIMEN = "dimen";
    public static final String DEF_PACKAGE = "android";
    //---------------------end.
    PageFragmentAdpter fragmentAdpter;

    ViewPager view_pager;
    RelativeLayout activity_main;
    LinearLayout linearLayout;

    List<AppFragment> fragmentList;
    List<List<ResolveInfo>> allPage;

    private MonetHub netBroadcastReceiver;

    private WifiHub wifiReceive;

    public static MainActivity getInstance() {
        return mainActivity;
    }
    public Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            //super.handleMessage(msg);
            switch (msg.what) {

                case 1:
                    NetDataHub.get().setCanReflashDesk(true);
                    //refreshPageAndOffen();del by fsy 20220726 如果当前已经是桌面，应该立刻刷新    此处需要修改
                    break;
                case 2:
                    removeViewPager();
                    break;
                case 3:
                    Tell.toast((String) msg.obj, getApplicationContext());
                    break;
                case 4:
                    showDialog(MainActivity.this,(String)msg.obj);
                    break;
                case 5:
                    Toast.makeText(MainActivity.this, "当前已是最新版本，无需升级！", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(MainActivity.this, "服务器没有升级包!", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(MainActivity.this, "辅助功能尚未开启,无法进行控制!", Toast.LENGTH_LONG).show();
                    break;
                case 8:
                    Toast.makeText(MainActivity.this, "远程功能需先获取截屏权限", Toast.LENGTH_LONG).show();
                    break;
                case 9:
                    Toast.makeText(MainActivity.this, "请将EMM显示在其他应用上层设为允许", Toast.LENGTH_LONG).show();
                    break;
                case 10:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startMediaProjection();
                    }
                    break;
                case 11:
                    updateApk(MainActivity.this,(String)msg.obj);
                    break;
                case 12:
                    wakeAndUnlock(MainActivity.this);
                    break;
                case 13:
                    Toast.makeText(MainActivity.this,"请在电池优化中将EMM设置为不优化",Toast.LENGTH_LONG).show();
                    break;
                default:
            }

        }
    };

    /**
    * @author  fsy
    * @date    2022/1/11 10:06
    * @return
    * @description  有些指令需要的通知亮屏
    */
    @SuppressLint("InvalidWakeLockTag")
    private void wakeAndUnlock(Context c)
    {
        try{
            PowerManager pm;
            PowerManager.WakeLock wl;
            pm=(PowerManager) c.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            //点亮屏幕
            wl.acquire();
            Log.w("EMMScreen", "点亮");
        }catch (Exception e){
            Log.e("EMMScreen", "wakeAndUnlock--error "+e.toString());
        }
    }

    void initView()
    {
        view_pager = findViewById(R.id.view_pager);

        activity_main = findViewById(R.id.activity_main);
        linearLayout = findViewById(R.id.linearLayout);

        fragmentList = new ArrayList<>();
        allPage = new ArrayList<>();

    }

    void initData()
    {
        try{
        //----add by gwb;2020.9.16  数据初始化放在最前面。
            NetDataHub.init(MainActivity.this);

            NetCtrlHub.init(MainActivity.this, handler);
            //------------------

            CPackOperate.init(MainActivity.this, handler);

            AppDataHub.init(getApplicationContext());
            OffenApp.init(this, handler);

            AppSetting.setHandler(handler);

            Log.w(TAG,"注册WifiHub");
            wifiReceive = new WifiHub();
            //wh.closeWifiAp(this);//add by gwb;2021.2.20 测试禁用热点。
            this.registerReceiver(wifiReceive, new IntentFilter("android.net.wifi.RSSI_CHANGED"));

        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }

    void removeViewPager()
    {
        view_pager.removeAllViews();
    }

    void refreshPageAndOffen()
    {
        NetDataHub.get().addLog("refreshPageAndOffen------刷新桌面.");
        Log.w("EMMMain","------刷新桌面----begin.");
        try {

            /*不能随便刷新
            if(!NetDataHub.get().CheckAppHaveChange()&&!NetDataHub.get().getCanReflashDesk() ){
                NetDataHub.get().addLog("refreshPageAndOffen------APP没有变化，不刷新.");
                return ;
            }
            */
            if(!NetDataHub.get().getCanReflashDesk())
                return;
            Tell.toast("刷新桌面！",getApplication());
            NetDataHub.get().setCanReflashDesk(false);

            removeViewPager();
            AppDataHub.loadApps();
            allPage = AppDataHub.getAppPage();
            fragmentList.clear();
            Log.w("EMMMain","几页屏:"+allPage.size()+"  显示APP总数:" + AppDataHub.getShowAppCount());


            for (int i = 0; i < allPage.size(); i++) {

                fragmentList.add(new AppFragment(getApplicationContext(), allPage.get(i), i));
            }

            fragmentAdpter = new PageFragmentAdpter(getSupportFragmentManager(), fragmentList);//getSupportFragmentManager

            view_pager.setAdapter(fragmentAdpter);
            if (fragmentList.size() > 3) {
                view_pager.setOffscreenPageLimit(3);
            }

            OffenApp.load(AppDataHub.getOffen());
            NetDataHub.get().addLog("refreshPageAndOffen-----刷新桌面----End.");
        }
        catch(Exception e) {
            NetDataHub.get().addLog("refreshPageAndOffen------异常-----:"+e.getMessage());;
        }
    }

    void initPageAndOffen()
    {
        try{
            AppDataHub.loadApps();
            allPage = AppDataHub.getAppPage();

            Log.i("EMMmain","initPageAndOffen------allPage.size():"+allPage.size());

            for (int i = 0; i < allPage.size(); i++)
            {
                fragmentList.add(new AppFragment(getApplicationContext(), allPage.get(i), i));
            }
            fragmentAdpter = new PageFragmentAdpter(getSupportFragmentManager(), fragmentList);
            view_pager.setAdapter(fragmentAdpter);

            OffenApp.load(AppDataHub.getOffen());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void initNetData()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (NetCtrlHub.get().GetServerPolicy())
                {
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        try {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    // 只要有一个权限没有被授予, 则直接返回 false

                    NetDataHub.get().addLog("checkPermissionAllGranted----权限["+permission+"]没有授权");
                    //System.out.println("checkPermissionAllGranted----权限["+permission+"]没有授权");
                    Log.w(TAG,"checkPermissionAllGranted----权限["+permission+"]没有授权");
                    return false;
                }
                else{
                    NetDataHub.get().addLog("checkPermissionAllGranted----权限["+permission+"]已授权");
                    Log.w(TAG,"checkPermissionAllGranted----权限["+permission+"]已授权");
                    //System.out.println("checkPermissionAllGranted----权限["+permission+"]已授权");
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "checkPermissionAllGranted error:"+e.getMessage());
            return false;
        }
        return true;
    }
    public void checkPermission() { //add by gwb;2020.10.13
        try {
            //-----add by gwb;2020.11.25 增加地图支持
            String[] PermissionString = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.SET_WALLPAPER,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION

                    };
            //---------------end.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Build.VERSION.SDK_INT是获取当前手机版本 Build.VERSION_CODES.M为6.0系统
                //if (targetSdkVersion >= Build.VERSION_CODES.M) {
                //第 1 步: 检查是否有相应的权限
                boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                if (isAllGranted) {
                    Log.w(TAG,"checkPermission----所有权限已经授权");
                    return;
                }
                Log.w(TAG,"checkPermission----弹出权限申请:" + PermissionString.toString());
                // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
                this.requestPermissions( PermissionString, 123);//一定要用下面的，否则回调收不到通知。
                //}

                /*
                if (!Settings.System.canWrite(this)) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + this.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                } else {
                    //有了权限，具体的动作

                }
                 */

            }
        }
        catch(Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void SetDesktopBackground()
    {//add by gwb;2020.10.13

        try {
            int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
                //拥有权限，执行操作
                WallpaperManager wm = WallpaperManager.getInstance(this);
                Drawable wallPaper = wm.getDrawable();
                activity_main.setBackground(wallPaper);//一定要WRITE_EXTERNAL_STORAGE这个权限。
                view_pager.getBackground().setAlpha(0);
                linearLayout.getBackground().setAlpha(0);
            } else {
                //没有权限，向用户请求权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static boolean isEMUI() {
        //emuiApiLevel>0 即华为系统
        int emuiApiLevel = 0;
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getDeclaredMethod("get", String.class);
            emuiApiLevel = Integer.parseInt((String) method.invoke(cls, new Object[]{"ro.build.hw_emui_api_level"}));//string为"",报错捕获
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emuiApiLevel > 0;
    }

    //利用反射获取是否拥有悬浮框权限
    public static boolean getAppOps(Context context) {
        try {
            Object object = context.getSystemService(context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            NetDataHub.get().addLog("EMMmain------获取悬浮框权限error:"+ ex.toString());
        }
        return true;    //如果报错了，即不能成功获取，那就别一直弹出悬浮框申请页面了；
    }

    //自定义颜色，add by fsy 2022.2.17
    /**
    * @author  fsy
    * @date    2022/4/14 18:32
    * @return
    * @description  合并了方法，设置颜色
    */
    public void setMainColor()
    {
        try{
            DeviceInfoUtil.getTextColor();//获取一下主题文字颜色格式
            if(EMMApp.getInstance().textColor!=-1){
                TextView offen1 = findViewById(R.id.offen_text1);
                TextView offen2 = findViewById(R.id.offen_text2);
                TextView offen3 = findViewById(R.id.offen_text3);
                TextView offen4 = findViewById(R.id.offen_text4);

                offen1.setTextColor(EMMApp.getInstance().textColor);
                offen2.setTextColor(EMMApp.getInstance().textColor);
                offen3.setTextColor(EMMApp.getInstance().textColor);
                offen4.setTextColor(EMMApp.getInstance().textColor);
                Log.w("EMMMain", "textColor1:"+EMMApp.getInstance().textColor);
            }
        }catch (Exception e){
            Log.e("EMMMain","e:"+e.toString());
        }
    }
 /*   @Override
    public boolean moveTaskToBack(boolean nonRoot){
        return false;
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        mainActivity = this;
        Log.w("EMMMain","-------onCreate()" );

        //------------add by gwb;2020.9.23
        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, DeviceReceiver.class);
        szVersionNum = getVersionName(this);

       // DeviceInfoUtil.setTextColor();//设置一下主题文字颜色,注意一下这里

        String szRelease = Build.VERSION.RELEASE;
        if(szRelease.length()>1)
            szRelease = szRelease.substring(0,2);
        else
            szRelease = szRelease.substring(0,1);
        szRelease = szRelease.replace(".","");
        androidv =Integer.parseInt(szRelease);

  		setContentView(R.layout.android_app);
        setMainColor();//add by fsy 2022.4.14
 //     startService(new Intent(this, TaskThink.class));
		initData();
        initView();
        initPageAndOffen();
        //initNetData();  del by gwb;2020.9.17  不需用，在后面定时器里处理。

        DeviceInfoUtil.initDeviceInfo();
        NetDataHub.get().addLog("版本号:"+szVersionNum);
        mReceiver = ActionReceiver.getInstance();
        mReceiver.setHandler(handler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.grampus.hualauncherkai.action.STOP_RECEIVER");
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");//add by gwb;2021.2.20  增加热点变化通知。
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);//add by fsy;2021.12.23  增加亮屏通知
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);//add by fsy;2021.1.13    解锁
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);//add by fsy;2021.1.14    黑屏
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);//add by fsy;2021.1.14    系统框（关机重启框）
        registerReceiver(mReceiver, intentFilter);


        /**/
        /**
         * 2020.04.10 未来修改
         * 设置状态栏颜色
         */
        //setStatusBarColor(this, R.color.brightBlue);
        //setNavigationBarColor(this, R.color.dockColor);

        /**
         * 2020.04.14 未来修改
         * 进行一些系统的设置
         */
        /* del by gwb;
        appUtils au = new appUtils(this);
        au.appControl();
        */
        /**
         * 2020.04.26 未来修改
         * 移动网络注册监听
         */

        /* del by gwb;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            //实例化IntentFilter对象
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netBroadcastReceiver = new MonetHub();
            //注册广播接收
            registerReceiver(netBroadcastReceiver, filter);
        }
        */


        /**
         * 2020.06.23 未来修改
         * 更改了壁纸的设置，需要动态申请读写权限
         * 设置程序不允许卸载
         */

        /* del by gwb;2020.7.20
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED)
        {
            //拥有权限，执行操作
            WallpaperManager wm = WallpaperManager.getInstance(this);
            Drawable wallPaper = wm.getDrawable();
            activity_main.setBackground(wallPaper);
            view_pager.getBackground().setAlpha(0);
            linearLayout.getBackground().setAlpha(0);
        }
        else
        {
            //没有权限，向用户请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            System.out.println("请求权限");
        }
        devicePolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, DeviceReceiver.class);
        devicePolicyManager.setUninstallBlocked(componentName, "com.grampus.hualauncherkai", true);
        */
       // View view=getWindow().getDecorView().findFocus();

        //--------add by gwb;2020.10.8
        try {

            String manufacturer = Build.MANUFACTURER;
            if (manufacturer != null && manufacturer.length() > 0) {
                manufacturer = manufacturer.toLowerCase();
            }
            if (androidv >= 9 && (isEMUI() || manufacturer == "huawei")) {//9.0以上华为才需要

                //new SampleEula(this, mDevicePolicyManager, mAdminName).show();

                g_bUseHuaWeiMDM = true;
            }
            //           String version = VersionInfo.getApiVersion();//华为的SDK里面的


            new SampleEula(this, mDevicePolicyManager, mAdminName).show();

            checkPermission();




            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SetDesktopBackground();
            }

            //----------add by gwb;2021.2.25
            /*
            try {
                location = MyRawLocation.getInstance(MainActivity.this).showLocation();
                if (location != null) {
                    String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
                    Log.d("FLY.LocationUtils", address);
                    NetDataHub.get().addLog("MainActivity___开机就获得GPS："+address);
                }
                else
                {
                    NetDataHub.get().addLog("MainActivity___开机就获得GPS失败.");
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            Log.d(TAG, "-----------end.");
             */
            //-----------end.

           // preventStatusBarExpansion(this);


            String deviceBrand= Build.BRAND + "-" + Build.MODEL;
            NetDataHub.get().addLog("BRAND:"+deviceBrand);
            if(EMMApp.getInstance().HONOR_PAD_V7.equalsIgnoreCase(deviceBrand)) {
                EMMApp.getInstance().controlType = 1;
                NetDataHub.get().addLog("EMM-main------荣耀平板V7");
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startMediaProjection(){
        try {
            Log.w(TAG, "main--------startMediaProjection");
            if(EMMApp.getInstance().resultData == null)
            {
                Log.w("EMMSCreen", "main----弹出申请 isAutoAgree设置为true");
                EMMAccessibilityService.getInstance().isAutoAgree = true;
                this.mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 300);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EMMAccessibilityService.getInstance().agreeScreenCast();
                    }
                }).start();
            }
        }catch (Exception e) {
            Log.e(TAG, "startMediaProjection---error: "+e.toString() );
        }

    }

    @Override
    protected void onResume() { //add by fsy 2021.9.20
        super.onResume();   //注掉测返回无app
        if(NetDataHub.get().isCtrlWifi()){
            WifiHub.wifiThink(EMMApp.getInstance().mainContext);
        }
        g_bAllowSetting = false;
        loadDesk();
    }

    private void loadDesk() {

       Log.w("EMMMain","onResume--页数:"+allPage.size()+"  显示APP总数:" + AppDataHub.getShowAppCount());
        //需要刷新桌面
        if(NetDataHub.get().getCanReflashDesk()){
            refreshPageAndOffen();
        }

        try{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //小于安卓6.0,系统会默认打开悬浮框权限

                if(getAppOps(getApplicationContext()))
                {
                    //        NetDataHub.get().addLog("EMMmain------已拥有悬浮框权限");//测试1
                    g_bAllowSetting = false;

                    //     handler.sendEmptyMessage(9);
                }else
                {
                    NetDataHub.get().addLog("EMMmain------未获得悬浮框权限---请求开启");
                    // WifiHub.isWhiteWifi = false;    //该变量的作用是控制辅助服务不会立刻屏蔽设置
                    g_bAllowSetting = true;

                    //请将EMM显示在其他应用上层设为运行
                    handler.sendEmptyMessage(9);
                    if (!Settings.canDrawOverlays(this)) {
                        NetDataHub.get().addLog("main------1");
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //    startActivityForResult(intent, 10);
                    }

                }

            }

    /*
            //控件生成时Activity可能还没有完成加载
            // https://blog.csdn.net/sdsxtianshi/article/details/78530491/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Log.e("WifiHub", "111111111111---------onResume" );
                EMMAccessibilityService.getInstance().getWiFiNow(getApplicationContext());
            }
    */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(EMMAccessibilityService.isStart())
                {
                    if(g_bAllowSetting){
                        //                  EMMAccessibilityService.getInstance().autoAgree();    不好用，以后再好好改
                    }
                    startMediaProjection();
                }
            }
            //runningActivity
            NetDataHub.get().addLog("getDesktopPackageName:"+getDesktopPackageName(this));
            if(getPackageName().equals(getDesktopPackageName(this))){
                EMMApp.getInstance().settingDesktop = true;
                Log.e(TAG, "onResume-----是默认桌面"+getDesktopPackageName(this) );
            }else
            {
                EMMApp.getInstance().settingDesktop = false;
                Log.e(TAG, "onResume-----不是默认桌面"+getDesktopPackageName(this) );
            }
        }  catch (Exception e) {
            Log.e(TAG, "onResume---error: "+e.toString() );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            Log.e("EMMMain","onDestroy-------------释放！！");

            unregisterReceiver(mReceiver);
            unregisterReceiver(wifiReceive);//add by gwb;2021.4.15
            //unregisterReceiver(netBroadcastReceiver);//del by gwb;2021.4.15

            //------------------------add by gwb;2021.4.15  安卓小于8.0时才这样处理,淮安医院PDA在插上4G卡的时候会自动走到onDestroy，导致桌面图标全没了，所以这里先重启解决。
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            {

                Log.e("EMMMain","onDestroy-------------重启进程！！");
                System.out.println("onDestroy------------------重启进程！！");
                finish();
                Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

                mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(mHomeIntent);

                stopself();
            }
            Log.e("EMMMain","onDestroy-------------end！！");
        }
        catch (Exception e)
        {
            Log.e("EMMMain","onDestroy-error:"+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
//        super.onBackPressed();
//        handler.sendEmptyMessage(1);
    }

    private void stopself()
    {
        System.out.println("stopself-----自杀进程 ！！！！");
  //      stopService(new Intent(this, TaskThink.class));
        finish();
        Process.killProcess(Process.myPid());
    }
    /**
     * 获取版本号
     *
     * @throws PackageManager.NameNotFoundException
     */
    public String getVersionName(Context context) {
        String version = "";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
//            Log.w(TAG,"getVersionName---"+packInfo.versionName);
        }
        catch(Exception e){
            Log.w(TAG,"getVersionName---error:"+e.toString());
        }
        return version;
    }

    public  void showDialog(final Activity activity, final String szServerVersion){//add by gwb;
        try {

            VersionInfoBean versionInfoBean;

            Log.w("EMMA11y","showDialog---当前服务器上版本与本地不同，提示需要升级");
            NetDataHub.get().addLog("showDialog---当前服务器上版本与本地不同，提示需要升级："+szServerVersion);

            versionInfoBean = new VersionInfoBean("1.1.1",
                    "http://11.apk",
                    "有新版本更新，请点击安装开始升级！",
                    "/1.1.1.jpg");

       //     final Dialog dialog = new Dialog(activity);   改用全局唯一的窗口
            if(g_dialog == null){
                g_dialog = new Dialog(activity);
            }

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView version, content;
            Button left, right;
            View view = inflater.inflate(R.layout.version_update, null, false);
            version = view.findViewById(R.id.version);
            content = view.findViewById(R.id.content);
            left = view.findViewById(R.id.left);
            right = view.findViewById(R.id.right);
            Log.w("EMMA11y","showDialog-----1");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                content.setText(Html.fromHtml(versionInfoBean.getDesc(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                content.setText(Html.fromHtml(versionInfoBean.getDesc()));
            }
            content.setMovementMethod(LinkMovementMethod.getInstance());
            version.setText("存在新版本： " + szServerVersion);
            g_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    g_dialog.dismiss();
                }
            });
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    g_dialog.dismiss();

                    updateApk(activity,szServerVersion);//add by fsy 2021.12.24 新的下载更新APK方式
                //    downloadNewVersionFromServer(activity,szServerVersion);

                }
            });
//            Log.w("EMMA11y","showDialog-----2");
            g_dialog.setContentView(view);
            g_dialog.setCancelable(false);
            Window dialogWindow = g_dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);
            //dialogWindow.setWindowAnimations(R.style.ActionSheetDialogAnimation);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            WindowManager wm = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
            //lp.width =wm.getDefaultDisplay().getWidth()/10*9;
 //           Log.w("EMMA11y","showDialog-----3");
            Rect size = new Rect();
            wm.getDefaultDisplay().getRectSize(size);
            lp.width = size.width()/ 10 * 9;
            dialogWindow.setAttributes(lp);
            g_dialog.show();
            Log.w("EMMA11y","showDialog-----4");
            //Test   如果没有点掉就一直show，准备加参数让唯一
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
    * @author  fsy
    * @date    2021/12/24 14:30
    * @return
    * @description  新的HTTP下载方式，以前的方式在一家客户华为平板上不启动下载
    *               未找出原因，更换下载方式。绑定一个进度框。
    */
    public void updateApk(Activity activity,String szServerVersion) {
        try
        {
            wakeAndUnlock(MainActivity.this);
            if(EMMApp.getInstance().screenLock)
            {
                Log.i(TAG, "锁屏时不下载安装");
                return;
            }
            String ServerIP = NetCtrlHub.get().getServiceAd();
            String urlStr = "http://" + ServerIP + "/teldown/encrypttemp/EMM-Update/" + szServerVersion + ".apk";

            String fileName = szServerVersion + ".apk";

            Log.w(TAG,"updateApk---url:"+urlStr);
            //  String rootPath = Environment.DIRECTORY_DOWNLOADS;

            if (FileManagerUtil.hasSdcard()) {
                String szDirPath = activity.getExternalFilesDir("Download").getPath();
                File fileDir = new File(szDirPath);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                FileManagerUtil.deleteFolderFile(szDirPath, false);

                Log.w(TAG,"updateApk--------2");

                ProgressDialog pd = new ProgressDialog(activity);
                pd.setTitle(fileName);
                //设置对话进度条样式为水平
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setMessage("正在更新EMM下载中......");
                //设置对话进度条显示在屏幕顶部
                pd.getWindow().setGravity(Gravity.TOP);
                pd.setMax(100);
                pd.show();//调用show方法显示进度条对话框
                //pd.setCancelable(false);  //强制窗口显示不可取消
                Log.w(TAG,"updateApk--------3");
                HttpDownloaderThread httpDownloader = new HttpDownloaderThread(urlStr, szDirPath,fileName,pd);
                httpDownloader.start(); //如果正在下载，再次点击会弹出进度条
                Log.w(TAG,"updateApk--------4");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if(EMMAccessibilityService.isStart()){
                        EMMAccessibilityService.getInstance().isUpdateApk = true;
                    }
                }
            }

        } catch (Exception e){
            NetDataHub.get().addLog("updateApk-----error:"+e.toString());
        }
    }
    /**
     * 启动服务后台下载
     */
    static public void downloadNewVersionFromServer(Activity activity,String szServerVersion){//add by gwb;2020.10.20
      //  if(new File(versionInfoBean.getPath()).exists()){
      //      new File(versionInfoBean.getPath()).delete();
      //  }

        try {

            String rootPath = Environment.DIRECTORY_DOWNLOADS;

            if (FileManagerUtil.hasSdcard()) {
                String szDirPath = activity.getExternalFilesDir("Download").getPath();
                File fileDir = new File(szDirPath);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                FileManagerUtil.deleteFolderFile(szDirPath, false);

                final File file = new File(szDirPath + "/" + szServerVersion + ".apk");

                String szServerIP = NetCtrlHub.get().getServiceAd();

                String url = "http://"+szServerIP+"/teldown/encrypttemp/EMM-Update/" +szServerVersion+".apk";

                //String urlStr = null;
                //urlStr = URLEncoder.encode(url,"utf-8") ;这边不需要用。

                NetDataHub.get().addLog("Download-----开始下载升级包:"+url);

                DownloadFile df = new DownloadFile();

                df.Download(url, rootPath, activity, null,null);
                NetDataHub.get().addLog("Download-----下载升级包结束:"+url);
            } else {
                Toast.makeText(activity, "请插入SD后才能升级！", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限已经都通过了，可以下载apk到SDk中了
                    Toast.makeText(this, "权限已经都通过了！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有申请权限
                    Toast.makeText(this, "申请权限没有全部通过！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值

        if (requestCode == 777) {
            NetDataHub.get().setHuaWeiDesktop(this);//add by gwb;2020.10.13
        }
        else if (requestCode == 300) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (resultCode != Activity.RESULT_OK){  //没点确定就算了。不做再次弹出申请的操作
//                    EMMApp.getInstatnce().resultData = null;
                    //startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 300);
                    Log.i(TAG, "User didn't allow.requestCode"+requestCode);
                } else {
                    Log.w("EMMScreen", "Starting screen capture requestCode"+requestCode+"data:"+data.toString());
                    // Intent inten = new Intent
                    EMMApp.getInstance().resultCode = resultCode;
                    EMMApp.getInstance().resultData = data; //其他可赋值，主要在于data
                    Save.fileSave(data, EMMApp.getInstance().mainContext, "resultData");
                    Log.w("EMMA11yService", "Starting screen capture requestCode"+requestCode+"data:"+data.toString());


                    //--------返回到后台，代码移动到这----
                    if(EMMApp.getInstance().settingDesktop == false)
                    {
                        if(moveTaskToBack(true)){
                            Tell.toast("自动返回到后台", getApplicationContext());
                        }else
                        {
                            Log.w(TAG, "MyAccessubility---moveTaskToBack返回false" );
                        }

                    }

                }
            }
        }

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        // TODO Auto-generated method stub
  //      disableStatusBar();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.w(TAG, "MainActivity------onNewIntent");    //测试下哪些时候走到这里，通过start重建activity时会走
        super.onNewIntent(intent);
        setIntent(intent);//在多次启动同一个栈唯一模式下的activity时，在onNewIntent()里面的getIntent()得到的intent感觉都是第一次的那个数据。
                            // 会返回第一个intent的数据。setIntent()将最新的intent设置给这个activity实例。
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        NetDataHub.get().setCanReflashDesk(true);
        Log.w(TAG, "MainActivity-----横竖屏切换，状态：" + newConfig.orientation);
        if(NetDataHub.get() != null){
            NetDataHub.get().addLog("横竖屏切换，状态：" + newConfig.orientation );
        }
    }

    public static String getDesktopPackageName(Context context) {
        try {
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
            if (res.activityInfo == null) {
                // should not happen. A home is always installed, isn't it?
                return null;
            }
            if (res.activityInfo.packageName.equals("android")) {
                // 有多个桌面程序存在，且未指定默认项时；
                return null;
            } else {
                return res.activityInfo.packageName;
            }
        }catch (Exception e) {
            Log.w(TAG,"getDesktopPackageName error:"+e.toString());
        }
        return "";
    }

/*	无系统权限无效
    public void disableStatusBar(){
       // Object service = getSystemService(Context.STATUS_BAR_SERVICE);//STATUS_BAR_SERVICE是系统api
       // Method test2 = statusbarManager.getMethod("collapsePanels");
        try {
            Object service = getSystemService("statusbar");//STATUS_BAR_SERVICE是系统api
            Log.i("EMM", "disableStatusBar-------------");
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod("disable", int.class);
            //判断版本大小
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                expand.invoke(service,0x00010000);//View.STATUS_BAR_DISABLE_EXPAND     0x00010000
         //   }else {
         //       expand.invoke(service, DISABLE_EXPAND_LOW);
            }
        } catch (Exception e) {
            Log.e("EMM", "disableStatusBar--error:"+e.toString());
            e.printStackTrace();
        }
    }
*/



    public void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager)context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        int resId = context.getResources()
                .getIdentifier(STATUS_BAR_HEIGHT, DIMEN, DEF_PACKAGE);
        int result;
        if (resId > 0) {
            result = context.getResources()
                    .getDimensionPixelSize(resId);
        } else {
            // Use Fallback size:
            result = 60; // 60px Fallback
        }

        localLayoutParams.height = result;
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        if (view == null) {
            view = new CustomViewGroup(context);
        }

        try {
            if (manager != null) {
                manager.addView(view, localLayoutParams);
            }
        } catch (Exception ignored) {
        }
    }
    public void allowStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));
        try {
            if (manager != null) {
                manager.removeViewImmediate(view);
            }
        } catch (Exception ignored) {
        }
    }


    class CustomViewGroup extends ViewGroup {
        public CustomViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed,
                                int l,
                                int t,
                                int r,
                                int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            // Intercepted touch!
            return true;
        }
    }
}

/*  add by gwb;
adb shell dpm set-device-owner com.grampus.hualauncherkai/.Tools.DeviceReceiver

高德地图使用：
https://console.amap.com/dev/key/app
帐号为:75954194@qq.com
对应的KEY为：44f375049fe955bbeb7fb56315d482ab
发布安全码SHA1：8A:76:E0:CA:0D:A0:E5:25:E9:57:8D:C2:61:03:90:A0:FC:5E:E2:2C
调用安全码SHA1:7E:AD:FD:B7:70:0E:A1:BE:BB:84:84:36:9A:33:DD:41:FD:42:77:FA
包名称一定要是com.grampus.hualauncherkai
 */