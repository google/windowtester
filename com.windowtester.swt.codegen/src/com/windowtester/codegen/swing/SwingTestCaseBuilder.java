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

import java.awt.event.KeyEvent;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.TestCaseBuilder;
import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.UITestCaseSwing;

/**
 * Test case builder for Swing applications 
 */
public class SwingTestCaseBuilder extends TestCaseBuilder {
	
	
	private static final String TESTCASE_BASE_CLASS = "UITestCaseSwing";

	private static final String[] DEFAULT_IMPORTS = new String[] {
		UITestCaseSwing.class.getName(),
		IUIContext.class.getName()
	};
	
	
	private final String _mainSwingType;
	private final String[] _progArgs;
	
	public SwingTestCaseBuilder(String name, String pkg, ExecutionProfile profile) {		
		this(name, pkg, profile.getMainSwtClassName(), profile.getProgramArgs());
	}
	
	public SwingTestCaseBuilder(String name, String pkg, String mainSwingType, String [] progArgs) {		
		super(name, pkg);
		_mainSwingType = mainSwingType;
		_progArgs    = progArgs;
		setExtends(TESTCASE_BASE_CLASS);
	}

	/*
	 * Override SWT defaults.
	 *
	 * @see com.windowtester.codegen.TestCaseBuilder#addBoilerPlateImports()
	 */
	protected void addBoilerPlateImports() {
		for (int i = 0; i < DEFAULT_IMPORTS.length; i++) {
			addImport(new ImportUnit(DEFAULT_IMPORTS[i]));
		}
	}
	
    /**
	 * Get the boiler plate test constructor.
	 * 
	 * @see com.windowtester.codegen.TestCaseBuilder#getConsMethod()
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
		sb.append("super(").append(_mainSwingType).append(".class");
		if (_progArgs != null && _progArgs.length != 0)
			sb.append(", "). append(stringify(_progArgs));
		sb.append(");").append(NEW_LINE);
		
    	CodeBlock block = new CodeBlock(sb.toString());
        m.setBody(block);
		return m;
    }
	
    /*
     * @see com.windowtester.codegen.TestCaseBuilder#getUIContextTypeName()
     */
    public String getUIContextTypeName() {
    	return "IUIContext";
    }
    
   
    /*
     * @see com.windowtester.codegen.ISourceTypeBuilder#getExtends()
     */
    public String getExtends() {
    	return "UITestCaseSwing";
    }
    
    /**
     * @see com.windowtester.codegen.TestCaseBuilder#getUIContextGetterName()
     */
    public String getUIContextGetterName() {
    	return "getUI()";
    }
    
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ITestCaseBuilder#parseControlKey(com.windowtester.recorder.event.user.SemanticKeyDownEvent)
     */
    public String parseControlKey(SemanticKeyDownEvent kde) {
    	
    	String key = kde.getKey();
    	if (isTab(key))
    		return  "KeyEvent.VK_TAB";  //"SWT.TAB";
    	if (isEnter(key))
    		return "KeyEvent.VK_ENTER"; //"SWT.CR";
    	
    	switch(kde.getKeyCode()) {
    		case KeyEvent.VK_RIGHT :
    			return "KeyEvent.VK_RIGHT";
    		case KeyEvent.VK_LEFT :
    			return "KeyEvent.VK_LEFT";
    		case KeyEvent.VK_UP :
    			return "KeyEvent.VK_UP";
    		case KeyEvent.VK_DOWN :
    			return "KeyEvent.VK_DOWN";
    			
    		default :
    			return null;
    	}
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.TestCaseBuilder#getControlKey()
     */
    public String getControlKey() {
    	return "KeyEvent.VK_CONTROL";
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
