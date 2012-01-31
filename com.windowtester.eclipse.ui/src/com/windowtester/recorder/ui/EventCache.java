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
package com.windowtester.recorder.ui;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.ui.core.model.ISemanticEvent;

public class EventCache implements IEventProvider {

	List _events = new ArrayList();
	
	public List getEventList() {
		return _events;
	}

	public ISemanticEvent[] getEvents() {
		return (ISemanticEvent[]) getEventList().toArray(new ISemanticEvent[]{});
	}
	
}
