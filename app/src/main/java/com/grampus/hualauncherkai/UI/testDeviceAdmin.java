package com.grampus.hualauncherkai.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.DeviceReceiver;

public class testDeviceAdmin extends AppCompatActivity
{
    /**
     * 激活组件的请求码
     */
    private static final int REQUEST_CODE_ACTIVE_COMPONENT = 1;
    /**
     * 设备安全管理服务，2.2之前需要通过反射技术获取
     */
    private DevicePolicyManager devicePolicyManager = null;
    /**
     * 对应自定义DeviceAdminReceiver的组件
     */
    private ComponentName componentName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_device_admin);
        setTranslucent(this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, DeviceReceiver.class);

        /**
         * 激活设备管理器
         */
        findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isAdminActive())
                {
                    Toast.makeText(testDeviceAdmin.this, "设备管理器已激活", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // 打开管理器的激活窗口
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    // 指定需要激活的组件
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "(激活窗口中的描述信息)");
                    startActivityForResult(intent, REQUEST_CODE_ACTIVE_COMPONENT);
                }
            }
        });

        /**
         * 取消激活
         */
        findViewById(R.id.btn_cancel_active).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isAdminActive())
                {
                    devicePolicyManager.removeActiveAdmin(componentName);
                    Toast.makeText(testDeviceAdmin.this, "将触发deviceAdminReceiver.onDisabled", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(testDeviceAdmin.this, "设备管理未激活", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 锁屏
         */
        findViewById(R.id.btn_lock).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isAdminActive())
                {
                    devicePolicyManager.lockNow();
                }
                else
                {
                    Toast.makeText(testDeviceAdmin.this, "设备管理未激活", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 禁止使用摄像头
         */
        findViewById(R.id.btn_setCameraDisabled).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isAdminActive())
                {
                    devicePolicyManager.setCameraDisabled(componentName, true);
                }
            }
        });

        /**
         * 启动摄像头
         */
        findViewById(R.id.btn_setCameraDisabled1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isAdminActive())
                {
                    devicePolicyManager.setCameraDisabled(componentName, false);
                }
            }
        });

        /**
         * 设置密码
         */
        findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isAdminActive())
                {
                    devicePolicyManager.resetPassword("123456", 1);
                }
            }
        });

        /**
         * 取消密码
         */
        findViewById(R.id.btn_cancel_password).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isAdminActive())
                {
                    devicePolicyManager.resetPassword("", 0);
                }
            }
        });
    }

    /**
     * 判断该组建是否有系统管理员的权限（系统安全-设备管理器 中是否激活）
     *
     * @return
     */
    private boolean isAdminActive()
    {
        return devicePolicyManager.isAdminActive(componentName);
    }

    /**
     * 用户是否点击激活或取消的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTIVE_COMPONENT)
        {
            // 激活组件的响应
            if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "用户手动取消激活", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "已触发DeviceAdminReceiver.onEnabled", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static void setTranslucent(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }
}
