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
package com.windowtester.runtime.swt.internal.finder.eclipse.views;

/**
 * Basic {@link com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewHandle} implementation.
 */
public class ViewHandle implements IViewHandle {

	/** The view id */
	private final String _viewId;

	/**
	 * Create an instance describing the given view id (must not be null).
	 */
	public ViewHandle(String viewId) {
		if (viewId == null)
			throw new AssertionError("view id must not be null");
		_viewId = viewId;
	}
	
	/**
	 * @see com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewHandle#getId()
	 */
	public String getId() {
		return _viewId;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ViewHandle) {
			ViewHandle other = (ViewHandle)obj;
			return getId().equals(other.getId());
		}
		return false;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return 13*getId().hashCode();
	}
	
	
	/**
	 * Get a String representation of this view handle.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ViewHandle("+ getId() + ")";
	}
	
	
}
