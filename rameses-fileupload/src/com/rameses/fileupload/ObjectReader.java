package com.rameses.fileupload;
import com.rameses.util.Base64Cipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ObjectReader 
{
    private BufferedReader br;
    
    public ObjectReader(File file) throws Exception{
        Reader reader = new FileReader(file);
        br = new BufferedReader(reader);
    }
    
    public ObjectReader(InputStream is) throws Exception{
        Reader reader = new InputStreamReader(is);
        br = new BufferedReader(reader);
    }
    
    public Object read() throws Exception{
        String s = br.readLine();
        if (s == null) 
            return null;
        return toObject(s);
    }
    
    public void close() throws Exception{
        if (br != null) {
            try { 
                br.close();
            } catch (Exception ex) {
                //ignore
            }
        }
    }
    
    private Object toObject(String base64Str){
        return new Base64Cipher().decode(base64Str);
    }
    
}
