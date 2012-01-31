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
package com.windowtester.runtime.internal;

import com.windowtester.runtime.internal.application.IApplicationContext;

/**
 * Implementers might affect a given application context state.
 */
public interface IApplicationContextAdvisor {

	void preFlight(IApplicationContext context);
	void postFlight(IApplicationContext context);
	void exceptionOccurred(IApplicationContext context);
}
