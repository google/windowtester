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
package com.windowtester.recorder;

import com.windowtester.recorder.event.ISemanticEventListener;

/**
 * Provides semantic events.
 *
 */
public interface ISemanticEventProvider {

	/**
	 * Add the given semantic event listener to this recorder.  Event listeners
	 * are notified of all semantic events.
	 */
	void addListener(ISemanticEventListener listener);

	/**
	 * Removes the given semantic event listener from this recorder.
	 */
	void removeListener(ISemanticEventListener listener);

}