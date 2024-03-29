package com.grampus.hualauncherkai.amap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.grampus.hualauncherkai.log.LogTrace;

public class LocationService extends Service implements AMapLocationListener
{
    private static final String TAG = "LocationService";

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private String latitude;//纬度
    private String longitude;//经度
    private String country;//国家K
    private String province;//省
    private String city;//市
    private String district;//区
    private String address;//地址
    private String errorMsg;

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        LogTrace.i(TAG, "onBind called.", "------------");
        return new MyBinder();
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        LogTrace.i(TAG, "onCreate called.", "------------");
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        LogTrace.i(TAG, "onDestroy called.", "------------");
        if (null != locationClient)
        {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    public void enableMyLocation()
    {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(false);
        // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
        locationOption.setInterval(5000);

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    public void disableMyLocation()
    {
        // 停止定位
        locationClient.stopLocation();
    }


    /**
     * @author user
     */
    public class MyBinder extends Binder
    {

        /**
         * @param name
         */
        public void location()
        {
            enableMyLocation();
        }
    }


    @Override
    public void onLocationChanged(AMapLocation location)
    {
        /*//add by gwb;2020.9.14  先不用了。
        // TODO Auto-generated method stub
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        String result = AmapLocationUtils.getLocationStr(location);
        LogTrace.i(TAG, "onLocationChanged", "result=" + result);
        if (location.getErrorCode() == 0)
        {
            latitude = location.getLatitude() + "";//纬度
            longitude = location.getLongitude() + "";//经度
            country = location.getCountry();//国家
            province = location.getProvince();//省
            city = location.getCity();//市
            district = location.getDistrict();//区
            address = location.getAddress();//地址
            Intent intent = new Intent();
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("country", country);
            intent.putExtra("province", province);
            intent.putExtra("city", city);
            intent.putExtra("district", district);
            intent.putExtra("address", address);
            intent.setAction("com.grampus.hualauncherkai.action.LOCATION_RECEIVER");
            sendBroadcast(intent);
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
            errorMsg = sb.toString();

        }
         */
    }
}
