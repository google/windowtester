package com.windowtester.event.selector.swt;

import com.windowtester.runtime.IUIContext;

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
public class ComboSelector extends com.windowtester.runtime.swt.internal.selector.ComboSelector {
	
	public ComboSelector() {
		super();
	}
	
	//create with a backpointer to the ui for implementing conditional waits
	public ComboSelector(IUIContext ui) {
		super(ui);
	}
		
}
