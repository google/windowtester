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
package com.windowtester.eclipse.ui.launcher;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;

public class RecorderSwingLaunchConfDelegate extends
		RecorderSWTLaunchConfDelegate {


	protected ExecutionProfile createExecutionProfile(ILaunch launch) throws CoreException {
		ExecutionProfile profile =  super.createExecutionProfile(launch);
		profile.setExecType(ExecutionProfile.SWING_EXEC_TYPE);
		return profile;
	}

	protected void setVMArgsSection(ILaunchConfigurationWorkingCopy wc) throws CoreException {
		super.setVMArgsSection(wc);
		String vmargs = getVmAttributes(wc);
		// add vm arg to indicate swing recording
		vmargs += " -D"+EventRecorderPlugin.SWING_LAUNCH_PROP+"=yes";
		setVmAttributes(wc, vmargs);
	}
	
	/* $codepro.preprocessor.if version > 3.1.0  $ */
	// Override to pass on the user specified program arguments
	@SuppressWarnings("unchecked")
	public String[] getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		String[] pargs = super.getProgramArguments(configuration);
		ArrayList list = new ArrayList();
		
		for (int i = 0; i < pargs.length; i++) {
			// to remove all the unnecessary arguments
			if (pargs[i].equals("-pdelaunch"))
				break;
			// in 3.1  there is -showsplash 600
			// replace by -noSpash
			if (pargs[i].equals("-showsplash")){
				list.add("-noSplash");
				i++;
			}
			else list.add(pargs[i]);
		}
		
		String args = wcSwt.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "");
		String[] progArgs = null;
		if(!"".equals(args)) {	
			progArgs = args.split("[ \t]");
		
			// add the user specified arguments
			for (int i = 0; i < progArgs.length; i++) {
				list.add(progArgs[i]);
			}
		}
		return (String[])list.toArray(new String[list.size()]);
	}
	
	/* $codepro.preprocessor.endif $ */

}
