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
package com.windowtester.recorder.event.meta;

import com.windowtester.recorder.event.IRecorderSemanticEvent;
import com.windowtester.recorder.event.ISemanticEventHandler;

public class RecorderAssertionHookAddedEvent implements IRecorderSemanticEvent {

	
	private static final long serialVersionUID = -1443156876689979723L;
	
	private final String _hookName;

	public RecorderAssertionHookAddedEvent(String hookName) {
		_hookName = hookName;
	}

	public String getHookName() {
		return _hookName;
	}
	
	
	public void accept(ISemanticEventHandler handler) {
		handler.handle(this);
	}

}
