package com.grampus.hualauncherkai.UI;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.AppDataHub;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.Tools.Tell;

import java.util.List;

import static com.grampus.hualauncherkai.Data.NetDataHub.isShowAppStore;

/**
 * Created by Grampus on 2017/4/18.
 */

public class GridViewAdpter extends BaseAdapter
{
    private MyViewHolder myViewHolder;
    List<ResolveInfo> appInfoList;
    Context context;


    public GridViewAdpter(Context context, List<ResolveInfo> appInfoList)
    {
        try {
            this.context = context;
            this.appInfoList = appInfoList;
        }
        catch(Exception e){

            Toast.makeText(context, "GridViewAdpter 出现异常！", Toast.LENGTH_SHORT).show();
            System.out.println("GridViewAdpter----构造异常!");
            e.printStackTrace();
        }
    }

    public GridViewAdpter()
    {
    }

    @Override
    public int getCount()
    {
        if(appInfoList == null)
            return 0;

        return appInfoList.size();
    }

    @Override
    public Object getItem(int i)
    {
        return appInfoList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup)
    {
        final ResolveInfo info = appInfoList.get(i);

       // System.out.println("getView----i:"+i+"  convertView:" + convertView + "  viewGroup: "+viewGroup);//这是好像是每一个显示的APP都会走到这个，只是显示一页的数据。
        if (convertView == null)
        {

            myViewHolder = new MyViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.text_img_view, null);
            myViewHolder.icon = convertView.findViewById(R.id.image);
            myViewHolder.text = convertView.findViewById(R.id.text);

            //自定义颜色
            try{
                if(EMMApp.getInstance().textColor!=-1){
                    myViewHolder.text.setTextColor(EMMApp.getInstance().textColor);
                    Log.w("EMMMain", "textColor3:"+EMMApp.getInstance().textColor);
                }
            //
            }catch (Exception e){
                Log.e("EMMMain","e:"+e.toString());
            }

            myViewHolder.layout = convertView.findViewById(R.id.layout);
            convertView.setTag(myViewHolder);
        } else
        {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        if (info != null && info.icon != -1 && info.icon != -2)
        {//正常的APP
         //   Log.w("EMMMain","getView-1--i:" + i +"|info.icon ="+info.icon );
            try
            {
                String text = info.loadLabel(context.getPackageManager()).toString();
                //Log.w("EMMMain","text:" + text +"|i="+i);
               // System.out.println("getView----i:"+i+"  text:" + text + "    info.icon:" + info.icon);

                Drawable drawable = info.activityInfo.loadIcon(context.getPackageManager());
                myViewHolder.text.setText(text);
                myViewHolder.icon.setImageDrawable(drawable);

                myViewHolder.layout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ResolveInfo info = appInfoList.get(i);
                        //该应用的包名
                        String pkg = info.activityInfo.packageName;
                        //应用的主activity类
                        String cls = info.activityInfo.name;
                     //   Log.w("EMMMain","packageName:"+pkg+" -- name:"+cls);
                    //    NetDataHub.get().addLog("packageName:"+pkg+"--name"+cls);
                        ComponentName componet = new ComponentName(pkg, cls);
                        Intent intent = new Intent();
                        intent.setComponent(componet);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });//packageName:com.tencent.mm name:com.tencent.mm.ui.LauncherUI
                //packageName:com.tencent.qqmusic name:com.tencent.qqmusic.activity.AppStarterActivity

                /* del by gwb;2021.3.23  先不用快捷方式了。*/
                myViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        Tell.log("长按了");
                        AppDataHub.addSettingApp(info);
                        Intent intent = new Intent(context, AppSetting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        return true;
                    }
                });

                 /**/
            } catch (Exception e)
            {
            //    Log.e("EMMMain","getview -- icon:"+info.icon+"|error:"+e.getMessage());
                myViewHolder.text.setText("");
                myViewHolder.icon.setImageDrawable(null);
            }
        } else
        {//管理员设置界面模拟APP
            Tell.log("getView---i:" + i + "apps.Size:" + appInfoList.size() + "\n");
           // Log.w("EMMMain","getView---i:" + i +"|size="+appInfoList.size() +"|isShowAppStore:"+isShowAppStore);
            //if ((apps.size() - 1) == i)
            if ((appInfoList.size() - 1) == i && appInfoList.size()>1 && isShowAppStore)
                //add by gwb;2020.10.22 当管理员设置图标正好在一页的最后一个时，会变成应用商店，导致有两个应用商店存在。
            {
                String text = "应用商店";
                myViewHolder.text.setText(text);
                myViewHolder.icon.setImageResource(R.drawable.ico_02);

                myViewHolder.layout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //Save.putValue(context, "service_ad", "117.89.71.180:16670");
                        String serviceAd = Save.getValue(context, "service_ad", "");
                        if (!serviceAd.equals(""))
                        {
                            Intent intent = new Intent(context, AppStoreActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else
                        {
                            Tell.toast("请先登录管理员用户设置IP", context);
                        }
                    }
                });
                /* del by gwb;2021.3.24
                myViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        Tell.toast("应用商店不能设置快捷", context);

                        return true;
                    }
                });
                 */
            }
            else
            {
                String text = "管理员设置";
                myViewHolder.text.setText(text);
                myViewHolder.icon.setImageResource(R.mipmap.ico);

                myViewHolder.layout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(context, LoginSetting.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                /* del by gwb;2021.3.24
                wuViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        Tell.toast("管理员设置不能设置快捷", context);

                        return true;
                    }
                });

                 */
            }
        }


        return convertView;

    }

    private class MyViewHolder
    {
        private ImageView icon;
        private TextView text;
        private LinearLayout layout;
    }
}
