package com.example.kimp.magicmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendList extends Activity {
    ListView friendlistview;
    static String TAG = "FriendListTAG";
    String friendName[], friendSIMID[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);
        findV();
        getFriend();
        clickListener();
    }

    public void findV() {
        friendlistview = (ListView) findViewById(R.id.friend_list);

    }

    public void clickListener() {
        friendlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendList.this);
                builder.setTitle(R.string.friend_dialog_title);
                builder.setMessage("暱稱：" + friendName[position]);
                builder.setMessage("SIM ID：" + friendSIMID[position]);
                builder.show();
            }
        });
    }


    public void getFriend() {
        Model model = new Model();
        model.userListModel(userHandler);
    }

    private Handler userHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("result");
            Log.d(TAG, "FriendList Result:" + result);

            try {
                JSONArray ja = new JSONArray(result);
                friendName = new String[ja.length()];
                friendSIMID = new String[ja.length()];
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = new JSONObject(ja.get(i).toString());

                    friendName[i] = jo.getString("name");
                    friendSIMID[i] = jo.getString("simid");

                }

                friendlistview.setAdapter(
                        new ArrayAdapter<String>(
                                FriendList.this,
                                android.R.layout.simple_list_item_1,
                                friendName));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

}
