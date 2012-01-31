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
package com.windowtester.codegen.swt;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.ICodeGenerator;
import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.codegen.generator.CodegenSettings;
import com.windowtester.codegen.generator.NewAPICodeBlockBuilder;
import com.windowtester.codegen.generator.PluggableCodeGenerator;

/**
 * A test generator for SWT V2 Test Cases.
 *
 */
public class SWTV2TestGenerator implements ICodeGenerator {

	
	ICodeGenerator generator;

	
	public SWTV2TestGenerator(String name, String pkg, String mainClassName, String [] progArgs) {
		this(name, pkg, mainClassName, progArgs, ExecutionProfile.UNKNOWN_EXEC_TYPE);
	}

	public SWTV2TestGenerator(String name, String pkg,
			String mainClassName, String[] progArgs, int execType) {
		SWTV2TestCaseBuilder builder = new SWTV2TestCaseBuilder(name, pkg, mainClassName, progArgs, execType);
		generator = new PluggableCodeGenerator(builder, new NewAPICodeBlockBuilder(builder));
	}

	public SWTV2TestGenerator(String name, String pkg, 
			String mainClassName, String baseClassName, String[] progArgs, int execType) {
		SWTV2TestCaseBuilder builder = new SWTV2TestCaseBuilder(name, pkg, mainClassName, ClassName.forQualifiedName(baseClassName), progArgs, execType);
		generator = new PluggableCodeGenerator(builder, new NewAPICodeBlockBuilder(builder));
	}
	
	

	public SWTV2TestGenerator(String typeName, String packageName,String baseClassName, CodegenSettings settings) {
		ExecutionProfile profile = settings.profile();
		SWTV2TestCaseBuilder builder = new SWTV2TestCaseBuilder(typeName, packageName, profile.getMainSwtClassName(), ClassName.forQualifiedName(baseClassName), profile.getProgramArgs(), profile.getExecType()).withSettings(settings);
		generator = new PluggableCodeGenerator(builder, new NewAPICodeBlockBuilder(builder));
	
	}

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.ICodeGenerator#generate(com.windowtester.codegen.eventstream.IEventStream)
	 */
	public String generate(IEventStream stream) {
		return generator.generate(stream);
	}


}
