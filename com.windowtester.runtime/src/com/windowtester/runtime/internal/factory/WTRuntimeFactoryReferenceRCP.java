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
package com.windowtester.runtime.internal.factory;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.ProductInfo;
import com.windowtester.internal.runtime.RuntimePlugin;

/**
 * A facade for an instance of {@link WTRuntimeFactory} when running within the Eclipse
 * infrastructure such as when testing an RCP application.
 */
class WTRuntimeFactoryReferenceRCP extends WTRuntimeFactoryReference
{
	protected static final String RUNTIME_FACTORY_TAG = "runtimeFactory";

	private final IConfigurationElement element;

	/**
	 * Find the known widget factories as defined by the runtimeFactory extension point.
	 * 
	 * @return an array of references (not <code>null</code>, contains no
	 *         <code>null</code>s)
	 */
	static WTRuntimeFactoryReference[] createFactoryReferences() {
		String os = Platform.getOS();
		String ws = Platform.getWS();
		String arch = Platform.getOSArch();
		String debugInfo = "WindowTester: " + ProductInfo.build + " - " + os + "," + ws + "," + arch;
		Logger.log(debugInfo);
		WTRuntimeManager.setPlatformDebugInfo(debugInfo);
		
		Collection<WTRuntimeFactoryReference> result = new ArrayList<WTRuntimeFactoryReference>();
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(RuntimePlugin.PLUGIN_ID,
			RUNTIME_FACTORY_TAG);
		IExtension[] allExtensions = extensionPoint.getExtensions();
		for (IExtension extension : allExtensions) {
			IConfigurationElement[] allElements = extension.getConfigurationElements();
			for (IConfigurationElement element : allElements) {
				if (!element.getName().equals(FACTORY_TAG))
					continue;
				String className = element.getAttribute(CLASS_TAG);
				if (className == null) {
					Logger.log("Missing class attribute for " + RuntimePlugin.PLUGIN_ID + "." + RUNTIME_FACTORY_TAG
						+ " extension in " + element.getContributor().getName());
					continue;
				}
				if (!isFactoryFor(os, element.getAttribute(OS_TAG)))
					continue;
				if (!isFactoryFor(ws, element.getAttribute(WS_TAG)))
					continue;
				if (!isFactoryFor(arch, element.getAttribute(ARCH_TAG)))
					continue;
				result.add(new WTRuntimeFactoryReferenceRCP(element));
			}
		}
		return result.toArray(new WTRuntimeFactoryReference[result.size()]);
	}

	private WTRuntimeFactoryReferenceRCP(IConfigurationElement element) {
		this.element = element;
	}

	/**
	 * Instantiate the factory
	 * 
	 * @return the factory (not <code>null</code>)
	 * @throws Exception if the factory could not be instantiated
	 */
	WTRuntimeFactory createFactory() throws Exception {
		return (WTRuntimeFactory) element.createExecutableExtension(CLASS_TAG);
	}

	void logFactoryCreationException(Exception exception) {
		Logger.log("Failed to instantiate runtime factory defined in " + element.getContributor().getName(), exception);
	}
}
