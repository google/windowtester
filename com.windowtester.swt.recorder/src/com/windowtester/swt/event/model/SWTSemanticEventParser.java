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
package com.windowtester.swt.event.model;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.event.StyleBits;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.ISemanticSelectionEvent;
import com.windowtester.recorder.event.user.SemanticFocusEvent;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticListSelectionEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetClosedEvent;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.TreeEventType;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.swt.event.model.factory.SWTSemanticEventFactoryImplV2;

/**
 * 
 * While the <code>SWTSemanticEventInterpreter</code> translates one event, this stateful parser
 * handles streams of events.
 * 
 * <br><br>
 * The grammar of SWT events has an effective look-ahead of one token.  To accommodate this,
 * we implement a buffer where we store the last event seen.  This event only gets dispatched
 * from the parser <em>after</em> the next event is processed.  Clients of the parser must 
 * call <code>flush()</code> on event stream termination to ensure that the buffer is flushed 
 * (otherwise a token may remain cached and undispatched).
 * 
 * @see com.windowtester.swt.event.model.SWTSemanticEventInterpreter
 */
public class SWTSemanticEventParser {

	/** The interpreter used for single event translations.*/
	private final SWTSemanticEventInterpreter _interpreter;

	/** The buffered last seen event */
	private IUISemanticEvent _buffered;
	/** Temporary event references */
	private IUISemanticEvent _fresh;
	private IUISemanticEvent _result;

	/** A buffered last seen typed event for dnd handling */
	private TypedEvent _lastTypedEvent;

	private Event _lastEvent;
	
	/**
	 * Create an instance.
	 * @param interpreter
	 */
	public SWTSemanticEventParser(SWTSemanticEventInterpreter interpreter) {
		_interpreter = interpreter;
	}

    /**
     * Translates the given SWT widget event into an SWT semantic event.
     * @return the semantic event -- or <code>null</code> if there is none (and it should be ignored)
     */
	public IUISemanticEvent parse(final Event event) {
		
		/*
		 * invalidate our typed event back pointer
		 */
		_lastTypedEvent = null;
		
		
		//System.out.println("parsing: " + event);
		
		_fresh = _interpreter.interpret(event);
		if (_fresh == null)
			return _fresh;   //fast fail
			
		if (_fresh instanceof SemanticWidgetInspectionEvent) {
			_buffered = _fresh;			
		}
		
		
		//check for double-click case
		if (_fresh.getClicks() == 2) {
			_result = handleDoubleClick(_buffered);
			_buffered = null;
		} else { 
				
			
			/*
			 * (Arrow) traversal events may be followed by a selection.
			 * This selection is bogus and should be ignored. 
			 */
			if (isArrowTraversalCase(_buffered, _fresh)) {
				_result   = _buffered;
				_buffered = null;
			
			/*
			 * If a shell shows that is not the shell associated with the next
			 * event, assume it is transient and can be discarded
			 */	
			} else if (isSuperfluousShellChangeEvent(_buffered, _fresh)){	
				//dispose of superfluous Shell event
				_result   = _buffered;
				_buffered = null;
			} else if (isWidgetDisposalCase(_buffered, _fresh)) {
				handleWidgetCloseCase();
			} else if (isCRCase(_buffered, _fresh)) {
				//dispose of superfluous CR
				_result   = _buffered;
				_buffered = null;
			/*
			 * Focus events that succeed tab traversals can be ignored.
			 */
			} else if (isTabTraversalEvent(_buffered) && _fresh instanceof SemanticFocusEvent) {
				//System.out.println("focus ignored: " + _fresh);
				_result = _buffered;
				_buffered = null;
				
			/**
			 * Workaround for the demo to ensure that all changes to focus on a text
			 * box are guaranteed to create a setFocus event.  
			 * NOTE: this is a TEMPORARY FIX: it will become unnecessary when we have
             * proper traversal event handling.
			 */
			} else if (targetIsText(_buffered)) {
				//System.out.println("target is text!");
				//just advance to the next token
				advanceToNextToken();
			} else if (_buffered instanceof SemanticFocusEvent && (!(_fresh instanceof SemanticKeyDownEvent) /*&& !(_fresh instanceof SemanticFocusEvent)*/ )) {
				/**
				 * Focus events that precede anything BUT text entry and other focus events can be discarded.
				 * Setting focus is redundant...
				 */
				//uncommenting focus events causes lots of superfluous focus changes to be codegened
				
				
				_buffered = _fresh;  
				_result   = null;  
			} 
			else {
				
				/**
				 * Check for two focus events for the same element in a row; in which case,
				 * we can discard one
				 */
				if (_buffered instanceof SemanticFocusEvent && _fresh instanceof SemanticFocusEvent && _buffered.getHierarchyInfo().equals(_fresh.getHierarchyInfo())) {
					_buffered = _fresh;
					_result   = null;    //this check may or may not help (they could be an intervening ALT-TAB) keystroke that gets ignored later
										 //a second fix occurs in code-gen
				} else if (isPullDownMenuCase(_fresh)) {
					
					/*
					 * Strategy: parcel pull-down target in parent info of menu select
					 */
					final SemanticMenuSelectionEvent menuSelect = (SemanticMenuSelectionEvent)_fresh;
					IWidgetIdentifier info = menuSelect.getHierarchyInfo();
					
					if (_buffered != null) {

						// note: only addressing NEW API case
						if (info instanceof SWTWidgetLocator) {
							IUISemanticEvent parent = _buffered;
							// advance the fresh menu select to the buffer
							_buffered = _fresh;
							// and update its hierarchy info with a synthesized
							// pull-down:
							_buffered.setHierarchyInfo(new SWTSemanticEventFactoryImplV2()
											.createPullDownMenuSelection((SWTWidgetLocator) parent.getHierarchyInfo(), menuSelect));
							// set current to null
							_result = null;
						} else {
							
							if (_lastEvent != null) {
								
								SWTWidgetLocator pullDownHost = getPullDownHostLocator(event);
								
								if (pullDownHost == null) {
									advanceToNextToken();
								} else {
									_buffered = _fresh;
									_buffered.setHierarchyInfo(new SWTSemanticEventFactoryImplV2().createPullDownMenuSelection(pullDownHost, menuSelect));
									// set current to null
									_result = null;									
								}
							} else {
								advanceToNextToken();
							}
						}
					} else {
						
						
						//view case?
						String viewID = getActiveViewPartId();
						if (viewID == null)
							advanceToNextToken();
						else  {
							_fresh.setHierarchyInfo(new SWTSemanticEventFactoryImplV2().createPullDownMenuSelection(new ViewLocator(viewID), menuSelect));
							_buffered = _fresh;
							_result = null;	
						}
					}
				} else {
					advanceToNextToken();
				}
			}
		}	
		//buffer for view menu source
		_lastEvent = event;
		return sanityCheck(_result);
	}


	//last chance to filter results
	private IUISemanticEvent sanityCheck(IUISemanticEvent result) {
		if (isSpuriousPulldownMenuSelection(result))
			return null;
		return result;
	}

	/**
	 * @param result
	 * @return
	 * @since 3.9.1
	 */
	private boolean isSpuriousPulldownMenuSelection(IUISemanticEvent result) {
		if (result == null)
			return false;
		IWidgetIdentifier info = result.getHierarchyInfo();
		return result instanceof SemanticMenuSelectionEvent && info == null;
	}

	private SWTWidgetLocator getPullDownHostLocator(Event currentEvent) {
		if (isViewPullDown(currentEvent)) {
			Widget lastWidget = _lastEvent.widget;
			return ViewFinder.findInViewStack(lastWidget);			
		}
		return (SWTWidgetLocator) _buffered.getHierarchyInfo();
	}

	private boolean isViewPullDown(Event event) {
		Widget widget = event.widget;
		if (!(widget instanceof MenuItem))
			return false;
		MenuItem item = (MenuItem)widget;
		Object data = item.getData();
		if (!(data instanceof ActionContributionItem))
			return false;
		ActionContributionItem action = (ActionContributionItem)data;
		IContributionManager parent = action.getParent();
		return isViewManager(parent);
	}
	
	private boolean isViewManager(IContributionManager parent) {
		if (parent == null)
			return false;
		String name = parent.getClass().getName();
		if (name.startsWith("org.eclipse.ui.internal.ViewPane"))
			return true;
		if (parent instanceof MenuManager)
			return isViewManager( ((MenuManager)parent).getParent());
		return false;
	}

	private void handleWidgetCloseCase() {
		//update close target
		_fresh.setHierarchyInfo(_buffered.getHierarchyInfo());
		//advance
		_buffered =_fresh;
		/*
		 * NOTE: since we are eagerly flushing, the codegenerator will have to ignore the selection before the close
		 */
	}


	private boolean isWidgetDisposalCase(IUISemanticEvent buffered, IUISemanticEvent fresh) {
		return (buffered instanceof SemanticWidgetSelectionEvent && fresh instanceof SemanticWidgetClosedEvent);
	}

	private void advanceToNextToken() {
		//just advance to the next token
		_result = _buffered;
		_buffered = _fresh;
	}

	private String getActiveViewPartId() {
		try {
			return (String) DisplayExec.sync(new RunnableWithResult() {
				public Object runWithResult() {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IWorkbenchPart part = page.getActivePart();
					if (!(part instanceof IViewPart))
						return null;
					IViewPart view = (IViewPart) part;
					return view.getSite().getId();
				}
			});
		} catch (Throwable e) {
			return null;
		}
	}
	
	
    private boolean isPullDownMenuCase(IUISemanticEvent event) {
    	if (!(event instanceof SemanticMenuSelectionEvent)) 
    		return false;
    	SemanticMenuSelectionEvent menuSelect = (SemanticMenuSelectionEvent)event;
    	if (StyleBits.isPullDown(menuSelect.getStyle()))
    		return true;
		return false;
	}

	/**
     * Translates the given SWT typed event into an SWT semantic event.
     * @return the semantic event -- or <code>null</code> if there is none (and it should be ignored)
     */
	public IUISemanticEvent parse(TypedEvent event) {
		
		/*
		 * N.B. Typed events bypass our buffering scheme.
		 * flush() should have been called previous to this parse.
		 */
		
		IUISemanticEvent current = _interpreter.interpret(event);
	
		/*
		 * for some reason drops are producing two events...
		 * in this case we want to nullify one of them
		 */
		if (_lastTypedEvent != null && _lastTypedEvent instanceof DropTargetEvent) {
			DropTargetEvent lastDrop = (DropTargetEvent)_lastTypedEvent;
			if (event instanceof DropTargetEvent) {
				DropTargetEvent currentDrop = (DropTargetEvent)event;
				if (lastDrop.data == currentDrop.data)
					current = null;
			}	
		}
		
		//update pointer
		_lastTypedEvent = event;
		return current;
		
	}
	
	
	private boolean isSuperfluousShellChangeEvent(IUISemanticEvent last, IUISemanticEvent current) {

		//TODO: reimplement this? (parent shell info has been refactored away...)
		return false;
		
//		if (!(last instanceof SemanticShellShowingEvent))
//			return false;
//		return  last.getParentShellTitle().equals(current.getParentShellTitle());
	}

	//widgetSelect(w=x) keyDown('\r' w=x)
	private boolean isCRCase(IUISemanticEvent last, IUISemanticEvent current) {
		
		//System.out.println("&&&&comparing: " + last + " to " + current);
		
		if (!(last instanceof SemanticWidgetSelectionEvent))
			return false;
		
		if (!(current instanceof SemanticKeyDownEvent))
			return false;
		SemanticKeyDownEvent kde = (SemanticKeyDownEvent)current;
		
		
		//System.out.println("&&&&checking char");
		
		
		//if it's not a carriage return
		if (!"\r".equals(kde.getKey()))
			return false;
			
		//System.out.println("&&&&checking hierarchy info: " + last + " " + current);
		
		return sameHierarchyInfo(last, current);			
	}

	
	
	private boolean isArrowTraversalCase(IUISemanticEvent last, IUISemanticEvent current) { 
		if (!(last instanceof SemanticKeyDownEvent))
			return false;
		SemanticKeyDownEvent kde = (SemanticKeyDownEvent)last;
		
		if (!isArrowTraversalEvent(kde))
			return false;
		
		if (!(current instanceof ISemanticSelectionEvent))
			return false;
		
		/*
		 * Finally, we want to make sure that the events are associated with the same widget.
		 */
		
		return sameHierarchyInfo(last, current);
		
	}

	private boolean sameHierarchyInfo(IUISemanticEvent last, IUISemanticEvent current) {
		IWidgetIdentifier info1 = last.getHierarchyInfo();
		IWidgetIdentifier info2 = current.getHierarchyInfo();
		if (info1 == null)
			return info2 == null;
		return info1.equals(info2);
	}

	private boolean isArrowTraversalEvent(SemanticKeyDownEvent kde) {
	   	switch(kde.getKeyCode()) {
	   		case SWT.ARROW_RIGHT :
	   			return true;
	   		case SWT.ARROW_LEFT :
	   			return true;
	   		case SWT.ARROW_UP :
	   			return true;
	   		case SWT.ARROW_DOWN :
	   			return true;
	   		default :
	   			return false;
	   	}
	}

	private boolean isTabTraversalEvent(IUISemanticEvent event) {
		if (!(event instanceof SemanticKeyDownEvent))
			return false;
		SemanticKeyDownEvent keyDown = (SemanticKeyDownEvent)event;
		return "\t".equals(keyDown.getKey());
	}

	/**
	 * Check to see if the target of this event is a Text widget.
	 * !pq: note: this is a temporary workaround, when traverse events
	 * are properly handled, this should go away.
	 */
	private boolean targetIsText(IUISemanticEvent event) {
		/*
		 * day before release: treading lightly (i.e., lots of guards)
		 * 
		 */
		
		if (event == null)
			return false;
		IWidgetIdentifier info = event.getHierarchyInfo();
		if (info == null)
			return false;
		
		String clsName = info.getTargetClassName();
		if (clsName == null)
			return false;
		
		//System.out.println("target is: " + clsName);
		return clsName.equals("org.eclipse.swt.widgets.Text");
	}

	/**
	 * Flush the buffer, returning the buffered event.
	 * @return the buffered event (or null if there is none)
	 */
	public IUISemanticEvent flush() {
		_result   = _buffered;
		_buffered = null;
		return sanityCheck(_result);
	}
	
	
	/**
	 * Handle the double-click case.
	 */
	private IUISemanticEvent handleDoubleClick(IUISemanticEvent event) {
		
		if (event == null) {  //no buffered event 
			return _fresh;
		} else if (event instanceof SemanticTreeItemSelectionEvent) {
			SemanticTreeItemSelectionEvent treeSelect = (SemanticTreeItemSelectionEvent)event;
			//TreeEventType type = treeSelect.getType();
			//assert type == single-click here...
			treeSelect.setType(TreeEventType.DOUBLE_CLICK);
		} else if (event instanceof SemanticListSelectionEvent) {
			SemanticListSelectionEvent listSelect = (SemanticListSelectionEvent)event;
			listSelect.setClicks(2);
		} else if (event instanceof SemanticWidgetSelectionEvent) {
			IWidgetIdentifier current = event.getHierarchyInfo();
			//tables require special treatment
			//TODO[pq]: this isn't right -- investigate!
			if (current.equals(Table.class) && TableItem.class.equals((_buffered.getHierarchyInfo()).getTargetClass())) {
				//discard Table selection
				event = _buffered;
			}
	
			SemanticWidgetSelectionEvent wSelect = (SemanticWidgetSelectionEvent)event;
			wSelect.setClicks(2);
		}  
		//TODO: combos?
	
		return event;
	}



	
}
