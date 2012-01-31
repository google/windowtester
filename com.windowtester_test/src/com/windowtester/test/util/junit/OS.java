package com.windowtester.test.util.junit;

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
public enum OS {
	ALL, 
	WIN {
		@Override
		public boolean isCurrent() {
			return !OSX.isCurrent() && !LINUX.isCurrent();
		}
	},
	OSX {
		@Override
		public boolean isCurrent() {
			return com.windowtester.runtime.internal.OS.isOSX();
		}
	},
	LINUX {
		@Override
		public boolean isCurrent() {
			return com.windowtester.runtime.internal.OS.isLinux();
		}
	}, NONE {
		@Override
		public boolean isCurrent() {
			return false;
		}
		
	};
	
	public boolean isCurrent(){
		return true;
	}
}