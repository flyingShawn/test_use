package com.grampus.hualauncherkai.fragment;


import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlWifi;
import static com.grampus.hualauncherkai.Data.NetDataHub.wifiList;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.grampus.hualauncherkai.R;

public class WiFiWhiteList extends PreferenceFragment
{
    private SwitchPreference isEnabled;
    private PreferenceCategory container;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wifi_white_list);

        container=(PreferenceCategory)findPreference("container2");

        isEnabled = (SwitchPreference) findPreference("isEnabled");
        isEnabled.setChecked(isCtrlWifi);
        if (isEnabled.isChecked())
        {
            isEnabled.setTitle("已启用");
        }
        else
        {
            isEnabled.setTitle("未启用");
        }
        if(wifiList == null)//add by gwb;2020.9.17  当没有获取到策略时会报错。
            return ;

        int wifiWhiteListCount = wifiList.length();
        String[] wifiWhiteListSSID = new String[wifiWhiteListCount];
        String[] wifiWhiteListMac = new String[wifiWhiteListCount];
        Preference[] wifiWhiteList = new Preference[wifiWhiteListCount];

        try
        {
            for (int i = 0; i < wifiWhiteListCount; i++)
            {
                wifiWhiteListSSID[i] = wifiList.getJSONObject(i).getString("Name1");
                wifiWhiteListMac[i] = wifiList.getJSONObject(i).getString("Name2");

                wifiWhiteList[i]=new Preference(getActivity());

                wifiWhiteList[i].setKey("whiteWhiteList" + i);
                if (!wifiWhiteListSSID[i].equals(""))
                {
                    wifiWhiteList[i].setTitle(wifiWhiteListSSID[i]);
                }
                if (!wifiWhiteListMac[i].equals(""))
                {
                    wifiWhiteList[i].setSummary("mac:" + wifiWhiteListMac[i]);
                }
                wifiWhiteList[i].setIcon(R.drawable.list);

                container.addPreference(wifiWhiteList[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }





}
