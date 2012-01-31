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

import com.windowtester.codegen.generator.PluggableCodeGenerator;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.internal.corel.model.Event;

public abstract class AbstractParsedTextEvent extends ParsedEvent {

	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.ParsedEvent#consumes(com.windowtester.recorder.ui.ParsedEvent)
	 */
	public boolean consumes(ParsedEvent event) {
		if (!(event instanceof ParsedKeyEvent))
			return false;
		ParsedKeyEvent key = (ParsedKeyEvent)event;
		return isStandardKeyEvent(key.key());
	}
	
	private static boolean isStandardKeyEvent(Object semantic) {
		return semantic instanceof SemanticKeyDownEvent && !isTextEventTerminator(semantic);
	}
	
	public static boolean isTextEventTerminator(ISemanticEvent event) {
		Object semantic = getSemantic(event);
		return isTextEventTerminator(semantic);
	}
	
	public static boolean isTextEventTerminator(Object semantic) {
		if (!(semantic instanceof SemanticKeyDownEvent))
			return true;
		SemanticKeyDownEvent keyDown = (SemanticKeyDownEvent)semantic;
		String key = keyDown.getKey();
		boolean isTerminator = PluggableCodeGenerator.isControl(key) && !PluggableCodeGenerator.isBackSpace(key);		
		return isTerminator;
	}
	
	private static Object getSemantic(ISemanticEvent event) {
		if (event instanceof Event)
			return ((Event)event).getUIEvent();
		if (event instanceof com.windowtester.recorder.event.ISemanticEvent)
			return event;
		return null;
	}

	
}
