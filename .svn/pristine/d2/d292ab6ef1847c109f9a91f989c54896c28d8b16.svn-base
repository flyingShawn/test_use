package com.grampus.hualauncherkai.UI;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.grampus.hualauncherkai.Data.AppDataHub;
import com.grampus.hualauncherkai.Data.NetCtrlHub;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.Tell;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import static com.grampus.hualauncherkai.Tools.DeviceUtils.setNavigationBarColor;
import static com.grampus.hualauncherkai.Tools.DeviceUtils.setStatusBarColor;

public class LogActivity extends AppCompatActivity
{
    TitleBar titleBar;
    TextView log_textview;
    ContentLoadingProgressBar loadingBar;

    Handler handler = new Handler()
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        setStatusBarColor(this, R.color.brightBlue);
        setNavigationBarColor(this,R.color.brightBlue);


        loadingBar=findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.VISIBLE);
/*  del by gwb;2020.9.16  就不在这里处理了。没必要再次发送。
        new Thread(new Runnable()
        {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run()
            {

                if (NetCtrlHub.get().TimerCheckDo())
                {

                    handler.sendEmptyMessage(1);
                }
                NetCtrlHub.get().upHardlist();
                NetCtrlHub.get().upSoftlist();
            }
        }).start();
*/
        init();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                loadingBar.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        reflashLog();
    }

    private void init()
    {
        titleBar=findViewById(R.id.phone_settings_titlebar);
        titleBar.setOnTitleBarListener(new OnTitleBarListener()
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
                loadingBar.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        loadingBar.setVisibility(View.INVISIBLE);
                    }
                }, 1000);

                log_textview.setText("");
                reflashLog();
            }
        });

        log_textview=findViewById(R.id.log_textview);
        log_textview.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void reflashLog()
    {
        log_textview.setText(NetDataHub.get().getAllLog());
    }

    public void OnDeleteLogClick(View view){
        NetDataHub.get().deleteAllLog();
        Tell.toast("清理日志成功！", getApplicationContext());
        reflashLog();
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
}
