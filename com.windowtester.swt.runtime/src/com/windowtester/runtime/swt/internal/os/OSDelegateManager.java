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
package com.windowtester.runtime.swt.internal.os;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.internal.debug.Logger;
import com.windowtester.runtime.swt.internal.RuntimePlugin;

class OSDelegateManager {

	private static final IExtension[] NO_EXTENSIONS = new IExtension[] {};
	private static final String CLASS_TAG = "class";
	private static final String OS_DELEGATE_EXTENSION_POINT = "osDelegate";
	
	private static final OSDelegateManager INSTANCE = new OSDelegateManager();
	
	
	private IOSDelegate[] delegates;
	
	public static OSDelegateManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Answer the extensions for the <code>osDelegate</code> extension point
	 * 
	 * @return the extensions (not <code>null</code>, contains no <code>null</code>s)
	 */
	public IExtension[] getExtensions() {
		if (!Platform.isRunning())
			return NO_EXTENSIONS;
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(RuntimePlugin.PLUGIN_ID, OS_DELEGATE_EXTENSION_POINT);
		if (extensionPoint == null)
			return NO_EXTENSIONS;
		return extensionPoint.getExtensions();
	}
	
	
	public IOSDelegate[] getContributedDelegates() {
		if (delegates == null)
			delegates = doGetContributors();
		return delegates;
	}

	private IOSDelegate[] doGetContributors() {
		IExtension[] allExtensions = getExtensions();
		List<IOSDelegate> delegates = new ArrayList<IOSDelegate>();
		for (int i = 0; i < allExtensions.length; i++) {
			IExtension extension = allExtensions[i];
			IConfigurationElement[] allElements = extension.getConfigurationElements();
			for (int j = 0; j < allElements.length; j++) {
				IConfigurationElement element = allElements[j];
				if (element.getName().equals(OS_DELEGATE_EXTENSION_POINT)) {
					try {
						String clsName = element.getAttribute(CLASS_TAG);
						String namespace = extension.getNamespaceIdentifier();
						Bundle bundle = Platform.getBundle(namespace);
						Class<?> cls = bundle.loadClass(clsName);
						IOSDelegate action = (IOSDelegate)cls.newInstance();
						delegates.add(action);
					} catch (Exception e) {
						Logger.log("An error occured configuring collecting OS delegates", e);
					}
				}
			}
		}
		return delegates.toArray(new IOSDelegate[]{});
	}
	
}
