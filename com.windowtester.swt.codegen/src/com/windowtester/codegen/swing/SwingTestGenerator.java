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
package com.windowtester.codegen.swing;

import com.windowtester.codegen.ICodeGenerator;
import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.codegen.generator.NewAPICodeBlockBuilder;
import com.windowtester.codegen.generator.PluggableCodeGenerator;

/**
 * A test generator for Swing Test Cases.
 */
public class SwingTestGenerator implements ICodeGenerator {

	
	ICodeGenerator _generator;
	
	public SwingTestGenerator(String name, String pkg, String mainClassName, String [] progArgs) {
		SwingTestCaseBuilder builder = new SwingTestCaseBuilder(name, pkg,mainClassName, progArgs);
		_generator = new PluggableCodeGenerator(builder, new NewAPICodeBlockBuilder(builder));
	}

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.ICodeGenerator#generate(com.windowtester.codegen.eventstream.IEventStream)
	 */
	public String generate(IEventStream stream) {
		return _generator.generate(stream);
	}

}
