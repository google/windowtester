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
package com.windowtester.codegen;

import java.util.Collection;

import org.eclipse.jdt.core.IType;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ClassUnit;
import com.windowtester.codegen.assembly.unit.FieldUnit;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;

/**
 * A builder for Java source types.
 */
public interface ISourceTypeBuilder extends ICodeGenConstants {

		
    /**
     * Add this source block to the "current method unit" in the type under construction.
     * @param unit - the unit to add
     */
    void add(CodeBlock unit);
    
    /**
     * Perform assembly of the type.
     * @return - the assembled type
     */
    IType assemble();

    /**
     * Ask if the type under construction contains the given method.
     * @param method - the method in question
     * @return true if the method is contained, false otherwise
     */
    boolean hasMethod(MethodUnit method);
    
    /**
     * Add this method to the type under construction.
     * @param method - the method to add
     * @return true if the method list changed as a result of the call
     */
    boolean addMethod(MethodUnit method);
    
    /**
     * Ask if the type under construction contains the given field.
     * @param field - the field in question
     * @return true if the field is contained, false otherwise
     */
    boolean hasField(FieldUnit field);
    
    /**
     * Add this field to the type under construction.
     * @param field - the field to add
     * @return true if the field list changed as a result of the call
     */
    boolean addField(FieldUnit field);
    
    /**
     * Ask if the type under construction contains the given import.
     * @param imprt - the import in question
     * @return true if the import is contained, false otherwise
     */
    boolean hasImport(ImportUnit imprt);
    
    /**
     * Add this import to the type under construction.
     * @param imprt - the import to add
     * @return true if the import list changed as a result of the call
     */
    boolean addImport(ImportUnit imprt);

    /**
     * Add this class to the type under construction.
     * @param cls - the import to add
     * @return true if the class list changed as a result of the call
     */
    boolean addClass(ClassUnit cls);    
    
    /**
     * @return the currently defined inner classes
     */
    Collection getClasses();
    
    /**
     * Ask if the type under construction contains the given import.
     * @param cls - the class in question
     * @return true if the class is contained, false otherwise
     */
    boolean hasClass(ClassUnit cls);
    
    /**
     * Get the block currently under construction.
     * @return the current code block
     */
    CodeBlock getCurrentBlock();
    
    /**
     * Get the title of the current active shell.
     * @return the current shell's title
     */
    String getCurrentShellTitle();
    
    
    /**
     * Set the current shell title and signal if it has changed.
     * @param shellTitle - the title to set
     * @return true if the title has changed
     * TODO: this state should be encapsulated somewhere else (a BuilderState object?)
     */
    boolean updateCurrentShellTitle(String shellTitle);
    
    
	/**
	 * @return this test case's methods
	 */
	Collection getMethods();

	/**
	 * @return this test case's fields
	 */
	Collection getFields();

	/**
	 * @return this test case's extends clause
	 */
	String getExtends();

	/**
	 * @return this test case's declared package
	 */
	String getPackage();

	/**
	 * @return this test case's imports
	 */
	Collection getImports();

	/**
	 * @return this test case's name
	 */
	String getName();

    
}
