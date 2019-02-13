/*
 * CustomFieldValidator.java
 *
 * Created on August 12, 2010, 2:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import com.rameses.osiris3.persistence.ValidationResult;

/**
 *
 * @author elmo
 */
public interface SimpleFieldValidator {
    void validate(ValidationResult result, SimpleField field, Class fieldClass, String refname, Object fieldValue );
}
