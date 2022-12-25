package com.grampus.hualauncherkai.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StringUtil {
    public static final String EMPTY = "";
    /**
     *  �?查字符串是否为空，包括NULL、空串和只有空格的字符串
     *
     * @param x
     * @return
     */
    public static boolean isEmpty(String x) {
        return x == null || x.trim().length() == 0;
    }
    
    public static boolean isEmpty(List l) {
        return l == null || l.size() == 0;
    }
    
    public static String getCurTime() {
    	SimpleDateFormat yyyy = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return yyyy.format(new Date()) ;
    }
}

