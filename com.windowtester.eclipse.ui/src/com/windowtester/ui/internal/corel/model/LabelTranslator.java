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
package com.windowtester.ui.internal.corel.model;

import com.windowtester.internal.runtime.DefaultCodeGenerator;
import com.windowtester.internal.runtime.ICodegenParticipant;

public class LabelTranslator {

	
	public static String fromCodeString(ICodegenParticipant cp) {
		DefaultCodeGenerator cg = new DefaultCodeGenerator();
		cp.describeTo(cg);
		String codeString = cg.toCodeString();
		
		return fromCodeString(codeString);
	}

	
	private static String fromCodeString(String codeString) {
		codeString = codeString.replaceAll("new ", "");
		codeString = codeString.replaceAll("\\(\"", "'");
		codeString = codeString.replaceAll("\"\\)", "'");
		codeString = codeString.replaceAll("Locator", ": ");
		codeString = codeString.replaceAll("\\.inSection", " in Section ");
		codeString = codeString.replaceAll("\\.inView", " in View ");
		return codeString;
	}

}
