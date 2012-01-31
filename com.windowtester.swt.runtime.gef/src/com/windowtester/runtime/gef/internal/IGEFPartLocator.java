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
package com.windowtester.runtime.gef.internal;

import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;

public interface IGEFPartLocator {
	
	/**
	 * Get an editor locator that identifies the editor that contains this part.
	 */
	EditorLocator getEditorLocator();
	
	/**
	 * Get a matcher that identifies the target part.
	 */
	IGEFEditPartMatcher getPartMatcher();
}
