package com.grampus.hualauncherkai.util;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpClientUtil {
	private static final String TAG = "HttpClientUtil";
	private static AsyncHttpClient asyncClient;
	static {
		asyncClient = new AsyncHttpClient();
		
		asyncClient.setTimeout(20 * 1000);
		
		asyncClient.setUserAgent("android-async-http/1.4.6 (http://loopj.com/android-async-http)");
	}

	public static void setHttpHeader(String domain, String sc,boolean flag) {
		asyncClient.addHeader("X-SUP-DOMAIN", domain);
		asyncClient.addHeader("X-SUP-SC", sc);
		asyncClient.setUserAgent("android-async-http/1.4.6 (http://loopj.com/android-async-http)");
		if(flag){
			//asyncClient.addHeader("Cookie", "");
		}
	}
	
	public static void getUrl(Context context, String url, AsyncHttpResponseHandler responseHandler) {
		asyncClient.get(context, url, responseHandler);
		Log.i(TAG, url);
	}
	
	public static void gettest(Context context, String url, AsyncHttpResponseHandler responseHandler) {
		//asyncClient.get(context, url, responseHandler);
		asyncClient.post(context, url, null, responseHandler);
		Log.i(TAG,url);
	}
	
	public static void get(String url, AsyncHttpResponseHandler responseHandler) {
		asyncClient.get(url, responseHandler);
		Log.i(TAG, "ulr" + url);
	}

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		asyncClient.get(url, params, responseHandler);
		Log.i(TAG, "url" + url);
	}

	public static void get(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		asyncClient.get(context, url, params,
				responseHandler);
	}

	public static void post(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		asyncClient.post(context, url, params, responseHandler);
	}

	public static void TimerAction(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		asyncClient.post(context, url, params, responseHandler);
	}
	

	public static void cancelRequest(Context context) {
		asyncClient.cancelRequests(context, true);
	}

}
