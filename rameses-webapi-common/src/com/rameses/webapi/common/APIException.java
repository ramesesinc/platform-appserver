/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.webapi.common;

import java.lang.RuntimeException;
import java.lang.String;

/**
 *
 * @author wflores
 */
public class APIException extends RuntimeException {
    
    private int code; 

    public APIException( int code, String message ) { 
        super( message ); 
        this.code = code; 
    } 

    public int getCode() { 
        return code; 
    }

    public static class Require extends APIException {
        public Require( String message ) { 
            super(1, message); 
        }
    }    
    public static class RequiredParam extends APIException {
        public RequiredParam( String name ) { 
            super(1, name + " parameter is required"); 
        }
    }
    public static class InvalidAccount extends APIException {
        public InvalidAccount() {
            this("Invalid Account"); 
        }
        public InvalidAccount( String message ) {
            super(2, message); 
        }
    }
    public static class InvalidHashKey extends APIException {
        public InvalidHashKey() {
            super(3, "Invalid HashKey"); 
        }
    }
    public static class InvalidFormat extends APIException {
        public InvalidFormat( String message) {
            super(4, message); 
        }
    }
    public static class RecordNotFound extends APIException { 
        public RecordNotFound( String message) { 
            super(5, message); 
        } 
    }     
}
