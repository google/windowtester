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
package com.windowtester.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.swt.event.recorder.DebugHandler;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;

/**
 * A manager for contributed control actions.
 */
public class ControllerManager {
	
	private static final String CLASS_TAG = "class";
	private static final String CONTROLLER_ACTION_EXTENSION_POINT = "controller_action";
	
	/**
	 * Answer the extensions for the startup extension point
	 * 
	 * @return the extensions (not <code>null</code>, contains no <code>null</code>s)
	 */
	public static IExtension[] getExtensions() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EventRecorderPlugin.PLUGIN_ID, CONTROLLER_ACTION_EXTENSION_POINT);
		
		if (extensionPoint == null)
			return new IExtension[] {};
		return extensionPoint.getExtensions();
	}
	
	
	/**
	 * @return the contributed controller actions
	 */
	public static IControllerAction[] getContributedActions() {
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
						
						//for pre 3.2: String namespace = extension.getNamespace();
						String namespace = extension.getContributor().getName();
						
						Bundle bundle = Platform.getBundle(namespace);
						Class cls = bundle.loadClass(clsName);
						IControllerAction action = (IControllerAction)cls.newInstance();
						actions.add(action);
					} catch (Exception e) {
						DebugHandler.log("An error occured configured contributed controller actions", e);
					}
				}
			}
		}
		return (IControllerAction[])actions.toArray(new IControllerAction[]{});
	}
			
}
