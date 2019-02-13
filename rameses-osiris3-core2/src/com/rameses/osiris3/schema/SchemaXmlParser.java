/*
 * XmlSchemaParser.java
 *
 * Created on August 12, 2010, 1:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import com.rameses.util.ParserUtil;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author elmo
 */
public class SchemaXmlParser extends DefaultHandler{
    
    private SchemaElement currentElement;
    private Schema schema; 
    private SchemaManager schemaManager;
    private IRelationalField currentRelationalField;
    private Relation currentRelation;
    
    /** Creates a new instance of XmlSchemaParser */
    public SchemaXmlParser(SchemaManager sm) {
        schemaManager = sm;
    }
    
    public Schema parse(InputStream is, String name ) throws Exception {
        schema = new Schema(name,schemaManager);
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse( is, this );
        return schema;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName.equals("schema")) {
            ParserUtil.loadAttributes(schema,schema.getProperties(), attributes);
        }
        else if(qName.equals("element")) {
            currentElement = new SchemaElement(schema);
            ParserUtil.loadAttributes(currentElement,currentElement.getProperties(), attributes);
            schema.addElement(currentElement);
        }
        else if(qName.equals("field")) {
            SimpleField field = new SimpleField();
            ParserUtil.loadAttributes(field,field.getProperties(), attributes);
            currentElement.addSchemaField(field);
        }
        else if(qName.equals("complex")) {
            ComplexField field = new ComplexField();
            ParserUtil.loadAttributes(field,field.getProperties(), attributes);
            currentElement.addSchemaField(field);
            currentRelationalField = field;
        }
        else if(qName.equals("relation")) {
            currentRelation = new Relation(schema.getName());
            ParserUtil.loadAttributes( currentRelation, null, attributes );
            schema.addRelation( currentRelation );
            /*
            RelationKey rk = new RelationKey();
            ParserUtil.loadAttributes(rk,null, attributes);
            if(currentRelationalField!=null) {
                currentRelationalField.getRelationKeys().add(rk);
            }
             */
        }
        else if(qName.equals("key")) {
            RelationKey rk = new RelationKey();
            ParserUtil.loadAttributes( rk, null, attributes );
            if(currentRelation!=null) {
                currentRelation.addKey( rk );    
            }
            else {
                currentRelationalField.addKey(rk);
            }
        }
        else {
            if(!qName.equals("schema")) System.out.println("schema " + qName + " is not supported"); 
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals("element")) {
            currentElement = null;
        }
        else if(qName.equals("relation")) {
            currentRelation = null;
        }
    }
    
    
}

