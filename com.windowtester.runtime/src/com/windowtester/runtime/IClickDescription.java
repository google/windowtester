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
 * Description of a click.  
 * <p>
 * <strong>Internal:</strong> This interface is for internal use only and not meant to 
 * be implemented by clients.
 * 
 * @noimplement 
 *
 */
public interface IClickDescription {

	//sentinel constant for marking center clicks
	static final int DEFAULT_CENTER_CLICK = -1;
	
	int clicks();

	int x();
	
	int y();
	
	int relative();
	
	int modifierMask();
	
	boolean isDefaultCenterClick();
	
		
}
