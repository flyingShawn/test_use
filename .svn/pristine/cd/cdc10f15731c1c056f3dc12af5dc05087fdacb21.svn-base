package com.grampus.hualauncherkai.Tools;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by Grampus on 2017/4/18.
 */

public class Tell {


    static public Toast toast;



    static public void toast(String data, Context context){
        if(toast!=null){
            toast.setText(data);
        }else{
            toast=Toast.makeText(context,data,Toast.LENGTH_LONG);
        }
        toast.show();


    }

    static public void log(String data){
        System.out.println("Hua:"+data);
    }


}
