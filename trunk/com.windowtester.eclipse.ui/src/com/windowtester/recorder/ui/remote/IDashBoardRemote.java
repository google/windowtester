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
package com.windowtester.recorder.ui.remote;

import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.recorder.ui.IRecorderActionSource;

/**
 * Dashboard remote interface.
 */
public interface IDashBoardRemote {

	public void open();

	public void close();

	public void addStatusSource(IRecorderActionSource actionSource);

	public IDashBoardRemote withModel(IEventSequenceModel sequenceModel);

}
