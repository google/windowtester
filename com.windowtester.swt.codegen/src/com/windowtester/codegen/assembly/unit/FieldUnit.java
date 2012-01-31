/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.codegen.assembly.unit;

/**
 * A field unit.
 */
public class FieldUnit extends MemberUnit {
    
    /** This field's type */
    private String _type;
    
    /**
     * Create an instance.
     * @param type - the field's type name 
     * @param name - the field name
     */
    public FieldUnit(String type, String name) {
        super(name);
        _type = type;
    }

    /**
     * @return Returns the field's type.
     */
    public String getType() {
        return _type;
    }
    
    
}
