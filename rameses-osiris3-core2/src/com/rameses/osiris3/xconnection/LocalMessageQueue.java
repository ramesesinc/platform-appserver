/*
 * AsyncQueue.java
 *
 * Created on May 29, 2014, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.osiris3.xconnection;

import com.rameses.common.AsyncBatchResult;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Elmo
 */
public final class LocalMessageQueue implements MessageQueue {

    private final int QUEUE_LIMIT_SIZE = 20;
    
    private String id;
    private Map options;
    private boolean debug;
    private long timeout = 20000;

    private LinkedBlockingQueue queue = new LinkedBlockingQueue();
    
    public LocalMessageQueue(String id, Map options) {
        this.id = id;
        this.options = options;

        debug = "true".equals(options.get("debug") + "");
        if (options.containsKey("timeout")) {
            timeout = Long.parseLong(options.get("timeout").toString());
        }
    }

    public void push(Object obj) throws Exception {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_push] id=" + id + ", obj=" + obj);
        }
        queue.add(obj);
    }

    public Object poll() throws Exception {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_poll] id=" + id + ", empty=" + queue.isEmpty());
        }

        Object o = queue.poll(timeout, TimeUnit.MILLISECONDS);
        if (!queue.isEmpty()) {
            if (debug) {
                System.out.println("[" + getClass().getSimpleName() + "_poll] id=" + id + ", empty=false, queue-size=" + queue.size());
            }

            List list = new AsyncBatchResult();
            list.add(o);
            while (!queue.isEmpty()) {
                list.add(queue.poll());
            }
            return list;
        } else {
            if (debug && o != null) {
                System.out.println("[" + getClass().getSimpleName() + "_poll] id=" + id + ", empty=false, queue-size=1");
            }

            return o;
        }
    }
    
    public String toString() {
        return "MESSAGE-QUEUE " + this.id;
    }
}
