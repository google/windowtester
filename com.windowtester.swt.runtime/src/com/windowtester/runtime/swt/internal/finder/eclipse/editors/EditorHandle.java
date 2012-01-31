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

import org.eclipse.core.runtime.IPath;

public class EditorHandle implements IEditorHandle {

	private final String _id;
	private final IPath _source;

	public EditorHandle(String id, IPath source) {
		_id = id;
		_source = source;
	}

	public String getId() {
		return _id;
	}
	
	public IPath getSourcePath() {
		return _source;
	}
	

}
