package com.grampus.hualauncherkai.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.grampus.hualauncherkai.R;

public class RightFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
    private Preference wifiManage;
    private Preference appManage;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //从xml文件加载选项
        addPreferencesFromResource(R.xml.rightfrag);

        wifiManage=findPreference("wifi_manage");
        wifiManage.setOnPreferenceClickListener(this);

        appManage=findPreference("app_manage");
        appManage.setOnPreferenceClickListener(this);
    }


    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();

        if (wifiManage.equals(preference))
        {
            WiFiWhiteList wiFiWhiteList=new WiFiWhiteList();
            transaction.replace(R.id.right_fragment,wiFiWhiteList);
            transaction.commit();
        }

        if (appManage.equals(preference))
        {

        }
        return true;
    }
}
