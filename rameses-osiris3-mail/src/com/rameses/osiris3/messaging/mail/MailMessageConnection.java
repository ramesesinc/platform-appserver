/*
 * MailMessageConnection.java
 *
 * Created on February 7, 2013, 4:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.messaging.mail;

import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;

import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Elmo
 *  mail.smtp.starttls.enable", "true"); // added this line
 * props.put("mail.smtp.host", host);
 * props.put("mail.smtp.user", from);
 * props.put("mail.smtp.password", pass);
 * props.put("mail.smtp.port", "587");
 * props.put("mail.smtp.auth", "true");
 *
 *
 */
public class MailMessageConnection extends MessageConnection {
    
    private Properties properties;
    
    public MailMessageConnection(String name, Map props) {
        properties = new Properties();
        properties.putAll( props );
    }
    
    public void start() {
    }
    
    public void stop() {
    }
    
    public void send(Object data) {
        Transport transport = null;
        try {
            String host = properties.getProperty( "mail.smtp.host" );
            String user = properties.getProperty( "mail.smtp.user" );
            String pass = properties.getProperty( "mail.smtp.password" );
            String protocol = properties.getProperty( "mail.transport.protocol", "smtp" );
            
            Map map = (Map)data;
            String from = (String) map.get("from");
            if(from==null) {
                from = (String) properties.get("mail.from");
                if(from==null) {
                    from = (String) properties.get("mail.smtp.user");
                }
            }
            String sto = (String)map.get("to");
            if(sto==null)
                throw new Exception("To must be specified in data");
            String[] to = sto.split(",");
            String subject = (String)map.get("subject");
            String msg = (String)map.get("msg");
            
            
            Session session = Session.getDefaultInstance(properties, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            
            InternetAddress[] toAddress = new InternetAddress[to.length];
            
            // To get the array of addresses
            System.out.println("to-length=" + to.length);
            for( int i=0; i < to.length; i++ ) { // changed from a while loop
                toAddress[i] = new InternetAddress(to[i].trim());
                System.out.println("  " + toAddress[i]);                
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            
            message.setSubject(subject);
            message.setContent(msg, "text/html; charset=utf-8");
            transport = session.getTransport(protocol);
            transport.connect(host, user, pass);
            transport.sendMessage(message, message.getAllRecipients());
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            try { transport.close(); } catch(Exception ign){;}
        }
    }
    
    public void sendText(String data) {
        throw new RuntimeException("sendText method in mail not supported");
    }
    
    public Map getConf() {
        return properties;
    }

    @Override
    public void send(Object data, String queueName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addResponseHandler(String tokenid, MessageHandler handler) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
