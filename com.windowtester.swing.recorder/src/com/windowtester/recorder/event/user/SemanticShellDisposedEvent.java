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
package com.windowtester.recorder.event.user;



public class SemanticShellDisposedEvent extends SemanticShellEvent {

	private static final long serialVersionUID = -6420650777142325456L;
	private final String _shellTitle;
	
	public SemanticShellDisposedEvent(EventInfo info,String shellTitle) {
		super(info);
		_shellTitle = shellTitle;
	}
	
	public SemanticShellDisposedEvent(EventInfo info) {
		this(info,null);
	}
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.SemanticShellEvent#getName()
	 */
	public String getName() {
		if (_shellTitle != null)
			return _shellTitle;
		return super.getName();
	}
	
	public String toString(){
		return "Shell: '" + getName() + "' disposed";

	}
}
