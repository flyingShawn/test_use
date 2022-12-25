/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.grampus.hualauncherkai.UI;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.common.Utils;

/**
 * The LicenseActivity for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.license_layout);
        Button acceptBtn = findViewById(R.id.cancelBtn);
        acceptBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView licenseText = findViewById(R.id.license_content);
        String filename = "huawei_software_license.html";
        //String content = Utils.getStringFromHtmlFile(this, filename);
        String content = Utils.getStringFromHtmlFile(this, R.raw.huawei_software_license);
        licenseText.setText(Html.fromHtml(content));
    }


}
