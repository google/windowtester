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
package com.windowtester.internal.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.InputEvent;

import abbot.WaitTimedOutError;
import abbot.script.Condition;
import abbot.util.AWT;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.finder.swing.SwingWidgetFinder;
import com.windowtester.internal.runtime.Diagnostic;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.internal.runtime.RuntimePlugin;
import com.windowtester.internal.runtime.UIContextCommon;
import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.internal.runtime.finder.IWidgetFinder;
import com.windowtester.internal.runtime.preferences.PlaybackSettings;
import com.windowtester.internal.runtime.selector.ClickHelper;
import com.windowtester.internal.swing.monitor.UIThreadMonitorSwing;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.monitor.IUIThreadMonitor;
import com.windowtester.runtime.swing.locator.AbstractPathLocator;
import com.windowtester.runtime.swing.locator.JTableItemLocator;
import com.windowtester.runtime.swing.locator.JTextComponentLocator;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;

/**
 * Concrete implementation of {@link com.windowtester.runtime.IUIContext}
 */
public class UIContextSwing extends UIContextCommon
{

	private static final int DEFAULT_BUTTON_MASK = InputEvent.BUTTON1_MASK;
	
	private boolean _licenseChecked = false;
    private UIDriverSwing _driver;    
    private IUIThreadMonitor _threadMonitor;
    

	
	public IWidgetLocator click(int clickCount, ILocator locator, int buttonMask) throws WidgetSearchException {
		handleConditions();
		return super.click(clickCount, locator, buttonMask);
	}

	
	public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem, int modifierMask) throws WidgetSearchException {
		handleConditions();
		return super.contextClick(locator, menuItem, modifierMask);
	}

	public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem) throws WidgetSearchException {
		handleConditions();
		return super.contextClick(locator, menuItem);
	}

	public IWidgetLocator contextClick(ILocator locator, String menuItem, int modifierMask) throws WidgetSearchException {
		handleConditions();
		return super.contextClick(locator, menuItem, modifierMask);
	}

	public IWidgetLocator contextClick(ILocator locator, String menuItem) throws WidgetSearchException {
		handleConditions();
		return super.contextClick(locator, menuItem);
	}

	public IWidgetLocator mouseMove(ILocator locator) throws WidgetSearchException {
		handleConditions();
		IWidgetLocator widgetLocator = ClickHelper.getWidgetLocator(locator);
		Component w = (Component) ((IWidgetReference)find(widgetLocator)).getWidget();
		getDriver().mouseMove(w);
		return widgetLocator;
	}

	public IWidgetLocator dragTo(ILocator locator) throws WidgetSearchException {
		Point p;
		handleConditions(); 
		IWidgetLocator widgetLocator = ClickHelper.getWidgetLocator(locator);
		Component w = (Component) ((IWidgetReference)find(widgetLocator)).getWidget();
		if (locator instanceof AbstractPathLocator){
			String path = ((AbstractPathLocator)locator).getPath();
			p = getDriver().getLocation(w,path);
		}
		else if (locator instanceof JTableItemLocator){
			JTableItemLocator loc = (JTableItemLocator)locator;
			p = getDriver().getLocation(w,loc.getRow(),loc.getColumn());
		}
		else if (locator instanceof JTextComponentLocator){
			p = getDriver().getLocation(w, ((JTextComponentLocator)locator).getCaretPosition());
		}
		else {
			p = getDriver().getLocation(w);
		}
		
		getDriver().doDragTo(w,p.x,p.y);
		return widgetLocator;
	}

	public IWidgetLocator dragTo(ILocator locator, int buttonMask) throws WidgetSearchException {
		try {
			getDriver().mouseDown(buttonMask);
			return dragTo(locator);
		} finally {
			getDriver().mouseUp(buttonMask);
		}
	}

	public void enterText(String txt) {
		handleConditions();
		getDriver().enterText(txt);

	}

	public void keyClick(int key) {
		handleConditions();
		getDriver().keyClick(key);

	}

	public void keyClick(char key) {
		handleConditions();
		getDriver().keyClick(key);

	}

	public void keyClick(int ctrl, char c) {
		handleConditions();
		getDriver().keyClick(ctrl, c);

	}

	public void close(IWidgetLocator locator) {
		WidgetReference l = null;
		try {
			l = (WidgetReference)find(locator);
		} catch (WidgetSearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// if component to close is not a window, throw exception
		if (l.getWidget() instanceof Window)
			getDriver().close((Window)l.getWidget());
		else
			throw new UnsupportedOperationException();

	}


	public void wait(ICondition condition) throws WaitTimedOutException {
		wait(condition, UIDriverSwing.getDefaultTimeout());
	}

	public void wait(ICondition condition, long timeout) throws WaitTimedOutException {
		wait(condition, timeout, UIDriverSwing.getDefaultSleepInterval());
	}

	public void wait(ICondition condition, long timeout, int interval) throws WaitTimedOutException {
		if (_threadMonitor != null)
			_threadMonitor.expectDelay(timeout);
		handleConditions();

		abbot.script.Condition c = getAbbotCondition(condition);
		try {
			getDriver().wait(c,timeout,interval);
		}
		catch (WaitTimedOutError e){
			throw new WaitTimedOutException("Timed out waiting for " + condition);
		}	
	}

	public void pause(int ms) {
		if (_threadMonitor != null)
			_threadMonitor.expectDelay(ms);
		handleConditions();
		getDriver().pause(ms);
	}

	public IWidgetLocator find(IWidgetLocator locator) throws WidgetSearchException {
		IWidgetLocator[] locators = findAll(locator);
		if (locators.length > 1){
			takeScreenShot();
			throw new MultipleWidgetsFoundException("Multiple Components found");
		}
		if (locators.length == 0){	
			takeScreenShot();
			throw new WidgetNotFoundException(Diagnostic.toString("Component not found "+ locator.toString(), locator)); 
		}
		return locators[0];
	}

	public IWidgetLocator[] findAll(IWidgetLocator locator) {
		IWidgetLocator[] locators = (IWidgetLocator[])locator.findAll(this);
		return locators;
	}

	public Object getActiveWindow() {
		return AWT.getWindow(AWT.getFocusOwner());
	}

	
	private void takeScreenShot() {
		String testcaseID = TestMonitor.getInstance().getCurrentTestCaseID();
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "Creating screenshot for testcase: " + testcaseID);
        //TODO: make this filename format user configurable
		ScreenCapture.createScreenCapture(testcaseID /*+ "_" + desc*/);
	}
	
	
	/**
	 *  translate a ICondition to a abbot Condition
	 * @param c
	 * @return a abbot.script.Condition
	 */
	private Condition getAbbotCondition(final ICondition c){
		Condition condition = new Condition(){
			public boolean test() {
				return ConditionMonitor.test(UIContextSwing.this, c);
			}
			public String toString() {
				return c.toString();
			}
		};
		return condition;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////
	
	public UIDriverSwing getDriver(){
		if (_driver == null){
			if (getPlaybackSettings().getDelayOn()) 
				_driver = new DelayUIDriverSwing();
			else
				_driver = new UIDriverSwing();
		}
		return _driver;
	}
	
	
	protected PlaybackSettings getPlaybackSettings() {
		return Platform.isRunning() ? RuntimePlugin.getDefault().getPlaybackSettings() :
									  PlaybackSettings.loadFromFile();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.UIContext2Common#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		
		if (adapter.equals(IUIThreadMonitor.class)) {
			if (_threadMonitor == null)
				_threadMonitor = new UIThreadMonitorSwing(this);
			return _threadMonitor;
		}
		
		if (adapter == IWidgetFinder.class)
			return new SwingWidgetFinder();
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.UIContext2Common#getDefaultButtonMask()
	 */
	protected int getDefaultButtonMask() {
		return DEFAULT_BUTTON_MASK;
	}
	
}
