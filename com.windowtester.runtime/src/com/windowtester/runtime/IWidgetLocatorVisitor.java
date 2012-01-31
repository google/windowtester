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
package com.windowtester.runtime;

/**
 * A visitor for traversing WidgetLocator graphs. New methods may be added to this
 * interface, so rather than implementing this interface, it is better to subclass
 * {@link WidgetLocatorAdapter}
 */
public interface IWidgetLocatorVisitor
{
	/**
	 * Visit this WidgetLocator object.
	 * 
	 * @param locator - the locator to visit
	 */
	void visit(WidgetLocator locator);

}
