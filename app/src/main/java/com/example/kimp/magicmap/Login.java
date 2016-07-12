package com.example.kimp.magicmap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    static String TAG = "LoginTAG";
    Button btn_login;
    EditText edt_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findV();
        setClickListener();
        getSIMID();
    }

    public void findV(){
        btn_login = (Button) findViewById(R.id.btn_login);
        edt_name = (EditText) findViewById(R.id.edt_name);
    }

    public void setClickListener(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model model = new Model();
                model.loginModel(edt_name.getText().toString(), loginHandler);
            }
        });
    }

    public void getSIMID(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        value.simid = telephonyManager.getDeviceId();

    }

    private Handler loginHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("result");
//            Log.d("LoginDebug","Login Result:"+result);

            try {
                JSONObject jo = new JSONObject(result);
                String status = jo.getString("responese");


                if(status.equals("success"))
                {
                    Intent service = new Intent(Login.this, LocationService.class);
                    startService(service);

                    Intent intent = new Intent();
                    intent.setClass(Login.this, Select.class);
                    startActivity(intent);
                }else if(status.equals("fail"))
                {
                    Toast.makeText(Login.this, "Login Fail", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

}
