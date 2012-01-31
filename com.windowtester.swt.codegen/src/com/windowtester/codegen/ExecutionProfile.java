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
package com.windowtester.codegen;

import org.eclipse.debug.core.ILaunch;

/**
 * Execution profile provides helpful information about execution environment of launched application
 */
public class ExecutionProfile {
	
	public static final int UNKNOWN_EXEC_TYPE = -1;
	public static final int RCP_EXEC_TYPE = 0;
	public static final int SWT_EXEC_TYPE = 1;
	public static final int SWING_EXEC_TYPE = 2;

	/** SWT main application project name */
	private String projectName;
	/** SWT application main class */
	private String mainSwtClassName;
	/** The execution type of this profile */ 
	private int execType;
	/** The program arguments of RCP or SWT application */
	private String[] programArgs;
	/** A launch instance of this applications */
	private ILaunch launch;
	/** temporary launch configuration name for debug perposes */
	public static final String TMP_APPLICATION_LAUNCH_CONFIG = "tmp_workbench_launch_config";
	
	
	public int getExecType(){
		return execType;
	}
	public ExecutionProfile setExecType(int execType) {
		this.execType = execType;
		return this;
	}
	public void setMainSwtClassName(String mainSwtClassName) {
		this.mainSwtClassName = mainSwtClassName;
	}
	public String getMainSwtClassName(){
		return mainSwtClassName;
	}
	public String[] getProgramArgs(){
		return programArgs;
	}
	public void setProgramArgs(String[] programArgs) {
		this.programArgs = programArgs;
	}
	public ILaunch getLaunch() {
		return launch;
	}
	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public static ExecutionProfile forUnknown() {
		ExecutionProfile executionProfile = new ExecutionProfile();
		executionProfile.setExecType(UNKNOWN_EXEC_TYPE);
		return executionProfile;
	}

}
