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

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.windowtester.codegen.util.IBuildPathUpdater;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.locator.ILocator;

public interface ICodegenAdvisor {

	
	/**
	 * A call back for advisors to communicate back to the codegen engine.
	 */
	public static class Advice {
		
		private boolean override;
		/**
		 * If this method is called, the standard selection handling will be considered overriden (and skipped).
		 */
		public void override() {
			override = true;
		}
		
		public boolean isOverriden() {
			return override;
		}
	}
	
	
	String toJavaString(ILocator locator);


	/**
	 * Handle this selection event.
	 */
	void handleSelection(ISemanticEvent event, PluggableCodeGenerator generator, Advice advice);


	/**
	 * Check these events and if appropriate add any required plugins to
	 * the build path.
	 * @throws CoreException 
	 */
	void addPluginDependencies(List events, IBuildPathUpdater updater) throws Exception;
	
	
	
	
}
