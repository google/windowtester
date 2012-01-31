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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;
import com.windowtester.codegen.util.CodeGenSnippetBuilder;
import com.windowtester.internal.runtime.IWidgetIdentifier;
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
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;
import com.windowtester.swt.WidgetMapper;

/**
 * A code generator that generates test code using the WindowTester test API.
 * 
 */
public class CodeGenerator extends TestCaseGenerator {

	/** The name of the instance variable that points to a UIContext */
	private String _uiContextInstanceName = "_uiContext";
	
	/** A code snippet builder */
    private final CodeGenSnippetBuilder _snippetHelper;
    
    /** A mapper instance */
    private WidgetMapper _mapper;
    
    /** The set of open shells, used to infer needed "wait for" conditions */
    //private Set /*<ParentShellInfo>*/ _openShells = new HashSet();
    
    private ShellSet _openShells = new ShellSet();
    
    /**
     * Create an instance.
	 * @param builder - the builder strategy
	 */
	public CodeGenerator(ITestCaseBuilder builder) {
		super(builder);
		_mapper = builder.getMapper();
		_uiContextInstanceName = builder.getUIContextInstanceName();
		_snippetHelper = new CodeGenSnippetBuilder(_uiContextInstanceName);
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
		//WidgetLocator info = event.getHierarchyInfo();
		//add and check to see if already present...
		boolean alreadyOpen = !_openShells.add(event);
		if (!alreadyOpen)
			addWaitForShell(event.getName());
	}
	
	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleShellDisposed(com.windowtester.recorder.event.user.SemanticShellDisposedEvent)
	 */
	protected void handleShellDisposed(SemanticShellDisposedEvent event) {
		
		//remove the shell from the open list
		_openShells.remove(event);
		
		addWaitForShellDisposed(event.getName());
	}


	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleShellClosing(com.windowtester.recorder.event.user.SemanticShellClosingEvent)
	 */
	protected void handleShellClosing(SemanticShellClosingEvent event) {
		//remove the shell from the open list
		_openShells.remove(event);
		//gen closing code:
        String label = registerWidget(event.getHierarchyInfo());
		StringBuffer sb = new StringBuffer(); 
        sb.append(_snippetHelper.closeShellSnippet(label));
        CodeBlock block = new CodeBlock(sb.toString());
        getTestBuilder().add(block);
		
	}
	
	private String registerWidget(IWidgetIdentifier locator) {
		if (locator instanceof com.windowtester.swt.WidgetLocator)
			return registerWidget((com.windowtester.swt.WidgetLocator)locator);
		//TODO handle mapper for Swing case
		return "";
	}
	
	private String registerWidget(com.windowtester.swt.WidgetLocator locator) {
		return _mapper.register(locator);
	}


	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleMove(com.windowtester.recorder.event.user.SemanticMoveEvent)
	 */
	protected void handleMove(SemanticMoveEvent event) {
        StringBuffer sb = new StringBuffer();     
        
        IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("move"));
        } else {
            String label = registerWidget(info);
            sb.append(_snippetHelper.moveSnippet(label, event.getX(), event.getY()));
        }

        CodeBlock block = new CodeBlock(sb.toString());
        getTestBuilder().add(block);
	}
	
	/**
	 * @see com.windowtester.codegen.TestCaseGenerator#handleResize(com.windowtester.recorder.event.user.SemanticResizeEvent)
	 */
	protected void handleResize(SemanticResizeEvent event) {
        StringBuffer sb = new StringBuffer();     
        
        IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("resize"));
        } else {
            String label = registerWidget(info);
            sb.append(_snippetHelper.resizeSnippet(label, event.getWidth(), event.getHeight()));
        }
        CodeBlock block = new CodeBlock(sb.toString());
        getTestBuilder().add(block);
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
				StringBuffer sb = new StringBuffer();     
		        
		        String label = registerWidget(curr.getHierarchyInfo());
		        
		        sb.append(_snippetHelper.setFocus(label));
		        CodeBlock block = new CodeBlock(sb.toString());
		        getTestBuilder().add(block);
				
		        last = curr;
			} // else, ignore duplicates
			
		}

	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleButtonClick(com.windowtester.swt.event.model.SemanticWidgetSelectionEvent)
	 */
	protected void handleButtonClick(SemanticWidgetSelectionEvent event) {
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
        
        CodeBlock block = new CodeBlock(sb.toString());
        getTestBuilder().add(block);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleMenuInvocation(java.util.List, com.windowtester.swt.event.model.SemanticWidgetSelectionEvent)
	 */
	protected void handleMenuInvocation(List events, SemanticMenuSelectionEvent event) {
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
		
		CodeBlock block = new CodeBlock(sb.toString());
		getTestBuilder().add(block);
		
		//TODO: fix bug in shell change detection --- issue: modal dialog creates new shell (noticed) but it's dismissal is not...
		//handleShellChange(event);
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleTreeItemSelection(com.windowtester.swt.event.model.SemanticTreeItemSelectionEvent)
	 */
	protected void handleTreeItemSelection(SemanticTreeItemSelectionEvent event) {
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
        
        //add import to resolve SWT.CHECK
        if (event.getChecked())
        	getTestBuilder().addImport(new ImportUnit("org.eclipse.swt.SWT"));
        
        CodeBlock block = new CodeBlock(sb.toString());
        getTestBuilder().add(block);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleTableItemSelection(com.windowtester.recorder.event.user.SemanticTableSelectionEvent)
	 */
	protected void handleTableItemSelection(SemanticTableSelectionEvent event) {
		// should not be called (table items are just generic selections in SWT)
		getTestBuilder().add(new CodeBlock("//INTERNAL ERROR: Unexpected Table Item Selection Event"));
		
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleGenericWidgetSelection(com.windowtester.swt.event.model.SemanticWidgetSelectionEvent)
	 */
	protected void handleGenericWidgetSelection(SemanticWidgetSelectionEvent event) {
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
    		if (mask != null)
    			getTestBuilder().addImport(new ImportUnit("org.eclipse.swt.SWT"));
    		
    		code = (event.requiresLocationInfo()) ? _snippetHelper.genericWidgetSelection(label, event.getIndex(), event.getMask(), event.getClicks(), event.getX(), event.getY()) :
    				_snippetHelper.genericWidgetSelection(label, event.getIndex(), mask, event.getClicks());
        }
		
		CodeBlock block = new CodeBlock(code);
        getTestBuilder().add(block);
	}
	
	
    /* (non-Javadoc)
     * @see com.windowtester.codegen.TestCaseGenerator#handleTextEntry(java.util.List)
     */
    protected void handleTextEntry(List events) {
    	
    	/*
    	 * Identification errors are silently ignored here...  a warngin message should be produced
    	 */
    	
    	
        SemanticKeyDownEvent kde = (SemanticKeyDownEvent)events.get(0);
        //!pq: tentatively removed
        //handleShellChange(kde);
        
   
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
        			String codeSnippet = _snippetHelper.enterTextSnippet(sb.toString());
        			CodeBlock block = new CodeBlock(codeSnippet);
        			getTestBuilder().add(block);
        			sb = new StringBuffer();
        		}
	    
	        	//handle focus change
	            //!pq: tentatively removing (trusting traverse events to properly ensure focus)
        		//handleFocusChange(textEntryTarget);
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
            		else {
            			controlKey = "SWT.BS";
            		}
            	}
            		
            	controlKey = parseControlKey(kde);
            	
            	//if it's a control char, handle it specially:
            	if (controlKey != null) {
            		//flush the previous keys
            		if (sb.length() > 0) {
            			String codeSnippet = _snippetHelper.enterTextSnippet(sb.toString());
            			CodeBlock block = new CodeBlock(codeSnippet);
            			getTestBuilder().add(block);
            			sb = new StringBuffer();
            		}
            		           		
        			//add import to resolve control key
        			getTestBuilder().addImport(new ImportUnit("org.eclipse.swt.SWT"));
            		//create a control entry snippet
            		String codeSnippet = _snippetHelper.enterKeySnippet(controlKey);
        			CodeBlock block = new CodeBlock(codeSnippet);
        			getTestBuilder().add(block);         		
            	}
            //control sequences (ctrl-A) are also special...
            } else if (kde.isControlSequence()) {
    			//add import to resolve control key
    			getTestBuilder().addImport(new ImportUnit("org.eclipse.swt.SWT"));
        		//create a control entry snippet
        		String codeSnippet = _snippetHelper.enterKeySnippet("SWT.CTRL", key);
    			CodeBlock block = new CodeBlock(codeSnippet);
    			getTestBuilder().add(block);            	
            	
            	
        	} else {  //non-control keys are just appended
                sb.append(key);
            }
        }	
        
        //flush cached keys (if any)
		if (sb.length() > 0) {
			String codeSnippet = _snippetHelper.enterTextSnippet(sb.toString());
			CodeBlock block = new CodeBlock(codeSnippet);
			getTestBuilder().add(block);
		}
  
    }


    private String parseControlKey(SemanticKeyDownEvent kde) {
    	
    	String key = kde.getKey();
    	if (isTab(key))
    		return "SWT.TAB";
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
     * Set the ui focus to a new target.
     * @param newTarget - the new focus target
     */
	protected void handleFocusChange(IWidgetIdentifier newTarget) {
		
		StringBuffer sb = new StringBuffer();
		
        //check for error
        if (newTarget == null) {
            sb.append(createIdErrorMessage("focus change"));
        } else {
    		String label = registerWidget(newTarget);		
    		sb.append(_snippetHelper.setFocus(label));
        }
        
		CodeBlock block = new CodeBlock(sb.toString());
		getTestBuilder().add(block);
	}

    /**
     * @see com.windowtester.codegen.TestCaseGenerator#handleListSelection(com.windowtester.recorder.event.user.SemanticListSelectionEvent)
     */
    protected void handleListSelection(SemanticListSelectionEvent listSelection) {
    	
    	StringBuffer sb = new StringBuffer();
    	
    	IWidgetIdentifier info = listSelection.getHierarchyInfo();
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("list selection"));
        } else {
        	String label = registerWidget(listSelection.getHierarchyInfo());
        	String item = listSelection.getItem();
        	String mask = listSelection.getMask();
        	if (mask != null) {
        		//add SWT import to identify mask
        		getTestBuilder().addImport(new ImportUnit("org.eclipse.swt.SWT"));
        	}
        	int numClicks = listSelection.getClicks();
        	sb.append(_snippetHelper.listSelection(label, item, mask, numClicks));
        }
    	
		CodeBlock block = new CodeBlock(sb.toString());
        getTestBuilder().add(block);
    }
    
	/**
     * @see com.windowtester.codegen.TestCaseGenerator#handleComboSelection(com.windowtester.recorder.event.user.SemanticComboSelectionEvent)
     */
    protected void handleComboSelection(SemanticComboSelectionEvent comboSelection) {
    	
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
    	
		CodeBlock block = new CodeBlock(sb.toString());
        getTestBuilder().add(block);
    }

    /**
     * @see com.windowtester.codegen.TestCaseGenerator#handleDragDrop(com.windowtester.recorder.event.user.SemanticDragEvent, com.windowtester.recorder.event.user.SemanticDropEvent)
     */
    protected void handleDragDrop(SemanticDragEvent drag, SemanticDropEvent drop) {
    	
    	/*
    	 * The current implementation is not as clean as it might be.
    	 * In particular, thre is a selection event that may not be 
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
    	 * 2) handle drag
    	 */
    	
    	IUISemanticEvent dest = drop.getDropTargetEvent();
    	handleDragTo(dest);
   
    }
    
    protected void handleDrop(SemanticDropEvent event) {
    	//not implemented here
    }
    
    private void handleDragTo(IUISemanticEvent event) {
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

		CodeBlock block = new CodeBlock(sb.toString());
		getTestBuilder().add(block);
	}


	private void handleMouseMoveTo(IUISemanticEvent event) {
        StringBuffer sb = new StringBuffer();     
        
        IWidgetIdentifier info = event.getHierarchyInfo();
        
        //check for error
        if (info == null) {
            sb.append(createIdErrorMessage("mouseMoveTo"));
        } else {
            String label = registerWidget(info);
            sb.append(_snippetHelper.mouseMoveToSnippet(label, event.getX(), event.getY()));
        }

        CodeBlock block = new CodeBlock(sb.toString());
        getTestBuilder().add(block);
	}


	protected void handleAssertion(RecorderAssertionHookAddedEvent assertEvent) {
    	String method = getTestBuilder().getFreshMethod(assertEvent.getHookName());
    	MethodUnit assertMethod = new MethodUnit(method,"// TODO Auto-generated method stub" + NEW_LINE);
    	assertMethod.addModifier(Modifier.PROTECTED);
    	assertMethod.addThrows("Exception");
    	CodeBlock block = new CodeBlock(_snippetHelper.methodInvocation(method));
        getTestBuilder().add(block);
        getTestBuilder().addMethod(assertMethod);
    }
    

	protected void handleInspection(SemanticWidgetInspectionEvent event) {
		//no op in legacy API
	}
    
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.TestCaseGenerator#handleWidgetClosed(com.windowtester.recorder.event.user.SemanticWidgetClosedEvent)
	 */
	protected void handleWidgetClosed(SemanticWidgetClosedEvent event) {
		//no op in legacy API
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
    private boolean isControl(String key) {
        return key != null && Character.isISOControl(key.charAt(0)); 
    }
    
    /**
     * Check for a backspace.
     * @param key - the character to check
     * @return true if the key is a backspace
     */
	private boolean isBackSpace(String key) {
		return key.charAt(0) == '\b';
	}
	
    /**
     * Check for a tab.
     * @param key - the character to check
     * @return true if the key is a tab
     */
	private boolean isTab(String key) {
		return key.charAt(0) == '\t';
	}
	
    /**
     * Check for an enter key.
     * @param key - the character to check
     * @return true if the key is an 'enter'
     */
	private boolean isEnter(String key) {
		return key.charAt(0) == ICodeGenConstants.NEW_LINE.charAt(0) || key.charAt(0) == '\r';
	}
	
	
    /**
     * Handle a backspace by deleting a char from the end of the buffer 
     * (if there is a char to delete). 
	 * @param sb - the string buffer
	 */
	private void handleBackspace(StringBuffer sb) {
		//System.out.print("<backspace>");
		int last = sb.length() - 1;
		if (last >= 0) 
			sb.deleteCharAt(last);
	}

//	/**
//     * Handle when a shell change event occurs by injecting the appropriate wait condition
//     * @param event - the UI event
//     */
//    private void handleShellChange(IUISemanticEvent event) {
//        String shellTitle = event.getParentShellTitle();
//        if (_builder.updateCurrentShellTitle(shellTitle)) {
//            if (shellTitle != null) {
//            	addWaitForShell(shellTitle);
//            }
//        }
//    }

	/**
     * Handle when a shell change event occurs by injecting the appropriate wait condition
     * @param shellTitle - the shell title
     */
	private void addWaitForShell(String shellTitle) {
		Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "(requires a wait for: " + shellTitle +")");
		String waitBody = _uiContextInstanceName + ".waitForShellShowing(\"" + shellTitle + "\");" + NEW_LINE;
		CodeBlock wait = new CodeBlock(waitBody);
		getTestBuilder().add(wait);
	}
    
	/**
     * Handle when a shell dispose event occurs by injecting the appropriate wait condition
     * @param shellTitle - the shell title
     */
	private void addWaitForShellDisposed(String shellTitle) {
		if (shellTitle == null || shellTitle.equals("") || shellTitle.length() ==0) {
			//empty shell titles are ignored for now (these are usually component frames)
			Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "wait for empty shell title ignored");
			return;
		}
		Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "(requires a wait for dispose: " + shellTitle +")");
		String waitBody = _uiContextInstanceName + ".waitForShellDisposed(\"" + shellTitle + "\");" + NEW_LINE;
		CodeBlock wait = new CodeBlock(waitBody);
		getTestBuilder().add(wait);
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
