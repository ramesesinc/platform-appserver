/*
 * CacheProvider.java
 *
 * Created on January 16, 2013, 4:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cache;

import java.util.Map;

/**
 *
 * @author Elmo
 */
public interface CacheProvider {
    String getProviderName();
    CacheConnection getCache(String name, Map conf);
}
