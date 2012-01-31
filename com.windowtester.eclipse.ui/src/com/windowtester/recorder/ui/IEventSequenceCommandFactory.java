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

import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.util.ICommand;
import com.windowtester.ui.util.ICommandProvider;

public interface IEventSequenceCommandFactory extends  ICommandProvider {

	ICommand removeEvery();

	ICommand removeAll(ISemanticEvent[] events);

	ICommand remove(ISemanticEvent event);

	ICommand add(ISemanticEvent event);

}
