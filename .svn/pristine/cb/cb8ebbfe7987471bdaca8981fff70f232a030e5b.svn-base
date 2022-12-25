package com.grampus.hualauncherkai.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetworkState {

	public static boolean get3GState(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// mobile 3G Data Network
		try {
			State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getState();
			if ((mobile == State.CONNECTED) || (mobile == State.CONNECTING)) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;
	}

	public static boolean getWifiState(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
        return (wifi == State.CONNECTED) || (wifi == State.CONNECTING);
    }

	public static boolean getNetworkState(Context context) {

        return (get3GState(context) == true) || (getWifiState(context) == true);
    }

}
