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
package com.windowtester.runtime.swt.internal.operation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.Robot;
import abbot.tester.swt.WidgetLocator;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;


/**
 * A (perhaps temporary) home for migrating <code>BasicWidgetSelector</code> click functionality.
 */
public class BasicSWTWidgetClickOperation<T extends SWTWidgetReference<?>> extends SWTWidgetClickOperation<T>{

	
	protected Point pointT = new Point(0, 0);
	
	public BasicSWTWidgetClickOperation(T widget) {
		super(widget);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.operation.SWTWidgetClickOperation#getLocation()
	 */
	@Override
	protected SWTLocation getLocation() {
		return new SWTWidgetLocation(getWidgetRef(), WTInternal.TOPLEFT).offset(getOffset());
	}


	@Override
	public void execute() {
		//this is an attempt to make more operation-like the click->click2 sequence (below)
		// TODO[pq]: push this into a subclass (if it's deemed needed)
		Widget w = getWidgetRef().getWidget();
		if (w == null)
			return;
		
		super.execute();
		
		if ((w != null) && (!w.isDisposed())) { 
			waitForIdle(getDisplay(w));
		}
		
		if (w instanceof MenuItem){
			pauseCurrentThread(300);
			if (OS.isOSX()) // Mac testing
				wiggleMouse();	
		} 
	}
	
	
	private void wiggleMouse() {
		Point offset = getOffset();
		wiggleMouseAt(getWidgetRef().getWidget(), offset.x, offset.y);
	}

//	public Widget click(final Widget w, final int x, final int y, final int mask, final int count) {
//
//		if (w == null)
//			return null;
//		
////		boolean shift = (mask & SWT.SHIFT) ==SWT.SHIFT;
////		boolean ctrl  = (mask & SWT.CTRL) == SWT.CTRL;
////		boolean check = (mask & SWT.CHECK) == SWT.CHECK;
////		boolean alt = (mask & SWT.ALT) == SWT.ALT; // Mac testing
////		boolean command = (mask & SWT.COMMAND) == SWT.COMMAND; // Mac testing
////
////		int type = SWT.MouseUp;
////		// If we're simulating a modifier key then our thread must be made to wait until
////		// click2() generates the key-up event. Note that this is extremely dependent
////		// on the implementation of click2().
////		if (ctrl||shift||check|alt|command) {
////			type = SWT.KeyUp;
////		}
////		Widget listenToWidget = w;
////		if(w instanceof ToolItem)
////			listenToWidget = UIProxy.getParent((ToolItem)w);
////		if(w instanceof CTabItem)
////			listenToWidget = UIProxy.getParent((CTabItem)w);
////		if(w instanceof TabItem)
////			listenToWidget = UIProxy.getParent((TabItem)w);
////		if(w instanceof TreeItem)
////			listenToWidget = UIProxy.getParent((TreeItem)w);
//		if(w instanceof MenuItem){
//			click2(w, x, y, mask, count);
//			pauseCurrentThread(300);
//			if (Platform.isOSX()) // Mac testing
//				wiggleMouseAt(w, x, y);
//			return w;
//		}
//
////		new SystemEventMonitor(listenToWidget, type){
////			public void syncExecEvents() {
//				click2(w, x, y, mask, count);
////			}
////		}.run();
//
//		return w;
//	}
//	
//	/**
//	 * Click in the given part of the component.  All other click methods
//	 * must eventually invoke this one. Except the cases that call the old
//	 * click(int,int,int,int) method, which does not handle checks. (Unless
//	 * those are bugs waiting to be found, which is a possibility.)
//	 * TODO rewrite this to query everything it needs from the widget *before*
//	 * it starts posting mouse clicks. The widget can, in theory, be disposed
//	 * any time after the first click. (Then check the sender tree.)
//	 */
//	protected Widget click2(final Widget w, final int x, final int y, int mask, int count) {
//		// TODO[pq]: this mapping should be pushed up (the ref should be passed into this method)
//		ISWTWidgetReference<?> ref = SWTWidgetReference.forWidget(w);
//		new SWTMouseOperation(mask).at(new SWTWidgetLocation(ref, WTInternal.TOPLEFT).offset(x, y)).count(count).execute();
//
////		printTraceMessage(w, x, y);
////
////		boolean shift = (mask & SWT.SHIFT) == SWT.SHIFT;
////		boolean ctrl = (mask & SWT.CTRL) == SWT.CTRL;
////		boolean check = (mask & SWT.CHECK) == SWT.CHECK;
////		boolean alt = (mask & SWT.ALT) == SWT.ALT; // Mac testing
////		boolean command = (mask & SWT.COMMAND) == SWT.COMMAND; // Mac testing
////
////		if (shift) 
////			trace("got shift!");
////		if (ctrl)
////			trace("got ctrl!");
////		if (check) 
////			trace("got check!");
////		if (alt) 
////			trace("got alt!");
////		if (command) 
////			trace("got command!");
////
////		// FIXME handle other modifiers
////		mask &= (SWT.BUTTON1
////				|SWT.BUTTON2
////				|SWT.BUTTON3);
////
////		if (shift)
////			_dispatcher.keyDown(SWT.SHIFT);
////		if (ctrl)
////			_dispatcher.keyDown(SWT.CTRL);
////		if (alt)
////			_dispatcher.keyDown(SWT.ALT);
////		if (command)
////			_dispatcher.keyDown(SWT.COMMAND);
////
////
////		if(!Platform.isLinux() ){
////			mousePress(w, x, y, mask);
////		}else{	
////			mouseMove(w, x, y);		
////			new abbot.swt.Robot().mousePress(mask);
////		}
////		// [author=Dan] No pause on Linux between mouse down and mouse up
////		// because some controls such as CTabFolder may receive the mouse down
////		// and call OS.lock, thus preventing us from ever posting the mouse up
////		// until the user wiggles the mouse.
////		if (!Platform.isLinux()) { // menu item check doesn't work
////			pauseCurrentThread(getClickDelay());
////		}
////		if (Platform.isOSX() || (Platform.isLinux()&& w instanceof MenuItem)) { // Mac testing
////			wiggleMouseAt(w, x, y);
////		}
////
////		while (count-- > 1) {
////			if(!Platform.isLinux() ){
////				_dispatcher.mouseUp(mask);
////			}else{
////				new abbot.swt.Robot().mousePress(mask);
////			}	
////			pauseCurrentThread(DEFAULT_DELAY);			
////			if(!Platform.isLinux() ){
////				_dispatcher.mouseDown(mask);
////			}else{	
////				new abbot.swt.Robot().mouseRelease(mask);
////			}	
////			if (!Platform.isLinux())
////				pauseCurrentThread(getClickDelay());
////			if (Platform.isOSX()) // Mac testing
////				wiggleMouseAt(w, x, y);
////		}
////		if( !Platform.isLinux()){
////			_dispatcher.mouseUp(mask);
////		}else{	
////			new abbot.swt.Robot().mouseRelease(mask);
////		}
////
////		/**
////		 * Handle checks here
////		 */
////		if (check) {
////			pauseCurrentThread(100);
////			setChecked(w);
////		}
////
////		if (shift)
////			_dispatcher.keyUp(SWT.SHIFT);
////		if (ctrl)
////			_dispatcher.keyUp(SWT.CTRL);
////		if (alt)
////			_dispatcher.keyUp(SWT.ALT);
////		if (command)
////			_dispatcher.keyUp(SWT.COMMAND);
//
//		if ((w != null) && (!w.isDisposed())) { 
//			waitForIdle(getDisplay(w));
//		}
//
//		return w;
//	}
	
	
	//guard to catch widget disposal timing issue
	
	private Display getDisplay(Widget w) {
		try {
			return w.getDisplay();
		} catch (SWTException e) {
			return Display.getDefault();
		}
	}
	
	protected /*synchronized*/ void waitForIdle(final Display display){
		
		/*
		 * Slow integ of new waitForIdle fixes
		 * To start, only for GTK (to guard against win32 regressions)
		 */
		if (SWT.getPlatform().equals("gtk") || OS.isOSX()) {
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
			
			//provisional fix for 29881: Dialogs Opened During Window Tester Widget Selector Actions Cause Hangs
			
			new SWTIdleCondition(display).waitForIdle();
		}		
	}
	
	private void wiggleMouseAt(Widget widget, int x, int y) {
		try {
			mouseMove(widget, x+1, y+1);
			pauseCurrentThread(50);
			mouseMove(widget, x, y);
		} catch (SWTException ex) {
			// ignore disposed widget problems
		}
	}
	
	protected void pauseCurrentThread(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
	
	public synchronized void mouseMove(final Widget w, int x, int y) {
		pointT = null;
		Robot.syncExec(w.getDisplay(), this, new Runnable() {
			public void run() {
				pointT = WidgetLocator.getLocation(w);
			}
		});
		if (pointT == null) // TODO added for Mac testing
			return;
		mouseMove(pointT.x + x, pointT.y + y);
	}

	public void mouseMove(int x, int y) {
		Event event = new Event();
		event.type = SWT.MouseMove;
		event.x = x;
		event.y = y;
		new SWTPushEventOperation(event).execute();
	}
	
}
