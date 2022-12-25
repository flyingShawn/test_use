package com.grampus.hualauncherkai.util;

import android.content.Context;
import android.util.Log;

import com.mph.okdroid.OkDroid;
import com.mph.okdroid.response.IResponseDownloadHandler;
import com.mph.okdroid.response.RawResHandler;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkDroidUtil {
	private static final String TAG = "OkDroidUtil";
	private static OkDroid okDroid;
	static {
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(10000L, TimeUnit.MILLISECONDS)
				.readTimeout(10000L, TimeUnit.MILLISECONDS)
				.build();
		okDroid = new OkDroid(okHttpClient);
		okDroid.setDebug(false);//开启log日志
	}



	public static void getUrl(Context context, String url, RawResHandler rawResHandler) {
		okDroid.get().url(url).tag(context).enqueue(rawResHandler);
		Log.i(TAG, url);
	}

	public static void getUrl(Context context, String url, String filePath, IResponseDownloadHandler rawResHandler) {
		okDroid.download().url(url).tag(context).filePath(filePath).enqueue(rawResHandler);
		Log.i(TAG, url);
	}
	

	public static void cancelRequest(Context context) {
		okDroid.cancel(context);
	}

}
