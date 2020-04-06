package com.ody.alt_odyusbserialservice.utils;

import android.content.Context;
import android.util.Log;

import com.ody.alt_odyusbserialservice.Broadcast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.com.prolific.driver.pl2303.PL2303Driver;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class ReadScale {
    private static String TAG = "ody.scale.readscale";

    public static void main(Context context, PL2303Driver usbSerialDriver){
        String result = "";
        try {
            if (usbSerialDriver.InitByBaudRate(PL2303Driver.BaudRate.B9600, 300)) {
                Log.i(TAG, "PL2303 Device-init: device connection succeeded");

                //read from the device
                Thread.sleep(30);

                Log.i(TAG, "Reader: entered");
                result = "no value";
                try {
                    result = read(usbSerialDriver);

                    Thread.sleep(30);
                    Log.i(TAG, "Reader: thread sleep");

                } catch (InterruptedException e) {
                    Log.i(TAG, "Reader: thread sleep broke");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "PL2303 Driver-init: killing driver");

            } else {
                if (!usbSerialDriver.PL2303Device_IsHasPermission()) {
                    Log.i(TAG, "PL2303 Device-init: device permission issue");
                }
            }
        }catch (Exception e){
            Log.e(TAG, "ReadScale: error", e);
        }
        Broadcast.result(context, result);
    }

    public static String read(PL2303Driver instance) {
        String result = "no match";
        byte[] rbuf = new byte[40];
        try {
            int len;
            int timeOut = 0;
            StringBuffer sbHex = new StringBuffer();
            do {
                timeOut++;
                Thread.sleep(200);
                len = instance.read(rbuf);
                Log.i(TAG, "Reader read-loop: Length of read " + len + "instance connection: " + instance.isConnected() + "::time out count - " + timeOut);
            } while (len < 20 && timeOut < 15);

            if (len > 0) {
                Log.i(TAG, "Reader: Length of read " + len);
                for (int j = 0; j < len; j++) {
                    sbHex.append((char) (rbuf[j] & 0x000000FF));
                }
                final String regex = "([\\d]{3}.[\\d]{3})(?!\\s+.(.).\\1)\\b";
                Pattern p = Pattern.compile(regex, CASE_INSENSITIVE);
                Matcher matcher = p.matcher(sbHex.toString());
                if (matcher.find()) {
                    result = matcher.group();
                } else {
                    Log.i(TAG, "Matcher: no match found");
                    result = read(instance);
                }
            } else if (timeOut == 15) {
                result = "timeout";
                Log.i(TAG, "read: Timed out " + timeOut);
            }else{
                Log.i(TAG, "read: leng > 0 " + len);
            }
        } catch (Exception e) {
            Log.e(TAG, "read: whoops", e);
        }
        return result;
    }
}
