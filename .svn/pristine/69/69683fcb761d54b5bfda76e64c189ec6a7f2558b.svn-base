package com.grampus.hualauncherkai.Tools;


import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * 一些可能会用到的工具方法，都写在这个类里面
 */
public class DeviceUtils
{
    @TargetApi(19)
    public static void transparencyBar(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 修改状态栏颜色，支持5.0以上版本
     *
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        }
    }


    public static void setNavigationBarColor(Activity activity, int colorId)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(activity.getResources().getColor(colorId));
        }
    }

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";

    public static boolean isMiUIV6OrAbove()
    {
        try
        {
            final Properties properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            String uiCode = properties.getProperty(KEY_MIUI_VERSION_CODE, null);
            if (uiCode != null)
            {
                int code = Integer.parseInt(uiCode);
                return code >= 4;
            }
            else
            {
                return false;
            }

        }
        catch (final Exception e)
        {
            return false;
        }

    }

    public static boolean isMiUIV7OrAbove()
    {
        try
        {
            final Properties properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            String uiCode = properties.getProperty(KEY_MIUI_VERSION_CODE, null);
            if (uiCode != null)
            {
                int code = Integer.parseInt(uiCode);
                return code >= 5;
            }
            else
            {
                return false;
            }

        }
        catch (final Exception e)
        {
            return false;
        }

    }

}
