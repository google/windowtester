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
package com.windowtester.runtime.swt.internal.matchers.eclipse;

import org.eclipse.swt.widgets.Control;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.internal.matchers.ContainedInMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;

/**
 * Base class for eclipse {@code Part} matchers.
 */
public abstract class PartComponentMatcher implements ISWTWidgetMatcher {

	/** Cached container matcher */
	private ISWTWidgetMatcher containerMatcher;
		
	private ISWTWidgetMatcher getContainerMatcher() throws WidgetSearchException {
		if (containerMatcher == null)
			containerMatcher = createContainerMatcher();
		return containerMatcher;
	}

	private ContainedInMatcher createContainerMatcher()
			throws WidgetSearchException {
		return new ContainedInMatcher(SWTWidgetReference.forWidget(getPartControl()));
	}
	
	protected abstract Control getPartControl() throws WidgetSearchException;

	/**
	 * Returns true if the given widget is contained in the widget hierarchy of the control
	 * associated with the {@link EditorPart} defined by this matcher.
	 * @see com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> ref) {
		try {
			return getContainerMatcher().matches(ref); // TODO[pq]: this is the legacy exception handling and should be revisited
		} catch (WidgetSearchException e) {
			throw new RuntimeException(e);
		}
	}

}
	