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
package com.windowtester.codegen.ui.controller;

import com.windowtester.swt.util.ImageManager;

/**
 * A class to manage the lifecyle of (shared) recorder images.  
 * based on org.eclipse.tptp.test.auto.gui.internal.AutoGUIImages and modified
 */
public class RecorderUIImages extends ImageManager {
	
	public static final RecorderUIImages INSTANCE = new RecorderUIImages();
	
	public static final String IMG_NEW_MANUAL					= "new_suite_obj.gif"; //$NON-NLS-1$	
	public static final String TERMINATE						= "terminate_co.gif";
	public static final String POSITION_BASED					= "position_based.gif";
	public static final String RESTART							= "restart.gif";
	public static final String DATAPOOL							= "datapool_obj.gif";
	public static final String ERROR							= "error.gif";
	public static final String WARNING							= "warning.gif";
	public static final String WAIT_TIME						= "artificial_wait.gif";
	public static final String LINK								= "link.gif";
	public static final String VARIABLE							= "variable.gif";
	public static final String VARIABLE_ITEM					= "variable_item.gif";
	public static final String START 							= "start_recording.gif";
	public static final String WRITE 							= "write_recording.gif";
	public static final String GENERATE_TEST					= "generate_test.gif"; //$NON-NLS-1$	
	public static final String PAUSE                            = "pause.gif";

	
	protected void addImages() 
	{
		add(T_OBJ, IMG_NEW_MANUAL);		
		add(T_OBJ, TERMINATE);		
		add(T_OBJ, RESTART);
		add(T_OBJ, POSITION_BASED);		
		add(T_OBJ, DATAPOOL);	
		add(T_OBJ, ERROR);
		add(T_OBJ, WARNING);
		add(T_OBJ, WAIT_TIME);
		add(T_OBJ, LINK);
		add(T_OBJ, VARIABLE);
		add(T_OBJ, VARIABLE_ITEM);
		add(T_OBJ, START);
		add(T_OBJ, WRITE);	
		add(T_OBJ, GENERATE_TEST);		
		add(T_OBJ, PAUSE);		
	}
}
