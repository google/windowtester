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
package com.windowtester.finder.matchers.swt;


/**
 * Matcher that matches widgets that are components of a shell identified by title.
 * 
 * @author Phil Quitslund
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 */
public class ShellComponentMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.ShellComponentMatcher {

	public ShellComponentMatcher(String shellTitle, boolean isModal) {
		super(shellTitle, isModal);
	}

}
