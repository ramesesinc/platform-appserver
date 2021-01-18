package com.rameses.fileupload;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class API {
    private String host;
    private int port;
    private String channel;
    private boolean secured = false;
    private boolean debug = false;
    private String downloadDir = "";

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public String getChannel() {
        return channel;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }
    
    public String getDownloadDir() {
        return downloadDir + "/";
    }
    
    public String getUri() {
        StringBuilder sb = new StringBuilder();
        sb.append(secured ? "https" : "http").append("://");
        sb.append(host == null ? "localhost" : host).append(":");
        sb.append(port <= 0 ? "3000" : (port + "")).append("/");
        return sb.toString();
    }
    
    public String getFullKey(String key, String channel) {
        if (key == null && channel == null) {
            return this.channel + "/";
        }
        if (key == null) {
            return channel;
        }
        if (channel == null) {
            return key;
        }
        return channel + "/" + key;
    }

    public String readString(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while(scanner.hasNext()) {
            sb.append(scanner.next());
        }
        return sb.toString();
    }
    
    
    public void debug(String key, String value) {
        debug(key + ": " + value);
    }
    
    public void debug(String msg) {
        if (debug) {
            System.out.println("FileUpload [DEBUG] " + msg);
        }
    }
    
    public void info(String action, String msg) {
        System.out.println("FileUpload [" + action.toUpperCase() + "] " + msg);
    }
    
}
