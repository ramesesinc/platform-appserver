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
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Elmo
 */
public class EmailConnection extends XConnection {
    
    private AbstractContext ctx;
    private Map conf;
    private String name;
    private Session session;
    
    public EmailConnection(String name, AbstractContext ctx, Map conf) {
        this.conf = conf;
        this.ctx = ctx;
        this.name = name;
    }
    
    public void start() {
        //System.out.println("startng email service ");
    }
    
    public void stop() {
        //System.out.println("stopping email service ->"+this.name);
    }
    
    public Map getConf() {
        return conf;
    }
    
    public void send( Map msg ) throws Exception  {

        try { 
            String to = (String)msg.get("to");
            if( to ==null || to.trim().length() == 0 ) {
                throw new Exception("to - Message Receipint is require in mail");
            }
            
            Properties properties = new Properties();
            properties.putAll( conf );
            
            if( properties.containsKey("debug")) {
                properties.setProperty("mail.debug", properties.get("mail.debug").toString() );                
            }
            
            Session session = Session.getDefaultInstance( properties );   
            MimeMessage message = new MimeMessage(session);  

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            if(msg.containsKey("subject")) {
                String subject = (String)msg.get("subject");
                message.setSubject(subject);
            }
            
            String msgText = (String)msg.get("message");
            
            List<Map> attachments = (List)msg.get("attachments");
            
            if ( attachments!=null && attachments.size() > 0 ) {
                Multipart multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent( (msgText==null?"":msgText),"text/html");
                multipart.addBodyPart(messageBodyPart);
                for( Map file : attachments ) {
                    messageBodyPart = new MimeBodyPart();
                    String filename = (String)file.get("filename");
                    if( filename ==null) throw new Exception("attachment must have a filename");
                    String title = (String)file.get("title");
                    if(title==null) title = filename;
                    DataSource source = new FileDataSource(filename);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(title);
                    multipart.addBodyPart(messageBodyPart);
                }
                message.setContent(multipart); 
            } 
            else if ( msgText != null ) { 
                message.setContent(msgText, "text/html");
            }
            Transport.send(message);
        } 
        catch (RuntimeException re) { 
            re.printStackTrace();
            throw re;  
        } 
        catch (Exception e) {  
            e.printStackTrace();
            throw e; 
        } 
    
    }
    
}
