package com.grampus.hualauncherkai.TcpSock;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.GlobalPara;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.service.EMMAccessibilityService;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


public class CPackOperate {

    public int m_nUniqueID = 0;
    //   CPacket m_Packet;

    private static CPackOperate mCPackOperate;

    // ScreenCastService screenCastService ;
    public static CPackOperate getInstance(){
        if(mCPackOperate == null){
            mCPackOperate = new CPackOperate();
        }
        return mCPackOperate;
    }

    Activity activity;
    Handler handler;

    public static void init(Activity activity, Handler handler)
    {
        mCPackOperate = getInstance();
        mCPackOperate.activity = activity;
        mCPackOperate.handler = handler;
    }


    /**
     * 锁屏下收到指令可以亮屏
     */
    @SuppressLint("InvalidWakeLockTag")
    private void wakeAndUnlock(Context c)
    {
        try{
            PowerManager pm;
            PowerManager.WakeLock wl;
            pm=(PowerManager) c.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            //点亮屏幕
            wl.acquire();
            Log.w("EMMScreen", "点亮");
        }catch (Exception e){
            Log.e("EMMScreen", "wakeAndUnlock--error "+e.toString());
        }
    }


    public String getString(ByteBuffer buffer)
    {
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try
        {
            charset = Charset.forName("UTF-8");// StandardCharsets.UTF_8;
            decoder = charset.newDecoder();
            // charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
            return charBuffer.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }
    }
    public void OnReceivePack(ByteBuffer bodyBuf){

    /*typedef struct screen_pack_head
      {
          int		nUniqueID;
          int		nMainCmd;
          int		nDataSize;
      }SCREEN_PACK_HEAD;*/

        bodyBuf.position(4);
        int nMainCmd = bodyBuf.order(ByteOrder.LITTLE_ENDIAN).getInt( );//取四个字节，并将其转为int类型的数字
        int nDataSize = bodyBuf.order(ByteOrder.LITTLE_ENDIAN).getInt( );

        if(nMainCmd == GlobalPara.WM_USE_NOTIFY_GET_CHANAGE_BUF){
            Log.i("EMMScreen","WM_USE_NOTIFY_GET_CHANAGE_BUF");
        }
        else if(nMainCmd == GlobalPara.WM_SINGLE_CAPTURE_START){
            Log.i("EMMScreen","WM_SINGLE_CAPTURE_START------开始监视.");
        }
        else if(nMainCmd == GlobalPara.WM_TO_ANROID_CMD_STRING) {//所有的命令全在这里加
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {  //4.3 才有辅助功能

                if (!EMMAccessibilityService.isStart()) {
                    handler.sendEmptyMessage(7);
                    NetDataHub.get().addLog("辅助功能尚未开启,无法进行控制");
                    return;
                }

                wakeAndUnlock(EMMApp.getInstance().mainContext);
                ByteBuffer byteCmdBuffer = ByteBuffer.wrap(bodyBuf.array(), 12, nDataSize - 1);//这边需要减去零结尾。这跟C++不一样。
                String szCmdLine = getString(byteCmdBuffer);

                EMMApp.getInstance().startCapture = true;
                //Home Back测试，没问题
                if (szCmdLine.equals("SendMouseStateCmd:Click_Home")) {
                    EMMAccessibilityService.getInstance().performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);//GLOBAL_ACTION_POWER_DIALOG
                  //  Android.AccessibilityServices.

                    Log.w("EMMScreen", "返回主界面");
                }
                if (szCmdLine.equals("SendMouseStateCmd:Click_Back")) {
                    EMMAccessibilityService.getInstance().performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    Log.w("EMMScreen", "退回上一步");
                }

                if ("SendMouseState:".equals(szCmdLine.substring(0, 15))) {
                    try {
                        String mousePos = szCmdLine.substring(15);
                        String[] position = mousePos.split("\\|");
                        //    Log.d("OnReceivePack", "mousePos:" + position[0]+","+position[1]+","+yPos);
                        int state = Integer.parseInt(position[0]);
                        int oldX = Integer.parseInt(position[1]);
                        int oldY = Integer.parseInt(position[2]);
                        int xPos = Integer.parseInt(position[3]);
                        int yPos = Integer.parseInt(position[4]);
                        int nTime = 50;
                        if (position.length == 6)     //之前版本的管理机没有发送时间的命令
                        {
                            nTime = Integer.parseInt(position[5]);
                        }

                        Log.w("EMMScreen", "计算倍率前：(" + oldX + "," + oldY + "),(" + xPos + "," + yPos + "),time:" + nTime);
                        oldX = (int) (oldX * EMMApp.getInstance().density);
                        oldY = (int) (oldY * EMMApp.getInstance().density);
                        xPos = (int) (xPos * EMMApp.getInstance().density);
                        yPos = (int) (yPos * EMMApp.getInstance().density);
                        //位置是传过来的像素，应该做一个转换。

                        switch (state) {
                            case 0:
                                EMMAccessibilityService.getInstance().dispatchGestureLongClick(xPos, yPos);
                                Log.w("EMMScreen", "长按鼠标中：" + xPos + "," + yPos);
                                break;
                            case 514:   //0x0202  鼠标左键弹起事件
                                Log.w("EMMScreen", "左击弹起：(" + oldX + "," + oldY + "),(" + xPos + "," + yPos + "),time:" + nTime);
                                EMMAccessibilityService.getInstance().dispatchGesture(oldX, oldY, xPos, yPos, nTime);
                                break;
                            default:
                                Log.w("EMMScreen", "其他事件" + "：" + position[0] + "," + xPos + "," + yPos);
                                break;
                        }
                    } catch (Exception e) {
                        Log.e("EMMScreen", "Failed to initial encoder, e: " + e);
                    }
                }

                if (szCmdLine.equals("StartAccessibilityService"))  //准备开始远程控制
                {
                    //setMouseClick();
                    Log.w("EMMScreen", "StartAccessibilityService---启动");
                    //m_Packet.SendScreenCmd()
                }
            }
        }
    }

}
