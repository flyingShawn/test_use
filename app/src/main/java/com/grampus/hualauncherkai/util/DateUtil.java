package com.grampus.hualauncherkai.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
	private static String pattern = "yyyy-MM-dd E HH:mm:ss";
    private static SimpleDateFormat patternDF = new SimpleDateFormat(pattern,
            Locale.CHINA);

    private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat(
            "yyyy-MM-dd");
    private static final SimpleDateFormat yyyyMMddNoSep = new SimpleDateFormat(
            "yyyyMMdd");
    private static final SimpleDateFormat HHmmss = new SimpleDateFormat(
            "HH:mm:ss");
    private static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat yyyyMMddHHmmssNoSep = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    private static final SimpleDateFormat yyyyMM = new SimpleDateFormat(
            "yyyy-MM");
    public static final SimpleDateFormat yyyyMMCn = new SimpleDateFormat(
            "yyyy年MM�?");
    public static final SimpleDateFormat yyyyMMNoSep = new SimpleDateFormat(
            "yyyyMM");
    private static final SimpleDateFormat MMdd = new SimpleDateFormat("MM.dd");

    public static String makeCurrentDateTimeStr() {
        return patternDF.format(getCurrentDate());
    }

    public static String formatDate(Date d) {
        if (d == null)
            return StringUtil.EMPTY;
        return yyyyMMdd.format(d);
    }
    
    public static Date formatDateValue(Date d) {
        if (d == null)
            return null;
        String strDate = yyyyMMdd.format(d);
        return formatStrDate(strDate);
    }
        
    public static String formatDate(Date d, String format){
        if (d == null)
            return StringUtil.EMPTY;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }
    
    public static String formatDate(Date d, SimpleDateFormat format){
        if (d == null)
            return StringUtil.EMPTY;
        return format.format(d);
    }

    public static Date formatStrDate(String d) {
        if (d == null)
            return getCurrentDate();

        Date date = null;
        try {
            date = yyyyMMdd.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return getCurrentDate();
        }
        return date;
    }
    
    public static Date formatStrDateTime(String d) {
        if (d == null)
            return getCurrentDate();

        Date date = null;
        try {
            date = yyyyMMddHHmmss.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return getCurrentDate();
        }
        return date;
    }

    public static String formatDate(String strDate) {
        if (strDate == null || strDate.trim().equals(""))
            return StringUtil.EMPTY;
        try {
            return yyyyMMdd.format(yyyyMMdd.parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return StringUtil.EMPTY;
        }
    }

    public static String formatTime(Date d) {
        if (d == null)
            return StringUtil.EMPTY;
        return HHmmss.format(d);
    }

    public static String formatTime(String strTime) {
        Date date;
        if (strTime == null || strTime.length() <= 0) {
            return "";
        }
        try {
            date = yyyyMMddHHmmss.parse(strTime);
            return HHmmss.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatDateTime(Date d) {
        if (d == null)
            return StringUtil.EMPTY;
        return yyyyMMddHHmmss.format(d);
    }

    public static String formatDateTimeNoSep(Date d) {
        if (d == null)
            return StringUtil.EMPTY;
        return yyyyMMddHHmmssNoSep.format(d);
    }

    public static String formatyyMM(Date d) {
        if (d == null) {
            return StringUtil.EMPTY;
        }
        return yyyyMM.format(d);
    }

    /**
     * yyyyMMddHHmmss--->yyyyMMdd
     * 
     * @param text
     * @return
     */
    public static String renderDate(String text) {
        if (StringUtil.isEmpty(text)) {
            return StringUtil.EMPTY;
        }
        int idx = text.indexOf(" ");
        if (idx <= 0) {
            return text;
        }
        return text.substring(0, idx).trim();
    }

    /**
     * yyyyMMddHHmmss--->yyyyMMdd
     * 
     * @param text
     * @return
     */
    public static String renderTime(String text) {
        if (StringUtil.isEmpty(text)) {
            return StringUtil.EMPTY;
        }
        int idx = text.indexOf(" ");
        if (idx <= 0) {
            return text;
        }
        return text.substring(idx).trim();
    }

    public static String formatNextMonthDate(String date) {
        String strDate = "";
        Date d = formatStrDate(date);
        Date currentDate = d;
        if (currentDate == null) {
            Date nextMonth = new Date(getCurrentDate().getTime()
                    + (24 * 60 * 60 * 1000));
            strDate = formatDate(nextMonth);
        } else {
            Date nextMonth = new Date(currentDate.getTime()
                    + (24 * 60 * 60 * 1000));
            strDate = formatDate(nextMonth);
        }
        return strDate;
    }

    public static String formatPrevMonthDate(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTimeInMillis(date.getTime());
        }
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        String strTime = formatDate(new Date(cal.getTimeInMillis()));
        return strTime.replace("-", "").substring(0, 6);
    }

    public static String formatTime(int seconds) {
        String ret = "00:00:00";
        if (seconds <= 0)
            return ret;
        int sec = seconds % 60;
        int min = (seconds / 60) % 60;
        int hr = seconds / 3600;
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), hr, min, sec);
        ret = String.format("%1$tH:%1$tM:%1$tS",
                new Date(cal.getTimeInMillis()));
        return ret;
    }
    
    public static String formatTime(long seconds) {
        String ret = "";
        
        
        if (seconds <= 0) {
        	return "0�?";
        }
            
        if (seconds >= 3600*24) {
        	int day = (int) seconds / (3600*24);
        	seconds = seconds % (3600*24);
        	ret += day + "�?" ;
        } 
        if (seconds >= 3600) {
        	int hr = (int) seconds / (3600);
        	seconds = seconds % (3600);
        	ret += hr + "小时" ;
        } 
        if (seconds >= 60) {
        	int min = (int) seconds / (60);
        	seconds = seconds % (60);
        	ret += min + "�?" ;
        } 
        
        
        return ret + seconds + "�?";
    }

    public static String formatYearMonth(Date date) {
        String str = "";
        if (date == null) {
            str = yyyyMMNoSep.format(getCurrentDate());
        } else {
            str = yyyyMMNoSep.format(date);
        }
        return str;
    }

    public static String formatYearMonth() {
        return formatYearMonth(null);
    }

    public static String formatYearMonth(Date date, int tick) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTimeInMillis(date.getTime());
        }
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + tick);

        String yearMonth = yyyyMMNoSep.format(new Date(cal.getTimeInMillis()));
        return yearMonth;
    }

    public static String formatYearMonthCn(Date date, int tick) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTimeInMillis(date.getTime());
        }
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + tick);

        String yearMonth = yyyyMMCn.format(new Date(cal.getTimeInMillis()));
        return yearMonth;
    }
    
    public static String formatYearMonthCn(Date date) {
    	String yearMonth = "" ;
        if (date != null) {
        	yearMonth = yyyyMMCn.format(date);
        } 
        return yearMonth;
    }

    public static String formatYMD(Date date) {
        String str = "";
        if (date == null) {
            str = yyyyMMddNoSep.format(getCurrentDate());
        } else {
            str = yyyyMMddNoSep.format(date);
        }
        str = str.replaceAll("-", "");
        return str;
    }

    public static String formatYMD() {
        return formatYMD(null);
    }

    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    public static long getCurrentTimeMills() {
        return System.currentTimeMillis();
    }

    /**
     * �?-�?
     * 
     * @param date
     * @return
     */
    public static String formatMD(String date) {
        if (StringUtil.isEmpty(date)) {
            return "";
        }
        String strDate = "";
        try {
            strDate = MMdd.format(formatStrDate(date));
        } catch (Exception e) {
            return "";
        }
        return strDate;
    }
    
    public static Date parseString(String s) {
    	Date date = new Date(Long.parseLong(s)) ;
    	return date ;
    }
    
    public static String formatDate2Chinese(String strDate){
        if(strDate == null || strDate.length()!=14){
            return null;
        }
        
        String result = "";
        result = strDate.substring(0, 4) + "�?";
        result += changeDate(strDate.substring(4, 6)) + "�?";
        result += changeDate(strDate.substring(6, 8)) + "�?";
        result += changeDate(strDate.substring(8, 10)) + "�?";
        result += changeDate(strDate.substring(10, 12)) + "�?";
        
        return result;
    }
    
    public static String formatDateToChinese(String str){
    	System.out.println("str�?"+str);
    	Date date = new Date(Long.parseLong(str)) ;
    	String strDate = formatDateTimeNoSep(date) ;
    	System.out.println("strDate�?"+strDate);
        if(strDate == null || strDate.length()!=14){
            return null;
        }
        
        String result = "";
        result = strDate.substring(0, 4) + "�?";
        result += changeDate(strDate.substring(4, 6)) + "�?";
        result += changeDate(strDate.substring(6, 8)) + "�?";
        result += changeToAPm(strDate.substring(8, 10)) + "�?";
        result += changeDate(strDate.substring(10, 12)) + "�?";
        result += changeDate(strDate.substring(12, 14)) + "�?";       
        
        return result;
    }
    
    private static String changeDate(String str){
        String result = "";
        try{
            result = String.valueOf(Integer.parseInt(str));
        } catch(NumberFormatException e){
            result = str;
        }
        
        return result ;
    }
    
    private static String changeToAPm(String str){
        String result = "";
        try{
            int temp = Integer.parseInt(str);
            if(temp>12){
                temp -= 12;
                result = "下午" + temp;
            } else{
                result = "上午" + temp;
            }            
        } catch(NumberFormatException e){
            result = str;
        }
        
        return result;
    }
    
    public static String formateMillisToHHSSMM(long milliseconds){
    	Date date = new Date(milliseconds);
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		return formatter.format(date);
    }
    
    public static String formateMillisToYYYYMMDDHHSS(long milliseconds){
    	Date date = new Date(milliseconds);
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		return formatter.format(date);
    }
    
    public static String formateMillisToHHMM(long milliseconds){
    	Date date = new Date(milliseconds);
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		return formatter.format(date);
    }
    
    public static String getCurrentTime(){
		SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
	            "yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return yyyyMMddHHmmss.format(date);
	}
    
    public static boolean compare(String date1, String date2) {
		if (!StringUtil.isEmpty(date1) && !StringUtil.isEmpty(date2)) {
			Long d1 = Long.parseLong(date1.replaceAll("-", ""));
			Long d2 = Long.parseLong(date2.replaceAll("-", ""));
			return d1 <= d2;
		}
		return true;
	}
    
    public static String formateDateShort(String date){
    	if (date != null && date.length() > 10) {
			return date.substring(0, 10);
		}
    	return date;
    }
    
    public static String getCurrentTimeHHmm(){
		SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
	            "HH:mm");
		Date date = new Date();
		return yyyyMMddHHmmss.format(date);
	}
    
    public static String getCurrentTimeHHmmEarly3Minute(){
		SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
	            "HH:mm");
		long current = getCurrentTimeMills();
		Date date = new Date(current - 180000);
		return yyyyMMddHHmmss.format(date);
	}
}