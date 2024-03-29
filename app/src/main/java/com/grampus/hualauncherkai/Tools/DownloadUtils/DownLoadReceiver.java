package com.grampus.hualauncherkai.Tools.DownloadUtils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.grampus.hualauncherkai.Data.AppItemRes;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.UI.AppStoreAdpter;

import java.io.File;


import static com.grampus.hualauncherkai.UI.AppStoreActivity.isDownLoad;

public class DownLoadReceiver extends BroadcastReceiver
{
    private String mTag = getClass().getSimpleName();
    //    private NotificationManager nm;
    //    private Intent mIntent;
    private DownloadManager downloadManager;
    private long mTaskId;
    private Context mContext;
    private String mFileName;
    private ImageView iv_main;
    private AppItemRes aItems;
    private AppStoreAdpter adapter;

    public DownLoadReceiver(Context context, DownloadManager downloadManager, long taskId, String fileName, AppItemRes aItems, AppStoreAdpter adapter)
    {
        NetDataHub.get().addLog("EMM----Download---onReceive -----fileName:"+fileName);
        this.downloadManager = downloadManager;
        this.mTaskId = taskId;
        this.mContext = context;
        this.mFileName = fileName;
        this.aItems = aItems;
        this.adapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // 检查下载状态
        NetDataHub.get().addLog("EMM----Download---onReceive -----");
        checkDownloadStatus(context);
    }

    //检查下载状态
    private void checkDownloadStatus(Context context)
    {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst())
        {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status)
            {
                case DownloadManager.STATUS_PAUSED:
                    //aItems.setDownLoading(false);
                    Toast.makeText(mContext, "下载暂停", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    //aItems.setDownLoading(false);
                    Toast.makeText(mContext, "下载延迟", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_RUNNING:
                    //aItems.setDownLoading(true);
                    Toast.makeText(mContext, "正在下载", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Toast.makeText(mContext, "下载完成,即将安装", Toast.LENGTH_SHORT).show();
                    //File file = new File(dir, mFileName); del by gwb;2020.10.21

                    String szDirPath = mContext.getExternalFilesDir("Download").getPath();
                    File file = new File(szDirPath, mFileName);

                    if(aItems != null) {
                        isDownLoad=false;
                        aItems.setDownLoading(false);
                        aItems.setPath(file.getAbsolutePath());
                    }
                    if(adapter != null){
                        adapter.notifyDataSetChanged();
                    }


                    inStallFile(file);
                    //Log.d("文件", "存在不：" + file.exists() + "路径" + file.getPath());
                    c.close();
                    break;
                case DownloadManager.STATUS_FAILED:
                    //aItems.setDownLoading(false);
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        c.close();

        mContext.unregisterReceiver(this);
    }
    //安装apk
    private void installApk(File file) {//add by gwb;2020.10.21
        Uri uri = null;
        try {

            NetDataHub.get().addLog("installApk-----下载完成，开始执行安装:"+file.getPath());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//为intent 设置特殊的标志，会覆盖 intent 已经设置的所有标志。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0 以上版本利用FileProvider进行访问私有文件
                uri = FileProvider.getUriForFile(mContext, "com.grampus.hualauncherkai.fileprovider", file);
            } else {
                //直接访问文件
                uri = Uri.fromFile(file);

            }
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//为intent 添加特殊的标志，不会覆盖，只会追加。
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inStallFile(File file)
    {
        try {
            if (!file.exists()) {
                NetDataHub.get().addLog("inStallFile-----下载完成，但是文件不存在"+file.getPath());
                return;
            }

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                boolean installAllowed=mContext.getPackageManager().canRequestPackageInstalls();//是否允许安装包
                if(installAllowed){
                    installApk(file);//允许，安装
                }else {
                    //跳转到设置页面，设置成允许安装
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + mContext.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    installApk(file);
                    return;
                }
            }
            //版本低于8.0
            else {
                installApk(file);
            }



            /* del by gwb;2020.10.21
            Uri uri = FileProvider.getUriForFile(mContext, "com.grampus.hualauncherkai.fileprovider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");


            mContext.startActivity(intent);

             */
        }
        catch(Exception e) {
            System.out.println("inStallFile-----安装下载文件失败!!!");
        }
    }
}
