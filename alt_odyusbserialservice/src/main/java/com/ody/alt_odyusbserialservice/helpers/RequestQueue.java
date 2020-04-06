package com.ody.alt_odyusbserialservice.helpers;

import java.util.Vector;

@SuppressWarnings("unchecked")
public class RequestQueue {
    private Vector queue;
    private static RequestQueue rq;
    private static final int bufSize = 512;

    public RequestQueue() {
        this.queue = new Vector();
    }

    public static RequestQueue getInstance() {
        if (rq == null) {
            rq = new RequestQueue();
        }
        return rq;
    }

    public synchronized RequestData dequeue()
            throws InterruptedException {
        while (this.queue.isEmpty()) {
            wait();
        }
        RequestData retValue = (RequestData) this.queue.firstElement();
        this.queue.removeElementAt(0);
        return retValue;
    }

    public synchronized void enqueue(RequestData obj) {
        if (obj.isPrintImmediate()) {
            this.queue.insertElementAt(obj, 0);
        } else {
            this.queue.addElement(obj);
        }
        notify();
    }

    public synchronized void clearQueue() {
        this.queue.removeAllElements();
    }

    public synchronized boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void addRequest(byte[] data) {
        addRequest(data, true);
    }

    public void addRequest(byte[] data, boolean encryptMode) {
        enqueue(new RequestData(data, false));
    }
}
