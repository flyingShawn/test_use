package com.grampus.hualauncherkai.UI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.grampus.hualauncherkai.R;

public class testActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Uri uri = Uri.parse("content://telephony/carriers");
        String[] projection = {"_id,apn,type,current"};

        try {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_APN_SETTINGS);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                //requesting permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_APN_SETTINGS}, 1);
            } else {
                //permission is granted and you can change APN settings
            }
            Cursor cr = this.getContentResolver().query(uri, projection, null, null, null);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
