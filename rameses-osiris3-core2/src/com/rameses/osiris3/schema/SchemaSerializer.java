package com.rameses.osiris3.schema;


public interface SchemaSerializer {

    Object read(String s);
    String write(Object s);
    
}
