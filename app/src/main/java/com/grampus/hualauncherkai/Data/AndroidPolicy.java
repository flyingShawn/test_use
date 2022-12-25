package com.grampus.hualauncherkai.Data;

import android.util.Log;

import com.grampus.hualauncherkai.Tools.HttpRequest;

import static com.grampus.hualauncherkai.util.DeviceInfoUtil.getPhoneIp;
import static com.grampus.hualauncherkai.util.DeviceInfoUtil.getWifiMacAddress;

/**
 * @author fsy
 * @date 2021/12/27 14:09
 */
public class AndroidPolicy {

    public static boolean NACCheckFalg0 = true;
    public static String NACUrl0 = "";
    public static String NACAddr0 = "";


//    public static String centerServerIp ;

    public static String getNACCheck()
    {
        try
        {
            if (NACAddr0 == null || NACAddr0.equals("") || NACAddr0.equals("0") || NACAddr0.length() < 4
                    || NACAddr0.equals("0.0.0.0") || NACAddr0.equals("255.255.255.255"))
            {
                Log.w("EMMA11y","NACAddr0空："+ NACAddr0);
                return "";
            }

            NACUrl0 = "http://" + NACAddr0 + "/TimerAction.php?ip=" + getPhoneIp() + "&mac=" + getWifiMacAddress();

            Log.w("EMMA11y","NACUrl0："+ NACUrl0);
            String rs = HttpRequest.httpGet(NACUrl0, null);

            Log.w("EMMA11y","neturl rs："+ rs);
            NACCheckFalg0 = false;
            return rs;
        }
        catch (Exception e)
        {
            Log.w("EMMA11y","neturl error："+ e.toString());
            return "";
        }
    }


}
