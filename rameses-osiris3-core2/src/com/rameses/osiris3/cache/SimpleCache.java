package com.rameses.osiris3.cache;


import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//THIS CAN BE USED FOR SINGLE SERVERS ONLY. NOT INTENDED FOR CLUSTERED
public class SimpleCache extends BlockingCache implements CacheConnection {
    
    private DefaultCacheProvider defaultCacheProvider;
    
    private Map conf;
    private String name;
    private HashMap<String,SimpleCacheUnit> map = new HashMap();
    
    public SimpleCache(DefaultCacheProvider defaultCacheProvider, String name, Map c) {
        this.defaultCacheProvider = defaultCacheProvider;
        this.name = name;
        this.conf = c;
    }
    
    public void start()  {
        super.start();
    }
    
    public void stop()  {
        super.stop();
        map.clear();
    }
    
    public Object get(String name) {
        SimpleCacheUnit su = map.get(name);
        if(su==null) return null;
        if(su.isExpired()) remove(name);
        return su.getValue();
    }
    
    public Object put(String name, Object data, int timeout) {
        return map.put( name,  new SimpleCacheUnit( data, timeout)  );
    }
    
    public Object put(String name, Object data) {
        return put(name, data, 0);
    }
    
    public void remove(String name) {
        map.remove(name);
    }
    
    public void createBulk(String id, int timeout, int options) {
        put(id,new BulkKeyEntry(timeout, options),timeout);
    }
    
    public void appendToBulk(String bulkid, String newKeyId, Object data) {
        if (newKeyId==null)  newKeyId = ("SUBKEY" + new UID());
        Object content = get(bulkid);
        if (content instanceof BulkKeyEntry) {
            BulkKeyEntry b = ((BulkKeyEntry)content);
            b.add( newKeyId );
            put(newKeyId, data, b.getTimeout());
        }  else {
            throw new RuntimeException("append not implemented because stored cache is not a bulk entry");
        }
    }
    
    //by default, after getting the data it is removed. so removeAfter is true by default
    public Map<String, Object> getBulk(String bulkid, int timeout) {
        Object content = get(bulkid);
        if( content ==null )
            throw new ChannelNotFoundException(bulkid);
        
        if (content instanceof BulkKeyEntry) {
            Map collect = new HashMap();
            BulkKeyEntry b = ((BulkKeyEntry)content);
            boolean removeAfter = true;
            List<String> removedEntries = new ArrayList();
            
            //this is blocking
            try {
                String k = b.getNextMessage( timeout );
                if(removeAfter) removedEntries.add(k);
                collect.put( k, get(k) );
                
                while( (k = b.getNextMessage())!=null ) {
                    if(removeAfter) removedEntries.add(k);
                    collect.put( k, get(k) );
                }
                
                //this will remove the sub cache entries
                for(String s : removedEntries) {
                    remove( s );
                }
                removedEntries.clear();
            } catch (InterruptedException ie) {;}
            return collect;
        } else {
            throw new RuntimeException("append not implemented because stored cache is not a bulk entry");
        }
    }

    public Map getConf() {
        return conf;
    }
}