package com.ody.alt_odyusbserialservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Broadcast {
    static String TAG = "ody.scale";
    public static void open(Context context, boolean isOpen){
        try {
            Intent intent = new Intent("com.ody.scale");
            intent.putExtra("response", "open");
            intent.putExtra("isOpen", isOpen);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, "open: Exception", e);
        }
    }

    public static void permission(Context context, boolean hasPermission){
        try {
            Intent intent = new Intent("com.ody.scale");
            intent.putExtra("response", "permission");
            intent.putExtra("hasPermission", hasPermission);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, "open: Exception", e);
        }
    }

    public static void close(Context context, boolean isClosed){
        try {
            Intent intent = new Intent("com.ody.scale");
            intent.putExtra("response", "close");
            intent.putExtra("isClosed", isClosed);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, "open: Exception", e);
        }
    }

    public static void result(Context context, String value){
        try {
            Intent intent = new Intent("com.ody.scale");
            intent.putExtra("response", "result");
            intent.putExtra("value", value);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, "open: Exception", e);
        }
    }
}
