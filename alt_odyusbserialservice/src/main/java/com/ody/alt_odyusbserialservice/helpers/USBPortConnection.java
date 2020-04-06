package com.ody.alt_odyusbserialservice.helpers;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import java.io.InputStream;

public class USBPortConnection implements DeviceConnection {
    private RequestQueue queue;
    private Thread requestHandler;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbInterface usbInterface;
    private UsbEndpoint endPointInput;
    private UsbEndpoint endPointOutput;

    //constructor
    protected USBPortConnection(UsbDeviceConnection deviceConnection)
    {
        this.usbDeviceConnection = deviceConnection;
        this.queue = new RequestQueue();
        this.requestHandler = new Thread(new SenderThread());
        this.requestHandler.start();
    }

    protected void setInterface(UsbInterface usbInterface)
    {
        this.usbInterface = usbInterface;
    }

    protected void setEndPointIn(UsbEndpoint endpoint)
    {
        this.endPointInput = endpoint;
    }

    protected void setEndPointOut(UsbEndpoint endpoint)
    {
        this.endPointOutput = endpoint;
    }

    public void close()
            throws InterruptedException
    {
        int count = 0;
        while ((!this.queue.isEmpty()) && (count < 3))
        {
            Thread.sleep(100L);
            count++;
        }
        this.usbDeviceConnection.releaseInterface(this.usbInterface);
        this.usbDeviceConnection.close();
        if ((this.requestHandler != null) && (this.requestHandler.isAlive())) {
            this.requestHandler.interrupt();
        }
    }

    @Deprecated
    public InputStream getInputStream()
    {
        return null;
    }

    public int readUSB(byte[] buffer)
    {
        return readUSB(buffer, 2000);
    }

    public int readUSB(byte[] buffer, int timeout)
    {
        return this.usbDeviceConnection.bulkTransfer(this.endPointInput, buffer, buffer.length, timeout);
    }

    public RequestQueue getQueue()
    {
        return this.queue;
    }

    class SenderThread
            implements Runnable
    {
        SenderThread() {}

        public void run()
        {
            try
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    byte[] data = USBPortConnection.this.queue.dequeue().getRequestData();
                    USBPortConnection.this.usbDeviceConnection.bulkTransfer(USBPortConnection.this.endPointOutput, data, data.length, 2000);
                    Thread.sleep(10L);
                }
            }
            catch (Exception e)
            {
                USBPortConnection.this.queue.clearQueue();
            }
        }
    }
}
