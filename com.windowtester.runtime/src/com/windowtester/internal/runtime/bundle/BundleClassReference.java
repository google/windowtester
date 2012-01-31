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

import com.windowtester.internal.runtime.ClassReference;

/**
 * A class reference that is resolved to a bundle.
 */
public class BundleClassReference extends ClassReference implements IBundleReference, Serializable {

	private static final long serialVersionUID = -8273274737082721065L;
	
	private BundleReference bundle;
	
	private BundleClassReference(Class<?> cls) {
		super(cls);
	}

	private static BundleClassReference forClassName(Class<?> cls) {
		return new BundleClassReference(cls);
	}
	
	public static BundleClassReference forBundleClass(Class<?> cls) {
		return forClassName(cls).inBundle(BundleResolver.bundleNameForClass(cls));
	}
	
	BundleClassReference inBundle(String bundleName) {
		bundle = BundleReference.forName(bundleName);
		return this;
	}

	/* (non-Javadoc)
	 * @see test.IBundleReference#getBundleSymbolicName()
	 */
	public String getBundleSymbolicName() {
		if (bundle == null)
			return null;
		return bundle.getBundleSymbolicName();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + " - bundle = " + getBundleSymbolicName();
	}
	
}
