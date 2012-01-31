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
package com.windowtester.runtime.gef.internal;

import org.eclipse.draw2d.Connection;

import com.windowtester.runtime.gef.IFigureReference;

/**
 * A wrapper for a Draw2D {@link Connection}.
 */
public interface IConnectionInfo {

	/**
	 * Get the underlying {@link Connection} instance.
	 */
	Connection getConnection();
	
	/**
	 * Get the figure which is the source of this connection.
	 */
	IFigureReference getSource();

	/**
	 * Get the figure which is the target of this connection.
	 */
	IFigureReference getTarget();
	
	
}
