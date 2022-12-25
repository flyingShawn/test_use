package com.grampus.hualauncherkai.fragment;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.DeviceReceiver;

import static android.content.Context.CONSUMER_IR_SERVICE;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlBlueTooth;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlCamera;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlMonet;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlUSB;
import static com.grampus.hualauncherkai.Data.NetDataHub.m_bForbitAP;
import static com.grampus.hualauncherkai.UI.MainActivity.androidv;

public class SettingsFragmentPhoneDeviceManage extends PreferenceFragment
{
    private SwitchPreference infraredManage;
    private SwitchPreference bluetoothManage;
    private SwitchPreference cameraManage;
    private SwitchPreference USBManage;
    private SwitchPreference monetManage;
    private SwitchPreference APManage;

    private boolean isDeviceAdmin;

    /**
     * 设备安全管理服务，2.2之前需要通过反射技术获取
     */
    private DevicePolicyManager devicePolicyManager = null;

    /**
     * 对应自定义DeviceAdminReceiver的组件
     */
    private ComponentName componentName = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rightfragkai);

        devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(getActivity(), DeviceReceiver.class);

        //infraredManage=(SwitchPreference)findPreference("infrared_manage");
        monetManage=(SwitchPreference)findPreference("monet_manage");
        bluetoothManage=(SwitchPreference)findPreference("bluetooth_manage");
        cameraManage=(SwitchPreference)findPreference("camera_manage");
        USBManage=(SwitchPreference)findPreference("usb_manage");
        APManage=(SwitchPreference)findPreference("AP_manage");


        isDeviceAdmin=isAdminActive();


        if(androidv<5) {
            Toast.makeText(getActivity(), "当前安卓版本"+android.os.Build.VERSION.RELEASE+"  不支持设备管理.", Toast.LENGTH_SHORT).show();
            return ;
        }

        //没有取得权限
        if (!isDeviceAdmin)
        {
            Toast.makeText(getActivity(), "设备管理未激活", Toast.LENGTH_SHORT).show();
            //infraredManage.setEnabled(false);
            monetManage.setEnabled(false);
            bluetoothManage.setEnabled(false);
            cameraManage.setEnabled(false);
            USBManage.setEnabled(false);
            APManage.setEnabled(false);//add by gwb;2021.2.20
        }
        else if(androidv>=5)//add by gwb;2020.9.25    禁用USB和蓝牙必须是5.0及以上的系统才会支持上，否则调用报错。
        {
            //获取红外设备
            //ConsumerIrManager IR=(ConsumerIrManager)getActivity().getSystemService(CONSUMER_IR_SERVICE);

            //红外
//            if (isCtrlInfrared)
//            {
//                if (IR.hasIrEmitter())
//                {
//                    infraredManage.setChecked(true);
//                    //devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), "当前设备无红外设备", Toast.LENGTH_SHORT).show();
//                }
//            }
//            else
//            {
//                if (IR.hasIrEmitter())
//                {
//                    infraredManage.setChecked(false);
//                    //devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), "当前设备无红外设备", Toast.LENGTH_SHORT).show();
//                }
//            }

            /* del by gwb;2020.9.25
            //移动网络
            if (isCtrlMonet)
            {
                monetManage.setChecked(true);
                devicePolicyManager.addUserRestriction(componentName,UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
            }
            else
            {
                monetManage.setChecked(false);
                try
                {
                    devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
                }
                catch (SecurityException e)
                {

                }
            }

             */


            //蓝牙
            try {
                if (isCtrlBlueTooth) {
                    bluetoothManage.setChecked(true);
                    devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_BLUETOOTH);
                } else {
                    bluetoothManage.setChecked(false);

                    devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_BLUETOOTH);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }


            //摄像头
            try {
                if (isCtrlCamera){
                    cameraManage.setChecked(true);
                } else {
                    cameraManage.setChecked(false);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }


            //USB设备
            if (isCtrlUSB)
            {
                USBManage.setChecked(true);
               //devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);del by gwb;2020.9.25 会报错。
            }
            else
            {
                USBManage.setChecked(false);
                try
                {
                    //devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);del by gwb;2020.9.25  会报错。
                }
                catch (SecurityException e)
                {

                }
            }

            //热点
            try {
                if (m_bForbitAP) {
                    APManage.setChecked(true);
                } else {
                    APManage.setChecked(false);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }


        }

    }

    public boolean isAdminActive()
    {
        return devicePolicyManager.isAdminActive(componentName);
    }

    public void setDeviceCtrl()
    {

    }

}
