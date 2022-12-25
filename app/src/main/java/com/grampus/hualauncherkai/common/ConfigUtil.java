package com.grampus.hualauncherkai.common;

import android.content.Context;
import android.os.Environment;

public class ConfigUtil {
	public static final String version = "V1.0.0";
	//是否生产环境
	public static boolean isPRD = false;
	public static String ip = "";
	public static String httpPort = ":16670";
	public static String URLSUFFIX;
	public static String mbs_FARM_ID;
	public static String rbs_FARM_ID;
	
	
	public static final String SP_hasAuthor = "hasAuthor";
	public static final String SP_decryption = "decryption";
	public static final String SP_USERNAME = "SP_USERNAME";
	public static final String SP_USERPASS = "SP_USERPASS";
	public static final String SP_IP_ADDRESS = "SP_IP_ADDRESS";
	public static final String SP_SERVER = "SP_SERVER";
	public static final String SP_ZR_SERVER = "SP_ZR_SERVER";
	public static final String SP_WIFI_LIST = "SP_WIFI_LIST";
	public static final String SP_WIFI_CHECK = "SP_WIFI_CHECK";
	public static final String SP_ZR_CHECK = "SP_ZR_CHECK";
	public static final String SP_EXIT = "eixt_app";
	public static final String SP_UNINSTALL = "uninstall_app";
	
	public final static int pageCount = 20; //listview列表数目
	public static String SERVER = "";
	
	public static final String SERVER_CMD_REQUEST = "telAuditCmd.php?";
	public static final String SERVER_CMD_DOWNLOAD = "teldown";
	
	public static final String GET_SERVER_IP_URL = "http://www.yangtusoft.cn/control.asp?action=getservip&SNUM=%s";
	
	public static final String URL_LOGIN = "Action=logon&UniqueID=%s&TelVersion=%s";
	public static final String URL_CHECKUSER = "Action=checkuser&UniqueID=%S&UID=%s&PWD=%s";
	public static final String URL_GETWAITAUDIT = "Action=getwaitaudit&UniqueID=%s&PageIndex=%s";
	public static final String URL_AUDITEND = "Action=auditend&UniqueID=%s&IDList=%s&AuditPass=%s&AuditCmt=%s";
	public static final String URL_GET_HISTORY_AUDIT = "Action=gethistoryaudit&UniqueID=%s&BTime=%s&ETime=%s&PageIndex=%s";
	public static final String URL_UPLOAD_GPS = "showmap/showmap.php?device_name=%s&IP=%s&MAC=%s&longitude=%s&latitude=%s";
	public static final String URL_UPLOAD_LOG = "TelSafeDesk.php?Action=LoginLog&Mac=%s";
	public static String getSDcardPath(Context mContext){
		return mContext.getExternalCacheDir().toString() + "/yangtu/files/";
	}
	
	public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/yangtu/files/";
	public static final String SD_PATH_DecryptFiles = "/yangtu/files/DecryptFiles/";
}
