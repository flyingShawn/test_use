package com.grampus.hualauncherkai.fragment;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import android.preference.SwitchPreference;
import android.widget.Toast;

import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.DeviceReceiver;

public class AcquireDeviceAdmin extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
    SwitchPreference acquireDeviceAdmin;

    /**
     * 激活组件的请求码
     */
    private final int REQUEST_CODE_ACTIVE_COMPONENT = 1;

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
        addPreferencesFromResource(R.xml.acquire_device_admin);

        devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(getActivity(), DeviceReceiver.class);

        acquireDeviceAdmin = (SwitchPreference) findPreference("acquire_device_admin");
        acquireDeviceAdmin.setOnPreferenceClickListener(this);

        //如果有权限就设置开，没有就是关
        acquireDeviceAdmin.setChecked(isAdminActive());


        acquireDeviceAdmin.setSelectable(true);//add by gwb;2020.9.30  让可以选择直接打开设备管理器权限,否则不能选。

        if (acquireDeviceAdmin.isChecked())
        {
            acquireDeviceAdmin.setSelectable(false);
            acquireDeviceAdmin.setTitle("已取得设备管理器权限");
            acquireDeviceAdmin.setSummary("您已经成功取得设备管理器权限，现在设备管控可以正常使用");
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        if (acquireDeviceAdmin.equals(preference))
        {
            if (isAdminActive())
            {
                Toast.makeText(getActivity(), "设备管理器已激活", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                // 打开管理器的激活窗口
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                // 指定需要激活的组件
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "(激活窗口中的描述信息)");
                startActivityForResult(intent, REQUEST_CODE_ACTIVE_COMPONENT);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断该组建是否有系统管理员的权限（系统安全-设备管理器 中是否激活）
     *
     * @return
     */
    public boolean isAdminActive()
    {
        return devicePolicyManager.isAdminActive(componentName);
    }
}
