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
package com.windowtester.runtime.gef.internal.commandstack;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;

/**
 * Stack event type.
 */
public class CommandStackEventType {
	
	public static final CommandStackEventType PRE     = new CommandStackEventType("PRE") {
		public boolean isPre() { 
			return true; 
		}
	};
	public static final CommandStackEventType POST    = new CommandStackEventType("POST");
	public static final CommandStackEventType UNKNOWN = new CommandStackEventType("UNKNOWN");
	
	private final String typeName;

	public String toString() {
		return typeName + " CommandStack Event";
	}
	
	private CommandStackEventType(String typeName) {
		this.typeName = typeName;
	}
	
	public boolean isPre() {
		return false;
	}
	
	public static CommandStackEventType forEvent(CommandStackEvent event) {
		int detail = event.getDetail();
		if ((detail & CommandStack.PRE_EXECUTE) == CommandStack.PRE_EXECUTE)
			return PRE;
		if ((detail & CommandStack.POST_EXECUTE) == CommandStack.POST_EXECUTE)
			return POST;
		if ((detail & CommandStack.PRE_UNDO) == CommandStack.PRE_UNDO)
			return PRE;
		if ((detail & CommandStack.POST_UNDO) == CommandStack.POST_UNDO)
			return POST;
		if ((detail & CommandStack.PRE_REDO) == CommandStack.PRE_REDO)
			return PRE;
		if ((detail & CommandStack.POST_REDO) == CommandStack.POST_REDO)
			return POST;		
		return UNKNOWN;
	}
}