package com.grampus.hualauncherkai.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.grampus.hualauncherkai.Data.NetCtrlHub;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.UI.AppWhiteListActivity;
import com.grampus.hualauncherkai.UI.LogActivity;
import com.grampus.hualauncherkai.UI.MainActivity;
import com.grampus.hualauncherkai.UI.ShowAdminRightActivity;
import com.grampus.hualauncherkai.UI.ShowDeviceManageActivity;
import com.grampus.hualauncherkai.UI.WiFiWhiteListActivity;

import static com.grampus.hualauncherkai.UI.MainActivity.androidv;

public class SettingsFragmentPhone extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
    private Preference whiteList;
    private Preference deviceManage;
    private Preference viewLog;
    private Preference adminSettings;

    private Preference appwhiteList;
    private Preference wifiwhiteList;

    private Preference updateVersion;


    //---------add by gwb;2020.10.21    //Looper.getMainLooper() 2022.12.29
    public Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 10)
            {
                System.out.println("SettingsFragmentPhone---收到消息了");
                if(MainActivity.getInstance()!=null)
                    MainActivity.getInstance().showDialog(SettingsFragmentPhone.this.getActivity(),(String)msg.obj);
                //SettingsFragmentPhone.this.getActivity().finish();
            }
        }
    };
    //-----------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        /* del by gwb;2020.9.24
        //从xml文件加载选项
        addPreferencesFromResource(R.xml.leftfrag);

        whiteList=findPreference("white_list");
        whiteList.setOnPreferenceClickListener(this);

        deviceManage=findPreference("device_manage");
        deviceManage.setOnPreferenceClickListener(this);

        viewLog=findPreference("view_log");
        viewLog.setOnPreferenceClickListener(this);

        adminSettings=findPreference("admin_settings");
        adminSettings.setOnPreferenceClickListener(this);
        */

        //--------add by gwb;2020.9.24
        addPreferencesFromResource(R.xml.leftfrag2);

        appwhiteList=findPreference("whiteApp_list");
        appwhiteList.setOnPreferenceClickListener(this);

        wifiwhiteList=findPreference("whiteWifi_list");
        wifiwhiteList.setOnPreferenceClickListener(this);

        deviceManage=findPreference("device_manage");
        deviceManage.setOnPreferenceClickListener(this);

        viewLog=findPreference("view_log");
        viewLog.setOnPreferenceClickListener(this);

        adminSettings=findPreference("admin_settings");
        adminSettings.setOnPreferenceClickListener(this);

        updateVersion=findPreference("update_self");
        updateVersion.setOnPreferenceClickListener(this);



        //-------------------end.
    }
    @Override
    public boolean onPreferenceClick(Preference preference)  //add by gwb;2020.9.24
    {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();

        if (appwhiteList!=null && appwhiteList.equals(preference))
        {
            Intent intent = new Intent(SettingsFragmentPhone.this.getActivity(), AppWhiteListActivity.class);
            startActivity(intent);
        }
        else if (wifiwhiteList!=null && wifiwhiteList.equals(preference))
        {
            Intent intent = new Intent(SettingsFragmentPhone.this.getActivity(), WiFiWhiteListActivity.class);
            startActivity(intent);
        }
        else if (deviceManage!=null && deviceManage.equals(preference))
        {
           // Intent intent = new Intent(SettingsFragmentPhone.this.getActivity(),admin_devicemanage.class);
            Intent intent = new Intent(SettingsFragmentPhone.this.getActivity(), ShowDeviceManageActivity.class);

            startActivity(intent);
        }
        else if (viewLog.equals(preference))
        {
            Intent intent = new Intent(SettingsFragmentPhone.this.getActivity(), LogActivity.class);
            startActivity(intent);
        }
        else if(adminSettings.equals(preference)) {
            Intent intent = new Intent(SettingsFragmentPhone.this.getActivity(), ShowAdminRightActivity.class);
            startActivity(intent);
        }
        else if(updateVersion.equals(preference)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String szServerVersion = NetCtrlHub.get().CheckUpdateVersion(true);//检查升级   不能在主线程里面访问网络
                    if(szServerVersion.length()>0) {//需要升级
                        Message message = new Message();
                        message.what = 10;
                        message.obj = szServerVersion;
                        handler.sendMessage(message);
                    }
                }
            }).start();
            //NetCtrlHub.get().CheckUpdateVersion(true);//检查升级   不能在主线程里面访问网络
        }


        return true;
    }


    public boolean onPreferenceClick_old(Preference preference)
    {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();

        if (whiteList.equals(preference))
        {
            if(androidv>=5) { //add by gwb;2020.9.23  增加了判断。
                SettingsFragmentPhoneWhiteList secondFragment = new SettingsFragmentPhoneWhiteList();
                transaction.replace(R.id.settings_phone, secondFragment);
                transaction.commit();
                return true;
            }
            else
            {

                System.out.println("版本太低，先不处理，因为显示不了.");
                return true;
            }
/*
            //---add by gwb;2020.9.23  这在安卓4.0版本上是可以点击显示出来，否则用上面replace方法点了没有反应。
            Fragment temp1 = manager.findFragmentByTag("device_manage");
            if(temp1 != null)
                transaction.hide(temp1);
            temp1 = manager.findFragmentByTag("sys_settings");
            if(temp1 != null)
                transaction.hide(temp1);

            Fragment temp = manager.findFragmentByTag("white_list");
            if(temp == null) {
                SettingsFragmentPhoneWhiteList secondFragment=new SettingsFragmentPhoneWhiteList();
                transaction.add(R.id.settings_phone,secondFragment,"white_list");
                transaction.show(secondFragment);
            }
            else
            {
                //判断此Fragment是否已经添加到FragmentTransaction事物中
                transaction.show(temp);
            }
            transaction.commit();
            //------------------------End.
            return true;
 */
        }

        else if (deviceManage.equals(preference))
        {
            if(androidv>=5) { //add by gwb;2020.9.23  增加了判断。
                SettingsFragmentPhoneDeviceManage secondFragment=new SettingsFragmentPhoneDeviceManage();
                transaction.replace(R.id.settings_phone,secondFragment);
                transaction.commit();
                return true;
            }
            else
            {
                System.out.println("版本太低，先不处理，因为显示不了.");
                return true;
            }
/*
            //SettingsFragmentPhoneDeviceManage secondFragment=new SettingsFragmentPhoneDeviceManage();
            //transaction.replace(R.id.settings_phone,secondFragment);del by gwb;2020.9.23

            //---add by gwb;2020.9.23  这在安卓4.0版本上是可以点击显示出来，否则用上面replace方法点了没有反应。
            Fragment temp1 = manager.findFragmentByTag("white_list");
            if(temp1 != null)
                transaction.hide(temp1);
            temp1 = manager.findFragmentByTag("sys_settings");
            if(temp1 != null)
                transaction.hide(temp1);

            Fragment temp = manager.findFragmentByTag("device_manage");
            if(temp == null) {
                SettingsFragmentPhoneDeviceManage secondFragment=new SettingsFragmentPhoneDeviceManage();
                transaction.add(R.id.settings_phone,secondFragment,"device_manage");
                transaction.show(secondFragment);
            }
            else
            {
                //判断此Fragment是否已经添加到FragmentTransaction事物中
                transaction.show(temp);
            }
            //------------------------End.


            transaction.commit();

            return true;

 */
        }

        else if (viewLog.equals(preference))
        {
            Intent intent = new Intent(SettingsFragmentPhone.this.getActivity(),LogActivity.class);
            startActivity(intent);

            return true;
        }

        else if(adminSettings.equals(preference))
        {
            if(androidv>=5) { //add by gwb;2020.9.23  增加了判断。
                AcquireDeviceAdmin secondFragment=new AcquireDeviceAdmin();
                transaction.replace(R.id.settings_phone,secondFragment);
                transaction.commit();
                return true;
            }
            else
            {
                System.out.println("版本太低，先不处理，因为显示不了.");
                return true;
            }
            /*
            //---add by gwb;2020.9.23  这在安卓4.0版本上是可以点击显示出来，否则用上面replace方法点了没有反应。

            Fragment temp1 = manager.findFragmentByTag("white_list");
            if(temp1 != null)
                transaction.hide(temp1);
            temp1 = manager.findFragmentByTag("device_manage");
            if(temp1 != null)
                transaction.hide(temp1);

            Fragment temp = manager.findFragmentByTag("sys_settings");
            if(temp == null) {
                AcquireDeviceAdmin secondFragment=new AcquireDeviceAdmin();
                transaction.add(R.id.settings_phone,secondFragment,"sys_settings");
                transaction.show(secondFragment);
            }
            else
            {
                //判断此Fragment是否已经添加到FragmentTransaction事物中
                transaction.show(temp);
            }
            //------------------------End.


            transaction.commit();
            return true;

             */
        }

        return false;
    }
}
