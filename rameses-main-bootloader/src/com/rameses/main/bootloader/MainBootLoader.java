/*
 * MainBootLoader.java
 *
 * Created on March 27, 2013, 1:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.main.bootloader;

import com.rameses.server.BootLoader;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author Elmo
 */
public class MainBootLoader {
    
    public static void main(String[] args) throws Exception {
        loadEnv();
        
        BootLoader boot = new BootLoader();
        boot.start();
    }
    
    private static void loadEnv() {
        String srundir = System.getProperty("osiris.run.dir"); 
        if ( srundir == null || srundir.length() == 0 ) { 
            srundir = System.getProperty("user.home.dir", ""); 
        } 
        
        File dir = new File( srundir ); 
        File envfile = new File( dir, "env.conf" ); 
        if ( !envfile.exists() || envfile.isDirectory()) {
            System.out.println("** No available env.conf file");
            System.out.println(" ");
            return; 
        }

        System.out.println("** Loading file env.conf ...");
        System.out.println(" ");
        
        FileInputStream fis = null; 
        try {
            fis = new FileInputStream( envfile ); 

            Properties props = new Properties();
            props.load( fis ); 
            System.getProperties().putAll( props ); 
            props.clear(); 
        } 
        catch(Throwable t) {
            t.printStackTrace(); 
        }
        finally {
            try { fis.close(); }catch(Throwable t){;} 
        }
    }
}
