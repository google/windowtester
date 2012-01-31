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
package com.windowtester.recorder.ui.remote.standalone;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.internal.runtime.Platform;

public class ImageManager {

	
	public static Image getImage(String imageName) {
		try {
			if (!Platform.isRunning()) {
				ImageData data = new ImageData(ImageManager.class.getResourceAsStream(imageName));
				return new Image(Display.getDefault(), data);
			}
			return UiPlugin.getDefault().getImage(
					"icons/full/obj16/" + imageName);
		} catch (Throwable e) {
			return null;
		}
	}

	public static ImageDescriptor getDescriptor(String imageName) {
		return ImageDescriptor.createFromImage(getImage(imageName));
	}
	
}
