/*
 * ChannelProvider.java
 *
 * Created on March 4, 2013, 9:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.util.List;


public interface ChannelProvider {
    
    //loads the channels from the provider
    List<Channel> loadChannels();
    
    //for dynamic channels
    Channel findChannel(String name);
}
