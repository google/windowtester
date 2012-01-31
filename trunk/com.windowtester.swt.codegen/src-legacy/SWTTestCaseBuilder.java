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
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;

/**
 * Test case builder for SWT application 
 */
public class SWTTestCaseBuilder extends TestCaseBuilder {
	
	private final String _mainSwtType;
	private final String[] _progArgs;
	
	public SWTTestCaseBuilder(String name, String pkg, ExecutionProfile profile) {		
		this(name, pkg, profile.getMainSwtClassName(), profile.getProgramArgs());
	}
	
	public SWTTestCaseBuilder(String name, String pkg, String mainSwtType, String [] progArgs) {		
		super(name, pkg);
		_mainSwtType = mainSwtType;
		_progArgs    = progArgs;
	}

	
	
    /**
     * Get the boiler plate test constructor.
     * @return method for the boilerplate constructor
     */
    protected MethodUnit getConsMethod() {
    	
		MethodUnit m = new MethodUnit(getName()); 
		StringBuffer com = new StringBuffer();
		com.append("/**").append(NEW_LINE).append("* Create an Instance").append(NEW_LINE).
			append(" */");
		m.setComment(com.toString());

		m.setConstructor(true);
		m.addModifier(Modifier.PUBLIC);
		//m.addParameter(new Parameter("String", "testName"));
		
		StringBuffer sb = new StringBuffer();
		sb.append("super(").append(_mainSwtType).append(".class");
		if (_progArgs != null && _progArgs.length != 0)
			sb.append(", "). append(stringify(_progArgs));
		sb.append(");").append(NEW_LINE);
		
    	CodeBlock block = new CodeBlock(sb.toString());
        m.setBody(block);
		return m;
    }
	
	
	private static String stringify(String[] progArgs) {
		StringBuffer sb = new StringBuffer();
		sb.append("new String[]{");
		for (int i = 0; i < progArgs.length; i++) {
			//ignore ","s
			if (progArgs[i].trim().equals(","))
				continue;
			//ignore empty args
			if (progArgs[i].trim().equals(""))
				continue;
			sb.append("\"").append(progArgs[i]).append("\"");
			if (i+1 < progArgs.length)
				sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}
	
}
