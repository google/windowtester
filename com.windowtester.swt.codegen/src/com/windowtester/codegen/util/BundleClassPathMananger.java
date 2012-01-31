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
package com.windowtester.codegen.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.windowtester.codegen.generator.CodegenContributionManager;
import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.bundle.IBundleReference;
import com.windowtester.internal.runtime.locator.LocatorIterator;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.locator.ILocator;


/**
 * Queries locators for embedded bundle info.
 */
public class BundleClassPathMananger {

	public static void addPluginDependencies(List events, IBuildPathUpdater updater) {
		//first be sure the list is adapted so our casts are safe
		events = CodegenContributionManager.getSemanticEvents(events);
		for (Iterator iterator = events.iterator(); iterator.hasNext();) {
			addDependenciesForEvent(iterator.next(), updater);
		}
	}

	private static void addDependenciesForEvent(Object e, IBuildPathUpdater updater) {
		try {
			if (e instanceof IUISemanticEvent) {
				IUISemanticEvent semantic = (IUISemanticEvent)e;
				IWidgetIdentifier identifier = semantic.getHierarchyInfo();
				if (!(identifier instanceof ILocator))
					return;
				LocatorIterator iterator = LocatorIterator.forLocator((ILocator)identifier);
				for( ; iterator.hasNext(); ) {
					IBundleReference ref = adaptToBundleReference(iterator.next());
					addBundleReference(updater, ref);
				}	
			}
		} catch(Throwable ex) {
			Logger.log(ex);
		}
		
	}

	private static void addBundleReference(IBuildPathUpdater updater, IBundleReference ref) throws CoreException {
		if (ref == null)
			return;
		String bundleName = ref.getBundleSymbolicName();
		if (bundleName == null)
			return;
		updater.addPluginDependency(bundleName);
	}

	private static IBundleReference adaptToBundleReference(ILocator locator) {
		if (!(locator instanceof IAdaptable))
			return null;
		return (IBundleReference) ((IAdaptable)locator).getAdapter(IBundleReference.class);
	}

}
