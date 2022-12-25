package com.grampus.hualauncherkai.util;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

public class NetWorkGPSUtil {
	private static final String TAG = "NetWorkGPSUtil";
	
	//移动网络是否可用
	public static boolean isMobileNetworkEnable(Context context){
		ConnectivityManager cm =   
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mMobileNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
		if (mMobileNetworkInfo != null) {  
//			mMobileNetworkInfo.isAvailable();判断是否使用移动网络，但是不是一定开启数据流�?
			//移动网络是否连接,就是是否�?启数�?
			return mMobileNetworkInfo.isConnected();  
		}
		else{
			return false;
		}
	}
	
	// Wifi是否可用  
	public static boolean isWifiEnable(Context context) {  
        WifiManager wifiManager = (WifiManager) context  
                .getSystemService(Context.WIFI_SERVICE); 
        return wifiManager.isWifiEnabled();  
    }
	
	//是否连接WIFI
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

	// 是否有可用网�?  
    public static boolean isNetworkConnected(Context context) {  
        ConnectivityManager cm =   
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo network = cm.getActiveNetworkInfo();  
        if (network != null) {  
            return network.isAvailable();  
        }  
        return false;  
    }  
    
 // Gps是否可用  
    public static boolean isGpsEnable(Context context) {  
        LocationManager locationManager =   
                ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));  
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
    }  
    
    public static void toggleGPS(Context context) {
//        Intent gpsIntent = new Intent();  
//        gpsIntent.setClassName("com.android.settings",  
//                "com.android.settings.widget.SettingsAppWidgetProvider");  
//        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");  
//        gpsIntent.setData(Uri.parse("custom:3"));  
//        try {  
//            PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();  
//        }  
//        catch (CanceledException e) {  
//            e.printStackTrace();  
//        }  
    	Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try 
        {
        	context.startActivity(intent);
                    
            
        } catch(ActivityNotFoundException ex) 
        {
            
            // The Android SDK doc says that the location settings activity
            // may not be found. In that case show the general settings.
            
            // General settings activity
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
            	context.startActivity(intent);
            } catch (Exception e) {
            }
        }
    }  
}
