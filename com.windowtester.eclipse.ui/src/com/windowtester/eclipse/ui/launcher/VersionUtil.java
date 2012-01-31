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

import org.osgi.framework.Version;

/**
 * A Version Helper.  Note: this is just a wrapper for access to
 * org.eclipse.pde.internal.core.util.VersionUtil.
 */
public class VersionUtil {

	/**
	 * @since 3.8.1
	 */
	public static int compareMacroMinorMicro(Version v1, Version v2) {
		
		/* $codepro.preprocessor.if version >= 3.4 $
		return org.eclipse.pde.internal.core.util.VersionUtil.compareMacroMinorMicro(v1, v2);
		$codepro.preprocessor.endif $ */

		//backport of 3.4
		/* $codepro.preprocessor.if version < 3.4 $ */		
		int result = v1.getMajor() - v2.getMajor();
		if (result != 0)
			return result;

		result = v1.getMinor() - v2.getMinor();
		if (result != 0)
			return result;

		result = v1.getMicro() - v2.getMicro();
		return result;
		/* $codepro.preprocessor.endif $ */
	
	}

	
	
}
