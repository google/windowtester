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
package com.windowtester.ui.internal.corel.model;

import com.windowtester.ui.core.model.IObservation;

public class Observation implements IObservation {

	
	private final String _desc;

	public Observation(String desc) {
		_desc = desc;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IObservation#getDescription()
	 */
	public String getDescription() {
		return _desc;
	}

}
