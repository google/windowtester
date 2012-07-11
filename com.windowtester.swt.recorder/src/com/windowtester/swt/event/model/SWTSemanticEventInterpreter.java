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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.tester.swt.ButtonTester;
import abbot.tester.swt.TreeTester;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.debug.Tracer;
import com.windowtester.internal.runtime.MouseConfig;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticDropEvent;
import com.windowtester.recorder.event.user.SemanticEventAdapter;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.TreeEventType;
import com.windowtester.recorder.event.user.UISemanticEvent;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.swt.event.model.dnd.DropTargetHelper;
import com.windowtester.swt.event.recorder.IEventRecorderPluginTraceOptions;
import com.windowtester.swt.event.spy.SpyEventHandler;
import com.windowtester.swt.util.SelectionDeltaParser;

/** 
 * A simple stateful interpreter for semantic events.  
 */
public class SWTSemanticEventInterpreter extends SemanticEventAdapter {
	
	//enable/disable interpreter tracing to the console
	private static final boolean CONSOLE_TRACE = false;

	/** Flags to indicate if this selection is associated with a mouse event */
	private boolean _singleClick, _doubleClick; 
		
	/** A helper for interpreting mouse events */
	//private MouseEventInterpreter _mouseHandler = new MouseEventInterpreter();
	
	/** A helper for interpreting tree mouse events */
	private TreeEventInterpreter _treeEventHandler = new TreeEventInterpreter();
	
	/** A helper for interpreting list selection events */
	private ListSelectionHandler _listSelectionHandler;

	/** A helper for interpreting browser selection events */
	private BrowserHandler _browserHandler;
	
	/** A helper for interpreting table item selection events */
	//private TableSelectionHandler _tableSelectionHandler;
	
	/** A helper for interpreting text traversal events */
	private ControlTraversalHandler _controlTraversalHandler = new ControlTraversalHandler();
	
	/** A helper for interpreting text traversal events */
	//private KeyEntryHandler _keyEntryHandler = new KeyEntryHandler();
	
	/** A callback to update raw event listening filters */
	private IEventRecorderCallBack _recorderCallBack;
	
	/** A stack for caching key strokes */
	private Stack _keyStack = new Stack();

	/** A reference to the currently clicked button (useful for determining context click cases) */
	private int _button;

	/** A reference to the target of a context click */
	private Widget _contextTarget;
	
	/** A back pointer to a _cCombo for use in handling cCombo selections */
	private CCombo _cComboTarget;

	/** A reference to the control that has current display focus
	 *  This is required to address a bug in detecting double-clicks on items in controls that
	 *  have initial focus.  The rub is that we use the FocusIn event to add handlers that detect the
	 *  double click.  Controls with initial focus don't have such an event so handlers are not added.
	 *  Our workaround is to check on mouse entry whether this current focus pointer is set.  If not,
	 *  it is updated to the Display's control in current focus.
	 *  
	 *  TODO: this work-around can probably be made to go away in light of the new traversal
	 *  handler logic.
	 */
	private Control _controlInFocus;
	
	private boolean _isDoubleClickEventCase;

	
	private UISemanticEvent _contextTableSelection;

	/** A pointer to the last mouse event for use in click statemask discovery */
	private Event _lastMouseEvent;
	
	/** a pointer to a detail widget in mouse events --- used to detect user-driven disposals in things like ctabfolders */
	private Widget _lastMouseEventDetail;

	
	private SemanticTreeItemSelectionEvent _contextMenuTreeItemTarget;

	/** A map of drop targets to controls --- used for dnd event generation */
	private final Map _dropTargetMappings = new HashMap();

	/** Check state for use in table item selection creation */
	private boolean _isCheckSelection;
	

	private final DNDHelper _dndHelper = new DNDHelper();

	private Event lastEvent; //cached to avoid duplicate handlings of the same event

	private final SpyEventHandler spyHandler = new SpyEventHandler();

	private UISemanticEvent cachedDragStart;
	
	
    /**
     * Translates the given SWT typed event into an SWT semantic event.
     * @return the semantic event -- or <code>null</code> if there is none (and it should be ignored)
     */
	public IUISemanticEvent interpret(TypedEvent event) {
		IUISemanticEvent semantic = null;
		
		if (event instanceof DropTargetEvent) {
			semantic = handleDrop((DropTargetEvent)event);
		} else { 		
			LogHandler.log("typed event of type: " + event.getClass() + " ignored");	
		}
		return semantic;
	}
	
	/**
     * Translates the given SWT widget event into an SWT semantic event.
     * @return the semantic event -- or <code>null</code> if there is none (and it should be ignored)
     */
    public UISemanticEvent interpret(Event event) {
    	    	
    	//1.16/2008: oddly: text entry events are getting doubled...
    	//this guard protects against this --- TODO: investigate, fix and remove guard!
    	if (isDuplicateEvent(event))
    		return null;
    	
    	traceInterpretingEvent(event);
    	
    	UISemanticEvent semantic = null;
		Widget widget             = event.widget;
		switch (event.type) {
			case SWT.Activate :
				handleActivate(widget);
				break;
			case SWT.Deactivate :
				handleDeActivate(widget);
				break;
			case SWT.MouseDown :
				handleMouseDown(event);
				break;
			case SWT.MouseUp :
				semantic = handleMouseUp(event, semantic, widget);
				break;
			case SWT.MouseDoubleClick :
				semantic = handleDoubleClick(event, widget);
				break;
			case SWT.MouseEnter :
				handleMouseEnter(widget);
				break;
			case SWT.FocusIn   :
				semantic = handleFocusIn(event, widget);
				break;
			case SWT.FocusOut   :
				semantic = handleFocusOut(widget);
				break;
			case SWT.Arm :
				semantic = handleArm(event, semantic, widget);
				break;
			case SWT.Selection :
				semantic = handleSelection(semantic, widget, event);
				break;
			case SWT.KeyDown :
				semantic = handleKeyDown(event, semantic);
				break;
			case SWT.KeyUp :
				handleKeyUp();
				break;
			case SWT.Show :
				semantic = handleShow(event, widget);
				break;
			case SWT.Close :
				semantic = handleClose(event, widget);
				break;	
			case SWT.Dispose :
				semantic = handleDispose(event, widget);
				break;	
			case SWT.Move :
				semantic = handleMove(event, widget);
				break;
			case SWT.Resize :
				semantic = handleResize(event, widget);
				break;	
			case SWT.Expand :
				semantic = handleExpand(event, widget);
				break;
			case SWT.Collapse :
				semantic = handleCollapse(event, widget);
				break;
			case SWT.DragDetect :
				semantic = handleDragDetect(event, widget); //TODO: is this ever seen?
				break;
			case SWT.Hide :
				//ignored for now
				break;
			case SWT.MouseHover :
				semantic = handleHover(event);
				break;
			default : /* raw event */
				semantic = handleDefault(event);
				break;
		}
		
		return semantic;
    }


	private boolean isDuplicateEvent(Event event) {
		Event cached = lastEvent;
    	lastEvent = event; //advance
    	return event == cached;
   }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Specific event type handlers
    //
    ////////////////////////////////////////////////////////////////////////////////////////
	
	private UISemanticEvent handleDragDetect(Event event, Widget widget) {
		/*
		 * sigh -- another special case:
		 * 
		 * In case an explicit selection event is not generated for the drag start, this cached
		 * locator will be used later to recreate it.
		 */
		cachedDragStart = createDragStartSelectionEvent(event);
		return _dndHelper.getDragSource();
	}

	private UISemanticEvent createDragStartSelectionEvent(Event event) {
		
		if (event.widget instanceof Tree) {
			UISemanticEvent treeSelection = _treeEventHandler.getEvent();
			if (treeSelection != null)
				return treeSelection;
		}
		
		return SWTSemanticEventFactory.createWidgetSelectionEvent(event);
	}

	/**
	 * Handle activation.
	 * (We need to track activation events in case a focus event was not sent.)
	 */
	private void handleActivate(Widget widget) {
	    // addHandlers(w);
		// just add traversal handler
		if (widget instanceof Control) {
			Control control = (Control) widget;
			//only add handler if control has focus
			if (control.isFocusControl())
				addControlFocusHandler(control);
		}
		
		if (widget instanceof Browser) {
			_browserHandler = new BrowserHandler((Browser)widget);
		}
		
		
		/*
		 * N.B. there is no need to handle de-activations as listeners will be
		 * removed on focus out (TODO: and pause?)
		 */
	}
	
	private void handleDeActivate(Widget widget) {
	
		if (widget instanceof Browser) {
			if(_browserHandler!=null){
				_browserHandler.stopListening();
				_browserHandler = null;
			}
		}
	}
	
	private void addControlFocusHandler(Control control) {
//		_keyEntryHandler.startListeningTo(control);
		_controlTraversalHandler.startListeningTo(control);	
		_dndHelper.startListeningTo(control);
	
	}

	private UISemanticEvent handleDoubleClick(Event event, Widget widget) {
		
		UISemanticEvent semantic = createDragStartSelectionEvent(event);
		semantic.setClicks(2);
		if (widget instanceof Canvas) //canvases use (x,y) info for playback
			semantic.setRequiresLocationInfo(true);
		_isDoubleClickEventCase = true;
		return semantic;
	}


	private void handleMouseEnter(Widget widget) {
		/**
		 * Workaround to ensure that widget's with initial focus have handlers added.
		 */
		if (_controlInFocus == null) {
			_controlInFocus = widget.getDisplay().getFocusControl();
			//add the appropriate handlers...
			addHandlers(_controlInFocus);
		}
	}

   
	/**
	 * Handle a hover event.
	 */
	private UISemanticEvent handleHover(Event event) {
		return spyHandler.interepretHover(event);
	}
	
    /////////////////////////////////////////////////////////////////////////////////////
    //
    // Specific event type handlers
    //
    /////////////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Handle drop event.
	 */
    private IUISemanticEvent handleDrop(DropTargetEvent event) {
    	Control target = (Control) _dropTargetMappings.get(event.widget);
		SemanticDropEvent dropEvent = SWTSemanticEventFactory.createDropEvent(event, target);
		UISemanticEvent dragSource = _dndHelper.getDragSource();
		if (dragSource != null) {
			dropEvent.withSource(dragSource);
		} else {
			if (cachedDragStart != null)
				dropEvent.withSource(cachedDragStart);
		}
		return dropEvent;
	}
	
    /**
     * Handle close event.
     */
	private UISemanticEvent handleClose(Event event, Widget widget) {
		if (widget instanceof Shell)
			return SWTSemanticEventFactory.createShellClosingEvent(event);
		return null;
	}
	
    /**
     * Handle dispose event.
     */
	private UISemanticEvent handleDispose(Event event, Widget widget) {
		if (widget instanceof Shell)
			return SWTSemanticEventFactory.createShellDisposedEvent(event);
		if (widget instanceof CTabItem) {
			if (lastEventWasAClickOnThisWidget(widget))
				return SWTSemanticEventFactory.createWidgetClosedEvent(widget);
		}
		return null;
	}
	
	

	private boolean lastEventWasAClickOnThisWidget(Widget widget) {
		if (_lastMouseEvent == null)
			return false;
		boolean isMouseEvent = (_lastMouseEvent.type == SWT.MouseDown || _lastMouseEvent.type == SWT.MouseUp);
		boolean isWidget = (widget == _lastMouseEvent.widget || widget == _lastMouseEvent.item || widget == _lastMouseEventDetail);
		return isMouseEvent && isWidget;
	}

	/**
	 * Handle resize event.
	 */
	private UISemanticEvent handleResize(Event event, Widget widget) {
//TODO: need to identify resize events that are strictly USER-generated
//		if (widget instanceof Control)
//			return SWTSemanticEventFactory.createResizeEvent(event, widget);
		return null;
	}

	/**
	 * Handle move event.
	 */
	private UISemanticEvent handleMove(Event event, Widget widget) {
//		TODO: need to identify resize events that are strictly USER-generated
//		if (widget instanceof Control)
//			return SWTSemanticEventFactory.createMoveEvent(event, widget);
		return null;
	}
	
    /**
     * Handle show event.
     */
	private UISemanticEvent handleShow(Event event, Widget widget) {
		//notice we have to ignore Shells associated with cCombos
		if (widget instanceof Shell && _cComboTarget == null) {
			//an approximation here --- we're assuming that shells with no children (contents?)
			//are transient and uninteresting
			//this issue can be reproduced via selection events on GEF palette items which trigger
			//an empty shell to show
			if (shellHasTitleOrChildren((Shell) widget))
				return SWTSemanticEventFactory.createShellShowingEvent(event);
		}
		return null;
	}


	private boolean shellHasTitleOrChildren(Shell shell) {
		try {
			String text = shell.getText();
			if (text != null && text.trim().length() != 0)
				return true;
			if (shell.getChildren().length != 0)
				return true;
		} catch (Exception e) { // $codepro.audit.disable emptyCatchClause
			/*
			 * assuming that an exception is an indication of a really transient shell which can be ignored
			 */
		}
		return false;
	}



    /**
     * Handle collapse event.
     */
	private UISemanticEvent handleCollapse(Event event, Widget widget) {
		//collapses are not supported fully, this is just a place holder
		//return SWTSemanticEventFactory.createTreeItemSelectionEvent(widget, TreeEventType.COLLAPSE, SWT.BUTTON1);
		return null;
	}

    /**
     * Handle expand event.
     */
	private UISemanticEvent handleExpand(Event event, Widget widget) {
		return null;
		//expands are not supported fully, this is just a place holder
	//	return SWTSemanticEventFactory.createTreeItemSelectionEvent(widget, TreeEventType.EXPAND, SWT.BUTTON1);
	}
	
	
	/**
	 * Handle focus in change.
	 * @param event 
	 */
	private UISemanticEvent handleFocusIn(Event event, Widget widget) {
		
		//add the appropriate handlers...
		addHandlers(widget);
		
		traceEvent("focus in: " + widget);				
		/**
		 * Focus events should only be associated with Controls so a cast should be safe...  still, 
		 * we'll be careful.
		 */
		_controlInFocus = widget instanceof Control ? (Control)widget : null;
		
		//again, be safe and ignore the (hopefully impossible) null case
//		if (_controlInFocus != null)
//			return SWTSemanticEventFactory.createFocusEvent(event, widget);
		return null;
	}

	/**
	 * Add widget-specific handlers/listener. 
	 */
	private void addHandlers(Widget widget) {
		
		/**
		 * All controls get a traversal handler and a drop target listener
		 */
		if (widget instanceof Control) {
			//System.out.println("adding traversal listener to: " + widget);
			Control control = (Control)widget;
			addControlFocusHandler(control);
			addDropTargetListener(control);
		}
		
		//when trees get focus we want to setup a listener to inform us of selection events
		if (widget instanceof Tree) {
			Tree tree = (Tree)widget;
			//tree.addListener(SWT.Selection, _mouseHandler);
			//tree.addTreeListener(_mouseHandler);
			//tree.addMouseListener(_mouseHandler);
			tree.addListener(SWT.Selection, _treeEventHandler);
			tree.addMouseListener(_treeEventHandler);
			tree.addTreeListener(_treeEventHandler);
		//similarly with CTabFolders
		} else if (widget instanceof CTabFolder) {
			//TODO: add CTabFolder support here...
		} else if (widget instanceof List) {
			List list = (List)widget;
			_listSelectionHandler = new ListSelectionHandler(list);
		} else if (widget instanceof Table) {
			//_tableSelectionHandler = new TableSelectionHandler((Table)widget);
		} 
	}
	

	/**
	 * Add a callback to the event recorder whcih is used to update
	 * filters on primitive events (e.g., when adding a drop target listener)
	 * @param recorder
	 */
	public void addEventRecorderCallBack(IEventRecorderCallBack recorder) {
		_recorderCallBack = recorder;
	}
	
	
	private void addDropTargetListener(final Control control) {

		
		
		if (_recorderCallBack == null) {
			LogHandler.log("recorder callback is null; drop target listener not added");
			return;
		}

		DropTarget dropTarget = DropTargetHelper.findDropTarget(control);
		//if a target was found, update the recorder and register a mapping
		if (dropTarget != null) {
			//tell the recorder to list to this target
			_recorderCallBack.listenForDropEvents(dropTarget);
			//remember the owner control; for use in semantic event generation	
			_dropTargetMappings.put(dropTarget, control);
		}
		
		
		
//		IDropHandler dropHandler = DropHandlerFactory.createHandler(control);
//		//tell the recorder to listen to drops on this target
//		_recorderCallBack.addDropHandler(dropHandler);
		
		
		
		
//		try {
//
////			DropTarget dropTarget = (DropTarget) control.getData("DropTarget"); // note
////			Control parent = null;
////			
////			if (dropTarget == null) {	
////				ControlTester tester = new ControlTester();
////				
////				parent = tester.getParent(control);
////				while (parent != null && dropTarget == null) {
////					dropTarget = (DropTarget) parent.getData("DropTarget"); 
////					parent = tester.getParent(parent);
////				}
////				
////				if (dropTarget == null)
////					return; //ignored
////			}
////				
////			
////			dropTarget.addDropListener(new DropTargetAdapter() {
////
////				public void drop(DropTargetEvent event) {
////					System.out.println("dropped: " + event.widget + " - " + event.item + " "
////							+ event.x + ", " + event.y + " (display relative)");
////					
////					Display d = event.widget.getDisplay();
////					System.out.println("relative to control: " + control + ":");
////					System.out.println(d.map(null, control, event.x, event.y));
////					
////				}
////			});
//
////			_recorderCallBack.listenForDropEvents(dropTarget);
//			
//			
//			
//
//		} catch (Throwable t) {
//			LogHandler.log(t);
//		}
		
	}

	/**
	 * Handle focus out change.
	 */
	private UISemanticEvent handleFocusOut(Widget widget) {
		
		//fetch the associated traversal event (if there is one)
		UISemanticEvent e = _controlTraversalHandler.getEvent();
		//UISemanticEvent e = _keyEntryHandler.getEvent();
		
		//System.out.println("..removing handlers for: " + widget);
		removeHandlers(widget);
		
		return e;
	}
	
	/**
	 * Remove widget-specific handlers/listener. 
	 */
	private void removeHandlers(Widget widget) {
		
		/**
		 * All controls get a traversal handler
		 */
		if (widget instanceof Control) {
			Control control = (Control)widget;
			//_controlTraversalHandler.stopListeningTo(control);
			removeControlFocusHandler(control);
			
		}
		
		
		//when trees go out of focus we want to remove the associated tree event listener
		if (widget instanceof Tree) {
			Tree tree = (Tree)widget;
//			tree.removeListener(SWT.Selection, _mouseHandler);
//			tree.removeTreeListener(_mouseHandler);
//			tree.removeMouseListener(_mouseHandler);
//			
			tree.addMouseListener(_treeEventHandler); //TODO: shouldn't this be a REMOVE?
			tree.addTreeListener(_treeEventHandler);
			
		//same with CTabFolders
		} else if (widget instanceof CTabFolder) {
			//TODO: add CTabFolder support here...
		} else if (widget instanceof List) {
			//TODO: setting this to null here is not safe since the focus out event gets called before the handler is queried
			//_listSelectionHandler = null;
		} else if (widget instanceof Table) {
			//_tableSelectionHandler = null;
		}
	}

	/**
	 * Remove control focus handler.
     */ 
	private void removeControlFocusHandler(Control control) {
		_controlTraversalHandler.stopListeningTo(control);
		_dndHelper.stopListeningTo(control);
	}
	
	/**
	 * Handle key down events. 
	 */
	private UISemanticEvent handleKeyDown(Event event, UISemanticEvent semantic) {
		
		traceEvent("Key-down event: " + event.keyCode);
		
		/*
		 * Here we cache key strokes (for use in shift-click and ctrl-key entry events
		 */
		//TODO: what happened here?  do we no longer need to do this?
		
		
		if (!isControlOrShift(event)) {
			
			//create the keyDown event
			semantic = SWTSemanticEventFactory.createKeyDownEvent(event);
			
			//System.out.print((int)event.character);
			//if the previous key was a ctrl char, set control flag
			Integer topKey = peekAtKeyStack();
			if (topKey != null && topKey.intValue() == SWT.CTRL) {

				((SemanticKeyDownEvent)semantic).setIsControlSequence(true);
				((SemanticKeyDownEvent)semantic).setKey((char)event.keyCode);
				
				//N.B. no need to pop previous, it will be popped on key Up since this keystroke
				//will not be on the stack...
			
			} else {
				//otherwise a simple push will do
				_keyStack.push(new Integer(event.keyCode));
			}
				
			
			
		} else {
			_keyStack.push(new Integer(event.keyCode)); //store it for later inspection
		}
			
		
		
		
		return semantic;
	}

	/**
	 * Handle key up events. 
	 */
	private void handleKeyUp() {
		//remove the key from the cache
		popKeyStack();
		resetButtonCache();
	}
	
	private void resetButtonCache() {
		_button = MouseConfig.PRIMARY_BUTTON;
	}

	/**
	 * Handle arm events. 
	 */
	private UISemanticEvent handleArm(Event event, UISemanticEvent semantic, Widget widget) {
		/**
		 * Once, this is where we managed menu selections...
		 */
		return semantic;
	}

	/**
	 * Handle mouse down events. 
	 */
	private void handleMouseDown(Event event) {
		//cache the current button down type:
		_button = event.button;
		_lastMouseEvent = event;
		_lastMouseEventDetail = getDetail(event);
	}
	
	/**
	 * Handle mouse up events. 
	 */
	private UISemanticEvent handleMouseUp(Event event, UISemanticEvent semantic, Widget widget) {
		
		_lastMouseEvent = event;
		_lastMouseEventDetail = getDetail(event);

		if (_isDoubleClickEventCase) {
			/**
			 * Some widgets fire double-click events.  In these cases, ignore the mouseUp.
			 */
			_isDoubleClickEventCase = false; //reset
		} else if (widget instanceof Tree) {
			semantic = _treeEventHandler.getEvent(); //on the way out, fetch the selection event
			/*
			 * N.B. context selections create a (bogus) event preceding the menu selection which is
			 * handled on the associated MenuItemSelection; here we discard the event but cache the
			 * selected item for use in context menu event creation
			 */
			if (semantic != null && semantic.isContext()) {
				_contextMenuTreeItemTarget = (SemanticTreeItemSelectionEvent) semantic;
				semantic = null;
			}
			
		} else if ((widget instanceof TabFolder || widget instanceof CTabFolder) && !Platform.isOSX()) {
			semantic = SWTSemanticEventFactory.createTabItemSelectionEvent(event);
			//ctabitems MAY require xy
			if (semantic != null && requiresLocationInfo(widget)) {
				semantic.setRequiresLocationInfo(true);
			}
		} else if (widget instanceof List) {
			//moved to selection.... 
		} else if (widget instanceof Table) {
//			//moved to selection.... 
//			//here we grab the table context selection
//			_contextTableSelection = _tableSelectionHandler.getEvent();
//			//and cache the clicked item
//			_clickedTableItem = _tableSelectionHandler.getSelection();
			
			Point point = new Point(event.x, event.y);

			Table table = (Table) widget;
			TableItem item = table.getItem(point);
			traceEvent("Mouse up on table item: " + item);

			int index = 0;
			
			if (item == null) {
				index = getColumnIndex(table, point);
				item = getItem(table, point);
			}
			
			//checks the state mask of the event
			String mask = null;
			if ((event.stateMask & (SWT.SHIFT | SWT.CTRL | SWT.ALT)) != 0 || _isCheckSelection) {
				mask = "SWT.BUTTON1";
				if ((event.stateMask & SWT.SHIFT) != 0)
					mask = mask + " | SWT.SHIFT";
				if ((event.stateMask & SWT.CTRL) != 0)
					mask = mask + " | SWT.CTRL";
				if ((event.stateMask & SWT.ALT) != 0)
					mask = mask + " | SWT.ALT";
				if (_isCheckSelection)
					mask = mask + " | SWT.CHECK";
			}
			
						
			/**
			 * Sanity: in case the item cannot be found, we skip the event.
			 * TODO: characterize this corner case.
			 */
			if (item != null) {
				UISemanticEvent semanticEvent = SWTSemanticEventFactory
						.createTableItemSelectionEvent(table, item, index, mask);

				// context case is handled in the associated menu selection
				if (!isContextCase()) {
					semantic = semanticEvent;
				} else {
					_contextTableSelection = semanticEvent;
				}
			}
			
		} else if (widget instanceof Combo) {
			//moved to selection.... 
		} else if (widget instanceof ToolBar || widget instanceof ToolItem /* shouldn't see this but in case...*/) {
			//moved to selection.... 
		} else if (widget instanceof Button) {
			//moved _from_ selection:
			semantic = createDragStartSelectionEvent(event);
		} else {
			
			//context menus require us to cache the target of the menu click
			if (isContextCase()) {
				_contextTarget = event.widget;
			} else {	
				
				UISemanticEvent dragSource = _dndHelper.getDragSource();
				if (dragSource != null) {
					/*
					 * Note we cache source in case we need to synthesize a click (happens if the click was not
					 * explicitly performed before the drag)
					 */
					semantic = SWTSemanticEventFactory.createDragToEvent(event).withSource(dragSource);
					
					_dndHelper.processed();
				} else
					semantic = createDragStartSelectionEvent(event);
				if (requiresLocationInfo(widget)) 
					semantic.setRequiresLocationInfo(true);
			}
			//finally, ccombos require us to cache the ccombo since the selection happens in a 
			//forthcomming list selection
//			if (widget instanceof Button) {
//				Widget parent = new ButtonTester().getParent((Button)widget);
//				if (parent instanceof CCombo) {
//					_cComboTarget  = (CCombo)parent;  //cache the combo
//					semantic       = null;            //nullify the selection event
//				}
//			}
			//<--- moved to selection
		}
		return semantic;
	}


	private Widget getDetail(Event event) {
		if (event.widget instanceof CTabFolder)
			return ((CTabFolder)event.widget).getItem(new Point(event.x, event.y));
		return null;
	}

	private TableItem getItem(Table table, Point point) {
		
		int columnCount = table.getColumnCount();
		
		
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = (TableItem)items[i];
			for (int j = 0; j < columnCount; ++j) {
				if (item.getBounds(j).contains(point))
					return item;
			}
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	
	
	private int getColumnIndex(Table table, Point point) {
		
		TableItem[] items = table.getItems();
		int columnCount = table.getColumnCount();
		
		if (columnCount == 0)
			return 0; 
		
		
		Rectangle bounds = null;
		for (int i = 0; i < items.length; i++) {
			
			TableItem item = (TableItem)items[i];
			for (int j=0; j < columnCount; ++j) {
				bounds = item.getBounds(j);
				if (bounds.contains(point))
					return j;
			}
			
			
		}
		
		
		
		
		return 0; //fail but default
	}

	
	
	/**
	 * Check to see if the given widget requires x,y info for playback.
	 */
	private boolean requiresLocationInfo(Object widget) {
		if (widget == null)
			return false;   //being safe...
		

		/**
		 * If a text widget has text in it, we want the click to be location conscious
		 * (in case there are text entries forthcoming)
		 */
		if (widget instanceof Text) {
			Text text = (Text)widget;
			String txt = text.getText();
			return txt != null && txt.length()>0;
		}

		/** canvases use (x,y) info for playback by default*/
		if (widget instanceof Canvas) {
			//but like text widgets, styled text is location aware if it has contents
			if (widget instanceof StyledText) {
				StyledText stext = (StyledText)widget;
				String txt = stext.getText();
				return txt != null && txt.length()>0;
			} else 
				return true;
		}
		
//Moving to new close API		
//		/**
//		 * A kludge to add x,y info to ctabitems to attempt to reproduce close events
//		 */
//		if (widget instanceof CTabItem) {
//			return true; //if we're lucky, location will help reproduce close events
//		}
		if (widget instanceof CTabFolder)
			return requiresLocationInfo(((CTabFolder)widget).getSelection());
		
		//otherwise, unknown widgets fall back to use x,y coordinates
		Package pkg = widget.getClass().getPackage();
		String name = pkg.getName();
		if ("org.eclipse.swt.widgets".equals(name))
			return false;
		if ("org.eclipse.swt.custom".equals(name))
			return false;
		return true;
	}












	/**
	 * Handle selection events. 
	 */
	private UISemanticEvent handleSelection(UISemanticEvent semantic, Widget widget, Event event) {
		traceEvent("selecting: " + widget);
		
		//cache check state for use in table event creation
		_isCheckSelection = isCheckSelection(event);
		if (_isCheckSelection)
			traceEvent("[check event]");
		
		
		/**
		 * Check to see if the selection is associated with a traversal event first
		 * if it is, short-circuit regular selection handling.
		 */
		SemanticKeyDownEvent traversalEvent = _controlTraversalHandler.getEvent();
		
		/*
		 * N.B. : for now we let the following logic continue even if it is a traversal
		 * (to clear the event cached by the selection handlers.
		 */
				
		if (widget instanceof List) {
			//ignore lists that belong to a cCombo
			if (_cComboTarget == null)
				semantic = _listSelectionHandler.getEvent();
		} else if (widget instanceof Table) {
			//context case is handled in the menu item selection
//			if (!isContextCase())
//				semantic = _tableSelectionHandler.getEvent();
			//^--- now handled in mouse up
		} else if (widget instanceof MenuItem) {
			if (isContextCase()) { //the context case requires a pointer back to the source of the menu
				if (_contextTableSelection != null) {
					semantic = SWTSemanticEventFactory.createContextMenuSelectionEvent(_contextTableSelection, event);
				} else {
					/*
					 * If we have a cached context target we use it:
					 */
					if (_contextTarget != null) {
						semantic = SWTSemanticEventFactory.createContextMenuSelectionEvent(_contextTarget, event);
					} else {
//						/*
//						 * if we don't we try and infer it:
//						 */
//						if (_controlInFocus != null) {
//							semantic = SWTSemanticEventFactory.createContextMenuSelectionEventFromFocusControl(_controlInFocus, event);
//						}
						if (_contextMenuTreeItemTarget != null) {
							semantic = SWTSemanticEventFactory.createTreeItemContextMenuSelectionEvent(_contextMenuTreeItemTarget, event);
						} else {
							if (_controlInFocus instanceof Tree) {

								/*
								 * We assume this is a case where a tree item
								 * has initial focus and so never gets
								 * selected...
								 */
								TreeItem[] selection = new TreeTester().getSelection((Tree)_controlInFocus);
								if (selection != null && selection.length > 0) {
									/*
									 * We're taking the first selection (because we have no choice!)
									 */
									if (selection.length > 1)
										LogHandler.log("selection count for tree with initial focus > 1; defaulting to first selection for context menu event recording");
									SemanticTreeItemSelectionEvent targetSelect = (SemanticTreeItemSelectionEvent) SWTSemanticEventFactory.createTreeItemSelectionEvent(selection[0], TreeEventType.SINGLE_CLICK, 3);
							
									semantic = SWTSemanticEventFactory.createTreeItemContextMenuSelectionEvent(targetSelect, event);
									
								} 
								
							}
							//fall-through: click on the control:
							if (semantic == null)
								semantic = SWTSemanticEventFactory.createContextMenuSelectionEvent(_controlInFocus, event);
							
							
						}
							
					}
						
				}
				_contextMenuTreeItemTarget = null; //reset
				_contextTarget = null;             //reset the context pointer for sanity
			} else
				semantic = SWTSemanticEventFactory.createMenuSelectionEvent(event);
		} else if (widget instanceof ToolItem) {
			semantic = createDragStartSelectionEvent(event);
		} else if (widget instanceof Tree) {
			//semantic = _treeEventHandler.getEvent();
		} else if (widget instanceof Combo) {
			semantic = SWTSemanticEventFactory.createComboSelectionEvent(event);
		} else if (widget instanceof CCombo) {
			semantic = SWTSemanticEventFactory.createCComboSelectionEvent(event);
			_cComboTarget = null; //set target to null to clean up
		} else if (widget instanceof Button) {
			
			//moved to mouseup to avoid programmatic (vs. user) selections
			//semantic = SWTSemanticEventFactory.createWidgetSelectionEvent(event);
			
			//finally, ccombos require us to cache the ccombo since the selection happens in a 
			Widget parent = new ButtonTester().getParent((Button)widget);
			if (parent instanceof CCombo) {
					_cComboTarget  = (CCombo)parent;  //cache the combo
					semantic       = null;            //nullify the selection event
			}
		} else if ((widget instanceof TabFolder || widget instanceof CTabFolder) && Platform.isOSX()) {
			boolean skip = false;
			if (widget instanceof TabFolder) {
				TabFolder tab = (TabFolder) widget;
				TabItem[] items = tab.getItems();
				if (items.length == 1 && items[0] == event.item && items[0].getControl() == null) {
					// see TabFolder.createItem()
					skip = true;
				}
			}
			if (!skip) {
				semantic = SWTSemanticEventFactory.createTabItemSelectionEvent(event);
				//ctabitems MAY require xy
				if (semantic != null && requiresLocationInfo(widget)) {
					semantic.setRequiresLocationInfo(true);
				}
			}
		}
		
		/*
		 * Now, having advanced the relevant handlers, check to see if we're
		 * actually in a traversal
		 */
		if (traversalEvent != null)
			semantic = traversalEvent;
		
		
		if ((semantic != null) && requiresLocationInfo(widget))
			semantic.setRequiresLocationInfo(true);
		
		return semantic;
	}
	
	/**
	 * Handle fall-through/default events. 
	 */
	private UISemanticEvent handleDefault(Event event) {
		UISemanticEvent semantic = null;
//		if (event.type == SWT.DefaultSelection)
//		//	semantic = SWTSemanticEventFactory.createDefaultSelectionEvent(event);
//			; //now ignored
		if (event.type == SWT.DragDetect) {

			//check handlers for cached events
			semantic = checkHandlersForCachedEvents();
			
			//if there's none cached, create a raw event
			if (semantic == null)
				semantic = SWTSemanticEventFactory.createRawEvent(event);
			//finally: turn the event into a drag event
			semantic = SWTSemanticEventFactory.createDragEvent(semantic);
			
		} else {
			semantic =  SWTSemanticEventFactory.createRawEvent(event);
		}
		return semantic;
	}
	
	/**
	 * Check handlers for for cached events.
	 */	
	private UISemanticEvent checkHandlersForCachedEvents() {
		UISemanticEvent cached = null;
		
		cached = _treeEventHandler.getEvent();
		
		if (cached == null && _listSelectionHandler != null)
			cached = _listSelectionHandler.getEvent();
		
		return cached;
	}

	/**
	 * Respond to start recorder events.
	 * @see com.windowtester.recorder.event.ISemanticEventListener#notifyStart()
	 */
	public void notifyStart() {
		/*
		 * At start, discover initial focus and ensure the traversal listener is 
		 * properly registered to listen.
		 */
		establishInitialFocus();
	}

	/**
	 * Respond to pause recorder events.
	 * @see com.windowtester.recorder.event.ISemanticEventListener#notifyPause()
	 */
	public void notifyPause() {
		//remove traversal handler from all its listener queues
		_controlTraversalHandler.clearInterests();
//		_keyEntryHandler.clearInterests();

	}
	
	/**
	 * Respond to stop recorder events.
	 * @see com.windowtester.recorder.event.ISemanticEventListener#notifyStop()
	 */
	public void notifyStop() {
		//remove traversal handler from all its listener queues
		_controlTraversalHandler.clearInterests();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.SemanticEventAdapter#notifySpyModeToggle()
	 */
	public void notifySpyModeToggle() {
		spyHandler.spyModeToggled();
	}
	
	private void establishInitialFocus() {
        //get the current active display
        Display display = Display.getDefault();
        if (display == null) {
            Logger.log("Error getting current display");
            return;
        }
        //get the current focus control
        Control focusControl = display.getFocusControl();
        if (focusControl == null) {
        	//changed log entry to trace:
            Tracer.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS,"Error getting current focus control");
            return;
        }
        //add appropriate handlers
        addHandlers(focusControl);
       
    }
	
	
	
    /////////////////////////////////////////////////////////////////////////////////////
    //
    // Helper predicates
    //
    /////////////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Is this a control or shift key event?
	 */
	private boolean isControlOrShift(Event event) {
		return (event.keyCode&SWT.SHIFT)==SWT.SHIFT || (event.keyCode&SWT.CTRL)==SWT.CTRL;
	}

	/**
	 * Are we in a context selection? 
	 */
	private boolean isContextCase() {
		return _button == 3;
	}
	
	/**
	 * Peek at the key stack.  If it is empty return null.
	 * @return the Integer at the top of the stack, or null if it's empty
	 */
	private Integer peekAtKeyStack() {
		return _keyStack.isEmpty() ? null : (Integer)_keyStack.peek();
	}
	
	/**
	 * Pop the top key off the stack.
	 */
	private void popKeyStack() {
		if (!_keyStack.isEmpty())
			_keyStack.pop();
	}
	
	/**
	 * Check for an SWT.CHECK.
	 */
	private boolean isCheckSelection(Event event) {
		return (event.detail & SWT.CHECK) != 0;
	}
	
	/**
     * Handles interpretation of mouse and tree selection events.
     */
    class TreeEventInterpreter implements MouseListener, TreeListener, Listener {
    	
    	/** The cache constructured Semantic event */
    	private UISemanticEvent _cached;
    	
    	/** The cached button */
    	private int _button;
    	
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseDoubleClick(MouseEvent e) {
			traceEvent("Double clicked: " + e);
			_doubleClick = true;
			_button = e.button;
			//System.out.println("double-click mouse selection event: " + item);
			//_cached = SWTSemanticEventFactory.createTreeItemSelectionEvent(e.widget, TreeEventType.DOUBLE_CLICK, _button);
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseDown(MouseEvent e) {
			traceEvent("Mouse down: " + e);
			_singleClick = true;
			_button = e.button;
			//_cached = SWTSemanticEventFactory.createTreeItemSelectionEvent(e.widget, TreeEventType.SINGLE_CLICK, _button);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseUp(MouseEvent e) {
			//System.out.println("mouse up: " + e);
			_singleClick = _doubleClick = false;
		}
		
		/**
		 * get the cached event, and set it to null
		 * @return the cached event
		 */
		UISemanticEvent getEvent() {
			UISemanticEvent event = _cached;
			_cached = null;
			return event;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.TreeListener#treeCollapsed(org.eclipse.swt.events.TreeEvent)
		 */
		public void treeCollapsed(TreeEvent e) {
			//FIXME[author=pq] SWTEvents are built around Events and NOT TreeEvents... must unify
			//_cached = new SemanticTreeItemSelectionEvent(e, TreeEventType.COLLAPSE);
			traceEvent("Collapse events unsupported...");
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.TreeListener#treeExpanded(org.eclipse.swt.events.TreeEvent)
		 */
		public void treeExpanded(TreeEvent e) {
			//_cached = new SemanticTreeItemSelectionEvent(e, TreeEventType.EXPAND);
			traceEvent("Expand events unsupported...");
		}

		public void handleEvent(Event e) {
			TreeItem item = (TreeItem)e.item;
			boolean check = (e.detail & SWT.CHECK) != 0;
			
			
			Integer code = peekAtKeyStack();
			String mask = null;
			if (code != null) {
				if (code.intValue() == SWT.CTRL)
					mask = "SWT.BUTTON1 | SWT.CTRL";
				if (code.intValue() == SWT.SHIFT)
					mask = "SWT.BUTTON1 | SWT.SHIFT";
			}
			
			
			/**
			 * Select events don't carry the button info so we need to pass it along too.
			 */
			if (_singleClick) {
				traceEvent("Single-click mouse selection event: " + item + (check ? " [check]" : ""));
				_cached = SWTSemanticEventFactory.createTreeItemSelectionEvent(e, TreeEventType.SINGLE_CLICK, _button);
			} else if (_doubleClick) {
				traceEvent("Double-click mouse selection event: " + item + (check ? " [check]" : ""));
				_cached = SWTSemanticEventFactory.createTreeItemSelectionEvent(e, TreeEventType.DOUBLE_CLICK, _button);
			}
			
			if (check)
				_cached.setChecked(true);
			
			if (mask != null && _cached != null)
				((SemanticTreeItemSelectionEvent)_cached).setMask(mask);
		}
    }

    
    
    
    /**
     * Base class for handling deltas in selections (e.g., tables and lists).
     */
    abstract class SelectionHandler /*<T,I>*/ {
    	
    	/** The cached constructed Semantic event */
    	private UISemanticEvent _cached;
    	
    	/** The cached indices */
    	private int[] _cachedIndices;

    	/** The owner of this selection */
		protected final Object /*<T>*/ _owner;
		
    	/**
    	 * Create an instance.
    	 */
    	public SelectionHandler(Object /*<T>*/ owner) {
    		_owner = owner;
			updateIndices();
		}

		/**
		 * Update cached indices.
		 */
		private void updateIndices() {
			_cachedIndices = getSelectionIndices();
		}
		
		/**
		 * Get the cached event, and set it to null
		 * @return the cached event
		 */
		UISemanticEvent getEvent() {

			String mask = null;			
			int selectType = 0;
			
			if (_lastMouseEvent != null) {
				if ((_lastMouseEvent.stateMask & (SWT.SHIFT | SWT.CTRL | SWT.ALT)) != 0) {
					mask = "SWT.BUTTON1";
					if ((_lastMouseEvent.stateMask & SWT.SHIFT) != 0)
						mask = mask + " | SWT.SHIFT";
					if ((_lastMouseEvent.stateMask & SWT.CTRL) != 0)
						mask = mask + " | SWT.CTRL";
					if ((_lastMouseEvent.stateMask & SWT.ALT) != 0)
						mask = mask + " | SWT.ALT";
				}
			}
			
			int index = 0;
			try {
				index = SelectionDeltaParser.indexToSelect(_cachedIndices, getSelectionIndices(), selectType);
			} catch(Throwable t) {
				/* A pesky and (very intermittent) bug causes an array index out of bounds exception
				 * in index parsing.
				 */
				Logger.log("Error in selection delta parsing", t);
			}
				
			Object /*<I>*/ selection = getItem(index);
			_cached = createSelectionEvent(_owner, selection, mask);
			
			updateIndices();
			
			UISemanticEvent event = _cached;
			_cached = null;
			return event;
		}

		/**
		 * Get the selection indices for this parent.
		 */
		protected abstract int[] getSelectionIndices();
		
		/**
		 * Create the appropriate selection event.
		 */
		protected abstract UISemanticEvent createSelectionEvent(Object /*<T>*/ owner, Object /*<I>*/ selection, String mask);

		/**
		 * Get the item at the given index.
		 */
		protected abstract Object /*<I>*/ getItem(int index);

    }

    /**
     * A selection handler tailored for lists.
     */
    class ListSelectionHandler extends SelectionHandler {

    	/**
    	 * Create an instance.
    	 * @param list
    	 */
    	public ListSelectionHandler(List list) {
    		super(list);
		}
    	
		/**
		 * @see com.windowtester.swt.event.model.SWTSemanticEventInterpreter.SelectionHandler#getSelectionIndices()
		 */
		protected int[] getSelectionIndices() {
			return getList().getSelectionIndices();
		}

		/**
		 * @return the underlying list
		 */
		public List getList() {
			return (List)_owner;
		}
		
		/**
		 * @see com.windowtester.swt.event.model.SWTSemanticEventInterpreter.SelectionHandler#createSelectionEvent(java.lang.Object, java.lang.Object, java.lang.String)
		 */
		protected UISemanticEvent createSelectionEvent(Object owner, Object selection, String mask) {
			return SWTSemanticEventFactory.createListItemSelectionEvent(getList(), (String)selection, mask);
		}

		/**
		 * @see com.windowtester.swt.event.model.SWTSemanticEventInterpreter.SelectionHandler#getItem(int)
		 */
		protected Object getItem(int index) {
			return getList().getItem(index);
		}
    	
    }

    
    class ControlTraversalHandler implements TraverseListener {

    	/** The current traversal event */
    	TraverseEvent _event;
    	
        /** Collection of controls to which this handler is listening */
        java.util.List _interests = new ArrayList();
    	
    	/**
    	 * Handle a key traversal event.
    	 * @see org.eclipse.swt.events.TraverseListener#keyTraversed(org.eclipse.swt.events.TraverseEvent)
    	 */
		public void keyTraversed(TraverseEvent e) {
			_event = e;
			//System.out.println("-->key traversed: " + e);
		}
		
		/**
		 * @return the Semantic event associated with the last recorded traversal event
		 */
		SemanticKeyDownEvent getEvent() {
			if (_event == null) // || _event.character != '\t')
				return null;
			//reset for next call
			TraverseEvent current = _event;
			_event = null;
			//System.out.println("traversal event gotten" + current);
			return SWTSemanticEventFactory.createKeyDownEvent(current);
		}
		
	    void startListeningTo(Control c) {
	        if (!_interests.contains(c)) {
	            c.addTraverseListener(this);
	            _interests.add(c);
	        }
	    }
	    
	    synchronized void stopListeningTo(Control c) {
	        if (!c.isDisposed()) //check may be unnecessary but better to be safe...
	            c.removeTraverseListener(this);
	        _interests.remove(c);
	    }

	    //remove all registered interests
	    synchronized void clearInterests() {
	    	/*
	    	 * We'd like to do something obvious like:
	    	 *   	for (Iterator iter = _interests.iterator(); iter.hasNext(); )
	    	 *			stopListeningTo((Control)iter.next());
	    	 * but the removals are causing ConcurrentModification Exceptions to be thrown...
	    	 */
	    	int size = _interests.size();
	    	for (int i= 0; i < size; ++i)
	    		stopListeningTo((Control)_interests.get(0));	
	    }
	    
    }
    
    class KeyEntryHandler implements KeyListener {

    	/** The current key event */
    	KeyEvent _event;
    	
        /** Collection of controls to which this handler is listening */
        Collection _interests = new ArrayList();
    	
    	public void keyPressed(KeyEvent e) {
    		//no-op
    		//key cached on release
    	}
        
    	public void keyReleased(KeyEvent e) {    		
    		_event = e;
    	}
    	
		/**
		 * @return the Semantic event associated with the last recorded traversal event
		 */
		SemanticKeyDownEvent getEvent() {
			if (_event == null || _event.character != '\t')
				return null;
			//reset for next call
			KeyEvent current = _event;
			_event = null;
			//System.out.println("traversal event gotten" + current);
			return SWTSemanticEventFactory.createKeyDownEvent(current);
		}
		
	    void startListeningTo(Control c) {
	        if (!_interests.contains(c)) {
	            c.addKeyListener(this);
	            _interests.add(c);
//	            Control parent = c.getParent();
//	            if (parent != null) {
//	            	startListeningTo(parent); //TODO: mirror this in stop
//	            	System.out.println("got control parent: " + parent);
//	            }
	        }
	    }
	    
	    void stopListeningTo(Control c) {
	        if (!c.isDisposed()) //check may be unnecessary but better to be safe...
	            c.removeKeyListener(this);
	        _interests.remove(c);
	    }

	    //remove all registered interests
	    void clearInterests() {
	    	//TODO: throws ConcurrentModificationErrors...
	    	for (Iterator iter = _interests.iterator(); iter.hasNext(); )
	    		stopListeningTo((Control)iter.next());
	    }
	    
    }
    
    class BrowserHandler implements MouseListener, LocationListener, Listener {

		private final Browser _browser;

		public BrowserHandler(Browser browser) {
			_browser = browser;
			startListening();
		}



		public void mouseDoubleClick(MouseEvent e) {
			System.out.println("browser doubleClick: " + e);
		}

		public void mouseDown(MouseEvent e) {
			System.out.println("browser mouseDown: " + e);
		}

		public void mouseUp(MouseEvent e) {
			System.out.println("browser mouseUp: " + e);
		}
    	
		public  void startListening() {
			_browser.addMouseListener(this);
			_browser.addLocationListener(this);
			Control[] children = _browser.getChildren();
			for (int i=0 ; i < children.length; ++i) {
				Control child = children[i];
				child.addMouseListener(this);
			}
		}
		
		public void stopListening() {
			_browser.removeMouseListener(this);
			_browser.removeLocationListener(this);
		}



		public void changing(LocationEvent event) {
			System.out.println("changing: " + event);
		}



		public void changed(LocationEvent event) {
			System.out.println("changed: " + event);
		}



		public void handleEvent(Event event) {
			System.out.println("browser got: " + event);
		}
    	
    }
    
    
 

	/**
	 * Send this event-related tracing message to the tracer.
	 * @param event - the event to trace
	 */
	private void traceInterpretingEvent(Event event) {
    	String eventDescription = "Interpreting ";
    	try {
    		if (event.widget instanceof Menu && event.type == SWT.Dispose) {
    			eventDescription += "{Menu Disposed}";
    		} else {
    			eventDescription += event.toString();
    		}
    	} catch(RuntimeException e) {
    		eventDescription += "<unable to get event description>";
    	}
    	traceEvent(eventDescription);
	}
	
	/**
	 * Send this event-related tracing message to the tracer.
	 * @param msg - the message to trace
	 */
	private void traceEvent(String eventDescription) {
		if (CONSOLE_TRACE)
			System.out.println(eventDescription);
		Tracer.trace(IEventRecorderPluginTraceOptions.SWT_EVENTS, eventDescription);
	}


}
