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
package com.windowtester.test.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class TreeItemReferenceStaticTreeWithImagesTest extends TreeItemReferenceStaticTreeWithChecksTest {

	
		@Override
		protected void itemCreated(TreeItem item) {
			Display display = item.getDisplay();
			Image image = new Image (display, 16, 16);
			GC gc = new GC(image);
			gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
			gc.drawLine(1, 1, 14, 14);
			gc.drawLine(1, 14, 14, 1);
			gc.drawOval(2, 2, 11, 11);
			gc.dispose();
			item.setImage(image);
		}
		
	
	
}
