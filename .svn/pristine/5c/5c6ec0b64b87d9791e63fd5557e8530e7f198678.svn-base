package com.grampus.hualauncherkai.UI;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.grampus.hualauncherkai.R;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import static com.grampus.hualauncherkai.Tools.DeviceUtils.setNavigationBarColor;
import static com.grampus.hualauncherkai.Tools.DeviceUtils.setStatusBarColor;

public class MoreSettingActivity extends AppCompatActivity {

    private TitleBar titlebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_setting);

        setStatusBarColor(this,R.color.brightBlue);
        setNavigationBarColor(this,R.color.brightBlue);

        titlebar = findViewById(R.id.settings_titlebar);
        titlebar.setOnTitleBarListener(new OnTitleBarListener()
        {
            @Override
            public void onLeftClick(View v)
            {
                onBackPressed();
            }

            @Override
            public void onTitleClick(View v)
            { }

            @Override
            public void onRightClick(View v)
            { }
        });
    }


}
