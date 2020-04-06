package com.ody.odyscale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ody.odyusbserialservice.SerialWorker;

public class MainActivity extends AppCompatActivity {
    public TextView tv;
    private String TAG = "ody.scale.app";
    SerialWorker sw = null;
    IntentFilter filter;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.hasExtra("response")) {
                    switch (intent.getStringExtra("response")) {
                        case "open":
                            if (tv != null) {
                                boolean value = intent.getBooleanExtra("isOpen", false);
                                if (value) {
                                    tv.setText("true");
                                } else {
                                    tv.setText("false");
                                }
                            }
                            break;
                        case "close":
                            if (tv != null) {
                                boolean value = intent.getBooleanExtra("isClosed", false);
                                if (value) {
                                    tv.setText("true");
                                } else {
                                    tv.setText("false");
                                }
                            }
                            break;
                        case "permission":
                            if (tv != null) {
                                boolean value = intent.getBooleanExtra("hasPermission", false);
                                if (value) {
                                    tv.setText("true");
                                } else {
                                    tv.setText("false");
                                }
                            }
                            break;
                        case "result":
                            if (tv != null) {
                                String value = intent.getStringExtra("value");
                                tv.setText(value);
                                if (sw != null){
                                    sw.Close();
                                    sw.main();
                                    sw.Read();
                                }
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "onReceive: broke it again", e);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        filter = new IntentFilter("com.ody.scale");

        tv = findViewById(R.id.tv_scale_reading);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactWithLib(context, 1659);
            }
        });
        sw = new SerialWorker(context, 1659);
        Button open = findViewById(R.id.btn_open);
        Button read = findViewById(R.id.btn_read);
        Button close = findViewById(R.id.btn_close);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerReceiver(mUsbReceiver, filter);
                sw.main();
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sw.Read();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sw.Close();
                if(mUsbReceiver != null){
                    try {
                        context.unregisterReceiver(mUsbReceiver);
                    }catch (IllegalArgumentException il){
                        Log.e(TAG, "onClick: planned error, receiver not registered", il);
                    }
                }
            }
        });

    }

    public void interactWithLib(Context context, int deviceVID) {
        SerialWorker worker = new SerialWorker(context, deviceVID);
        //update();
    }

    public void update(String value) {
        tv.setText(value);
    }

}
