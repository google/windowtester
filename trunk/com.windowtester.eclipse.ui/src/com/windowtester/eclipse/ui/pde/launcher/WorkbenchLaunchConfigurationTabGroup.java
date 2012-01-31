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
package com.windowtester.eclipse.ui.pde.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.pde.ui.launcher.ConfigurationTab;
import org.eclipse.pde.ui.launcher.MainTab;
import org.eclipse.pde.ui.launcher.PluginsTab;
import org.eclipse.pde.ui.launcher.TracingTab;

import com.windowtester.eclipse.ui.launcher.BundleInjection;

public class WorkbenchLaunchConfigurationTabGroup extends
/* $codepro.preprocessor.if version >= 3.3 $ */
org.eclipse.pde.ui.launcher.EclipseLauncherTabGroup
/*	$codepro.preprocessor.endif $ */
/* $codepro.preprocessor.if version <= 3.2 $ 
org.eclipse.pde.internal.ui.launcher.EclipseApplicationLauncherTabGroup
$codepro.preprocessor.endif $ */
{	
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {		
		List items = new ArrayList();
		items.addAll(basicTabs());
		if (!BundleInjection.isDisabled())
			items.add(new RecorderTab());
		setTabs((ILaunchConfigurationTab[]) items.toArray(new ILaunchConfigurationTab[] {}));
	}

	private Collection basicTabs() {
		return Arrays.asList(new ILaunchConfigurationTab[] {new MainTab(), new JavaArgumentsTab(), new PluginsTab(), new ConfigurationTab(), new TracingTab(), new EnvironmentTab(), new CommonTab()});
	}
}
