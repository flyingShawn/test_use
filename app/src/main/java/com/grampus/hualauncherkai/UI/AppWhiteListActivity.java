package com.grampus.hualauncherkai.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.grampus.hualauncherkai.R;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import static com.grampus.hualauncherkai.Tools.DeviceUtils.setNavigationBarColor;
import static com.grampus.hualauncherkai.Tools.DeviceUtils.setStatusBarColor;

public class AppWhiteListActivity extends AppCompatActivity
{
    TitleBar appTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_white_list);

        setStatusBarColor(this, R.color.brightBlue);
        setNavigationBarColor(this,R.color.brightBlue);

        appTitleBar=findViewById(R.id.app_titlebar);
        appTitleBar.setOnTitleBarListener(new OnTitleBarListener()
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
