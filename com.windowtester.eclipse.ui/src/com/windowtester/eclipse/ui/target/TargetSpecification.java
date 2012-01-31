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
package com.windowtester.eclipse.ui.target;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.eclipse.ui.target.BundleHelper.BundleSet;

/**
 * A target spec.
 */
public class TargetSpecification {

	public static TargetSpecification RECORDING = new TargetSpecification().withBundles(RequiredPlugins.RECORDING);
	
	public static TargetSpecification EXECUTION = new TargetSpecification().withBundles(RequiredPlugins.RUNTIME);

	
	private final List<String> bundleIds = new ArrayList<String>();
	
	TargetSpecification withBundles(String[] bundleId) {
		for (int i = 0; i < bundleId.length; i++) {
			bundleIds.add(bundleId[i]);
		}
		return this;
	}
	
	
	public BundleSet getBundles() {
		return BundleHelper.getBundlesWithDependencies(getBundleIds());
	}

	protected String[] getBundleIds() {
		return bundleIds.toArray(new String[]{});
	}
	
	
	
}
