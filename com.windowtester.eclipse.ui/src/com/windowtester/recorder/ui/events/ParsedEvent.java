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
package com.windowtester.recorder.ui.events;

import com.windowtester.ui.core.model.IEventSequence;

public abstract class ParsedEvent {

	public boolean consumes(ParsedEvent event) {
		return false;
	}

	public ParsedEvent consume(ParsedEvent event) {
		return this;
	}

	public abstract void addTo(IEventSequence sequence);
	
}
