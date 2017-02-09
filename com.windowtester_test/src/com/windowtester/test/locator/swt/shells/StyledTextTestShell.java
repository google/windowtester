/*******************************************************************************
 *  Copyright (c) 2013 Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Frederic Gurr - initial API and implementation
 *******************************************************************************/
package com.windowtester.test.locator.swt.shells;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StyledTextTestShell {

	protected Shell shell;
	
	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		createContents();
		shell.setSize (250, 250);
		shell.setLayout(new FillLayout());
		shell.setText("StyledTextTestShell");
		shell.open ();
		shell.layout();
//		shell.pack();
	}	
	
	private void createContents() {
		StyledText styledText = new StyledText(shell, SWT.BORDER);
		styledText.setText( "First line\r\n"+
							"Second line\r\n"+
							"Third line\r\n"+
							"Fourth line\r\n"+
							"Fifth line\r\n");
		StyleRange style1 = new StyleRange();
		style1.start = 0;
		style1.length = 10;
		style1.fontStyle = SWT.BOLD;
		styledText.setStyleRange(style1);
		StyleRange style2 = new StyleRange();
		style2.start = 11;
		style2.length = 12;
		style2.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_RED);
		styledText.setStyleRange(style2);
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
			StyledTextTestShell window = new StyledTextTestShell();
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