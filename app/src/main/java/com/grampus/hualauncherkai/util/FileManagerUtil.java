package com.grampus.hualauncherkai.util;

import java.io.File;
import java.math.BigDecimal;

import android.os.Environment;
import android.os.StatFs;

public class FileManagerUtil {
	
	public static boolean hasSdcard() {
	     String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
	 }

	public long getSDFreeSize(){  
	     //取得SD卡文件路�?  
	     File path = Environment.getExternalStorageDirectory();   
	     StatFs sf = new StatFs(path.getPath());   
	     //获取单个数据块的大小(Byte)  
	     long blockSize = sf.getBlockSize();   
	     //空闲的数据块的数�?  
	     long freeBlocks = sf.getAvailableBlocks();  
	     //返回SD卡空闲大�?  
	     //return freeBlocks * blockSize;  //单位Byte  
	     //return (freeBlocks * blockSize)/1024;   //单位KB  
	     return (freeBlocks * blockSize)/1024 /1024; //单位MB  
	   }   
	
	public long getSDAllSize(){  
	     //取得SD卡文件路�?  
	     File path = Environment.getExternalStorageDirectory();   
	     StatFs sf = new StatFs(path.getPath());   
	     //获取单个数据块的大小(Byte)  
	     long blockSize = sf.getBlockSize();   
	     //获取�?有数据块�?  
	     long allBlocks = sf.getBlockCount();  
	     //返回SD卡大�?  
	     //return allBlocks * blockSize; //单位Byte  
	     //return (allBlocks * blockSize)/1024; //单位KB  
	     return (allBlocks * blockSize)/1024/1024; //单位MB  
	   }    
	
	/**   
     * 获取文件夹大�?   
     * @param file File实例   
     * @return long      
     */     
    public static String getFolderSize(String filePath){    
   
        String size = "";
        File file = new File(filePath);
        if (file.exists()) {
			return getFormatSize(getFolderSize(file));
		} 
        else {
        	return size;    
		}
    }   
    
	/**   
     * 获取文件夹大�?   
     * @param file File实例   
     * @return long      
     */     
    public static long getFolderSize(File file){    
   
        long size = 0;    
        try {  
            File[] fileList = file.listFiles();     
            for (int i = 0; i < fileList.length; i++)     
            {     
                if (fileList[i].isDirectory())     
                {     
                    size = size + getFolderSize(fileList[i]);    
   
                }else{     
                    size = size + fileList[i].length();    
   
                }     
            }  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }     
       //return size/1048576;    
        return size;    
    }    
      
    /**   
     * 删除指定目录下文件及目录    
     * @param deleteThisPath   
     * @param filepath   
     * @return    
     */     
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {     
        if (!StringUtil.isEmpty(filePath)) {     
            try {  
                File file = new File(filePath);     
                if (file.isDirectory()) {// 处理目录     
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {     
                        deleteFolderFile(files[i].getAbsolutePath(), true);     
                    }      
                }     
                if (deleteThisPath) {     
                    if (!file.isDirectory()) {// 如果是文件，删除     
                        file.delete();     
                    } 
                    else 
                    {// 目录     
	                   if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除     
	                            file.delete();     
	                        }     
                    }     
                }  
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }     
        }     
    }    
    /** 
     * 格式化单�? 
     * @param size 
     * @return 
     */  
    public static String getFormatSize(double size) {  
        double kiloByte = size/1024;  
        if(kiloByte < 1) {  
            return "0KB";  
        }  
          
        double megaByte = kiloByte/1024;  
        if(megaByte < 1) {  
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));  
            return result1.setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";  
        }  
          
        double gigaByte = megaByte/1024;  
        if(gigaByte < 1) {  
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));  
            return result2.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";  
        }  
          
        double teraBytes = gigaByte/1024;  
        if(teraBytes < 1) {  
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));  
            return result3.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";  
        }  
        BigDecimal result4 = new BigDecimal(teraBytes);  
        return result4.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";  
    }  
}
