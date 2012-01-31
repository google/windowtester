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
package com.windowtester.swt.codegen.wizards;

import java.util.Comparator;

import org.eclipse.jface.preference.IPreferenceStore;

import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;
import com.windowtester.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.preferences.CodegenPreferences;

public class SetupHandlerTableStore {

	private static final String[] NO_STRINGS = new String[]{};

	private static final String DELIM = ";";

	public static final String SELECTED_HANDLER_STRING_KEY = "selected_setup_handlers";
	public static final String STORE_ACTIVE_KEY            = "store_active";
	
	
	private final IPreferenceStore store;

	
	
	private static final class ByClassComparator implements Comparator {
		public int compare(Object h1, Object h2) {
			if (h1.getClass() == h2.getClass())
				return 0;
			return -1;
		}
	}
	private static final ByClassComparator handlerComparator = new ByClassComparator();


	public static Comparator getHandlerComparator() {
		return handlerComparator;
	}
	
	public static SetupHandlerTableStore forDefaultPreferences() {
		return new SetupHandlerTableStore(CodegenPreferences.getStore());
	}
	
	
	public SetupHandlerTableStore(IPreferenceStore store) {
		this.store = store;
	}
	
	
	public SetupHandlerSet getSelectedHandlers() {
		return getHandlersFromStore(store);
	}


	protected SetupHandlerSet getHandlersFromStore(IPreferenceStore store) {
		String handlerStringValue = getSelectedHandlerStringValue(store);
		return parseHandlersFromString(handlerStringValue);		
	}

	//public for testing
	public SetupHandlerSet parseHandlersFromString(String handlerString) {
		SetupHandlerSet handlers = new SetupHandlerSet();
		String[] handlerNames = parseHandlerStrings(handlerString);
		for (int i = 0; i < handlerNames.length; i++) {
			ISetupHandler handler = handlerForName(handlerNames[i]);
			if (handler != null)
				handlers = handlers.withHandler(handler);
		}
		return handlers;
	}



	public ISetupHandler handlerForName(String className) {
		try {
			return (ISetupHandler) Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			LogHandler.log(e);
		} catch (IllegalAccessException e) {
			LogHandler.log(e);
		} catch (ClassNotFoundException e) {
			LogHandler.log(e);
		}
		return null;
	}


	public String[] parseHandlerStrings(String handlerString) {
		if (isNullOrEmpty(handlerString))
			return NO_STRINGS;
		return handlerString.split(DELIM);
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}

	private String getSelectedHandlerStringValue(IPreferenceStore store) {
		return store.getString(SELECTED_HANDLER_STRING_KEY);
	}



	public void setSelectedHandlers(ISetupHandler[] handlers) {
		String handlerString = toString(handlers);
		store.setValue(SELECTED_HANDLER_STRING_KEY, handlerString);
		setActive();
	}



	public String toString(ISetupHandler[] handlers) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < handlers.length; i++) {
			sb.append(handlers[i].getClass().getName()).append(DELIM);
		}
		return sb.toString();
	}


	public SetupHandlerSet findMatches(ISetupHandler[] handlers) {
		SetupHandlerSet handlerSet = new SetupHandlerSet();
		for (int i = 0; i < handlers.length; i++) {
			ISetupHandler handler = handlers[i];
			if (storeContains(handler))
				handlerSet = handlerSet.withHandler(handler);
		}
		return handlerSet;
	}


	public boolean storeContains(ISetupHandler handler) {
		ISetupHandler[] storedHandlers = getHandlersFromStore(store).toArray();
		for (int i = 0; i < storedHandlers.length; i++) {
			if (matches(handler, storedHandlers[i]))
				return true;
		}
		return false;
	}

	private boolean matches(ISetupHandler h1, ISetupHandler h2) {
		return handlerComparator.compare(h1, h2) == 0;
	}
	
	private void setActive() {
		store.setValue(STORE_ACTIVE_KEY, true);
	}
	
	public boolean isActivated() {
		return store.getBoolean(STORE_ACTIVE_KEY);
	}
	

}
