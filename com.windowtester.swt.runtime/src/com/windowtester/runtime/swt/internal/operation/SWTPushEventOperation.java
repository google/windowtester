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
package com.windowtester.runtime.swt.internal.operation;

import org.eclipse.swt.widgets.Event;

/**
 * This is temporary class for pushing events on the OS event queue.
 * 
 * @deprecated Use more specific subclasses of {@link SWTOperation} such as
 *             {@link SWTMouseOperation} and {@link SWTKeyOperation}
 */
public class SWTPushEventOperation extends SWTOperation
{
	/**
	 * Construct a new instance to push the specified event on the event queue
	 * 
	 * @param event the event to push
	 */
	public SWTPushEventOperation(Event event) {
		queueOSEvent(event);
	}
}
