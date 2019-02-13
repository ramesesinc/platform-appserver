/*
 * BasicChannelProvider.java
 *
 * Created on March 4, 2013, 10:23 AM
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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Elmo
 */
public class BasicChannelProvider implements ChannelProvider {
    
    private Channel createChannel(String name, Map props) {
        String type = (String)props.get("type");
        if(type!=null && type.equals("queue")) {
            return new QueueChannel(name);
        } 
        else {
            return new TopicChannel(name);
        }
    }
    
    public List<Channel> loadChannels() {
        final List<Channel> channels = new ArrayList();
        try {
            Enumeration<URL> urls = getClass().getClassLoader().getResources( "channels" );
            while(urls.hasMoreElements()) {
                URL u = urls.nextElement();
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
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            try {is.close();}catch(Exception ign){;}
                            return false;
                        }
                    }
                });
            }
            return channels;
        } 
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public Channel findChannel(String name) {
        return null;
    }
    
}
