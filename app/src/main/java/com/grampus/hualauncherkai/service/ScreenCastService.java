package com.grampus.hualauncherkai.service;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.grampus.hualauncherkai.EMMApp;
import com.grampus.hualauncherkai.Data.NetDataHub;
import com.grampus.hualauncherkai.TcpSock.TcpSocketClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static com.grampus.hualauncherkai.R.mipmap.ic_launcher;

/**
 * Created by sih on 2017-05-31.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class ScreenCastService extends Service {

    private static final int FPS = 1;  //帧率

    private final String TAG = "EMMScreenCastService";

    private MediaProjectionManager mediaProjectionManager;

    private TcpSocketClient tcpSocketClient;

    private MediaProjection mediaProjection;
    private Surface inputSurface;
    private VirtualDisplay virtualDisplay;
    //private MediaCodec.BufferInfo videoBufferInfo;
    private MediaCodec encoder;

    private InetAddress remoteHost;
    private int remotePort;

    public static ScreenCastService mScreenCastService;

/*
    private ScreenCastService (){}
    public static ScreenCastService getInstance() {
        return mScreenCastService;
    }
*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * @author  fsy
     * @description  设置应用服务未前台，前台通知展示，保证服务不会被系统回收
     */
    private void setForeground() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("screen_cast_service",
                        "阳途移动终端安全管理", NotificationManager.IMPORTANCE_LOW);
                NotificationManager manager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
            }
            Notification notification =
                    new NotificationCompat.Builder(this, "screen_cast_service")
                            .setContentTitle("阳途移动终端安全管理")
                            .setContentText("正在运行中")
                            .setSmallIcon(ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), ic_launcher))
                            .build();
            startForeground(12, notification);
        }catch (Exception e){
            Log.i(TAG, "setForeground--error "+e.toString());
        }
    }

    /**
     * 锁屏下点亮屏幕
     */
    @SuppressLint("InvalidWakeLockTag")
    private void wakeAndUnlock()
    {
        try{
            PowerManager pm;
            PowerManager.WakeLock wl;

            pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
            //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            //点亮屏幕
            wl.acquire();
        }catch (Exception e){
            Log.i(TAG, "wakeAndUnlock--error "+e.toString());
        }
    }
    /**
     * 30秒唤醒一次屏幕
     */
    public void wakeOnTime() {
        try{
            while(true){

                android.app.KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

                Log.i("EMMScreen", "wakeAndUnlock----检测是否黑屏-------");
                if(mKeyguardManager.isKeyguardLocked())
                {
                    Log.i("EMMScreen", "wakeAndUnlock----黑屏点亮屏幕-------");
                    wakeAndUnlock();

                    KeyguardManager.KeyguardLock kl = mKeyguardManager.newKeyguardLock("unLock");
                    kl.disableKeyguard();
                }

                Thread.sleep(30000);
                if(mScreenCastService==null)
                {
                    break;
                }

            }
        }catch (Exception e){
            Log.i("EMMScreen", "wakeAndUnlock--error "+e.toString());
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        NetDataHub.get().addLog("ScreenCastService-----Screen Service--onCreate");
        Log.d("EMMScreen", "Screen Service--onCreate");
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        System.out.println("onCreate----------begin  threadid:"+android.os.Process.myTid());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NetDataHub.get().addLog("ScreenCastService---Screen Service---onDestroy");
        Log.d("EMMScreen", "Screen Service--onDestroy");
        stopScreenCapture();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        try{
            NetDataHub.get().addLog("ScreenCastService---onStartCommand---begin");
            final String remoteHost = EMMApp.getInstance().serverIp;
           // remotePort = 6671;
            try {
                this.remoteHost = InetAddress.getByName(remoteHost);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return START_NOT_STICKY;
            }

            DisplayMetrics dm =new DisplayMetrics();
            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getRealMetrics(dm);

            float density = dm.density;
            float scaledDensity = dm.scaledDensity;
            int nScreenDpi = dm.densityDpi;//dm.densityDpi;
            Log.i("EMMScreen",  "widthPixels,heightPixels,density,scaledDensity,densityDpi：" +  dm.widthPixels+ "," + dm.heightPixels+ "," + density + "," + scaledDensity+ "," +nScreenDpi  );

            int nWidth = (int)(dm.widthPixels/dm.density);    //432   360
            int nHeight = (int)( dm.heightPixels/dm.density); // 872   780

//*录制最大分辨率为960
            int nMaxPixels = 960;
            if(nHeight > nWidth)
            {
                NetDataHub.get().addLog("nWidth--竖屏" );
                if(nHeight > nMaxPixels)
                {
                    nHeight = nMaxPixels;
                    density = (float)dm.heightPixels /(float)nMaxPixels;    //过大的进行缩小
                    nWidth = (int) (dm.widthPixels / density);
                //    nScreenDpi = (int)(160*density);
                    NetDataHub.get().addLog("nHeight调整 到" + nMaxPixels + " ，nWidth为 " + nWidth );
                }
            }else
            {
                NetDataHub.get().addLog("nWidth--横屏" );
                if(nWidth > nMaxPixels)
                {
                    nWidth = nMaxPixels;
                    density = (float)dm.widthPixels /(float)nMaxPixels;    //过大的进行缩小
                    nHeight = (int)(dm.heightPixels / density);
                    NetDataHub.get().addLog("nWidth 到 "+ nMaxPixels + " ，nHeight " + nHeight );
                }
            }
//*/
            if(nWidth %2 == 1){
                nWidth--;
                NetDataHub.get().addLog("得到分辨率宽为奇数，减一调整 " + nWidth );
            }
            if(nHeight %2 == 1){
                nHeight--;
                NetDataHub.get().addLog("得到分辨率高为奇数，减一调整 " + nHeight );
            }

            final  int screenDpi = nScreenDpi;//dm.densityDpi;
            final int screenWidth = nWidth;    //432   360
            final int screenHeight = nHeight;

            NetDataHub.get().addLog("widthPixels " +  dm.widthPixels+ ",heightPixels " + dm.heightPixels+ ",density " + density + ",scaledDensity " + scaledDensity);
            NetDataHub.get().addLog("screenWidth " + screenWidth+ ",screenHeight " +screenHeight+",screenDpi "+screenDpi );

            final String format = "video/avc";
            final int bitrate = 20*1024*1000;//各设备大小分辨率差距极大，最好应该分档次

            EMMApp.getInstance().screeenWidth = screenWidth ;
            EMMApp.getInstance().screenHight = screenHeight ;
            EMMApp.getInstance().density = density;
            EMMApp.getInstance().densityDpi = screenDpi ;

            NetDataHub.get().addLog("ScreenCastService---onStartCommand---a");
            Log.w("EMMScreen", "Start casting with format:" + format + ", screen:" + screenWidth +"x"+screenHeight +" @ " + screenDpi + " bitrate:" + bitrate);
             if (!createSocket()) {
                Log.e("EMMScreen", "Failed to connect tcp:" + remoteHost + ":" + remotePort);
                return START_NOT_STICKY;

            }

            final int resultCode = -1;
            final Intent resultData = intent.getParcelableExtra("RESULT_DATA");
            Log.i("EMMScreen", "resultCode: " + resultCode + " resultData:" + resultData+ " resultData.ts:" +"RESULT_DATA");

            if (resultCode == 0 || resultData == null) {
                return  START_NOT_STICKY;
            }

            mScreenCastService = this;
            new Thread(new Runnable(){

                @Override
                public void run() {
                    startScreenCapture(resultCode, resultData, format, screenWidth, screenHeight, screenDpi, bitrate);
                }
            }).start();

           // startScreenCapture(format, screenWidth, screenHeight, screenDpi, bitrate);

            NetDataHub.get().addLog("ScreenCastService---nStartCommand---b");
            new Thread(new Runnable(){

                @Override
                public void run() {
                    wakeOnTime();
                }
            }).start();
            return START_STICKY;
        }catch(Exception e){
            NetDataHub.get().addLog("ScreenCastService----onStartCommand--Error:"+e.toString());
            return  START_NOT_STICKY;
        }
        //return START_REDELIVER_INTENT;
    }

    private void startScreenCapture(int resultCode, Intent resultData, String format, int width,int height, int dpi, int bitrate) {

        NetDataHub.get().addLog("ScreenCastService---start---开始屏幕监视");
        //MediaPlayer MediaPlayer = new MediaPlayer();

        setForeground();
        try {
            int i = 0;
            while(!(tcpSocketClient.isConnectOK))
            {
                Log.i("EMMScreen", " width:"+width+" height:"+height+"dpi:"+dpi+"-------slepp 10");
                NetDataHub.get().addLog(" width:"+width+" height:"+height+"dpi:"+dpi+"-------slepp 10");
                Thread.sleep(10);
                if(i++>500) {
                    stopScreenCapture();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            NetDataHub.get().addLog("ScreenCastService---start while() error:"+ e.toString());
        }

   //     Log.i("EMMScreen", " width:"+width+" height:"+height+" dpi:"+dpi);
        try {
            this.mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData);
        }catch (Exception e){
            NetDataHub.get().addLog("ScreenCastService---start error:"+ e.toString());
            Log.e( "OnReceivePack", e.toString());
        }
        //this.videoBufferInfo = new MediaCodec.BufferInfo();
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(format, width, height);

        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 0);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 50);  // 配置GOP大小

        //mediaFormat.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 40);画面禁止一段时间后发送FRAME
        //BITRATE_MODE_CQ尽量保持速度，还不考虑图像质量
        mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);

        try {
            // AVC
            NetDataHub.get().addLog("ScreenCastService---start---mediaFormat设置参数");
            Log.d( "OnReceivePack", "MIMETYPE_VIDEO_AVC.....");
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);

            this.encoder = MediaCodec.createEncoderByType(format);

            this.encoder.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(MediaCodec codec, int inputBufferId) {

                    if( EMMApp.getInstance().g_testCount < 6){
                        NetDataHub.get().addLog("ScreenCastService---InputAvailable");
                    }
                }

                //Calendar.getInstance().getTimeInMillis();计算效率不如 System.currentTimeMillis()；
               // long curTime = System.currentTimeMillis() - 30;//System.nanoTime();获取纳秒级
                @Override
                public void onOutputBufferAvailable(MediaCodec codec, int outputBufferId, MediaCodec.BufferInfo info) {
                    try {

                        /*
                        if( EMMApp.getInstatnce().g_testCount == 50000 ) {
                            mediaFormat.setInteger(MediaFormat.KEY_WIDTH, height);
                            mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, width);

                            encoder.reset();
                            encoder.configure(mediaFormat
                                    , null // surface
                                , null // crypto
                                , MediaCodec.CONFIGURE_FLAG_ENCODE);
                        }*/
                        ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferId);

                        if( EMMApp.getInstance().g_testCount < 6) {
                            NetDataHub.get().addLog("ScreenCastService---OutputAvailable--"+EMMApp.getInstance().g_testCount);
                            EMMApp.getInstance().g_testCount++;
                        }
                        if (info.size > 0 && outputBuffer != null) {
                            //int nBufSize = outputBuffer.remaining();        //返回剩余的可用长度，此长度为实际读取的数据长度，最大自然是底层数组的长度
                            outputBuffer.position(info.offset);
                            outputBuffer.limit(info.offset + info.size);
                            byte[] b = new byte[outputBuffer.remaining()];
                            outputBuffer.get(b);        //读取缓冲区当前位置的值,然后递增，返回当前缓冲区位置上的值

                            sendData(null, b);

                            //自定义速度，30ms以上一张
                   //         while( System.currentTimeMillis() - curTime < 35)
                    //        {
                    //            Thread.sleep(4);
                    //        }

                     //       curTime = System.currentTimeMillis();
                        }
                        if (encoder != null && outputBufferId > 0) {
                            encoder.releaseOutputBuffer(outputBufferId, false);

                        }
                      /*
                       if(videoBufferInfo!=null)
                        {
                            if ((videoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                Log.i("OnReceivePack", "End of Stream");
                                stopScreenCapture();
                            }
                        }
                        */
                    }catch(Exception e){
                        Log.e("EMMScreen", "onOutputBufferAvailable--error-"+e.toString());
                        //mScreenCastService.onDestroy();
                        stopScreenCapture();
                    }
                }

                @Override
                public void onError(MediaCodec codec, MediaCodec.CodecException e) {
                    e.printStackTrace();
                }

                @Override
                public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
                    Log.i(TAG, "onOutputFormatChanged. CodecInfo:" + codec.getCodecInfo().toString() + " MediaFormat:" + format.toString());
                }
            });
            this.encoder.configure(mediaFormat
                                    , null // surface
                                    , null // crypto
                                    , MediaCodec.CONFIGURE_FLAG_ENCODE);

            this.inputSurface = this.encoder.createInputSurface();
            this.encoder.start();
            NetDataHub.get().addLog("ScreenCastService---------encoder.start()");
        } catch (Exception e) {
            NetDataHub.get().addLog("ScreenCastService---start error:"+e.toString());
            //Log.e(TAG, "Failed to initial encoder, e: " + e);
            releaseEncoders();
        }

        try{
            this.virtualDisplay = this.mediaProjection.createVirtualDisplay("Recording Display", width, height, dpi, 0, this.inputSurface, null, null);
        }catch (Exception e)
        {
            NetDataHub.get().addLog("ScreenCastService---virtualDisplay error:"+e.toString());
        }
   }

    private void sendData(byte[] header, byte[] data) {

        if(tcpSocketClient != null) {
            if(header != null) {
                tcpSocketClient.send(header);
            }
            tcpSocketClient.send(data);

            if( EMMApp.getInstance().g_testCount < 6) {
            //    NetDataHub.get().addLog("sendData--------1");
            }
        }
        else{
            NetDataHub.get().addLog("远程传输失败，关闭远程连接");
            stopScreenCapture();
        }
    }

    private void stopScreenCapture() {
        try{
            releaseEncoders();

            EMMApp.getInstance().startCapture = false;
            if(mScreenCastService!=null)
            {
                NetDataHub.get().addLog("ScreenCastService---退出屏幕录制");
                mScreenCastService = null;
            }
            closeSocket();
            stopForeground(true);
            if (virtualDisplay == null) {
                return;
            }
            virtualDisplay.release();
            virtualDisplay = null;
        }catch (Exception e){
            Log.i("EMMScreen", "stopForeground---error "+e.toString());
            NetDataHub.get().addLog("ScreenCastService---stop error"+e.toString());
        }
    }

    private void releaseEncoders() {    //mod by fsy 2022.4.11分开try catch,前面的报错导致mediaProjection没退出。
        try{
            if (encoder != null) {
                encoder.stop();
                encoder.release();
                encoder = null;
            }
        }catch (Exception e){
            Log.i("EMMScreen", "releaseEncoders--error "+e.toString());
            NetDataHub.get().addLog("ScreenCastService---releaseEncoders encoder stop error:"+e.toString());
        }
        try{
            if (inputSurface != null) {
                inputSurface.release();
                inputSurface = null;
            }
        }catch (Exception e){
            Log.i("EMMScreen", "releaseEncoders--error "+e.toString());
            NetDataHub.get().addLog("ScreenCastService---releaseEncoders inputSurface release error"+e.toString());
        }

        try{
            if (mediaProjection != null) {
                mediaProjection.stop();
                mediaProjection = null;
            }
        }catch (Exception e){
            Log.i("EMMScreen", "releaseEncoders--error "+e.toString());
            NetDataHub.get().addLog("ScreenCastService---releaseEncoders mediaProjection stop error"+e.toString());
        }
        //videoBufferInfo = null;
    }

    private boolean createSocket() {
        tcpSocketClient = new TcpSocketClient(remoteHost, remotePort);
        tcpSocketClient.start();
        NetDataHub.get().addLog("ScreenCastService---createSocket");
        return true;
    }

    private void closeSocket() {
        if(tcpSocketClient != null) {
            Log.w("EMMScreen", "关闭远程连接");
            try {
                tcpSocketClient.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                tcpSocketClient = null;
            }
        }
    }

}
