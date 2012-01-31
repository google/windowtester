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
package com.windowtester.codegen.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.codegen.CodeGenPlugin;
import com.windowtester.codegen.util.IBuildPathUpdater;
import com.windowtester.internal.debug.Logger;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.locator.ILocator;

public class CodegenContributionManager {

	private static final String CLASS_TAG = "class";
	private static final String CODEGEN_CONTRIB_EXTENSION_POINT = "codegenParticipant";
	
	private static final CodegenContributionManager INSTANCE = new CodegenContributionManager();
	
	
	private ICodegenAdvisor[] _delegates;
	
	public static CodegenContributionManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Answer the extensions for the <code>widgetIdentifierDelegate</code> extension point
	 * 
	 * @return the extensions (not <code>null</code>, contains no <code>null</code>s)
	 */
	public IExtension[] getExtensions() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(CodeGenPlugin.PLUGIN_ID, CODEGEN_CONTRIB_EXTENSION_POINT);
		
		if (extensionPoint == null)
			return new IExtension[] {};
		return extensionPoint.getExtensions();
	}
	
	
	public ICodegenAdvisor[] getContributedGenerators() {
		if (_delegates == null)
			_delegates = doGetContributors();
		return _delegates;
	}

	private ICodegenAdvisor[] doGetContributors() {
		IExtension[] allExtensions = getExtensions();
		List actions = new ArrayList();
		for (int i = 0; i < allExtensions.length; i++) {
			IExtension extension = allExtensions[i];
			IConfigurationElement[] allElements = extension.getConfigurationElements();
			for (int j = 0; j < allElements.length; j++) {
				IConfigurationElement element = allElements[j];
				if (element.getName().equals(CODEGEN_CONTRIB_EXTENSION_POINT)) {
					try {
						String clsName = element.getAttribute(CLASS_TAG);
						//pre 3.2: String namespace = extension.getNamespace();
						String namespace = extension.getContributor().getName();
						Bundle bundle = Platform.getBundle(namespace);
						Class cls = bundle.loadClass(clsName);
						ICodegenAdvisor action = (ICodegenAdvisor)cls.newInstance();
						actions.add(action);
					} catch (Exception e) {
						Logger.log("An error occured configuting contributed codegenerators", e);
					}
				}
			}
		}
		return (ICodegenAdvisor[])actions.toArray(new ICodegenAdvisor[]{});
	}
	
	public static String toJavaString(ILocator locator) {
		ICodegenAdvisor[] generator = getInstance().getContributedGenerators();
		for (int i = 0; i < generator.length; i++) {
			ICodegenAdvisor participant = generator[i];
			String string = participant.toJavaString(locator);
			if (string != null)
				return string;
		}
		return null;
	}

	public static void addPluginDependencies(List events, IBuildPathUpdater updater) throws Exception {
		ICodegenAdvisor[] generator = getInstance().getContributedGenerators();
		for (int i = 0; i < generator.length; i++) {
			ICodegenAdvisor participant = generator[i];
			try {
				participant.addPluginDependencies(getSemanticEvents(events), updater);
			} catch (Throwable e) {
				Logger.log(e);
			}
		}
	}
	
	
	//adapt to play nice with contributor expectations
	public static List getSemanticEvents(List events) {
		List semantics = new ArrayList();
		for (Iterator iterator = events.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			if (o instanceof ISemanticEvent)
				semantics.add(o);
			else if (o instanceof IAdaptable) {
				Object adapted = ((IAdaptable)o).getAdapter(ISemanticEvent.class);
				if (adapted != null)
					semantics.add(adapted);
			}
		}
		return semantics;
		
	}
	
}