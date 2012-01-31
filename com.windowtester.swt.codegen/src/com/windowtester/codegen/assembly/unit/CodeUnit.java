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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.windowtester.codegen.assembly.ICodeRequirement;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;

/**
 * A base class for basic code units.
 */
public class CodeUnit implements ICodeUnit, ICodeGenConstants {

	/** This code unit's javadoc */
	private String _comment;
	
    /** This code unit's requirements */
    private List _requirements;
    
    /**
     * Set documentation (e.g., javadoc comment) for this unit.
	 * @param doc - documentation for this unit
	 */
	public void setComment(String doc) {
		_comment = doc;
	}
    
	/**
	 * Get this unit's documentation.
	 * @return Returns the unit's documentation.
	 */
	public String getComment() {
		return _comment;
	}
	
	
    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.ICodeRequirement#getRequirements()
     */
    public List getRequirements() {
        if (_requirements == null)
            _requirements = new ArrayList();
        return _requirements;
    }

    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.unit.ICodeUnit#addRequirement(com.windowtester.codegen.assembly.ICodeRequirement)
     */
    public boolean addRequirement(ICodeRequirement requirement) {
        return getRequirements().add(requirement);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.unit.ICodeUnit#addRequirements(java.util.Collection)
     */
    public boolean addRequirements(Collection requirements) {
        return _requirements.addAll(requirements);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.unit.ICodeUnit#removeRequirement(com.windowtester.codegen.assembly.ICodeRequirement)
     */
    public boolean removeRequirement(ICodeRequirement requirement) {
        return getRequirements().remove(requirement);
    }
}
