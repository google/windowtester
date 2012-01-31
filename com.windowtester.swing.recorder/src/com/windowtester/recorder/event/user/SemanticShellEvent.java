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

import com.windowtester.internal.runtime.IWidgetIdentifier;


public class SemanticShellEvent extends UISemanticEvent {

	private static final long serialVersionUID = 4064903724976242810L;


	public SemanticShellEvent(EventInfo info) {
		super(info);
	}

	/**
	 * Get the name of the associated Shell.
	 */
	public String getName() {
		IWidgetIdentifier locator = getHierarchyInfo();
		String name = locator == null ? "null" : locator.getNameOrLabel();
		return name;
	}
	
	
}
