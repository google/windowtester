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
package com.windowtester.codegen.generator;

import com.windowtester.codegen.ICodeGenPluginTraceOptions;
import com.windowtester.codegen.ITestCaseBuilder;
import com.windowtester.codegen.TestCaseGenerator;
import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.util.CodeGenSnippetBuilder;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticComboSelectionEvent;
import com.windowtester.recorder.event.user.SemanticFocusEvent;
import com.windowtester.recorder.event.user.SemanticListSelectionEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticMoveEvent;
import com.windowtester.recorder.event.user.SemanticResizeEvent;
import com.windowtester.recorder.event.user.SemanticShellClosingEvent;
import com.windowtester.recorder.event.user.SemanticShellDisposedEvent;
import com.windowtester.recorder.event.user.SemanticShellShowingEvent;
import com.windowtester.recorder.event.user.SemanticTableSelectionEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetClosedEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.swt.WidgetMapper;


/**
 * Snippet building logic used in release v1.*.
 *
 */
public class OldAPICodeBlockBuilder implements ICodeBlockBuilder {

	private static final String NEW_LINE = TestCaseGenerator.NEW_LINE;

	/** The name of the instance variable that points to a UIContext */
	private String _uiContextInstanceName = "_uiContext";
	
	/** A code snippet builder */
    private final CodeGenSnippetBuilder _snippetHelper;
   
    /** A mapper instance */
    private WidgetMapper _mapper;
	
    
    //////////////////////////////////////////////////////////////////////////
    //
    // Instance creation
    //
    //////////////////////////////////////////////////////////////////////////
    
    /**
     * Create an instance.
	 * @param builder - the builder strategy
	 */
	public OldAPICodeBlockBuilder(ITestCaseBuilder builder) {
		_mapper = builder.getMapper();
		_uiContextInstanceName = builder.getUIContextInstanceName();
		_snippetHelper = new CodeGenSnippetBuilder(_uiContextInstanceName);
	}
    
  
    //////////////////////////////////////////////////////////////////////////
    //
    // Code block building
    //
    //////////////////////////////////////////////////////////////////////////
    
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildAssertion(com.windowtester.runtime.locator.ILocator, com.windowtester.recorder.event.PropertySet.PropertyMapping)
	 */
	public CodeBlock buildAssertion(ILocator locator, PropertyMapping propertyMapping) {
		return notHandled("assertion"); //legacy API does not support assertions
	}
	
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#build(com.windowtester.recorder.event.user.SemanticShellClosingEvent)
     */
    public CodeBlock buildShellClosing(SemanticShellClosingEvent event) {
		//gen closing code:
        String label = registerWidget(event.getHierarchyInfo());
		StringBuffer sb = new StringBuffer(); 
        sb.append(_snippetHelper.closeShellSnippet(label));
        return new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildWidgetClosing(com.windowtester.recorder.event.user.SemanticWidgetClosedEvent)
     */
    public CodeBlock buildWidgetClosing(SemanticWidgetClosedEvent event) {
    	return notHandled("widget closing"); //legacy API does not support closing of generic widgets
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#build(com.windowtester.recorder.event.user.SemanticMoveEvent)
     */
    public CodeBlock buildMove(SemanticMoveEvent event) {
        StringBuffer sb = new StringBuffer();     
        
        IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("move"));
        } else {
            String label = registerWidget(info);
            sb.append(_snippetHelper.moveSnippet(label, event.getX(), event.getY()));
        }

        return new CodeBlock(sb.toString());
    }

    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#build(com.windowtester.recorder.event.user.SemanticResizeEvent)
     */
    public CodeBlock buildResize(SemanticResizeEvent event) {
		StringBuffer sb = new StringBuffer();     
        
        IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("resize"));
        } else {
            String label = registerWidget(info);
            sb.append(_snippetHelper.resizeSnippet(label, event.getWidth(), event.getHeight()));
        }
        return  new CodeBlock(sb.toString());
    }

    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#build(com.windowtester.recorder.event.user.SemanticFocusEvent)
     */
    public CodeBlock buildFocus(SemanticFocusEvent curr) {
    	StringBuffer sb = new StringBuffer();     
        String label = registerWidget(curr.getHierarchyInfo());
        sb.append(_snippetHelper.setFocus(label));
        return new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildButtonSelect(com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent)
     */
    public CodeBlock buildButtonSelect(SemanticWidgetSelectionEvent event) {
		
		Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "handling button click: " + event.getItemLabel());
        //handleShellChange(event); <-- now handled by shell open event
        StringBuffer sb = new StringBuffer();     
        
        IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("click"));
        } else {
        	String label = registerWidget(info);
        	sb.append(_snippetHelper.clickButtonSnippet(label));
        }
        return new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildMenuSelect(com.windowtester.recorder.event.user.SemanticMenuSelectionEvent)
     */
    public CodeBlock buildMenuSelect(SemanticMenuSelectionEvent event) {
		Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "handling menu invocation: ");
		
		StringBuffer sb = new StringBuffer();     
		IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("menu invocation"));
        } else {
        	String label = registerWidget(event.getHierarchyInfo());
        	sb.append(_snippetHelper.invokeMenuItemSnippet(label, event.getIndex(), event.getPathString(), event.getButton()));
        }
		
        return new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildTreeSelect(com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent)
     */
    public CodeBlock buildTreeSelect(SemanticTreeItemSelectionEvent event) {
		
		Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "handling tree item selection: " + event.getItemLabel());
        StringBuffer sb = new StringBuffer();    
		IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("tree item selection"));
        } else {
            String label = registerWidget(info);
            sb.append(_snippetHelper.treeItemSelectSnippet(label, event));
        }
        return new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildTableSelect(com.windowtester.recorder.event.user.SemanticTableSelectionEvent)
     */
    public CodeBlock buildTableSelect(SemanticTableSelectionEvent tableSelection) {
    	//this should never be reached -- table items in SWT are just items...
    	return new CodeBlock("//ERROR: table item selections not handled");
    }
    
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildSelect(com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent)
     */
    public CodeBlock buildSelect(SemanticWidgetSelectionEvent event) {
	
    	Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "handling generic item selection: " + event.toString());
		
        String code = null; 
		IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            code = createIdErrorMessage("widget selection").toString();
        } else {
    		String label = registerWidget(info);
    		
    		//handle import if necessary:
    		String mask = event.getMask();
    		
    		code = (event.requiresLocationInfo()) ? _snippetHelper.genericWidgetSelection(label, event.getIndex(), event.getMask(), event.getClicks(), event.getX(), event.getY()) :
    				_snippetHelper.genericWidgetSelection(label, event.getIndex(), mask, event.getClicks());
        }		
		return new CodeBlock(code);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildTextEntry(java.lang.String)
     */
    public CodeBlock buildTextEntry(String str) {
		String codeSnippet = _snippetHelper.enterTextSnippet(str);
		return new CodeBlock(codeSnippet);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildKeyEntry(java.lang.String)
     */
    public CodeBlock buildKeyEntry(String key) {
		String codeSnippet = _snippetHelper.enterKeySnippet(key);
		return new CodeBlock(codeSnippet);         		
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildKeyEntry(java.lang.String, java.lang.String)
     */
    public CodeBlock buildKeyEntry(String ctrl, String key) {
		String codeSnippet = _snippetHelper.enterKeySnippet(ctrl, key);
		return new CodeBlock(codeSnippet);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildFocusChange(com.windowtester.runtime.IWidgetLocator)
     */
    public CodeBlock buildFocusChange(IWidgetIdentifier newTarget) {
		StringBuffer sb = new StringBuffer();
		
        //check for error
        if (newTarget == null) {
            sb.append(createIdErrorMessage("focus change"));
        } else {
    		String label = registerWidget(newTarget);		
    		sb.append(_snippetHelper.setFocus(label));
        }
		return  new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#build(com.windowtester.recorder.event.user.SemanticListSelectionEvent)
     */
    public CodeBlock build(SemanticListSelectionEvent listSelection) {
    	StringBuffer sb = new StringBuffer();
    	
    	IWidgetIdentifier info = listSelection.getHierarchyInfo();
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("list selection"));
        } else {
        	String label = registerWidget(listSelection.getHierarchyInfo());
        	String item = listSelection.getItem();
        	String mask = listSelection.getMask();
        	int numClicks = listSelection.getClicks();
        	sb.append(_snippetHelper.listSelection(label, item, mask, numClicks));
        }
		return new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#build(com.windowtester.recorder.event.user.SemanticComboSelectionEvent)
     */
    public CodeBlock build(SemanticComboSelectionEvent comboSelection) {
	 
    	StringBuffer sb = new StringBuffer();
    	
    	IWidgetIdentifier info = comboSelection.getHierarchyInfo();
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("combo selection"));
        } else {
        	String label = registerWidget(comboSelection.getHierarchyInfo());
        	String item = comboSelection.getSelection();
        	sb.append(_snippetHelper.comboSelection(label, item));
        }
    	
		return new CodeBlock(sb.toString());
    }
    
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildDragTo(com.windowtester.recorder.event.IUISemanticEvent)
     */
    public CodeBlock buildDragTo(IUISemanticEvent event) {
	 
    	
		StringBuffer sb = new StringBuffer();

		IWidgetIdentifier info = event.getHierarchyInfo();

		// check for error
		if (info == null) {
			sb.append(createIdErrorMessage("dragTo"));
		} else {
			String label = registerWidget(info);
			/*
			 * Handle path here
			 */
			String path  = null;
			if (event instanceof SemanticTreeItemSelectionEvent) {
				path = ((SemanticTreeItemSelectionEvent)event).getPathString();
			}
			sb.append(_snippetHelper.dragToSnippet(label, path, event.getX(),
					event.getY()));
		}
		return new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildMoveTo(com.windowtester.recorder.event.IUISemanticEvent)
     */
    public CodeBlock buildMoveTo(IUISemanticEvent event) {
        StringBuffer sb = new StringBuffer();     
        
        IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("mouseMoveTo"));
        } else {
            String label = registerWidget(info);
            sb.append(_snippetHelper.mouseMoveToSnippet(label, event.getX(), event.getY()));
        }

        return new CodeBlock(sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildMethodInvocation(java.lang.String)
     */
    public CodeBlock buildMethodInvocation(String method) {
    	return new CodeBlock(_snippetHelper.methodInvocation(method));
    }
    
    public CodeBlock buildWaitForShellShowing(SemanticShellShowingEvent event) {
    	return buildWaitForShellShowing(event.getName());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildWaitForShellShowing(java.lang.String)
     */
    public CodeBlock buildWaitForShellShowing(String shellTitle) {
		Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "(requires a wait for: " + shellTitle +")");
		String waitBody = _uiContextInstanceName + ".waitForShellShowing(\"" + shellTitle + "\");" + NEW_LINE;
		return new CodeBlock(waitBody);
    }
    
    public CodeBlock buildWaitForShellDisposed(SemanticShellDisposedEvent event) {
    	return buildWaitForShellDisposed(event.getName());
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildWaitForShellDisposed(java.lang.String)
     */
    public CodeBlock buildWaitForShellDisposed(String shellTitle) {
		Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "(requires a wait for dispose: " + shellTitle +")");
		String waitBody = _uiContextInstanceName + ".waitForShellDisposed(\"" + shellTitle + "\");" + NEW_LINE;
		return new CodeBlock(waitBody);
    }
    

	////////////////////////////////////////////////////////////////////////////
	//
	// Key Events
	//
	////////////////////////////////////////////////////////////////////////////
	
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#getBackSpaceKey()
     */
    public String getBackSpaceKey() {
    	return "SWT.BS";
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.generator.ICodeBlockBuilder#getKeyEventImport()
     */
    public ImportUnit getKeyEventImport() {
    	return new ImportUnit("org.eclipse.swt.SWT");
    }
    
	////////////////////////////////////////////////////////////////////////////
	//
	// Helpers
	//
	////////////////////////////////////////////////////////////////////////////
	
    protected StringBuffer createIdErrorMessage(String eventType, String comment) {
        StringBuffer sb = new StringBuffer();
        sb.append("//error identifying target of ").append(eventType).append(" event;").append(comment).append(NEW_LINE);
        return sb;
    }
    
    protected StringBuffer createIdErrorMessage(String eventType) {
        return createIdErrorMessage(eventType, "event ignored"); //a default comment
    }

	private CodeBlock notHandled(String type) {
		return new CodeBlock("//event type: [" + type + "] not handled" + NEW_LINE);
	}
    
    //////////////////////////////////////////////////////////////////////////
    //
    // Widget registration
    //
    //////////////////////////////////////////////////////////////////////////
    
	private String registerWidget(IWidgetIdentifier locator) {
		if (locator instanceof com.windowtester.swt.WidgetLocator)
			return registerWidget((com.windowtester.swt.WidgetLocator)locator);
		//TODO handle mapper for Swing case
		return "";
	}
	
	private String registerWidget(com.windowtester.swt.WidgetLocator locator) {
		return _mapper.register(locator);
	}
}
