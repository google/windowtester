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
package com.windowtester.runtime.internal.factory;

import com.windowtester.internal.runtime.Platform;
import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * Entry point to create instances of {@link IWidgetReference} for various instances of {@link Widget)
 */
public class WTRuntimeManager
{
	/** The WTRuntimeManager singleton */
	private static WTRuntimeManager manager;

	/** Synchronize against this before accessing {@link #manager} */
	private static Object LOCK = new Object();

	/** The debugging information to be included in exception messages */
	private static String platformDebugInfo = "No platform info";

	/**
	 * An array of references representing the known widget reference factories (not
	 * <code>null</code>, contains no <code>null</code>s)
	 */
	private WTRuntimeFactoryReference[] factoryReferences;

	/**
	 * Instantiates a new {@link IWidgetReference} for the specified widget
	 * 
	 * @param widget the widget (not <code>null</code>)
	 * @return the widget reference (not <code>null</code>)
	 * @throws RuntimeException if a widget reference cannot be created for the specified
	 *             widget
	 */
	public static IWidgetReference asReference(Object widget) {
		return getInstance().createReference(widget);
	}

	/**
	 * Answer the runtime manager singleton
	 * 
	 * @return the singleton (not <code>null</code>)
	 */
	public static WTRuntimeManager getInstance() {
		synchronized (LOCK) {
			if (manager == null) {
				manager = new WTRuntimeManager();
				manager.initialize();
			}
			return manager;
		}
	}

	//==========================================================================
	// Internal

	/**
	 * Initialize the receiver for execution within the Eclipse infrastructure such as
	 * when testing an RCP application, or without any Eclipse infrastructure such as when
	 * testing a Swing application, based upon the value returned by
	 * {@link Platform#isRunning()}
	 */
	private void initialize() {
		if (Platform.isRunning()) {
			// Initialize for execution within the Eclipse infrastructure - Eclipse RCP
			factoryReferences = WTRuntimeFactoryReferenceRCP.createFactoryReferences();
		}
		else {
			// Initialize for execution outside the Eclipse infrastructure - Swing and SWT outside Eclipse
			factoryReferences = WTRuntimeFactoryReferenceJava.createFactoryReferences();
		}
	}

	/**
	 * Cycles through the known widget reference factories until a factory returns
	 * instance of {@link IWidgetReference} for the specified widget.
	 * 
	 * @param widget the widget (not <code>null</code>)
	 * @return the widget reference (not <code>null</code>)
	 * @throws RuntimeException if a widget reference cannot be created for the specified
	 *             widget
	 */
	private IWidgetReference createReference(Object widget) {
		if (widget == null)
			throw new IllegalArgumentException("Cannot create a widget reference for null");
		for (WTRuntimeFactoryReference factoryRef : factoryReferences) {
			IWidgetReference widgetRef = factoryRef.createReference(widget);
			if (widgetRef != null)
				return widgetRef;
		}
		String errMsg = "Failed to create widget reference for instance of " + widget.getClass().getName();
		errMsg += StringUtils.NEW_LINE + "   " + getPlatformDebugInfo();
		for (WTRuntimeFactoryReference factoryRef : factoryReferences)
			errMsg += StringUtils.NEW_LINE + "   factory: " + factoryRef.getFactory();
		throw new RuntimeException(errMsg);
	}

	/**
	 * Set the debugging information to be included in exception messages
	 */
	public static void setPlatformDebugInfo(String debugInfo) {
		platformDebugInfo  = debugInfo;
	}

	/**
	 * Answer the debugging information to be included in exception messages
	 */
	public static String getPlatformDebugInfo() {
		return platformDebugInfo;
	}
}
