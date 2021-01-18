package com.rameses.fileupload;

import com.rameses.io.FileUtil;
import com.rameses.util.Base64Cipher;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

public class ObjectWriter 
{
    private File tempFile;
    private Writer writer;
    private String fileName;
    private String tempFileName;
    public final static String TEMP_FILE_PREFIX = "~TMP";
    
    public ObjectWriter(String name, Map conf) throws Exception{
        Object uploadDir = conf.get("uploadDir");
        if (uploadDir == null) throw new Exception("uploadDir must be specified in connection");
        
        String dir = uploadDir.toString();
        if (!dir.endsWith("/")) {
            dir += "/";
        }
                
        fileName = dir + name;
        tempFileName = dir + TEMP_FILE_PREFIX + name;
        
        tempFile = new File(tempFileName);
        createFile(tempFile);
    }
    
    public ObjectWriter(File file) throws Exception{
        this.tempFile = file;
        createFile(this.tempFile);
    }
    
    public void write(Object obj) throws Exception{
        String base64Str = toBase64(obj);
        writer.write(base64Str);
    }
    
    public void close() throws Exception{
        if (writer != null){ 
            try {
                writer.close();
                writer = null;
            } catch (Exception ex) {
                //ignore
            }
        }
        if (tempFileName != null) {
            FileUtil.copy(tempFile, new File(fileName));
            tempFile.delete();
        }
    }
    
    public void cancel() throws Exception{
        close();
        if (tempFile.exists()){
            tempFile.delete();
        }
    }
    
    private String toBase64(Object obj){
        String s = new Base64Cipher().encode(obj);
        s += "\n";
        return s;
    }
    
    private void createFile(File file) throws Exception {
        if (file.exists()){
            file.delete();
            file.createNewFile();
        }
        writer = new FileWriter(file, true);
    }
    
}
