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
package com.windowtester.recorder.event;

import java.io.Serializable;

/**
 * ISemanticEvent is the supertype of all recorder produced events.  There are two types 
 * of event: "meta events" such as recorder start and stop and "user events" such as key presses 
 * and button selections.
 * 
 * @see IRecorderSemanticEvent
 * @see IUISemanticEvent
 */
public interface ISemanticEvent extends Serializable {

	/**
	 * Accept this event handler.
	 * @param handler - the handler
	 */
	void accept(ISemanticEventHandler handler);

}
