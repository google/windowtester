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
package com.windowtester.swt.event.model;

import org.eclipse.swt.dnd.DropTarget;

/** 
 * A callback to a recorder used for updating the primitive event
 * stream listening story.
 */
public interface IEventRecorderCallBack {

	/**
	 * Update recorder filters to include listening for drop 
	 * events on this target
	 * @param dropTarget the drop target to list for
	 */
	void listenForDropEvents(DropTarget dropTarget);

}
