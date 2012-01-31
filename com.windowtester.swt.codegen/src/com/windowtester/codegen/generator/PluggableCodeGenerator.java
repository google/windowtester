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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.windowtester.codegen.ICodeGenPluginTraceOptions;
import com.windowtester.codegen.ITestCaseBuilder;
import com.windowtester.codegen.TestCaseGenerator;
import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;
import com.windowtester.codegen.generator.ICodegenAdvisor.Advice;
import com.windowtester.internal.debug.Tracer;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.user.SemanticComboSelectionEvent;
import com.windowtester.recorder.event.user.SemanticDragEvent;
import com.windowtester.recorder.event.user.SemanticDropEvent;
import com.windowtester.recorder.event.user.SemanticFocusEvent;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
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
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;

/**
 * A code generator that generates test code using the WindowTester test API.
 */
public class PluggableCodeGenerator extends TestCaseGenerator {

    private ShellSet _openShells = new ShellSet();

	private ICodeBlockBuilder _snippetBuilder;
    
    /**
     * Create an instance.
	 * @param builder - the builder strategy
	 */
	public PluggableCodeGenerator(ITestCaseBuilder builder, ICodeBlockBuilder blockBuilder) {
		super(builder);
		_snippetBuilder = blockBuilder;
	}

	public PluggableCodeGenerator(ITestCaseBuilder builder, ICodeBlockBuilder blockBuilder, CodegenSettings codegenSettings) {
		super(builder, codegenSettings);
		_snippetBuilder = blockBuilder;
	}
	
	
	public ICodeBlockBuilder getBlockBuilder() {
		return _snippetBuilder;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Event Handlers
	//
	////////////////////////////////////////////////////////////////////////////
		
	
	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleShellShowing(com.windowtester.recorder.event.user.SemanticShellShowingEvent)
	 */
	protected void handleShellShowing(SemanticShellShowingEvent event) {
		/**
		 * If the shell was not already showing we need to generate a wait for condition
		 * TODO: this is abbot's scheme (to wait for Shell by name) but it is not robust (suppose we have 2 shells with the same name?)
		 */
		boolean alreadyOpen = !_openShells.add(event);
		if (!alreadyOpen)
			addWaitForShell(event);
	}
	
	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleShellDisposed(com.windowtester.recorder.event.user.SemanticShellDisposedEvent)
	 */
	protected void handleShellDisposed(SemanticShellDisposedEvent event) {
		
		//remove the shell from the open list
		_openShells.remove(event);
		
		addWaitForShellDisposed(event);
	}


	
	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleShellClosing(com.windowtester.recorder.event.user.SemanticShellClosingEvent)
	 */
	protected void handleShellClosing(SemanticShellClosingEvent event) {
		//remove the shell from the open list
		_openShells.remove(event);
		//gen closing code:		
        addCodeBlock(getBlockBuilder().buildShellClosing(event));
	}

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleWidgetClosed(com.windowtester.recorder.event.user.SemanticWidgetClosedEvent)
	 */
	protected void handleWidgetClosed(SemanticWidgetClosedEvent event) {
		 addCodeBlock(getBlockBuilder().buildWidgetClosing(event));
	}
	
	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleMove(com.windowtester.recorder.event.user.SemanticMoveEvent)
	 */
	protected void handleMove(SemanticMoveEvent event) {
		addCodeBlock(getBlockBuilder().buildMove(event));
	}

	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleResize(com.windowtester.recorder.event.user.SemanticResizeEvent)
	 */
	protected void handleResize(SemanticResizeEvent event) {        
		addCodeBlock(getBlockBuilder().buildResize(event));
	}
	
	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleFocus(com.windowtester.recorder.event.user.SemanticFocusEvent)
	 */
	protected void handleFocus(List events) {
	
		SemanticFocusEvent curr;
		SemanticFocusEvent last = null;
		
		for (Iterator iter = events.iterator(); iter.hasNext();) {
			curr = (SemanticFocusEvent)iter.next();
			if (last == null || last != null && last.getHierarchyInfo().equals(curr.getHierarchyInfo())) {
				addCodeBlock(getBlockBuilder().buildFocus(curr));
		        last = curr;
			} // else, ignore duplicates
		}
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleInspection(com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent)
	 */
	protected void handleInspection(SemanticWidgetInspectionEvent event) {
		PropertyMapping[] props = event.getProperties().toArray();
		for (int i = 0; i < props.length; i++) {
			if (props[i].isFlagged())
				addCodeBlock(getBlockBuilder().buildAssertion(event.getLocator(), props[i]));
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleButtonClick(com.windowtester.swt.event.model.SemanticWidgetSelectionEvent)
	 */
	protected void handleButtonClick(SemanticWidgetSelectionEvent event) {
		addCodeBlock(getBlockBuilder().buildButtonSelect(event));		   
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleMenuInvocation(java.util.List, com.windowtester.swt.event.model.SemanticWidgetSelectionEvent)
	 */
	protected void handleMenuInvocation(List events, SemanticMenuSelectionEvent event) {
		addCodeBlock(getBlockBuilder().buildMenuSelect(event));		
	}

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleTreeItemSelection(com.windowtester.swt.event.model.SemanticTreeItemSelectionEvent)
	 */
	protected void handleTreeItemSelection(SemanticTreeItemSelectionEvent event) {
		//TODO: move this to a generic import handler
		//add import to resolve SWT.CHECK
		//TODO: handle this in a system-independent way
//        if (event.getChecked())
//        	addImport(new ImportUnit("org.eclipse.swt.SWT"));
		addCodeBlock(getBlockBuilder().buildTreeSelect(event));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleGenericWidgetSelection(com.windowtester.swt.event.model.SemanticWidgetSelectionEvent)
	 */
	protected void handleGenericWidgetSelection(SemanticWidgetSelectionEvent event) {
		
		Advice advice = getAdvice(event);
		if (!advice.isOverriden())
			doHandleSelection(event);
	}

	protected Advice getAdvice(SemanticWidgetSelectionEvent event) {
		ICodegenAdvisor[] contribs = CodegenContributionManager.getInstance().getContributedGenerators();
		Advice advice = new Advice();
		for (int i = 0; i < contribs.length; i++) {
			ICodegenAdvisor advisor = contribs[i];
			advisor.handleSelection(event, this, advice);
		}
		return advice;
	}

	
	/**
	 * Handle this selection event.  DO NOT delegate to any advisors.
	 */
	public void doHandleSelection(SemanticWidgetSelectionEvent event) {
		addCodeBlock(getSelectBlock(event));
	}

	private CodeBlock getSelectBlock(SemanticWidgetSelectionEvent event) {
		return getBlockBuilder().buildSelect(event);
	}
	
	
    /* (non-Javadoc)
     * @see com.windowtester.codegen.TestCaseGenerator#handleTextEntry(java.util.List)
     */
    protected void handleTextEntry(List events) {
    	
    	/*
    	 * Identification errors are silently ignored here...  a warning message should be produced
    	 * 
    	 * TODO: this has been quickly hacked for the STPCon demo, it needs to be repaired to be SWT pluggable
    	 * 
    	 */
    	
    	
        SemanticKeyDownEvent kde = (SemanticKeyDownEvent)events.get(0);
   
        String key = null;
        StringBuffer sb = new StringBuffer();
        String controlKey = null; //used for control character handling
        IWidgetIdentifier textEntryTarget = null;
        
        for (Iterator iter = events.iterator(); iter.hasNext();) {
			
        	kde        = (SemanticKeyDownEvent)iter.next();
        	key        = kde.getKey();
        	controlKey = null;         //reset the controlKey
        	
        	//check for focus change
			textEntryTarget = kde.getHierarchyInfo();
	        if (textEntryTarget != null && !textEntryTarget.equals(currentWidgetInfo)) {
	        	//update cache...
	        	currentWidgetInfo = textEntryTarget;
	        	
        		//flush the previous keys
        		if (sb.length() > 0) {
        			addCodeBlock(getBlockBuilder().buildTextEntry(sb.toString()));
        			sb = new StringBuffer();
        		}
			}	
        	
        	//handle "control" keys specially (TABs, LFs)
        	if (isControl(key)) {
        		
            	if (isBackSpace(key)) {
            	
            		/**
            		 * If the backspace "mid-stream", it is considered a typo and is corrected for,
            		 * otherwise it is emmtited.
            		 */
            		if (sb.length() > 0) //in midstream
            			handleBackspace(sb);
   
            	}
            		
            	controlKey = parseControlKey(kde);
            	
            	//if it's a control char, handle it specially:
            	if (controlKey != null) {
            		//flush the previous keys
            		if (sb.length() > 0) {
            			addCodeBlock(getBlockBuilder().buildTextEntry(sb.toString()));
            			sb = new StringBuffer();
            		}
            		           		
        			//add import to resolve control key
        			addImport(getBlockBuilder().getKeyEventImport());
        			
            		//create a control entry snippet
        			addCodeBlock(getBlockBuilder().buildKeyEntry(controlKey));
        			
            	}
            //control sequences (ctrl-A) are also special...
            } else if (kde.isControlSequence()) {
    			//add import to resolve control key
    			addImport(getBlockBuilder().getKeyEventImport());
        		//create a control entry snippet           	
    			addCodeBlock(getBlockBuilder().buildKeyEntry(getTestBuilder().getControlKey(), key));
        	} else {  //non-control keys are just appended
                sb.append(key);
            }
        }	
        
        //flush cached keys (if any)
		if (sb.length() > 0) {
			addCodeBlock(getBlockBuilder().buildTextEntry(sb.toString()));
		}
  
    }


    private String parseControlKey(SemanticKeyDownEvent kde) {
    	return getTestBuilder().parseControlKey(kde);
	}


	/**
     * Set the ui focus to a new target.
     * @param newTarget - the new focus target
     */
	protected void handleFocusChange(IWidgetIdentifier newTarget) {
		addCodeBlock(getBlockBuilder().buildFocusChange(newTarget));	
	}

    /**
     * @see com.windowtester.codegen.TestCaseGenerator#handleListSelection(com.windowtester.recorder.event.user.SemanticListSelectionEvent)
     */
    protected void handleListSelection(SemanticListSelectionEvent listSelection) {

		//TODO: handle this in a system-independent way
//    	String mask = listSelection.getMask();
//    	if (mask != null) {
//    		//add SWT import to identify mask
//    		addImport(new ImportUnit("org.eclipse.swt.SWT"));
//    	}
    	
		addCodeBlock(getBlockBuilder().build(listSelection));	
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.TestCaseGenerator#handleTableItemSelection(com.windowtester.recorder.event.user.SemanticTableSelectionEvent)
     */
    protected void handleTableItemSelection(SemanticTableSelectionEvent tableSelection) {
    	//TODO[!pq]: shouldn't table item selections have masks?
//    	String mask = tableSelection.getMask();
//    	if (mask != null) {
//    		//add SWT import to identify mask
//    		addImport(new ImportUnit("org.eclipse.swt.SWT"));
//    	}
		addCodeBlock(getBlockBuilder().buildTableSelect(tableSelection));	
    }
    
    
	/**
     * @see com.windowtester.codegen.TestCaseGenerator#handleComboSelection(com.windowtester.recorder.event.user.SemanticComboSelectionEvent)
     */
    protected void handleComboSelection(SemanticComboSelectionEvent comboSelection) {
		addCodeBlock(getBlockBuilder().build(comboSelection));	
    }

    /* (non-Javadoc)
     * @see com.windowtester.codegen.TestCaseGenerator#handleDrop(com.windowtester.recorder.event.user.SemanticDropEvent)
     */
    protected void handleDrop(SemanticDropEvent drop) {
    	
    	handleMissingSelectionIfNecessary(drop);
    
    	IUISemanticEvent dest = drop.getDropTargetEvent();
    	handleDragTo(dest);
    }
    
   
	private void handleMissingSelectionIfNecessary(SemanticDropEvent drop) {	
    	CodeBlock missingSelect = getMissingDragTargetSelection(drop);
    	if (missingSelect != null)
    		addCodeBlock(missingSelect);
	}
    
    /**
     * @see com.windowtester.codegen.TestCaseGenerator#handleDragDrop(com.windowtester.recorder.event.user.SemanticDragEvent, com.windowtester.recorder.event.user.SemanticDropEvent)
     */
    //TODO: this is likely never called...
    protected void handleDragDrop(SemanticDragEvent drag, SemanticDropEvent drop) {
    	
    	/*
    	 * The current implementation is not as clean as it might be.
    	 * In particular, there is a selection event that may not be 
    	 * necessary (an extra tree selection for instance).  To improve
    	 * this, we might keep a back point to the last handled event
    	 * or current focus and check to see if we really need to update...
    	 */
    	
    	
    	/*
    	 * 1) handle hover
    	 */
    	
    	IUISemanticEvent src  = drag.getDragSourceEvent();
    	
    	//first check for path-based selections:
    	if (src instanceof SemanticTreeItemSelectionEvent) {
    		handleTreeItemSelection((SemanticTreeItemSelectionEvent)src);
    	//NOTE: there might be more here: e.g., tables but tables are
    	//treated as widget selections for now 
    	} else {
    		handleMouseMoveTo(src);
    	}
    	
    	/*
    	 * 2) handle drag and drop
    	 */
    	
    	handleDrop(drop);
   
    }
    
    protected void handleDragTo(IUISemanticEvent event) {	
		addCodeBlock(getBlockBuilder().buildDragTo(event));	
	}



	private CodeBlock getMissingDragTargetSelection(SemanticDropEvent drop) {
		
		UISemanticEvent dragSource = drop.getDragSource();
		if (!(dragSource instanceof SemanticWidgetSelectionEvent))
			return null;
		CodeBlock select = getBlockBuilder().buildSelect((SemanticWidgetSelectionEvent)dragSource);
		if (select == null)
			return null;
		String selectString = select.toString();
		if (selectString == null)
			return null;
		
		CodeBlock currentBlock = getTestBuilder().getCurrentBlock();
		if (currentBlock == null)
			return select;
		if (currentBlock.toString().trim().equals(selectString.trim()))
			return null;
		return select;
	}

	private void handleMouseMoveTo(IUISemanticEvent event) {
		addCodeBlock(getBlockBuilder().buildMoveTo(event));
	}


	protected void handleAssertion(RecorderAssertionHookAddedEvent assertEvent) {
    	String method = getTestBuilder().getFreshMethod(assertEvent.getHookName());
    	MethodUnit assertMethod = new MethodUnit(method,"// TODO Auto-generated method stub" + NEW_LINE);
    	assertMethod.addModifier(Modifier.PROTECTED);
    	assertMethod.addThrows("Exception");
    	
    	addCodeBlock(getBlockBuilder().buildMethodInvocation(method));
        getTestBuilder().addMethod(assertMethod);
    }
    
	////////////////////////////////////////////////////////////////////////////
	//
	// Code block helpers
	//
	////////////////////////////////////////////////////////////////////////////

	
	protected void addCodeBlock(CodeBlock block) {
		getTestBuilder().add(block);
	}
	
	protected void addImport(ImportUnit imprt) {
		getTestBuilder().addImport(imprt);
	}
	
	/**
     * Handle when a shell change event occurs by injecting the appropriate wait condition
     * @param event - the shell title
     */
	private void addWaitForShell(SemanticShellShowingEvent event) {
		addCodeBlock(getBlockBuilder().buildWaitForShellShowing(event));
	}
    
	/**
     * Handle when a shell dispose event occurs by injecting the appropriate wait condition
     * @param event - the shell title
     */
	private void addWaitForShellDisposed(SemanticShellDisposedEvent event) {
		if (event == null || event.getName().equals("") || event.getName().length() ==0) {
			//empty shell titles are ignored for now (these are usually component frames)
			Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "wait for empty shell title ignored");
			return;
		}
		addCodeBlock(getBlockBuilder().buildWaitForShellDisposed(event));
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
    
    
    /**
     * Check for control characters
     * @param key - the character to check
     * @return true if the key is a control character
     */
    public static boolean isControl(String key) {
        return key != null && Character.isISOControl(key.charAt(0)); 
    }
    
    /**
     * Check for a backspace.
     * @param key - the character to check
     * @return true if the key is a backspace
     */
    public static boolean isBackSpace(String key) {
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
	
	
    /**
     * Handle a backspace by deleting a char from the end of the buffer 
     * (if there is a char to delete). 
	 * @param sb - the string buffer
	 */
	public void handleBackspace(StringBuffer sb) {
		//System.out.print("<backspace>");
		int last = sb.length() - 1;
		if (last >= 0) 
			sb.deleteCharAt(last);
	}

	protected static final class ShellSet {
		
		Set _shellTitles = new HashSet();
		
		boolean add(String title) {
			return _shellTitles.add(title);
		}
		boolean remove(String title) {
			return _shellTitles.remove(title);
		}
		boolean add(SemanticShellShowingEvent event) {
			String title = event.getName();
			if (title == null) {
				LogHandler.log("Shell title null --- ignoring in add");
				return false;
			}	
			return _shellTitles.add(title);
		}
		
		boolean remove(SemanticShellClosingEvent event) {
			String title = event.getName();
			if (title == null) {
				LogHandler.log("Shell title null --- ignoring in remove");
				return false;
			}	
			return _shellTitles.remove(title);		
		}
		
		boolean remove(SemanticShellDisposedEvent event) {
			String title = event.getName();
			if (title == null) {
				LogHandler.log("Shell title null --- ignoring in remove");
				return false;
			}	
			return _shellTitles.remove(title);		
		}
		
		
		boolean contains(String title) {
			return _shellTitles.contains(title);
		}
		
	}
	
	
}
