package com.grampus.hualauncherkai.UI;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grampus.hualauncherkai.Data.AppDataHub;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.Tell;

/**
 * Created by Grampus on 2017/4/18.
 */

public class OffenApp
{
    static ResolveInfo[] offenInfo;
    static Activity activity;
    static Handler handler;

    static public boolean isSettingOffen = false;


    final static int[] ivId = new int[]{R.id.offen_icon1, R.id.offen_icon2, R.id.offen_icon3, R.id.offen_icon4};
    final static int[] loId = new int[]{R.id.offen_layout1, R.id.offen_layout2, R.id.offen_layout3, R.id.offen_layout4};
    final static int[] tvId = new int[]{R.id.offen_text1, R.id.offen_text2, R.id.offen_text3, R.id.offen_text4};
    static OffenApp[] offenApp = new OffenApp[AppDataHub.ItemNum];


    static public void init(Activity _Activity, Handler hd)
    {
        handler = hd;
        activity = _Activity;
        for (int i = 0; i < offenApp.length; i++)
        {
            offenApp[i] = new OffenApp(ivId[i], tvId[i], loId[i]);
        }
    }

    static public void load(ResolveInfo[] _offenInfo)
    {
        offenInfo = _offenInfo;
        for (int i = 0; i < offenInfo.length; i++)
        {
            offenApp[i].set(offenInfo[i]);
        }
    }


    ImageView imageView;
    TextView textView;
    LinearLayout linearLayout;

    public OffenApp(int iv, int tv, int lo)
    {
        this.linearLayout = activity.findViewById(lo);
        this.imageView = activity.findViewById(iv);
        this.textView = activity.findViewById(tv);
    }

    public void set(final ResolveInfo resolveInfo)
    {
        if (resolveInfo != null)
        {
            imageView.setImageDrawable(resolveInfo.activityInfo.loadIcon(activity.getPackageManager()));
            textView.setText(resolveInfo.loadLabel(activity.getPackageManager()));


            linearLayout.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (resolveInfo != null)
                    {
                        AppDataHub.addSettingApp(resolveInfo);
                        Intent intent = new Intent(activity, AppSetting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        activity.startActivity(intent);
                    }


                    return true;
                }
            });
        } else
        {
            imageView.setImageDrawable(null);
            textView.setText("");
        }

        linearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //该应用的包名
                if (!isSettingOffen)
                {
                    if (resolveInfo != null)
                    {
                        String pkg = resolveInfo.activityInfo.packageName;
                        //应用的主activity类
                        String cls = resolveInfo.activityInfo.name;
                        ComponentName componet = new ComponentName(pkg, cls);
                        Intent intent = new Intent();
                        intent.setComponent(componet);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        activity.startActivity(intent);
                    } else
                    {
                        Tell.toast("请点击图标设置快捷启动应用", activity);
                    }
                } else
                {
                    isSettingOffen = false;
                    AppDataHub.setNewOffen(resolveInfo);
                    handler.sendEmptyMessage(1);


                }
            }
        });


    }

}
