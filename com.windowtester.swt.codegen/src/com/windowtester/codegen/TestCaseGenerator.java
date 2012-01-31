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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.codegen.generator.CodegenSettings;
import com.windowtester.codegen.generator.SetupBlockBuilder;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.debug.Tracer;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.recorder.event.ISemanticEvent;
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
import com.windowtester.recorder.event.user.SemanticTextEntryEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetClosedEvent;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;

/**
 * An abstract base class that iterates over a stream of events and dispatches
 * handling to abstract "handle*(..)" hook methods.
 */
public abstract class TestCaseGenerator implements ICodeGenerator {


	/** The builder instance */
	protected ITestCaseBuilder builder;
	
	protected final SetupBlockBuilder setupBuilder;
	
	/** Cached current widget info for use in ensuring proper focus in text entry handling */
	protected IWidgetIdentifier currentWidgetInfo;

	public static final String NEW_LINE = ICodeGenConstants.NEW_LINE;
	
	/**
	 * Create an instance.
	 * @param builder - the test case builder
	 */
	public TestCaseGenerator(ITestCaseBuilder builder) {
		this(builder, CodegenSettings.forPreferences());
	}

	public TestCaseGenerator(ITestCaseBuilder builder, CodegenSettings codegenSettings) {
		this.builder = builder;
		this.setupBuilder = new SetupBlockBuilder(builder, codegenSettings);

	}
	
	
	public ITestCaseBuilder getTestBuilder() {
		return builder;
	}

	
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.ICodeGenerator#generate(com.windowtester.codegen.eventstream.IEventStream)
	 */
	public String generate(IEventStream stream) {
        
		getTestBuilder().prime();
        
        handleSetup(stream);
                
        while(stream.hasNext()) {
        	ISemanticEvent event = stream.nextEvent();
        	
        	if (isHandledInSetup(event))
        		continue;
        	
       
        	if (event instanceof SemanticWidgetInspectionEvent) {
        		handleInspection((SemanticWidgetInspectionEvent)event);
        	}
        	
            if (event instanceof IUISemanticEvent) {
                IUISemanticEvent uiEvent = (IUISemanticEvent)event;
                //cache the generating widget for focus check in text entry handling
                currentWidgetInfo = uiEvent.getHierarchyInfo();
            }
        	
            //menu (menu)* menuItem <-- TODO: update this grammar (menu*)
            if (event instanceof SemanticMenuSelectionEvent) {
                handleMenuCase(stream, event);
                            
            //keyDown (keyDown)*  [ | in text area]
            //for now only text area related key downs are being captured...
            } else if (event instanceof SemanticKeyDownEvent) {
                List events = new ArrayList();
                events.add(event);
                while(stream.hasNext() && ((event = stream.peek()) instanceof SemanticKeyDownEvent)) {
                	events.add(stream.nextEvent()); //advance
                }
                handleTextEntry(events);
            //text entry
            } else if (event instanceof SemanticTextEntryEvent) {
            	SemanticKeyDownEvent[] keys = ((SemanticTextEntryEvent)event).getKeys();
            	handleTextEntry(Arrays.asList(keys));
            //tree-item
            } else if (event instanceof SemanticTreeItemSelectionEvent) {
            	SemanticTreeItemSelectionEvent e = (SemanticTreeItemSelectionEvent)event;
            	handleTreeItemSelection(e);
            //swing table item
            } else if (event instanceof SemanticTableSelectionEvent) { 
            	SemanticTableSelectionEvent e = (SemanticTableSelectionEvent)event;
            	handleTableItemSelection(e);
            } else if (event instanceof SemanticShellShowingEvent) {
            	SemanticShellShowingEvent show = (SemanticShellShowingEvent)event;
            	/*
            	 * A work-around for the case were a close event causes a 
            	 * progress dialog to pop up.  Waiting for this Shell is NOT
            	 * the right thing to do.
            	 * 
            	 * The case to ignore is where the _last_ event is a waitForShellShowing
            	 */
            	if (!stream.hasNext())
            		Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "Trailing wait for shell event discarded");
            	else {
            		/*
            		 * Another special case.  Progress monitors that pop-up and go away should not be waited 
            		 * for.  To handle these cases we discard any pairs of showing/disposed events that 
            		 * are on the same shell and have no events in between.
            		 */
            		ISemanticEvent next = stream.peek();
            		if (next != null && next instanceof SemanticShellDisposedEvent && sameShell((SemanticShellDisposedEvent)next, show)) {
            			//just advance the stream
            			stream.nextEvent();
            			TraceHandler.trace(ICodeGenPluginTraceOptions.CODEGEN, "show/dispose event pair (" + show.getName() +") ignored");
            		} else {
            			handleShellShowing(show);
            		}
            	}
            } else if (event instanceof SemanticShellClosingEvent) {
            	handleShellClosing((SemanticShellClosingEvent)event);   
            } else if (event instanceof SemanticShellDisposedEvent) {
            	handleShellDisposed((SemanticShellDisposedEvent)event);
            //buttons, etc.
            } else if (event instanceof SemanticWidgetSelectionEvent) {
            	SemanticWidgetSelectionEvent widgetSelection = (SemanticWidgetSelectionEvent)event;
            	if (isButtonSelection(widgetSelection))
            		handleButtonClick(widgetSelection);
            	else if (widgetSelection.isContext()) {
                    handleMenuCase(stream, event);
            	
            	} else {
            		/*
            		 * Special case: dispose of superfluous selection before close
            		 * TODO: push this out to be used by the recorder console
            		 */
            		if (!handledSelectionBeforeCloseSpecialCase(stream, widgetSelection))
            			handleGenericWidgetSelection(widgetSelection); //handle fall-through cases
            	}
            } else if (event instanceof SemanticWidgetClosedEvent) {
            	handleWidgetClosed((SemanticWidgetClosedEvent)event);
            } else if (event instanceof SemanticListSelectionEvent) {
            	SemanticListSelectionEvent listSelection = (SemanticListSelectionEvent)event;
            	handleListSelection(listSelection);	
            } else if (event instanceof SemanticComboSelectionEvent) {
            	SemanticComboSelectionEvent comboSelection = (SemanticComboSelectionEvent)event;
            	handleComboSelection(comboSelection);
            } else if (event instanceof SemanticResizeEvent) {
            	SemanticResizeEvent resizeEvent = (SemanticResizeEvent)event;
            	handleResize(resizeEvent);
            } else if (event instanceof SemanticMoveEvent) {
            	SemanticMoveEvent moveEvent = (SemanticMoveEvent)event;
            	handleMove(moveEvent); 
            } else if (event instanceof RecorderAssertionHookAddedEvent) {
            	RecorderAssertionHookAddedEvent assertEvent = (RecorderAssertionHookAddedEvent)event;
            	handleAssertion(assertEvent);
            } else if (event instanceof SemanticFocusEvent) {
                List events = new ArrayList();
                events.add(event);
                while(stream.hasNext() && ((event = stream.peek()) instanceof SemanticFocusEvent)) {
                	events.add(stream.nextEvent()); //advance
                }
                handleFocus(events);
            } else if (event instanceof SemanticDropEvent) {
            	handleDrop((SemanticDropEvent)event);            	   
            } else if (event instanceof SemanticDragEvent) {
            	ISemanticEvent next = null;
            	do  {
            		next =  stream.peek();	
                	if (next instanceof SemanticDropEvent) {
                		handleDragDrop((SemanticDragEvent)event, (SemanticDropEvent)stream.nextEvent());
                		//signal that we're done
                		next = null;
                	// shell lifecycle events are ignored
                	} else if (isShellLifeCycleEvent(next)) {
                		//advance token and trace
                		TraceHandler.trace(ICodeGenPluginTraceOptions.CODEGEN, "shell lifecyle event " + stream.nextEvent() + " ignored in drag event generation");
                	} else {
                		LogHandler.log("drag event(" + event +") not followed by a drop - got " + stream.peek() + " instead - discarded");
                		//we're done
                		next = null;
            		}
            	} while (next != null); //until we error or drop
            }
        }
         
        return getTestBuilder().build();
        
    }


	private boolean isHandledInSetup(ISemanticEvent event) {
		return setupBuilder.handled(event);
	}

	private void handleSetup(IEventStream stream) {
		MethodUnit setup = setupBuilder.buildSetup(stream);
		if (setup == null)
			return;
		getTestBuilder().addMethod(setup);
	}

	private boolean handledSelectionBeforeCloseSpecialCase(
			IEventStream stream, SemanticWidgetSelectionEvent widgetSelection) {
		if (!stream.hasNext())
			return false;
		ISemanticEvent next = stream.peek();
		if (!(next instanceof SemanticWidgetClosedEvent))
			return false;
		IWidgetIdentifier closedWidget   = ((SemanticWidgetClosedEvent) next).getHierarchyInfo();
		IWidgetIdentifier selectedWidget = widgetSelection.getHierarchyInfo();
		if (closedWidget == null || selectedWidget == null)
			return false;
		//spot check to see if targets are the same -- for now just class will suffice
		if (closedWidget.getClass().equals(selectedWidget.getClass()))
			return true;		

		
		return false;
	}




	/**
	 * Check to see if the events are triggered by the same shell.
	 */
	private boolean sameShell(SemanticShellDisposedEvent dispose, SemanticShellShowingEvent show) {
		String n1 = dispose.getName();
		String n2 = show.getName();
		return n1 == null ? n1 == null : n1.equals(n2);
		
	}

	/**
	 * Check for shell life-cycle event.
	 * @param event the event in question.
	 * @return <code>true</code> if it is a shell lifecycle event
	 */
	private boolean isShellLifeCycleEvent(ISemanticEvent event) {
		return event instanceof SemanticShellClosingEvent ||
			   event instanceof SemanticShellDisposedEvent ||
			   event instanceof SemanticShellShowingEvent;
	}

	/**
	 * Handle menu selections
	 */
	private void handleMenuCase(IEventStream stream, ISemanticEvent event) {
		List events = new ArrayList();
		events.add(event);
		ISemanticEvent top = event; 
		
		while(stream.hasNext() && ((event = stream.peek()) instanceof SemanticMenuSelectionEvent) && isInSameMenu(event, top)) {
		    events.add(stream.nextEvent()); //advance
		}
		SemanticMenuSelectionEvent last = (SemanticMenuSelectionEvent)events.remove(events.size()-1);
		handleMenuInvocation(events, last);
	}


	////////////////////////////////////////////////////////////////////////////
	//
	// Hooks for implementing custom event handling.
	//
	////////////////////////////////////////////////////////////////////////////

	protected abstract void handleDragDrop(SemanticDragEvent drag, SemanticDropEvent drop);

	protected abstract void handleShellClosing(SemanticShellClosingEvent event);

	protected abstract void handleShellDisposed(SemanticShellDisposedEvent event);
	
	protected abstract void handleWidgetClosed(SemanticWidgetClosedEvent event);
	
	protected abstract void handleShellShowing(SemanticShellShowingEvent event);

	protected abstract void handleListSelection(SemanticListSelectionEvent listSelection);

	protected abstract void handleComboSelection(SemanticComboSelectionEvent comboSelection);

	protected abstract void handleMove(SemanticMoveEvent moveEvent);

	protected abstract void handleResize(SemanticResizeEvent resizeEvent);
	
	protected abstract void handleDrop(SemanticDropEvent event);
	
	/**
	 * Handle assert hooks.
	 */
	protected abstract void handleAssertion(RecorderAssertionHookAddedEvent assertEvent);

	
	/**
	 * Handle an inspection (and possible flagged assertion).
	 */
	protected abstract  void handleInspection(SemanticWidgetInspectionEvent event);

	/**
	 * A fall-through method to handle widget selections that do not have defined handling logic. 
	 * Note that this method is only called if no other handler is called for the given event.
	 * @param event - the  event
	 */
	protected abstract void handleGenericWidgetSelection(SemanticWidgetSelectionEvent event);

	/**
	 * Handle a tree-item select UI event.
	 * @param event - the event
	 */
	protected abstract void handleTreeItemSelection(SemanticTreeItemSelectionEvent event);

	/**
	 * Handle a table-item select UI event.
	 * @param event - the event
	 */
	protected abstract void handleTableItemSelection(SemanticTableSelectionEvent event);
	
	/**
	 * Handle a button-click UI event.
	 * @param event - the event
	 */
	protected abstract void handleButtonClick(SemanticWidgetSelectionEvent event);

	/**
	 * Handle a text entry event.
	 * @param events - the list of key events
	 */
	protected abstract void handleTextEntry(List events);

	/**
	 * Handle a focus event.
	 * @param events - the list of focus events
	 */
	protected abstract void handleFocus(List events);

	
	/**
	 * Handle a menu invocation event.
	 * @param events - the list of menu selection events
	 * @param last - the item selection event
	 */
	protected abstract void handleMenuInvocation(List events, SemanticMenuSelectionEvent last);

	////////////////////////////////////////////////////////////////////////////
	//
	// Helper predicates
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Check whether this is a button selection event.
	 * @param event - the event in question
	 * @return true if it is a button-selection
	 */
	private boolean isButtonSelection(SemanticWidgetSelectionEvent event) {
		return event.getItemClass().equals("org.eclipse.swt.widgets.Button");
	}

	/**
	 * Check to see if these two events are in the same menu.
	 */
	private boolean isInSameMenu(ISemanticEvent event, ISemanticEvent top) {
		
		//these should both be ISWTSemanticEvents
		if (!(event instanceof IUISemanticEvent) || !(top instanceof IUISemanticEvent))
			return false;
		
		IUISemanticEvent one = (IUISemanticEvent)event;
		IUISemanticEvent two = (IUISemanticEvent)top;
		
		if (one.getHierarchyInfo() == null || two == null)
			return false;
		return one.getHierarchyInfo().equals(two.getClass());
	}


	
}
