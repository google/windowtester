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
import com.windowtester.codegen.assembly.unit.ImportUnit;

/**
 * A method requirement.
 */
public class RequiresImport extends CodeRequirement {
    
    /** The required import */
    private ImportUnit _import;
    
    /**
     * Create an instance.
     * 
     */
    public RequiresImport(ImportUnit imprt) {
        _import = imprt;
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.CodeRequirement#doSatisfy(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public void doSatisfy(ISourceTypeBuilder builder) {
        builder.addImport(_import);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.ICodeRequirement#isSatisfied(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public boolean isSatisfied(ISourceTypeBuilder builder) {
        return builder.hasImport(_import);
    }
    
    
}
