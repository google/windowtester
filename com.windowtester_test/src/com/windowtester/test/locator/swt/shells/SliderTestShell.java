/*******************************************************************************
 *  Copyright (c) 2012 Phillip Jensen, Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Phillip Jensen - initial API and implementation
 *  Frederic Gurr - alignment to WindowTester code standards
 *******************************************************************************/
package com.windowtester.test.locator.swt.shells;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

public class SliderTestShell {

	protected Shell shell;
	
	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		createContents();
		shell.setSize (250, 250);
		shell.open ();
		shell.layout();
		shell.pack();
	}	
	
	private void createContents() {
		Slider slider = new Slider(shell, SWT.HORIZONTAL);
		slider.setBounds(0, 0, 120, 15);
		slider.setMinimum(1);
		slider.setMaximum(50);
		slider.setSelection(30);
		slider.pack();
	}

	public Shell getShell() {
		return shell;
	}
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
	 		SliderTestShell window = new SliderTestShell();
			window.open();
			
	 		//new EventRecordingWatcher(window.getShell()).watch();
	 			
 			final Display display = Display.getDefault();
	 		while (!window.getShell().isDisposed()) {
	 			if (!display.readAndDispatch())
					display.sleep();
	 		}
	 	} catch (Exception e) {
	 		e.printStackTrace();
	 	}
	}
}