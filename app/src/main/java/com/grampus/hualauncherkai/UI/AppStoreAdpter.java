package com.grampus.hualauncherkai.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grampus.hualauncherkai.Data.AppItemRes;
import com.grampus.hualauncherkai.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DGY on 2018/1/6.
 */

public class AppStoreAdpter extends BaseAdapter
{
//    private MyViewHolder wuViewHolder;
    private AppStoreListViewItem listViewItem;
    private LayoutInflater mlayoutinflater;
    List<AppItemRes> apps;
    Context context;


    public AppStoreAdpter(Context context, List<AppItemRes> apps)
    {
        this.context = context;
        mlayoutinflater = LayoutInflater.from(this.context);
        this.apps = apps;
    }

    public AppStoreAdpter(Context context)
    {
        super();
        this.context = context;
        mlayoutinflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount()
    {
        if (apps != null)
        {
            return apps.size();
        }
        return 0;
    }

    @Override
    public AppItemRes getItem(int i)
    {
        return apps.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup)
    {
        final AppItemRes info = apps.get(i);

        if (convertView == null)
        {
//            wuViewHolder = new MyViewHolder();
//            convertView = mlayoutinflater.inflate(R.layout.item_store, null);
//            wuViewHolder.icon = (ImageView) convertView.findViewById(R.id.img_p);
//            wuViewHolder.tvName = (TextView) convertView.findViewById(R.id.text_name);
//            wuViewHolder.tvSize = (TextView) convertView.findViewById(R.id.text_size);
//            wuViewHolder.tvState = (TextView) convertView.findViewById(R.id.text_state);
//            convertView.setTag(wuViewHolder);

            listViewItem = new AppStoreListViewItem();
            convertView = mlayoutinflater.inflate(R.layout.appstore_listview_item, null);
            listViewItem.mainLayout = convertView.findViewById(R.id.listview_item_main_layout);
            listViewItem.icon = convertView.findViewById(R.id.listview_item_icon);
            listViewItem.textLayout = convertView.findViewById(R.id.listview_item_text_layout);
            listViewItem.appName = convertView.findViewById(R.id.listview_item_appname);
            listViewItem.appSize = convertView.findViewById(R.id.listview_item_appsize);
            listViewItem.buttonLayout = convertView.findViewById(R.id.listview_item_button_layout);
            listViewItem.progressBar = convertView.findViewById(R.id.listview_item_progressbar);
            listViewItem.button = convertView.findViewById(R.id.listview_item_button);

            convertView.setTag(listViewItem);
        }
        else
        {
//            wuViewHolder = (MyViewHolder) convertView.getTag();
            listViewItem = (AppStoreListViewItem) convertView.getTag();
        }
//        wuViewHolder.tvName.setText(getItem(i).getAppName());
//        wuViewHolder.tvSize.setText(getItem(i).getAppSize());
        listViewItem.appName.setText(getItem(i).getAppName());
        listViewItem.appSize.setText(getItem(i).getAppSize());

        if (getItem(i).isDownLoading())
        {
            //wuViewHolder.tvState.setText("下载中...");
            listViewItem.button.setVisibility(View.INVISIBLE);
            listViewItem.progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            /**
             * 此处应做进一步处理
             */
//            if (getItem(i).getProgress() == 0)
//            {
//                wuViewHolder.tvState.setText("下载");
//            }
//            else if (getItem(i).getProgress() == 100)
//            {
//                wuViewHolder.tvState.setText("安装");
//            }
//            else
//            {
//                wuViewHolder.tvState.setText("下载中...");
//            }
            listViewItem.button.setVisibility(View.VISIBLE);
            listViewItem.progressBar.setVisibility(View.INVISIBLE);
        }

//        ImageProUtil.updateImage(wuViewHolder.icon, getItem(i).getProgress());
//        ImageProUtil.updateImage(listViewItem.icon, getItem(i).getProgress());


        return convertView;

    }

    public void setContentList(ArrayList<AppItemRes> contentList)
    {
        this.apps = contentList;
    }

//    private class MyViewHolder
//    {
//        private ImageView icon;
//        private TextView tvState;
//        private TextView tvName;
//        private TextView tvSize;
//        private LinearLayout layout;
//    }

    private class AppStoreListViewItem
    {
        LinearLayout mainLayout;
        ImageView icon;
        LinearLayout textLayout;
        TextView appName;
        TextView appSize;
        FrameLayout buttonLayout;
        ImageView button;
        ProgressBar progressBar;
    }

}
