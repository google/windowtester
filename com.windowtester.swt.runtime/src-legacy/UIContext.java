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
package com.windowtester.swt;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.matchers.swt.NameMatcher;
import abbot.finder.swt.BasicFinder;
import abbot.finder.swt.Matcher;
import abbot.finder.swt.TestHierarchy;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.internal.swing.UIContextSwingFactory;
import com.windowtester.runtime.swt.internal.UIContextSWT;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.dnd.DragAndDropHelper;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.swt.condition.ICondition;
import com.windowtester.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.swt.condition.shell.ShellShowingCondition;

public class UIContext extends UIContextSWT
	implements IUIContext
{
	private DragAndDropHelper _dndHelper;
	
	WidgetMapper _mapper;       //a widget mapper instance

	/** A finder used to retrieve named widgets */
	private abbot.finder.swt.WidgetFinder _finder = BasicFinder.getDefault();
	
	/** Used to decide whether to check conditions on a mouseMove */
	private boolean _mouseDown;
	private boolean _keyDown;
	
	/** Cached Swing context */
	// 1/8/07 kp : changed to use new swing context2
	//private IUIContextSwing _swingContext;
	private com.windowtester.runtime.IUIContext _swingContext;
	
	
	
	/** A default timeout for wait for shell conditions 
	 * Note: upped from 5000,
	 */
	private static final int DEFAULT_TIMEOUT  = 15000; //TODO: move this somewhere central and user-configurable

	
	///////////////////////////////////////////////////////////////////////////
	//
	// Instance creation
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a new instance. Callers must also call {@link #setDisplay(Display)} to
	 * properly initialize the receiver.
	 */
	UIContext() {
		
		_mapper  = new WidgetMapper();
		
		/*
		 * Log creation and highlighting details
		 */
		try {
			TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "UIContext created --- highlighting/delay on: " + isHighlightingOrDelayOn());
		} catch( Throwable t) {
			//!pq: why the try/catch?
			//this is a quick patch for John and I want to be sure nothing can show-stop
			//if this turns out to be safe, we can remove it
			LogHandler.log(t);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Adaptation
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * @see com.windowtester.swt.IUIContext#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {	
		// 1/8/07 kp: changed to use swing context2
		//if (adapter.equals(IUIContextSwing.class)) {
		if (adapter.equals(UIContextSwing.class)) {
			if (_swingContext == null){
				
				/*
				 * This hack is necessary because swing context creation
				 * kicks off the RobotVerifier which creates an AWT window
				 * and gives it focus.
				 * 
				 * An alternative approach is to disable the verification step...
				 */
				// 3/22/07 : kp: removed get and set active shell for now
				// SWT thread error.
				// get the active shell
			//	final Shell activeShell;
				
				_swingContext = UIContextSwingFactory.createContext(com.windowtester.runtime.IUIContext.class);
				// set focus back to the shell
		//		activeShell.setFocus();
			}
			return _swingContext;
		}
		
		
		if (IUIContext.class.equals(adapter))
			return this;
		if (com.windowtester.runtime.IUIContext.class.equals(adapter))
			return this;
		
		
		return super.getAdapter(adapter);
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////

	protected DragAndDropHelper getDNDHelper() {
		if (_dndHelper == null)
			_dndHelper = new DragAndDropHelper(this);
		return _dndHelper;
	}

	////////////////////////////////////////////////////////////////////////
	//
	// State accessors
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * Check to see if a mouse button or key is currently down.
	 */
	private boolean isMouseOrKeyActive() {
		return _mouseDown || _keyDown;
	}
	
	
	////////////////////////////////////////////////////////////////////////
	//
	// Widget handle registration
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see com.windowtester.swt.IUIContext#register(java.lang.String, com.windowtester.swt.WidgetLocator)
	 */
	public void register(String key, WidgetLocator info) {
		_mapper.register(key, info);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Click actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see com.windowtester.swt.IUIContext#click(org.eclipse.swt.widgets.Widget)
	 */
	public Widget click(Widget widget) {
		handleConditions();
		Widget w = getDriver().click(widget);
		handleConditions();
		return w;
	}

	/**
	 * @see com.windowtester.swt.IUIContext#click(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public Widget click(Widget w, int x, int y) {
		handleConditions();
		return getDriver().click(w, x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#click(java.lang.String)
	 */
	public Widget click(String handle) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find 
		return click(find(handle));
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#click(java.lang.String, int, int)
	 */
	public Widget click(String handle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return click(find(handle), x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#click(org.eclipse.swt.widgets.Widget, int)
	 */
	public Widget click(Widget widget, int buttonMask) {
		handleConditions();
		return getDriver().click(widget, buttonMask);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#click(org.eclipse.swt.widgets.Widget, int, int, int)
	 */
	public Widget click(Widget w, int x, int y, int mask) {
		handleConditions();
		return getDriver().click(w, x, y, mask);
	}
		
	/**
	 * @see com.windowtester.swt.IUIContext#click(java.lang.String, int)
	 */
	public Widget click(String ownerHandle, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find 
		return click(find(ownerHandle), buttonMask);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#click(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget click(Widget owner, String labelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		handleConditions();
		try{
			return getDriver().click(owner, labelOrPath);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	/**
	 * @see com.windowtester.swt.IUIContext#click(java.lang.String, java.lang.String)
	 */
	public Widget click(String ownerHandle, String labelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find 
		return click(find(ownerHandle), labelOrPath);
	}
	
	/**
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.swt.IUIContext#click(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget click(Widget owner, String labelOrPath, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		handleConditions();
		try {
			return getDriver().click(owner, labelOrPath, buttonMask);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	/**
	 * @see com.windowtester.swt.IUIContext#click(java.lang.String, java.lang.String, int)
	 */
	public Widget click(String ownerHandle, String labelOrPath, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find 
		try {
			return click(find(ownerHandle), labelOrPath, buttonMask);
		} catch (WidgetNotFoundException e) {
			throw new com.windowtester.swt.MultipleWidgetsFoundException(e);
		} catch (MultipleWidgetsFoundException e) {
			throw new com.windowtester.swt.MultipleWidgetsFoundException(e);
		}
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(org.eclipse.swt.widgets.Widget)
	 */
	public Widget doubleClick(Widget widget) {
		handleConditions();
		return getDriver().doubleClick(widget);
	}

	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public Widget doubleClick(Widget w, int x, int y) {
		handleConditions();
		return getDriver().doubleClick(w, x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(java.lang.String)
	 */
	public Widget doubleClick(String handle) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find 
		return doubleClick(find(handle));
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget doubleClick(Widget owner, String labelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		handleConditions();
		try {
			return getDriver().doubleClick(owner, labelOrPath);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(java.lang.String, int, int)
	 */
	public Widget doubleClick(String handle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find 
		return doubleClick(find(handle), x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(org.eclipse.swt.widgets.Widget, int)
	 */
	public Widget doubleClick(Widget widget, int buttonMask) {
		handleConditions();
		return getDriver().doubleClick(widget, buttonMask);
	}

	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(org.eclipse.swt.widgets.Widget, int, int, int)
	 */
	public Widget doubleClick(Widget widget, int x, int y, int buttonMask) {
		handleConditions();
		return getDriver().doubleClick(widget, x, y, buttonMask);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(java.lang.String, int)
	 */
	public Widget  doubleClick(String widgetHandle, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		return doubleClick(find(widgetHandle), buttonMask);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#doubleClick(java.lang.String, int, int, int)
	 */
	public Widget doubleClick(String widgetHandle, int x, int y, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		return doubleClick(find(widgetHandle), x, y, buttonMask);
	}
	
	/**
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.swt.IUIContext#doubleClick(java.lang.String, java.lang.String)
	 */
	public Widget doubleClick(String ownerHandle, String labelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		try {
			return getDriver().doubleClick(find(ownerHandle), labelOrPath);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}
	
	/**
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 * @see com.windowtester.swt.IUIContext#contextClick(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget contextClick(Widget widget, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException {
		handleConditions();
		try {
			return getDriver().contextClick(widget, path);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}

	/**
	 * @see com.windowtester.swt.IUIContext#contextClick(org.eclipse.swt.widgets.Widget, int, int, java.lang.String)
	 */
	public Widget contextClick(Widget widget, int x, int y, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException {
		handleConditions();
		try {
			return getDriver().contextClick(widget, x, y, path);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}	
	
	/**
	 * @see com.windowtester.swt.IUIContext#contextClick(java.lang.String, java.lang.String)
	 */
	public Widget contextClick(String widgetHandle, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		return contextClick(find(widgetHandle), path);
	}

	/**
	 * @see com.windowtester.swt.IUIContext#contextClick(java.lang.String, int, int, java.lang.String)
	 */
	public Widget contextClick(String widgetHandle, int x, int y, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		return contextClick(find(widgetHandle), x, y, path);
	}
	
	
	/**
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.swt.IUIContext#contextClick(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Widget contextClick(String widgetHandle, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		try {
			return getDriver().contextClick(find(widgetHandle), itemPath, menuPath);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}
	
	
	/**
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 * @see com.windowtester.swt.IUIContext#contextClick(org.eclipse.swt.widgets.Widget, java.lang.String, java.lang.String)
	 */
	public Widget contextClick(Widget widget, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		try {
			return getDriver().contextClick(widget, itemPath, menuPath);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Drag and drop actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see com.windowtester.swt.IUIContext#dragTo(org.eclipse.swt.widgets.Widget)
	 */
	public Widget dragTo(Widget target) {
		handleConditions();
		return getDNDHelper().dragTo(target);
	}

	/**
	 * @see com.windowtester.swt.IUIContext#dragTo(java.lang.String)
	 */
	public Widget dragTo(String widgetHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		return getDNDHelper().dragTo(find(widgetHandle));
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#dragTo(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public Widget dragTo(Widget target, int x, int y) {
		handleConditions();
		return getDNDHelper().dragTo(target, x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#dragTo(java.lang.String, int, int)
	 */
	public Widget dragTo(String widgetHandle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		return getDNDHelper().dragTo(find(widgetHandle), x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#dragTo(org.eclipse.swt.widgets.Widget, java.lang.String, int, int)
	 */
	public Widget dragTo(Widget w, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		handleConditions();
		try {
			return getDNDHelper().dragTo(w, path, x, y);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#dragTo(java.lang.String, java.lang.String, int, int)
	 */
	public Widget dragTo(String widgetHandle, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		try {
			return getDNDHelper().dragTo(find(widgetHandle), path, x, y);
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			throw new WidgetNotFoundException(e);
		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
			throw new MultipleWidgetsFoundException(e);
		}
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#dragTo(int, int)
	 */
	public void dragTo(int x, int y) {
		handleConditions();
		getDNDHelper().dragTo(x,y);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Primitive mouse action commands
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see com.windowtester.swt.IUIContext#mouseMove(org.eclipse.swt.widgets.Widget)
	 */
	public void mouseMove(Widget w) {
		if (isMouseOrKeyActive()) //if mouse or key is down checking for conditions will block
			TraceHandler.trace(IRuntimePluginTraceOptions.CONDITIONS, "condition check skipped (mouse or key down)");
		else
			handleConditions();
		getDriver().mouseMove(w);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#mouseMove(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public void mouseMove(Widget w, int x, int y) {
		if (isMouseOrKeyActive()) //if mouse or key is down checking for conditions will block
			TraceHandler.trace(IRuntimePluginTraceOptions.CONDITIONS, "condition check skipped (mouse or key down)");
		else
			handleConditions();
		getDriver().mouseMove(w, x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#mouseMove(int, int)
	 */
	public void mouseMove(int x, int y) {
		if (isMouseOrKeyActive()) //if mouse or key is down checking for conditions will block
			TraceHandler.trace(IRuntimePluginTraceOptions.CONDITIONS, "condition check skipped (mouse or key down)");
		else
			handleConditions();
		getDriver().mouseMove(x, y);
	}
	


	/**
	 * @see com.windowtester.swt.IUIContext#mouseDown(int)
	 */
	public void mouseDown(int accel) {
		_mouseDown = true; 
		handleConditions();
		getDriver().mouseDown(accel);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#mouseUp(int)
	 */
	public void mouseUp(int accel) {
		getDriver().mouseUp(accel);
		_mouseDown = false;
		handleConditions(); //handle AFTER!
	}
	

	
	///////////////////////////////////////////////////////////////////////////
	//
	// Selection actions
	//
	///////////////////////////////////////////////////////////////////////////
	

	/**
	 * Defered to a later release
	 */
	
	
//	/**
//	 * @see com.windowtester.swt.IUIContext#select(org.eclipse.swt.widgets.Widget, int, int)
//	 */
//	public void select(Widget w, int start, int stop) {
//		getDriver().select(w, start, stop);
//	}
//	
//	/**
//	 * @see com.windowtester.swt.IUIContext#select(java.lang.String, int, int)
//	 */
//	public void select(String handle, int start, int stop) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		select(find(handle), start, stop);
//	}
//	
//	
//	/**
//	 * @see com.windowtester.swt.IUIContext#selectAll(org.eclipse.swt.widgets.Widget)
//	 */
//	public void selectAll(Widget w) {
//		getDriver().selectAll(w);
//	}
//	
//	/**
//	 * @see com.windowtester.swt.IUIContext#selectAll(java.lang.String)
//	 */
//	public void selectAll(String handle) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		selectAll(find(handle));
//	}
	
		
	
	///////////////////////////////////////////////////////////////////////////
	//
	// "Meta" actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see com.windowtester.swt.IUIContext#close(org.eclipse.swt.widgets.Shell)
	 */
	public void close(Shell shell) {
		//pushed up
		super.close(shell);
	}

	/**
	 * @see com.windowtester.swt.IUIContext#close(java.lang.String)
	 */
	public void close(String shellHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		Widget widget = find(shellHandle);
		if (!(widget instanceof Shell))
			throw new WidgetNotFoundException(); //TODO: this could be more explanatory...
		close((Shell)widget);
	}
	

	private void focus(Control control) {
		handleConditions();
		getDriver().focus(control);
	}

	/** Set the focus on to the given component. */
	/* TODO MAY NEED TO CHECK THAT THE CONTROL DOES INDEED HAVE FOCUS */
	public void setFocus(Widget widget) {
		handleConditions();
		TestHierarchy hierarchy = new TestHierarchy(getDisplay());
		while(!(widget instanceof Control))
			widget = hierarchy.getParent(widget);
		focus((Control)widget);
		waitForIdle();
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#setFocus(java.lang.String)
	 */
	public void setFocus(String widgetHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		setFocus(find(widgetHandle));
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#focus(java.lang.String)
	 */
	public void focus(String controlHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		Widget widget = find(controlHandle);
		if (!(widget instanceof Control))
			throw new WidgetNotFoundException(); //TODO: this could be more explanatory...
		focus((Control)widget);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#move(org.eclipse.swt.widgets.Control, int, int)
	 */
	public void move(Control control, int x, int y) {
		handleConditions();
		UIProxy.setLocation(control, x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#move(java.lang.String, int, int)
	 */
	public void move(String controlHandle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		Widget widget = find(controlHandle);
		if (!(widget instanceof Control))
			throw new WidgetNotFoundException(); //TODO: this could be more explanatory...
		move((Control)widget, x, y);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#resize(org.eclipse.swt.widgets.Control, int, int)
	 */
	public void resize(Control control, int width, int height) {
		handleConditions();
		UIProxy.resize(control, width, height);
	}
	
	/**
	 * @see com.windowtester.swt.IUIContext#resize(java.lang.String, int, int)
	 */
	public void resize(String controlHandle, int width, int height)  throws WidgetNotFoundException, MultipleWidgetsFoundException {
		//condition handling done in find
		Widget widget = find(controlHandle);
		if (!(widget instanceof Control))
			throw new WidgetNotFoundException(); //TODO: this could be more explanatory...
		resize((Control)widget, width, height);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Timing
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see com.windowtester.swt.IUIContext#waitForIdle()
	 */
	public void waitForIdle() {
		//FIXME: check for conditions here?
		handleConditions();
		getDriver().waitForIdle(getDisplay());	
	}

	/**
	 * @see com.windowtester.swt.IUIContext#waitForShellShowing(java.lang.String)
	 */
	public void waitForShellShowing(String shellName) {
		waitForShellShowing(shellName, DEFAULT_TIMEOUT);
	}
	
	public void waitForShellShowing(String shellName, int timeout) {
		wait(new ShellShowingCondition(shellName), timeout);
	}

	public void waitForShellDisposed(String shellName) {
		waitForShellDisposed(shellName, DEFAULT_TIMEOUT);
	}
	
	public void waitForShellDisposed(String shellName, int timeout) {
		super.wait(new ShellDisposedCondition(shellName), timeout);
	}

	public void wait(ICondition condition) {
		super.wait(condition);
	}

	public void wait(ICondition condition, long timeout) {
		super.wait(condition, timeout);
	}

	public void wait(ICondition condition, long timeout, int interval) {
		super.wait(condition, timeout, interval);
	}

	////////////////////////////////////////////////////////////////////////
	//
	// Widget finding helpers
	//
	////////////////////////////////////////////////////////////////////////
		
	public Widget find(String key) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		handleConditions();
		try {
			Widget w = null;
			
			//first check to see if this key has been registered with our mapper
			if (_mapper.containsKey(key))
				w = _mapper.find(key);
			//then check for a name match
			else 
				w = _finder.find(new NameMatcher(key));
			
			handleConditions();
			return w;
			
		} catch (abbot.finder.swt.WidgetNotFoundException wnfe) {
			/*
			 * Handled by a ModalShellClosingExceptionListener in the finder.
			 */
			//handle open shells first...
			//new ExceptionHandlingHelper(_display, true).closeOpenShells();
			//replace/rethrow with our own exception
			throw new WidgetNotFoundException(wnfe.getMessage());
		} catch (abbot.finder.swt.MultipleWidgetsFoundException mwfe) {
			/*
			 * Handled by a ModalShellClosingExceptionListener in the finder.
			 */
			//handle open shells first...
			//new ExceptionHandlingHelper(_display, true).closeOpenShells();
			//replace/rethrow with our own exception
			throw new MultipleWidgetsFoundException(mwfe.getMessage());
		}
	}


   /**
    * @throws WidgetNotFoundException 
    * @throws MultipleWidgetsFoundException 
    * @see com.windowtester.swt.IUIContext#find(com.windowtester.swt.WidgetLocator)
    */
	public Widget find(WidgetLocator locator) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		handleConditions();
		Matcher m = WidgetLocatorService.getMatcher(locator);
		try {
			return _finder.find(m);
		} catch (abbot.finder.swt.WidgetNotFoundException wnfe) {
			
			//!pq: happens in finder:
			//doScreenCapture("widget not found");
			//replace/rethrow with our own exception
			throw new WidgetNotFoundException(wnfe.getMessage());
		} catch (abbot.finder.swt.MultipleWidgetsFoundException mwfe) {
			
			//!pq: happens in finder:
			//doScreenCapture("mutiple widgets found");
			//replace/rethrow with our own exception
			throw new MultipleWidgetsFoundException(mwfe.getMessage());
		}
   	}
}
