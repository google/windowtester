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
package com.windowtester.ui.core.model;

import com.windowtester.recorder.ui.IEventProvider;

public interface IEventSequence extends IEventProvider {

	IEventSequence add(ISemanticEvent event);

	IEventSequence addAll(ISemanticEvent[] events);

	IEventSequence removeAll();

	IEventSequence removeAll(ISemanticEvent[] events);

	IEventSequence remove(ISemanticEvent event);

	IEventSequence add(int index, ISemanticEvent event);

	IEventGroup group(IEvent[] events);

	boolean isEmpty();

	
}
