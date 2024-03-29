package com.grampus.hualauncherkai.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grampus.hualauncherkai.Data.SystemDataGet;
import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.UI.GridViewAdpter;

import java.util.Calendar;
import java.util.List;

import static com.grampus.hualauncherkai.UI.MainActivity.szVersionNum;

/**
 * Created by Grampus on 2017/4/18.
 */

public class AppFragment extends Fragment
{

    GridView gridView;
    View view;
    List<ResolveInfo> appInfoList;
    GridViewAdpter gridViewAdpter;
    Context context;
    //DigitalClock digitalClock;
    LinearLayout lyInfo;
    TextView tvDate;
    TextView tvTime;
    TextView tvDeviceName;
    TextView tvIp;
    TextView tvMAC;
    TextView tvMAC0;
    int pageNum;
    Runnable mTicker;
    Calendar mCalendar = Calendar.getInstance();


    private final static String mFormat = "yyyy年MM月dd日 EEEE";
    private static String mFormatTime = "HH:mm";

    public AppFragment(Context context, List<ResolveInfo> appInfoList, int pageNum)
    {
        try {
            this.pageNum = pageNum;
            this.context = context;
            this.appInfoList = appInfoList;

/*            for (int i = 0; i < appInfoList.size()-2; i++) {
                Log.w("EMMAPPFra",appInfoList.size()+"|"+appInfoList.get(i).activityInfo.name);
            }
*/
            gridViewAdpter = new GridViewAdpter(this.context, this.appInfoList);
        }
        catch(Exception e) {
            Log.w("EMMAPPFra","new AppFragment error:"+e.toString());
        }
    }

    public AppFragment()
    {
    }

    void initView()
    {

        gridView = view.findViewById(R.id.grid_view);
        //digitalClock = (DigitalClock) view.findViewById(R.id.analogClock);
        lyInfo = view.findViewById(R.id.ly_analogClock);
        tvDate = view.findViewById(R.id.tv_date);
        tvTime = view.findViewById(R.id.tv_time);
        tvDeviceName = view.findViewById(R.id.tv_deivename);
        tvIp = view.findViewById(R.id.tv_ip);
        tvMAC = view.findViewById(R.id.tv_mac);
        tvMAC0 = view.findViewById(R.id.tv_mac0);   //MAC字母所在的VIEW

		//在两个安卓4.1设备上发现24小时制显示时间错误
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
            mFormatTime = "hh:mm";
        }
        try{
            if(EMMApp.getInstance().textColor!=-1){
                tvDate.setTextColor(EMMApp.getInstance().textColor);
                tvTime.setTextColor(EMMApp.getInstance().textColor);
                tvDeviceName.setTextColor(EMMApp.getInstance().textColor);
                tvIp.setTextColor(EMMApp.getInstance().textColor);
                tvMAC.setTextColor(EMMApp.getInstance().textColor);
                tvMAC0.setTextColor(EMMApp.getInstance().textColor);
                Log.w("EMMMain", "textColor2:"+EMMApp.getInstance().textColor);
            }

        }catch (Exception e){
            Log.e("EMMMain","e:"+e.toString());
        }

        if (pageNum != 0)
        {
            //digitalClock.setVisibility(View.GONE);
            lyInfo.setVisibility(View.GONE);    //GONE:不会占用空间 INVISIBLE:仍旧会占用空间，只是内容不显示。

        }
        else
        {
            lyInfo.setVisibility(View.VISIBLE);
            /*digitalClock.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Tell.log("你长按了时钟");
                    return true;
                }
            });*/
            setText();
        }

        gridView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Log.w("EMMMain", "gridView-------OnLongClickListener");
                final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
                Intent chooser = Intent.createChooser(pickWallpaper, "chooser_wallpaper");
                //发送设置壁纸的请求
                startActivity(chooser);
                return true;
            }
        });

    }

    @Override
    public void onStart()
    {
        //gridViewAdpter = new GridViewAdpter(this.context, this.apps);del by gwb;2021.3.25  这块感觉不需要了。而且如果有这一句，登陆失败后再返回会桌面上图标全消失掉。
        if(gridViewAdpter != null)
            gridView.setAdapter(gridViewAdpter);

        super.onStart();
    }

    public String show()
    {
        String s = "";
        for (ResolveInfo one : appInfoList)
        {
            s += one.loadLabel(getActivity().getPackageManager()).toString() + " ";
        }
        return s;
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        //Tell.log("一页View消掉了--------" + pageNum);
        mHandler.removeCallbacks(mTicker);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Tell.log("一页消掉了--------" + pageNum);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.gridview, container, false);
        initView();
        startTimeLock();
        return view;
    }

    //2022.12.29 add Looper.getMainLooper()
    private Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            setText();
            //System.out.println("AppFragment-----handleMessage-----定时时处理");
        }
    };

    private void startTimeLock()
    {
        if (pageNum != 0)
        {
            return;
        }
        mTicker = new Runnable()
        {
            @Override
            public void run()
            {
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);//抹零头其实没啥意义，本来获取的时间就是系统启动时间而已
                mHandler.sendEmptyMessage(1);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    private void setText()
    {

        mCalendar.setTimeInMillis(System.currentTimeMillis());
        String date = DateFormat.format(mFormat, mCalendar).toString();
        String time = DateFormat.format(mFormatTime, mCalendar).toString();
        //tvDate.setText(date + " v201017\n");

        tvDate.setText(date + " " +szVersionNum + "\n");
        tvTime.setText(time);
        tvDeviceName.setText(EMMApp.getInstance().deviceName);//add by fsy 2021.2.15 避免每秒重复查询，在定时器中更新这个
        //tvIp.setText(DeviceInfoUtil.getPhoneIp());
        tvIp.setText(SystemDataGet.getIp(context));//这个会更准确一些。add by gwb;2021.7.15
        tvMAC.setText(EMMApp.getInstance().macAddr);	//add by fsy 2021.2.15 避免每秒重复查询，在定时器中更新这个
        //tvMAC.setText(getWifiMacAddress());

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


}
