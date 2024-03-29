package com.grampus.hualauncherkai.UI;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.grampus.hualauncherkai.Data.AppItemRes;
import com.grampus.hualauncherkai.Data.SystemDataGet;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.common.ConfigUtil;
import com.grampus.hualauncherkai.util.DateUtil;
import com.grampus.hualauncherkai.util.FileManagerUtil;
import com.grampus.hualauncherkai.util.HttpClientUtil;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class AppDetailActivity extends BaseActivity{
	private static final String TAG = "AppDetailActivity";
	private TextView tvAppName;
	private TextView tvAppSize;
	private TextView tvAppVersion;
	private TextView tvAppInstall;
	private ImageButton imageButton;
	private AppItemRes appItemRes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_detail);
		
		getIntentData();
		initTopViews();
		setTopViews();
		findViews();
    }
	
	private void findViews() {
		// TODO Auto-generated method stub
		tvAppName = findViewById(R.id.id_tv_appName);
		tvAppSize = findViewById(R.id.id_tv_file_size);
		tvAppVersion = findViewById(R.id.id_tv_version);
		tvAppInstall = findViewById(R.id.id_tv_install);
		imageButton = findViewById(R.id.id_btn_open);
		imageButton.setOnClickListener(clickListener);
	}

	private void setTopViews() {
		// TODO Auto-generated method stub
		setTopTitle("应用详情");
		setTopLeftBtnImage(R.mipmap.icon_back);
		setTopLeftBtnText(R.string.btn_back);
		showView(top_left_btn);
		showView(top_left_btn_image);
		showView(top_left_btn_text);

		top_left_btn.setOnClickListener(clickListener);
	}

	private void getIntentData() {
		// TODO Auto-generated method stub
		String content = getIntent().getStringExtra("content");
		if (content != null){
			Gson gson = new Gson();
			appItemRes = gson.fromJson(content, AppItemRes.class);
		}
		
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setAppData();
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
			case R.id.top_left_btn:
				finish();
				break;
			case R.id.id_btn_open:
				installApp();
				break;
			default:
				break;
		}
		}
	};

	private void installApp() {
		if (appItemRes != null){
			if (isAppInstalled(this, appItemRes.getAppPkg())){
				Intent intent = this.getPackageManager().getLaunchIntentForPackage(appItemRes.getAppPkg());
				startActivity(intent);
			}else{
				downLoadApp(appItemRes.getApkName());
			}
		}

	}

	private void setAppData() {
		// TODO Auto-generated method stub
		if (appItemRes != null){
			tvAppName.setText(appItemRes.getAppName());
			tvAppSize.setText(appItemRes.getAppSize());
			if (isAppInstalled(this, appItemRes.getAppPkg())){
				tvAppInstall.setText("打开");
			}else{
				tvAppInstall.setText("安装");
			}
		}

	}

	private void downLoadApp(String apkName) {
		String serviceAd = Save.getValue(this, "service_ad", "");
		if (serviceAd != ""){
			String url = "http://"+serviceAd+"/teldown/encrypttemp/Software/" +apkName;
			try {
				String urlStr = null;
				urlStr = URLEncoder.encode(url,"utf-8").replaceAll("\\+", "%20");
				//编码之后的路径中的“/”也变成编码的东西了 所有还有将其替换回来 这样才是完整的路径
				urlStr = urlStr.replaceAll("%3A", ":").replaceAll("%2F", "/");
				downLoadFile(urlStr);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private void downLoadFile(String urlStr) {
		if (FileManagerUtil.hasSdcard()) {
			String dir = ConfigUtil.SD_PATH;
			File fileDir = new File(dir);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File file = new File(dir + DateUtil.getCurrentTimeMills()+".apk");
			try {
				file.createNewFile();
				showProgressDialog("正在下载应用...");
				HttpClientUtil.getUrl(this, urlStr, new FileAsyncHttpResponseHandler(file) {
					@Override
					public void onProgress(long bytesWritten, long totalSize) {
						super.onProgress(bytesWritten, totalSize);
						ProgressDialog mpDialog = getMpDialog();
						if (mpDialog != null && mpDialog.isShowing()) {
							mpDialog.setMessage("已经下载"+(int)(((bytesWritten/(float)totalSize))*100)+"%");
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
						hideProgressDialog();
						showTipsDialog(R.string.dialog_network_timeout_download);
						file.delete();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, File file) {
						hideProgressDialog();
						inStallFile(file);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			showTipsDialog(R.string.dialog_sdcard_not_exist);
		}
	}

	private void inStallFile(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/*
	* check the app is installed
	*/
	private boolean isAppInstalled(Context context, String packagename)
	{
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
		}catch (PackageManager.NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
        return packageInfo != null;
	}
}
