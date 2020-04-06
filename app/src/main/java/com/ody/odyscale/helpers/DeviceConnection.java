package com.ody.odyscale.helpers;

import java.io.InputStream;

public abstract interface DeviceConnection {
    public abstract InputStream getInputStream();

    public abstract RequestQueue getQueue();
}
