package com.grampus.hualauncherkai.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.AndroidPolicy;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.FloatWindow.EMMFloatWindowService;
import com.grampus.hualauncherkai.Receiver.WifiHub;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.UI.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.grampus.hualauncherkai.R.mipmap.ic_launcher;
import static com.grampus.hualauncherkai.UI.MainActivity.g_bAllowSetting;
import static com.grampus.hualauncherkai.UI.MainActivity.g_bUseHuaWeiMDM;

/**
 * 这个服务属于系统级别辅助服务 需要在设置里去手动开启 和平常app里
 * 经常使用的service 是有很大不同的 非常特殊
 * add by fsy 2021.8.8
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class EMMAccessibilityService extends AccessibilityService {
    public EMMAccessibilityService() {
    }

    private static EMMAccessibilityService mService;

    public static synchronized EMMAccessibilityService getInstance(){
        if(mService == null){
            mService = new EMMAccessibilityService();
        }
        return mService;
    }

    private final String TAG = "EMMA11yService";
    private void setForeground() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //--------此处是以前写的，这段应该不需要才对，2022.12.18
                NotificationChannel channel = new NotificationChannel("Foreground_Service",
                        "Foreground_Service", NotificationManager.IMPORTANCE_LOW);
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager == null) {
                    return;
                }
                manager.createNotificationChannel(channel);
                //----------
                Notification notification =
                        new NotificationCompat.Builder(this, "Foreground_Service")
                                .setContentTitle("阳途安卓辅助功能")
                                .setContentText("正在运行中")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), ic_launcher))
                                .build();
                startForeground(11, notification);
            }
        }catch (Exception e) {
            Log.e(TAG, "setForeground------error:"+e.toString() );
        }
    }

    private void readNACAndConnect() {

        //测试 在服务中 读取文件中的策略      //测试无问题
        Object NACAddrObj = Save.readFile(this, "NACAddr");  //add by fsy 2021.12.20
        if(NACAddrObj != null)
        {
            AndroidPolicy.NACAddr0 = (String) NACAddrObj;
            Log.w(TAG, "==NACAddr0 = "+AndroidPolicy.NACAddr0);
        }

        Object resultData = Save.readFile(this, "resultData");  //add by fsy 2021.12.20
        if(resultData != null)
        {
            EMMApp.getInstance().resultData = (Intent) resultData;
            Log.w(TAG, "==resultData = "+EMMApp.getInstance().resultData);
        }

    }
    public void openMainActivity () {

        if(EMMApp.getInstance().screenLock)
        {
            Log.i(TAG, "锁屏时跳转不开启");
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//FLAG_ACTIVITY_NEW_TASK如果栈中已存在改activity，启动不了
        /*

//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.w(TAG,"----openMainActivity----");
        if(NetDataHub.get()!=null)
            NetDataHub.get().addLog("EMMLogonSock---收到屏幕监视消息444open");
        */
        startActivity(intent);
    }

    /**
     * AccessibilityService 这个服务可以关联很多属性，这些属性 一般可以通过代码在这个方法里进行设置，
     * 如果很复杂比如需要初始化广播之类的工作 都可以在这个方法里写。
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.w(TAG, "EMMAccessibilityService---onServiceConnected---- " );
        setForeground();
        mService = this;
        openMainActivity();

        readNACAndConnect();  //显注掉，几次辅助服务起来后面就无效了，不知是否这里原因
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "EMMAccessibilityService---onStartCommand---- " );
        return super.onStartCommand(intent, flags, startId);
    }

    public static boolean isStart() {
        return mService != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "EMMAccessibilityService---onCreate--");
    }
    @Override
    public void onInterrupt() {
        Log.i(TAG, "EMMAccessibilityService---onInterrupt--" );
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.w(TAG, "EMMAccessibilityService--------onUnbind-- ");
        mService = null;

        stopForeground(true);

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MyAccessibilityService------onDestroy ------ ");
        mService = null;
        stopForeground(true);
    }


    public boolean isAutoAgree = false;
    /**
     * 当你这个服务正常开启的时候，就可以监听事件了，当然监听什么事件，监听到什么程度 都是由给这个服务的属性来决定的，
     * 监控时不需要用这个监听，我会主动发起调用
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        try{
               switch (event.getEventType()) {
                //typeNotificationStateChanged|typeWindowStateChanged|typeWindowContentChanged
                //原来是TYPE_WINDOWS_CHANGED
                //TYPE_VIEW_CLICKED  TYPE_VIEW_TEXT_CHANGED
/*
                case AccessibilityEvent.TYPE_ANNOUNCEMENT://TYPE_VIEW_CLICKED 有些设备的默认桌面也是有这个格式的，导致在默认桌面上不能滑动
                    NetDataHub.addLog1("forbidUI----TYPE_ANNOUNCEMENT");
                    if(NetDataHub.get() != null) {
                        if (!NetDataHub.get().isProtectSetting())
                            return;
                    }
                    for (int i = 0; i < 5 ; i++) {
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                        Thread.sleep(100);
                    }
                    return;

                case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_VIEW_HOVER_ENTER");
                case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                    NetDataHub.addLog1("forbidUI----TYPE_VIEW_HOVER_EXIT");
                case AccessibilityEvent.TYPE_VIEW_SELECTED://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_VIEW_SELECTED");
                case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_VIEW_HOVER_EXIT");
                case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_TOUCH_EXPLORATION_GESTURE_END");

                case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY");
                case AccessibilityEvent.TYPE_GESTURE_DETECTION_START://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_GESTURE_DETECTION_START");
                case AccessibilityEvent.TYPE_GESTURE_DETECTION_END://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_GESTURE_DETECTION_END");
                case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_TOUCH_INTERACTION_START");
                case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END://TYPE_VIEW_CLICKED
                    NetDataHub.addLog1("forbidUI----TYPE_TOUCH_INTERACTION_END");

                case AccessibilityEvent.TYPE_VIEW_CLICKED://TYPE_VIEW_CLICKED

                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://TYPE_VIEW_CLICKED  //typeAllMask
 */          //     case AccessibilityEvent.TYPES_ALL_MASK:
                    //Test 测试，测试在mainActivity不在活动的情况下
              //      testBack("打印");

                    //     findEMMClick(getRootInActiveWindow(),"EMM");
                case AccessibilityEvent.TYPE_WINDOWS_CHANGED://TYPE_VIEW_CLICKED
                    if(isAutoAgree)
                    {
                        Log.i(TAG, "isAutoAgree = true ");
                        return;
                    }
                    if(g_bUseHuaWeiMDM)
                    {
                        Log.i(TAG, "华为==================== ");
                        return;
                    }

//                    Log.i(TAG, "TYPE_WINDOWS_CHANGED");
                    if(NetDataHub.get() != null){

                        if(!NetDataHub.get().isProtectSetting())
                            return;
                        if(NetDataHub.get().isCtrlApp()||NetDataHub.get().isCtrlWifi()) {
/*
*/

// 2022.11.30 六院测试
                            runForbidSystemUI();
                        }
                    }
            }
        }catch (Exception e){
            Log.e(TAG, "onAccessibilityEvent error---"+e.toString());
        }
    }

    /**
    * @author  fsy
    * @date    2022/12/6 13:38
    * @return
    * @description  方法移出来，走循环就是因为下拉界面，可以手按着不松，需要一直需要返回
    */
    private void runForbidSystemUI() {
        if(EMMApp.getInstance().controlType == 0) {


            new Thread(new Runnable() { // 匿名类的Runnable接口
                @Override
                public void run() {
                    AccessibilityNodeInfo rowNode1 = getRootInActiveWindow();
                    int i=0;
                    //如果黑屏---跳过管控

                    if(rowNode1!=null&&"com.android.systemui".equals(rowNode1.getPackageName())){

                        while (forbidSystemUI(rowNode1)&&!EMMApp.getInstance().screenLock) {
                            if(i++>500) {//防止循环太多次出故障 1000   00
                                break;
                            }
                            //---解锁再恢复，参数在 ActionReceiver 中监听改动
                            rowNode1 = getRootInActiveWindow();
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                //    Log.e(TAG, "TYPE_WINDOWS_CHANGED error---"+e.toString());
                            }
                        }
                    }
                    else
                        forbidSystemUI(rowNode1);

                }
            }).start();
        }else if(EMMApp.getInstance().controlType == 1) {
            AccessibilityNodeInfo rowNode = getRootInActiveWindow();
            forbidSystemUI(rowNode);
        }
    }

    /**
     * @author  fsy
     * @description  首先判断包名时systemui和settings相关才会进入
     * 下拉界面以wifi_combo辨认，未连接wifi时显示为"WLAN"
     * 非白名单下的wifi界面以 白名单内的wifi+android:id/title辨认
     */
    public boolean forbidSystemUI(AccessibilityNodeInfo info){
        try{
            //Log.i(TAG, "fbdText：" + info.getText()+"|"+info.getClassName()+"|"+info.getViewIdResourceName()+"|P:"+info.getPackageName());
            if(info==null)
                return false;

            //Log.i(TAG, "g_bAllowSetting|"+ g_bAllowSetting +"|");
            //锁屏界面可以通过广播状态来判断，不需要这个wifi_combo了吧//暂时还得要
            if ("com.android.systemui".equals(info.getPackageName())) {

                //Log.i(TAG, "g_bAllowSetting|"+ g_bAllowSetting +"|1111");
                //NetDataHub.addLog1("EMMService screenLock = "+EMMApp.getInstatnce().screenLock +"getViewIdResourceName = "+info.getViewIdResourceName()+"|||"+info.getText());
                //    if(!EMMApp.getInstatnce().screenLock)//麻烦，联新PDA不能用这个简单的方式  //22.9.9联想也不能走这个
                //    {
                //*2022.7.14 外星人用下面这个不好用         //联想的一定要走下面这个
                if("com.android.systemui:id/wifi_combo".equals(info.getViewIdResourceName()))
                {
                //    Log.i(TAG, "g_bAllowSetting|"+ g_bAllowSetting +"|2222");
                    //*/
                    NetDataHub.addLog1("forbidUI1----GLOBAL_ACTION_BACK");
                    backToHome();
                    return true;
                }
                //wifi未连接时候的样子 add by fsy 2022.11.28    contentEquals括号里为null是竟然还会报错，不如用equals
                else if("WLAN".equals(info.getText())&&"com.android.systemui:id/tile_label".equals(info.getViewIdResourceName()))
                {
                 //   Log.i(TAG, "g_bAllowSetting|"+ g_bAllowSetting +"|33333");
                    backToHome();
                    return true;
                }
                //*           //2022.3.2 联新PDA上，判断EMMApp.getInstatnce().screenLock，在多任务界面不行，判断有误
                for (int i = 0; i < info.getChildCount(); i++) {
                    if(forbidSystemUI(info.getChild(i)))
                        return true;
                }
//*/
            }
            else if ("com.android.settings".contentEquals(info.getPackageName())) {   //找到wifi设置界面则返回false
            //    Log.i(TAG, "getPackageName|"+ info.getPackageName() +"|");

            //    ProtectSetting(info);     //2022.12.6  11:19待测试有无异常
                if(g_bAllowSetting) {          //非白名单  或者是申请悬浮请求  if(!g_isWhiteWifi|| g_bAllowSetting)

                    if(info.getText()!=null) {
                        ProtectSetting(info);
                        return false;
                    }
//*
                    //未找到，继续遍历子窗口，无子窗口则返回true;
                    for (int i = 0; i < info.getChildCount(); i++) {
                        if(info.getChild(i)!=null){
                            if(!forbidSystemUI(info.getChild(i)))
                                return false;
                        }
                    }
//*/
/*直接允许控制
                    for (int i = 0; i < info.getChildCount(); i++) {
                        if(info.getChild(i)!=null){
                        //    Log.i(TAG, "i|"+ i +"|Count|"+ info.getChildCount());
                            if(ProtectSetting(info.getChild(i)))
                                return true;
                        }
                    }
                    NetDataHub.addLog1("-在白名单外，发现setting,返回true,继续执行,找一下有无wifi界面" );
                    //*/
                    return true;
                }
                backToHome();
                return true;//该改false吧      好像不用
            }

        }catch (Exception e){
            Log.e(TAG, "forbidSystemUI error---"+e.toString());
        }
    //    NetDataHub.addLog1("--Text：" + info.getText()+"|"+info.getPackageName()+"无发现，返回false");
        return false;
    }

    private void turnBack() {
        if(EMMApp.getInstance().controlType == 0){
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }else if(EMMApp.getInstance().controlType == 1){
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            openMainActivity();//12.5test
        }
    }

    private void backToHome() {
        if(EMMApp.getInstance().controlType == 0){
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);//2022.11.30  12.5原来从设置返回不要回桌面的
        }else if(EMMApp.getInstance().controlType == 1){
        //    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        //    performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
            openMainActivity();//12.5test
        }
    }

    //测试失败，先搁置
    private void showWarnWindow() {
        Log.e(TAG, "forbidSystemUI ----------- showWarnWindow");
     //   EMMForbidWifiWindowManager.createSysuiWarnWindow(EMMApp.getInstatnce().mainContext);
    }

    /**
    * @author  fsy
    * @date    2022/12/6 14:41
    * @description  设置检测，检测到wifi白名单内容信息，允许输入密码，所以不返回
    */
    private void ProtectSetting(AccessibilityNodeInfo info) {

       // Log.w(TAG, "ViewIdResourceName：" + info.getViewIdResourceName() + "|WifiHub.wifiSsid:" + WifiHub.wifiSsid + "|getText:" + info.getText());
       // NetDataHub.get().addLog("Text：" + info.getText() + "|" + info.getClassName() + "|" + info.getViewIdResourceName() + "|D:" + info.getContentDescription());
        //测试四台不同品牌设备，WIFI界面内部是title
        String title = (String) info.getText();
        ///*如果标题是 白名单内的 ，不返回，这是为了输密码界面
        if (CheckInWiFiList(title)) {
            if (EMMFloatWindowService.isStart())
                EMMFloatWindowService.getInstance().reqShow = false;
            //      NetDataHub.addLog1("emm发现设置wifi界面--返回fasle,不再执行" );

        } else if (WifiHub.wifiSsid.equals(title) && ("android:id/title").equals(info.getViewIdResourceName())) {

            if (EMMFloatWindowService.isStart())
                EMMFloatWindowService.getInstance().reqShow = false;
            //      NetDataHub.addLog1("emm发现设置wifi界面--返回fasle,不再执行" );
              //如果再子窗口找到了wifi名，不处理，如果遍历完都没有，则响应返回；
        } //在测试的荣耀设备上是这样，未有更多机型测试
        else if ("WLAN".equals(title) && ("android:id/action_bar_title".equals(info.getViewIdResourceName()))) {

            if (EMMFloatWindowService.isStart())
                EMMFloatWindowService.getInstance().reqShow = false;

        } else if ("显示在其他应用的上层".equals(title)) {
            NetDataHub.addLog1("emm发现显示在其他应用的上层--返回fasle,不再执行");

        } else {
            // Log.w(TAG, "---直接返回--");
            //部分横屏过宽，可以左侧选项，右侧设置，左右侧设置不同，以后再考虑
            NetDataHub.addLog1("emm未发现设置wifi界面--返回fasle,不再执行");
            backToHome();
        }
    }

    private boolean CheckInWiFiList(String wifiName) {
        try {
            if(wifiName==null||wifiName.isEmpty())
                return false;
            String wifiListString = (String) Save.readFile(EMMApp.getInstance().mainContext, "wifiList");
            JSONArray wifiList = new JSONArray(wifiListString);
            if(wifiList.length()<1) {
                return true;//未设置白名单？
            }

            for (int i = 0; i < wifiList.length(); i++)
            {
                JSONObject one = wifiList.getJSONObject(i);
                String name1=one.getString("Name1");
                if(wifiName.equals(name1))
                {
                    return true;
                }
            }
        } catch (Exception e) {
            NetDataHub.get().addLog("CheckInWiFiList error：" + e.getMessage());
        }
        return false;
    }

    /**
     * 滑动轨迹
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     * 2021.11.13 更新滑动时间由管理机传递过来
     */
    public void dispatchGesture(int x, int y,int xPos, int yPos,int nTime) {
        try {
            Path path = new Path();
            path.moveTo(x , y);
            if(xPos<15)
                xPos = 15;

            if(nTime<50)
            {
                nTime = 50;
            }
            //约束一下X,Y的范围，未知名情况下会为负，消除此影响
            xPos = xPos > 15 ? xPos:15;
            int xMax = (int)(EMMApp.getInstance().screeenWidth * EMMApp.getInstance().density)-15;
            xPos = xPos < xMax? xPos:xMax;

            yPos = yPos > 15 ? yPos:15;
            int yMax = (int)(EMMApp.getInstance().screenHight * EMMApp.getInstance().density)-15;
            yPos = yPos < yMax? yPos:yMax;

            path.lineTo(xPos, yPos);

         //   Log.w("OnReceivePack", "左击弹起：" + x + "," + y + "," + xPos + "," + yPos);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                       (path, 0, nTime)).build(), null, null);
          }
//        new AccessibilityService.GestureResultCallback(){
//            @Override
//            public void onCompleted(GestureDescription gestureDescription) {
//                super.onCompleted(gestureDescription);
//            }
//
//            @Override
//            public void onCancelled(GestureDescription gestureDescription) {
//                super.onCancelled(gestureDescription);
//            }
//        }

        }catch (Exception e) {
            Log.e(TAG, "dispatchGesture-----error:"+e.toString() );
        }
    }

    /**
     * 长按指定位置
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     */
    public void dispatchGestureLongClick(int x, int y) {
        try {

            Path path = new Path();
            path.moveTo(x - 1, y - 1);
            path.lineTo(x, y - 1);
            path.lineTo(x, y);
            path.lineTo(x - 1, y);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                        (path, 0, 800)).build(), null, null);
            }
        }catch (Exception e) {
            Log.e(TAG, "dispatchGestureLongClick-------- "+e.toString() );
        }
    }


    public boolean rycleFindClick(String... text){
        try {
            for (int i = 0; i <10 ; i++) {
                for(String temp:text)
                    if(findTextClick(getRootInActiveWindow(),temp))
                        return true;
                if(i==9) {
                  //  Log.w(TAG, "查找------" + text + "------ 失败");
                    return false;
                }
                Thread.sleep(120);
            }
            Log.w(TAG, "return------ true=" );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean isUpdateApk = false;
    public void autoUpdate() {

        // AccessibilityNodeInfo nodeInfo = getRootInActiveWindow(); //当前窗口根节点
        try
        {
            Thread.sleep(1000);
            rycleFindClick("安装","继续安装");

            Thread.sleep(1500);
            rycleFindClick("允许");

            Thread.sleep(1500);
            rycleFindClick("继续安装","安装");

            Thread.sleep(2000);
            rycleFindClick("由系统确定");
            Thread.sleep(3000);
            rycleFindClick("打开");
        }catch (Exception e){

        }
    }


    //手动写的代码循环查找子节点，不用了，用系统方法findAccessibilityNodeInfosByText吧
    public boolean autoUpdate1() {

        try {
            Thread.sleep(2000);

            for (int i = 0; i <8 ; i++) {
                if(findKey(getRootInActiveWindow(), "安装"))
                    break;
                if(i==7)
                    Log.w(TAG,"安装----- 失败");
                Thread.sleep(500);
            }

            Thread.sleep(2000);
            for (int i = 0; i <5 ; i++) {
                if(findKey(getRootInActiveWindow(), "允许"))
                    break;
                if(i==4)
                    Log.w(TAG,"允许----- 失败");
                Thread.sleep(300);
            }

            Thread.sleep(2000);
            for (int i = 0; i <20 ; i++) {
                if(findKey(getRootInActiveWindow(), "继续安装","安装"))
                    break;
                if(i==19)
                    Log.w(TAG,"继续安装----- 失败");
                Thread.sleep(300);
            }

            Thread.sleep(1000);
            for (int i = 0; i <5 ; i++) {
                if(findKey(getRootInActiveWindow(), "由系统确定"))   //淮二PDA的存储位置
                    break;
                if(i==4)
                    Log.w(TAG,"由系统确定----- 失败");
                Thread.sleep(300);
            }

        } catch (Exception e) {
            Log.e(TAG,"autoUpdate----- error"+e.toString());
        }finally {
            Log.w(TAG,"autoUpdate----- end");
            return true;
        }
    }

    public void  agreeScreenCast() {

        try{
            if(!rycleFindClick("立即开始","允许")){
                Log.w(TAG, "agreeScreenCast---继续" );
                Thread.sleep(1800);
                rycleFindClick("立即开始","允许");
            }
//Test        桌面包名部位当前包名你，说明不是默认桌面，不需要展示。

//            Log.w(TAG, "RootPackageName:"+getRootInActiveWindow().getPackageName());
//            if(getPackageName()!=null&&!getPackageName().equals(getRootInActiveWindow().getPackageName()))


        } catch (Exception e) {
            Log.e(TAG,"agreeScreenCast----- error"+e.toString());
        }finally {
            isAutoAgree = false;
            Log.w(TAG,"agreeScreenCast----- end----isAutoAgree设置为false");
        }
    }


    public boolean findKey(AccessibilityNodeInfo info,String... text) {

        if(info==null)
        {
            Log.d(TAG,"(info==null)"+text);
            return false;
        }

        Log.i(TAG, "findKeyText："+ text+"|"+ info.getText()+"|"+info.getClassName()+"|P:"+info.getPackageName());

        for(String temp:text)
        {
            if(temp.equals(info.getText()))
            {
                Log.w(TAG,"findKey --成功-- "+temp);
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            }
        }
        for (int i = 0; i < info.getChildCount(); i++) {
         if(findKey(info.getChild(i),text))
            return true;
        }
        return false;
    }

    /**
    * @author  fsy
    * @date    2022/1/11 13:22
    * @return
    * @description  做了修改，传输不定数量的Text
    */
    private boolean findTextClick(AccessibilityNodeInfo nodeInfo, String text) {

        try {
            if (nodeInfo == null)
                return false;

                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(text);

                Log.i(TAG, "findTextClick: " + text + ", "  + " ViewIdResourceName:"+nodeInfo.getViewIdResourceName());
                if (nodes == null || nodes.isEmpty())
                    return false;
                Log.i(TAG, "findTextClick: " + text + ", " + nodes.size() + ", ");
                for (AccessibilityNodeInfo node : nodes) {
                    if (node.isEnabled() && node.isClickable()) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                        Log.i(TAG, "Text：" + node.getText()+"|"+"|"+node.getViewIdResourceName()+"|P:"+node.getPackageName()+"|:"+getPackageName());
                        return true;
                    }
                }

        }catch (Exception e)
        {
            Log.e(TAG,"findTxtClick---"+text+"---error:"+e.toString());
        }
        return false;
    }


    /**
    * @author  fsy
    * @date    2022/1/7 9:17
    * @return
    * @description  自动同意开启悬浮框，设计的不好用
    */
    public void autoAgree() {

        try
        {
            Log.i(TAG, "autoAgree: EMM 悬浮框1");

            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null)
                return  ;

            rycleFindClick("EMM");
            Log.i(TAG, "autoAgree: EMM 悬浮框2");
            rycleFindClick("允许显示在其他应用上层");

            Log.i(TAG, "autoAgree: EMM" + ", "  + " ViewIdResourceName:"+nodeInfo.getViewIdResourceName());

        }catch (Exception e){

        }
    }
}