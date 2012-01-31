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

import com.windowtester.codegen.generator.CodegenSettings;
import com.windowtester.codegen.swing.SwingTestGenerator;
import com.windowtester.codegen.swt.SWTV2TestGenerator;

/**
 * A factory for producing CodeGenerator instances.
 */
public class CodeGeneratorFactory {

//	/**
//	 * Create a code generator instance
//	 * @param builder - the source builder
//	 * @return the code generator instance
//	 */
//	public static ICodeGenerator getGenerator(ITestCaseBuilder builder) {
//		//TODO[author=pq] there should be some switch logic here
//		return new CodeGenerator(builder);
//	}
//	/**
//	 * Create a code generator instance
//	 * @param typeName - the name of the test to generate
//	 * @param packageName - the package name for the test type
//	 * @return the code generator instance
//	 */
//	public static ICodeGenerator getGenerator(String typeName, String packageName) {
//		return getGenerator(new TestCaseBuilder(typeName, packageName));
//	}
	/**
	 * Create a code generator instance
	 * @param typeName - the name of the test to generate
	 * @param packageName - the package name for the test type
	 * @param codegenSettings an execution profile that this code generator must support
	 * @return the code generator instance
	 */
	public static ICodeGenerator getGenerator(String typeName, String packageName, String baseClass, CodegenSettings codegenSettings) {
		ExecutionProfile profile = codegenSettings.profile();
		if (profile.getExecType() == ExecutionProfile.SWING_EXEC_TYPE) //TODO: clean up this constructor
			return new SwingTestGenerator(typeName, packageName, profile.getMainSwtClassName(), profile.getProgramArgs());
		
//		//first check for a v2 generator
//		int apiVersion = PlaybackSettingsFactory.getPlaybackSettings().getRuntimeAPIVersion();	
//		if (apiVersion == 2)
			return new SWTV2TestGenerator(typeName, packageName, baseClass, codegenSettings);
	
//		TestCaseBuilder builder = null;	
//		if (profile.getExecType()==ExecutionProfile.SWT_EXEC_TYPE) {
//			builder = new SWTTestCaseBuilder(typeName, packageName, profile);
//		} else {
//			builder = new TestCaseBuilder(typeName, packageName);
//		}
//		return getGenerator(builder);
	}


}
