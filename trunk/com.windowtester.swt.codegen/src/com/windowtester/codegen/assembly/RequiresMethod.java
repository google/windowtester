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
import com.windowtester.codegen.assembly.unit.MethodUnit;

/**
 * A method requirement.
 */
public class RequiresMethod extends CodeRequirement {
    
    /** The required method */
    private MethodUnit _method;
    
    /**
     * Create an instance.
     * 
     */
    public RequiresMethod(MethodUnit method) {
        _method = method;
        addRequirements(method.getRequirements());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.CodeRequirement#doSatisfy(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public void doSatisfy(ISourceTypeBuilder builder) {
        builder.addMethod(_method);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.ICodeRequirement#isSatisfied(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public boolean isSatisfied(ISourceTypeBuilder builder) {
        return builder.hasMethod(_method);
    }
    
    
}
