package com.grampus.hualauncherkai.TcpSock;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.GlobalPara;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.Tools.Save;
import com.grampus.hualauncherkai.service.EMMAccessibilityService;
import com.grampus.hualauncherkai.service.ScreenCastService;
import com.grampus.hualauncherkai.util.DeviceInfoUtil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.grampus.hualauncherkai.util.DeviceInfoUtil.getPhoneIp;
import static com.grampus.hualauncherkai.util.DeviceInfoUtil.getWifiMacAddress;

public class CLogonSock extends Thread{


    //private Context context = null;

    private final String TAG = "EMMTcpSocketClient";

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private BufferedOutputStream bufferedOutputStream;
    private Handler handler;

    public InetAddress getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(InetAddress remoteHost) {
        this.remoteHost = remoteHost;
    }

    private InetAddress remoteHost;
    private int remotePort;
    public int m_nEquipID = 0;
    public int m_nTimerCount = 0;

    private CPacket m_pack;



    public CLogonSock(InetAddress remoteHost, int remotePort) {
        super( "CLogonSock");
        try {
            this.remoteHost = remoteHost;
            this.remotePort = remotePort;

            EMMApp.getInstance().centerServerIp = remoteHost.getHostAddress();
        }
        catch(Exception e) {

        }
    }

    private Activity mActivity;
    private Handler mHandler2;

    public void init(Activity activity, Handler handler)
    {
        mActivity = activity;
        mHandler2 = handler;
    }

    public String inet_ntoa(long raw) {
        /*
        return ((add & 0xff000000) >> 24) + "." + ((add & 0xff0000) >> 16)
                + "." + ((add & 0xff00) >> 8) + "." + ((add & 0xff));
                */
            byte[] b = new byte[] {(byte)(raw >> 24), (byte)(raw >> 16), (byte)(raw >> 8), (byte)raw};
            try {
                return InetAddress.getByAddress(b).getHostAddress();
            } catch(Exception e) {
                //No way here
                return null;
            }
    }

    public void OnReceiveCenterServPack(int nHeadCmd,long nReserved,ByteBuffer bodyBuf,int nManageID){

        //System.out.println("OnReceivePack------nHeadCmd:"+nHeadCmd);

        if(nHeadCmd == GlobalPara.WM_CLINET_LOGON_END){
            bodyBuf.position(0);
            m_nEquipID = bodyBuf.order(ByteOrder.LITTLE_ENDIAN).getInt( );//取四个字节，并将其转为int类型的数字
            m_pack.m_nUniqueID = m_nEquipID;
            NetDataHub.get().g_nEquipID = m_nEquipID;
            Log.i("EMMScreen","收到服务器定时器消息------WM_CLINET_LOGON_END.");
            NetDataHub.get().addLog("OnReceiveCenterServPack---接收登陆成功---nEquipID:" + m_nEquipID + "---------WM_CLINET_LOGON_END.");
            //Log.i("OnReceiveCenterServPack","-----nEquipID:" + m_nEquipID + "---------WM_CLINET_LOGON_END.");
        }
        else if(nHeadCmd == GlobalPara.WM_ONLINE_TIMER){
            m_nTimerCount = 0;
            //Log.i("OnReceivePack","------收到服务器定时器消息------WM_ONLINE_TIMER.");
            NetDataHub.get().addLog("OnReceiveCenterServPack---收到定时器消息---WM_ONLINE_TIMER.");
        }
        else if(nHeadCmd == GlobalPara.SCREEN_BEGIN){
            try {

                mHandler2.sendEmptyMessage(12);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    NetDataHub.get().addLog("OnReceiveCenterServPack---SCREEN_BEGIN---安卓版本>=5");

                    EMMApp.getInstance().g_testCount = 0;
                 // boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
                    Log.i("EMMScreen", "收到服务器消息------SCREEN_BEGIN--- ");
                    String szManageIP = inet_ntoa(nReserved);
                    if (szManageIP.equals("127.0.0.1")) {
                        szManageIP = remoteHost.getHostAddress();
                    }
                    //String szManageIP = remoteHost.toString();
                    EMMApp.getInstance().serverIp = szManageIP;

                    if (ScreenCastService.mScreenCastService != null) {
                        NetDataHub.get().addLog("SCREEN_BEGIN---当前设备已在远程中，来自IP"+szManageIP+"的管理机远程失败");
                        return;
                    }
                    EMMApp.getInstance().equipID = m_nEquipID;
                    EMMApp.getInstance().manageID = nManageID;

                    beginService();
                    NetDataHub.get().addLog("EMMLogonSock---接收到开始屏幕监视消息--nManageID:" + nManageID + "  管理机IP:" + szManageIP );
                } else {
                    NetDataHub.get().addLog("EMMLogonSock---接收开始屏幕监视消息----安卓版本<5  不能屏幕监视.");
                }

                // Window window = MainActivity.activity.getWindow();
                //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }catch (Exception e) {
                NetDataHub.get().addLog("nHeadCmd ==GlobalPara.SCREEN_BEGIN---error"+e.toString());
                Log.e(TAG, "OnReceiveCenterServPack----error:"+e.toString() );
            }
        }
        else if(nHeadCmd == GlobalPara.WM_CLIENT_UNIQUEID_DOUBLE){  // add by fsy 2022.4.15 DiskNum标识重复的消息
            try
            {
                NetDataHub.get().addLog("OnReceiveCenterServPack---DiskNum:" + EMMApp.getInstance().diskNum);
                if(EMMApp.getInstance().mainContext!=null)
                {
                    EMMApp.getInstance().macAddr = getWifiMacAddress();//重新获取一下MAC
                    EMMApp.getInstance().diskNum = EMMApp.getInstance().macAddr;
                    Save.fileSave(EMMApp.getInstance().diskNum, EMMApp.getInstance().mainContext, "diskNum");
                }
                NetDataHub.get().addLog("OnReceiveCenterServPack---DiskNum重新获取---DiskNum:" + EMMApp.getInstance().diskNum);
            }catch (Exception e)
            {
                NetDataHub.get().addLog("GlobalPara.WM_CLIENT_UNIQUEID_DOUBLE---error"+e.toString());
            }
        }
    }


    /**
    * @author  fsy
    * @date    2022/1/6 14:50
    * @return
    * @description   辅助服务新增openMainActivity（），可以达成效果，应用在后台并未获取截屏权限且非默认桌面下
     * 仍然可自动跳转打开同意权限，连上远程。安卓务必开启自启动和允许后台弹出
    */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void beginService() {
        try{
            int stateResultCode = Activity.RESULT_OK;
          //  Intent stateResultData = EMMApp.getInstatnce().resultData;

            NetDataHub.get().addLog("EMMLogonSock---收到屏幕监视消息");
            if (stateResultCode != 0 && EMMApp.getInstance().resultData != null) {
                NetDataHub.get().addLog("EMMLogonSock---收到屏幕监视消息1111");
                startScreenService(EMMApp.getInstance().resultData);
                NetDataHub.get().addLog("EMMLogonSock---收到屏幕监视消息2222");
            }else {
                NetDataHub.get().addLog("EMMLogonSock---收到屏幕监视消息3333");
                if(EMMAccessibilityService.isStart()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
  //                          EMMAccessibilityService.getInstance().performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);//GLOBAL_ACTION_HOME

                            NetDataHub.get().addLog("EMMLogonSock---收到屏幕监视消息4444");
                            EMMAccessibilityService.getInstance().openMainActivity();
                            mHandler2.sendEmptyMessageDelayed(10,800); //命令MainActivity启动截屏框
                            Log.w("EMMLogonSock", "EMMAccessibilityService------已启用");

                            for (int i = 0; i < 100 ; i++) {
                                if(EMMApp.getInstance().resultData != null) {
                                    startScreenService(EMMApp.getInstance().resultData);
                                    return;
                                }
                                try {
                                    sleep(30);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }

                Log.w("EMMLogonSock", "startService------启动服务---失败");
                NetDataHub.get().addLog("startService---启动服务---失败");
                mHandler2.sendEmptyMessage(8);

            }

        }catch (Exception e){
            Log.e("EMMScreen","startService---: "+e.toString());
            NetDataHub.get().addLog("startService---error"+e.toString());
        }
    }

	//add by fsy 2021.11.11有默认桌面的话，开着辅助服务应该可以自动弹出并同意
    private void startScreenService(Intent stateResultData)
    {
        Context contex = EMMApp.getInstance().mainContext;
        final Intent intent = new Intent(contex, ScreenCastService.class);
        NetDataHub.get().addLog("startScreenService---启动服务---");
        //intent.putExtra(ExtraIntent.RESULT_CODE.toString(), stateResultCode);
        intent.putExtra("RESULT_DATA", stateResultData);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        contex.startService(intent);
    }

    private void WhileRecvDataTh(){
        System.out.println("WhileRecvDataTh--------begin  threadid:"+android.os.Process.myTid());
        NetDataHub.get().addLog("CLogonSock----WhileRecvDataTh---");
        int SOCK_MAX_PACKSIZE = 20*1024*1024;
        int nPackBodySize = 1024*100;
        ByteBuffer headBuf = ByteBuffer.allocate(100);
        ByteBuffer bodyBuf = ByteBuffer.allocate(nPackBodySize);

        boolean m_bPackHeadRevEnd = false;
        boolean m_bCurrentPackRevEnd = false;
        int m_nCurrentPackRevSize = 0;
        int m_dwCurrentPackBodySize = 0;
        int m_dwUnZipBodySize = 0;
        int m_nHeadCmd = 0;
        long m_nReserved = 0;
        int nManageID = 0;

        try {
            //int nRead = inputStream.read(readBuf);
            //System.out.println("ReadBuf----------nRead:"+nRead);
            while (true) {
                int HeadLen = 17 * 4;
                if (!m_bPackHeadRevEnd) {
                    int nRec = inputStream.read(headBuf.array(), m_nCurrentPackRevSize, HeadLen - m_nCurrentPackRevSize);
                    if (nRec <= 0) {
                        System.out.println("WhileRecvDataTh---sock read header error3");
                        break;
                    } else {
                        m_nCurrentPackRevSize += nRec;
                        if (m_nCurrentPackRevSize == HeadLen)//包头接全了
                        {
                            m_bPackHeadRevEnd = true;
                            m_bCurrentPackRevEnd = false;
                            m_nCurrentPackRevSize = 0;

                            m_nHeadCmd = headBuf.order(ByteOrder.LITTLE_ENDIAN).getInt();

                            headBuf.position(8);
                            int nPackFlag = headBuf.order(ByteOrder.LITTLE_ENDIAN).getInt( );
                            //int nPackFlag = SockTransfer.int32Reverse(headBuf.getInt());
                            m_nReserved = headBuf.order(ByteOrder.BIG_ENDIAN).getInt( );

                            if (nPackFlag != GlobalPara.PACK_XFCenterServIDENTIFY) {
                                System.out.println("WhileRecvDataTh---packHead.nPackFlag != PACK_IDENTIFY error.");
                                close();
                                break;
                            }

                            headBuf.position(12*4);
                            //m_dwCurrentPackBodySize = SockTransfer.int32Reverse(headBuf.getInt());
                            m_dwCurrentPackBodySize = headBuf.order(ByteOrder.LITTLE_ENDIAN).getInt( )-HeadLen;


                            headBuf.position(15*4);
                            nManageID = headBuf.order(ByteOrder.LITTLE_ENDIAN).getInt( );

                            if (m_dwCurrentPackBodySize == (HeadLen) ) {
                                m_bPackHeadRevEnd = false;
                                m_bCurrentPackRevEnd = false;
                                m_nCurrentPackRevSize = 0;
                                bodyBuf.rewind();
                                headBuf.rewind();

                                OnReceiveCenterServPack(m_nHeadCmd,m_nReserved,bodyBuf,nManageID);
                                continue;
                            }

                            if (m_dwCurrentPackBodySize > SOCK_MAX_PACKSIZE) {
                                System.out.println("WhileRecvDataTh---pack too large error.");
                                break;
                            }
                            else if (m_dwCurrentPackBodySize > nPackBodySize) {
                                bodyBuf = ByteBuffer.allocate(m_dwCurrentPackBodySize);
                                nPackBodySize = m_dwCurrentPackBodySize;
                            }
                        }
                    }
                } else if (!m_bCurrentPackRevEnd) {
                    int nRec = inputStream.read(bodyBuf.array(), m_nCurrentPackRevSize, m_dwCurrentPackBodySize - m_nCurrentPackRevSize);
                    if (nRec <= 0) {
                        System.out.println("WhileRecvDataTh---sock read body error4");
                        break;
                    } else {
                        //WriteLogEx(_T("RecvDataTh--Body----m_hsock:%d---接包身 nRec:%d"),nRec,m_hsock);

                        m_nCurrentPackRevSize += nRec;
                        if (m_nCurrentPackRevSize == m_dwCurrentPackBodySize)//接全包身
                        {
                            //WriteLogEx(_T("RecvDataTh--Body----nRec:%d---m_hsock:%d---包身接成功"),nRec,m_hsock);

                            m_bPackHeadRevEnd = false;
                            m_bCurrentPackRevEnd = false;
                            m_nCurrentPackRevSize = 0;

                            OnReceiveCenterServPack(m_nHeadCmd,m_nReserved,bodyBuf,nManageID);

                            bodyBuf.rewind();
                            headBuf.rewind();
                        }
                    }
                }
            }
        }
        catch(IOException e){
            NetDataHub.get().addLog("CLogonSock---WhileRecvDataTh---异常出错:"+e.toString());
            e.printStackTrace();
        }
        close();
    }

    void BeginConnectAndRecvTh(){

        try {

            NetDataHub.get().addLog("BeginConnectAndRecvTh---Connect XFServer："+remoteHost.toString()+" Begin");

            Log.e(TAG, "BeginConnectAndRecvTh---Connect XFServer："+remoteHost.toString()+" Begin");
            socket = new Socket();
            SocketAddress socAddress = new InetSocketAddress(remoteHost, remotePort);
            socket.connect(socAddress, 3000);
            //socket = new Socket(remoteHost, remotePort);

            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            //bufferedOutputStream = new BufferedOutputStream(outputStream);//del by gwb;感觉没有必要用这个。用了BufferedOutputStream还得flush数据才真实发。

            if(m_pack == null) {
                m_pack = new CPacket();
            }

            DeviceInfoUtil.initDeviceInfo();
            String szIP = getPhoneIp();
            String szMac = EMMApp.getInstance().macAddr;
            String szDiskNum = EMMApp.getInstance().diskNum;
//            String szHostName =   android.os.Build.BRAND+"-" +android.os.Build.MODEL;// 手机品牌+手机型号

            String szHostName =  android.os.Build.BRAND+"-" + EMMApp.getInstance().deviceName;// 手机品牌+手机型号

            szHostName = szHostName.replaceAll(" ", "-");//空格去除
            if(m_pack.SendLogonToCenterServ(outputStream,szIP,szMac,szDiskNum,szHostName) == false)
            {
                NetDataHub.get().addLog("BeginConnectAndRecvTh---连接中心服务器失败 PhoneIp:"+szIP+" Mac:"+szMac + " HostName:"+szHostName);
                EMMApp.getInstance().canSendLog = false;
            }else {
                NetDataHub.get().addLog("BeginConnectAndRecvTh---连接中心服务器成功 PhoneIp:"+szIP+" Mac:"+szMac + " HostName:"+szHostName);
 			//	EMMApp.getInstance().canSendLog = true;
            }
            //m_pack.SendCmdToCenterServ(outputStream,GlobalPara.WM_ONLINE_TIMER);
            new Thread(new Runnable() { // 匿名类的Runnable接口
                @Override
                public void run() {
                    WhileRecvDataTh();
                }
            }).start();

        }catch (Exception e) {
            NetDataHub.get().addLog("BeginConnectAndRecvTh---catch Error:"+e.toString());
            Log.e(TAG, "Socket creation failed - " + e.toString());
            socket = null;
            outputStream = null;
            bufferedOutputStream = null;
        }
    }
    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        try {
            BeginConnectAndRecvTh();

            Looper.prepare();
            handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    if(message == null || message.obj == null) return;
                    byte[] msg = (byte[])message.obj;
                    try {
                        if(socket != null) {

                            System.out.println("handleMessage---发送字节数为:" + msg.length + "线程ID:" + android.os.Process.myTid());
                        }
                        //sleep(15000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        close();
                    }
                }
            };
            Looper.loop();

        } catch (Exception e) {
            Log.e(TAG, "CLogon create failed - " + e.toString());
            NetDataHub.get().addLog("CLogonSock建立连接异常：" + e.toString());
            socket = null;
            outputStream = null;
            bufferedOutputStream = null;
        }
    }

    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;
                outputStream = null;
                bufferedOutputStream = null;
            }
        }
    }
    public void SendTelLogToServ(String data){
        try{
           // String d = "\nsendok";
            m_pack.SendDataToCenterServ(outputStream, GlobalPara.WM_EMM_ONLINE_LOG,data.getBytes());
        }catch (Exception e){
            Log.e(TAG,"SendTelLogToServ error"+e.toString());
        }

    }
    public void SendTimerCmdToServ()
    {
        boolean bOK = false;
        try {
            if(handler == null || socket == null || outputStream == null) {
                NetDataHub.get().addLog("SendTimerCmdToServ---连接存在异常！！！");
            }
            else
            {
                NetDataHub.get().addLog("SendTimerCmdToServ---发送定时器到XFServer.");
                //System.out.println("SendTimerCmdToServ---------发送定时器到XFServer.");
                Log.w(TAG,outputStream.toString());
                m_pack.SendCmdToCenterServ(outputStream, GlobalPara.WM_ONLINE_TIMER);
				//EMMApp.getInstance().canSendLog = true;
                bOK = true;
            }
        } catch (Exception e) {
            NetDataHub.get().addLog("SendTimerCmdToServ---catch Error："+e.toString());
            EMMApp.getInstance().canSendLog = false;
            e.printStackTrace();
        }

        if(!bOK) {
            close();
            NetDataHub.get().addLog("SendTimerCmdToServ---发送失败，开始重连服务器.");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            BeginConnectAndRecvTh();
        }
    }
    public void send(final byte[] data) {
        if(handler == null || socket == null || outputStream == null)  {
            return;
        }

        try {

            try {

                if(data.length>0  ) {
                    Message message = handler.obtainMessage();
                    message.obj = data;
                    handler.sendMessage(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //System.out.println("send----消息之后结束-------调用发送:" + data.length + "线程ID:" + android.os.Process.myTid());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
