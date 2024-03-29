package com.grampus.hualauncherkai.Data;

import android.app.ProgressDialog;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DGY on 2018/1/6.
 */

public class AppItemRes
{
    @SerializedName("Name1")
    private String appName = "";//软件名称
    @SerializedName("Name2")
    private String ApkName = "";//安装包名称
    @SerializedName("Name3")
    private String appSize = "";//安装包大小
    @SerializedName("Name4")
    private String appPkg = "a";//安装包pkg.

    private ProgressDialog appDialog = null;   //add by fsy 2021.12.23

    public ProgressDialog getAppDialog() {
        return appDialog;
    }

    public void setAppDialog(ProgressDialog appDialog) {
        this.appDialog = appDialog;
    }

    private boolean downLoading;
    private String path;

    private int progress;

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public String getApkName()
    {
        return ApkName;
    }

    public void setApkName(String apkName)
    {
        ApkName = apkName;
    }

    public String getAppSize()
    {
        return appSize;
    }

    public void setAppSize(String appSize)
    {
        this.appSize = appSize;
    }

    public String getAppPkg()
    {
        return appPkg;
    }

    public void setAppPkg(String appPkg)
    {
        this.appPkg = appPkg;
    }

    public int getProgress()
    {
        return progress;
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
    }

    public boolean isDownLoading()
    {
        return downLoading;
    }

    public void setDownLoading(boolean downLoading)
    {
        this.downLoading = downLoading;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }
}
