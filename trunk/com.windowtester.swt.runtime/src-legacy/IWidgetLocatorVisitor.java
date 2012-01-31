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

/**
 * A visitor for traversing WidgetLocator graphs.
 * 
 * @deprecated Use {@link com.windowtester.runtime.IWidgetLocatorVisitor} instead.
 */
public interface IWidgetLocatorVisitor {

	/**
	 * Visit this WidgetLocator object.
	 * @param locator - the locator to visit
	 */
	void visit(WidgetLocator locator);

}
