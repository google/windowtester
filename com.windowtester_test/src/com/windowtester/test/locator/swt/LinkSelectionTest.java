package com.windowtester.test.locator.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;


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
public class LinkSelectionTest extends AbstractLocatorTest {


	private static final String FULL_LINK_TEXT1 = "This a very simple <A>link</A> widget.";
	private static final String FULL_LINK_TEXT2 = "This another very simple <A>link</A> widget.";
	private static final String FULL_LINK_TEXT3 = "This is yet another very simple <A>link</A> widget.";

	
	private boolean linkOneSelected;
	private boolean linkTwoSelected;
	private boolean linkThreeSelected;
	private Shell shell;

	
	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiSetup()
	 */
	@Override
	public void uiSetup() {
		shell = new Shell(Display.getDefault());
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);

		Link link = new Link(shell, SWT.BORDER);
		link.setText(FULL_LINK_TEXT1);
		link.setSize(300, 40);
		link.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				System.out.println("Selection: " + e.text);
				linkOneSelected = true;
			}
		});
		
		Link link2 = new Link(shell, SWT.BORDER);
		link2.setText(FULL_LINK_TEXT2);
		link2.setSize(300, 40);
		link2.addSelectionListener(new SelectionListener() {


			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				System.out.println("Selection: " + e.text);
				linkTwoSelected = true;
			}
		});
		
		Link link3 = new Link(shell, SWT.BORDER);
		link3.setText(FULL_LINK_TEXT3);
		link3.setSize(300, 40);
		link3.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				System.out.println("Selection: " + e.text);
				linkThreeSelected = true;
			}
		});
		
		shell.layout();
		shell.pack ();
		shell.open();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiTearDown()
	 */
	@Override
	public void uiTearDown() {
		shell.dispose();
	}
	
	public void testSelectLink() throws Exception {
		IUIContext ui = getUI();
		
		ui.click(new SWTWidgetLocator(Link.class, FULL_LINK_TEXT1));
		ui.assertThat(new ICondition() {
			public boolean test() {
				return linkOneSelected;
			}
		});
		ui.click(new SWTWidgetLocator(Link.class, FULL_LINK_TEXT2));
		ui.assertThat(new ICondition() {
			public boolean test() {
				return linkTwoSelected;
			}
		});
		ui.click(new SWTWidgetLocator(Link.class, FULL_LINK_TEXT3));
		ui.assertThat(new ICondition() {
			public boolean test() {
				return linkThreeSelected;
			}
		});
	}
	
	
}
