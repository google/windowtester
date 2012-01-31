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
package com.windowtester.codegen.debug;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * Provider for the "Classpath" tab in the {@link DebugRecordingDialog}
 */
class DebugRecordingClasspathContentProvider
	implements IStructuredContentProvider, ITreeContentProvider
{
	private static final Object[] NO_CHILDREN = new Object[]{};
	
	DebugRecordingInfo root;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		root = (DebugRecordingInfo) newInput;
	}

	public void dispose() {
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {
		if (root != null) {
			if (parentElement == root)
				return root.getClasspathNames();
			Object[] children = root.getClasspath(parentElement);
			if (children != null)
				return children;
		}
		return NO_CHILDREN;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
}