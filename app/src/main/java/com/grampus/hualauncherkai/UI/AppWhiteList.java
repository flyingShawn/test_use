package com.grampus.hualauncherkai.UI;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.grampus.hualauncherkai.R;

import static com.grampus.hualauncherkai.Data.NetDataHub.appList;
import static com.grampus.hualauncherkai.Data.NetDataHub.isCtrlApp;
import static com.grampus.hualauncherkai.Data.NetDataHub.whiteApp;
import static com.grampus.hualauncherkai.Data.NetDataHub.wifiList;

public class AppWhiteList extends PreferenceFragment
{
    private SwitchPreference isEnabled;
    private PreferenceCategory container;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_white_list);

        container=(PreferenceCategory)findPreference("container2");

        isEnabled=(SwitchPreference)findPreference("isEnabled");
        isEnabled.setChecked(isCtrlApp);
        if (isEnabled.isChecked())
        {
            isEnabled.setTitle("已启用");
        }
        else
        {
            isEnabled.setTitle("未启用");
        }


        if(whiteApp == null)//add by gwb;2020.9.22  当没有获取到策略时会报错。
            return ;

        int appWhiteListCount = whiteApp.size();

        if(appWhiteListCount < 1)//add by gwb;2020.9.17  当没有获取到策略时会报错。
            return ;

        String[] appWhiteListName = new String[appWhiteListCount];
        Preference[] appWhiteList = new Preference[appWhiteListCount];

        try
        {
            for (int i = 0; i < appWhiteListCount; i++)
            {
                appWhiteListName[i] = whiteApp.get(i);
                appWhiteList[i]=new Preference(getActivity());

                appWhiteList[i].setKey("whiteWhiteList" + i);
                if (!appWhiteListName[i].equals(""))
                {
                    appWhiteList[i].setTitle(appWhiteListName[i]);
                }
                appWhiteList[i].setIcon(R.drawable.list);

                container.addPreference(appWhiteList[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
