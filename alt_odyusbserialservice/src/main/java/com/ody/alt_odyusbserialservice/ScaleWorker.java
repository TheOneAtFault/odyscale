package com.ody.alt_odyusbserialservice;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.BoringLayout;
import android.util.Log;

import com.ody.alt_odyusbserialservice.utils.Permission;
import com.ody.alt_odyusbserialservice.utils.ReadScale;

import java.util.HashMap;
import java.util.Iterator;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class ScaleWorker {
    private static final String ACTION_USB_PERMISSION = "com.genericusb.mainactivity.USB_PERMISSION";
    private String TAG = "ody.scale";

    private Context context;
    private int deviceVID;
    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private PL2303Driver usbSerialDriver;

    public ScaleWorker(Context context, int deviceVID) {
        this.context = context;
        this.deviceVID = deviceVID;
    }

    public void main() {
        String result = "000.000";

        //get device
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        usbDevice = GetDevice(usbManager, deviceVID);
        if (usbDevice != null) {
            if (usbSerialDriver == null)
                usbSerialDriver = new PL2303Driver(usbManager, context, ACTION_USB_PERMISSION);

            if (!usbSerialDriver.isConnected()) {
                ConnectDriver();
                Broadcast.open(context, true);
            } else {
                Broadcast.open(context, true);
            }
        } else {
            Broadcast.open(context, false);
        }
    }

    public void Read() {
        ReadScale.main(this.context, usbSerialDriver);
    }


    private void ConnectDriver() {
        try {
            usbSerialDriver.enumerate();
            Thread.sleep(200);
        } catch (Exception e) {
            Log.e(TAG, "ConnectDriver: error", e);
        }
    }

    public void Close() {
        if (usbSerialDriver != null) {
            usbSerialDriver.end();
            usbSerialDriver = null;
        }
        Broadcast.close(context, true);
    }


    public UsbDevice GetDevice(UsbManager usbManager, int deviceVID) {
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
