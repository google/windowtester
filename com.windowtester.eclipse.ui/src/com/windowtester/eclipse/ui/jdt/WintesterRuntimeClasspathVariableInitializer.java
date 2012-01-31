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
package com.windowtester.eclipse.ui.jdt;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.ui.util.Logger;

public class WintesterRuntimeClasspathVariableInitializer extends ClasspathVariableInitializer {
	
	private static String WINTESTER_SWT_RUNTIME_PLUGIN_ID = "com.windowtester.swt.runtime";
	private static String WINTESTER_COMMON_RUNTIME_PLUGIN_ID = "com.windowtester.runtime";
	private static String WINTESTER_SWT_RUNTIME_JAR = "wt-runtime.jar";
	private static String WINTESTER_COMMON_RUNTIME_JAR = "commonRuntime.jar";
		
	public void initialize(String variable) {
		
		try {
			initClasspathVariable(UiPlugin.WINDOWTESTER_RUNTIME_VAR, WINTESTER_COMMON_RUNTIME_PLUGIN_ID, WINTESTER_COMMON_RUNTIME_JAR);
			initClasspathVariable(UiPlugin.WINDOWTESTER_SWT_RUNTIME_VAR, WINTESTER_SWT_RUNTIME_PLUGIN_ID, WINTESTER_SWT_RUNTIME_JAR);
		} catch (Throwable e) {
			Logger.log(e);
		}
	}
	
	private void initClasspathVariable(String name, String bundleId, String jar) throws Exception{
		Bundle bundle = Platform.getBundle(bundleId);
		if(bundle==null){
			Logger.log("Cannot find bundle: "+bundleId+". May be it is not a deployed plugin.");
			return;
		}
		URL	pluginURL = FileLocator.resolve(bundle.getEntry("/"+jar));
		String pluginInstallDir = pluginURL.getPath().trim();
		JavaCore.setClasspathVariable(name, new Path(pluginInstallDir), null);
	}

}
