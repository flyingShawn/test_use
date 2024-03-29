package com.grampus.hualauncherkai.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.fragment.LeftFragment;

public class AdminSettings extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Log.v("Settings","Settings准备启动！");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //init();
    }



    public void init()
    {
        //加载PrefFragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        LeftFragment prefFragment = new LeftFragment();
        transaction.add(R.id.settings_main, prefFragment);
        transaction.commit();
    }
}
