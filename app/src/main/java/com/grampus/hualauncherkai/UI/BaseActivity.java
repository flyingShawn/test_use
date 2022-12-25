package com.grampus.hualauncherkai.UI;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.grampus.hualauncherkai.R;
import com.grampus.hualauncherkai.log.LogTrace;
import com.grampus.hualauncherkai.util.DateUtil;
import com.grampus.hualauncherkai.util.HttpClientUtil;
import com.grampus.hualauncherkai.util.NetworkState;
import com.grampus.hualauncherkai.util.StringUtil;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.io.File;
import java.util.Calendar;

import static com.grampus.hualauncherkai.UI.AppStoreActivity.dir;

public class BaseActivity extends Activity
{
    private static final String BASE_TAG = "BaseActivity";

    protected LinearLayout top_left_btn;
    protected LinearLayout top_right_btn;
    protected TextView top_title;
    protected ImageView top_left_btn_image;
    protected TextView top_left_btn_text;
    protected ImageView top_right_btn_image;
    protected TextView top_right_btn_text;
    public String leadsDateStart = "";
    public String leadsDateEnd = "";
    private PopupWindow popWindowFilter;

    private TitleBar titleBar;

    public interface OnPositiveClickListener
    {
        void positiveClick();
    }

    public interface OnNegitiveClickListener
    {
        void NegitiveClick();
    }

    protected void initTopViews()
    {
        top_left_btn = findViewById(R.id.top_left_btn);
        top_right_btn = findViewById(R.id.top_right_btn);
        top_title = findViewById(R.id.top_title);
        top_left_btn_image = findViewById(R.id.top_left_btn_image);
        top_left_btn_text = findViewById(R.id.top_left_btn_text);
        top_right_btn_image = findViewById(R.id.top_right_btn_image);
        top_right_btn_text = findViewById(R.id.top_right_btn_text);

        titleBar = findViewById(R.id.appstore_titlebar);
        titleBar.setOnTitleBarListener(new OnTitleBarListener()
        {
            @Override
            public void onLeftClick(View v)
            {
                onBackPressed();
            }

            @Override
            public void onTitleClick(View v)
            {

            }

            @Override
            public void onRightClick(View v)
            {

            }
        });
    }

    protected void setTopTitle(String str)
    {
        top_title.setText(str);
    }


    protected void setTopTitle(int resId)
    {
        top_title.setText(resId);
    }

    protected void hideViewGone(View v)
    {
        v.setVisibility(View.GONE);
    }

    protected void hideViewInvisible(View v)
    {
        v.setVisibility(View.INVISIBLE);
    }

    protected void showView(View v)
    {
        v.setVisibility(View.VISIBLE);
    }

    /**
     * 设置左侧图片
     *
     * @param resId
     */
    protected void setTopLeftBtnImage(int resId)
    {
        top_left_btn_image.setImageResource(resId);
    }

    /**
     * 设置左侧文字
     *
     * @param resId
     */
    protected void setTopLeftBtnText(int resId)
    {
        top_left_btn_text.setText(resId);
    }

    /**
     * 设置右侧图片
     *
     * @param resId
     */
    protected void setTopRightBtnImage(int resId)
    {
        top_right_btn_image.setImageResource(resId);
    }

    /**
     * 设置右侧文字
     *
     * @param resId
     */
    protected void setTopRightBtnText(int resId)
    {
        top_right_btn_text.setText(resId);
    }

    /**
     * 隐藏左侧按钮
     *
     * @param hide
     */
    protected void hideLeftBtn(boolean hide)
    {
        if (hide)
        {
            top_left_btn.setVisibility(View.INVISIBLE);
        }
        else
        {
            top_left_btn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏左侧按钮图片
     *
     * @param hide
     */
    protected void hideLeftBtnImage(boolean hide)
    {
        if (hide)
        {
            top_left_btn_image.setVisibility(View.INVISIBLE);
        }
        else
        {
            top_left_btn_image.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏左侧按钮文字
     *
     * @param hide
     */
    protected void hideLeftBtnText(boolean hide)
    {
        if (hide)
        {
            top_left_btn_text.setVisibility(View.INVISIBLE);
        }
        else
        {
            top_left_btn_text.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏右侧按钮
     *
     * @param hide
     */
    protected void hideRightBtn(boolean hide)
    {
        if (hide)
        {
            top_right_btn.setVisibility(View.INVISIBLE);
        }
        else
        {
            top_right_btn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏右侧按钮图片
     *
     * @param hide
     */
    protected void hideRightBtnImage(boolean hide)
    {
        if (hide)
        {
            top_right_btn_image.setVisibility(View.INVISIBLE);
        }
        else
        {
            top_right_btn_image.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏右侧按钮文字
     *
     * @param hide
     */
    protected void hideRightBtnText(boolean hide)
    {
        if (hide)
        {
            top_right_btn_text.setVisibility(View.INVISIBLE);
        }
        else
        {
            top_right_btn_text.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
    }

    public void showToast(String msg)
    {
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resId)
    {
        Toast.makeText(BaseActivity.this, resId, Toast.LENGTH_SHORT).show();
    }

    private ProgressDialog mpDialog;

    public void showProgressDialog(String msg)
    {
//		if (null == mpDialog) {
        mpDialog = new ProgressDialog(BaseActivity.this);
        mpDialog.setMessage(msg);
        mpDialog.setCancelable(false);
        mpDialog.setOnKeyListener(onBackKeyListener);
        mpDialog.show();
//		} 
//		else {
//			mpDialog.setMessage(msg);
//			mpDialog.show();
//		}
    }

    public ProgressDialog getMpDialog()
    {
        return mpDialog;
    }

    /**
     * add a keylistener for progress dialog
     */
    private OnKeyListener onBackKeyListener = new OnKeyListener()
    {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
        {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
            {
                HttpClientUtil.cancelRequest(BaseActivity.this);
                hideProgressDialog();
            }
            return false;
        }
    };

    public void hideProgressDialog()
    {
        if (mpDialog != null && mpDialog.isShowing())
        {
            mpDialog.dismiss();
            mpDialog = null;
        }
    }

    public void showTipsDialog(String msg)
    {
        showConfirmDialog(msg, false, new OnPositiveClickListener()
        {

            @Override
            public void positiveClick()
            {
                // TODO Auto-generated method stub

            }

        }, null);
    }

    public void showTipsDialog(int resId)
    {
        showConfirmDialog(resId, false, new OnPositiveClickListener()
        {

            @Override
            public void positiveClick()
            {
            }

        }, null);
    }

    public void showConfirmDialog(String message, boolean hasNoBtn,
                                  final OnPositiveClickListener pListener, final OnNegitiveClickListener nListener)
    {

        Builder builder = new Builder(BaseActivity.this);
        builder.setMessage(message);

        builder.setTitle(R.string.dialog_title);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                pListener.positiveClick();
            }
        });
        if (hasNoBtn)
        {
            builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    nListener.NegitiveClick();
                }
            });
        }
        builder.create().show();
    }

    public void showConfirmDialog(int resId, boolean hasNoBtn,
                                  final OnPositiveClickListener pListener, final OnNegitiveClickListener nListener)
    {

        Builder builder = new Builder(BaseActivity.this);
        builder.setMessage(resId);

        builder.setTitle(R.string.dialog_title);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                pListener.positiveClick();
            }
        });
        if (hasNoBtn)
        {
            builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    nListener.NegitiveClick();
                }
            });
        }
        builder.create().show();
    }

    public void showConfirmDialog(String message, boolean hasNoBtn,
                                  String positiveStr, String negativeStr)
    {
        Builder builder = new Builder(BaseActivity.this);
        builder.setMessage(message);

        builder.setTitle("提示");
        builder.setCancelable(false);
        builder.setPositiveButton(positiveStr, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                onClickYes(dialog);
            }
        });
        if (hasNoBtn)
        {
            builder.setNegativeButton(negativeStr, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    onClickNo(dialog);
                }
            });
        }
        builder.create().show();
    }

    public void onClickYes(DialogInterface dialog)
    {
        dialog.dismiss();
    }

    public void onClickNo(DialogInterface dialog)
    {
        dialog.dismiss();
    }

    public void showDialogNetworkUnAvailable()
    {
        String msg = getResources().getString(R.string.dialog_no_network);
        showConfirmDialog(msg, false, getResources().getString(R.string.btn_ok), getResources().getString(R.string.btn_cancel));
    }

    public void showDialogNetworkTimeout()
    {
        String msg = getResources().getString(R.string.dialog_network_timeout);
        showConfirmDialog(msg, false, getResources().getString(R.string.btn_ok), getResources().getString(R.string.btn_cancel));
    }

    public void showDialogNetworkTimeoutDownload()
    {
        String msg = getResources().getString(R.string.dialog_network_timeout_download);
        showConfirmDialog(msg, false, getResources().getString(R.string.btn_ok), getResources().getString(R.string.btn_cancel));
    }


    public String getVersion() throws NameNotFoundException
    {
        PackageManager manager = getPackageManager();
        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
        return info.versionName;
    }

    public PackageInfo getpackageInfo() throws NameNotFoundException
    {
        PackageManager manager = getPackageManager();
        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
        return info;
    }

    public boolean checkNetworkState()
    {
        return NetworkState.getNetworkState(this);
    }


    public boolean ServiceUnavailable(String error)
    {
        return error != null && error.contains("The service is unavailable");
    }

    public boolean BadRequest(String error)
    {
        return error != null && error.contains("Bad Request");
    }

    public void showDatePickerDialog(final String str1, final String str2,
                                     final TextView textView, final boolean isLast)
    {
        String date = str1;
        int year = 0;
        int month = 0;
        int day = 0;
        if (!StringUtil.isEmpty(date))
        {
            String[] strs = date.split("-");
            year = Integer.parseInt(strs[0]);
            month = getIntFromString(strs[1]) - 1;
            day = getIntFromString(strs[2]);
        }
        else
        {
            Time t = new Time();
            t.setToNow();
            year = t.year;
            month = t.month;
            day = t.monthDay;
        }
        OnDateSetListener dateListener = new OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month,
                                  int dayOfMonth)
            {
                String dateStr = year + "-" + getStringFromInt(month + 1) + "-"
                        + getStringFromInt(dayOfMonth);
                String dateStr2 = str2;
                if (!isLast)
                {
                    if (!DateUtil.compare(dateStr, dateStr2))
                    {
                        dateStr = dateStr2;
                    }
                    leadsDateStart = dateStr;
                }
                else
                {
                    if (!DateUtil.compare(dateStr2, dateStr))
                    {
                        dateStr = dateStr2;
                    }
                    leadsDateEnd = dateStr;
                }
                textView.setText(dateStr);
            }
        };
        new DatePickerDialog(this, dateListener, year, month, day).show();
    }

    private int getIntFromString(String s)
    {
        if (s.startsWith("0"))
        {
            s = s.substring(1);
        }
        return Integer.parseInt(s);
    }

    private String getStringFromInt(int i)
    {
        if (i < 10)
        {
            return "0" + i;
        }
        else
        {
            return "" + i;
        }
    }

    public void showDatePickerDialog(String dateTime, OnDateSetListener dateSetListener)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (!StringUtil.isEmpty(dateTime) && dateTime.length() == 10)
        {
            year = Integer.parseInt(dateTime.substring(0, 4));
            month = Integer.parseInt(dateTime.substring(5, 7)) - 1;
            day = Integer.parseInt(dateTime.substring(8));
        }

        Log.i("showDatePickerDialog", year + "      " + (month) + "           " + day + "");
        new DatePickerDialog(this, dateSetListener, year, month, day).show();
    }

    protected String formatDateTOYYYYMMDD(int year, int monthOfYear,
                                          int dayOfMonth)
    {
        // TODO Auto-generated method stub
        Log.i("formatDateTOYYYYMMDD", year + "      " + (monthOfYear + 1) + "           " + dayOfMonth + "");
        String date;
        String mm;
        String dd;
        if (monthOfYear < 9)
        {
            mm = "0" + (monthOfYear + 1);
        }
        else
        {
            mm = String.valueOf(monthOfYear + 1);
        }

        if (dayOfMonth <= 9)
        {
            dd = "0" + dayOfMonth;
        }
        else
        {
            dd = String.valueOf(dayOfMonth);
        }
        date = year + "-" + mm + "-" + dd;
        return date;
    }


    protected boolean isEndDateSmall(String startDate, String endDate)
    {
        // TODO Auto-generated method stub
        startDate = startDate.replaceAll("-", "");
        endDate = endDate.replaceAll("-", "");
        return Integer.parseInt(startDate) > Integer.parseInt(endDate);
    }

    public void showTimePickerDialog(String time, OnTimeSetListener timeSetListener)
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (!StringUtil.isEmpty(time) && time.length() == 5)
        {
            hour = Integer.parseInt(time.substring(0, 2));
            minute = Integer.parseInt(time.substring(3, 5));
        }

        new TimePickerDialog(this, timeSetListener, hour, minute, true).show();
    }

    protected String formatTimeToHHMM(int hourOfDay, int minute)
    {
        // TODO Auto-generated method stub
        Log.i("formatTimeToHHMM", hourOfDay + "      " + "           " + minute + "");
        String date;
        String hh;
        String mm;
        if (hourOfDay <= 9)
        {
            hh = "0" + hourOfDay;
        }
        else
        {
            hh = String.valueOf(hourOfDay);
        }

        if (minute <= 9)
        {
            mm = "0" + minute;
        }
        else
        {
            mm = String.valueOf(minute);
        }
        date = hh + ":" + mm;
        return date;
    }

    protected void setText(TextView view, String text)
    {
        if (view != null)
        {
            if (!StringUtil.isEmpty(text))
            {
                view.setText(text);
            }
            else
            {
                view.setText("-");
            }
        }
        else
        {
            LogTrace.i(BASE_TAG, "setText", "view is null");
        }
    }

    protected void startActivity(Class<?> cls)
    {
        // TODO Auto-generated method stub
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
