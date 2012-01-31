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
package com.windowtester.runtime.swt.condition.eclipse;

import org.eclipse.ui.IPerspectiveDescriptor;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.condition.eclipse.PerspectiveCondition;
import com.windowtester.runtime.swt.internal.finder.eclipse.PerspectiveFinder;

/**
 * Tests if a given perspective is active.
 * 
 */
public class PerspectiveActiveCondition implements ICondition {

	private final String perspectiveId;
	private final ICondition condition;


	/**
	 * Create an active perspective condition by name, where a perspective's
	 * name is derived from the perspectives label attribute as defined in
	 * {@link IPerspectiveDescriptor#getLabel()}.
	 */
	public static ICondition forName(String perspectiveName) {
		return PerspectiveCondition.isActive(PerspectiveFinder.findNamed(perspectiveName));	
	}
	
	/**
	 * Create an active perspective condition by id, where a perspective's
	 * id is derived from the perspectives id attribute as defined in
	 * {@link IPerspectiveDescriptor#getId()}.
	 */
	public static ICondition forId(String perspectiveId) {
		return PerspectiveCondition.isActive(PerspectiveFinder.findWithId(perspectiveId));	
	}
	
	/**
	 * Create a condition for a perspective with the given perspective id.
	 * <p>
	 * Note: this is equivalent to calling {@link #forId(String)}.
	 */
	public PerspectiveActiveCondition(String perspectiveId) {
		this.perspectiveId = perspectiveId;
		this.condition     = forId(perspectiveId);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		return condition.test();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return " for perspective (" + perspectiveId
				+ ") to be open and active";
	}
}
