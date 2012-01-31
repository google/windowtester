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
 * A semantic event that corresponds to a move event.
 */
public class SemanticMoveEvent extends UISemanticEvent {

	private static final long serialVersionUID = 7247278664202263173L;

	/**
	 * Create an instance.
	 * @param info
	 */
	public SemanticMoveEvent(EventInfo info) {
		super(info);
	}

}
