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

import com.windowtester.codegen.assembly.RequiresImport;
import com.windowtester.codegen.assembly.block.CodeBlock;

/**
 * A base class for member units.
 */
public class MemberUnit extends CodeUnit {

    /** The member name */
    private String _name;
    /** The member's modifiers */
    private ModifierList _modifiers;
    /** The member body */
    private CodeBlock _body;
    
    protected MemberUnit() {}
    
    /**
     * Create an instance.
     * @param name
     */
    public MemberUnit(String name) {
        _name = name;
    }
    
    /**
     * Create an instance.
     * @param name
     * @param body
     */
    public MemberUnit(String name, CodeBlock body) {
        this(name);
        _body = body;
    }

    /**
     * Set the member's name.
     * @param name - the name to set.
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * @return the member's name
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Set the body
     * @param body - the body to set.
     */
    public void setBody(CodeBlock body) {
        _body = body;
    }
    
    /**
     * @return the member's body
     */
    public CodeBlock getBody() {
        return _body;
    }    
    
    
    /**
     * Add the given import to this units list of requirements.
     * @param importName - the import name to be added
     * @return true if the list of requires was changed
     */
    public boolean addRequiredImport(String importName) {
        return addRequirement(new RequiresImport(new ImportUnit(importName)));
    }
    
    /**
     * Add a modifier to this member's list of modifiers.
     * @param mod - the modifier to add
     * @return true if this addition changed the list of modifiers
     */
    public boolean addModifier(Modifier mod) {
    	return getModifiers().add(mod);
    }

	/**
	 * Get this members modifiers.
	 * @return the list of modifiers for this member
	 */
	public ModifierList getModifiers() {
    	if (_modifiers == null)
    		_modifiers = new ModifierList();
		return _modifiers;
	}
    
}
