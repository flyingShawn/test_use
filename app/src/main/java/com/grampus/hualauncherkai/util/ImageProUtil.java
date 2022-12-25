package com.grampus.hualauncherkai.util;

import android.widget.ImageView;

import com.grampus.hualauncherkai.R;

/**
 * Created by DGY on 2018/1/10.
 */

public class ImageProUtil {
    public static void updateImage(ImageView imageView, int progress){
        if (progress == 0){
            imageView.setImageResource(R.drawable.ico_p0);
        }else if (progress > 0 && progress <= 5){
            imageView.setImageResource(R.drawable.ico_p1);
        }else if (progress > 5 && progress <= 10){
            imageView.setImageResource(R.drawable.ico_p2);
        }else if (progress > 10 && progress <= 15){
            imageView.setImageResource(R.drawable.ico_p3);
        }else if (progress > 15 && progress <= 20){
            imageView.setImageResource(R.drawable.ico_p4);
        }else if (progress > 20 && progress <= 25){
            imageView.setImageResource(R.drawable.ico_p5);
        }else if (progress > 25 && progress <= 30){
            imageView.setImageResource(R.drawable.ico_p6);
        }else if (progress > 30 && progress <= 35){
            imageView.setImageResource(R.drawable.ico_p7);
        }else if (progress > 35 && progress <= 40){
            imageView.setImageResource(R.drawable.ico_p8);
        }else if (progress > 40 && progress <= 45){
            imageView.setImageResource(R.drawable.ico_p9);
        }else if (progress > 45 && progress <= 50){
            imageView.setImageResource(R.drawable.ico_p10);
        }else if (progress > 50 && progress <= 55){
            imageView.setImageResource(R.drawable.ico_p11);
        }else if (progress > 55 && progress <= 60){
            imageView.setImageResource(R.drawable.ico_p12);
        }else if (progress > 60 && progress <= 65){
            imageView.setImageResource(R.drawable.ico_p13);
        }else if (progress > 65 && progress <= 70){
            imageView.setImageResource(R.drawable.ico_p14);
        }else if (progress > 70 && progress <= 75){
            imageView.setImageResource(R.drawable.ico_p15);
        }else if (progress > 75 && progress <= 80){
            imageView.setImageResource(R.drawable.ico_p16);
        }else if (progress > 80 && progress <= 85){
            imageView.setImageResource(R.drawable.ico_p17);
        }else if (progress > 85 && progress <= 90){
            imageView.setImageResource(R.drawable.ico_p18);
        }else if (progress > 90 && progress <= 95){
            imageView.setImageResource(R.drawable.ico_p19);
        }
        else{
            imageView.setImageResource(R.drawable.ico_p19);
        }
    }
}
