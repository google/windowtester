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
package com.windowtester.runtime.gef.internal.finder;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

/**
 * Indicates that a location generates multiple anchors for a given request.
 */
public final class MultipleAnchorsForLocationException extends Exception {
	private static final long serialVersionUID = 873684758448544839L;
	
	
	public MultipleAnchorsForLocationException(String msg) {
		super(msg);
	}


	public MultipleAnchorsForLocationException(IFigure host, Point point) {
		this("mulitple anchors for point: " + point + " in " + host);
	}
	
	
	
}