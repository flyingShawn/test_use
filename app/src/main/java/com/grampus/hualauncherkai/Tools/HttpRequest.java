package com.grampus.hualauncherkai.Tools;

/**
 * Created by Grampus on 2017/3/17.
 */


import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * HttpClient GET POST PUT 请求
 *
 * @author huang
 * @date 2013-4-10
 */
public class HttpRequest
{

    //remove 掉了throws IOException
    public static String httpGet(String getUrl, Map<String, String> getHeaders)
    {
        String rs="";
        Tell.log("http请求Get URL:" + getUrl);
    try {
        URL getURL = new URL(getUrl);
        HttpURLConnection connection = (HttpURLConnection) getURL.openConnection();
        //add by gwb;2020.9.14
        connection.setConnectTimeout(5 * 1000);
        connection.setReadTimeout((8 * 1000));
        //-------------end;

        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");//在get请求中这是能在各个浏览器兼容json
        if (getHeaders != null) {
            for (String pKey : getHeaders.keySet()) {
                connection.setRequestProperty(pKey, getHeaders.get(pKey));
            }
        }
        connection.connect();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sbStr = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sbStr.append(line);
        }
        bufferedReader.close();
        connection.disconnect();
        
        Tell.log("http请求Get 结果:" + sbStr);
        rs = new String(sbStr.toString().getBytes(), "UTF-8");

    }
    catch(Exception e)
    {
        e.printStackTrace();
        Log.w("EMMHttp","httpGet--e:"+e.toString());
    }

        return rs;
    }

    public static String httpPost(String postUrl, Map<String, String> postHeaders, String postEntity) throws IOException
    {
        //Tell.log("http请求Post URL-yt:" + postUrl);

        URL postURL = new URL(postUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) postURL.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setInstanceFollowRedirects(true);
        //application/json x-www-form-urlencoded
        //httpURLConnection.setRequestProperty("Content-Type",  "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        StringBuilder sbStr = new StringBuilder();
        if (postHeaders != null)
        {
            for (String pKey : postHeaders.keySet())
            {
                httpURLConnection.setRequestProperty(pKey, postHeaders.get(pKey));
            }
        }
        if (postEntity != null)
        {

            PrintWriter out = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            out.println(postEntity);
            out.close();
            //httpURLConnection.getInputStream()
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));  //解决返回值汉字乱码的问题

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                sbStr.append(inputLine);
            }
            in.close();
        }
        httpURLConnection.disconnect();
        String rs = new String(sbStr.toString().getBytes(), "UTF-8");
        Tell.log("http请求Post 结果:" + rs);

        return rs;
    }

    public static String HttpPut(String postUrl, Map<String, String> postHeaders, String postEntity) throws Exception
    {
        URL postURL = new URL(postUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) postURL.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setInstanceFollowRedirects(true);
        //application/json x-www-form-urlencoded
        //httpURLConnection.setRequestProperty("Content-Type",  "application/x-www-form-urlencoded");//表单上传的模式
        httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");//json格式上传的模式
        StringBuilder sbStr = new StringBuilder();
        if (postHeaders != null)
        {
            for (String pKey : postHeaders.keySet())
            {
                httpURLConnection.setRequestProperty(pKey, postHeaders.get(pKey));
            }
        }
        if (postEntity != null)
        {
            JSONObject obj = new JSONObject(postEntity);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            out.println(obj);
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection
                    .getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                sbStr.append(inputLine);
            }
            in.close();
        }
        httpURLConnection.disconnect();
        return new String(sbStr.toString().getBytes(), "UTF-8");
    }
}
