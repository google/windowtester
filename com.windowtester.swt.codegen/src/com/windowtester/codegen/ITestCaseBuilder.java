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

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;

public interface ITestCaseBuilder extends ISourceTypeBuilder {

	/**
	 * Do any initialization that needs to happen at the beginning of a codegen operation.
	 */
	void prime();

	/**
	 * Build the test.
	 * @return a String representing the generated test case.
	 */
	String build();

	/**
	 * Get a fresh variable name (based on the given prefix) in the current scope.
	 * @param prefix - the variable prefix (e.g. "var" -> "var1")
	 * @return the fresh variable
	 */
	String getFreshVariable(String string);

	/**
	 * Get a fresh (e.g. unused) method name based on this prefix.
	 * @param prefix - the method prefix (e.g. "method" -> "method1")
	 * @return the fresh method name
	 */
	String getFreshMethod(String prefix);

	/**
	 * Add this block to the current method.
	 * @param block - the block to add
	 */
	void add(CodeBlock block);

	/**
	 * Get the block currently under construction.
	 * @return the current code block
	 */
	CodeBlock getCurrentBlock();

	/**
	 * Set the current root block.
	 * @param block - the new current root block
	 */
	void setCurrentRoot(CodeBlock block);

	
//	/**
//	 * Get the associated mapper instance.
//	 * @return mapper - the associated mapper
//	 */
//	WidgetMapper getMapper();

	/**
	 * Get the name of the UIContext instance.
	 */
	String getUIContextInstanceName();

	/**
	 * Get an import to ensure Key Events are resolved.
	 */
	ImportUnit getKeyEventImport();

	String parseControlKey(SemanticKeyDownEvent kde);

	String getControlKey();

	ISetupHandler[] getSetupHandlers();


}
