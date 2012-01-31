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

import com.windowtester.runtime.locator.IWidgetReference;

/**
 * A facade for an instance of {@link WTRuntimeFactory}
 */
abstract class WTRuntimeFactoryReference
{
	protected static final String FACTORY_TAG = "factory";
	protected static final String CLASS_TAG = "class";
	protected static final String OS_TAG = "os";
	protected static final String WS_TAG = "ws";
	protected static final String ARCH_TAG = "arch";

	/**
	 * Answer true if the values is <code>null</code> or if the values contains the
	 * specified key
	 * 
	 * @param key the key such as the operation system (e.g. "win32"), the windowing
	 *            system, or the processor
	 * @param values a comma separated list of operating systems (or windowing systems, or
	 *            processors) for which the factory is valid or <code>null</code> if the
	 *            factory is valid for all
	 * @return <code>true</code> if the factory is valid for the specified aspect of this
	 *         execution environment
	 */
	protected static boolean isFactoryFor(String key, String values) {
		if (key == null || values == null)
			return true;
		for (String value : values.split(","))
			if (key.equals(value.trim()))
				return true;
		return false;
	}

	/**
	 * The factory wrappered by this reference or <code>null</code> if it has not been
	 * instantiated.
	 * 
	 * @see #getFactory()
	 */
	private WTRuntimeFactory factory;

	/** Synchronize against this before accessing {@link #factory} */
	private static Object LOCK = new Object();

	/**
	 * Instantiates a new {@link IWidgetReference} for the specified widget if possible,
	 * or returns <code>null</code> if not.
	 * 
	 * @param widget the widget
	 * @return the widget reference or <code>null</code> if no widget reference can be
	 *         created for the specified widget.
	 */
	IWidgetReference createReference(Object widget) {
		return getFactory().createReference(widget);
	}

	/**
	 * Answer the factory, creating it if necessary. If the factory cannot be created,
	 * then
	 * 
	 * @return the factory (not <code>null</code>)
	 */
	WTRuntimeFactory getFactory() {
		synchronized (LOCK) {
			if (factory == null) {
				try {
					factory = createFactory();
				}
				catch (Exception e) {
					logFactoryCreationException(e);
				}
				if (factory == null)
					factory = NO_OP_FACTORY;
			}
		}
		return factory;
	}

	/**
	 * Instantiate the factory
	 * 
	 * @return the factory (not <code>null</code>)
	 * @throws Exception if the factory could not be instantiated
	 */
	abstract WTRuntimeFactory createFactory() throws Exception;

	/**
	 * Log the failure to create a factory for the receiver. Default implementation prints
	 * a stack trace to standard error. Subclasses may override or extend.
	 * 
	 * @param exception the exception (not <code>null</code>)
	 */
	void logFactoryCreationException(Exception exception) {
		exception.printStackTrace();
	}

	/**
	 * A placeholder used when a factory reference fails to instantiate its factory
	 */
	private static final WTRuntimeFactory NO_OP_FACTORY = new WTRuntimeFactory() {
		public IWidgetReference createReference(Object widget) {
			return null;
		}
	};
}
