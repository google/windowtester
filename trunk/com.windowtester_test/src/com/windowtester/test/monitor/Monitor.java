package com.windowtester.test.monitor;

import com.windowtester.internal.runtime.monitor.UIThreadMonitorCommon;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.condition.IConditionMonitor;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.monitor.IUIThreadMonitorListener;

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
final class Monitor extends UIThreadMonitorCommon
{
	private boolean _isUIResponsive;
	private long _testEnd;

	private boolean _wasTimeout;
	private boolean _wasTimeoutResponsive;

	////////////////////////////////////////////////////////////////////////////
	//
	// Testing
	//
	////////////////////////////////////////////////////////////////////////////

	public Monitor(long testDuration, long expectedDelay, boolean isUIResponsive) {
		super(createContext());
		_testEnd = System.currentTimeMillis() + testDuration;
		_isUIResponsive = isUIResponsive;
		_wasTimeout = false;
		setDefaultExpectedDelay(expectedDelay);
		setConsoleTracing(true);
		setListener(new IUIThreadMonitorListener() {
			public void uiTimeout(boolean isResponsive) {
				_wasTimeoutResponsive = isResponsive;
				_wasTimeout = true;
			}

			@SuppressWarnings("unused")
			public int processConditions() {
				// TODO Auto-generated method stub
				return IConditionMonitor.PROCESS_NONE;
			}
		});
		while (!hasTestEnded() && !_wasTimeout) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				// ignored
			}
		}
	}

	public boolean wasTimeout() {
		return _wasTimeout;
	}

	public boolean wasTimeoutResponsive() {
		return _wasTimeoutResponsive;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Implementation
	//
	////////////////////////////////////////////////////////////////////////////

	protected boolean isUIThreadResponsive() {
		return _isUIResponsive;
	}

	protected boolean hasTestEnded() {
		long currentTimeMillis = System.currentTimeMillis();
		trace("hasTestEnded ", _testEnd - currentTimeMillis);
		return _testEnd < currentTimeMillis;
	}

	protected void addEventListeners() {
	}

	protected void removeEventListeners() {
	}

	private static IUIContext createContext() {
		return new IUIContext() {

			public Object getActiveWindow() {
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator[] findAll(IWidgetLocator locator) {
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator find(IWidgetLocator locator) throws WidgetSearchException {
				// TODO Auto-generated method stub
				return null;
			}

			public void pause(int ms) {
				// TODO Auto-generated method stub

			}

			public int handleConditions() {
				// TODO Auto-generated method stub
				return 0;
			}

			/* (non-Javadoc)
			 * @see com.windowtester.runtime.IUIContext#getConditionMonitor()
			 */
			public IConditionMonitor getConditionMonitor() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public void wait(ICondition condition, long timeout, int interval) throws WaitTimedOutException {
				// TODO Auto-generated method stub

			}

			public void wait(ICondition condition, long timeout) throws WaitTimedOutException {
				// TODO Auto-generated method stub

			}

			public void wait(ICondition condition) throws WaitTimedOutException {
				// TODO Auto-generated method stub

			}

			public IWidgetLocator setFocus(IWidgetLocator locator) {
				// TODO Auto-generated method stub
				return null;
			}

			public void close(IWidgetLocator locator) {
				// TODO Auto-generated method stub

			}

			public void keyClick(int modifierMask, char c) {
				// TODO Auto-generated method stub

			}

			public void keyClick(char key) {
				// TODO Auto-generated method stub

			}

			public void keyClick(int key) {
				// TODO Auto-generated method stub

			}

			public void enterText(String txt) {
				// TODO Auto-generated method stub

			}

			public IWidgetLocator dragTo(ILocator locator, int modifierMask) {
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator dragTo(ILocator locator) {
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator mouseMove(ILocator locator) {
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem, int modifierMask)
				throws WidgetSearchException
			{
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem)
				throws WidgetSearchException
			{
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator click(int clickCount, ILocator locator, int modifierMask)
				throws WidgetSearchException
			{
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator click(int clickCount, ILocator locator) throws WidgetSearchException {
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator click(ILocator locator) throws WidgetSearchException {
				// TODO Auto-generated method stub
				return null;
			}

			@SuppressWarnings("unchecked")
			public Object getAdapter(Class adapter) {
				// TODO Auto-generated method stub
				return null;
			}

			public void assertThat(ICondition condition) throws WaitTimedOutException {
				// TODO Auto-generated method stub

			}

			public void assertThat(String message, ICondition condition) throws WaitTimedOutException {
				// TODO Auto-generated method stub
				
			}

			public IWidgetLocator contextClick(ILocator locator, String menuItem) throws WidgetSearchException {
				// TODO Auto-generated method stub
				return null;
			}

			public IWidgetLocator contextClick(ILocator locator, String menuItem, int modifierMask)
				throws WidgetSearchException
			{
				// TODO Auto-generated method stub
				return null;
			}

			public void ensureThat(IConditionHandler conditionHandler)
					throws WaitTimedOutException, Exception {
				// TODO Auto-generated method stub
				
			}

		};
	}
}