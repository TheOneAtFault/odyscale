package com.ody.alt_odyusbserialservice.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.ody.alt_odyusbserialservice.Broadcast;

import java.util.HashMap;
import java.util.Iterator;

public class Permission {
    private static final String ACTION_USB_PERMISSION = "com.genericusb.mainactivity.USB_PERMISSION";

    public static boolean check(Context context, int deviceVID){
        UsbManager manager =(UsbManager) context.getSystemService(Context.USB_SERVICE);
        UsbDevice device = GetDevice(manager, deviceVID);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(ACTION_USB_PERMISSION), 0);

        if (!manager.hasPermission(device)) { //prompt permission
            manager.requestPermission(device, mPermissionIntent);
            return false;
        }else{
            return true;
        }
    }

    public static UsbDevice GetDevice(UsbManager usbManager, int deviceVID) {
        boolean seek = true;
        UsbDevice tempDevice = null;
        HashMap<String, UsbDevice> usblist = usbManager.getDeviceList();
        Iterator<String> iterator = usblist.keySet().iterator();

        while (iterator.hasNext() && seek) {
            tempDevice = (UsbDevice) usblist.get(iterator.next());
            if (tempDevice.getVendorId() == deviceVID) {
                seek = false;//stop while loop
            }
        }
        return tempDevice;
    }
}
