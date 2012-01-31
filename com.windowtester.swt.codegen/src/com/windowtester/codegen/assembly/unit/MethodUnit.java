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

import com.windowtester.codegen.assembly.block.CodeBlock;

/**
 * A method code unit.
 */
public class MethodUnit extends MemberUnit {
    
	/** This method's parameters */
	private ParameterList _parameters = new ParameterList();
	/** This method's return type */
	private String _returnType = "void"; //default
	/** This method's return type */
	private ThrowsList _throwsList = new ThrowsList();
	/** A flag to indicate if this method a constructor */
	private boolean _isCons;
	
    /**
     * Create an instance.
     */
    public MethodUnit(String name, String body) {
        this(name, new CodeBlock(body));
    }

    /**
     * Create an instance.
     */
    public MethodUnit(String name, CodeBlock body) {
        super(name, body);
    }    
    
    /**
     * Create an instance.
     * @param name
     */
    public MethodUnit(String name) {
       super(name);
    }


    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.unit.MemberUnit#getBody()
     */
    public CodeBlock getBody() {
    	
    	//build return snippet
    	//constructors have no return type...
    	String returnType = isConstructor() ? "" : getReturnType() + ' ';
    	
    	//build modifiers snippet
    	ModifierList modifiers = getModifiers();
    	String modString = modifiers.isEmpty() ? "" : modifiers.toString() + ' ';
    
    	//put it all together...
    	StringBuffer sb = new StringBuffer();
    	String comment = getComment();
    	if (comment != null)
    		sb.append(comment).append(NEW_LINE);
    	
        sb.append(modString).append(returnType).
		   append(getName()).append('(').append(_parameters).append(')').
		   append(_throwsList).append(" {").append(NEW_LINE);
        
        CodeBlock superBody = getMethodBodyContents();
        if (superBody != null)
        	sb.append(superBody.toString());
        
        sb.append("}").append(NEW_LINE);
        return new CodeBlock(sb.toString());
    }
    
    
    public CodeBlock getMethodBodyContents() {
    	return super.getBody();
    }
    
    /**
     * Add a parameter to this member's list of parameters.
     * @param param - the parameter to add
     * @return true if this addition changed the list of parameters
     */
    public boolean addParameter(Parameter param) {
    	if (_parameters == null)
    		_parameters = new ParameterList();
    	return _parameters.add(param);
    }

	/**
	 * Set a return type for this method (as a String)
	 * @param returnType - the return type for this method
	 */
	public void setReturnType(String returnType) {
		_returnType = returnType;
	}
    
	/**
	 * Get this method's return type.
	 * @return Returns this method's return type.
	 */
	public String getReturnType() {
		return _returnType;
	}

	/**
	 * Add a throws clause to this method's list of exceptions.
	 * @param exception - an exception to add
	 */
	public void addThrows(String exception) {
		_throwsList.add(exception);
	}

	/**
	 * Set whether this method is a constructor method.
	 * @param isCons - whether this method is a constructor
	 */
	public void setConstructor(boolean isCons) {
		_isCons = isCons;
	}
 
	/**
	 * Return whether this method is a constructor.
	 * @return true if this method is a constructor
	 */
	public boolean isConstructor() {
		return _isCons;
	}
	
	
}
