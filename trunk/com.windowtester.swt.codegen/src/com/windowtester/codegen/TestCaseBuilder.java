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

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.FieldUnit;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;
import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.codegen.util.FreshMethodNameFinder;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;

/**
 * A builder for WindowTester JUnit test cases.  
 */
public class TestCaseBuilder extends SourceTypeBuilder implements ITestCaseBuilder {
    
    /** The root test block */
    private CodeBlock _root = new CodeBlock("");
    
    /** A pointer to the current (active/last added) block */
    private CodeBlock _current;
    
    /** A Mapper instance */
//    private WidgetMapper mapper = new WidgetMapper();

    /** Initialization flag */
    protected boolean _initialized;

    //TODO: make this user configurable
	private String _uiContextInstanceName = "ui";

	private SourceStringBuilder _stringBuilder;

	//populated in subclasses
	protected ISetupHandler[] handlers = new ISetupHandler[]{};
    
    /**
     * Create an instance.
     * @param name - the name of the test case
     * @param pkg  - the package of the test case
     */
    public TestCaseBuilder(String name, String pkg) {
        super(name, pkg);
        _stringBuilder = new SourceStringBuilder(this);
    }

	protected SourceStringBuilder getStringBuilder() {
		return _stringBuilder;
	}
    
	////////////////////////////////////////////////////////////////////////////
	//
	// Initialization (boiler plate method creation)
	//
	////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ITestCaseBuilder#prime()
     */
    public void prime() {
    	//no-op: all done in init();
    }
    
    /**
     * Setup boiler plate imports, setUp method (if specified), testMethod, etc.
     */
    protected void init() {
        addBoilerPlateImports();
        //setExtends("UITestCase");
        //addHelperFields(); ... now in superclass
        addBoilerPlateMethods();
    }
        
    /**
     * Add basic boilerplate methods (setUp(), suite...)
     */
    protected void addBoilerPlateMethods() {
    	
    	addConstructor();
        
        //TODO: this should be a user pref...
        //addMethod(getSuiteMethod());
        
        //addMethod(getSetUpMethod());
        //addMethod(getTearDownMethod());
        addMethod(getMainMethod());
    }    

	/**
     * Add a constructor (if required).
     */
	protected void addConstructor() {
		MethodUnit consMethod = getConsMethod();
		if (consMethod != null)
			addMethod(consMethod); 
	}


	/**
     * Add helper fields (_uiContext)
     */
    protected void addHelperFields() {
    	String body = "/** The UIContext instance.*/" + NEW_LINE + "private IUIContext _uiContext;" + NEW_LINE;
        addHelperField("IUIContext", "_uiContext", body);
    }

    /**
     * Add a field.
     * @param type - the type of the field
     * @param name - the name of the field
     * @param body - the body of the field
     */
    private void addHelperField(String type, String name, String body) {
        FieldUnit field = new FieldUnit(type,name);
        field.setBody(new CodeBlock(body));
        addField(field);
    }

	protected void addBoilerPlateImports() {
        String[] defaults = {
				"junit.extensions.UITestCase",
				"com.windowtester.swt.IUIContext",
				"com.windowtester.swt.WidgetLocator"
        };
        for (int i = 0; i < defaults.length; i++) {
            addImport(new ImportUnit(defaults[i]));
        }        
	}
   
    
	////////////////////////////////////////////////////////////////////////////
	//
	// Boiler plate method bodies
	//
	////////////////////////////////////////////////////////////////////////////

    /**
	 * Get the main test method. 
	 * @return main test method
	 */
	protected MethodUnit getMainMethod() {
		
		String mthName = "test" + getName();
		if (mthName.endsWith("Test"))
			mthName = mthName.substring(0, mthName.length() - 4);

		MethodUnit m = new MethodUnit(mthName);
		String comment = "/**" + NEW_LINE + "* Main test method." + NEW_LINE + "*/";
		m.setComment(comment);
		m.addModifier(Modifier.PUBLIC);
		m.addThrows("Exception");
		
		//add pointer to IUIContext instance
		_root = new CodeBlock(getUIContextInitilizationBlock());
		
		//set the main method's body to be the method under construction
		m.setBody(_root);
		
		return m;
	}
    
	protected String getUIContextInitilizationBlock() {
		StringBuffer sb = new StringBuffer();
		sb.append(getUIContextTypeName()).append(' ').append(getUIContextInstanceName()).append(" = ").append(getUIContextGetterName()).append(";").append(NEW_LINE);
		return sb.toString();
	}

	
	public String getUIContextGetterName() {
		return "getUIContext()";
	}

	/**
	 * Get the name of the UIContext type.
	 */
	public String getUIContextTypeName() {
		return "IUIContext";
	}

	/**
	 * Get the name of the UIContext instance.
	 */
	public String getUIContextInstanceName() {
		return _uiContextInstanceName;
	}


//	/**
//     * Get a method unit describing the suite method for this test.
//	 * @return boiler plate suite method
//	 */
//	private MethodUnit getSuiteMethod() {
//		MethodUnit m = new MethodUnit("suite");
//		String comment = "/** @return the test suite" + NEW_LINE + "*/";
//		m.setComment(comment);
//		m.addModifier(Modifier.PUBLIC);
//		m.addModifier(Modifier.STATIC);
//		m.setReturnType("Test");
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append("return new ActivePDETestSuite(").append(NEW_LINE).
//           append(getName()).append(".class, ").
//           append(getName()).append(".class.getName());").append(NEW_LINE);
//        CodeBlock block = new CodeBlock(sb.toString());
//        m.setBody(block);
//		return m;
//	}

//    /**
//     * Get the boiler plate setup method body.
//     * @return boiler plate setup method body
//	 */
//	private MethodUnit getSetUpMethod() {
//		MethodUnit m = new MethodUnit("setUp");
//		String comment = "/* @see junit.framework.TestCase#setUp()" + NEW_LINE + "*/";
//		m.setComment(comment);
//		m.addModifier(Modifier.PROTECTED);
//		m.addThrows("Exception");
//		
//    	StringBuffer sb = new StringBuffer();
//    	sb.append("super.setUp();").append(NEW_LINE).
//       	   append("_uiContext = UIContextFactory.createContext(Display.getCurrent());").append(NEW_LINE).
//       	   append("registerWidgetInfo();").append(NEW_LINE);
//        
//    	CodeBlock block = new CodeBlock(sb.toString());
//        m.setBody(block);
//		return m;
//    }
 
    /**
     * Get the boiler plate tearDown method.
     * @return boiler plate tearDown method body
     */
    protected MethodUnit getTearDownMethod() {
    	
		MethodUnit m = new MethodUnit("tearDown");
		String comment = "/* @see junit.framework.TestCase#tearDown()" + NEW_LINE + "*/";
		m.setComment(comment);
		m.addModifier(Modifier.PROTECTED);
		m.addThrows("Exception");
		
    	StringBuffer sb = new StringBuffer();
    	sb.append("_uiContext.dispose();").append(NEW_LINE).
    	   append("super.tearDown();").append(NEW_LINE);
        
    	CodeBlock block = new CodeBlock(sb.toString());
        m.setBody(block);
		return m;
    }

//    /**
//     * Get the boiler plate registerWidgetInfo method.
//     * NOTE: if there were no widget registrations, this will return null.
//     * @return boiler plate registerWidgetInfo method body
//     */
//    protected MethodUnit getWidgetRegistrationMethod() {
//    	
//    	Set mappings = mapper.getMappings();
//    	
//    	/*
//    	 * Fast return if there are no mapppings to register
//    	 */
//    	if (mappings.isEmpty())
//    		return null;
//    	
//    	MethodUnit m = new MethodUnit("registerWidgetInfo");
//		String comment = "/* Register widgets." + NEW_LINE + "*/";
//		m.setComment(comment);
//		m.addModifier(Modifier.PROTECTED);
//		
//    	StringBuffer sb = new StringBuffer();
//        sb.append(getUIContextInitilizationBlock());
//    	
//
//    	String constructorCall, key, name;
//    	WidgetLocator locator;
//    	
//    	/* 
//    	 * A flag to tell us whether a widget was registered.
//    	 * The no-registration case should not generate a method.
//    	 */
//    	boolean registeredWidget = false;
//    	
//    	for (Iterator iter = mappings.iterator(); iter.hasNext();) {
//			Map.Entry mapping = (Map.Entry) iter.next();
//			key = mapping.getKey().toString();
//			locator = (WidgetLocator)mapping.getValue();
//			/*
//			 * Check for naming conflicts
//			 */
//			name = locator.getData("name");			
//			//if it's named and there was no conflict, we don't emit a locator
//			if (name != null && name.equals(key)) {
//				//do nothing
//			} else {
//				
//				//note the registration
//				registeredWidget = true;
//				
//				//if it's named and the name is not the key we have a collision case
//				if (name != null && !name.equals(key)) {
//					sb.append("//FIXME: name of named widget not used due to naming conflict").append(NEW_LINE);
//				}
//				//add imports for locator class
//				locator.accept(new IWidgetLocatorVisitor() {
//					public void visit(WidgetLocator wl) {
//						addImport(new ImportUnit(wl.getClass().getName()));		
//					}
//				});
//				
//				constructorCall = WidgetLocatorService.getJavaString(locator);
//				sb.append(getUIContextInstanceName()).append(".register(\"").append(key).append("\", ").append(constructorCall).append(");").append(NEW_LINE);
//			}
//		}
//    	
//    	//if there were no widget registrations return an empty method
//    	if (!registeredWidget)
//    		return null;
//    	
//    	CodeBlock block = new CodeBlock(sb.toString());
//        m.setBody(block);
//		return m;		
//	}
    
    
    /**
     * Get the boiler plate test constructor.
     * @return method for the boilerplate constructor
     */
    protected MethodUnit getConsMethod() {
    	
//		MethodUnit m = new MethodUnit(getName());
//		String comment = "/**" + NEW_LINE + "* Create an Instance" + NEW_LINE + "* @param testName" + NEW_LINE + "*/";
//		m.setComment(comment);
//		
//		m.setConstructor(true);
//		m.addModifier(Modifier.PUBLIC);
//		m.addParameter(new Parameter("String", "testName"));
//		
//    	CodeBlock block = new CodeBlock("super(testName);" + NEW_LINE);
//      m.setBody(block);
//		return m;
    	return null;
    }

    
	////////////////////////////////////////////////////////////////////////////
	//
	// Cutom keys
	//
	////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ITestCaseBuilder#getKeyEventImport()
     */
    public ImportUnit getKeyEventImport() {
    	return new ImportUnit("org.eclipse.swt.SWT");
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ITestCaseBuilder#getControlKey()
     */
    public String getControlKey() {
    	return "SWT.CTRL";
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ITestCaseBuilder#parseControlKey(com.windowtester.recorder.event.user.SemanticKeyDownEvent)
     */
    public String parseControlKey(SemanticKeyDownEvent kde) {
    	/*
    	 * This has been hacked for awt key events -- needs to be pluggable
    	 */
    	
    	String key = kde.getKey();
    	if (isTab(key))
    		return  "SWT.TAB";
    	if (isEnter(key))
    		return "SWT.CR";
    	
    	switch(kde.getKeyCode()) {
    		case SWT.ARROW_RIGHT :
    			return "SWT.ARROW_RIGHT";
    		case SWT.ARROW_LEFT :
    			return "SWT.ARROW_LEFT";
    		case SWT.ARROW_UP :
    			return "SWT.ARROW_UP";
    		case SWT.ARROW_DOWN :
    			return "SWT.ARROW_DOWN";    		
    		default :
    			return null;
    	}
    }
    
    /**
     * Check for control characters
     * @param key - the character to check
     * @return true if the key is a control character
     */
    protected boolean isControl(String key) {
        return key != null && Character.isISOControl(key.charAt(0)); 
    }
    
    /**
     * Check for a backspace.
     * @param key - the character to check
     * @return true if the key is a backspace
     */
    protected boolean isBackSpace(String key) {
		return key.charAt(0) == '\b';
	}
	
    /**
     * Check for a tab.
     * @param key - the character to check
     * @return true if the key is a tab
     */
    protected boolean isTab(String key) {
		return key.charAt(0) == '\t';
	}
	
    /**
     * Check for an enter key.
     * @param key - the character to check
     * @return true if the key is an 'enter'
     */
    protected boolean isEnter(String key) {
		return key.charAt(0) == ICodeGenConstants.NEW_LINE.charAt(0) || key.charAt(0) == '\r';
	}
    
    
    
	////////////////////////////////////////////////////////////////////////////
	//
	// Construction
	//
	////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#add(com.windowtester.codegen.assembly.CodeBlock)
     */
    public void add(CodeBlock block) {
    	if (!_initialized) {
    		init();
    		_initialized = true;
    	}
        _current = block;
        _root.addChild(block);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#assemble()
     */
    public IType assemble() {
//        AST ast = new AST();
//        CompilationUnit unit = ast.newCompilationUnit();
//        PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
//        packageDeclaration.setName(ast.newSimpleName("test"));
//        unit.setPackage(packageDeclaration);
//        
//        for (Iterator iter = getImports().iterator(); iter.hasNext();) {
//            ImportUnit element = (ImportUnit) iter.next();
//            
//        }
        return null;
    }

    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ITestCaseBuilder#build()
     */
    public String build() {
//    	//main has been constructed now, so the mapper is populated with references
//    	//we need to register
//    	if (getMapper().getMappings().size() > 0) {
//    		addMethod(getWidgetRegistrationMethod());
//    		
//            // add registered types next:
//			Set mappings = mapper.getMappings();
//			for (Iterator iter = mappings.iterator(); iter.hasNext();) {
//				Map.Entry entry = (Map.Entry) iter.next();
//				WidgetLocator info = (WidgetLocator) entry.getValue();
//				info.accept(new IWidgetLocatorVisitor() {
//					public void visit(WidgetLocator info) {
//						addImport(new ImportUnit(info.getTargetClass()
//								.getName()));
//					}
//				});
//			}
//    		
//    	}
    		
    		
        return getStringBuilder().build();
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#getCurrentBlock()
     */
    public CodeBlock getCurrentBlock() {
        return _current;
    }
        
    public CodeBlock getRootBlock() {
    	return _root;
    }
    
    /**
     * Set the current root pointer to this block
     * @param block
     */
    public void setCurrentRoot(CodeBlock block) {
        _root = block;
    }

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.ITestCaseBuilder#getFreshVariable(java.lang.String)
	 */
	public String getFreshVariable(String string) {
		// TODO [author=pq] not clear if we need to generate variables anymore 
		throw new UnsupportedOperationException();
	}
    
	/**
	 * @see com.windowtester.codegen.ITestCaseBuilder#getFreshMethod(java.lang.String)
	 */
	public String getFreshMethod(String prefix) {
		return new FreshMethodNameFinder(getMethods()).find(prefix);
	}
	

//	/**
//	 * @see com.windowtester.codegen.ITestCaseBuilder#getMapper()
//	 */
//	public WidgetMapper getMapper() {
//		return mapper;
//	}
    
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.ITestCaseBuilder#getSetupHandlers()
	 */
	public ISetupHandler[] getSetupHandlers() {
		return handlers;
	}

	
}