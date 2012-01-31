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

import java.util.Collection;

import com.windowtester.codegen.assembly.ICodeRequirement;


/**
 * Code units are pieces of code to be added to a Source type.
 */
public interface ICodeUnit {

    /**
     * Add a (pre)requirement to this code unit.
     * @param requirement - the requirement to add
     * @return true if the requires list changed as a result of the call
     */
    boolean addRequirement(ICodeRequirement requirement);
    
    /**
     * Add a collection of (pre)requirements to this code unit.
     * @param requirements - the requirement to add
     * @return true if the requires list changed as a result of the call
     */
    boolean addRequirements(Collection requirements);
    
    
    /**
     * Remove a (pre)requirement from this code unit.
     * @param requirement - the requirement to remove
     * @return true if the requires list changed as a result of the call
     */
    boolean removeRequirement(ICodeRequirement requirement);
}
