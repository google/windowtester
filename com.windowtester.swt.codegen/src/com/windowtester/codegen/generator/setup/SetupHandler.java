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
package com.windowtester.codegen.generator.setup;

import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.recorder.event.ISemanticEvent;

/**
 * Base class for setup handlers.  Provides default implementations and adds
 * "default" status to the {@link ISetupHandler} interface.
 * 
 */
public abstract class SetupHandler implements ISetupHandler, IDefaultable {

	
	private boolean enabledByDefault = false;
	
	public SetupHandler enabledByDefault() {
		enabledByDefault = true;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#appliesTo(com.windowtester.codegen.eventstream.IEventStream)
	 */
	public boolean appliesTo(IEventStream stream) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#fullyHandles(com.windowtester.recorder.event.ISemanticEvent)
	 */
	public boolean fullyHandles(ISemanticEvent event) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.IDefaultable#isDefault()
	 */
	public boolean isDefault() {
		return enabledByDefault;
	}

}
