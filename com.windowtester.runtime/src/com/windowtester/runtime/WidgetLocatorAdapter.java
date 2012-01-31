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
 * An abstract adapter class for visiting Widget Locator graphs.
 */
public abstract class WidgetLocatorAdapter implements IWidgetLocatorVisitor {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IWidgetLocatorVisitor#visit(com.windowtester.runtime.WidgetLocator)
	 */
	public void visit(WidgetLocator locator) {
		//no-op
	}


}
