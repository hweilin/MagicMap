package com.example.kimp.magicmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static String TAG = "MapActivity"; //Log的TAG
    private GoogleMap mMap; //Google Maps元件
    String aid=""; //活動ID
    LatLng latLng; //座標的經緯度

    double nowlat, nowlng;
    String sNowLat, sNowLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        getValue(); //取得上一頁傳來的活動ID(aid)
        //取得Map的資訊
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //實作OnMapReadyCallback的方法
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //設定活動的位置和好友的位置
        setMarker();
    }

    //取得上一頁活動的ID
    private void getValue(){
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        aid = bundle.getString("aid");
    }

    //使用Model的setMapMarker得到活動的資訊
    private void setMarker(){
        Model model = new Model();
        model.setMapMarker(aid, getActivityInfo);
    }


    private Handler getActivityInfo = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("result");
            Log.e(TAG, "MapActivity Result: " + result);
            //把活動的資訊做打點
            getMarker(result);
        }
    };

    private void getMarker(String result){
        //解析活動的資訊
        try{
            JSONArray ja = new JSONArray(result);
            JSONObject jo;
            for (int i=0; i<ja.length(); i++){
                //最後一筆為活動的資訊，將活動的資訊做打點
                if (i==ja.length()-1){
                    jo = new JSONObject(ja.get(i).toString());

                    //將目前位置(double)轉成字串
                    sNowLat = jo.getString("lat");
                    sNowLng = jo.getString("lng");
                    nowlat = Double.parseDouble(sNowLat);
                    nowlng = Double.parseDouble(sNowLng);
                    latLng = new LatLng(nowlat, nowlng);

                    //將畫面移到目前位置
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                    String title = jo.getString("name");

                    //Marker的詳細資訊
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)//設定Marker的位置
                            .title(title);//設定Marker的詳細資訊
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));//設定Marker的顏色
                    //加入Marker
                    mMap.addMarker(markerOptions);
                }
                //將好友資訊做打點
                else {
                    jo = new JSONObject(ja.get(i).toString());

                    //將目前位置(double)轉成字串
                    sNowLat = jo.getString("lat");
                    sNowLng = jo.getString("lng");
                    nowlat = Double.parseDouble(sNowLat);
                    nowlng = Double.parseDouble(sNowLng);
                    LatLng latLng = new LatLng(nowlat, nowlng);


                    String name = jo.getString("name");
                    Log.e(TAG, name + ": " + nowlat + "," + nowlng);
                    //Marker的詳細資訊
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)//設定Marker的位置
                            .title(name);//設定Marker的詳細資訊
                    //加入Marker
                    mMap.addMarker(markerOptions);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
