package com.example.kimp.magicmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Model {
    static String TAG = "Model";

    //藉由此方法將參數傳到另一個副執行緒執行
    private void connectService(String serviceName, Handler handler) {
        connectService(serviceName, new HashMap(), handler);
    }

    //執行副執行緒
    private void connectService(String serviceName, HashMap parameter, Handler handler) {
        Thread runService = new Thread(new RunService(serviceName, parameter, handler));
        runService.start();
    }

    /*底下每一個方法代表使用不同的php方法，放入參數也會因為php所需參數不同有所不一樣，統一傳到
    connectService去執行副執行緒*/

    //登入(使用者暱稱,Handler)
    protected void loginModel(String name, Handler handler) {
        HashMap hMap = new HashMap();
        hMap.put("simid", value.simid);
        hMap.put("name", name);
        connectService("login", hMap, handler);
    }

    //取得活所參與的活動(Handler)
    protected void getActivityModel(Handler handler) {
        HashMap hMap = new HashMap();
        hMap.put("simid", value.simid);
        connectService("myActivities", hMap, handler);
        Log.e(TAG + "myActivityies", value.simid);
    }

    //取得所有使用者的資訊(Handler)
    protected void userListModel(Handler handler) {
        connectService("user", handler);
    }

    //更新目前的位置(目前緯度,活動經度,Handler)
    protected void setLocation(double lat, double lng, Handler handler) {
        HashMap hMap = new HashMap();
        hMap.put("simid", value.simid);
        hMap.put("lat", lat);
        hMap.put("lng", lng);
        Log.e(TAG + "updateMyLocation", value.simid + lat + ", " + lng);
        connectService("updateMylocation", hMap, handler);
    }

    //新增新的活動(活動名稱,活動緯度,活動經度,活動好友名單,Handler)
    protected void addNewActivity(String name, String lat, String lng, String friends, Handler handler) {
        HashMap hMap = new HashMap();
        hMap.put("title", name);
        hMap.put("lat", lat);
        hMap.put("lng", lng);
        hMap.put("friend", friends);
        connectService("addActivities", hMap, handler);
        Log.e(TAG + "addActivities", value.simid + "LatLng:" + lat + ", " + lng);
    }

    //刪除目前所參與活動(活動id,Handler)
    protected void deleteActivity(String aid, Handler handler) {
        HashMap hMap = new HashMap();
        hMap.put("aid", aid);
        hMap.put("simid", value.simid);
        connectService("deleteActivities", hMap, handler);
        Log.e(TAG + "deleteActivities", "Delete");
    }

    //取得活動相關資訊(活動id,Handler)
    protected void setMapMarker(String aid, Handler handler) {
        HashMap hMap = new HashMap();
        hMap.put("aid", aid);
        connectService("aboutActivity", hMap, handler);
    }

}

//執行緒
class RunService implements Runnable {
    String serviceName;
    HashMap parameter;
    Handler handler;

    //設定傳入參數值
    public RunService(String serviceName, HashMap parameter, Handler handler) {
        this.serviceName = serviceName;
        this.parameter = parameter;
        this.handler = handler;
    }

    @Override
    public void run() {
        //Please open network permission
        String paramContent = "";
        int runNum = 0;

        for (Object param : parameter.keySet()) {
            paramContent += (param + "=" + parameter.get(param));
            if (runNum != parameter.size() - 1) {
                paramContent += "&";
            }
            runNum++;
        }

        //get的網址
        String url = value.service + serviceName + ".php?" + paramContent;
        Log.e("Model", "url: " + url);
        //使用Connection方法傳入get網址
        Connection(url);
    }

    //連線
    public String Connection(String s) {
        String result = "";
        try {
            //建立URL連線
            URL url = new URL(s);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //取得回傳值代碼
            int code = urlConnection.getResponseCode();
            //如果回傳值代碼是200，將回傳結果使用InputStream解析
            if (code == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        /* 將回傳結果回傳至Handler
        (因為handler需要使用Message放入值，先將回傳結果放入Bundle後再放入Message) */
        Bundle countBundle = new Bundle();
        countBundle.putString("result", result);
        Message msg = new Message();
        msg.setData(countBundle);
        handler.sendMessage(msg);

        Log.e("Model", "Return: " + result);
        return result;
    }


}
