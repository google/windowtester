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

public class RecorderControllerStartEvent implements IRecorderSemanticEvent {

	private static final long serialVersionUID = 3550315448169821517L;

	/** port number to pass to main Workbench */
	private int port;
	
	public RecorderControllerStartEvent(int port) {
		this.port = port;
	}

	/**
	 * @return Returns the port.
	 */
	public int getPort() {
		return port;
	}

	public void accept(ISemanticEventHandler handler) {
		handler.handleControllerStart(this);
	}

}
