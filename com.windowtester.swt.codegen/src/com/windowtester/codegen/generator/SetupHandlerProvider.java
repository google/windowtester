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
package com.windowtester.codegen.generator;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.codegen.generator.setup.IDefaultable;
import com.windowtester.codegen.generator.setup.ISetupHandler;

/**
 * A provider of setup handlers appropriate for a given execution context.
 */
public class SetupHandlerProvider {

		
	private final ISetupHandler[] handlers;

	
	protected SetupHandlerProvider(ISetupHandler[] handlers) {
		this.handlers = handlers;
	}
	
	public ISetupHandler[] getHandlers() {
		return handlers;
	}

	public ISetupHandler[] getDefaults() {
		List defaults = new ArrayList();
		for (int i = 0; i < handlers.length; i++) {
			ISetupHandler handler = handlers[i];
			if (isDefault(handler))
				defaults.add(handler);
		}
		return (ISetupHandler[]) defaults.toArray(new ISetupHandler[]{});
	}

	private boolean isDefault(ISetupHandler handler) {
		if (handler instanceof IDefaultable)
			return ((IDefaultable)handler).isDefault();
		return false;
	}
	

}
