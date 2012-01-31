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
package com.windowtester.runtime.swt.internal.locator.forms;

import com.windowtester.runtime.swt.locator.SectionLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

public class SectionFormTextLocator extends FormTextLocator {

	private static final long serialVersionUID = -1638420906781576845L;

	public SectionFormTextLocator(String sectionTitle) {
		super(new SectionLocator(sectionTitle));
	}

	public SectionFormTextLocator(String sectionTitle, ViewLocator viewLocator) {
		super(new SectionLocator(sectionTitle), viewLocator);
	}
		
}
