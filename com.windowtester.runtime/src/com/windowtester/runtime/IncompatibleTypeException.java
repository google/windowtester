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
 * Signals that a locator has been constructed using an incompatible type.
 * <p>
 * For example:
 * <pre>
 * ui.find(new TreeItemLocator("path/to/node", new ComboLocator()));
 * </pre>
 * would generate such an exception because a tree item cannot be the
 * child of a Combo.
 */
public class IncompatibleTypeException extends IllegalStateException {

	private static final long serialVersionUID = -1675440415583239600L;

    /**
     * Constructs an IncompatibleTypeException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public IncompatibleTypeException() {
    	super();
    }

    /**
     * Constructs an IncompatibleTypeException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * @param msg the String that contains a detailed message
     */
    public IncompatibleTypeException(String msg) {
    	super(msg);
    }
	
	
}
