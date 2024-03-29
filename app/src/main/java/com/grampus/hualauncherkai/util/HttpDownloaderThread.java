package com.grampus.hualauncherkai.util;
import android.app.ProgressDialog;
import android.util.Log;

import com.grampus.hualauncherkai.Data.AppItemRes;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *  @author fsy
 *  @date 2021/12/23 16:02
 * Http下载线程，目前两个地方用到
 * 软件商店和APK升级
 */
public class HttpDownloaderThread extends Thread{
    private URL url = null;
    private final String TAG = "EMMDownloaderThread";

    private String urlStr;
    private String path;
    private String fileName;

    private ProgressDialog updateDialog;
    private AppItemRes appItem;
    private int apkSize;

    public HttpDownloaderThread(String urlStr, String path, AppItemRes appItem) {
        this.urlStr = urlStr;
        this.path = path;
        this.appItem = appItem;
        this.fileName = appItem.getApkName();
        this.updateDialog = appItem.getAppDialog();
    }
    public HttpDownloaderThread(String urlStr, String path,String fileName,ProgressDialog updateDialog) {
        this.urlStr = urlStr;
        this.path = path;
        this.fileName = fileName;
        this.updateDialog = updateDialog;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public AppItemRes getAppItem() {
        return appItem;
    }

    public void setAppItem(AppItemRes appItem) {
        this.appItem = appItem;
    }


    /**
     *
     * @param urlStr   网址
     * @param path  本地保存的路径
     * @param fileName 本地保存的路径文件名
     * @param updateDialog 进度条对话框
     * @return  ，代表下载失败。返回0，代表成功。返回1代表文件已经存在，应该是走不到的。
     */
    public int downlaodApk(String urlStr, String path,String fileName,ProgressDialog updateDialog) {

        Log.w(TAG,"updateApk--------3");
        InputStream input = null;
        try {
            FileUtil fileUtil = new FileUtil();
            fileUtil.setUpdateDialog(updateDialog);
            if (fileUtil.isFileExist(path,fileName)) {
                return 1;
            } else {
                input = getInputStearmFormUrl(urlStr);
                File resultFile = fileUtil.write2SDFromInput(path,fileName,input,apkSize);
                if (resultFile == null) {
                    return -1;
                }
            }
        } catch (Exception e) {
            Log.e("EMM","downlaodFile1 error"+e.toString());
            e.printStackTrace();
            return -1;
        }
        finally {
            try {
                input.close();
            } catch (Exception e) {
                Log.e("EMM","downlaodFile error:"+e.toString());
                e.printStackTrace();
            }
        }
        return  0;
    }

    /**
    * @author  fsy
    * @date    2021/12/23 18:30
    * @return
    * @description
    */
    public int downlaodFile(String urlStr, String path, AppItemRes appItem) {

        InputStream input = null;
        try {
//            FileUtil fileUtil = new FileUtil();
            FileUtil fileUtil = new FileUtil(appItem);
            if (fileUtil.isFileExist(path,appItem.getApkName())) {
                Log.w("EMM","downlaodFile----isFileExist");
                return 1;
            } else {
                input = getInputStearmFormUrl(urlStr);
                File resultFile = fileUtil.write2SDFromInput(path,appItem.getApkName(),input,apkSize);
                if (resultFile == null) {
                    return -1;
                }
            }
        } catch (Exception e) {
            Log.e("EMM","downlaodFile1 error"+e.toString());
            e.printStackTrace();
            return -1;
        }
        finally {
            try {
                input.close();
            } catch (Exception e) {
                Log.e("EMM","downlaodFile error:"+e.toString());
                e.printStackTrace();
            }
        }
        return  0;
    }


    public InputStream getInputStearmFormUrl(String urlStr) {
        InputStream input = null;
        try{
   //         Log.d("EMM","getInputStearmFormUrl-----:"+urlStr);
            url = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            input = urlConn.getInputStream();
            apkSize = urlConn.getContentLength();
            Log.d("EMM","getInputStearmFormUrl----size:"+apkSize);

        }catch(Exception e)
        {
            Log.e("EMM","getInputStearmFormUr error"+e.toString());
        }

        return input;
    }

    @Override
    public void run() {
        if(appItem!=null)
            downlaodFile(urlStr,path,appItem);
          //  downlaodFile(urlStr,path,appItem,fileName,updateDialog);
            //downlaodFile(urlStr,path,fileName,updateDialog);
        else
            downlaodApk(urlStr,path,fileName,updateDialog);
    }
}