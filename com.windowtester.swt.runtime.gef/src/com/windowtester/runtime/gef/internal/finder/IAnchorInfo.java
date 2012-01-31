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
package com.windowtester.runtime.gef.internal.finder;

import org.eclipse.draw2d.ConnectionAnchor;

import com.windowtester.runtime.gef.Position;

public interface IAnchorInfo {
	
	public static IAnchorInfo MULTIPLE_ANCHORS = new IAnchorInfo() {
		public ConnectionAnchor getAnchor() {
			// TODO Auto-generated method stub
			return null;
		}
		public Position getPosition() {
			// TODO Auto-generated method stub
			return null;
		}
		
		public String toString() {
			return "[Mutliple Anchors]";
		}
		public boolean hasPosition(Position position) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	
	ConnectionAnchor getAnchor();
	Position getPosition();
	boolean hasPosition(Position position);
	
}