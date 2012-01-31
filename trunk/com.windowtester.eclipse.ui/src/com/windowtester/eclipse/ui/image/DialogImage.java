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
package com.windowtester.eclipse.ui.image;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Type Safe Enumeration: DialogImage elements: INFO, WARNING, ERROR, QUESTION
 * <p>
 * @pattern Type Safe Enumeration
 *
 * @author Dan Rubel
 */
public final class DialogImage {
	
	/**
	 * The "INFO" enumeration element
	 */
	/* $codepro.preprocessor.if version >= 3.0 $ */
	public static final DialogImage INFO = new DialogImage("INFO", Display.getCurrent().getSystemImage(SWT.ICON_INFORMATION));
	/* $codepro.preprocessor.elseif version < 3.0 $
	public static final DialogImage INFO = new DialogImage("INFO", org.eclipse.jface.dialogs.Dialog.DLG_IMG_INFO);
	$codepro.preprocessor.endif $ */

	/**
	 * The "WARNING" enumeration element
	 */
	/* $codepro.preprocessor.if version >= 3.0 $ */
	public static final DialogImage WARNING = new DialogImage("WARNING", Display.getCurrent().getSystemImage(SWT.ICON_WARNING));
	/* $codepro.preprocessor.elseif version < 3.0 $
	public static final DialogImage WARNING = new DialogImage("WARNING", org.eclipse.jface.dialogs.Dialog.DLG_IMG_WARNING);
	$codepro.preprocessor.endif $ */

	/**
	 * The "ERROR" enumeration element
	 */
	/* $codepro.preprocessor.if version >= 3.0 $ */
	public static final DialogImage ERROR = new DialogImage("ERROR", Display.getCurrent().getSystemImage(SWT.ICON_ERROR));
	/* $codepro.preprocessor.elseif version < 3.0 $
	public static final DialogImage ERROR = new DialogImage("ERROR", org.eclipse.jface.dialogs.Dialog.DLG_IMG_ERROR);
	$codepro.preprocessor.endif $ */

	/**
	 * The "QUESTION" enumeration element
	 */
	/* $codepro.preprocessor.if version >= 3.0 $ */
	public static final DialogImage QUESTION = new DialogImage("QUESTION", Display.getCurrent().getSystemImage(SWT.ICON_QUESTION));
	/* $codepro.preprocessor.elseif version < 3.0 $
	public static final DialogImage QUESTION = new DialogImage("QUESTION", org.eclipse.jface.dialogs.Dialog.DLG_IMG_QUESTION);
	$codepro.preprocessor.endif $ */

	/**
	 * The string representation of the enumeration.
	 */
	private final String printName;

	/**
	 * The registery key for the image
	 */
	/* $codepro.preprocessor.if version >= 3.0 $ */
	private Image image;
	/* $codepro.preprocessor.elseif version < 3.0 $
	private String imageId;
	$codepro.preprocessor.endif $ */

	/**
	 * Create an enumeration element.
	 *
	 * @param name the name of the enumeration element
	 */
	/* $codepro.preprocessor.if version >= 3.0 $ */
	private DialogImage(String name, Image image) {
		this.printName = name;
		this.image = image;
	}
	/* $codepro.preprocessor.elseif version < 3.0 $
	private DialogImage(String name, String imageId) {
		this.printName = name;
		this.imageId = imageId;
	}
	$codepro.preprocessor.endif $ */

	/**
	 * Return the string representation of the enumeration.
	 *
	 * @return the name of the enumeration element
	 */
	public String toString() {
		return "DialogImage." + printName;
	}
	
	/**
	 * Return the image associated with the receiver.
	 * 
	 * @return the image
	 */
	/* $codepro.preprocessor.if version >= 3.0 $ */
	public Image getImage() {
		return image;
	}
	/* $codepro.preprocessor.elseif version < 3.0 $
	public Image getImage() {
		return org.eclipse.jface.resource.JFaceResources.getImageRegistry().get(imageId);
	}
	$codepro.preprocessor.endif $ */

}