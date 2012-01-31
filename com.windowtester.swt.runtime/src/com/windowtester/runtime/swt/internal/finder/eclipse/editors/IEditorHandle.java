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
package com.windowtester.runtime.swt.internal.finder.eclipse.editors;

/**
 * A handle on an eclipse editor.
 * 
 */
public interface IEditorHandle {

	/**
	 * Get the platform defined String Identifier for this editor.
	 * @return a String editor id
	 */
	String getId();

}
