/*
 * TemplateBuilder.java
 *
 * Created on February 7, 2013, 8:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.templates;

import java.io.InputStream;

/**
 *
 * @author Elmo
 */
public interface TemplateBuilder {
    String getExtension();
    Template build(InputStream is) throws Exception;
}
