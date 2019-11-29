package com.example.client2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class start extends Activity {


    private EditText mEditIP;
    private ClientThread mClientThread;
    private Button mBtnConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        Log.d("start", "start 시작");
        mEditIP = (EditText) findViewById(R.id.editIP);
        mBtnConnect = (Button) findViewById(R.id.btnConnect);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean auto_connect = prefs.getBoolean("pref_autoconnect", false);
        String default_ip = prefs.getString("pref_defaultip", "172.20.10.6");
        mEditIP.setText(default_ip);
//        if (auto_connect)
//            mOnClick(mBtnConnect);

        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClientThread != null)
                    return;
                String addr = mEditIP.getText().toString();
                if (addr.length() == 0)
                    return;
                Intent sendAddr = new Intent(start.this, MainActivity.class);
                sendAddr.putExtra("stringAddr", addr);
                startActivity(sendAddr);
                Log.d("finish", "finish");
                finish();

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Remove the activity when its off the screen
        Log.d("onPause", "onPause");
        finish();
    }
}

