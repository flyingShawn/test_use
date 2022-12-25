package com.grampus.hualauncherkai.Tools.DownloadUtils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.grampus.hualauncherkai.Data.AppItemRes;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.UI.AppStoreAdpter;
import com.grampus.hualauncherkai.util.DateUtil;

import java.io.File;

public class DownloadFile
{
    public DownloadFile(String rootPath)
    {
        File file = new File(rootPath);
        if (file == null)
        {
            throw new IllegalStateException("Failed to get external storage files directory");
        }
        else if (file.exists())
        {
            if (!file.isDirectory())
            {
                throw new IllegalStateException(file.getAbsolutePath() +
                        " already exists and is not a directory");
            }
        }
        else
        {
            if (!file.mkdirs())
            {
                throw new IllegalStateException("Unable to create directory: " + file.getAbsolutePath());
            }
        }
    }

    public DownloadFile()
    {

    }

    public void Download(String url, String rootPath, Activity activity, AppItemRes aItems, AppStoreAdpter adapter)
    {
        try {
            // 创建下载任务
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            // 漫游网络是否可以下载
            request.setAllowedOverRoaming(false);

            //设置文件名
            String filename = DateUtil.getCurrentTimeMills() + ".apk";

            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
            request.setMimeType(mimeString);

            // 在通知栏中显示，默认就是显示的
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);

            // sdcard的目录下的download文件夹，必须设置
            request.setDestinationInExternalFilesDir(activity, rootPath, filename);

            // 将下载请求加入下载队列
            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            // 加入下载队列后会给该任务返回一个long型的id，
            // 通过该id可以取消任务，重启任务等等
            long taskId = downloadManager.enqueue(request);

            NetDataHub.get().addLog("EMM----Download---taskId :"+taskId);

            //注册广播接收者，监听下载状态
            DownLoadReceiver downLoadReceiver = new DownLoadReceiver(activity, downloadManager, taskId, filename, aItems, adapter);
            activity.registerReceiver(downLoadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
        catch(Exception e) {
            NetDataHub.get().addLog("EMM----Download---catch error:"+e.toString());
            e.printStackTrace();
        }

    }
}
