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
package com.windowtester.runtime.swt.internal.selector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.finder.swt.TestHierarchy;
import abbot.tester.swt.Robot;
import abbot.tester.swt.RunnableWithResult;
import abbot.tester.swt.WidgetLocator;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.operation.SWTDisplayLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector.PopupFailedException;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;
import com.windowtester.runtime.swt.widgets.ISWTWidgetReference;
import com.windowtester.swt.util.FinderUtil;


/**
 * A base widget selector that provides basic widget selection functions.
 * 
 * @author Phil Quitslund
 */
public class BasicWidgetSelector implements ISWTWidgetSelectorDelegate {

	//TODO: parcel up these constants elsewhere?
	
	/** Suitable delay for most cases; tests have been run safely at this
	value.  Should definitely be less than the double-click threshold.
	(The default value, zero, causes half the tests to fail on linux).
	FIXME need to find a value between 0 and 100 (100 is kinda slow).
	30 works (almost) for w32/linux, but OSX 10.1.5 text input lags (50 is 
	minimum). <p>
	As platforms are tested at 0 delay, adjust this value.<p>
	OSX test run time was reduced from 130s to 96s.<p>
	Not sure it's worth tracking down all the robot bugs and working
	around them.
	*/
	private static final int DEFAULT_DELAY = Platform.isOSX() || Platform.isLinux() 
			|| Platform.isWindows() ? 0 : 50;

	
	//constant used for closing menus in case of failure
	private static final int MAX_MENU_DEPTH = 5;
	
	/** Normal click delay: between mouseDown and mouseUp */
	private int clickDelay = DEFAULT_DELAY;
	/** Double click delay: between first and the second click */
	private int doubleClickDelay = DEFAULT_DELAY;
	

	protected int getClickDelay() { return clickDelay; }
	protected void setClickDelay(int ms) { clickDelay = ms; }
	protected int getDoubleClickDelay() { return doubleClickDelay; }
	protected void setDoubleClickDelay(int ms) { doubleClickDelay = ms; }
	
	//a dispatcher for raw SWT events
	private final DisplayEventDispatcher _dispatcher = new DisplayEventDispatcher();
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Click actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	
	/** Click in the center of the given component.  This is not static b/c it
	 * sometimes needs to be redefined (i.e. JComponent to scroll before
	 * clicking).
	 */
	public synchronized Widget click(final Widget w) {
		return click(w, SWT.BUTTON1);
	}
	
    public Widget click(final Widget w, final int mask) {
		Rectangle rect = getBounds(w);
		return click(w, rect.width/2, rect.height/2, mask);
    }
    
	private Rectangle getBounds(Widget w) {
		return SWTWidgetReference.forWidget(w).getDisplayBounds();
	}
	
    public Widget click(Widget w, int x, int y, int mask) {
    	return click(w, x, y, mask, 1);
    }
    
    
//    public Widget doubleClick(Widget w, int mask) {
//    	Rectangle rect = getBounds(w);
//    	return click(w, rect.width/2, rect.height/2, mask, 2);
//    }
    
    public Widget doubleClick(Widget w, int x, int y, int mask) {
    	return click(w, x, y, mask, 2);
    }
    
	/**
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget click(Widget w, String itemLabel) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return click(w, itemLabel, SWT.BUTTON1);
	}
		
	/**
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget click(Widget w, String itemLabel, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		throw new UnsupportedOperationException();	//subclass responsibility
	}
		
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#doubleClickItem(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget doubleClickItem(Widget w, String itemLabel) {
		return doubleClick(w, itemLabel, SWT.BUTTON1);
	}
	
	/**
	 * Must be overriden in subclasses.
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#doubleClick(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget doubleClick(Widget w, String itemLabel, int mask) {
		throw new UnsupportedOperationException();	//subclass responsibility
	}
	
	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#contextClick(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget contextClick(Widget w, String menuItemPath) throws MultipleWidgetsFoundException, WidgetNotFoundException {
		
		/**
		 * Disabling old approach in favor of BEA's PopupMenuSelector.
		 */
		
//		//bring up the context menu
//		click(w, SWT.BUTTON3);
//		//find the menu and click the item
//		return findChildMenuAndClick(w, path);
		
		Control control = FinderUtil.getControl(w);
		if (control == null)
			throw new UnsupportedOperationException("Context menus unsupported for widgets of type: " + w.getClass());
		Widget clicked = null;
	
		//clicked = new PopupMenuSelector2().contextClick(w, path);
		
		try {
			clicked = new PopupMenuSelector2().runPopup(control, w, menuItemPath);
		} catch (PopupFailedException e) {
			throw new WidgetNotFoundException("Context Menu item: " + menuItemPath + " not found in widget " + SWTWidgetReference.forWidget(w).toString());
		}
		
		return clicked;
	}

	public Widget contextClick(Widget w, int x, int y, String menuItemPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		
		Control control = FinderUtil.getControl(w);
		if (control == null)
			throw new UnsupportedOperationException("Context menus unsupported for widgets of type: " + w.getClass());
		Widget clicked = null;
	
		try {
			// TODO[pq]: this should return a reference and not a widget
			clicked = new PopupMenuSelector2().runPopup(control, w, x, y, menuItemPath);
		} catch (PopupFailedException e) {
			throw new WidgetNotFoundException("menu item: " + menuItemPath + " not found in widget " + w);
		}
		
		return clicked;
	}
	
	/**
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#contextClick(org.eclipse.swt.widgets.Widget, java.lang.String, java.lang.String)
	 */
	public Widget contextClick(Widget w, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		throw new UnsupportedOperationException(); //subclass responsibility
	}
	
	
	
//	protected Widget findChildMenuAndClick(Widget w, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException  {
//		Exception caught = null;
//		
//		try {
//			//TODO: finish cases --- and consider pushing into specific selectors (this forward dependency is bad)
//			if (w instanceof TreeItem) {
//				w = UIProxy.getParent((TreeItem)w);
//			}
//			if (w instanceof MenuItem) {
//				w = UIProxy.getParent((MenuItem)w);
//			}
//			if (w instanceof TableItem) {
//				w = new TableItemTester().getParent((TableItem)w);
//			}
//			Menu menu = (Menu)BasicFinder2.getDefault().find(new HierarchyMatcher(Menu.class, new InstanceMatcher(w)));
//			return new MenuItemSelector().click(menu, path);
//			
//		} catch (WidgetNotFoundException e) {
//			caught = e;
//		} catch (MultipleWidgetsFoundException e) {
//			caught = e;
//		} catch (abbot.finder.swt.WidgetNotFoundException e) {
//			caught = new WidgetNotFoundException(e.getMessage());
//		} catch (abbot.finder.swt.MultipleWidgetsFoundException e) {
//			caught = new MultipleWidgetsFoundException(e.getMessage());
//		} finally {
//			if (caught != null) {
//				LogHandler.log(caught);
//				//rethrow
//				if (caught instanceof MultipleWidgetsFoundException)
//					throw (MultipleWidgetsFoundException)caught;
//				if (caught instanceof WidgetNotFoundException)
//					throw (WidgetNotFoundException)caught;
//			}
//		}
//		return null;
//	}

	/*
	 * TODO rewrite this to query everything it needs from the widget *before*
	 * it starts posting mouse clicks (in click2). The widget can, in theory,
	 * be disposed any time after the first click.
	 */
	public Widget click(final Widget w, final int x, final int y, final int mask, final int count) {

		if (w == null)
			return null;
		
//		boolean shift = (mask & SWT.SHIFT) ==SWT.SHIFT;
//		boolean ctrl  = (mask & SWT.CTRL) == SWT.CTRL;
//		boolean check = (mask & SWT.CHECK) == SWT.CHECK;
//		boolean alt = (mask & SWT.ALT) == SWT.ALT; // Mac testing
//		boolean command = (mask & SWT.COMMAND) == SWT.COMMAND; // Mac testing
//
//		int type = SWT.MouseUp;
//		// If we're simulating a modifier key then our thread must be made to wait until
//		// click2() generates the key-up event. Note that this is extremely dependent
//		// on the implementation of click2().
//		if (ctrl||shift||check|alt|command) {
//			type = SWT.KeyUp;
//		}
//		Widget listenToWidget = w;
//		if(w instanceof ToolItem)
//			listenToWidget = UIProxy.getParent((ToolItem)w);
//		if(w instanceof CTabItem)
//			listenToWidget = UIProxy.getParent((CTabItem)w);
//		if(w instanceof TabItem)
//			listenToWidget = UIProxy.getParent((TabItem)w);
//		if(w instanceof TreeItem)
//			listenToWidget = UIProxy.getParent((TreeItem)w);
		if(w instanceof MenuItem){
			click2(w, x, y, mask, count);
			pauseCurrentThread(300);
			if (Platform.isOSX()) // Mac testing
				wiggleMouseAt(w, x, y);
			return w;
		}

//		new SystemEventMonitor(listenToWidget, type){
//			public void syncExecEvents() {
				click2(w, x, y, mask, count);
//			}
//		}.run();

		return w;
	}

	private void wiggleMouseAt(Widget widget, int x, int y) {
		try {
			_dispatcher.mouseMove(widget, x+1, y+1);
			pauseCurrentThread(50);
			_dispatcher.mouseMove(widget, x, y);
		} catch (SWTException ex) {
			// ignore disposed widget problems
		}
	}

	/**
	 * Click in the given part of the component.  All other click methods
	 * must eventually invoke this one. Except the cases that call the old
	 * click(int,int,int,int) method, which does not handle checks. (Unless
	 * those are bugs waiting to be found, which is a possibility.)
	 * TODO rewrite this to query everything it needs from the widget *before*
	 * it starts posting mouse clicks. The widget can, in theory, be disposed
	 * any time after the first click. (Then check the sender tree.)
	 */
	protected Widget click2(final Widget w, final int x, final int y, int mask, int count) {
		// TODO[pq]: this mapping should be pushed up (the ref should be passed into this method)
		ISWTWidgetReference<?> ref = SWTWidgetReference.forWidget(w);
		new SWTMouseOperation(mask).at(new SWTWidgetLocation(ref, WTInternal.TOPLEFT).offset(x, y)).count(count).execute();

//		printTraceMessage(w, x, y);
//
//		boolean shift = (mask & SWT.SHIFT) == SWT.SHIFT;
//		boolean ctrl = (mask & SWT.CTRL) == SWT.CTRL;
//		boolean check = (mask & SWT.CHECK) == SWT.CHECK;
//		boolean alt = (mask & SWT.ALT) == SWT.ALT; // Mac testing
//		boolean command = (mask & SWT.COMMAND) == SWT.COMMAND; // Mac testing
//
//		if (shift) 
//			trace("got shift!");
//		if (ctrl)
//			trace("got ctrl!");
//		if (check) 
//			trace("got check!");
//		if (alt) 
//			trace("got alt!");
//		if (command) 
//			trace("got command!");
//
//		// FIXME handle other modifiers
//		mask &= (SWT.BUTTON1
//				|SWT.BUTTON2
//				|SWT.BUTTON3);
//
//		if (shift)
//			_dispatcher.keyDown(SWT.SHIFT);
//		if (ctrl)
//			_dispatcher.keyDown(SWT.CTRL);
//		if (alt)
//			_dispatcher.keyDown(SWT.ALT);
//		if (command)
//			_dispatcher.keyDown(SWT.COMMAND);
//
//
//		if(!Platform.isLinux() ){
//			mousePress(w, x, y, mask);
//		}else{	
//			mouseMove(w, x, y);		
//			new abbot.swt.Robot().mousePress(mask);
//		}
//		// [author=Dan] No pause on Linux between mouse down and mouse up
//		// because some controls such as CTabFolder may receive the mouse down
//		// and call OS.lock, thus preventing us from ever posting the mouse up
//		// until the user wiggles the mouse.
//		if (!Platform.isLinux()) { // menu item check doesn't work
//			pauseCurrentThread(getClickDelay());
//		}
//		if (Platform.isOSX() || (Platform.isLinux()&& w instanceof MenuItem)) { // Mac testing
//			wiggleMouseAt(w, x, y);
//		}
//
//		while (count-- > 1) {
//			if(!Platform.isLinux() ){
//				_dispatcher.mouseUp(mask);
//			}else{
//				new abbot.swt.Robot().mousePress(mask);
//			}	
//			pauseCurrentThread(DEFAULT_DELAY);			
//			if(!Platform.isLinux() ){
//				_dispatcher.mouseDown(mask);
//			}else{	
//				new abbot.swt.Robot().mouseRelease(mask);
//			}	
//			if (!Platform.isLinux())
//				pauseCurrentThread(getClickDelay());
//			if (Platform.isOSX()) // Mac testing
//				wiggleMouseAt(w, x, y);
//		}
//		if( !Platform.isLinux()){
//			_dispatcher.mouseUp(mask);
//		}else{	
//			new abbot.swt.Robot().mouseRelease(mask);
//		}
//
//		/**
//		 * Handle checks here
//		 */
//		if (check) {
//			pauseCurrentThread(100);
//			setChecked(w);
//		}
//
//		if (shift)
//			_dispatcher.keyUp(SWT.SHIFT);
//		if (ctrl)
//			_dispatcher.keyUp(SWT.CTRL);
//		if (alt)
//			_dispatcher.keyUp(SWT.ALT);
//		if (command)
//			_dispatcher.keyUp(SWT.COMMAND);

		if ((w != null) && (!w.isDisposed())) { 
			waitForIdle(getDisplay(w));
		}

		return w;
	}
	
	//guard to catch widget disposal timing issue
	
	private Display getDisplay(Widget w) {
		try {
			return w.getDisplay();
		} catch (SWTException e) {
			return Display.getDefault();
		}
	}

	/** Click in the given part of the component.  All other click methods
	 * must eventually invoke this one.
	 * @deprecated
	 */
	protected void click(final int x, final int y, int mask, int count) {
		new SWTMouseOperation(mask).at(new SWTDisplayLocation().offset(x, y)).count(count).execute();
//		boolean shift = (mask & SWT.SHIFT) == SWT.SHIFT;
//		boolean ctrl = (mask & SWT.CTRL) == SWT.CTRL;
//		boolean alt = (mask & SWT.ALT) == SWT.ALT; // Mac testing
//		boolean command = (mask & SWT.COMMAND) == SWT.COMMAND; // Mac testing
//
//		if (shift) 
//			trace("got shift!");
//		if (ctrl)
//			trace("got ctrl!");
//		if (alt) 
//			trace("got alt!");
//		if (command) 
//			trace("got command!");
//
//		// FIXME handle other modifiers
//		mask &= (SWT.BUTTON1
//				|SWT.BUTTON2
//				|SWT.BUTTON3);
//
//		if (shift)
//			_dispatcher.keyDown(SWT.SHIFT);
//		if (ctrl)
//			_dispatcher.keyDown(SWT.CTRL);
//		if (alt)
//			_dispatcher.keyDown(SWT.ALT);
//		if (command)
//			_dispatcher.keyDown(SWT.COMMAND);
//
//		mouseMove(x, y);
//
//		_dispatcher.mouseDown(mask);
//		pauseCurrentThread(getClickDelay());
//
//		while (count-- > 1) {
//			_dispatcher.mouseUp(mask);
//			pauseCurrentThread(DEFAULT_DELAY);
//			_dispatcher.mouseDown(mask);
//			pauseCurrentThread(getClickDelay());
//		}
//		_dispatcher.mouseUp(mask);
//
//		if (shift)
//			_dispatcher.keyUp(SWT.SHIFT);
//		if (ctrl)
//			_dispatcher.keyUp(SWT.CTRL);
//		if (alt)
//			_dispatcher.keyUp(SWT.ALT);
//		if (command)
//			_dispatcher.keyUp(SWT.COMMAND);

	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#getClickOffset(org.eclipse.swt.widgets.Widget)
	 */
	public Point getClickOffset(Widget w, int mask) {
		/*
		 * Default is CENTER.
		 */
		Rectangle bounds = getBounds(w);
		return new Point(bounds.width/2, bounds.height/2);
	}
	
	
	/**
	 * Set this widget to be checked (NOTE: the widget is guaranteed to have just been clicked).
	 * Note: This method MUST cause a KeyUp event for click/click2 to work properly. (That is,
	 * unless we need to model a modifier key being pressed during setChecked(). We might
	 * not get the key-up event for the modifier key with this implementation.)
	 */
	protected void setChecked(Widget w) {
//removing tentatively (default selection below should suffice)		
//		if (Platform.isOSX()) { // Mac testing
//			Rectangle rect = UIProxy.getBounds(w);
//			Rectangle outer = rect;
//			if (w instanceof Item) {
//				SWTHierarchy h = new SWTHierarchy(getDisplay(w));
//				Widget parent = h.getParent(w);
//				outer = UIProxy.getBounds(parent);
//			}
//			int left = rect.x - outer.x - 15; // magic number, offset from left to checkbox
//			mousePress(w, -left, rect.height/2, SWT.BUTTON1);
//			mouseRelease(SWT.BUTTON1);
//		}
		// fix for GTK must be safe on other platforms
		if (SWT.getPlatform().equals("gtk")){
			
			// tree item special case
			// fix for: case 14412
			// Note: logic taken from BEA TreeItemTester special case logic
	        // Arrow left 3 times b/c linux native widget has 3 widgets, with most
	        // left being the checkbox.
	    	if (w instanceof TreeItem || w instanceof Tree) {
	    		_dispatcher.keyClick(SWT.ARROW_LEFT);
	    	}			
			_dispatcher.keyClick(SWT.ARROW_LEFT);
			_dispatcher.keyClick(SWT.ARROW_LEFT);
		}
		/**
		 * The default check action is emit a ' ' keystroke (TODO: confirm on non Windows OSes)
		 * Can be overrriden in subclasses -- to, for instance, use setChecked(..) where provided
		 */
		_dispatcher.keyClick(' ');
	}

	/**
	 * Must be provided by subclasses.
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#clickExpand(org.eclipse.swt.widgets.Widget)
	 */
	public Widget clickExpand(Widget w) {
		throw new UnsupportedOperationException(); //subclass responsibility
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Selection actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#select(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public void select(Widget w, int start, int stop) {
		throw new UnsupportedOperationException(); //subclass responsibility
	}
	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#selectAll(org.eclipse.swt.widgets.Widget)
	 */
	public void selectAll(Widget w) {
		throw new UnsupportedOperationException(); //subclass responsibility
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Internal
	//
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Get the bounding rectangle for the given Widget in global
	 * screen coordinates.
	 */
	protected Rectangle getGlobalBounds(Widget w){
		return getGlobalBounds(w,true);
	}
	
	/** 
	 * Get the bounding rectangle for the given Widget in global
	 * screen coordinates, optionally ignoring the 'trimmings'.
	 */
	protected Rectangle getGlobalBounds(final Widget w, final boolean ignoreBorder){
	    Rectangle result = (Rectangle) Robot.syncExec(getDisplay(w), new RunnableWithResult() {
	        public Object runWithResult() {
	            return WidgetLocator.getBounds(w,ignoreBorder);
	        }
	    });
	    return result;
	}

	
	/** Set the focus on to the given component. */
	/* TODO MAY NEED TO CHECK THAT THE CONTROL DOES INDEED HAVE FOCUS */
	protected void setFocus(Widget widget) {
		TestHierarchy hierarchy = new TestHierarchy(Display.getDefault());
		while(!(widget instanceof Control))
			widget = hierarchy.getParent(widget);
		focus((Control)widget);
		waitForIdle(getDisplay(widget));
	}
	

	/** Move keyboard focus to the given component. */
	protected void focus(final Control c) {
		final Display display = c.getDisplay();
		display.syncExec(new Runnable(){
			public void run(){
				if (!c.forceFocus())
					trace("unable to give " + c + " focus");
			}		 
		});
	}
	
	protected void pauseDisplayThread(final Display d, final int ms){
		d.syncExec(new Runnable() {
			public void run() {
				d.timerExec(ms, new Runnable() {
					public void run() {
						// do nothing
					}
				});
			}
		});
	}
	
	protected void pauseCurrentThread(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
	
	protected /*synchronized*/ void waitForIdle(final Display display){
		
		/*
		 * Slow integ of new waitForIdle fixes
		 * To start, only for GTK (to guard against win32 regressions)
		 */
		if (SWT.getPlatform().equals("gtk") || Platform.isOSX()) {
			new SWTIdleCondition(display).waitForIdle();
		} else {
			/*
			 * The OLD way to wait (found not safe in Linux)
			 */
			// display.syncExec(new Runnable() {
			// public void run() {
			// 		while(display.readAndDispatch());
			// 	}
			// });
			
			//provisional fix for Dialogs Opened During Window Tester Widget Selector Actions Cause Hangs
			
			new SWTIdleCondition(display).waitForIdle();
		}		
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	///////////////////////////////////////////////////////////////////////////
	
    protected void trace(String msg) {
    	TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, msg);
    }

    protected void trace(Object msg) {
    	trace(msg.toString());
    }
	
//	private void printTraceMessage(final Widget w, final int x, final int y) {
//		Object traceMsg = UIProxy.syncExec(getDisplay(w), new RunnableWithResult() {
//			public Object runWithResult() {
//				return "Click at (" + x + "," + y + ") on " + w;
//			}
//		});
//		trace(traceMsg);
//	}
	
    
	///////////////////////////////////////////////////////////////////////////
	//
	// SWT event posting proxies
	//
	///////////////////////////////////////////////////////////////////////////
	
    protected void keyClick(int key) {
		_dispatcher.keyClick(key);
	}
    
    protected void keyUp(int key) {
		_dispatcher.keyUp(key);
	}
    
    protected void keyDown(int key) {
		_dispatcher.keyDown(key);
	}
    protected void mousePress(int accelerator) {
		_dispatcher.mouseDown(accelerator);
	}
	protected void mousePress(final Widget w, int x, int y, int mask) {
		mouseMove(w, x, y);
		_dispatcher.mouseDown(mask);
	}
    protected void mouseRelease(int accelerator) {
		_dispatcher.mouseUp(accelerator);
	}
    
	/**
	 * Move the mouse to hover over the center of this widget
	 * @param w - the widget to hover over
	 */
    public void mouseMove(Widget w) {
		Rectangle rect = getBounds(w);
		mouseMove(w, rect.width/2, rect.height/2);
	}
    
    public void mouseMove(int x, int y) {
    	// double mouse move needed to trigger highlighting in menus on Linux
		_dispatcher.mouseMove(x, y);
		_dispatcher.mouseMove(x, y);
	}

    public void mouseMove(Widget w, int x, int y) {
    	// double mouse move needed to trigger highlighting in menus on Linux
		_dispatcher.mouseMove(w, x, y);
		_dispatcher.mouseMove(w, x, y);
	}
	
    
    ///////////////////////////////////////////////////////////////////////////////
    //
    // Error Handling
    //
    ///////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Close open menus (called on error cases).
	 */
	protected void handleMenuClose() {
		//TODO: this may be OS-specific...
		//TODO: this number should reflect the number of actual menu levels; for now we're just picking a constant MAX
		for (int i= 0; i <= MAX_MENU_DEPTH; ++i) {
			//System.err.println("ESC");
			keyClick(SWT.ESC); //close menu by hitting ESCAPE
		}
	}
    
    
}
