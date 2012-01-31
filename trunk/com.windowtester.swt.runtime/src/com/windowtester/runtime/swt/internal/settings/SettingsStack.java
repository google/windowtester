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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SettingsStack {

	private List _settings = new ArrayList();
	
//	{
//		//put default settings at bottom of stack
//		//push(getDefaultSettings());
//	}
	
	IRuntimeSettings peek() {
		return (IRuntimeSettings) _settings.get(lastIndex());
	}
	
	void push() {
		push(new Settings());
	}
	void push(IRuntimeSettings settings) {
		_settings.add(settings);
		//System.out.println(_settings.size());
	}
	
	IRuntimeSettings pop() {
		IRuntimeSettings top = (IRuntimeSettings) _settings.get(lastIndex());
		_settings.remove(lastIndex());
		//System.out.println(_settings.size());
		return top;
	}
	
	int lastIndex() {
		return _settings.size()-1;
	}
	
	IRuntimeSettings[] getItems() {
		return (IRuntimeSettings[]) _settings.toArray(new IRuntimeSettings[]{});
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (Iterator iter = _settings.iterator(); iter.hasNext();) {
			sb.append(iter.next());
			if (iter.hasNext())
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	
	static class Settings implements IRuntimeSettings {

		private int _contextClickDelay = IRuntimeSettingsConstants.UNSET;
		private int _finderRetries = IRuntimeSettingsConstants.UNSET;
		private int _finderRetryInterval = IRuntimeSettingsConstants.UNSET;
		private int _contextMenuWait = IRuntimeSettingsConstants.UNSET;
		
//		private void initDefaults() {
//			_contextClickDelay   = IRuntimeSettingsConstants.DEFAULT_CONTEXT_CLICK_DELAY;
//			_finderRetries       = IRuntimeSettingsConstants.DEFAULT_FINDER_RETRIES;
//			_finderRetryInterval = IRuntimeSettingsConstants.DEFAULT_FINDER_RETRY_INTERVAL;
//			_contextMenuWait     = IRuntimeSettingsConstants.DEFAULT_CONTEXT_MENU_VISIBLE_TIMEOUT;
//		}
		
		public int getPreContextClickDelay() {
			return _contextClickDelay;
		}

		public void setPreContextClickDelay(int ms) {
			_contextClickDelay = ms;
		}
		
		public int getFinderRetries() {
			return _finderRetries;
		}
		
		public void setFinderRetries(int numberOfRetries) {
			_finderRetries = numberOfRetries;
		}
		
		public int getFinderRetryInterval() {
			return _finderRetryInterval;
		}
		
		public void setFinderRetryInterval(int interval) {
			_finderRetryInterval = interval;
		}
		
		public int getWaitForContextMenuTimeOut() {
			return _contextMenuWait;
		}
		
		public void setWaitForContextMenuTimeOut(int wait) {
			_contextMenuWait = wait;
		}
	
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("Settings (");
			sb.append("context-click delay: ").append(_contextClickDelay);
			sb.append(", finder retries: ").append(_finderRetries);
			sb.append(", finder interval: ").append(_finderRetryInterval);
			sb.append(")");
			return sb.toString();
		}
		
	}
	
	
//	private static IRuntimeSettings getDefaultSettings() {
//		Settings settings = new Settings();
//		settings.initDefaults();
//		return settings;
//	}
	
}