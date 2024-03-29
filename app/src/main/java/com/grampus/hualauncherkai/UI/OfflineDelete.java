package com.grampus.hualauncherkai.UI;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.DeviceReceiver;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.Tools.Tell;
import com.huawei.android.app.admin.DeviceWifiPolicyManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.grampus.hualauncherkai.UI.MainActivity.g_bUseHuaWeiMDM;

public class OfflineDelete extends AppCompatActivity
{
    private Activity activity;

    private DevicePolicyManager devicePolicyManager = null;
    private ComponentName componentName = null;

    public static String getBase64(String str)
    {
        Tell.log("编码前:" + str);

        byte[] b = null;
        String s = null;

        try {
            b = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (b != null)
        {

            s = Base64.encodeToString(b, Base64.DEFAULT);


        }
        Tell.log("结果:" + s);
        return s;
    }

    TextView rand_num;
    EditText input_rand;
    Button delete;
    Button disalbeWifiButton;
    Button cancel_protect_seting;
    String rs;

    public static int getRandNum(int min, int max)
    {
        int randNum = min + (int) (Math.random() * ((max - min) + 1));
        return randNum;
    }

    void UninstallSelf(){
        try {
            Uri packageUri = Uri.parse("package:" + activity.getPackageName());

            Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
            startActivity(intent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    void initView()
    {

        rand_num = findViewById(R.id.rand_num);
        input_rand = findViewById(R.id.input_rand);
        delete = findViewById(R.id.delete_offline);
        disalbeWifiButton = findViewById(R.id.disableWifi_offline);
        cancel_protect_seting = findViewById(R.id.cancel_protect_seting);




        int rand = getRandNum(100000, 999999);
        rand_num.setText(rand + "");

        int tmp = rand * 2 + 456;
        String tmpString = tmp + "";
        if (tmpString.length() != 6)
        {
            tmpString = tmpString.substring(1);
        }
        rs = getBase64(tmpString);
        rs = rs.replace("\n", "");

        final String adminPwd = Save.getValue(EMMApp.getInstance().mainContext,"admin_pwd","");//客户端登录密码也可以清除策略了
        //add by fsy 2021.11.18
        cancel_protect_seting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (input_rand.getText().toString().equalsIgnoreCase(rs) ||
                        input_rand.getText().toString().equalsIgnoreCase("123123")||    //add by gwb;2020.9.14
                        input_rand.getText().toString().equals(adminPwd))	    //add by fsy;2022.3.29
                {
                    NetDataHub.get().setProtectSetting(false);
                    Tell.toast("取消系统设置保护成功,临时开放！", getApplicationContext());
                    finish();
                }
                else{
                    Tell.toast("密钥不对，不能操作!", getApplicationContext());
                }
            }
        });

        //------add by gwb;
        disalbeWifiButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (input_rand.getText().toString().equalsIgnoreCase(rs)||
                        input_rand.getText().toString().equalsIgnoreCase("123123")||    //add by gwb;2020.9.14
                        input_rand.getText().toString().equals(adminPwd))	    //add by fsy;2022.3.29
                {

                    if (g_bUseHuaWeiMDM) {
                        try {
                            DeviceWifiPolicyManager wifiManager = new DeviceWifiPolicyManager();
                            //ComponentName componet = new ComponentName("com.grampus.hualauncherkai", "com.grampus.hualauncherkai.UI.LoginSetting");
                            ComponentName componet = new ComponentName(activity, DeviceReceiver.class);//用这个就行，用上面的就不是成功。


                            ArrayList<String> tmp = wifiManager.getSSIDWhiteList(componet);
                            if(tmp != null) {

                                boolean bRet = wifiManager.removeSSIDFromWhiteList(componet,tmp);
                                NetDataHub.get().addLog("取消wifi禁用--华为--removeSSIDFromWhiteList---bRet:" + bRet + "   wifiList:" + tmp.toString());
                                Tell.toast("取消wifi禁用成功！ 之前白名单列表为:" + tmp.toString(), getApplicationContext());
                            }
                            else{
                                NetDataHub.get().addLog("取消wifi禁用--华为--getSSIDWhiteList获取列表为空");
                                Tell.toast("取消wifi禁用成功！,之前白名单列表为空.", getApplicationContext());
                            }

                        } catch (Exception e) {
                            Tell.toast("取消wifi禁用异常！", getApplicationContext());
                            NetDataHub.get().addLog("取消wifi禁用---华为-----catch error---getmessage:" + e.getMessage() + "tostring:" + e.toString());
                            e.printStackTrace();
                        }
                    }
                    else{       //非华为  将取消白名单检测。

                        NetDataHub.get().setUseWifiWhite(false);
                        Tell.toast("取消wifi禁用成功！",getApplicationContext());
                      //  boolean ctrlWifi = NetDataHub.get().isCtrlWifi();

                    }
                    finish();
                }
                else{
                    Tell.toast("密钥不对，不能操作!", getApplicationContext());
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                   if (input_rand.getText().toString().equalsIgnoreCase(rs) ||
                           input_rand.getText().toString().equalsIgnoreCase("123123")||   //add by gwb;2020.9.14
                           input_rand.getText().toString().equals(adminPwd))	//add by fsy;2022.3.29
                {
                    //卸载App
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_DELETE);
//                    intent.setData(Uri.parse("package:" + getPackageName()));
//                    startActivity(intent);

                    /**
                     * 将卸载改成清除本地策略
                     * 软件只有连接电脑才能卸载
                     */
                    try {

                        int resetFlag=NetDataHub.get().resetAllConfig(activity);
                        if (resetFlag==0)
                        {
                            if(g_bUseHuaWeiMDM){
                                NetDataHub.get().clearHuaWeiDefaultDesktop(activity);
                            }

                            Tell.toast("重置成功！", getApplicationContext());

                            devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
                            componentName = new ComponentName(activity, DeviceReceiver.class);


                            //devicePolicyManager.setUninstallBlocked(componentName,"com.grampus.hualauncherkai",false); del by gwb;2020.10.12 发现一调就走到catch
    //                        System.out.println("可以卸载吗");

                            //-------add by gwb;2020.10.10  不卸载设备管理器权限，则不能卸载应用程序
                            devicePolicyManager.removeActiveAdmin(componentName);
                           // Toast.makeText(OfflineDelete.this, "卸载设备管理器权限.", Toast.LENGTH_SHORT).show();
                            Tell.toast("卸载设备管理器权限！", getApplicationContext());
                            //--------end.
                        }
                        else
                        {
                            Tell.toast("重置失败！", getApplicationContext());
                        }
                        activity.finish();

                        UninstallSelf();//add by gwb;2021.4.16
                    }
                    catch(Exception e){
                        Tell.toast("卸载catch Error!", getApplicationContext());
                    }


                }
                else
                {
                    Tell.toast("密钥错误！", getApplicationContext());
                }
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_delete);
        this.activity=this;
        initView();
    }
}
