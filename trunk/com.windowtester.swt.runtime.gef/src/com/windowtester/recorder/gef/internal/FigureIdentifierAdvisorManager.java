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
package com.windowtester.recorder.gef.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.IFigure;
import org.osgi.framework.Bundle;

import com.windowtester.internal.debug.Logger;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.RuntimePlugin;

/**
 * A manager for overriding identifiers.
 */
public class FigureIdentifierAdvisorManager {
	
	private static final String CLASS_TAG = "class";
	private static final String ADVISOR_EXTENSION_POINT = "figureIdentifierAdvisor";
	
	private static final FigureIdentifierAdvisorManager INSTANCE = new FigureIdentifierAdvisorManager();
	
	
	private IFigureIdentifierAdvisor[] _advisors;
	
	public static FigureIdentifierAdvisorManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Answer the extensions for the <code>widgetIdentifierDelegate</code> extension point
	 * 
	 * @return the extensions (not <code>null</code>, contains no <code>null</code>s)
	 */
	public IExtension[] getExtensions() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(RuntimePlugin.PLUGIN_ID, ADVISOR_EXTENSION_POINT);
		
		if (extensionPoint == null)
			return new IExtension[] {};
		return extensionPoint.getExtensions();
	}
	
	
	public IFigureIdentifierAdvisor[] getAdvisors() {
		if (_advisors == null)
			_advisors = doGetAdvisors();
		return _advisors;
	}

	private IFigureIdentifierAdvisor[] doGetAdvisors() {
		IExtension[] allExtensions = getExtensions();
		List actions = new ArrayList();
		for (int i = 0; i < allExtensions.length; i++) {
			IExtension extension = allExtensions[i];
			IConfigurationElement[] allElements = extension.getConfigurationElements();
			for (int j = 0; j < allElements.length; j++) {
				IConfigurationElement element = allElements[j];
				if (element.getName().equals(ADVISOR_EXTENSION_POINT)) {
					try {
						String clsName = element.getAttribute(CLASS_TAG);
						//pre 3.2: String namespace = extension.getNamespace();
						String namespace = extension.getContributor().getName();
						Bundle bundle = Platform.getBundle(namespace);
						Class cls = bundle.loadClass(clsName);
						IFigureIdentifierAdvisor action = (IFigureIdentifierAdvisor)cls.newInstance();
						actions.add(action);
					} catch (Exception e) {
						Logger.log("An error occured configured contributed controller actions", e);
					}
				}
			}
		}
		return (IFigureIdentifierAdvisor[])actions.toArray(new IFigureIdentifierAdvisor[]{});
	}


	public static ILocator identify(IFigure figure) {
		IFigureIdentifierAdvisor[] id = getInstance().getAdvisors();
		ILocator locator = null;
		for (int i = 0; locator == null && i < id.length; i++) {
			locator = id[i].identify(figure);
		}
		return locator;
	}
			
}
