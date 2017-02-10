/*******************************************************************************
 *  Copyright (c) 2013 Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Frederic Gurr - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.custom.StyledText;

/**
 * A {@link StyledText} reference.
 */
public class StyledTextReference extends CompositeReference<StyledText> {

	public StyledTextReference(StyledText control) {
		super(control);
	}
	
	public String getLine(final int lineIndex){
		return displayRef.execute(new Callable<String>() {
			public String call() throws Exception {
				return widget.getLine(lineIndex);
			}
		});
	}
}
