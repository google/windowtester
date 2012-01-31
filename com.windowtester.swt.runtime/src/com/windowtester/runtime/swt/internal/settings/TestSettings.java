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
package com.windowtester.runtime.swt.internal.settings;


public class TestSettings implements IRuntimeSettings {

	private static final SettingsStack _stack = new SettingsStack();

	
	private static final TestSettings INSTANCE = new TestSettings();
	
	public static TestSettings getInstance() {
		return INSTANCE;
	}
	
	public IRuntimeSettings getCurrent() {
		return _stack.peek();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.settings.IRuntimeSettings#setContextClickDelay(int)
	 */
	public void setPreContextClickDelay(int ms) {
		getCurrent().setPreContextClickDelay(ms);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.settings.IRuntimeSettings#getContextClickDelay()
	 */
	public int getPreContextClickDelay() {
		//work backwards in stack until a valid value is found
		IRuntimeSettings[] items = _stack.getItems();
		for (int i = items.length-1; i >= 0; i--) {
			int value = items[i].getPreContextClickDelay();
			if (isSet(value))
				return value;
		}
		//TODO: log this
		return IRuntimeSettingsConstants.DEFAULT_CONTEXT_CLICK_DELAY;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.settings.IRuntimeSettings#getFinderRetries()
	 */
	public int getFinderRetries() {
		//work backwards in stack until a valid value is found
		IRuntimeSettings[] items = _stack.getItems();
		for (int i = items.length-1; i >= 0; i--) {
			int value = items[i].getFinderRetries();
			if (isSet(value))
				return value;
		}
		//TODO: log this
		return IRuntimeSettingsConstants.DEFAULT_FINDER_RETRIES;

	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.settings.IRuntimeSettings#setFinderRetries(int)
	 */
	public void setFinderRetries(int numberOfRetries) {
		getCurrent().setFinderRetries(numberOfRetries);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.settings.IRuntimeSettings#getFinderInterval()
	 */
	public int getFinderRetryInterval() {
		//work backwards in stack until a valid value is found
		IRuntimeSettings[] items = _stack.getItems();
		for (int i = items.length-1; i >= 0; i--) {
			int value = items[i].getFinderRetryInterval();
			if (isSet(value))
				return value;
		}
		//TODO: log this
		return IRuntimeSettingsConstants.DEFAULT_FINDER_RETRY_INTERVAL;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.settings.IRuntimeSettings#setFinderInterval(int)
	 */
	public void setFinderRetryInterval(int interval) {
		getCurrent().setFinderRetryInterval(interval);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.settings.IRuntimeSettings#getWaitForContextMenuTimeOut()
	 */
	public int getWaitForContextMenuTimeOut() {
		//work backwards in stack until a valid value is found
		IRuntimeSettings[] items = _stack.getItems();
		for (int i = items.length-1; i >= 0; i--) {
			int value = items[i].getWaitForContextMenuTimeOut();
			if (isSet(value))
				return value;
		}
		//TODO: log this
		return IRuntimeSettingsConstants.DEFAULT_CONTEXT_MENU_VISIBLE_TIMEOUT;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.settings.IRuntimeSettings#setWaitForContextMenuTimeOut(int)
	 */
	public void setWaitForContextMenuTimeOut(int wait) {
		getCurrent().setWaitForContextMenuTimeOut(wait);
	}
	
	
	private boolean isSet(int value) {
		return value != IRuntimeSettingsConstants.UNSET;
	}

	public void push() {
		_stack.push();
	}
	
	public void pop() {
		_stack.pop();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		IRuntimeSettings curr = getCurrent();
		return curr == null ? "null" : curr.toString();	
	}


	public String getSettingsStackString() {
		return _stack.toString();
	}


	
	
}
