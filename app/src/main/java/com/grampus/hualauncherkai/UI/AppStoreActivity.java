package com.grampus.hualauncherkai.UI;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.grampus.hualauncherkai.Data.AppItemRes;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.DownloadUtils.DownloadFile;
import com.grampus.hualauncherkai.Tools.HttpRequest;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.log.LogTrace;
import com.grampus.hualauncherkai.util.DateUtil;
import com.grampus.hualauncherkai.util.DeviceInfoUtil;
import com.grampus.hualauncherkai.util.FileManagerUtil;
import com.grampus.hualauncherkai.util.FileUtil;
import com.grampus.hualauncherkai.util.HttpClientUtil;
import com.grampus.hualauncherkai.util.HttpDownloaderThread;
import com.grampus.hualauncherkai.util.OkDroidUtil;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mph.okdroid.response.RawResHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.grampus.hualauncherkai.Tools.DeviceUtils.setNavigationBarColor;
import static com.grampus.hualauncherkai.Tools.DeviceUtils.setStatusBarColor;

public class AppStoreActivity extends BaseActivity
{
    private static final String TAG = "AppStoreActivity";
    private ListView listView;
    private AppStoreAdpter adapter;

    public static boolean isDownLoad = false;

    public static String rootPath = Environment.DIRECTORY_DOWNLOADS;
    public static String dir = "";//""/storage/emulated/0/Android/data/com.grampus.hualauncherkai/files/Download";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_store);
        Log.w("EMMStore", "AppStoreActivity------onCreate---");
        dir = getExternalFilesDir("Download").getPath();//+"/Download/";//add by gwb;

        setStatusBarColor(this, R.color.brightBlue);
        setNavigationBarColor(this,R.color.brightBlue);

        getIntentData();
        initTopViews();
        setTopViews();
        findViews();
        //getAppDatas();
        getAppDataOk();
        deleteFile();
    }

    private void deleteFile()
    {
        //String dir = ConfigUtil.SD_PATH; del by gwb;2020.10.20
        File fileDir = new File(dir);
        if (fileDir.exists())
        {
            FileManagerUtil.deleteFolderFile(dir, false);
        }
    }

    private void findViews()
    {
        // TODO Auto-generated method stub
        listView = findViewById(R.id.id_gv_app);
        adapter = new AppStoreAdpter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
    }

    private void setTopViews()
    {
        // TODO Auto-generated method stub
        setTopTitle("应用商店");
        setTopLeftBtnImage(R.mipmap.icon_back);
        setTopLeftBtnText(R.string.btn_back);
        showView(top_left_btn);
        showView(top_left_btn_image);
        showView(top_left_btn_text);

        top_left_btn.setOnClickListener(clickListener);
    }

    private void getIntentData()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        backPressed();
    }
    //isDownLoad变量暂时不设置了，不可取消下载，以前应该是只能同时下载一个取消吧？ fsy 2021.12.24
    private void backPressed()
    {
        if (isDownLoad)
        {
            showConfirmDialog("当前正在下载应用，确定退出下载吗？", true, new OnPositiveClickListener()
            {
                @Override
                public void positiveClick()
                {
                    finish();
                }
            }, new OnNegitiveClickListener()
            {
                @Override
                public void NegitiveClick()
                {

                }
            });
        }
        else
        {
            finish();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        HttpClientUtil.cancelRequest(this);
        OkDroidUtil.cancelRequest(this);
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        //getAppData();
    }

    private OnClickListener clickListener = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id)
            {
                case R.id.top_left_btn:
                    backPressed();
                    break;
                default:
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
			/*AppItemRes appItemRes = adapter.getItem(position);
			Gson gson = new Gson();
			String content = gson.toJson(appItemRes);
			Intent intent = new Intent(AppStoreActivity.this, AppDetailActivity.class);
			intent.putExtra("content", content);
			startActivity(intent);*/
            AppItemRes app = adapter.getItem(position);
            if (app.getPath() != null)
            {
                File file = new File(app.getPath());
                if (file != null && file.exists())
                {
                    FileUtil.inStallFile(file);
                }
                else
                {
                    if (!app.isDownLoading())
                    {
                        downLoadApk(app);
                        LogTrace.i(TAG, "downLoadApk", "--------------------");
                    }
                    else
                    {
                        LogTrace.i(TAG, "downLoadingApk", "--------------------");
                        Toast.makeText(AppStoreActivity.this, "应用正在下载中...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                if (!app.isDownLoading())
                {
                    downLoadApk(app);
                    NetDataHub.get().addLog("EMM-------应用下载2----appName："+app.getAppName()+" apkName："+app.getApkName()+" appSize："+app.getAppSize());
                    LogTrace.i(TAG, "downLoadApk", "===================");
                }
                else
                {
                    LogTrace.i(TAG, "downLoadingApk", "===================");
                    Toast.makeText(AppStoreActivity.this, "应用正在下载中...", Toast.LENGTH_SHORT).show();
                    NetDataHub.get().addLog("EMM-------应用正在下载中2");
                    app.getAppDialog().show();
                }
            }
        }
    };

    private void getAppData()
    {
        // TODO Auto-generated method stub
        ArrayList<AppItemRes> contentList = new ArrayList<AppItemRes>();
		/*AppItemRes logInfoVo = new AppItemRes();
		logInfoVo.setAppName("饿了么");
		contentList.add(logInfoVo);
		contentList.add(logInfoVo);
		contentList.add(logInfoVo);
		contentList.add(logInfoVo);
		contentList.add(logInfoVo);
		contentList.add(logInfoVo);
		contentList.add(logInfoVo);
		contentList.add(logInfoVo);
		contentList.add(logInfoVo);*/
        LogTrace.i(TAG, "getAppData==========", "" + contentList.size());
        adapter.setContentList(contentList);
        adapter.notifyDataSetChanged();
        //Save.putValue(this, "service_ad", "180.102.152.91:16670");
        String serviceAd = Save.getValue(this, "service_ad", "");
        if (!serviceAd.equals(""))
        {
            String url = "http://" + serviceAd + "/TelSafeDesk.php?Action=softwarelist" + "&Mac=" +
                    DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum ;
            //"&Mac=865217037210303";
            showProgressDialog("加载中...");
            HttpClientUtil.getUrl(this, url, new TextHttpResponseHandler()
            {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
                {
                    LogTrace.i(TAG, "getAppData----onFailure", "responseString=" + responseString);
                    hideProgressDialog();
                    showTipsDialog(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString)
                {
                    LogTrace.i(TAG, "getAppData----onSuccess", "responseString =" + responseString);
                    hideProgressDialog();
                    ArrayList<AppItemRes> contentList = new ArrayList<AppItemRes>();
                    try
                    {
                        JSONArray jsonArray = new JSONArray(responseString);
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            AppItemRes appItemRes = new AppItemRes();
                            appItemRes.setAppName(object.getString("Name1"));
                            appItemRes.setApkName(object.getString("Name2"));
                            appItemRes.setAppSize(object.getString("Name3"));
                            contentList.add(appItemRes);
                        }
                    }
                    catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        showTipsDialog(responseString != null ? responseString : "JSON 解析失败");
                    }
                    adapter.setContentList(contentList);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void getAppDataOk()
    {
        // TODO Auto-generated method stub
        String serviceAd = Save.getValue(this, "service_ad", "");
        if (!serviceAd.equals(""))
        {
            String url = "http://" + serviceAd + "/TelSafeDesk.php?Action=softwarelist" + "&Mac=" +
                    DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum ;
            //"&Mac=865217037210303";
            showProgressDialog("加载中...");
            OkDroidUtil.getUrl(this, url, new RawResHandler()
            {
                @Override
                public void onSuccess(int statusCode, String response)
                {
                    hideProgressDialog();
                    ArrayList<AppItemRes> contentList = new ArrayList<AppItemRes>();
                    try
                    {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            AppItemRes appItemRes = new AppItemRes();
                            appItemRes.setAppName(object.getString("Name1"));
                            appItemRes.setApkName(object.getString("Name2"));
                            appItemRes.setAppSize(object.getString("Name3"));
                            contentList.add(appItemRes);
                        }
                    }
                    catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        showTipsDialog(response != null ? response : "JSON 解析失败");
                    }
                    adapter.setContentList(contentList);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailed(int statusCode, String errMsg)
                {
                    hideProgressDialog();
                    showTipsDialog(errMsg);
                }
            });
        }
    }

    private void getAppDatas()
    {
        String serviceAd = Save.getValue(this, "service_ad", "");
        if (!serviceAd.equals(""))
        {
            final String url = "http://" + serviceAd + "/TelSafeDesk.php?Action=softwarelist" +
                    "&Mac=" + DeviceInfoUtil.getWifiMacAddress() + "&DiskNum=" + EMMApp.getInstance().diskNum ;
            //"&Mac=865217037210303";
            showProgressDialog("加载中...");
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String re = HttpRequest.httpGet(url, null);
                        Message message = new Message();
                        message.obj = re;
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                    catch (Exception e)
                    {
                        Message message = new Message();
                        message.what = 2;
                        message.obj = e.toString();
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            hideProgressDialog();
            if (msg.what == 1)
            {
                praseData((String) msg.obj);
            }
            else if (msg.what == 2)
            {
                showTipsDialog((String) msg.obj);
            }
        }
    };

    private void praseData(String responseString)
    {
        ArrayList<AppItemRes> contentList = new ArrayList<AppItemRes>();
        try
        {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);
                AppItemRes appItemRes = new AppItemRes();
                appItemRes.setAppName(object.getString("Name1"));
                appItemRes.setApkName(object.getString("Name2"));
                appItemRes.setAppSize(object.getString("Name3"));
                contentList.add(appItemRes);
            }
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            showTipsDialog(responseString != null ? responseString : "JSON 解析失败");
        }
        adapter.setContentList(contentList);
        adapter.notifyDataSetChanged();
    }

    private void downLoadApk(AppItemRes aItems)
    {
        String apkName = aItems.getApkName();
        String serviceAd = Save.getValue(this, "service_ad", "");
        if (serviceAd != "")
        {
            String url = "http://" + serviceAd + "/teldown/encrypttemp/Software/" + apkName;
            NetDataHub.get().addLog("EMM-------downLoadApk----url:"+url);

            try
            {
                String urlStr = null;
                urlStr = URLEncoder.encode(url, "utf-8").replaceAll("\\+", "%20");
                //编码之后的路径中的“/”也变成编码的东西了 所有还有将其替换回来 这样才是完整的路径
                urlStr = urlStr.replaceAll("%3A", ":").replaceAll("%2F", "/");
                HttpDownLoadFile(urlStr, aItems);  // add by fsy 2021.12.24 新的下载方式
          //      downLoadFile(urlStr, aItems);
                NetDataHub.get().addLog("EMM-----downLoadApk------urlStr:"+urlStr);

            }
            catch (UnsupportedEncodingException e)
            {
                NetDataHub.get().addLog("EMM----downLoadApk----catch error:"+e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * add by fsy 2021.12.24 新的下载方式
     * 创建HttpDownloaderThread线程，一个下载链接对应一个线程及一个进度框，再次点击会重复显示进度条进度
     * @param urlStr
     * @param aItems
     */
    private void HttpDownLoadFile(String urlStr, final AppItemRes aItems)
    {
        if(FileManagerUtil.hasSdcard())
        {
        //  NetDataHub.get().addLog("EMM--------hasSdcard()----");
            try
            {
                NetDataHub.get().addLog("EMM---appName："+aItems.getAppName()+" apkName："+aItems.getApkName()+" appSize："+aItems.getAppSize());

                NetDataHub.get().addLog("EMM------urlStr："+urlStr);
                ProgressDialog pd = new ProgressDialog(this);
                pd.setTitle(aItems.getAppName());
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                //设置提示信息
                pd.setMessage("正在下载中......");
                //设置对话进度条显示在屏幕顶部
                pd.getWindow().setGravity(Gravity.TOP);
                pd.setMax(100);
                pd.show();//调用show方法显示进度条对话框
                //pd.setCancelable(false);  //强制窗口显示不可取消

                //     isDownLoad=true;
                aItems.setDownLoading(true);
                aItems.setAppDialog(pd);

                HttpDownloaderThread httpDownloader = new HttpDownloaderThread(urlStr,dir,aItems);
                httpDownloader.start(); //如果正在下载，再次点击会弹出进度条
            }
            catch (Exception e)
            {
                NetDataHub.get().addLog("EMM----httpDownloader downLoadApk----catch error:"+e.toString());
                e.printStackTrace();
            }
        }
        else
        {
            NetDataHub.get().addLog("EMM--------hasSdcard()=false----");
            showTipsDialog(R.string.dialog_sdcard_not_exist);
        }
    }



    /**
    * @date    2021/12/24 15:28
    * @description   fsy 暂时不用了，老的下载方式
    */
    private void downLoadFile(String urlStr, final AppItemRes aItems)
    {
        if(FileManagerUtil.hasSdcard())
        {
            isDownLoad = true;
            //String dir = ConfigUtil.SD_PATH;
            File fileDir = new File(dir);
            if (!fileDir.exists())
            {
                NetDataHub.get().addLog("EMM--------fileDir.mkdirs()----");
                fileDir.mkdirs();
            }
            final File file = new File(dir + DateUtil.getCurrentTimeMills() + ".apk");
            try
            {
                /**
                 * 2020.04.10 未来修改
                 * 这样直接创建文件可能会出错，所以进行修改
                 * 先创建文件的目录，再创建文件
                 */

                DownloadFile df = new DownloadFile();
                df.Download(urlStr, rootPath, this, aItems, adapter);
                aItems.setDownLoading(true);
                adapter.notifyDataSetChanged();

            }
            catch (Exception e)
            {
                NetDataHub.get().addLog("EMM----downLoadFile---catch error:"+e.toString());
                e.printStackTrace();
            }


        }
        else
        {
            NetDataHub.get().addLog("EMM--------hasSdcard()=false----");
            showTipsDialog(R.string.dialog_sdcard_not_exist);
        }
    }

}
