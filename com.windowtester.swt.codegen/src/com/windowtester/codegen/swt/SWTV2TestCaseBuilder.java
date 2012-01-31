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
import com.windowtester.codegen.TestCaseBuilder;
import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;
import com.windowtester.codegen.generator.CodegenSettings;
import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.UITestCaseSWT;

/**
 * Test case builder for new API SWT applications 
 */
public class SWTV2TestCaseBuilder extends TestCaseBuilder {
	
	
	private final String[] DEFAULT_IMPORTS = new String[] {
		IUIContext.class.getName()
	};

	private static final Class<?> DEFAULT_BASETEST = UITestCaseSWT.class;
	
	private final String mainType;
	private final String[] progArgs;

	private final ClassName baseClass;

	private CodegenSettings settings = CodegenSettings.forUnknown(); //null object default
	
	
	public SWTV2TestCaseBuilder(String name, String pkg, String mainType, String [] progArgs) {		
		this(name, pkg, mainType, progArgs, ExecutionProfile.UNKNOWN_EXEC_TYPE);
	}

	public SWTV2TestCaseBuilder(String name, String pkg, String mainType, String [] progArgs, int execType) {
		this(name, pkg, mainType, ClassName.forClass(DEFAULT_BASETEST), progArgs, execType);
	}
	
	public SWTV2TestCaseBuilder withSettings(CodegenSettings settings) {
		this.settings = settings;
		return this;
	}

	public SWTV2TestCaseBuilder(String name, String pkg, String mainType, ClassName baseClassName, String [] progArgs, int execType) {
		super(name, pkg);
		this.mainType = mainType;
		this.baseClass = baseClassName;
		this.progArgs    = progArgs;
		setExtends(baseClass.getClassName());
		//we add this here to ensure that this import appears first
		addBaseTestImport();
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseBuilder#getSetupHandlers()
	 */
	public ISetupHandler[] getSetupHandlers() {
		return settings.handlers().toArray();
	}


	/*
	 * Override SWT defaults.
	 *
	 * @see com.windowtester.codegen.TestCaseBuilder#addBoilerPlateImports()
	 */
	protected void addBoilerPlateImports() {
		addDefaultImports();
	}

	private void addDefaultImports() {
		for (int i = 0; i < DEFAULT_IMPORTS.length; i++) {
			addImport(new ImportUnit(DEFAULT_IMPORTS[i]));
		}
	}
	
	private void addBaseTestImport() {
		addImport(new ImportUnit(baseClass.getQualifiedClassName()));
	}

	/**
	 * Get the boiler plate test constructor.
	 * 
	 * @see com.windowtester.codegen.TestCaseBuilder#getConsMethod()
     */
    protected MethodUnit getConsMethod() {
    	
    	//if main class is null, return null indicating not to create a constructor
    	if (mainType == null)
    		return null;
    	
		MethodUnit m = new MethodUnit(getName()); 
		StringBuffer com = new StringBuffer();
		com.append("/**").append(NEW_LINE).append("* Create an Instance").append(NEW_LINE).
			append(" */");
		m.setComment(com.toString());

		m.setConstructor(true);
		m.addModifier(Modifier.PUBLIC);
		//m.addParameter(new Parameter("String", "testName"));
		
		StringBuffer sb = new StringBuffer();
		sb.append("super(").append(mainType).append(".class");
		if (progArgs != null && progArgs.length != 0)
			sb.append(", "). append(stringify(progArgs));
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
    
       
    /**
     * @see com.windowtester.codegen.TestCaseBuilder#getUIContextGetterName()
     */
    public String getUIContextGetterName() {
    	return "getUI()";
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
