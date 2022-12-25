package com.grampus.hualauncherkai.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.UI.AppWhiteListActivity;
import com.grampus.hualauncherkai.UI.WiFiWhiteListActivity;

public class SettingsFragmentPhoneWhiteList extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
    private Preference wifiManage;
    private Preference appManage;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
            Intent intent = new Intent(SettingsFragmentPhoneWhiteList.this.getActivity(), WiFiWhiteListActivity.class);
            startActivity(intent);
        }

        if (appManage.equals(preference))
        {
            Intent intent = new Intent(SettingsFragmentPhoneWhiteList.this.getActivity(), AppWhiteListActivity.class);
            startActivity(intent);
        }

        return true;
    }
}
