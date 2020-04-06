package com.ody.odyusbserialservice.helpers;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

public class USBPort {
    private static final String TAG = "USBPORT";
    private UsbManager mUsbManager;
    private static final int VID = 1317;
    private static final int[] PID = { 42752, 42753, 42754 };

    //constructor
    public USBPort(UsbManager usbManager)
    {
        this.mUsbManager = usbManager;
    }

    public UsbDevice search_device(int model) //uses android.hardware.usb.UsbDevice;
    {
        HashMap<String, UsbDevice> usblist = this.mUsbManager.getDeviceList();
        Iterator<String> iterator = usblist.keySet().iterator();
        UsbDevice usbDev = null;
        if ((model < 0) || (model > 2)) {
            model = 0;
        }
        while (iterator.hasNext())
        {
            usbDev = (UsbDevice)usblist.get(iterator.next());
            if ((usbDev.getVendorId() == 1317) &&
                    (usbDev.getProductId() == PID[model]))
            {
                Log.i("USBPORT", "USB Connected. VID " +
                        Integer.toHexString(usbDev.getVendorId()) + ", PID " +
                        Integer.toHexString(usbDev.getProductId()));
                break;
            }
            usbDev = null;
        }
        return usbDev;
    }

    public USBPortConnection connect_device(UsbDevice usbDev)
            throws Exception
    {
        if (usbDev != null)
        {
            UsbInterface intf = null;
            int interfaceCount = 0;
            int endPointCount = 0;
            UsbEndpoint epin = null;
            UsbEndpoint epout = null;

            interfaceCount = usbDev.getInterfaceCount();
            Log.i("USBPORT", "Interface count " + interfaceCount);
            if (interfaceCount <= 0) {
                return null;
            }
            for (int i = 0; i < interfaceCount; i++)
            {
                intf = usbDev.getInterface(i);
                endPointCount = intf.getEndpointCount();
                Log.i("USBPORT", "Endpoint count " + endPointCount);
                if (endPointCount <= 0) {
                    return null;
                }
                for (int j = 0; j < endPointCount; j++)
                {
                    UsbEndpoint usbEndPoint = intf.getEndpoint(j);
                    if (usbEndPoint.getDirection() == 128) {
                        epin = usbEndPoint;
                    } else if (usbEndPoint.getDirection() == 0) {
                        epout = usbEndPoint;
                    }
                }
            }
            UsbDeviceConnection connection = this.mUsbManager.openDevice(usbDev);
            if ((connection != null) && (connection.claimInterface(intf, true)))
            {
                USBPortConnection portConnection = new USBPortConnection(connection);
                portConnection.setInterface(intf);
                portConnection.setEndPointIn(epin);
                portConnection.setEndPointOut(epout);
                return portConnection;
            }
            throw new Exception("");
        }
        return null;
    }
}
