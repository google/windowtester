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
package com.windowtester.internal.runtime.bundle;

import java.io.Serializable;

/**
 * Bundle info holder.
 */
public class BundleReference implements IBundleReference, Serializable {

	private static final long serialVersionUID = -2722815801109679990L;

	public static BundleReference forName(String bundleName) {
		return new BundleReference(bundleName);
	}
	
	private final String bundleName;

	private BundleReference(String bundleName) {
		this.bundleName = bundleName;
	}
	
	/* (non-Javadoc)
	 * @see test.IBundleReference#getBundleSymbolicName()
	 */
	public String getBundleSymbolicName() {
		return bundleName;
	}

}
