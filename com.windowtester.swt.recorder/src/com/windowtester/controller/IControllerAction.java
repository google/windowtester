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
package com.windowtester.controller;

import org.eclipse.swt.graphics.Image;

/**
 * An interface for actions contributed to the Event Controller Dialog
 */
public interface IControllerAction {
	
	
	/**
	 * @return the Image associated with this action
	 */
	Image getImage();

	/**
	 * @return the "tool tip" text associated with this action
	 */
	String getToolTipText();
	
	/**
	 * Perform the specified action
	 */
	void perform();
	
	
}
