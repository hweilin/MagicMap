package com.example.kimp.magicmap;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by user2 on 2015/9/29.
 */
public class LocationService extends Service {
    private Location locationInfo;
    static String TAG = "LocationService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), true);
        //初始化 locationManager 並取得最佳定位提供者
        Log.e(TAG, "Service start OK");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.e(TAG, "Permission OK");
        locationManager.requestLocationUpdates(bestProvider, 10000, 1,
                locationListener);
        //定位更新(bestProvider ,時間間隔1000ms ,距離間隔1m ,監聽物件)

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            locationInfo = location;
            if (location != null) {

                Thread theard = new Thread() {
                    public void run() {
                        Model model = new Model();
                        model.setLocation(location.getLatitude(), location.getLongitude(), setLocationHandler);
                        Log.e(TAG, location.getLatitude() + ", " + location.getLongitude());

                    }
                };
                theard.start();

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private Handler setLocationHandler = new Handler() {
        public void handleMessage(Message msg) {

        }
    };

}
