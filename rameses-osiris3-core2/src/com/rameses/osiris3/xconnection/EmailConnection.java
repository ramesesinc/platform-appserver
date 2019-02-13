/*
 * EmailConnection.java
 *
 * Created on April 25, 2014, 8:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.osiris3.core.AbstractContext;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Elmo
 */
public class EmailConnection extends XConnection {
    
    private AbstractContext ctx;
    private Map conf;
    private String name;
    private Session session;
    
    private LinkedBlockingQueue queue = new LinkedBlockingQueue();
    
    public EmailConnection(String name, AbstractContext ctx, Map conf) {
        this.conf = conf;
        this.ctx = ctx;
        this.name = name;
    }
    
    public void start() {
        Properties props = new Properties();
        final String username = (String)conf.remove("mail.username");
        final String password = (String)conf.remove("mail.password");
        
        for( Object om: conf.entrySet() ) {
            Map.Entry me = (Map.Entry)om;
            props.put( me.getKey(), (me.getValue()+"").trim() );
        }
        
        if(!props.containsKey("mail.smtp.auth")) {
            props.put("mail.smtp.auth","true");
        }
        if(!props.containsKey("mail.smtp.starttls.enable")) {
            props.put("mail.smtp.starttls.enable","true");
        }
        session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
        String debug = props.getProperty("mail.debug");    
        if( "true".equals(debug) ) {
            session.setDebug( true );
            session.setDebugOut( System.out );    
        }
    }
    
    public void stop() {
        System.out.println("stopping email service ->"+this.name);
    }
    
    public Map getConf() {
        return conf;
    }
    
    public void send( Map msg ) throws Exception  {
        try {
            String from = (String)msg.get("from");
            if(from == null ) from = (String)conf.get("mail.from");
            String subject = (String) msg.get("subject");
            String message = (String)msg.get("message");
            
            List<String> recipients = (List)msg.get("recipients");
            if(recipients==null)
                throw new Exception("XEmailConnection error. Please provide recipients. list of strings");
            
            Message m = new MimeMessage(session);
            if(from!=null) {
                InternetAddress addressFrom = new InternetAddress(from);
                m.setFrom(addressFrom);
            }
            InternetAddress[] addressTo = new InternetAddress[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                addressTo[i] = new InternetAddress(recipients.get(i));
            }
            m.setRecipients(Message.RecipientType.TO, addressTo);
            
            if(subject!=null) m.setSubject(subject);
            m.setContent(message, "text/html");
            
            Transport.send(m);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
