package com.grampus.hualauncherkai.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.grampus.hualauncherkai.R;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import static com.grampus.hualauncherkai.Tools.DeviceUtils.setNavigationBarColor;
import static com.grampus.hualauncherkai.Tools.DeviceUtils.setStatusBarColor;

public class WiFiWhiteListActivity extends AppCompatActivity
{
    TitleBar wifiTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_white_list);


        setStatusBarColor(this, R.color.brightBlue);
        setNavigationBarColor(this, R.color.dockColor);


        wifiTitleBar=findViewById(R.id.wifi_titlebar);
        wifiTitleBar.setOnTitleBarListener(new OnTitleBarListener()
        {
            @Override
            public void onLeftClick(View v)
            {
                onBackPressed();
            }

            @Override
            public void onTitleClick(View v)
            {

            }

            @Override
            public void onRightClick(View v)
            {

            }
        });
    }
}
