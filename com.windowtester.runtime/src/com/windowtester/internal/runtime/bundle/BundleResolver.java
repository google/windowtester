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

import org.eclipse.osgi.internal.loader.EquinoxClassLoader;
import org.osgi.framework.Bundle;

/**
 * Associates classes with their bundles.
 * 
 */
public class BundleResolver {

	/**
	 * Get the bundle associated with this class.
	 */
	public static Bundle bundleForClass(Class<?> cls) {
		
		ClassLoader classLoader = cls.getClassLoader();
		if (classLoader instanceof EquinoxClassLoader) {
			EquinoxClassLoader loader = (EquinoxClassLoader) classLoader;
			return loader.getBundle();
		}
		/* Does not work anymore in Eclipse 4.4.2 */
//		if (classLoader instanceof BaseClassLoader) {
//			BaseClassLoader loader = (BaseClassLoader)classLoader;
//			BaseData baseData = loader.getClasspathManager().getBaseData();
//			return baseData.getBundle();
//		}
		return null;
	}

	/**
	 * Get the symbolic name for the bundle associated with this class.
	 */
	public static String bundleNameForClass(Class<?> cls) {
		Bundle bundle = bundleForClass(cls);
		if (bundle == null)
			return null;
		return bundle.getSymbolicName();
	}
	
}
