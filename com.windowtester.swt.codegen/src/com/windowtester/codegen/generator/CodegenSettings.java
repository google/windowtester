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

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;
import com.windowtester.runtime.swt.internal.preferences.CodegenPreferences;

/**
 * Codgen settings.
 */
public class CodegenSettings {

	
	private final boolean usingStatics;

	private SetupHandlerSet handlers = new SetupHandlerSet();

	private ExecutionProfile profile = ExecutionProfile.forUnknown();
	
	private CodegenSettings(boolean usingStatics) {
		this.usingStatics = usingStatics;
	}
	
	public CodegenSettings withHandlers(SetupHandlerSet handlers) {
		this.handlers = handlers;
		return this;
	}
	
	public boolean usingStatics() {
		return usingStatics;
	}
	
	public SetupHandlerSet handlers() {
		return handlers;
	}

	public ExecutionProfile profile() {
		return profile;
	}

	public static CodegenSettings forStatics(boolean usingStatics) {
		return new CodegenSettings(usingStatics);
	}
	
	public static CodegenSettings forPreferences() {
		return forStatics(CodegenPreferences.stored().usingStatics());
	}

	public CodegenSettings forProfile(ExecutionProfile profile) {
		this.profile = profile;
		return this;
	}

	//a null object
	public static CodegenSettings forUnknown() {
		return forStatics(false);
	}
	

}
