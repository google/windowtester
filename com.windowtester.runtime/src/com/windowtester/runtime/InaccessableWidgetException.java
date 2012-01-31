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
 * Thrown when a matching widget is found, but it is not of the
 * necessary type or does not have the appropriate accessor method. For example, the
 * {@link com.windowtester.runtime.swt.locator.NamedWidgetLocator} throws this if
 * getText(IUIContext) is called and the widget found does not have a getText() method.
 */
public class InaccessableWidgetException extends WidgetSearchException
{
	private static final long serialVersionUID = 3035842156651482281L;

	public InaccessableWidgetException() {
	}
	
	public InaccessableWidgetException(String msg) {
		super(msg);
	}

	public InaccessableWidgetException(Throwable cause) {
		super(cause);
	}
}
