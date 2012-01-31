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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A group of handlers.
 */
public class SetupHandlerSet {

	
	public static final SetupHandlerSet EMPTY = forHandlers(new ISetupHandler[]{});
	
	private List handlers = new ArrayList();

	public static SetupHandlerSet forHandlers(ISetupHandler[] handlers) {
		SetupHandlerSet handlerSet = new SetupHandlerSet();
		handlerSet.handlers.addAll(Arrays.asList(handlers));
		return handlerSet;
	}

	public ISetupHandler[] toArray() {
		return (ISetupHandler[]) handlers.toArray(new ISetupHandler[]{});
	}

	public static SetupHandlerSet forHandler(ISetupHandler handler) {
		SetupHandlerSet set = new SetupHandlerSet();
		set.handlers.add(handler);
		return set;
	}
	
	public SetupHandlerSet withHandler(ISetupHandler handler) {
		handlers.add(handler);
		return this;
	}
	
	
	
}
