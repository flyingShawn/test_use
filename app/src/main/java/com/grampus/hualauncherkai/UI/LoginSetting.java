package com.grampus.hualauncherkai.UI;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.NetCtrlHub;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.Data.SystemDataGet;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.HttpRequest;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.Tools.Tell;
import com.grampus.hualauncherkai.util.DeviceInfoUtil;
import com.grampus.hualauncherkai.util.XClickUtil;

import static com.grampus.hualauncherkai.Tools.DeviceUtils.setStatusBarColor;
import static com.grampus.hualauncherkai.UI.MainActivity.g_bUseHuaWeiMDM;
import static com.grampus.hualauncherkai.util.DeviceInfoUtil.isPad;

public class LoginSetting extends AppCompatActivity
{


    Button go_login;
    EditText admin_id;
    EditText admin_pw;
    EditText service_ad;
    Button go_offline_delete;
    Button btn_view_policy;
    private Button btn_more_setting;

    private androidx.core.widget.ContentLoadingProgressBar loginLoadingBar;
    private FrameLayout loginView;


    private MediaProjectionManager mediaProjectionManager;

    String ad;
    String id;
    String pw;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
                Tell.toast("登录成功", getApplicationContext());

                loginOK();

                if(g_bUseHuaWeiMDM){
                    NetDataHub.get().setHuaWeiDesktop(LoginSetting.this);//add by gwb;2020.10.9
                }
            }
            else if (msg.what == 2)
            {
                Tell.toast("登录失败" + msg.obj, getApplicationContext());
            }
            else if (msg.what == 3)
            {
                //Toast.makeText(this, "fffffffffffff！",Toast.LENGTH_SHORT).show();
                //Toast.makeText(LoginSetting.this, "设备管理未激活", Toast.LENGTH_SHORT).show();
                Tell.toast((String) msg.obj, getApplicationContext());
            }
        }
    };



    void initView()
    {
        go_offline_delete = findViewById(R.id.go_offline_delete);
        btn_view_policy = findViewById(R.id.view_policy);
        go_login = findViewById(R.id.go_login);
        admin_id = findViewById(R.id.admin_id);
        admin_pw = findViewById(R.id.admin_pw);
        service_ad = findViewById(R.id.service_ad);
        service_ad.setText(NetCtrlHub.get().getServiceAd());
        loginLoadingBar = findViewById(R.id.login_loadingBar);
        loginLoadingBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(LoginSetting.this, R.color.brightBlue), PorterDuff.Mode.MULTIPLY);

        loginView = findViewById(R.id.login_view);

        btn_more_setting = findViewById(R.id.btn_more_setting);
        btn_more_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(XClickUtil.isFastDoubleClick(btn_more_setting,1000))
                    return;
                Intent intent = new Intent(LoginSetting.this, MoreSettingActivity.class);
                startActivity(intent);

            }
        });


        go_offline_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(XClickUtil.isFastDoubleClick(btn_more_setting,1000))
                    return;
                startActivity(new Intent(LoginSetting.this, OfflineDelete.class));
                finish();
            }
        });
        btn_view_policy.setOnClickListener(new View.OnClickListener()//add by gwb;2020.10.15
        {
            @Override
            public void onClick(View v)
            {
                if(XClickUtil.isFastDoubleClick(btn_view_policy,1000))
                    return;
                String adminid = "";
                String adminpwd = "";

                adminid = Save.getValue(LoginSetting.this, "admin_id", "");
                adminpwd = Save.getValue(LoginSetting.this, "admin_pwd", "");

                id = admin_id.getText().toString();
                pw = admin_pw.getText().toString();
                if ( id.equals(adminid)  && pw.equals(adminpwd))
                {
                    startActivity(new Intent(getApplicationContext(), SettingsPhone.class));
                    finish();
                }
                else {
                    Tell.toast("用户名或密码不对，请使用上次成功登陆的帐号和密码重试！", getApplicationContext());
                    return;
                }
            }
        });

        go_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(XClickUtil.isFastDoubleClick(go_login,1000))
                    return;
                if(EMMApp.getInstance().startCapture){
                    handler.sendEmptyMessage(1);
                }

                ad = service_ad.getText().toString();

                if(ad.compareToIgnoreCase("log") == 0)//add by gwb;2020.10.12
                {
                    Intent intent = new Intent(LoginSetting.this,LogActivity.class);
                    startActivity(intent);
                    finish();
                    return ;
                }

                //添加默认不输入端口的判断
                if (ad.indexOf(":") == -1)
                {
                    ad = ad + ":16670";
                }

                id = admin_id.getText().toString();
                pw = admin_pw.getText().toString();
                if (ad.equals("") || id.equals("") || pw.equals(""))    //密码可为空
                {
                    Tell.toast("不能有空项", getApplicationContext());

                    Log.w("EMMLogin","不能有空项");
                    return;
                }

             //   loginLoadingBar.setVisibility(View.VISIBLE);

                new Thread(new Runnable(){
                    @Override
                    public void run()
                    {
                        try
                        {
                            //-----------add  bt gwb;2020.9.14
                     //       String TelVersion = android.os.Build.MODEL;
                     //       TelVersion = TelVersion.replaceAll(" ", "-");//发现如果里面有空格，则认证一定不成功。

                            DeviceInfoUtil.initDeviceInfo();

                            String szHostName =   android.os.Build.BRAND+"-" + EMMApp.getInstance().deviceName;// 手机品牌+手机型号

                            szHostName = szHostName.replaceAll(" ", "-");//发现如果里面有空格，则认证一定不成功。

                            String url = "http://" + ad + "/TelSafeDesk.php?Action=login&Mac=" +
                                    SystemDataGet.getMacAddress(LoginSetting.this)
                                    + "&Ip=" + SystemDataGet.getIp(getApplicationContext())
                                    + "&TelVersion=" + szHostName
                                    + "&UserName=" + id
                                    + "&Pwd=" + pw
                                    + "&DiskNum=" + EMMApp.getInstance().diskNum;   //add by fsy 2022.4.22

                            Log.w("EMMLogin","Login url:"+url);

                            String result = HttpRequest.httpGet(url, null);

                            //2021.3.17 注意：服务器php文件必须是没有UTF8 3个字节头的文件，否则这边一直会判断失败，应该返回的数据CHECKOK前面多了UTF8头了。
                            String re = "";
 							//mod by fsy 2021.11.3 如果有UFT8的签名头，去掉
                            if(65279==(int)result.charAt(0))   
                            {
                                re = result.substring(1);
                            }else
                            {
                                re = result;
                            }

                            if ("CHECKOK".equals(re))
                            {
                                Log.w("EMMLogin","CHECKOK");
                                //System.out.println("登陆-----CHECKOK.");
                                handler.sendEmptyMessage(1);

                                Save.putValue(LoginSetting.this, "admin_id", id);//add by gwb;2020.10.15
                                Save.putValue(LoginSetting.this, "admin_pwd", pw);
                            }
                            else
                            {
                                Log.w("EMMLogin","CHECKError");
                                //System.out.println("登陆-----CHECKError.");
                                Message message = new Message();
                                if (re != null && re.length() > 0)
                                {
                                    Log.w("EMMLogin","用户名或密码错误");
                                    Tell.toast("登录失败" , getApplicationContext());
                                    //message.obj = "登录失败:" + "\n" + "url=" + url + "\n" + "后台LOG：" + re;
                                    message.obj = "登录失败：用户名或密码错误["+re+"]";
                                    //loginLoadingBar.setVisibility(View.INVISIBLE);  del by gwb;2020.9.23 线程里操作界面可能报错，先删除了
                                }
                                else
                                {

                                    Log.w("EMMLogin","登录失败");
                                    //message.obj = "登录失败:" + "\n" + "url=" + url;
                                    message.obj = "登录失败!";
                                    //loginLoadingBar.setVisibility(View.INVISIBLE); del by gwb;2020.9.23 线程里操作界面可能报错，先删除了
                                }
                                message.what = 3;
                                handler.sendMessage(message);
                            }

                        }
                        catch (Exception e)
                        {
                            Tell.log(e.toString());
                            Message message = new Message();
                            message.what = 2;
                            //message.obj = e.toString();
                            message.obj = "登录失败：网络连接异常！";
                            handler.sendMessage(message);
                            //loginLoadingBar.setVisibility(View.INVISIBLE); del by gwb;2020.9.23 当登陆失败时，好像调用到这个会报错。线程里能直接调用界面吗？
                        }
                    }
                }).start();
                Log.w("EMMLogin","Login end");

            }
        });


        admin_pw.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_NEXT)
                {//判断动作标识是否匹配
                    // To do something
                }
                if (actionId == EditorInfo.IME_ACTION_GO)
                {
                    go_login.performClick();
                }
                return false;
            }
        });
    }


    /**
     * 添加平板和手机的判定
     */
    void loginOK()
    {
        NetCtrlHub.get().setServiceAd(ad);

        NetDataHub.m_ManagerLogon = true;//add by gwb;2020.9.16  刚登陆成功。


        sendBroadcastInfo();

        loginLoadingBar.setVisibility(View.INVISIBLE);
        if (isPad(getApplicationContext()))
        {
            startActivity(new Intent(getApplicationContext(), AdminSettings.class));
            finish();
        }
        else
        {
            startActivity(new Intent(getApplicationContext(), SettingsPhone.class));
            finish();
        }
//        startActivity(new Intent(getApplicationContext(), Setting.class));
//        finish();
    }

    private void sendBroadcastInfo()
    {
        Intent intent = new Intent();
        intent.setAction("com.grampus.hualauncherkai.action.START_RECEIVER");
        sendBroadcast(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_login);

        Log.w("EMMLogin", "LoginSetting------onCreate---");
       // g_context= getApplicationContext();
        initView();

        setStatusBarColor(this, R.color.brightBlue);
//       setNavigationBarColor(this, R.color.brightBlue);
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值

    }


}
