package com.grampus.hualauncherkai.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by Grampus on 2017/4/18.
 */

public class Save
{
    public final static String SETTING = "LoginSetting";

    public final static String mainPath = "";
//    public final static String mainPath= Environment.getExternalStorageDirectory().getPath()+"/";

    public static void putValue(Context context, String key, String value)
    {
        SharedPreferences.Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putString(key, value);
        sp.commit();
    }

    public static String getValue(Context context, String key, String defValue)
    {
        SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        String value = sp.getString(key, defValue);
        return value;
    }

    static public void fileSave(Object oAuth_1, Context context, String fileName)
    {
        //保存在本地
        try
        {
//            File file=new File(mainPath+fileName);
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(oAuth_1);// 写入
            fos.close(); // 关闭输出流
            Tell.log(fileName + "存储成功");

            if (fileName.equals("whiteApp"))
            {
                Tell.log("白名单保存成功" + fileName);
            }

        }
        catch (Exception e)
        {
            Tell.log(fileName + "Object存储失败" + e.toString());
            if (fileName.equals("whiteApp"))
            {
                Tell.log("白名单保存失败" + e.toString());
            }
            e.printStackTrace();
        }
    }

    static public Object readFile(Context context, String fileName)
    {
        if (context == null)
        {
            Tell.log("读取失败，context是空");
        }
        else
        {
            Tell.log("读取继续，context不是空");

        }

        Object oAuth_1 = null;
        try
        {
//            File file=new File(mainPath+fileName);
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            oAuth_1 = ois.readObject();
            Tell.log(fileName + "读取成功");
            if (fileName.equals("whiteApp"))
            {
                Tell.log("白名单读取成功" + fileName);
            }

        }
        catch (Exception e)
        {
            Tell.log(fileName + "读取失败" + e.toString());
            if (fileName.equals("whiteApp"))
            {
            }
            return null;

        }
        return oAuth_1;

    }

    static public Object readFile(Context context, String fileName, StringBuffer allLog)
    {

        if (context == null)
        {
            Tell.log("读取失败，context是空");
        }
        else
        {
            Tell.log("读取继续，context不是空");

        }

        Object oAuth_1 = null;
        try
        {
//            File file=new File(mainPath+fileName);
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            oAuth_1 = ois.readObject();
            Tell.log(fileName + "读取成功");
            if (fileName.equals("whiteApp"))
            {
                Tell.log("白名单读取成功" + fileName);
                allLog.append("\n" + "白名单读取成功" + fileName);
            }

        }
        catch (Exception e)
        {
            Tell.log(fileName + "读取失败" + e.toString());
            if (fileName.equals("whiteApp"))
            {
                allLog.append("白名单读取失败" + e.toString());
            }
            return null;

        }
        return oAuth_1;

    }
}
