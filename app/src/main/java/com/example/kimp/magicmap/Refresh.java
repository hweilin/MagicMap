package com.example.kimp.magicmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Refresh extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh);
        Intent intent = new Intent(Refresh.this, ActivityList.class);
        startActivity(intent);
        finish();
    }
}
