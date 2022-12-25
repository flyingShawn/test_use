package com.grampus.hualauncherkai.util;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.AppItemRes;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.service.EMMAccessibilityService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;



/**
 *  @author fsy
 *  @date 2021/12/23 16:02
 *  目前是Http下载线程用的文件下载工具，目前两个地方用到
 * 软件商店和APK升级
 */
public class FileUtil {


    private AppItemRes appItem;

    private ProgressDialog updateDialog;
    public FileUtil( AppItemRes appItem) {
        this.appItem = appItem;
        updateDialog = appItem.getAppDialog();
    }

    public FileUtil() {
    }

    public ProgressDialog getUpdateDialog() {
        return updateDialog;
    }

    public void setUpdateDialog(ProgressDialog updateDialog) {
        this.updateDialog = updateDialog;
    }


    /**
     * 在SD卡上创建文件
     * @param fileName
     * @return
     * @throws Exception
     */
    public File createSDFile(String path,String fileName) throws Exception {

        File file = new File(path + "/" + fileName);
    //    Log.w("EMM","createSDFile-------"+path + "/" + fileName);

        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     * @param dirName 目录名字
     * @return 文件目录
     */
    public File createDir(String dirName){
        Log.w("EMM","createDir-------");
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExist(String path,String fileName){
        Log.w("EMM","isFileExist-------");
        File file = new File(path + "/"+fileName);
        return file.exists();
    }

    public File write2SDFromInput(String path,String fileName,InputStream input,int apkSize){
        File file = null;
        OutputStream output = null;
        Log.d("EMM","FileUtil---------write2SDFromInput");
        try {
            //createDir(path);

            file =createSDFile(path,fileName);
            output = new FileOutputStream(file);

            byte [] buffer = new byte[16 * 1024];

            int initial = 0;
            int totalSize = 0;
            while(true){

                int nRead = input.read(buffer);
                if(nRead<1)
                    break;
    //            Log.w("EMM","nRead :"+nRead+" apkSize :"+apkSize);

                totalSize += nRead;
                int n = 100*totalSize/apkSize;
                if(n>1) {
                    initial += n;
                    updateDialog.setProgress(initial);
                    totalSize = 0;
                }

                output.write(buffer,0,nRead);
                output.flush();
            }
            if(appItem!=null)
            {
                appItem.setDownLoading(false);
                appItem.setPath(file.getAbsolutePath());
            }
            updateDialog.dismiss();


            Log.e("EMMPath","path:"+file.getAbsolutePath());
            inStallFile(file);  //下载完成安装
            EMMApp.getInstance().shouldUpdate = false;
            Log.w("EMMA11y", "shouldUpdate = false，下载完成");

        } catch (Exception e) {
            Log.e("EMM","write2SDFromInput1 error"+e.toString());
            e.printStackTrace();
        }
        finally {
            try {
                Log.w("EMM","write2SDFromInput-------output.close()");
                output.close();
            } catch (Exception e) {
                Log.e("EMM","write2SDFromInput2 error"+e.toString());
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void installApk(File file) {
        Uri uri = null;
        try {

            NetDataHub.get().addLog("installApk-----下载完成，开始执行安装:"+file.getPath());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if(EMMAccessibilityService.isStart()){
                            if(EMMAccessibilityService.getInstance().isUpdateApk == true)
                                EMMAccessibilityService.getInstance().autoUpdate();
                            EMMAccessibilityService.getInstance().isUpdateApk = false;
                        }
                    }
                }
            }).start();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//为intent 设置特殊的标志，会覆盖 intent 已经设置的所有标志。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0 以上版本利用FileProvider进行访问私有文件
                uri = FileProvider.getUriForFile(EMMApp.getInstance().mainContext, "com.grampus.hualauncherkai.fileprovider", file);
            } else {
                //直接访问文件
                uri = Uri.fromFile(file);

            }
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            EMMApp.getInstance().mainContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void inStallFile(File file)
    {
        try {
            if(file == null) {
                Log.i("EMM", "开始安装---file == null:");
                return;
            }
            if (!file.exists()) {
                NetDataHub.get().addLog("inStallFile----下载完成，但是文件不存在"+file.getPath());
                return;
            }
            Log.i("EMM", "开始安装---1");
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                boolean installAllowed =true;
                if(EMMApp.getInstance().mainContext != null)
                {
                    Log.i("EMM","开始安装---EMMApp.getInstatnce().mainContext != null");
                    if(EMMApp.getInstance().mainContext.getPackageManager()!=null) {
                        installAllowed = EMMApp.getInstance().mainContext.getPackageManager().canRequestPackageInstalls();//是否允许安装包
                        Log.i("EMM","开始安装---getPackageManager != null");
                    }
                }

                Log.i("EMM","开始安装---installAllowed:"+installAllowed);
                if(installAllowed){
                    installApk(file);//允许，安装
                }else {
                    //sleep 1000,然后再来一次？
                    //跳转到设置页面，设置成允许安装
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + EMMApp.getInstance().mainContext.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    EMMApp.getInstance().mainContext.startActivity(intent);
                    installApk(file);
                    return;
                }
            }
            //版本低于8.0
            else {
                installApk(file);
            }
        }
        catch(Exception e) {
            Log.i("EMM","inStallFile-----安装下载文件失败!!!:"+e.toString());

//            System.out.println("inStallFile-----安装下载文件失败!!!");
        }
    }
}