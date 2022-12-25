/*
package com.grampus.hualauncherkai.util;


import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.grampus.hualauncherkai.Receiver.WifiHub;

*/
/**
 *
 *//*

public class WifiConnectUtil {

    private boolean isConnect = false;
    private WifiHub receiver;

    public void connect(Context context, String ssid, String bssid, String password, int timeOut, IConnectListener listener) {

        String mssid = ssid.replace("\"", "");

        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.allowedAuthAlgorithms.clear();
        wifiCong.allowedGroupCiphers.clear();
        wifiCong.allowedKeyManagement.clear();
        wifiCong.allowedPairwiseCiphers.clear();
        wifiCong.allowedProtocols.clear();
        wifiCong.SSID = "\"" + mssid + "\"";
        wifiCong.BSSID = bssid;
        wifiCong.preSharedKey = "\"" + password + "\"";//WPA-PSK密码
        wifiCong.hiddenSSID = false;
        wifiCong.status = WifiConfiguration.Status.ENABLED;
        int id = MyNetworkUtil.getInstance().getWifiManager().addNetwork(wifiCong);
        MyNetworkUtil.getInstance().getWifiManager().enableNetwork(id, true);

        //注册广播快速监听连接状态
        receiver = new WifiChangeReceiver(networkInfo -> {
            if (networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiInfo wifiInfo = MyNetworkUtil.getInstance().getWifiInfo();
                if (wifiInfo.getSSID().replace("\"", "").equals(mssid)) {
                    isConnect = true;
                    if (receiver != null) {
                        context.unregisterReceiver(receiver);
                        receiver = null;
                        listener.onConnected();
                    }
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(receiver, intentFilter);
        //延时监听变化
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnect) {
                    return;
                }
                if (receiver != null) {
                    context.unregisterReceiver(receiver);
                }
                MyNetworkUtil.getInstance().getWifiManager().removeNetwork(id);
                MyNetworkUtil.getInstance().getWifiManager().reconnect();
                listener.onConnectFail();
            }
        }, timeOut);
    }


    public interface IConnectListener {
        void onConnected();
        void onConnectFail();
    }
}*/
