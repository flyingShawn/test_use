/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.grampus.hualauncherkai.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * The Utils for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class Utils  {
    public static String TAG = "SampleUtils";
    /**
     * Get help string from html file
     * @param context: Context object
     * @param filePath: html file path
     * @return string
     */
    //public static String getStringFromHtmlFile (Context context, String filePath) {
    public static String getStringFromHtmlFile (Context context, int fileid) {//add by gwb;
        String result = "";
      //  if (null == context || null == filePath) {
      //      return result;
      //  }

        InputStream stream = null;
        BufferedReader reader = null;
        InputStreamReader streamReader = null;
        try {
            // Read html file into buffer
            //stream = context.getAssets().open(filePath); del by gwb;
            //streamReader = new InputStreamReader(stream, "utf-8");   del by gwb;


            //----add by gwb;2020.10.8
            Resources myResources = context.getResources();
            stream = myResources.openRawResource(fileid);
            streamReader = new InputStreamReader (stream, "UTF-8");
            //----------end.

            reader = new BufferedReader(streamReader);
            StringBuilder builder = new StringBuilder();
            String line = null;

            boolean readCurrentLine = true;
            // Read each line of the html file, and build a string.
            while ((line = reader.readLine()) != null) {
                // Don't read the Head tags when CSS styling is not supporeted. 
                if (line.contains("<style")) {
                    readCurrentLine = false;
                } else if (line.contains("</style")) {
                    readCurrentLine = true;
                }
                if (readCurrentLine) {
                    builder.append(line).append("\n");
                }
            }
            result = builder.toString();
        } catch (FileNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            if (null != streamReader) {
                try {
                    streamReader.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
        return result;
    }

}