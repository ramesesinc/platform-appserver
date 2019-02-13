/*
 * UrlChannelProvider.java
 *
 * Created on March 12, 2013, 1:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Elmo
 */
public class UrlChannelProvider implements ChannelProvider {
    
    private String rootUrl;
    
    public UrlChannelProvider() {
        rootUrl = System.getProperty( "websocket.channels" );
        if(rootUrl==null) {
            rootUrl = System.getenv( "websocket.channels" );
        }
    }
    
    private Channel createChannel(String name, Map props) {
        String type = (String)props.get("type");
        if(type!=null && type.equals("queue")) {
            return new QueueChannel(name);
        } else {
            return new TopicChannel(name);
        }
    }
    
    public List<Channel> loadChannels() {
        final List channels = new ArrayList();
        if(rootUrl==null) {
            System.out.println("Channels not found because websocket.channels is not defined");
        } else {
            try {
                System.out.println("looking for channels registered @ " + rootUrl );
                URL u = new URL(rootUrl);
                URLDirectory udir = new URLDirectory(u);
                udir.list( new URLFilter(){
                    public boolean accept(URL u, String filter) {
                        InputStream is = null;
                        try {
                            is = u.openStream();
                            Properties props = new Properties();
                            props.load( is );
                            if(filter.endsWith("/")) filter = filter.substring(0, filter.length()-1);
                            String sname = filter.substring(filter.lastIndexOf("/")+1);
                            Channel chan = createChannel(sname, props);
                            channels.add(chan);
                        } catch(Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {is.close();}catch(Exception ign){;}
                            return false;
                        }
                    }
                });
                
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return channels;
    }
    
    public Channel findChannel(String name) {
        return null;
    }
    
}
