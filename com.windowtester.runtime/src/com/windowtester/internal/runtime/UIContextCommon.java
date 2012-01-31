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
package com.windowtester.internal.runtime;

import static com.windowtester.internal.runtime.util.StringUtils.NEW_LINE;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IProduct;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.LogHandler;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.internal.runtime.locator.IContextMenuItemLocator;
import com.windowtester.internal.runtime.selector.ClickHelper;
import com.windowtester.internal.runtime.selector.IClickDriver;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.condition.IConditionMonitor;
import com.windowtester.runtime.internal.AssertionHandler;
import com.windowtester.runtime.internal.IAssertionHandler;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.MenuItemLocator;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;

/**
 * Abstract superclass of {@link UIContextSWT} and {@link UIContextSwing}.
 */
public abstract class UIContextCommon
	implements IUIContext
{

	//dump system information
	static {
		StringBuilder sb = new StringBuilder();
		
		sb.append(NEW_LINE);
		sb.append("*************************************************").append(NEW_LINE);
		sb.append("WindowTester Runtime " + ProductInfo.build).append(NEW_LINE);
		echoSystemProperties("OS:", new String[]{
			"os.name", "os.arch", "os.version"
		}, sb);
		echoSystemProperties("Java:", new String[]{
			"java.vendor", "java.version"
		}, sb);
		echoSystemProperties("Spec:", new String[]{
			"java.specification.name", "java.specification.vendor", "java.specification.version"
		}, sb);
		echoSystemProperties("VM:", new String[]{
			"java.vm.specification.name", "java.vm.specification.vendor", "java.vm.specification.version"
		}, sb);
		sb.append("*************************************************").append(NEW_LINE);
		
		LogHandler.log(sb.toString());
		
//		if (Platform.isRunning()) {
//			try {
//				UsageProfilerPlugin.getPlugin().setIsRuntimeWorkspace(true);
//			}
//			catch (Exception e) {
//				// Not critical, so log the exception and move on...
//				e.printStackTrace();
//			}
//		}
	}

	private static void echoSystemProperties(String tag, String[] keys, StringBuilder sb) {
		sb.append("   ");
		sb.append(tag);
		for (int i = tag.length(); i < 6; i++)
			sb.append(" ");
		sb.append(System.getProperty(keys[0]));
		for (int i = 1; i < keys.length; i++) {
			sb.append(", ");
			sb.append(System.getProperty(keys[i]));
		}
		sb.append(NEW_LINE);
	}
	
	private IClickDriver clickHelper;
	private IConditionMonitor conditionMonitor;
	private IAssertionHandler assertionHandler;
	
	// //////////////////////////////////////////////////////////////////////////
	//
	// Adaptation
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * @see IUIContext#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		// TODO[author=pq]: (how) can clients add adapters?
		if (adapter == IConditionMonitor.class)
			return getConditionMonitor();
		return null;
	}

	
	// //////////////////////////////////////////////////////////////////////////
	//
	// Selection
	//
	// //////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#click(com.windowtester.runtime.locator.ILocator)
	 */
	public IWidgetLocator click(ILocator locator) throws WidgetSearchException {
		//unifying context and standard clicks
		if (locator instanceof IContextMenuItemLocator) {
			IContextMenuItemLocator context = (IContextMenuItemLocator)locator;
			return contextClick(context.getOwner(), context.getMenuPath());
		}
		
		return click(1 /* default is one click */, locator);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#click(int, com.windowtester.runtime.locator.ILocator)
	 */
	public IWidgetLocator click(int clickCount, ILocator locator) throws WidgetSearchException {
		return click(clickCount, locator, getDefaultButtonMask());
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#click(int, com.windowtester.runtime.locator.ILocator, int)
	 */
	public IWidgetLocator click(int clickCount, ILocator locator, int buttonMask) throws WidgetSearchException {		
		return getClickDriver().click(clickCount, locator, buttonMask);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#contextClick(com.windowtester.runtime.locator.ILocator, com.windowtester.runtime.locator.IMenuItemLocator)
	 */
	public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem) throws WidgetSearchException {
		return getClickDriver().contextClick(locator, menuItem);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#contextClick(com.windowtester.runtime.locator.ILocator, com.windowtester.runtime.locator.IMenuItemLocator, int)
	 */
	public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem, int modifierMask) throws WidgetSearchException {
		throw new UnsupportedOperationException();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#contextClick(com.windowtester.runtime.locator.ILocator, java.lang.String)
	 */
	public IWidgetLocator contextClick(ILocator locator, String menuItem) throws WidgetSearchException {
		return contextClick(locator, new MenuItemLocator(menuItem));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#contextClick(com.windowtester.runtime.locator.ILocator, java.lang.String, int)
	 */
	public IWidgetLocator contextClick(ILocator locator, String menuItem, int modifierMask) throws WidgetSearchException {
		return contextClick(locator, new MenuItemLocator(menuItem), modifierMask);
	}
	
	
	/**
	 * Get the click driver (responsible for performing clicks)
	 */
	protected IClickDriver getClickDriver() {
		if (clickHelper == null)
			clickHelper = new ClickHelper(this);
		return clickHelper;
	}
	
	/**
	 * Get the default mouse button mask in case it is unspecified.
	 */
	protected abstract int getDefaultButtonMask();


	// //////////////////////////////////////////////////////////////////////
	//
	// Condition-handling
	//
	// //////////////////////////////////////////////////////////////////////
	
	/**
	 * Answer the condition monitor local to this particular UI context.
	 * 
	 * @return the local condition monitor (not <code>null</code>).
	 */
	public IConditionMonitor getConditionMonitor() {
		if (conditionMonitor == null)
			conditionMonitor = new ConditionMonitor(ConditionMonitor.getInstance());
		return conditionMonitor;
	}

	/**
	 * Answer the invariant handler local to this particular UI context.
	 * 
	 * @return the local invariant handler (not <code>null</code>).
	 * @since 3.7.1
	 */
	protected IAssertionHandler getAssertionHandler() {
		if (assertionHandler == null)
			assertionHandler = new AssertionHandler(this);
		return assertionHandler;
	}
	
	
	/**
	 * This implementation simply calls {@link IUIContext#wait(ICondition, long)}
	 * with the specified condition and a 3 second timeout.
	 * 
	 * @see com.windowtester.runtime.IUIContext#assertThat(com.windowtester.runtime.condition.ICondition)
	 */
	public void assertThat(ICondition condition) throws WaitTimedOutException {
		getAssertionHandler().assertThat(condition);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#assertThat(java.lang.String, com.windowtester.runtime.condition.ICondition)
	 */
	public void assertThat(String message, ICondition condition) throws WaitTimedOutException {
		getAssertionHandler().assertThat(message, condition);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#ensureThat(com.windowtester.runtime.condition.IConditionHandler)
	 * @since 3.7.1
	 */
	public void ensureThat(IConditionHandler conditionHandler) throws Exception {
		getAssertionHandler().ensureThat(conditionHandler);
	}
	
	/**
	 * Check for any active conditions and handle them. If a condition is handled,
	 * original hover context will be restored post condition handling.
	 * 
	 * @return one of the following flags indicating what was processed:
	 *         {@link IConditionMonitor#PROCESS_NONE} if conditions were processed but no conditions were satisfied, 
	 *         {@link IConditionMonitor#PROCESS_ONE_OR_MORE} if conditions were processed and at least on condition was satisfied,
	 *         {@link IConditionMonitor#PROCESS_RECURSIVE} if conditions were already being processed and no additional action was taken.
	 */
	public int handleConditions() {
		return getConditionMonitor().process(this);
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Utility
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Capture the current screen as a file in a standard location
	 * with a name based upon the current test and test case.
	 * 
	 * @param desc the description for logging purposes
	 */
	public void doScreenCapture(String desc) {
		String testcaseID = TestMonitor.getInstance().getCurrentTestCaseID();
		TraceHandler.trace(IRuntimePluginTraceOptions.CONDITIONS, "Creating screenshot ("+ desc +") for testcase: " + testcaseID);
	    ScreenCapture.createScreenCapture(testcaseID);
	}



}
