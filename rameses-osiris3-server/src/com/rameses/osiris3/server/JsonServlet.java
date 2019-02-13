/*
 * JsonServlet.java
 *
 * Created on January 10, 2013, 6:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.common.MediaFile;
import com.rameses.io.IOStream;
import com.rameses.io.StreamUtil;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class JsonServlet extends ServiceInvokerServlet {
    private static int DEFAULT_BUFFER_SIZE = 1024*4;
    
    /***
     * check first if there's an args parameters. if yes parse to json
     * if there's an env, parse json
     * else
     * create a map and place all parameters.assume the service accepts only a map entry
     */
    public String getMapping() { return "/json/*"; }
    
    private HttpServletRequest hreq;
    
    protected Object[] readRequest(HttpServletRequest hreq) throws IOException {
        this.hreq = hreq;
        Object[] args = null;
        
        if(hreq.getMethod().equalsIgnoreCase("POST")) {
            InputStream is = hreq.getInputStream();
            if(is!=null) {
                String s  = StreamUtil.toString(is);
                if(s.trim().startsWith("[")) {
                    args = new Object[] { JsonUtil.toList(s) };
                } else if(s.trim().startsWith("{")) {
                    args = new Object[] { JsonUtil.toMap(s) };
                } else {
                    throw new RuntimeException("Post body not identified");
                }
            }
        } else {
            String _args = hreq.getParameter("args");
            if (_args != null && _args.trim().length() > 0) {
                if (_args.startsWith("[")) {
                    args = JsonUtil.toObjectArray( _args );
                } else if(_args.startsWith("{")) {
                    args = new Object[]{JsonUtil.toMap( _args )};
                } else {
                    throw new RuntimeException("args must be enclosed with []");
                }
            } else {
                Map map = new HashMap();
                Enumeration<String> en = hreq.getParameterNames();
                while (en.hasMoreElements()) {
                    String s = en.nextElement();
                    if (!s.equals("env")) {
                        String s1 = hreq.getParameter(s);
                        Object v = s1;
                        if (s1.trim().startsWith("{") || s1.trim().startsWith("[")) {
                            v = JsonUtil.toObject(s1);
                        }
                        map.put( s, v );
                    }
                }
                
                if (map.isEmpty())
                    args = new Object[]{};
                else
                    args = new Object[]{map};
            }
        }
        
        Map env = new HashMap();
        env.put("CLIENTTYPE", "json");
        Enumeration<String> ss = hreq.getHeaderNames();
        while( ss.hasMoreElements() ) {
            String s2 = ss.nextElement();
            env.put( s2, hreq.getHeader(s2) );
        }
        
         String _env = hreq.getParameter( "env" );
        if (_env != null && _env.trim().length() > 0) {
            if (!_env.startsWith("{"))
                throw new RuntimeException("env must be enclosed with []");
            
            env.putAll( JsonUtil.toMap( _env ));
        }
        
        return new Object[]{ args, env };
    }
    
    protected void writeResponse(Object response, HttpServletResponse hres) {
        if (response instanceof MediaFile) {
            MediaFile mf = (MediaFile) response;
            hres.setContentType(mf.getContentType());
            try {
                write(hreq, hres, mf.getInputStream());
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
            
//            InputStream is = null;
//            OutputStream os = null;
//            try
//            {
//                os = new BufferedOutputStream(hres.getOutputStream());
//                int i = 0;
//                is = mf.getInputStream();
//                while( (i=is.read())!=-1 ) {
//                    os.write( i );
//                }
//                os.flush();
//            }
//            catch(Exception e) {
//                e.printStackTrace();
//            }
//            finally
//            {
//                try {is.close();} catch(Exception ign){;}
//                try {os.close();} catch(Exception ign){;}
//            }
        } else {
            hres.setContentType("application/json");
            try {
                hres.getWriter().println( JsonUtil.toString(response) );
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void write(HttpServletRequest hreq, HttpServletResponse hres, InputStream input) throws Exception {
        IOStream io = new IOStream();
        byte[] bytes = io.toByteArray(input);
        
        String token = '"' + getMd5Digest(bytes) + '"';
        hres.setHeader("ETag", token); // always store the ETag in the header
        String previousToken = hreq.getHeader("If-None-Match");
        
        // compare previous token with current one
        if (previousToken != null && previousToken.equals(token)) {
            hres.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            // use the same date we sent when we created the ETag the first time through
            hres.setHeader("Last-Modified", hreq.getHeader("If-Modified-Since"));
        }
        
        // first time through - set last modified time to now
        else {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            Date lastModified = cal.getTime();
            
            //set the headers
            hres.setBufferSize(DEFAULT_BUFFER_SIZE);
            hres.setDateHeader("Last-Modified", lastModified.getTime());
            hres.setContentLength(bytes.length);
            io.write(bytes, hres.getOutputStream(), DEFAULT_BUFFER_SIZE);
        }
    }
    
    private static String getMd5Digest(byte[] bytes) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 cryptographic algorithm is not available.", e);
        }
        
        byte[] raw = md.digest(bytes);
        return new BigInteger(1, raw).toString(16);
    }
}
