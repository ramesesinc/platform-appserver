package com.rameses.osiris3.cache;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;



//THIS CAN BE USED FOR SINGLE SERVERS ONLY. NOT INTENDED FOR CLUSTERED

//this only holds the keys. not the object
public class BulkKeyEntry  {
    
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue();
    private int timeout;
    private int options;
    
    public BulkKeyEntry(int timeout, int options) {
        this.timeout = timeout;
        this.options = options;
    }
    
    public synchronized void add(String key) {
        queue.add( key );
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    //this is the blocking code 
    public synchronized String getNextMessage(int timeout) throws InterruptedException {
        return queue.poll( timeout, TimeUnit.SECONDS );
    }

    public synchronized String getNextMessage() {
        return queue.poll();
    }
    
    public int getOptions() {
        return options;
    }
}