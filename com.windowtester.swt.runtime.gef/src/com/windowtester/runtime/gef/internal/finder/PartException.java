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
package com.windowtester.runtime.gef.internal.finder;

import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

/**
 * Helper utility for generating exceptions.
 *
 */
public class PartException {

	public static void multiple(IGEFEditPartMatcher partMatcher, IEditorLocator editor) throws MultiplePartsFoundException{
		String msg = "Multiple parts found for matcher (" + partMatcher + ")";
		msg = appendEditorDetails(editor, msg);
		throw new MultiplePartsFoundException(msg);
	}

	private static String appendEditorDetails(IEditorLocator editor, String msg) {
		if (editor != null)
			msg +=  " in editor: " + editor;
		return msg;
	}

	public static void notFound(IGEFEditPartMatcher partMatcher, IEditorLocator editor) throws PartNotFoundException {
		String msg = "Part not found for matcher (" + partMatcher + ")";
		msg = appendEditorDetails(editor, msg);
		throw new PartNotFoundException(msg);
	}

	
}
