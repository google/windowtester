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
 * An event for close events (e.g., CTabItems)
 *
 */
public class SemanticWidgetClosedEvent extends UISemanticEvent implements ISemanticSelectionEvent {

	private static final long serialVersionUID = 8357231342251229327L;
	
	public SemanticWidgetClosedEvent(EventInfo info) {
		super(info);
	}

}
