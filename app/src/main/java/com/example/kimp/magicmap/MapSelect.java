package com.example.kimp.magicmap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapSelect extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap; //Google Maps元件
    Button btn_nextStep;
    double nowlat, nowlng;
    LatLng latLng; //座標經緯度
    String sNowLat, sNowLng, lat = "", lng = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_select);

        //取得Map的資訊
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        //事件返回對象
        mapFragment.getMapAsync(this);

        findV();
        setClickListener(); //按鈕點擊事件監聽
    }

    //實作OnMapReadyCallback的方法
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //取得目前的位置，並將畫面移至目前位置
        getLoactionNow();
        //Move camera to current location
    }

    private void findV() {
        btn_nextStep = (Button) findViewById(R.id.btn_next);
    }

    private void setClickListener() {
        btn_nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果沒有點選Map的任何一點 lat, lng 長度就會是0
                if (lat.length() == 0 && lng.length() == 0) {
                    Toast.makeText(MapSelect.this,
                            R.string.mapSelect_txv_wrong,
                            Toast.LENGTH_SHORT)
                            .show();
                }
                //如果有lat, lng 就跳到ActivityTitle, 輸入活動的詳細資訊
                else {
                    // TODO: 跳至Activity_Title.java
                }
            }
        });
    }

    private void getLoactionNow() {
        //宣告LocationManger
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //取得最好的定位裝置
        String bestProvider = locationManager.getBestProvider(new Criteria(), true);
        //AndroidManifest.xml 定位權限判斷
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //取得目前位置
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            //將目前位置(double)轉成字串
            nowlat = location.getLatitude();
            nowlng = location.getLongitude();
            sNowLat = String.valueOf(nowlat);
            sNowLng = String.valueOf(nowlng);
            latLng = new LatLng(nowlat, nowlng);

            //將畫面移到目前位置
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            //在目前位置打點
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Position"));
        } else {
            latLng = new LatLng(22.764618, 120.375446);
            //將畫面移到目前位置
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            Toast.makeText(MapSelect.this, "找不到座標", Toast.LENGTH_SHORT).show();
        }
        //地圖點擊的監聽
        mMap.setOnMapClickListener(this);

    }

    //實作GoogleMap.OnMapClickListener的方法
    @Override
    public void onMapClick(LatLng point) {
        //先清空原有的Marker(標記點、氣球)
        mMap.clear();
        //Marker的詳細資訊
        MarkerOptions cliclMarket = new MarkerOptions()
                .position(point) //設定Marker的位置
                .title(point.toString()); //設定Marker的詳細資訊
        //加入Marker
        mMap.addMarker(cliclMarket);
        //將點擊的位置放入lat, lng 方便btn_step判斷是否有選擇活動地點
        lat = String.valueOf(point.latitude);
        lng = String.valueOf(point.longitude);
    }


}
