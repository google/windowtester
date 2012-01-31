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
package com.windowtester.codegen.assembly;

import com.windowtester.codegen.ISourceTypeBuilder;
import com.windowtester.codegen.assembly.unit.FieldUnit;


public class RequiresField extends CodeRequirement {

    /** The required field*/
    private FieldUnit _field;

    /**
     * Create an instance.
     * @param displayField
     */
    public RequiresField(FieldUnit field) {
        _field = field;
    }

    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.CodeRequirement#doSatisfy(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public void doSatisfy(ISourceTypeBuilder builder) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.ICodeRequirement#isSatisfied(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public boolean isSatisfied(ISourceTypeBuilder builder) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @return the required field
     */
    public FieldUnit getField() {
        return _field;
    }
    

}
