package com.grampus.hualauncherkai.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.grampus.hualauncherkai.R;

public class RightFragmentKai extends PreferenceFragment implements Preference.OnPreferenceChangeListener
{
    private SwitchPreference infraredManage;
    private SwitchPreference bluetoothManage;
    private SwitchPreference cameraManage;
    private SwitchPreference USBManage;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //从xml文件加载选项
        addPreferencesFromResource(R.xml.rightfragkai);

        infraredManage=(SwitchPreference)findPreference("infrared_manage");
        infraredManage.setOnPreferenceChangeListener(this);

        bluetoothManage=(SwitchPreference)findPreference("bluetooth_manage");
        bluetoothManage.setOnPreferenceChangeListener(this);

        cameraManage=(SwitchPreference)findPreference("camera_manage");
        cameraManage.setOnPreferenceChangeListener(this);

        USBManage=(SwitchPreference)findPreference("usb_manage");
        USBManage.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        if (infraredManage.equals(preference))
        {
            /**
             * 注意！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
             * 通过改变事件获取到的isChecked是相反的，可能是和时间点有关系
             * 本来是没选中，点击之后选中了，返回的值是false
             * 注意！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
             */
            //选中之后执行
            if (!infraredManage.isChecked())
            {

            }
            //取消选中之后执行
            else
            {

            }

        }

        if (bluetoothManage.equals(preference))
        {
            System.out.println("蓝牙");
        }

        if (cameraManage.equals(preference))
        {
            System.out.println("相机");
        }

        if (USBManage.equals(preference))
        {
            System.out.println("USB");
        }

        return true;
    }
}
