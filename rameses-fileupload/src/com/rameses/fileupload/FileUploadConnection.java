package com.rameses.fileupload;

import com.rameses.io.IOStream;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.server.JsonUtil;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.net.URIBuilder;

public class FileUploadConnection extends MessageConnection {

    private String name;
    private AbstractContext context;
    private Map conf;
    private boolean enabled = false;
    private boolean started = false;
    private API api;
    private boolean debug = false;

    public FileUploadConnection(String name, AbstractContext context, Map conf) {
        this.name = name;
        this.context = context;
        this.conf = conf;
        enabled = ("false".equals(getProperty("enabled") + "") ? false : true);
    }

    private String getProperty(String name) {
        return getProperty(name, conf);
    }

    private String getProperty(String name, Map map) {
        Object o = (map == null ? null : map.get(name));
        return (o == null ? null : o.toString());
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Map getConf() {
        return conf;
    }

    public void setAPI(API api) {
        this.api = api;
    }

    @Override
    public void start() {
        if (started) {
            return;
        }

        started = true;
        System.out.println(name + ": connected");
    }

    public Object upload(String key, File file, String channel) throws Exception {
        if (!started) {
            return null;
        }
        if (key == null) {
            throw new Exception("key must be specified");
        }

        String uploadUrl = api.getUri() + "filipizen/attachment/upload";
        String fullKey = api.getFullKey(key, channel);
        api.debug("key", fullKey);
        
        HttpPost httpPost = new HttpPost(uploadUrl);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        builder.addTextBody("key", fullKey);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        return doUpload(fullKey, httpPost);
    }

    public Object upload(String key, byte[] bytes, String channel) throws Exception {
        if (!started) {
            return null;
        }
        if (key == null) {
            throw new Exception("key must be specified");
        }

        String uploadUrl = api.getUri() + "filipizen/attachment/upload";
        String fullKey = api.getFullKey(key, channel);
        api.debug("key", fullKey);
        
        HttpPost httpPost = new HttpPost(uploadUrl);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", bytes);
        builder.addTextBody("key", fullKey);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        return doUpload(fullKey, httpPost);
    }
    
    public Object upload(String key, InputStream inputStream, String channel) throws Exception {
        if (!started) {
            return null;
        }
        if (key == null) {
            throw new Exception("key must be specified");
        }

        String uploadUrl = api.getUri() + "filipizen/attachment/upload";
        String fullKey = api.getFullKey(key, channel);
        api.debug("key", fullKey);
        
        HttpPost httpPost = new HttpPost(uploadUrl);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", inputStream);
        builder.addTextBody("key", fullKey);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        return doUpload(fullKey, httpPost);
    }

    public void download(String key, String channel) throws Exception {
        if (!started) {
            return;
        }
        if (key == null) {
            throw new Exception("key must be specified");
        }

        String downloadUrl = api.getUri() + "filipizen/attachment/download";
        String fullKey = api.getFullKey(key, channel);
        api.debug("key", fullKey);
        
        URIBuilder builder = new URIBuilder(downloadUrl);
        builder.setParameter("key", fullKey);
        HttpGet httpGet = new HttpGet(builder.build());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getCode() == 200) {
            String[] pathTokens = key.split("/");
            String fileName = api.getDownloadDir() + pathTokens[pathTokens.length - 1];
            api.debug("fileName", fileName);
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            IOStream.write(response.getEntity().getContent(), fos);
            api.info("DOWNLOADED", fullKey);
        } else {
            throw new Exception(response.getReasonPhrase());
        }
    }
    
    public ObjectReader getObject(String key, String channel) throws Exception {
        if (!started) {
            return null; 
        }
        if (key == null) {
            throw new Exception("key must be specified");
        }

        String downloadUrl = api.getUri() + "filipizen/attachment/download";
        String fullKey = api.getFullKey(key, channel);
        api.debug("key", fullKey);
        
        URIBuilder builder = new URIBuilder(downloadUrl);
        builder.setParameter("key", fullKey);
        HttpGet httpGet = new HttpGet(builder.build());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getCode() == 200) {
            api.info("GETOBJECT", fullKey);
            ObjectReader reader = new ObjectReader(response.getEntity().getContent());
            return reader;
        } else {
            throw new Exception(response.getReasonPhrase());
        }
    }

    public void delete(String key, String channel) throws Exception {
        if (!started) {
            return;
        }
        
        String deleteUri = api.getUri() + "filipizen/attachment/delete";
        
        String fullKey = api.getFullKey(key, channel);
        api.debug("key", fullKey);

        URIBuilder builder = new URIBuilder(deleteUri);
        builder.setParameter("key", fullKey);
        HttpDelete httpDelete = new HttpDelete(builder.build());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpDelete);
        if (response.getCode() != 200) {
            String error = response.getReasonPhrase();
            throw new Exception(error);
        } else {
            api.info("DELETED", fullKey);
        }
    }

    public List getList(String key, String channel) throws Exception {
        List emptyList = new ArrayList();
        if (!started) {
            return emptyList;
        }

        String listUrl = api.getUri() + "filipizen/attachment/list";
        String prefix = api.getFullKey(key, channel);
        
        URIBuilder builder = new URIBuilder(listUrl);
        builder.setParameter("prefix", prefix);
        HttpGet httpGet = new HttpGet(builder.build());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getCode() == 200) {
            String content = api.readString(response.getEntity().getContent());
            List list = JsonUtil.toList(content);
            return list;
        } else {
            throw new Exception(response.getReasonPhrase());
        }
    }

    @Override
    public void stop() {
        System.out.println(name + " : Stopping FileUploadConnection");
        super.stop();
    }

    /**
     * ************************************************************************
     * This is used for handling direct or P2P responses. The queue to create
     * will be a temporary queue.
    **************************************************************************
     */
    @Override
    public void addResponseHandler(String tokenid, MessageHandler handler) throws Exception {

    }

    @Override
    public void send(Object data) {
    }

    @Override
    public void sendText(String data) {
    }

    @Override
    public void send(Object data, String queueName) {
    }

    private Object doUpload(String key, HttpPost post) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post);
        if (response.getCode() == 200) {
            api.info("UPLOADED", key);
            String content = api.readString(response.getEntity().getContent());
            try { response.close(); } catch( Throwable t) {};
            Object obj = JsonUtil.toObject(content);
            return obj;
        } else {
            throw new Exception(response.getReasonPhrase());
        }
    }
}
