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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.finder.swt.SWTHierarchy;
import abbot.finder.swt.TestHierarchy;
import abbot.script.Condition;
import abbot.tester.swt.DecorationsTracker;
import abbot.tester.swt.Robot;
import abbot.tester.swt.WidgetLocator;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.internal.KeyStrokeDecoder;
import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.swt.internal.ExceptionHandlingHelper;
import com.windowtester.runtime.swt.internal.UIContextSWT;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.hover.HoverInfo;
import com.windowtester.runtime.swt.internal.hover.IHoverInfo;
import com.windowtester.runtime.swt.internal.operation.SWTPushEventOperation;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.runtime.util.StringComparator;

/**
 * A service that drives UI events.
 * @see UIContextSWT
 * 
 */
public class UIDriver {
	
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
	protected static final int DEFAULT_DELAY =
		Platform.isOSX() || Platform.isLinux() || Platform.isWindows()
			? 0 : 50;
	private static final int SLEEP_INTERVAL = WT.getDefaultWaitInterval();

	/** Base delay setting.  */
	private static int defaultTimeout = (int)WT.getDefaultWaitTimeOut();
		
		
		//Properties.getProperty("abbot.robot.default_delay", 0, 60000, 30000);

	/** Used to signal failed location gets */
	private static final Point INVALID_POINT = new Point(-1, -1);
	
	//a dispatcher for raw SWT events
	private final DisplayEventDispatcher _dispatcher = new DisplayEventDispatcher();

	/** The last clicked widget */ 
	private Widget _lastClicked;

	/** A settings object that manages playback-related settings */
	protected PlaybackSettings _settings;

	/** Current mouse hover position */
	private IHoverInfo _currentHoverInfo;

	/** Widget-specific offset for clicking */
	private Point _clickOffset = new Point(0,0);
		
	///////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Get the most recently clicked widget.
	 * @return the most recently clicked widget
	 */
	protected Widget getLastClicked() {
		return _lastClicked;
	}
	
	/**
	 * Get the current position of the mouse.
	 * @return the current mouse position info
	 */
	public IHoverInfo getCurrentMouseHoverInfo() {
		return _currentHoverInfo;
	}
	
	
//	/**
//	 * Get the current playback settings.
//	 */
//	public PlaybackSettings getSettings() {
//		if (_settings == null)
//			_settings = initSettings();
//		return _settings;
//	}
	
//	/**
//	 * Get settings from runtime.
//	 * @return the current playback settings
//	 */
//	private PlaybackSettings initSettings() {
//		return RuntimePlugin.getDefault().getPlaybackSettings();
//	}

	
	/**
	 * @deprecated use {@link WT#getDefaultWaitTimeOut()} instead
	 */
	public static int getDefaultTimeout() {
		return defaultTimeout;
	}
	
	/**
	 * @deprecated use {@link WT#getDefaultWaitInterval()} instead
	 */	
	public static int getDefaultSleepInterval() {
		return SLEEP_INTERVAL;
	}
	
	
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

	
	/** Click in the center of the given component.
	 */
	public synchronized Widget click(final Widget w, int mask) {
		_lastClicked = w;
		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(w)));
		_clickOffset = getClickOffset(w, mask);
		return click(w, _clickOffset.x, _clickOffset.y, mask);
	}

	/**
	 * Calculate the click offset for the given widget.
	 * <p>
	 * The default is to center click.  Some widget's (e.g., TableItems) overrride this behavior.
	 */
	private Point getClickOffset(Widget w, int mask) {
		/*
		 * TODO[pq]: rather than querying selector, we should push logic into selector click and just call it
		 */
		return WidgetSelectorFactory.get(w).getClickOffset(w, mask);
	}

	public Widget click(Widget w, int x, int y) {
		return click(w, x, y, SWT.BUTTON1);
	}

	/** Click in the center of the given component.
	 * @return 
	 */
	public synchronized Widget doubleClick(final Widget w) {
		return doubleClick(w, SWT.BUTTON1);
	}

	public synchronized Widget doubleClick(final Widget w, int mask) {
		_lastClicked = w;
		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(w)));
		_clickOffset = getClickOffset(w, mask);
		return doubleClick(w, _clickOffset.x, _clickOffset.y, mask);
	}

	public Widget doubleClick(Widget w, int x, int y) {
		return doubleClick(w, x, y, SWT.BUTTON1);
	}
	
	public Widget doubleClick(Widget w, String itemLabelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		_lastClicked = w;
		Widget clicked = WidgetSelectorFactory.get(w).doubleClick(w, itemLabelOrPath, SWT.BUTTON1);
        setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
        return clicked;
	}
	
	
	public Widget doubleClick(Widget w, int x, int y, int buttonMask) {
		_lastClicked = w;
		Widget clicked = WidgetSelectorFactory.get(w).doubleClick(w, x, y, buttonMask);
		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
        return clicked;
	}
	
	/** Click in the given part of the component.  All other click methods
	 * must eventually invoke this one.
	 */
	public Widget click(Widget w, int x, int y, int mask) {		
		_lastClicked = w;
		Widget clicked = WidgetSelectorFactory.get(w).click(w, x, y, mask);
		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
        return clicked;
	}
	
	public Widget click(Widget w, String itemLabelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		_lastClicked = w;
		Widget clicked = WidgetSelectorFactory.get(w).click(w, itemLabelOrPath);
		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
        return clicked;
	}

	public Widget click(Widget w, String itemLabelOrPath, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		_lastClicked = w;
		Widget clicked = WidgetSelectorFactory.get(w).click(w, itemLabelOrPath, mask);
		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
        return clicked;
	}
	
//	/**
//	 * Click to expand this widget.  In the case of a Tree Item, 
//	 * it expands the tree node.  In the case of a Tool Item, it expands
//	 * the pull down menu.
//	 * @param w - the widget to expand
//	 * TODO: could fold into click adding mask?
//	 * @return 
//	 */
//	public Widget clickExpand(Widget w) {
//		_lastClicked = w;
//		Widget clicked = WidgetSelectorFactory.get(w).clickExpand(w);
//		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
//        return clicked;
//	}
	
//	public Widget contextClick(Widget w, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException {
//		_lastClicked = w;
//		Widget clicked = WidgetSelectorFactory.get(w).contextClick(w, path);
//		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
//        return clicked;
//	}
	
//	public Widget contextClick(Widget w, int x, int y, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException {
//		_lastClicked = w;
//		Widget clicked = WidgetSelectorFactory.get(w).contextClick(w, x, y, path);
//		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
//        return clicked;
//	}
	
	
//	public Widget contextClick(Widget w, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		_lastClicked = w;
//		Widget clicked = WidgetSelectorFactory.get(w).contextClick(w, itemPath, menuPath);
//		setMouseHoverInfo(HoverInfo.getAbsolute(getLocation(clicked)));
//        return clicked;
//	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Selection actions
	//
	///////////////////////////////////////////////////////////////////////////
	
//	/** 
//	 * Select this range of items in the given widget. 
//	 * @param w - the widget in which to select
//	 * @param start - the starting index
//	 * @param stop - the stop index
//	 */
//	public void select(Widget w, int start, int stop) {
//		WidgetSelectorFactory.get(w).select(w, start, stop);
//	}
//		
//	/** 
//	 * Select all the items in the given widget. 
//	 * @param w - the widget in which to select
//	 */
//	public void selectAll(Widget w) {
//		WidgetSelectorFactory.get(w).selectAll(w);
//	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Primitive mouse action commands
	//
	///////////////////////////////////////////////////////////////////////////
		
	/**
	 * Move the mouse to hover over the center of this widget
	 * @param w - the widget to hover over
	 */
	public void mouseMove(Widget w) {
		Rectangle rect = UIProxy.getBounds(w);
		mouseMove(w, rect.width/2, rect.height/2);
	}
	
	/**
	 * Move the mouse to this location (API).
	 */
	public void mouseMove(int x, int y) {
		//save hover info in case of drag
		setMouseHoverInfo(HoverInfo.getAbsolute(x, y));
		mouseMove0(x, y);
	}

	/**
	 * Cache the current hover info (for potential retrieval as a drag source).
	 * @param hoverInfo the current hover info
	 */
	public void setMouseHoverInfo(IHoverInfo hoverInfo) {
		//ignore invalid locations
		Point location = hoverInfo.getLocation();
		if (location != null && !location.equals(INVALID_POINT)) {
			_currentHoverInfo = hoverInfo;
			//System.out.println("setting hover info: " + hoverInfo);
		} else { 
			//System.out.println("invalid hover source ignored");
		}
	}

	/**
	 * Move the mouse to this location (internal).
	 * <p>
	 * This is the interface to the low level event dispatch.
	 * Note that all mouseMoves ultimately call this one.
	 */
	protected void mouseMove0(int x, int y) {
		_dispatcher.mouseMove(x, y);
	}
	
	
	/**
	 * Move the mouse to hover over this widget at this x,y offset
	 * from its top left corner.
	 * <p>
	 * Note that all widget-relative moves call this one.
	 */
	public void mouseMove(final Widget w, int x, int y) {
		//save hover info in case of drag
		setMouseHoverInfo(HoverInfo.getRelative(w, x, y));
		Point target = getLocation(w);
		mouseMove0(target.x+x, target.y+y);
	}
	
	/**
	 * Press the mouse.
	 * @param accel - the mouse accelerator.
	 */
	public void mouseDown(int accel) {
		_dispatcher.mouseDown(accel);
	}
	
	/**
	 * Release the mouse
	 * @param accel - the mouse accelerator.
	 */
	public void mouseUp(int accel) {
		_dispatcher.mouseUp(accel);
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Text entry actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	public void keyClick(int key) {
		_dispatcher.keyClick(key);
		if (Platform.isLinux())
			pause(600);
	}
	
	public void keyClick(char key) {
		_dispatcher.keyClick(key);
		if (Platform.isLinux())
			pause(600);
	}
	
	public void keyClick(int ctrl, final char c) {
		
//		keyDown(ctrl);
//		keyDown(c);
//		keyUp(c);
//		keyUp(ctrl);

		// [author=Dan] On Linux, the key down and key up events must be posted
		// all at the same time or weird timing effects happen.
		// For example, if a key combination causes a new shell to open,
		// and key down is posted in a separate syncExec from key up, then
		// the new shell fails to have focus if the key down and key up
		// are posted in different syncExec blocks.
		
		// TODO refactor and do something similar for all other keyboard entry
		
		final List keyEvents = new ArrayList(8);
		
		int[] modifiers = KeyStrokeDecoder.extractModifiers(ctrl);
		for (int i= 0; i < modifiers.length; ++i) {
			Event modifierEvent = new Event();
			modifierEvent.type = SWT.KeyDown;
			modifierEvent.keyCode = modifiers[i];
			keyEvents.add(modifierEvent);
		}
		
		Event keyDownEvent = new Event();
		keyDownEvent.type = SWT.KeyDown;
		keyDownEvent.character = c;
		keyEvents.add(keyDownEvent);
		
		Event keyUpEvent = new Event();
		keyUpEvent.type = SWT.KeyUp;
		keyUpEvent.character = c;
		keyEvents.add(keyUpEvent);
		
		//NOTICE: this is done in reverse order!
		for (int i= modifiers.length-1; i >= 0; --i) {
			Event modifierEvent = new Event();
			modifierEvent.type = SWT.KeyUp;
			modifierEvent.keyCode = modifiers[i];
			keyEvents.add(modifierEvent);
		}

		// post ALL key down and key up events at the same time
		
		// [author=Dan] Not quite working... the current code works fine in a short test
		// such as when CompoundKeystrokeSmokeTest is run by itself in Linux
		// but still fails when CompoundKeystrokeSmokeTest is run as part of WTRuntimeScenario2.
		// uncommenting the code below with the long pause (yuck!) makes it work.
		// Is there a better way?
		
//		final Display display = Display.getDefault();
//		if (Platform.isLinux()) {
//			waitForIdle(display);
//			pause(600);
//			waitForIdle(display);
//			pause(15000);
//		}
		for (Iterator iter = keyEvents.iterator(); iter.hasNext();) {
			new SWTPushEventOperation(((Event) iter.next())).execute();
		}
		if (Platform.isLinux())
			pause(600);
	}
	
	public void keyDown(char key) {
		_dispatcher.keyDown(key);
	}
	
	public void keyUp(char key) {
		_dispatcher.keyUp(key);
	}
	
	public void keyDown(int key) {
		_dispatcher.keyDown(key);
	}
	
	public void keyUp(int key) {
		_dispatcher.keyUp(key);
	}
	
	
	public void enterText(String str) {
		_dispatcher.enterText(str);
		pause(500);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// "Meta" events
	//
	///////////////////////////////////////////////////////////////////////////
		
	/**
	 * Requests that the window manager close the receiver shell in
	 * the same way it would be closed when the user clicks on
	 * the "close box" or performs some other platform specific
	 * key or mouse combination that indicates the window
	 * should be removed.
	 * @param shell - the shell to close
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see Shell#close()
	 */
	public void close(Shell shell) {
		UIProxy.closeShell(shell);
	}

	/**
	 * Move this control to the given x,y coordinates.
	 * @param c - the control to move
	 * @param x - the x coordinate
	 * @param y - the y coordinate
	 */
	public void move(Control c, int x, int y) {
		UIProxy.setLocation(c, x, y);
	}

	/**
	 * Resize this control.
	 * @param c - the control to resize
	 * @param width - the new width
	 * @param height - the new height
	 */
	public void resize(Control c, int width, int height) {
		UIProxy.resize(c, width, height);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Timing
	//
	///////////////////////////////////////////////////////////////////////////
	
	public static void pause(int ms) {
		try { Thread.sleep(ms); } catch(InterruptedException ie) { }
	}
	
	/** Wait for the given Condition to return true.  The default timeout may
	 * be changed by setting abbot.robot.default_delay.
	 * @throws WaitTimedOutException if the default timeout (30s) is exceeded. 
	 */
	public static void wait(Condition condition) {
		wait(condition, defaultTimeout);
	}

	/** Wait for the given Condition to return true, waiting for timeout ms.
	 * @throws WaitTimedOutException if the timeout is exceeded. 
	 */
	public static void wait(Condition condition, long timeout) {
		try {
			wait(condition, timeout, SLEEP_INTERVAL);
		} catch(WaitTimedOutException e) {
			Display display = Display.getCurrent();
			if (display == null)
				LogHandler.log("attempt to get current display in wait timeout handling failed");
			else
				new ExceptionHandlingHelper(display, true).closeOpenShells();
			throw e; //now, rethrow e
		}
	}

	/** Wait for the given Condition to return true, waiting for timeout ms,
	 * polling at the given interval.
	 * @throws WaitTimedOutException if the timeout is exceeded. 
	 */
	public static void wait(Condition condition, long timeout, int interval) {
		long now = System.currentTimeMillis();
		while (!condition.test()) {
			if (System.currentTimeMillis() - now > timeout) {
				StringBuffer sb =
					new StringBuffer("Timed out waiting for " + condition);
//				//Display d = robot.getDisplay();
//				Display d = Display.getDefault(); //TODO[pq]: sanity check
//				if (d != null) {
//					final TestHierarchy h = new TestHierarchy(d);
//					if (h != null) {
//						final BasicFinder f = new BasicFinder(h);
//						if (f != null) {
//// TODO: write this
////							sb.append(f.printWidgetsToString());
////							f.printWidgets();
//						}
//					}
//				}
				throw new WaitTimedOutException(sb.toString());
			}
			pause(interval);
		}
	}
	
	
	/**
	 * Convenience wait for a shell to be displayed.  This method is like 
	 * waitForFrameShowing, with the exception that this method searches all 
	 * shells in all displays; the former only searches for top-level shells
	 * @param shellName the name of the Shell
	 * @param timeout in millis
	 */
	public void waitForShellShowing(final String shellName, final int timeout) {
		wait(new Condition() {
			public boolean test() {
				return assertDecorationsShowing(shellName, false);
			}
			public String toString() { return shellName + " to show"; }
		}, timeout);
	}	
	
//	/**
//	 * Convenience wait for a shell to be disposed.  
//	 * @param shellName the name of the Shell
//	 * @param timeout in millis
//	 * @deprecated use com.windowtester.swt.IUIContext#waitForShellDisposed(String,int) instead
//	 * @see com.windowtester.swt.IUIContext#waitForShellDisposed(String)
//	 * 
//	 */
//	public void waitForShellDisposed(Shell shell, int timeout) {
//		//should not be used because wait should trigger condition handlers at pause intervals		
//		wait(new WidgetDisposedCondition(shell), timeout);
//	}
	
	
    /**
     * A condition that waits for a widget to be disposed.
     */
    class WidgetDisposedCondition implements Condition {

    	Widget _widget;
    	
		public WidgetDisposedCondition(Widget widget) {
			_widget = widget;
		}

		public boolean test() {
			return _widget.isDisposed();
		}
    }
	
	
	
	public /*synchronized*/ void waitForIdle(final Display display){
		display.syncExec(new Runnable() {
			public void run() {
				while(display.readAndDispatch()); // $codepro.audit.disable
			}
		});		
	}
	
//	 /** Move keyboard focus to the given component. */
//	 public void focus(final Control c) {
//		 c.getDisplay().syncExec( new Runnable(){
//		 	public void run(){
//		 		c.forceFocus();
//		 	}		 
//		 });
//	 }
	
	/*
	 * these methods really don't belong here but they make V2 higlighting
	 * much easier
	 */ 
	 
	public void highlight(Widget w) {
		// no-op
	}

	public void postClickPause() {
		// no-op
	}
	
	boolean boolT;
	
	// FIXME provide more options for identifying the window (name, title,
	// class, nth window)	
	/* Support added to allow a search of either all decorations or just those that 
	 * are top-level decorations (root windows) */
	private synchronized boolean assertDecorationsShowing(final String title, boolean topOnly) {
		boolT = false;
		//System.out.println ("ADS: " + title + " / " + topOnly);
		if (topOnly) {
			/* only search top-level decorations */
			Display[] displays = DecorationsTracker.getDisplays();
			//ArrayList decorationsList = new ArrayList();
			for (int i=0;i<displays.length;i++) {
				Collection decorationsList = DecorationsTracker.getTracker(displays[i]).getRootDecorations();
				final Iterator iter = decorationsList.iterator();
				Robot.syncExec(displays[i],this,new Runnable(){
					public void run(){
						while (iter.hasNext()) {
							Decorations d = (Decorations)iter.next();
							if (!d.isDisposed() && d.getText().equals(title)) {
								boolT = d.isVisible();
							}
						}
					}
				});

			}					
		} else {
			// TODO: clean up and document this code
			// This code basically traverses the widget hierarchy looking for Decorations
			// We also have to make sure we run the methods that get text and other things 
			// from the correct thread
			/* search all decorations */
			//final Decorations w;
			Display[] displays = DecorationsTracker.getDisplays();
			ArrayList decorationsList = new ArrayList();
			for (int i=0;i<displays.length;i++) {
				//!pq: removing this trace -- noisy and not helpful...
				//TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "Display " + i + ": " + displays[i]);
				SWTHierarchy hierarchy = new TestHierarchy(displays[i]);
				final Iterator rootIter = hierarchy.getRoots().iterator();
				while (rootIter.hasNext()) {
					decorationsList.addAll(hierarchy.getWidgets((Widget)rootIter.next()));
				}
				decorationsList.addAll(hierarchy.getRoots());
				//hierarchy.dbPrintWidgets();
				final Iterator decorationsIter = decorationsList.iterator();
				ArrayList shellList = new ArrayList();
				while (decorationsIter.hasNext()){
					Object d = decorationsIter.next();
					//System.out.println ("Widget ("+d.hashCode()+") " + i + ": " + d + " belongs to: " + ((Widget)d).getDisplay());
					/* remove non-Decorations and those that don't belong to this display */
					if (
							( (d instanceof Decorations) 
							)
							
							&& ((Widget)d).getDisplay().equals(displays[i])) {
						shellList.add(d);
					}
				}
				final Iterator iter = shellList.iterator();

				Runnable runThread = new Runnable() {
					public void run(){
						while (iter.hasNext()) {
							Decorations d = (Decorations)iter.next();
//							if (!d.isDisposed() && d.getText().equals(title)) {
							//check for pattern match:
							if (!d.isDisposed() && StringComparator.matches(d.getText(), title)) {
								boolT = d.isVisible();
							}
						}
					}
				};		
				if (displays[i].getThread().equals(Thread.currentThread())) {
					/* run in this thread */
					while (iter.hasNext()) {
						Decorations d = (Decorations)iter.next();
						if (!d.isDisposed() && d.getText().equals(title)) {
							boolT = d.isVisible();
						}
					}

				} else {
					Robot.syncExec(displays[i],this,runThread);
					
				}
										
				}
			
		}
		//System.out.println ("ADS returning: " + boolT);
		return boolT;
	}

	
	///////////////////////////////////////////////////////////////////
	//
	// Location calculating helpers.
	//
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Get the absolute location of this widget.
	 * @param w - the widget in question
	 * @return the widget's point in space (or INVALID_POINT if there is an error)
	 */
	public static Point getLocation(final Widget w) {
		//Linux fix case 38523
		return OS.isLinux() ? linuxGetLocation(w) : genericGetLocation(w);
	}

	private static Point genericGetLocation(final Widget w) {
		final Point[] point = new Point[] {INVALID_POINT};
		
		if (w != null && !w.isDisposed()) {
			try {
				w.getDisplay().syncExec(new Runnable(){
					public void run(){
						point[0] = WidgetLocator.getLocation(w);		
					}
				});				
			} catch(Exception e) {
				//ignored -- bad get signalled by invalid location
			}
		}
		return point[0];
	}
	
	
	private static Point linuxGetLocation(final Widget w) {
		final Point[] point = new Point[] {INVALID_POINT};
		final Object lock = new Object();
		final boolean[] go = { false };
		
		if (w != null && !w.isDisposed()) {
			try {
				Runnable runnable = new Runnable(){
					public void run(){						
						try{
										
						point[0] = WidgetLocator.getLocation(w);
						
						synchronized (lock) {
							go[0] = true;
							lock.notifyAll();
						}						
						
						}catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				};				
				w.getDisplay().asyncExec(runnable);
				
			} catch(Exception e) {
				//ignored -- bad get signalled by invalid location
			}
		}
		else{			
			return point[0];
		}
		
		synchronized (lock) {
			while (!go[0]) {
				try {
					lock.wait();
				} catch (InterruptedException e) {}
			}
		}		
		return point[0];
	}
	
	public static Point getCurrentCursorLocation() {
		return (Point) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				return Display.getDefault().getCursorLocation();
			}
		});
	}


}
