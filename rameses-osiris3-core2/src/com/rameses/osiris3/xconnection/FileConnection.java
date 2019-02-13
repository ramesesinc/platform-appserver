/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.xconnection;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.util.Encoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 *
 * @author rameses
 */
public class FileConnection extends XConnection {
    
    public final static int CHUNK_SIZE = (64 * 1024);
    
    private AbstractContext ctx;
    private String name;
    private Map conf;

    private String host;
    
    public FileConnection(AbstractContext ctx, String name, Map conf) {
        this.ctx = ctx;
        this.name = name; 
        this.conf = conf; 
    }
    
    public void start() { 
        host = getProperty("host"); 
    } 

    public void stop() {
    }

    public Map getConf() { 
        return conf; 
    }
        
    public Folder createFolder( String name ) {         
        String sid = Encoder.MD5.encode( name );
        File file = new File( getBaseDirectory(), sid ); 
        if ( !file.exists() ) { 
            file.mkdirs(); 
        } 
        return new Folder( file, sid );
    } 

    public Folder getFolder( String name ) { 
        String sid = Encoder.MD5.encode( name );
        File file = new File( getBaseDirectory(), sid ); 
        if ( file.exists() ) {
            return new Folder( file, sid ); 
        }
        throw new RuntimeException("failed to locate folder for " + name); 
    } 
    

    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    public final File getBaseDirectory() {
        if ( host == null || host.trim().length() == 0 ) { 
            throw new RuntimeException("Please provide a host in connection file"); 
        } 
        
        File basedir = new File( this.host ); 
        if ( basedir.isDirectory() ) { 
            return basedir; 
        }
        
        throw new RuntimeException("Please provide a valid host in connection file");
    }
    
    private String getProperty( Object key ) {
        return getProperty( key, getConf() ); 
    }
    private String getProperty( Object key, Map conf ) {
        Object value = (conf == null ? null : conf.get(key)); 
        return (String) value; 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Folder ">
    
    public class Folder {
        
        private File basedir; 
        private String name; 
        
        Folder( File basedir, String name ) { 
            this.basedir = basedir; 
            this.name = name; 
        }
        
        public String getName() { return name; } 
        
        public IndexFile getIndexFile() {
            return new IndexFile( this.basedir );
        }
        
        public MetaFile getMeta() { 
            return new MetaFile( this.basedir ); 
        }
        
        public MetaFile writeMeta( String filename, String filetype, long filesize, int chunkcount ) {
            getIndexFile().remove();
            MetaFile mf = getMeta(); 
            mf.write( filename, filetype, filesize, chunkcount ); 
            return mf; 
        } 
        
        public DataFile getFile( int indexno ) {
            String sid = this.name +"-"+ indexno; 
            File file = new File( this.basedir, sid ); 
            return new DataFile( file );  
        } 
                        
        public DataFile addFile( int indexno, Object value ) {
            byte[] bytes = (byte[]) value;             
            DataFile df = getFile( indexno );         
            df.write( bytes ); 
            
            IndexFile xf = getIndexFile().load(); 
            long currentSize = xf.getDataSize(); 
            xf.setDataCount( indexno ); 
            xf.setDataSize( currentSize + bytes.length ); 
            xf.update(); 
            return df; 
        } 
        

        public void remove() { 
            MetaFile mf = getMeta().load(); 
            int chunkcount = mf.getChunkCount(); 
            for ( int i=1; i<=chunkcount; i++ ) { 
                String sid = this.name +"-"+ i; 
                File file = new File( this.basedir, sid ); 
                if ( file.exists() ) {
                    try { 
                        file.delete(); 
                    } catch(Throwable t) {;} 
                } 
            } 
            
            getIndexFile().remove(); 
            getMeta().remove(); 
            
            try { 
                this.basedir.delete(); 
            } catch(Throwable t){;} 
        } 
        
        public boolean isCompleted() {
            MetaFile mf = getMeta().load();
            IndexFile xf = getIndexFile().load();
            return (mf.getFileSize()==xf.getDataSize() && mf.getChunkCount()==xf.getDataCount()); 
        } 
        
        public byte[] getContent() { 
            MetaFile mf = getMeta().load(); 
            long filesize = mf.getFileSize(); 
            int chunkcount = mf.getChunkCount(); 
            
            ByteArrayOutputStream baos = null; 
            try {
                baos = new ByteArrayOutputStream();
                for ( int i=1; i<=chunkcount; i++ ) { 
                    String sid = this.name +"-"+ i; 
                    File file = new File( this.basedir, sid ); 
                    new DataFile( file ).read( baos ); 
                } 
                byte[] bytes = baos.toByteArray(); 
                if ( bytes.length == filesize ) {
                    return bytes; 
                } 
                throw new Exception("file is corrupted"); 
            } catch(Throwable t) { 
                throw new RuntimeException("failed to get content caused by " + t.getMessage()); 
            } finally {
                try { baos.close(); }catch(Throwable t){;} 
            }
        }
        
        public InputStream getContentAsStream() {
            byte[] bytes = getContent(); 
            return (bytes == null? null: new ByteArrayInputStream(bytes)); 
        }
    } 
    
    private class ItemFileFilter implements FilenameFilter {
        
        private String sid; 
        
        ItemFileFilter( String prefix ) {
            this.sid = prefix + "-"; 
        } 
        
        public boolean accept(File file, String name) {
            if ( file.isDirectory() ) { 
                return false;
            } else {
                return name.startsWith( sid ); 
            }
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MetaFile ">    
    
    public class MetaFile { 
        
        private File file; 

        private String filename;
        private String filetype;
        private long filesize;
        private int chunkcount; 
        
        MetaFile( File basedir ) {
            this.file = new File( basedir, "meta" ); 
        } 
        
        public String getFileName() { return filename; } 
        public String getFileType() { return filetype; } 
        public long getFileSize() { return filesize; }
        public int getChunkCount() { return chunkcount; } 
        
        void remove() {
            try {
                this.file.delete(); 
            } catch(Throwable t) {;}  
        }
                
        void write( String filename, String filetype, long filesize, int chunkcount ) {
            FileOutputStream fos = null; 
            ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream( file ); 
                oos = new ObjectOutputStream( fos );
                oos.writeObject(new Object[]{ 
                    new Object[]{ "filename", filename },
                    new Object[]{ "filetype", filetype },
                    new Object[]{ "filesize", filesize },
                    new Object[]{ "chunkcount", chunkcount } 
                }); 
            } catch( Throwable t ) { 
                throw new RuntimeException("failed to write meta file caused by " + t.getMessage()); 
            } finally {
                try { oos.close(); }catch(Throwable t){;} 
                try { fos.close(); }catch(Throwable t){;} 
            } 
            
            load(); 
        }
        
        public MetaFile load() { 
            this.filename = null; 
            this.filetype = null;  
            this.filesize = 0; 
            this.chunkcount = 0; 
            
            if ( file.exists() ) {
                Object result = null; 
                FileInputStream fis = null; 
                ObjectInputStream ois = null; 
                try {
                    fis = new FileInputStream( file ); 
                    ois = new ObjectInputStream( fis ); 
                    result = ois.readObject(); 
                } catch( Throwable t ) { 
                    throw new RuntimeException("failed to load meta file caused by " + t.getMessage()); 
                } finally {
                    try { ois.close(); }catch(Throwable t){;} 
                    try { fis.close(); }catch(Throwable t){;} 
                }    
                
                if ( result instanceof Object[] ) {
                    Object[] objs = (Object[])result; 
                    if ( objs.length >= 1 ) {
                        this.filename = getStringValue( objs[0] ); 
                    }
                    if ( objs.length >= 2 ) {
                        this.filetype = getStringValue( objs[1] ); 
                    }
                    if ( objs.length >= 3 ) {
                        Number num = getNumberValue( objs[2] ); 
                        this.filesize = (num == null? 0 : num.longValue()); 
                    }
                    if ( objs.length >= 4 ) {
                        Number num = getNumberValue( objs[3] ); 
                        this.chunkcount = (num == null? 0 : num.intValue()); 
                    } 
                }
            } 
            return this; 
        }  
        
        private String getStringValue( Object value ) {
            if ( value instanceof Object[] ) { 
                Object[] objs = (Object[]) value; 
                if ( objs.length >= 2 ) { 
                    Object o = objs[1]; 
                    return (o == null? null: o.toString()); 
                } 
            } 
            return null; 
        } 
        private Number getNumberValue( Object value ) {
            if ( value instanceof Object[] ) { 
                Object[] objs = (Object[]) value; 
                if ( objs.length >= 2 ) { 
                    Object o = objs[1]; 
                    if ( o instanceof Number ) {
                        return (Number)o; 
                    } 
                } 
            } 
            return null; 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" IndexFile ">    
    
    public class IndexFile { 
        
        private File file; 

        private long datasize;
        private int datacount; 
        
        IndexFile( File basedir ) {
            this.file = new File( basedir, "index" ); 
        } 
        
        public long getDataSize() { return datasize; }
        public void setDataSize( long datasize ) {
            this.datasize = datasize; 
        }
        
        public int getDataCount() { return datacount; } 
        public void setDataCount( int datacount ) {
            this.datacount = datacount; 
        }
        
        void remove() {
            try {
                this.file.delete(); 
            } catch(Throwable t) {;}  
        }
        
        public void update() {
            FileOutputStream fos = null; 
            ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream( file ); 
                oos = new ObjectOutputStream( fos );
                oos.writeObject(new Object[]{ 
                    new Object[]{ "datasize", datasize },
                    new Object[]{ "datacount", datacount } 
                }); 
            } catch( Throwable t ) { 
                throw new RuntimeException("failed to write index file caused by " + t.getMessage()); 
            } finally {
                try { oos.close(); }catch(Throwable t){;} 
                try { fos.close(); }catch(Throwable t){;} 
            } 
        }
        
        public IndexFile load() { 
            this.datasize = 0; 
            this.datacount = 0; 
            
            if ( file.exists() ) {
                Object result = null; 
                FileInputStream fis = null; 
                ObjectInputStream ois = null; 
                try {
                    fis = new FileInputStream( file ); 
                    ois = new ObjectInputStream( fis ); 
                    result = ois.readObject(); 
                } catch( Throwable t ) { 
                    throw new RuntimeException("failed to load meta file caused by " + t.getMessage()); 
                } finally {
                    try { ois.close(); }catch(Throwable t){;} 
                    try { fis.close(); }catch(Throwable t){;} 
                }    
                
                if ( result instanceof Object[] ) {
                    Object[] objs = (Object[])result; 
                    if ( objs.length >= 1 ) {
                        Number num = getNumberValue( objs[0] ); 
                        this.datasize = (num == null? 0 : num.longValue()); 
                    }
                    if ( objs.length >= 2 ) {
                        Number num = getNumberValue( objs[1] ); 
                        this.datacount = (num == null? 0 : num.intValue()); 
                    } 
                }            
            } 
            return this; 
        }  
        
        private String getStringValue( Object value ) {
            if ( value instanceof Object[] ) { 
                Object[] objs = (Object[]) value; 
                if ( objs.length >= 2 ) { 
                    Object o = objs[1]; 
                    return (o == null? null: o.toString()); 
                } 
            } 
            return null; 
        } 
        private Number getNumberValue( Object value ) {
            if ( value instanceof Object[] ) { 
                Object[] objs = (Object[]) value; 
                if ( objs.length >= 2 ) { 
                    Object o = objs[1]; 
                    if ( o instanceof Number ) {
                        return (Number)o; 
                    } 
                } 
            } 
            return null; 
        } 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" DataFile "> 
    
    public class DataFile {
        
        private File file; 
        
        DataFile( File file ) {
            this.file = file; 
        }
        
        public boolean exist() { 
            return file.exists(); 
        } 
        
        public int write( byte[] bytes ) { 
            FileOutputStream fos = null; 
            try {
                fos = new FileOutputStream( file ); 
                fos.write( bytes ); 
                fos.flush(); 
                return bytes.length; 
            } catch( Throwable t ) { 
                throw new RuntimeException("failed to write file caused by " + t.getMessage()); 
            } finally { 
                try { fos.close(); }catch(Throwable t){;} 
            } 
        }
        
        public byte[] read() { 
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
            try { 
                read( baos ); 
                return baos.toByteArray(); 
            } catch(Throwable t) {
                throw new RuntimeException("failed to read file caused by " + t.getMessage()); 
            } finally {
                try { baos.close(); }catch(Throwable t){;} 
            } 
        } 
        
        void read( OutputStream out ) { 
            if ( !file.exists() ) {
                //exit if does not exist 
                return;
            } 
            
            int read = -1; 
            byte[] bytes = new byte[ CHUNK_SIZE ];
            FileInputStream fis = null; 
            try {
                fis = new FileInputStream( file ); 
                while((read=fis.read(bytes)) != -1) {
                    if ( read == bytes.length ) {
                        out.write( bytes ); 
                    } else { 
                        out.write( bytes, 0, read ); 
                    } 
                }
            } catch(Throwable t) {
                throw new RuntimeException("failed to read file caused by " + t.getMessage()); 
            } finally { 
                try { fis.close(); }catch(Throwable t){;} 
                
                bytes = null; 
            } 
        }
        
        public long getSize() { 
            RandomAccessFile raf = null; 
            FileChannel channel = null; 
            try {
                if ( !file.exists() ) return -1;
                
                raf = new RandomAccessFile( file, "r" ); 
                channel = raf.getChannel(); 
                return channel.size(); 
            } catch(Throwable t) { 
                return -1; 
            } finally { 
                try { channel.close(); } catch(Throwable t){;} 
                try { raf.close(); }catch(Throwable t){;}  
            } 
        }  
    } 

    // </editor-fold>

}
