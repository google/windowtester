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
import com.windowtester.recorder.event.user.SemanticTextEntryEvent;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.internal.corel.model.Event;

public class EventParser {

	
	public static ParsedEvent parse(ISemanticEvent event) {
	
		Object semantic = getSemantic(event);
		if (isKeyEvent(semantic))
			return new ParsedKeyEvent((SemanticKeyDownEvent)semantic, event);
		if (isTextEvent(semantic))
			return new ParsedTextEvent((SemanticTextEntryEvent)semantic);
		return new ParsedWidgetEvent(event);
	}
	
	
	

	private static boolean isKeyEvent(Object semantic) {
		return semantic instanceof SemanticKeyDownEvent;
	}

	private static boolean isTextEvent(Object semantic) {
		return semantic instanceof SemanticTextEntryEvent;
	}
	
	public static boolean isTextEventTerminator(ISemanticEvent event) {
		Object semantic = getSemantic(event);
		return isTextEventTerminator(semantic);
	}
	
	public static boolean isTextEventTerminator(Object semantic) {
		SemanticKeyDownEvent keyDown = (SemanticKeyDownEvent)semantic;
		if (keyDown.isControlSequence())
			return true;
		String key = keyDown.getKey();
		boolean isTerminator = PluggableCodeGenerator.isControl(key) && !PluggableCodeGenerator.isBackSpace(key);		
		System.out.println(key + " terminates: " + isTerminator);
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
