package com.example.kimp.magicmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityList extends Activity {

    static String TAG = "ActivityList";
    Button addActivity;
    ListView listView;
    String titleList[], aidList[];
    ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getActivity();
        findV();
        clickListener();

    }

    public void findV() {
        addActivity = (Button) findViewById(R.id.activity_btn_addActivity);
        listView = (ListView) findViewById(R.id.activity_listview);
    }

    public void clickListener() {
        //新增活動
        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityList.this, MapSelect.class);
                startActivity(intent);
            }
        });
        //點擊進入活動清單
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> getItem = (HashMap<String, String>) listView.getAdapter().getItem(position);
                String getAid = getItem.get("Aid");
                String getTitle = getItem.get("Title");

                Intent intent = new Intent(ActivityList.this, MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("aid", getAid);
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(ActivityList.this, "活動名稱：" + getTitle, Toast.LENGTH_SHORT).show();
            }
        });
        //長按刪除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final HashMap<String, String> getItem = (HashMap<String, String>) listView.getAdapter().getItem(position);
                final String getAid = getItem.get("Aid");
                String getTitle = getItem.get("Title");

                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityList.this);
                builder.setTitle("刪除此活動？");
                builder.setMessage("確定要刪除" + getTitle + "這個活動嗎？");
                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteActivity(getAid);
                        Intent intent = new Intent(ActivityList.this, Refresh.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.show();

                return true;
            }
        });
    }

    public void deleteActivity(String aid) {
        Model model = new Model();
        model.deleteActivity(aid, deleteActivityHandler);
    }

    public void getActivity() {
        Model model = new Model();
        model.getActivityModel(getActivityHandler);
    }

    private Handler deleteActivityHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("response");
            Log.e("LoginDebug", "Login Result:" + result);
        }
    };

    private Handler getActivityHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("result");
            Log.e(TAG, "Activity_List Result:" + result);
            getJSONsetList(result);
        }
    };

    private void getJSONsetList(String result) {
        try {
            JSONArray ja = new JSONArray(result);
            titleList = new String[ja.length()];
            aidList = new String[ja.length()];

            /*剖析JSON, 放進String 陣列*/
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = new JSONObject(ja.get(i).toString());
                HashMap<String, String> value = new HashMap<>();
                value.put("Title", jo.getString("title"));
                value.put("Aid", jo.getString("aid"));
                items.add(value);
            }
            //建立SimpleAdapter(ListView 適配器)
            SimpleAdapter simpleAdapter = new SimpleAdapter(ActivityList.this, //目前Context
                    items, //要放入的資料
                    R.layout.simplelist, //適配器的Layout
                    new String[]{"Aid", "Title"}, // 資料的取出順序
                    new int[]{R.id.text1, R.id.text2}); //取出的資料對應元件id
            listView.setAdapter(simpleAdapter); //設定ListView 的適配器

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
