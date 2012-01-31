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
package com.windowtester.internal.runtime.resolver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.RuntimePlugin;

/**
 * Manages class resolvers.
 */
public class ClassResolverContributionManager {

	private static final String CLASS_TAG = "class";
	private static final String RESOLVER_CONTRIB_EXTENSION_POINT = "classResolver";
	
	private static final ClassResolverContributionManager INSTANCE = new ClassResolverContributionManager();
	
	
	private IClassResolver[] resolvers;
	
	public static ClassResolverContributionManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Answer the extensions for the <code>classResolver</code> extension point
	 * 
	 * @return the extensions (not <code>null</code>, contains no <code>null</code>s)
	 */
	public IExtension[] getExtensions() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(RuntimePlugin.PLUGIN_ID, RESOLVER_CONTRIB_EXTENSION_POINT);
		
		if (extensionPoint == null)
			return new IExtension[] {};
		return extensionPoint.getExtensions();
	}
	
	
	public IClassResolver[] getContributedResolvers() {
		if (resolvers == null)
			resolvers = doGetContributors();
		return resolvers;
	}

	private IClassResolver[] doGetContributors() {
		IExtension[] allExtensions = getExtensions();
		List actions = new ArrayList();
		for (int i = 0; i < allExtensions.length; i++) {
			IExtension extension = allExtensions[i];
			IConfigurationElement[] allElements = extension.getConfigurationElements();
			for (int j = 0; j < allElements.length; j++) {
				IConfigurationElement element = allElements[j];
				if (element.getName().equals(RESOLVER_CONTRIB_EXTENSION_POINT)) {
					try {
						String clsName = element.getAttribute(CLASS_TAG);
						//pre 3.2: 	String namespace = extension.getNamespace();
						String namespace = extension.getContributor().getName();
						Bundle bundle = Platform.getBundle(namespace);
						Class cls = bundle.loadClass(clsName);
						IClassResolver action = (IClassResolver)cls.newInstance();
						actions.add(action);
					} catch (Exception e) {
						Logger.log("An error occured configuring contributed class resolvers", e);
					}
				}
			}
		}
		return (IClassResolver[])actions.toArray(new IClassResolver[]{});
	}
	
	public static Class resolveClass(String className) {
		IClassResolver[] resolvers = getInstance().getContributedResolvers();
		for (int i = 0; i < resolvers.length; i++) {
			IClassResolver resolver = resolvers[i];
			Class cls = resolver.resolveClass(className);
			if (cls != null)
				return cls;
		}
		return null;
	}
	
}