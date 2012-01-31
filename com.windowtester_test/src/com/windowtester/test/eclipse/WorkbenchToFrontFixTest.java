package com.windowtester.test.eclipse;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.selector.UIDriver;

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
public class WorkbenchToFrontFixTest extends UITestCaseSWT {

	private class ActiveShellCondition implements ICondition {
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.condition.ICondition#test()
		 */
		public boolean test() {
			return ShellFinder.getActiveShell(Display.getDefault()) != null;
		}	
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "Active shell condition";
		}
		
		public ICondition not() {
			return new ICondition() {
				/* (non-Javadoc)
				 * @see com.windowtester.runtime.condition.ICondition#test()
				 */
				public boolean test() {
					return !ActiveShellCondition.this.test();
				}
				/* (non-Javadoc)
				 * @see java.lang.Object#toString()
				 */
				public String toString() {
					return "No active shell condition";
				}
			};
		}
	}
	
	
	public void testBringToFront() throws Exception {
		for (int i=0; i < 5; ++i)
			doTest();
	}


	private void doTest() throws WidgetSearchException {
		sendToBack();
		assertInBack();
		bringToFront();
		assertInFront();
	}


	private void assertInFront() {
		getUI().assertThat(new ActiveShellCondition());
	}

	private void bringToFront() {
		ShellFinder.bringRootToFront(Display.getDefault());
	}

	private void assertInBack() {
		getUI().assertThat(new ActiveShellCondition().not());
	}

	private void sendToBack() throws WidgetSearchException {
		UIDriver driver = new UIDriver();
		driver.click(ShellFinder.getActiveShell(Display.getDefault()), -10, -10);
	}
	
	
}
