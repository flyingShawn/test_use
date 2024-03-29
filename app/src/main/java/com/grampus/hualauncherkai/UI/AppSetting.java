package com.grampus.hualauncherkai.UI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grampus.hualauncherkai.Data.AppDataHub;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.Tell;

public class AppSetting extends Activity
{
    ImageView imageView;
    TextView textView;

    TextView setQuick;
    TextView delete;
    static Handler handler;

    boolean isQuick;

    void initView()
    {
        imageView = findViewById(R.id.setting_icon);
        textView = findViewById(R.id.setting_text);
        imageView.setImageDrawable(AppDataHub.getSettingApp().activityInfo.loadIcon(getPackageManager()));
        textView.setText(AppDataHub.getSettingApp().loadLabel(getPackageManager()));

        setQuick = findViewById(R.id.setquick);
        delete = findViewById(R.id.delete);
        AppDataHub.getOffen();

        isQuick = AppDataHub.isSettingOffen();

        if (isQuick)
        {
            setQuick.setText("取消快捷");
        }

        setQuick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NetDataHub.get().setCanReflashDesk(true);
                if (isQuick)
                {
                    Tell.log("你选择了取消快捷");
                    AppDataHub.removeSettingOffen();

                    handler.sendEmptyMessage(1);
                    finish();
                }
                else
                {
                    OffenApp.isSettingOffen = true;
                    handler.sendEmptyMessage(2);
                    finish();
                    Tell.toast("请选择底部图标", getApplicationContext());
                }
            }
        });


        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + AppDataHub.getSettingApp().activityInfo.packageName));
                startActivity(intent);
            }
        });

    }

    static public void setHandler(Handler hd)
    {
        handler = hd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);
        initView();
    }
}
