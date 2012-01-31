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

import java.util.Iterator;

import com.windowtester.codegen.ISourceTypeBuilder;
import com.windowtester.codegen.assembly.unit.CodeUnit;

/**
 * A base class for simple code requirements
 */
public abstract class CodeRequirement extends CodeUnit implements ICodeRequirement {

    /*
     * @see com.windowtester.codegen.assembly.ICodeRequirement#satisfy(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public final void satisfy(ISourceTypeBuilder builder) {
        //check pre-reqs first:
        for (Iterator iter = getRequirements().iterator(); iter.hasNext();) {
            ICodeRequirement req = (ICodeRequirement) iter.next();
            if (!req.isSatisfied(builder))
                req.satisfy(builder);
        }
        //call hook methods:
        if (!isSatisfied(builder))
            doSatisfy(builder);
    }

    /**
     * Satisfy this requirement with the knowledge that the pre-reqs have already been satisfied,
     * and that this requirement is indeed required (e.g., isSatisfied(..) == false)
     * This is a hook method.
     * @param builder - the builder with the type under construction
     */
    public abstract void doSatisfy(ISourceTypeBuilder builder);
 
}
