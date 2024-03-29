package com.grampus.hualauncherkai.TcpSock;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.GlobalPara;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.service.ScreenCastService;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Inflater;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TcpSocketClient extends Thread {

    private final String TAG = "TcpSocketClient";

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private BufferedOutputStream bufferedOutputStream;
    private Handler handler;
    private InetAddress remoteHost;
    private int remotePort;
    private CPackOperate m_PackOp;
    private CPacket m_pack;
    private int m_nFlag = 0;
    public boolean isConnectOK = false;

    public TcpSocketClient(InetAddress remoteHost, int remotePort) {
        super( "TcpSocketClientThread");
        try {
            // this.remoteHost = remoteHost;
            // this.remotePort = remotePort;

            String szTemp = EMMApp.getInstance().serverIp;
            //String szTemp = "127.0.0.1";
            this.remoteHost = InetAddress.getByName(szTemp);
            this.remotePort = 6674;
           // m_PackOp = new CPackOperate();
            m_PackOp = CPackOperate.getInstance();

            NetDataHub.get().addLog("TcpSocketClient---szTempIP："+szTemp);

        }
        catch(Exception e) {

        }
    }
    public CPackOperate getPackOp() {
        return m_PackOp;
    }


    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }
    private void WhileRecvDataTh(){
        System.out.println("WhileRecvDataTh----------begin  threadid:"+android.os.Process.myTid());
        NetDataHub.get().addLog("WhileRecvDataTh----------begin");

        int SOCK_MAX_PACKSIZE = 20*1024*1024;
        int nPackBodySize = 1024*100;
        ByteBuffer headBuf = ByteBuffer.allocate(100);
        ByteBuffer bodyBuf = ByteBuffer.allocate(nPackBodySize);

        boolean m_bPackHeadRevEnd = false;
        boolean m_bCurrentPackRevEnd = false;
        int m_nCurrentPackRevSize = 0;
        int m_dwCurrentPackBodySize = 0;
        int m_dwUnZipBodySize = 0;
        int m_bIsZip = 0;

        try {

            //int nRead = inputStream.read(readBuf);
            //System.out.println("ReadBuf----------nRead:"+nRead);
            while (true) {
                int HeadLen = 5 * 4;//sizeof(P_PACKHEAD);
                if (!m_bPackHeadRevEnd) {
                    int nRec = inputStream.read(headBuf.array(), m_nCurrentPackRevSize, HeadLen - m_nCurrentPackRevSize);
                    if (nRec <= 0) {
                        NetDataHub.get().addLog("WhileRecvDataTh---sock read header error.");
                        break;
                    } else {
                        m_nCurrentPackRevSize += nRec;
                        if (m_nCurrentPackRevSize == HeadLen)//包头接全了
                        {
                            m_bPackHeadRevEnd = true;
                            m_bCurrentPackRevEnd = false;
                            m_nCurrentPackRevSize = 0;

                            int nPackFlag = headBuf.order(ByteOrder.LITTLE_ENDIAN).getInt( );
                            //int nPackFlag = SockTransfer.int32Reverse(headBuf.getInt());

                            if (nPackFlag != GlobalPara.PACK_IDENTIFY) {
                                NetDataHub.get().addLog("WhileRecvDataTh---packHead.nPackFlag != PACK_IDENTIFY error.");
                                break;
                            }
                            headBuf.position(8);
                            m_bIsZip = headBuf.order(ByteOrder.LITTLE_ENDIAN).getInt();
                            m_dwUnZipBodySize = headBuf.order(ByteOrder.LITTLE_ENDIAN).getInt();

                            headBuf.position(16);
                            //m_dwCurrentPackBodySize = SockTransfer.int32Reverse(headBuf.getInt());
                            m_dwCurrentPackBodySize = headBuf.order(ByteOrder.LITTLE_ENDIAN).getInt( );

                            if (m_dwCurrentPackBodySize == 0) {
                                m_bPackHeadRevEnd = false;
                                m_bCurrentPackRevEnd = false;
                                m_nCurrentPackRevSize = 0;
                                headBuf.position(0);

                                m_PackOp.OnReceivePack(bodyBuf);
                                continue;
                            }

                            if (m_dwCurrentPackBodySize > nPackBodySize) {
                                bodyBuf = ByteBuffer.allocate(m_dwCurrentPackBodySize);
                                nPackBodySize = m_dwCurrentPackBodySize;
                            } else if (m_dwCurrentPackBodySize > SOCK_MAX_PACKSIZE) {
                                NetDataHub.get().addLog("WhileRecvDataTh---pack too large error.");
                                break;
                            }
                        }
                    }
                } else if (!m_bCurrentPackRevEnd) {
                    int nRec = inputStream.read(bodyBuf.array(), m_nCurrentPackRevSize, m_dwCurrentPackBodySize - m_nCurrentPackRevSize);
                    if (nRec <= 0) {
                        NetDataHub.get().addLog("WhileRecvDataTh---sock read body error.");
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

                            if(m_bIsZip==1 && m_dwUnZipBodySize>0){
                                ByteBuffer unzipByteBuf = ByteBuffer.allocate(m_dwUnZipBodySize);
                                unzipByteBuf.put(decompress(bodyBuf.array()));
                                m_PackOp.OnReceivePack(unzipByteBuf);
                            }
                            else {
                                m_PackOp.OnReceivePack(bodyBuf);
                            }
                            bodyBuf.rewind();
                            headBuf.rewind();
                        }
                    }
                }
            }
        }
        catch(IOException e){
          Log.e("EMMScreen","TCPsocket -socket["+socket+"]---error--" + e.toString());
          NetDataHub.get().addLog("TCPsocketClient--socket["+socket+"]------error-" + e.toString());

          e.printStackTrace();
        }
        finally {

            close();
        }

    }

    void LinkSwitchCenter() throws Exception {

            m_pack.m_nUniqueID = EMMApp.getInstance().equipID;
            socket = new Socket();

            //socket = new Socket();
            SocketAddress sAddress = new InetSocketAddress(remoteHost, remotePort);
            socket.connect(sAddress, 3000);
            NetDataHub.get().addLog("LinkSwitchCenter---连接中转服务器:" + remoteHost.toString()+" "+remotePort+"成功");
            isConnectOK = true;

            String szID;
            szID = EMMApp.getInstance().equipID + "|" + EMMApp.getInstance().manageID;

            ByteBuffer buf = ByteBuffer.allocate(szID.length());
            buf.put(szID.getBytes());

        //    NetDataHub.get().addLog("LinkSwitchCenter---ByteBuffer:" +buf.toString());
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            m_pack.SendFirseDataToSwitchServ(outputStream,GlobalPara.WM_SCREEN_SWITCH_CLIENT_LOGON,szID);

            NetDataHub.get().addLog("LinkSwitchCenter---发送首个包给中转服务器---szID:" +szID);
    }

    void BeginConnectAndRecvTh(){

        try {
            NetDataHub.get().addLog("EMMTcpSocketClient---BeginConnectAndRecvTh");
            m_pack = new CPacket();

            try {
                socket = new Socket();
              //  remotePort = 6666;
                SocketAddress socAddress = new InetSocketAddress(remoteHost, remotePort);
                socket.connect(socAddress, 2000);
                NetDataHub.get().addLog("BeginConnectAndRecvTh---connect");

                isConnectOK = true;
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                //bufferedOutputStream = new BufferedOutputStream(outputStream);//del by gwb;感觉没有必要用这个。用了BufferedOutputStream还得flush数据才真实发。
            }catch(Exception e) {
                NetDataHub.get().addLog("BeginConnectAndRecvTh---连接管理机失败 error:" + e.toString());

                isConnectOK = false;
                socket = null;
                try {
                    remoteHost = InetAddress.getByName(EMMApp.getInstance().centerServerIp);
                    remotePort = 6675;

                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                    NetDataHub.get().addLog("ip获取不正确 Error:" + ex.toString());
                }

                LinkSwitchCenter();
            }
            int screenWidth =  EMMApp.getInstance().screeenWidth;  //
            int screenHeight = EMMApp.getInstance().screenHight; //


            String szIP = EMMApp.getInstance().serverIp;
            String szMac = "48:2c:a0:ce:f2:ea";
            String szHostName = "Android";

            int nUniqueID = NetDataHub.get().g_nEquipID;

            NetDataHub.get().addLog("BeginConnectAndRecvTh---"+"Width:"+screenWidth+",Height:"+screenHeight);
            m_pack.m_nUniqueID = nUniqueID;
            m_pack.SendFirstPack(outputStream);

            if(m_pack.SendLogonInfo(outputStream,szIP,szMac,szHostName,nUniqueID,screenHeight,screenWidth) == false)
            {
            }

            System.out.println("tcp----begin  threadid:"+android.os.Process.myTid());
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

    @Override
    public void run() {
        try {
            BeginConnectAndRecvTh();
            NetDataHub.get().addLog("TcpSocketClient---Looper.prepare()");
            Looper.prepare();
            handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    if(message == null || message.obj == null) return;
                    byte[] msg = (byte[])message.obj;
                    try {
                        if(socket != null) {
                            int nHeadLen = 1+7*4;
                            ByteBuffer buf = ByteBuffer.allocate(nHeadLen+msg.length);
                            byte bImageType = 4;
                            buf.put(bImageType);
                            buf.putInt(0);
                            buf.putInt(0);
                            buf.putInt(0);
                            buf.putInt(0);
                            //----------------
                            buf.put(com.grampus.hualauncherkai.TcpSock.SockTransfer.toLH(msg.length));
                            buf.putInt(0);
                            buf.put(SockTransfer.toLH(m_PackOp.m_nUniqueID));
                            buf.put(msg);


                                m_pack.SendScreenCmd(outputStream,GlobalPara.WM_SEND_IMAGE_DATA,buf.array());
                            //bufferedOutputStream.write(msg);
                            if( EMMApp.getInstance().g_testCount < 6){
                                NetDataHub.get().addLog("sendData------handle--msg.length:"+msg.length);
                            }

                            System.out.println("handleMessage-------WM_SEND_IMAGE_DATA发送字节数为:" + msg.length + "线程ID:" + android.os.Process.myTid());
                        }
                        //sleep(15000);
                    } catch (Exception e) {
                        NetDataHub.get().addLog("TcpSocketClient---------handler error:"+e.toString());
                        e.printStackTrace();
                        close();
                    }
                }
            };
            Looper.loop();
            NetDataHub.get().addLog("TcpSocketClient---------Looper.loop()---end");

        } catch (Exception e) {
            Log.e(TAG, "Socket create failed -----" + e.toString());
            NetDataHub.get().addLog("Socket create failed ---" + e.toString());
            socket = null;
            outputStream = null;
            bufferedOutputStream = null;
        }
    }


    public void close() {
        Log.d("EMMScreen","TCPsocket close");
        NetDataHub.get().addLog("TcpSocketClient---close");
        if (socket != null) {
            try {
                Log.d("EMMScreen","TCPsocket close -----socket.close()");
                socket.close();

            } catch (IOException e) {
                Log.e("EMMScreen","TCPsocket close error-----"+e.toString());
                e.printStackTrace();
            } finally {
                socket = null;
                outputStream = null;
                bufferedOutputStream = null;
            }
        }
        if(ScreenCastService.mScreenCastService!=null)
        {
            Log.w("EMMScreen","mScreenCastService-----!=null");
            ScreenCastService.mScreenCastService.onDestroy();
        }
    }

    public void send(final byte[] data) {
        if(handler == null || socket == null || outputStream == null)  {
            NetDataHub.get().addLog("远程传输-----send-return");
            return;
        }
        if( EMMApp.getInstance().g_testCount < 6){
        //     NetDataHub.get().addLog("sendData--------2");
        }


        try {
           // if(!m_PackOp.m_bStartCapture)
           //     return ;
            //System.out.println("send----------调用发送:" + data.length + "线程ID:" + android.os.Process.myTid());

//            outputStream.write(data);

            try {

                if(data.length>0  ) {

                    m_nFlag ++;

                    Message message = handler.obtainMessage();
                    message.obj = data;
                    handler.sendMessage(message);   //判断发送失败，断开
                }


            } catch (Exception e) {
                NetDataHub.get().addLog("send data error:"+e.toString());
                //e.printStackTrace();
            }
            //----------------end.
    }
        catch(Exception e){
            NetDataHub.get().addLog("send error:"+e.toString());
            //e.printStackTrace();
        }
    }
}
