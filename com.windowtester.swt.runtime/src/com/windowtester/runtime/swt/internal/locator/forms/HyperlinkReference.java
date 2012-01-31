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

import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;

public class HyperlinkReference {

	public static HyperlinkLocator toLocator(IHyperlinkReference link) {
		return new HyperlinkLocator(link.getText());
	}

}
