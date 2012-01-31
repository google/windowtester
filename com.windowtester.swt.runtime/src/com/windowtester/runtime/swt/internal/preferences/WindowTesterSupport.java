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
package com.windowtester.runtime.swt.internal.preferences;

import com.windowtester.internal.product.Products;
import com.windowtester.internal.runtime.ProductInfo;

/**
 * Singleton providing WindowTester specific support information
 */
public final class WindowTesterSupport extends CommonSupport
{
	private static WindowTesterSupport instance;

	public static WindowTesterSupport getInstance() {
		if (instance == null)
			instance = new WindowTesterSupport();
		return instance;
	}

	private WindowTesterSupport() {
		super(Products.WINDOWTESTER_PRO, ProductInfo.build);
	}

	
}