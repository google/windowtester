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

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

public class SectionFinder {

	
	public static Section findParentSection(Widget w) {
		SWTHierarchyHelper helper = new SWTHierarchyHelper();
		while (w != null) {
			w = helper.getParent(w);
			if (w instanceof Section)
				return (Section) w;
		}
		return null;
	}
	
	public static String getText(final Section section) {
		return DisplayReference.getDefault().execute(new Callable<String>(){
			public String call() throws Exception {
				return section.getText();
			}
		});		
	}


	public static String getParentSectionText(Widget w) {
		Section section = findParentSection(w);
		if (section == null)
			return null;
		return getText(section);
	}
	
	
}
