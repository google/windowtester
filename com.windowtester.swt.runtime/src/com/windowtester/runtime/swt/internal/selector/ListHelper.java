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
package com.windowtester.runtime.swt.internal.selector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * This util class helps to get more information on Platform specific features of List widget 
 */
public class ListHelper {
	
	private static int itemListSpacing = -1;
	
	/**
	 * Calculate the spacing for list item off screen (shell wont be open).
	 * 
	 * @param d Display instance to support
	 * @return the spacing correction for list item height 
	 */
	public static int getItemListSpacing(Display d){
		if(itemListSpacing==-1)
			calculateItemListSpacing(d);
		return itemListSpacing;
	}
	
	public static void calculateItemListSpacing(final Display d) {
		d.syncExec(new Runnable(){
			public void run() {
				Shell shell = new Shell(d);
				shell.setLayout(new GridLayout());
				List list = new List(shell, SWT.NONE);
				list.setItems(new String[] {"1", "2"});
				shell.pack();
				int itemHeight = list.getItemHeight();
				int clientAreaHeight = list.getClientArea().height;
				itemListSpacing = (clientAreaHeight - itemHeight*2)/2;
				shell.dispose();
			}
		});
	}
	/**
	 * Fix item height dimention. On some platforms list items can have borders or spacing. This method provides platform independent calculation.
	 * 
	 * @param d the Display instance
	 * @param oldHeight the original height parameter 
	 * @return fixed height of list item including extra space consideration
	 */
	public static int fixItemHeight(Display d, int oldHeight){
		// get extra item spacing
		int extraItemSpace = ListHelper.getItemListSpacing(d);
		// if there is extra space for list items, - fix the Item height (this may be due to the 
		// fact that list itmes have borders on some platforms)
		if(extraItemSpace>0)
			return oldHeight + extraItemSpace;
		return oldHeight;
	}
	
	public static void main(String[] args){
		Display d = new Display();
		int spacing = getItemListSpacing(d);
		System.out.println(spacing);
	}
}
