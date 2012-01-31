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

import java.util.Collection;
import java.util.List;

import com.windowtester.codegen.ISourceTypeBuilder;

/**
 * Code requirements describe units of code that are required to be present in an
 * assembled type
 */
public interface ICodeRequirement {

    /**
     * Requirements are composites, and may themselves have requirements.
     * 
     * @return - this requirement's set of (pre)requirements
     */
    List/*<ICodeRequirement>*/ getRequirements();
    
    /**
     * Ask the builder if the type under construction satisfies this requirement.
     * @param builder - the builder
     * @return true if this requirement is satisfied, false otherwise
     */
    boolean isSatisfied(ISourceTypeBuilder builder);
    
    /**
     * Perform whatever actions on the builder's underlying type are needed to satsify
     * the requirement.
     * @param builder - the builder containing the type under construction
     */
    void satisfy(ISourceTypeBuilder builder);
    
    /**
     * Add a (pre)requirement to this requirement.
     * @param requirement - the requirement to add
     * @return true if the requires list changed as a result of the call
     */
    boolean addRequirement(ICodeRequirement requirement);
    
    /**
     * Add a collection of (pre)requirements to this requirement.
     * @param requirements - the requirement to add
     * @return true if the requires list changed as a result of the call
     */
    boolean addRequirements(Collection requirements);
    
    
    /**
     * Remove a (pre)requirement from this requirement.
     * @param requirement - the requirement to remove
     * @return true if the requires list changed as a result of the call
     */
    boolean removeRequirement(ICodeRequirement requirement);
    
}
