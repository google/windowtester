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
package com.windowtester.recorder.event.user;


/**
 * A semantic event that corresponds to a resize event.
 */
public class SemanticResizeEvent extends UISemanticEvent {
	
	private static final long serialVersionUID = -8325305574265101532L;
	
	/**@serial */
	private final int _width;
	/**@serial */
	private final int _height;
	
	
	/**
	 * Create an instance.
	 * @param info
	 * @param width
	 * @param height
	 */
	public SemanticResizeEvent(EventInfo info, int width, int height) {
		super(info);
		_width = width;
		_height = height;
	}
	
	/** Get the height of the resize event.
	 */
	public int getHeight() {
		return _height;
	}
	
	/** Get the width of the resize event.
	 */
	public int getWidth() {
		return _width;
	}

}
