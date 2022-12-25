package com.grampus.hualauncherkai.UI;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grampus.hualauncherkai.R;

public class SettingWIFIPassword extends AppCompatActivity
{


    private TextView wifi_name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_wifi_password);

        String wifiName = getIntent().getStringExtra("wifi_name");

        wifi_name = findViewById(R.id.wifi_name);
        wifi_name.setText(wifiName);

        EditText input_wifi_password = findViewById(R.id.input_wifi_password);
    }
}
