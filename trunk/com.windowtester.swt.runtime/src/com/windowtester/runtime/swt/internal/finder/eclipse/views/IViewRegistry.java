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
 * A store for all registered views.
 */
public interface IViewRegistry {

	/**
	 * Add this view to the registery.
	 * @param view the view to add
	 */
	void register(IViewHandle view);
	

	/** 
	 * Get all the registered views in the registry.
	 * @return the registered views
	 */
	IViewHandle[] get();
	
}
