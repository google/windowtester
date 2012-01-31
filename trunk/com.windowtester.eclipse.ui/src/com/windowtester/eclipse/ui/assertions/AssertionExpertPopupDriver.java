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
package com.windowtester.eclipse.ui.assertions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.recorder.event.user.IWidgetDescription;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * A driver for development/testing purposes.
 */
class AssertionExpertPopupDriver {

	
	public static void main(String[] args) {
		AssertionExpertPopup popup = (AssertionExpertPopup)DisplayExec.sync(new RunnableWithResult(){
			public Object runWithResult() {
				AssertionExpertPopup assertionExpert = new AssertionExpertPopup(new Shell(SWT.ON_TOP), createWidgetDescription());
				assertionExpert.open();
				//Cursor cursor = new Cursor(Display.getDefault(), SWT.CURSOR_HELP);
				//inspectorPopup.getShell().setCursor(cursor);
				return assertionExpert;				
			}


		});
		final Display display = Display.getDefault();
		while (popup.getShell() != null && !popup.getShell().isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}			
		
	}
	
	private static IWidgetDescription createWidgetDescription() {
		return new IWidgetDescription() {

			public ILocator getLocator() {
				//return new ButtonLocator("Ok", new ViewLocator("blah"));
				//return new ButtonLocator("Ok", new ViewLocator("blah"));
				return new ButtonLocator("innagoddadavida", new SWTWidgetLocator(Group.class, new ViewLocator("blah")));
				//return new ButtonLocator("Ok");
				
			}

			public PropertySet getProperties() {
				return new PropertySet.TestStub().
					withMapping(PropertyMapping.ENABLED.withValue(true)).
					withMapping(PropertyMapping.SELECTED.withValue(false)).
					withMapping(PropertyMapping.TEXT.withValue("innagoddadavJKkkjasdkljaklsdjaksjdk"));
			}
			
			public Point getHoverPoint() {
				return Display.getDefault().getCursorLocation();
			}
			public boolean isSame(IWidgetDescription event) {
				return false;
			}
			public String getDescriptionLabel() {
				return null;
			}
		};
	}


}
