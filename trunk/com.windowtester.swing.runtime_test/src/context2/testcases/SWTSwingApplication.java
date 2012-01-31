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
package context2.testcases;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import abbot.finder.AWTHierarchy;
import abbot.finder.swt.SWTHierarchy;

public class SWTSwingApplication {

	private JTextField textField;
	protected Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SWTSwingApplication window = new SWTSwingApplication();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		// read through the hierarchy
		SWTHierarchy h = new SWTHierarchy(display);
		h.dbPrintWidgets();
		AWTHierarchy ah = new AWTHierarchy();
		Collection c = ah.getRoots();	
		for (Iterator it = c.iterator();it.hasNext();){
			Object o = it.next();
			System.out.println(o);
			if (o instanceof java.awt.Frame){
				System.out.println("found frame");
				Collection comp = ah.getComponents((Frame)o);
				for (Iterator ic = comp.iterator();ic.hasNext();){
					Object oc = ic.next();
					System.out.println(oc);
					if (oc instanceof java.awt.Component)
						if (ah.contains((Component)oc))
							System.out.println("is in hierarchy");
				}
					
			}
		}
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setLayout(new FillLayout());
		shell.setSize(500, 375);
		shell.setText("SWT Application");

		final Composite composite = new Composite(shell, SWT.EMBEDDED);
		 composite.setBounds(20, 20, 400, 300);
		composite.setLayout(new FillLayout());

		final Frame frame = SWT_AWT.new_Frame(composite);
		frame.setLayout(null);
	

		final Panel panel = new Panel();
		frame.add(panel);

		final JButton clickMeButton = new JButton();
		clickMeButton.setBounds(204, 176, 93, 23);
		clickMeButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				System.out.println("Button clicked");
			}
			
		});
		clickMeButton.setText("Click Me");
		frame.add(clickMeButton);
		
		textField = new JTextField();
		textField.setBounds(181, 87, 118, 19);
		textField.setName("text");
		frame.add(textField);

		final JLabel swingTextLabel = new JLabel();
		swingTextLabel.setText("Swing Text");
		swingTextLabel.setBounds(95, 89, 80, 14);
		frame.add(swingTextLabel);
		//
	}

}
