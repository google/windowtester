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
package com.windowtester.runtime.gef.internal.experimental.locator;

import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;
import com.windowtester.runtime.gef.internal.IGEFPartLocator;
import com.windowtester.runtime.gef.internal.matchers.ModelObjectMatcher;
import com.windowtester.runtime.internal.IMatcher;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;

/**
 * A {@link IGEFPartLocator} that locates parts by matching properties of their
 * backing model objects as specified by an {@link IMatcher}.
 */
public class ModelObjectLocator extends AbstractGEFPartLocator {

	private final IMatcher _objectMatcher;

	/**
	 * Create an instance with the given matching criteria.
	 * @param objectMatcher
	 */
	public ModelObjectLocator(IMatcher objectMatcher) {
		_objectMatcher = objectMatcher;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.AbstractGEFPartLocator#buildMatcher()
	 */
	protected final IGEFEditPartMatcher buildMatcher() {
		return new ModelObjectMatcher(_objectMatcher);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.AbstractGEFPartLocator#buildViewerLocator()
	 */
	protected EditorLocator buildViewerLocator() {
		return new EditorLocator(".*"); //TODO: FIX THIS!
	}

}
