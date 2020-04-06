package com.ody.alt_odyusbserialservice.helpers;

import java.io.InputStream;

public abstract interface DeviceConnection {
    public abstract InputStream getInputStream();

    public abstract RequestQueue getQueue();
}
