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
package com.windowtester.ui.core.model;

public interface IEventGroup extends ISemanticEvent {

	IEvent[] getEvents();

	String getName();
	
	void setName(String name);
	
	void add(IEvent event);
	
	void addAll(IEvent[] events);

	void remove(IEvent event);
	
}
