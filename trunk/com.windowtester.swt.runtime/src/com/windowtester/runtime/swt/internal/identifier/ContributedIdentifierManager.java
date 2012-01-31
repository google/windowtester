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
package com.windowtester.runtime.swt.internal.identifier;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.internal.debug.Logger;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.RuntimePlugin;

/**
 * A manager for contributed control actions.
 */
public class ContributedIdentifierManager {
	

	private static final IExtension[] NO_EXTENSIONS = new IExtension[]{};
	
	private static final String CLASS_TAG = "class";
	private static final String CONTROLLER_ACTION_EXTENSION_POINT = "widgetIdentifierDelegate";
	
	private static final ContributedIdentifierManager INSTANCE = new ContributedIdentifierManager();
	
	
	private IWidgetIdentifierDelegate[] _delegates;
	
	public static ContributedIdentifierManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Answer the extensions for the <code>widgetIdentifierDelegate</code> extension point
	 * 
	 * @return the extensions (not <code>null</code>, contains no <code>null</code>s)
	 */
	public IExtension[] getExtensions() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if (registry == null)
			return NO_EXTENSIONS;
		IExtensionPoint extensionPoint = registry.getExtensionPoint(RuntimePlugin.PLUGIN_ID, CONTROLLER_ACTION_EXTENSION_POINT);
		if (extensionPoint == null)
			return NO_EXTENSIONS;
		return extensionPoint.getExtensions();
	}
	
	
	public IWidgetIdentifierDelegate[] getContributedIdentifiers() {
		if (_delegates == null)
			_delegates = doGetIdentifiers();
		return _delegates;
	}

	private IWidgetIdentifierDelegate[] doGetIdentifiers() {
		IExtension[] allExtensions = getExtensions();
		List actions = new ArrayList();
		for (int i = 0; i < allExtensions.length; i++) {
			IExtension extension = allExtensions[i];
			IConfigurationElement[] allElements = extension.getConfigurationElements();
			for (int j = 0; j < allElements.length; j++) {
				IConfigurationElement element = allElements[j];
				if (element.getName().equals(CONTROLLER_ACTION_EXTENSION_POINT)) {
					try {
						String clsName = element.getAttribute(CLASS_TAG);
						//pre 3.2: String namespace = extension.getNamespace();
						String namespace = extension.getContributor().getName();
						Bundle bundle = Platform.getBundle(namespace);
						Class cls = bundle.loadClass(clsName);
						IWidgetIdentifierDelegate action = (IWidgetIdentifierDelegate)cls.newInstance();
						actions.add(action);
					} catch (Exception e) {
						Logger.log("An error occured configured contributed controller actions", e);
					}
				}
			}
		}
		return (IWidgetIdentifierDelegate[])actions.toArray(new IWidgetIdentifierDelegate[]{});
	}


	public static ILocator identify(Object widget) {
		IWidgetIdentifierDelegate[] id = getInstance().getContributedIdentifiers();
		ILocator locator = null;
		for (int i = 0; locator == null && i < id.length; i++) {
			locator = id[i].identify(widget);
		}
		return locator;
	}
			
}
