package com.grampus.hualauncherkai.UI;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.DeviceReceiver;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlBlueTooth;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlCamera;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlMonet;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlUSB;

public class show_device_manage extends PreferenceActivity {

    private boolean isDeviceAdmin;
    private SwitchPreference infraredManage;
    private SwitchPreference bluetoothManage;
    private SwitchPreference cameraManage;
    private SwitchPreference USBManage;
    private SwitchPreference monetManage;

    /**
     * 设备安全管理服务，2.2之前需要通过反射技术获取
     */
    private DevicePolicyManager devicePolicyManager = null;

    /**
     * 对应自定义DeviceAdminReceiver的组件
     */
    private ComponentName componentName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        /*add by gwb; 一调用就报错，不知道为什么
        final boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        if(isCustom){
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.include_top_menu);
        }
         */

        this.addPreferencesFromResource(R.xml.rightfragkai);//引入我们的xml

        try {

            devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            componentName = new ComponentName(this, DeviceReceiver.class);

            //infraredManage=(SwitchPreference)findPreference("infrared_manage");
            monetManage = (SwitchPreference) findPreference("monet_manage");
            bluetoothManage = (SwitchPreference) findPreference("bluetooth_manage");
            cameraManage = (SwitchPreference) findPreference("camera_manage");
            USBManage = (SwitchPreference) findPreference("usb_manage");

            isDeviceAdmin = isAdminActive();

            //没有取得权限
            if ( !isDeviceAdmin) {
                Toast.makeText(this, "设备管理未激活", Toast.LENGTH_SHORT).show();
                //infraredManage.setEnabled(false);
                monetManage.setEnabled(false);
                bluetoothManage.setEnabled(false);
                cameraManage.setEnabled(false);
                USBManage.setEnabled(false);
            } else {
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

                //移动网络
                if (isCtrlMonet) {
                    monetManage.setChecked(true);
                    devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
                } else {
                    monetManage.setChecked(false);
                    try {
                        devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS);
                    } catch (SecurityException e) {

                    }
                }

                //蓝牙
                if (isCtrlBlueTooth) {
                    bluetoothManage.setChecked(true);
                    devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_BLUETOOTH);
                } else {
                    bluetoothManage.setChecked(false);
                    try {
                        devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_BLUETOOTH);
                    } catch (SecurityException e) {

                    }
                }

                //摄像头
                if (isCtrlCamera) {
                    devicePolicyManager.setCameraDisabled(componentName, true);
                    cameraManage.setChecked(true);
                } else {
                    try {
                        devicePolicyManager.setCameraDisabled(componentName, false);
                    } catch (SecurityException e) {

                    }
                    cameraManage.setChecked(false);
                }

                //USB设备
                if (isCtrlUSB) {
                    USBManage.setChecked(true);
                    devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);
                } else {
                    USBManage.setChecked(false);
                    try {
                        devicePolicyManager.clearUserRestriction(componentName, UserManager.DISALLOW_USB_FILE_TRANSFER);
                    } catch (SecurityException e) {

                    }
                }
            }
        }
        catch(Exception e) {
        }

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean isAdminActive()
    {
        return devicePolicyManager.isAdminActive(componentName);
    }

    public void setDeviceCtrl()
    {

    }
}