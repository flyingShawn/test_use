/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.grampus.hualauncherkai.Data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.Tools.Tell;
import com.grampus.hualauncherkai.UI.LicenseActivity;
import com.grampus.hualauncherkai.UI.MainActivity;
import com.grampus.hualauncherkai.common.SharedPreferenceUtil;
import com.grampus.hualauncherkai.common.Utils;

/**
 * The SampleEula for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class SampleEula {
    private static final int REQUEST_ENABLE = 777;
    private MainActivity mActivity = null;
    private DevicePolicyManager mDevicePolicyManager = null;
    private ComponentName mAdminName = null;
    boolean notShowAgain = false;

    public SampleEula(MainActivity context, DevicePolicyManager devicePolicyManager, ComponentName adminName) {
        mActivity = context;
        mDevicePolicyManager = devicePolicyManager;
        mAdminName = adminName;
    }

    @SuppressLint("InflateParams")
    public void show() {
        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(mActivity);
        notShowAgain = sharedPreferenceUtil.hasUserAccepted();
        if (notShowAgain == false) {
            // Show the Eula
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setPositiveButton(mActivity.getString(R.string.accept_btn),
                            new Dialog.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialogInterface, int i) {
                                    SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(mActivity);
                                    sharedPreferenceUtil.saveUserChoice(notShowAgain);
                                    dialogInterface.dismiss();
                                    activeProcess();
                                }
                            })
                    .setNegativeButton(mActivity.getString(R.string.exit_btn),
                            new Dialog.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Close the activity as they have declined
                                    // the EULA
                                    mActivity.finish();
                                }

                            });
            AlertDialog eulaDialog = builder.create();
            eulaDialog.setCancelable(false);
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View layout = inflater.inflate(R.layout.eula_layout, null);
            TextView permissionText = layout.findViewById(R.id.content_permissions);
            String filename = "huawei_permission_statement.html";
           //String content = Utils.getStringFromHtmlFile(mActivity, filename);
            String content = Utils.getStringFromHtmlFile(mActivity, R.raw.huawei_permission_statement);
            permissionText.setText(Html.fromHtml(content));

            TextView statementText = layout.findViewById(R.id.read_statement);
            statementText.setMovementMethod(LinkMovementMethod.getInstance());
            CharSequence text = statementText.getText();
            if (text instanceof Spannable) {
                int end = text.length();
                Spannable sp = (Spannable) text;
                URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.clearSpans();// should clear old spans  
                for (URLSpan url : urls) {
                    MyURLSpan myURLSpan = new MyURLSpan();
                    style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                statementText.setText(style);
            }
            CheckBox checkbox = layout.findViewById(R.id.not_show_check);
            checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    notShowAgain = isChecked;

                }
            });
            eulaDialog.setView(layout);
            eulaDialog.show();
        } else {
            activeProcess();
        }
    }

    private class MyURLSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            //widget.setBackgroundColor(Color.parseColor("#00000000"));

            Intent intent = new Intent(mActivity, LicenseActivity.class);
            mActivity.startActivity(intent);
        }
    }

    protected void activeProcess() {
        if (mDevicePolicyManager != null
                && !mDevicePolicyManager.isAdminActive(mAdminName)) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            mActivity.startActivityForResult(intent, REQUEST_ENABLE);
            //Log.d("SAMPLE", "activeProcess");
            NetDataHub.get().addLog("activeProcess----已有设备管理权限");
            //Tell.toast("已有设备管理权限！", mActivity.getApplicationContext());
        }
        else{
            NetDataHub.get().addLog("activeProcess----已有设备管理权限");
            Tell.toast("已有设备管理权限！", mActivity.getApplicationContext());

            NetDataHub.get().setHuaWeiDesktop(mActivity);//add by gwb;2020.10.13
        }
    }
}
