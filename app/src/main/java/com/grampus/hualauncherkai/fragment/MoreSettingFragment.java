package com.grampus.hualauncherkai.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.FloatWindow.EMMFloatWindowService;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.Tools.Tell;
import com.grampus.hualauncherkai.UI.MainActivity;
import com.grampus.hualauncherkai.UI.SettingWIFIPassword;
import com.grampus.hualauncherkai.service.EMMAccessibilityService;
import com.grampus.hualauncherkai.util.DeviceInfoUtil;
import com.grampus.hualauncherkai.util.WifiAutoConnectManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;

import static com.grampus.hualauncherkai.UI.MainActivity.g_bAllowSetting;

public class MoreSettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    private Preference go_open_a11y;
    private Preference obtain_screencast;
    private Preference set_text_color;
    private Preference connect_white_wifi;
    private MediaProjectionManager mediaProjectionManager;
    private Preference btn_power_manage;

    public MoreSettingFragment() {

    }


    public static MoreSettingFragment newInstance(String param1, String param2) {
        MoreSettingFragment fragment = new MoreSettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.fragment_more_setting);
        go_open_a11y = findPreference("go_open_a11y");
        obtain_screencast = findPreference("obtain_screencast");
        set_text_color = findPreference("set_text_color");
        connect_white_wifi = findPreference("connect_white_wifi");
        btn_power_manage = findPreference("btn_power_manage");

        go_open_a11y.setOnPreferenceClickListener(this);
        obtain_screencast.setOnPreferenceClickListener(this);
        set_text_color.setOnPreferenceClickListener(this);
        connect_white_wifi.setOnPreferenceClickListener(this);
        btn_power_manage.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if(preference.equals(go_open_a11y)) {
            OpenAlly();
        }else if (preference.equals(obtain_screencast)) {
            obtainScreencast();
        }
        else if (preference.equals(set_text_color)) {
            setThemeTextColor();
        }
        else if (preference.equals(connect_white_wifi)) {
            connectWhiteWifi();
        }
        else if (preference.equals(btn_power_manage)) {

            checkBatteryOptimization();
        }

        return false;
    }

    /**
     * @author  fsy
     * @date    2022/12/23 9:48
     * @return
     * @description 官方文档上是 Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
     *         很多机型上是  Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
     */
    private void checkBatteryOptimization() {
        try {

            //6.0之前不需要设置
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { return; }

            if(!isIgnoringBatteryOptimizations()){
                Intent intent = new Intent();
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
             // intent.setData(Uri.parse("package:" + getActivity().getPackageName()));出错
                Log.w("EMMMoreSetting", "main--------checkBatteryOptimization:"+getActivity().getPackageName());
                startActivity(intent);

                if(MainActivity.getInstance()!=null) {
                    Message message = new Message();
                    message.what = 13;
                    MainActivity.getInstance().handler.sendMessageDelayed(message,200);
                }
            }
            else
                Toast.makeText(getActivity(),"已取消省电限制，无需重复操作",Toast.LENGTH_SHORT).show();

        }catch (Exception e)
        {
            Log.e("EMMMoreSetting", "checkBatteryOptimization Exception:" + e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Boolean isIgnoringBatteryOptimizations() {

        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);

        if (powerManager!=null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getActivity().getPackageName());
        }
        Log.w("EMMMoreSetting", "isIgnoringBatteryOptimizations:"+isIgnoring);

        return isIgnoring;
    }

    private void connectWhiteWifi() {

        //在这里设置状态，跳转过去不被辅助功能阻止的
        g_bAllowSetting = true;
        if(EMMFloatWindowService.isStart())
        {
            EMMFloatWindowService.getInstance().reqShow = false;
        }

        //    跳转过去取消窗口，回来,用 FLAG_ACTIVITY_CLEAR_TASK清理下之前的栈
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        startActivity(intent);
    }

    /**
     * @author  fsy
     * @date    2022/2/17 14:56
     * @return
     * @description  选择字体颜色
     */
    private void setThemeTextColor() {
        try{
            // 颜色格式
            final String[] items = new String[] { "白色", "黑色" };

            // 创建对话框构建器
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // 设置参数
            String colorItem = Save.getValue(EMMApp.getInstance().mainContext, "TEXT_COLOR", "0");

            builder.setIcon(R.drawable.ic_launcher)
                    .setTitle("请选择桌面主题文字的颜色")
                    .setSingleChoiceItems(items, Integer.parseInt(colorItem), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Save.putValue(EMMApp.getInstance().mainContext,"TEXT_COLOR",String.valueOf(which)); //将选择的是第几项保存在文件
                            // TODO Auto-generated method stub
                        }
                    });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DeviceInfoUtil.getTextColor();
                    Toast.makeText(EMMApp.getInstance().mainContext, "设置完成，请重启应用刷新",
                            Toast.LENGTH_SHORT).show();
                }
            }).show();
            // builder.create();
        }catch (Exception e){
            Log.e("EMMMain","Login-e:"+e.toString());
        }
    }

    private void obtainScreencast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(getResultData())
            {
                Tell.toast("截屏权限已获取，无需重复操作",getActivity());
            }
        }
/*
        if(EMMApp.canSendLog){
            EMMApp.canSendLog = false;
            Tell.toast("停止发送日志",getActivity());
        }else {
            EMMApp.canSendLog = true;
            Tell.toast("开始发送日志", getActivity());
        }
/*
                //测试2
                if(EMMApp.canSendLog){
                    EMMApp.canSendLog = false;
                    Tell.toast("停止发送日志",getApplication());
                }

                //(EMMApp.mainContext);
                shutDown1();

                //测试2
                if(EMMApp.canSendLog){
                    EMMApp.canSendLog = false;
                    Tell.toast("停止发送日志",getApplication());
                }else{
                    EMMApp.canSendLog = true;
                    Tell.toast("发送日志",getApplication());
                }

//*/


/*测试视频支持的格式1
                int numCodecs = MediaCodecList.getCodecCount();
                Log.w("EMM","numCodecs="+numCodecs);
                NetDataHub.get().addLog("numCodecs="+numCodecs);
                for (int i = 0; i < numCodecs; i++) {
                    // 编解码器相关性信息存储在MediaCodecInfo中
                    MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
                    // 判断是否为编码器
                    if (!codecInfo.isEncoder()) {
                        continue;
                    }
                    // 获取编码器支持的MIME类型，并进行匹配
                    String[] types = codecInfo.getSupportedTypes();
                    for (int j = 0; j < types.length; j++) {
                        //     Log.w("EMM","i="+i+"|types:"+types[j]+"|name:"+codecInfo.getName());

                        NetDataHub.get().addLog("i="+i+"|types:"+types[j]);
                    }
                }
*/
    }

    /**
     * @author  fsy
     * @date    2022/1/17 14:56
     * @return
     * @description  跳转到辅助功能
     */
    private void OpenAlly() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!EMMAccessibilityService.isStart()) {
                try {
                    Log.w("EMMScreen", "辅助功能未开启，跳转开启服务" );
                    startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Exception e) {
                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    Log.e("EMMScreen", "辅助功能未开启，跳转出错 Exception:" + e.toString());
                    e.printStackTrace();
                }
            }
            else
            {
                Tell.toast("辅助功能已开启，无需跳转", EMMApp.getInstance().mainContext);
            }
        }
    }


    private ListView lv_white_list;
    /**
     * @author  fsy
     * @date    2022/11/14 14:56
     * @return
     * @description  设置wifi白名单内的连接
     */
    private void setWifiLink()
    {
        final String[] items = new String[] { "yt", "yt-5G" };

        Log.w("EMMA11y","nWhich 1");
        //    Log.w("EMMA11y","nWhich "+ nWhich+"nWhich0 "+ nWhich[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//-----
        lv_white_list = new ListView(EMMApp.getInstance().mainContext);

//        ll_white_list.addView(lv_white_list);

        AlertDialog settingWifiDialog = builder.setIcon(R.drawable.ic_launcher)
                .setTitle("请选择白名单内的wifi")
                .setView(lv_white_list).show();

        listWIFI(settingWifiDialog);
    }
    /**
     * @author  fsy
     * @date    2022/11/14 14:56
     * @return
     * @description  设置wifi白名单内的连接
     */
    private void listWIFI(final AlertDialog settingWifiDialog)
    {
        try {

            final String wifiListString = (String) Save.readFile(EMMApp.getInstance().mainContext, "wifiList");
            JSONArray wifiList = new JSONArray(wifiListString);

            Log.w("EMMA11y",wifiListString+"|"+wifiList.length()+"|"+wifiList);

            if(wifiList.length()<1)
            {
                Toast.makeText(getActivity(), "未设置可用白名单WIFI，请联系管理员",Toast.LENGTH_SHORT).show();
                return;
            }

            final String[] wifi_list = new String[wifiList.length()];
            for (int i = 0; i < wifiList.length(); i++)
            {
                JSONObject one = wifiList.getJSONObject(i);
                wifi_list[i]=one.getString("Name1");
            }
            final ArrayAdapter<String> wifiAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_select, wifi_list);

            lv_white_list.setAdapter(wifiAdapter);
            lv_white_list.setPadding(120,30,0,0);
            lv_white_list.setDivider(null);
            lv_white_list.setDividerHeight(0);
            lv_white_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //    EMMForbidWifiWindowManager.removeSmallWindow(context);
                    //    EMMFloatWindowService.getInstance().shouldShow=false;

                    Intent intent = new Intent(getActivity(), SettingWIFIPassword.class);
                    intent.putExtra("wifi_name",wifi_list[i]);
                    startActivity(intent);
                    //intputPassword(wifi_list[i]);
                    //   settingWifiDialog.dismiss();
                    //   settingWifiDialog.cancel();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * @author  fsy
     * @date    2022/11/14 14:56
     * @return
     * @description  设置wifi白名单内的密码
     */
    private void intputPassword(final String wifi){

        Log.w("EMMA11y","wifi_list:"+wifi);

        final EditText editText = new EditText(getActivity());
        editText.setSingleLine();
        editText.setHint("请输入密码");
        editText.requestFocus();
        editText.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//---------
        String title = "请设置" + wifi + "的wifi密码";
        builder.setIcon(R.drawable.ic_launcher)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = editText.getText().toString();
                        if (password.length() < 8) {
                            Toast.makeText(getActivity(), "密码长度不能小于8位",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            Log.w("EMMA11y","wifi_list:wifi_list[which]"+wifi);
                            Toast.makeText(EMMApp.getInstance().mainContext, "正在连接WIFI:"+wifi, Toast.LENGTH_LONG).show();

                            WifiManager wifiManager = (WifiManager) EMMApp.getInstance().mainContext.getSystemService(Context.WIFI_SERVICE);
                            WifiAutoConnectManager.newInstance(wifiManager).closeWifi();
                            WifiAutoConnectManager.newInstance(wifiManager).connectWifi(wifi,password);
                        }
                        //    return;
                    }
                }).show();
    }


    //没有系统权限签名
    private void shutDown() {
        //shutdown now
        String action = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
        Intent shutdown = new Intent(action);
        shutdown.putExtra("android.intent.extra.KEY_CONFIRM", false);
        shutdown.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(shutdown);

        }catch(Exception e){
            Tell.toast(e.toString(),EMMApp.getInstance().mainContext);
            //e.printStackTrace();
        }

    }
    //利用反射调用oIPowerManager方法，在有些机型上可行的
    private void shutDown1() {
        try {
            //获得ServiceManager类
            Class<?> ServiceManager = Class
                    .forName("android.os.ServiceManager");

            //获得ServiceManager的getService方法
            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);

            //调用getService获取RemoteService
            Object oRemoteService = getService.invoke(null, Context.POWER_SERVICE);

            //获得IPowerManager.Stub类
            Class<?> cStub = Class
                    .forName("android.os.IPowerManager$Stub");
            //获得asInterface方法
            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
            //调用asInterface方法获取IPowerManager对象
            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
            //获得shutdown()方法
            Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);
            //调用shutdown()方法
            shutdown.invoke(oIPowerManager,false,true);
        }catch(Exception e){
            Tell.toast(e.toString(),EMMApp.getInstance().mainContext);
            //e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Boolean getResultData(){
        if(EMMApp.getInstance().resultData == null)//resultCode == 0
        {
            this.mediaProjectionManager = (MediaProjectionManager) getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 300);

            if(EMMAccessibilityService.isStart())
            {
                //Log.i("EMMScreen", "MyAccessibilityService.mService已启动" );
                // EMMAccessibilityService.getInstance().isAutoAgree = true;  老方式，不用了 fsy
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EMMAccessibilityService.getInstance().agreeScreenCast();
                    }
                }).start();
            }
            return false;
        }
        return true;
    }//*/
    boolean isRetry = true;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值

        if (requestCode == 300) {
            if (resultCode != Activity.RESULT_OK) {
                //this.g_mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                if(isRetry) //再次申请
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 300);
                    }
                    isRetry= false;
                }
            } else {
                Log.w("EMMScreen", "Starting screen capture requestCode"+requestCode+"data:"+data.toString());
                EMMApp.getInstance().resultCode = resultCode;
                EMMApp.getInstance().resultData = data; //其他可赋值，主要在于data
            }
        }

    }

}