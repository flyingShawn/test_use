package com.grampus.hualauncherkai.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
	
	// SharePreference用法
	public static void insertDataToLoacl(Context context,String key,String value) {
		SharedPreferences settings = context.getSharedPreferences("spXML", 0);
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.putString(key, value);
		localEditor.commit();
	}
	// SharePreference用法
	public static String getDataFromLoacl(Context context,String key) {
		SharedPreferences settings = context.getSharedPreferences("spXML", 0);
		return settings.getString(key,"");
	}
	// SharePreference用法
	public static void clearDataFromLoacl(Context context) {
		SharedPreferences settings = context.getSharedPreferences("spXML", 0);
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.clear().commit();
	}
	
	// SharePreference用法
	public static void insertBooleanDataToLoacl(Context context,String key,Boolean value) {
		SharedPreferences settings = context.getSharedPreferences("spXML", 0);
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.putBoolean(key, value);
		localEditor.commit();
	}
	// SharePreference用法
	public static Boolean getBooleanDataFromLoacl(Context context,String key) {
		SharedPreferences settings = context.getSharedPreferences("spXML", 0);
		return settings.getBoolean(key,false);
	}
}
