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
package com.windowtester.runtime.swt.internal.os;

import java.util.Arrays;

final class MultipleRegisteredOSDelegates extends InvalidOSDelegate {
	
	private final IOSDelegate[] delegates;

	public MultipleRegisteredOSDelegates(IOSDelegate[] delegates) {
		this.delegates = delegates;
	}
	
	/**
	 * Take from {@link Arrays#toString(Object[])}
	 */
    static String toString(IOSDelegate[] a) {
		if (a == null)
			return "null";
		int iMax = a.length - 1;
		if (iMax == -1)
			return "[]";

		StringBuffer b = new StringBuffer();
		b.append('[');
		for (int i = 0;; i++) {
			b.append(String.valueOf(a[i]));
			if (i == iMax)
				return b.append(']').toString();
			b.append(", ");
		}
	}
    
    /* (non-Javadoc)
     * @see com.windowtester.runtime.swt.internal.os.InvalidOSDelegate#invalid()
     */
    protected void invalid() {
    	throw new IllegalStateException("Too many registered OS delegates: " + toString(delegates));
    }
    
    
}