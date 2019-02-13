/*
 * Main.java
 *
 * Created on May 19, 2014, 8:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cmd;

import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.OsirisContext;
import com.rameses.rcp.framework.ClientContext;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Elmo
 */
public class Main {
    
    
    public static class InitException extends Exception {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        try {
            
            Map map = new HashMap();
            for(String s: args ){
                String arr[] = s.split("=");
                map.put("app."+arr[0],arr[1]);
            }
            if(!map.containsKey("app.cluster")) map.put("app.cluster", "osiris3");
            
            if(!map.containsKey("app.host")) throw new InitException();
            if(!map.containsKey("app.context")) throw new InitException();
            
            System.out.println("\nWelcome to Osiris3 console\n\n");
            System.out.println("Type a command and press enter to run");
            System.out.println("Type -q to end, -h  for help contents");
            
            
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter(";");
            ClientContext ctx = OsirisContext.getClientContext();
            ctx.setAppEnv( map );
            System.out.print("\nosiris3> ");
            
            Binding binding = new Binding();
            GroovyShell shell = new GroovyShell(binding);
            
            while(true){
                if( scanner.hasNext()) {
                    String s1 = scanner.nextLine();
                    if(s1.startsWith("-e") || s1.startsWith("-es")) {
                        boolean displayResult = true;
                        if(s1.startsWith("-es")) displayResult = false;
                        
                        String s = null;
                        if(!displayResult)
                            s = s1.replace("-es","").trim();
                        else
                            s= s1.replace("-e","").trim();
                        int idx = s.indexOf(".");
                        String x1 = s.substring(0,idx);
                        String x2 = s.substring(idx+1);
                        StringBuilder sb = new StringBuilder();
                        sb.append(" return "+ s.trim()+ "\n");
                        
                        try {
                            Object svc = InvokerProxy.getInstance().create( x1 );
                            shell.setVariable(x1, svc );
                            Object r= shell.evaluate( sb.toString() );
                            if(r!=null && displayResult ) System.out.println(r);
                        }
                        catch(Exception ee) {
                            ee.printStackTrace();
                        }
                    } else if(s1.equals("-h")||s1.equals("help")) {
                        System.out.println("-e(s) <ServiceName>.<methodName>(<params>)  = calls a service and execute groovy script. s=silent dont disply result");
                        System.out.println("-q|quit                                       = exits the console");
                        System.out.println("-h|help                                       = displays help");
                        System.out.println("any other command                             = runs any groovy script");
                    } else if(s1.equals("-q")||s1.equals("quit")) {
                        System.out.println("Bye");
                        break;
                    } 
                    else if( s1.equals("-c")) {
                        System.in.mark(0);
                        System.in.reset();
                    }
                    else {
                        try {
                            Object z = shell.evaluate( s1 );
                            if(z!=null) System.out.println(z);
                        } 
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("\nosiris3> ");
                }
            }
            System.out.println("*** session ended ****");
        } catch(InitException ie) {
            System.out.println("Please include the following example:");
            System.out.println("host=<host:port> cluster=<clustername> context=<appname>");
        } catch(Exception e) {
            throw e;
        }
    }
    
    
    
    
    
    
}
