/*******************************************************************************
 *
 *   Copyright (c) 2012 Google, Inc.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *   Google, Inc. - initial API and implementation
 *  
 *******************************************************************************/

package com.windowtester.example.contactmanager.rcp.action;

import org.eclipse.jface.action.Action;

public class CutContactAction extends Action
{
	private CopyContactAction copyAction;
	private DeleteAction removeAction;
	
	public CutContactAction(
		CopyContactAction copyAction,
		DeleteAction removeAction,
		String text) {
		
		super(text);
		this.copyAction = copyAction;
		this.removeAction = removeAction;
	}
	
	public void run(){
		copyAction.run();
		removeAction.run();
		
	}
}
