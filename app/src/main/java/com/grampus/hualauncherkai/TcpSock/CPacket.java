package com.grampus.hualauncherkai.TcpSock;


import android.util.Log;

import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.UI.MainActivity;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class CPacket {
    public int m_nUniqueID = 0;
    /*typedef struct screen_pack_head
      {
          int		nUniqueID;
          int		nMainCmd;
          int		nDataSize;
      }SCREEN_PACK_HEAD;

      typedef struct  tag_PPackHead
      {
          int		nPackFlag;			//标识
          int		nHeadCmd;			//包头命令
          int   	bIsZipPack;			//是否为压缩包
          int		nUnZipBodySize;		//未压缩包身大小
          int		nBodySize;
      }P_PACKHEAD;

      typedef struct tag_ImgHeader
        {
            BYTE	bImgType;//0:位图 1：JPG
            RECT	rcChange;
            UINT	ImgSize;	//没有压缩的大小
            UINT	ImgZipSize; //压缩后的大小
            UINT	nClientID;
        }IMG_HEAD;
        typedef struct tagRECT
        {
            LONG    left;
            LONG    top;
            LONG    right;
            LONG    bottom;
        } RECT, *PRECT, NEAR *NPRECT, FAR *LPRECT;
*/
    /**
     * 将int转为低字节在前，高字节在后的byte数组
     */
    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            // char c0 = Integer.toHexString(c).charAt(0);
            unicode.append(c);
            char c1 = '\0';
            unicode.append(c1);
        }

        return unicode.toString();
    }

    public boolean SendFirstPack(OutputStream bufferOutputStream){

        try {

            int  PACK_CMD_SEND_FIRST = 1024 +100;
            ByteBuffer buffer = ByteBuffer.allocate(4 * 5);
            int PACK_IDENTIFY = 0x9a3c75b2;
            byte[] PackIdentify = SockTransfer.toLH(PACK_IDENTIFY);

            buffer.put(PackIdentify);
            buffer.put(SockTransfer.toLH(PACK_CMD_SEND_FIRST));
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            //-------------------------------------

            NetDataHub.get().addLog("CPakcet-------SendFirstPack----");
            bufferOutputStream.write(buffer.array());
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将标识、机器id、命令各种相关和pData组合一下写入bufferOutputStream
     * @param bufferOutputStream
     * @param nCmd
     * @param pData
     * @return
     */
    public boolean SendScreenCmd(OutputStream bufferOutputStream, int nCmd, byte[] pData){

        try {
            int nDataLen = pData.length;

            int nBodySize = 3 * 4 + nDataLen;

            int PACK_IDENTIFY = 0x9a3c75b2;
            int PACK_CMD_SEND_DATA = 1024 + 102;

            ByteBuffer sendBuf = ByteBuffer.allocate(nBodySize+5*4);
            sendBuf.put(SockTransfer.toLH(PACK_IDENTIFY));
            sendBuf.put(SockTransfer.toLH(PACK_CMD_SEND_DATA));
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.put(SockTransfer.toLH(nBodySize));

            //------------------------------------
            sendBuf.put(SockTransfer.toLH(m_nUniqueID));
            sendBuf.put(SockTransfer.toLH(nCmd));
            sendBuf.put(SockTransfer.toLH(nDataLen));

            if (pData.length > 0)
                sendBuf.put(pData);

            bufferOutputStream.write(sendBuf.array());
        //    if( EMMApp.getInstance().getInstatnce().g_testCount < 6) {
        //        if (NetDataHub.get() != null)
        //            NetDataHub.get().addLog("SendCmd---nDataLen:" + nDataLen + "|cmd:"+PACK_CMD_SEND_DATA);
        //    }
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * LogonInfo中携带IP、宽高等数据，拼接后再调用SendScreenCmd进一步整合
     * bufferOutputStream为最终流
     * @param bufferOutputStream
     * @param szIP
     * @param szMac
     * @param szHostName
     * @param nUniqueID
     * @param nWidth
     * @param nHeight
     * @return
     */
    public boolean SendLogonInfo(OutputStream bufferOutputStream, String szIP, String szMac, String szHostName, int nUniqueID, int nWidth, int nHeight ){
        /*
        typedef struct tag_LogonInfo
        {
            char	cClientIP[25];//测试时有后缀，此时会越界
            char	cHostName[50];
            char	cMacAddr[25];
            UINT	nViewType;
            UINT	nClientID;
            UINT	nScreenHeight;
            UINT	nScreenWidth;
        }LOGON_INFO1;*/

        try {
            int nDataLen = 25+50+25+4*4;

            ByteBuffer buffer = ByteBuffer.allocate(nDataLen);

            System.arraycopy(szIP.getBytes(),0,buffer.array(),0,szIP.length());
            System.arraycopy(szHostName.getBytes(),0,buffer.array(),25,szHostName.length());
            System.arraycopy(szMac.getBytes(),0,buffer.array(),75,szMac.length());
            buffer.position(100);
            buffer.putInt(0);
            buffer.put(SockTransfer.toLH(nUniqueID));
            buffer.put(SockTransfer.toLH(nWidth));
            buffer.put(SockTransfer.toLH(nHeight));

            int WM_CLIENT_EQUIPID_LOGON = 1024 + 5039;
            SendScreenCmd(bufferOutputStream,WM_CLIENT_EQUIPID_LOGON,buffer.array());

            //-----------------------------
        /*
            typedef struct tag_MultiScreen
            {
                int		nCmd;//0:登陆通知     1：主副切换通知
                int		nIsMultiScreen;//是否为双屏
                int		bCurShowSecond;//0:显示主屏   1:显示副屏
                RECT	rcFirst;
                RECT	rcSecond;
            }MULTI_SCREEN_PARA;*/

            //    m_MultiScreenPara.nCmd = 0;
            //    pSockSink->SendScreenCmd(WM_MULTI_SCREEN_PARA,(BYTE*)&m_MultiScreenPara,sizeof(m_MultiScreenPara));

            //    int nVersion = 20210701;
            //    pSockSink->SendScreenCmd(WM_CLIENT_SOFT_VERSION,(BYTE*)&nVersion,sizeof(nVersion));

            int WM_MULTI_SCREEN_PARA = 1024 + 5067;
            nDataLen = (3+4+4)*4;
            buffer = ByteBuffer.allocate(nDataLen);
            buffer.putInt(0);
            //buffer.put(SockTransfer.toLH(111));
            buffer.put(SockTransfer.toLH(100));
            buffer.putInt(0);

            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);

            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            SendScreenCmd(bufferOutputStream,WM_MULTI_SCREEN_PARA,buffer.array());

            int WM_CLIENT_SOFT_VERSION = 1024 + 5086;
            nDataLen = 4;
            buffer = ByteBuffer.allocate(nDataLen);
            buffer.put(SockTransfer.toLH(20210730));
            SendScreenCmd(bufferOutputStream,WM_CLIENT_SOFT_VERSION,buffer.array());

        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean SendLogonToCenterServ(OutputStream bufferOutputStream, String szIP, String szMac, String szDiskNum, String szHostName ){
        /*
        typedef struct tag_CLIENTLOGON2//客户端登陆结构
        {
            DWORD dwCommand;
            int  nClientType;
            char cGUID[50];//这个标识来确认是不是同一个客户端。
            char cVersion[20];//版本号
            WCHAR cMachineName[50];
            char cIP[20];
            char cMac[30];
            WCHAR cDiskNum[50];
            WCHAR cUniqueNum[50];
            int	 bFileEncrypt;//是否安装有加密
            char cDeskPolicyMd5[35];//桌管策略md5
            char cEncryptPolicyMd5[35];//加密策略md5
            DWORD   dwFlag;
            int		cBufSize;//尾部跟的数据块大小。
            WCHAR	cBuf[0];
        }CLIENTLOGON2;*/

        try {
            int nDataLen = 8+50+20+50*2+20+30+50*2+50*2+ 4+35+35+8;

            int nCommand = 20140103;//固定的

            ByteBuffer buffer = ByteBuffer.allocate(nDataLen);
            buffer.put(SockTransfer.toLH(nCommand));
            buffer.put(SockTransfer.toLH(3));//nClientType=3  代码安卓端socket连接

            String szVersion = MainActivity.szVersionNum;
            Log.w("EMMMainPacket","szVersion---"+szVersion);
            System.arraycopy(szVersion.getBytes(),0,buffer.array(),58,szVersion.getBytes().length);

//            String szWHostName = string2Unicode(szHostName);//好像有错误
//            System.arraycopy(szWHostName.getBytes(),0,buffer.array(),78,szWHostName.getBytes().length);

            //mod by fsy 2022.2.9 szHostName中时可以存在中文字符的，发送长度用字节长度
            System.arraycopy(szHostName.getBytes(),0,buffer.array(),78,szHostName.getBytes().length);
            //Log.w("EMM","szHostName:"+szHostName+"|"+szHostName.getBytes().length+"|");

            System.arraycopy(szIP.getBytes(),0,buffer.array(),178,szIP.length());
            System.arraycopy(szMac.getBytes(),0,buffer.array(),198,szMac.length());

            String szWDiskNum = string2Unicode(szDiskNum);
            String szUniqueNum = szWDiskNum;
            System.arraycopy(szDiskNum.getBytes(),0,buffer.array(),228,szDiskNum.getBytes().length);//.getBytes()
            System.arraycopy(szUniqueNum.getBytes(),0,buffer.array(),328,szUniqueNum.getBytes().length);
            Log.i("EMMCmd","m_nuID+"+m_nUniqueID+"szIP+"+szIP+"szMac+"+szMac+"|szDiskNum+"+szDiskNum+"szHostName+"+szHostName);
            int WM_2021_CLIENT_LOGON_CMD = 1024 + 1999;
            SendDataToCenterServ(bufferOutputStream,WM_2021_CLIENT_LOGON_CMD,buffer.array());
        }
        catch(Exception e){
            Log.e("EMM","SendLogonToCenterServ error:"+e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean SendCmdToCenterServ(OutputStream bufferOutputStream,int nCommand)
    {
        try {
            int nDataLen = 1;

            int nPackSize = 17 * 4 + nDataLen;

            int PACK_IDENTIFY = 0x236511cc;
            int PACK_CMD_SEND_DATA = 1024 + 102;

            ByteBuffer sendBuf = ByteBuffer.allocate(nPackSize);
            sendBuf.put(SockTransfer.toLH(nCommand));
            sendBuf.putInt(0);
            sendBuf.put(SockTransfer.toLH(PACK_IDENTIFY));
            sendBuf.putInt(0);

            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.putInt(0);

            int nValue = 1;
            sendBuf.put(SockTransfer.toLH(nValue));
            sendBuf.put(SockTransfer.toLH(nValue));
            sendBuf.put(SockTransfer.toLH(nPackSize));
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            //------------------------------------
            sendBuf.putInt(0);
            sendBuf.putInt(0);


            bufferOutputStream.write(sendBuf.array());
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * 发送给中转服务器的包，add by fsy
     * @param bufferOutputStream
     * @param nCommand
     * @param str
     * @return
     */
    public boolean SendFirseDataToSwitchServ(OutputStream bufferOutputStream, int nCommand,String str ){

        try {

            String szCmdStr = string2Unicode(str);

            if (szCmdStr.length() < 0)
                return false;

            int nDataLen =  szCmdStr.length();

            int nBodySize = 3 * 4 + nDataLen;

            int PACK_IDENTIFY = 0x9a3c75b2;
            int PACK_CMD_SEND_DATA = 1024 + 102;

            ByteBuffer sendBuf = ByteBuffer.allocate(nBodySize+5*4);
            sendBuf.put(SockTransfer.toLH(PACK_IDENTIFY));
            sendBuf.put(SockTransfer.toLH(PACK_CMD_SEND_DATA));
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.put(SockTransfer.toLH(nBodySize));

            //------------------------------------
            sendBuf.put(SockTransfer.toLH(m_nUniqueID));
            sendBuf.put(SockTransfer.toLH(nCommand));
            sendBuf.put(SockTransfer.toLH(nDataLen));

            sendBuf.put(szCmdStr.getBytes());

            bufferOutputStream.write(sendBuf.array());


        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean SendDataToCenterServ(OutputStream bufferOutputStream,int nCommand,byte[] pData)
    {
        /*
        #define PACK_FLAG  0x236511cc
        typedef struct  tag_PackHead
        {
            UINT nCommand;
            UINT nPassThrough;
            UINT nFlag;
            UINT nReserved;

            SYSTEMTIME  StFile;//文件时间   4个DWORD
            FILETIME ftFile;   2个DWORD

            UINT nPackCount;
            UINT nPackIndex;
            UINT nPackSize;
            UINT nPackMask;
            UINT nFileSize;

            UINT nManageID;//这个字段作为管理机ID，标识是发给哪个管理机的
            UINT nClientSock;
            BYTE pData[1];
        }PACKHEAD;

        int PackLen = sizeof(PACKHEAD) - 1 + nLen;
        PACKHEAD * pHead =(PACKHEAD *) new BYTE[PackLen];
        memset(pHead,0,PackLen);
        pHead->nPackCount = 1;
        pHead->nPackIndex = 1;
        pHead->nPackSize = PackLen;
        pHead->nCommand = nCommand;
        memcpy(pHead->pData,pData,nLen);
        int nRet = Send(pHead,pHead->nPackSize);
        delete pHead;
        return nRet;
         */


        try {
            int nDataLen = pData.length;

            int nPackSize = 17 * 4 + nDataLen;

            int PACK_IDENTIFY = 0x236511cc;
            int PACK_CMD_SEND_DATA = 1024 + 102;

            ByteBuffer sendBuf = ByteBuffer.allocate(nPackSize);
            sendBuf.put(SockTransfer.toLH(nCommand));
            sendBuf.putInt(0);
            sendBuf.put(SockTransfer.toLH(PACK_IDENTIFY));
            sendBuf.putInt(0);

            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            sendBuf.putInt(0);

            int nValue = 1;
            sendBuf.put(SockTransfer.toLH(nValue));
            sendBuf.put(SockTransfer.toLH(nValue));
            sendBuf.put(SockTransfer.toLH(nPackSize));
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            //------------------------------------
            sendBuf.putInt(0);
            sendBuf.putInt(0);
            if (pData.length > 0)
                sendBuf.put(pData);

            bufferOutputStream.write(sendBuf.array());
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
