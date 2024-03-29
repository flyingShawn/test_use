package com.grampus.hualauncherkai.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.grampus.hualauncherkai.*;
import com.grampus.hualauncherkai.Data.AppDataHub;
import com.grampus.hualauncherkai.Data.NetCtrlHub;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.Receiver.WifiHub;
import com.grampus.hualauncherkai.Tools.Save;

public class AdminSetting extends AppCompatActivity
{
    Button delete_launcher;

    Button reflash_log;
    Button exit_launcher;
    Button close_launcher;
    TextView log_text;

    //2022.12.29 add Looper.getMainLooper()
    Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
                reflashLog();
            }
        }
    };


    void initView()
    {
        delete_launcher = findViewById(R.id.delete_launcher);
        reflash_log = findViewById(R.id.reflash_log);
        exit_launcher = findViewById(R.id.exit_launcher);
        close_launcher = findViewById(R.id.close_setting);
        log_text = findViewById(R.id.log_text);
        reflash_log.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reflashLog();
            }
        });

        log_text.setText("Log抓取中");


        delete_launcher.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });

        exit_launcher.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setAction("com.grampus.hualauncherkai.action.STOP_RECEIVER");
                sendBroadcast(intent);
                finish();
            }
        });

        close_launcher.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                closeOpenAdmin();
            }
        });

        setCloseBtn();
    }

    private void setCloseBtn()
    {
        String config = Save.getValue(this, "WIFI_APP_WHITE", "0");
        if (config.equals("0"))
        {
            close_launcher.setText("关闭设置窗口");
        }
        else
        {
            close_launcher.setText("开启设置窗口");
        }
    }

    private void closeOpenAdmin()
    {
        String config = Save.getValue(this, "WIFI_APP_WHITE", "0");
        if (config.equals("0"))
        {
            Save.putValue(this, "WIFI_APP_WHITE", "1");
            //关闭
        }
        else
        {
            Save.putValue(this, "WIFI_APP_WHITE", "0");
            //开启
        }
        WifiHub.wifiThink(this);

        Intent intent = new Intent();
        intent.setAction("com.grampus.hualauncher.action.REFRESH_APP");
        sendBroadcast(intent);

        setCloseBtn();
    }

    void reflashLog()
    {
        log_text.setText(NetDataHub.get().getAllLog());
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        AppDataHub.isCanSetting = false;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        AppDataHub.isCanSetting = false;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        AppDataHub.isCanSetting = false;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        AppDataHub.isCanSetting = true;
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        AppDataHub.isCanSetting = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        new Thread(new Runnable()
        {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run()
            {
                if (NetCtrlHub.get().GetServerPolicy())
                {

                    handler.sendEmptyMessage(1);
                }
                NetCtrlHub.get().upHardlist();
                NetCtrlHub.get().upSoftlist();
            }
        }).start();

        initView();
        reflashLog();
    }
}
