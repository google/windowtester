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
package com.windowtester.runtime.swt.locator.eclipse;

import java.io.Serializable;

import org.eclipse.ui.IPerspectiveDescriptor;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.condition.eclipse.PerspectiveActiveConditionHandler;
import com.windowtester.runtime.swt.internal.condition.eclipse.PerspectiveClosedConditionHandler;
import com.windowtester.runtime.swt.internal.condition.eclipse.PerspectiveCondition;
import com.windowtester.runtime.swt.internal.finder.eclipse.PerspectiveFinder;

/**
 * Locates eclipse perspectives.
 */
public class PerspectiveLocator implements ILocator, Serializable {

	private static final long serialVersionUID = 8680276551536726486L;

	private final IPerspectiveDescriptor descriptor;
	
	

	/**
	 * Create a new Perspective Locator that identifies perspectives by name.
	 * @param perspectiveName the name of the target perspective
	 * @return a new name-matching Perspective Locator
	 * @since 3.8.1
	 */
	public static PerspectiveLocator forName(String perspectiveName) {
		return new PerspectiveLocator(PerspectiveFinder.findByNameInRegistry(perspectiveName));
	}

	/**
	 * Create a new Perspective Locator that identifies perspectives by id.
	 * @param perspectiveId the id of the target perspective
	 * @return a new id-matching Perspective Locator
	 * @since 3.8.1
	 */
	public static PerspectiveLocator forId(String perspectiveId) {
		return new PerspectiveLocator(perspectiveId);
	}
	
	
	/**
	 * Create an instance that locates the given perspective by id.
	 * @param perspectiveId the id of the perspective to locate
	 */
	public PerspectiveLocator(String perspectiveId) {
		this(PerspectiveFinder.findByIdInRegistry(perspectiveId));
	}

	private PerspectiveLocator(IPerspectiveDescriptor descriptor) {
		this.descriptor = descriptor;	
	}
	
	
	public String getPerspectiveId() {
		return descriptor.getId();
	}
	
	/**
	 * Get the associated descriptor.
	 * @since 3.8.1
	 */
	public IPerspectiveDescriptor getDescriptor() {
		return descriptor;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Condition factories
	//
	//////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Create a condition that tests whether the given perspective is active.
	 * @since 3.8.1
	 */
	public IConditionHandler isActive() {
		return PerspectiveActiveConditionHandler.forPerspective(this);
	}

	/**
	 * Create a condition that tests whether the given perspective is active.
	 * @param expected - whether the perspective should be active
	 */
	public ICondition isActive(boolean expected) {
		PerspectiveCondition active = PerspectiveCondition.isActive(this);
		if (!expected)
			return active.not();
		return active;
	}

	/**
	 * Create a condition that tests whether the given perspective is closed.
	 * @since 3.8.1
	 */
	public IConditionHandler isClosed() {
		return PerspectiveClosedConditionHandler.forPerspective(this);
	}



	
}
