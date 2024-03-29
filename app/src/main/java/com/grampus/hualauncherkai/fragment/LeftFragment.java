package com.grampus.hualauncherkai.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.grampus.hualauncherkai.R;


public class LeftFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
    private Preference whiteList;
    private Preference deviceManage;
    private Preference viewLog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //从xml文件加载选项
        addPreferencesFromResource(R.xml.leftfrag);

        whiteList=findPreference("white_list");
        whiteList.setOnPreferenceClickListener(this);

        deviceManage=findPreference("device_manage");
        deviceManage.setOnPreferenceClickListener(this);

        viewLog=findPreference("view_log");
        viewLog.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();

        if (whiteList.equals(preference))
        {
            RightFragment rightFragment=new RightFragment();
            transaction.replace(R.id.right_fragment,rightFragment);
            transaction.commit();
        }

        if (deviceManage.equals(preference))
        {
            RightFragmentKai rightFragment=new RightFragmentKai();
            transaction.replace(R.id.right_fragment,rightFragment);
            transaction.commit();
        }
        return false;
    }
}
