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
package com.windowtester.recorder.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.windowtester.recorder.ui.EventSequenceCommandFactory.NotApplicableCommand;
import com.windowtester.recorder.ui.EventSequenceCommandFactory.RemoveAllCommand;
import com.windowtester.recorder.ui.EventSequenceCommandFactory.RemoveCommand;
import com.windowtester.recorder.ui.EventSequenceCommandFactory.RemoveEveryCommand;
import com.windowtester.ui.util.ICommand;
import com.windowtester.ui.util.ICommandLabelProvider;

public class SequenceCommandLabelProvider implements ICommandLabelProvider {

	public ImageDescriptor getImage(ICommand command) {
		
		if (isRemove(command) || isRemoveAll(command) || isRemoveEvery(command)) {
			ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
			return images.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE);
		}
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(ICommand command) {
		if (isNotApplicable(command))
			return IActionConstants.NOT_APPLICABLE_TEXT;
		if (isRemove(command))
			return IActionConstants.DELETE_ACTION_TEXT;
		if (isRemoveAll(command))
			return IActionConstants.DELETE_ACTION_TEXT;
		if (isRemoveEvery(command))
			return IActionConstants.DELETE_ACTION_TEXT;
		
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isNotApplicable(ICommand command) {
		return command instanceof NotApplicableCommand;
	}

	private boolean isRemove(ICommand command) {
		return command instanceof RemoveCommand;
	}
	
	private boolean isRemoveAll(ICommand command) {
		return command instanceof RemoveAllCommand;
	}
	
	private boolean isRemoveEvery(ICommand command) {
		return command instanceof RemoveEveryCommand;
	}
	
	
}
