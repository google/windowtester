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
package com.windowtester.runtime.gef.internal.helpers;

import java.util.Iterator;

import org.eclipse.gef.EditPart;

public class EditPartHelper {

	public static interface IEditPartVisitor {
		/**
		 * Visit this part.
		 * @param part the part to visit
		 * @return <code>true</code> if traversal should continue, <code><code>false</code> otherwise
		 */
		public boolean visit(EditPart part);
	}
	
	
	/**
	 * 
	 * EditPartViewer viewer = ...;
	 * EditPart part = viewer.getContents();
	 * visit(part, new IEditPartVisitor() {
	 *   public void(EditPart part) {
	 *   	System.out.println(part);
	 *   }
	 * });
	 * 
	 * @param part
	 * @param visitor
	 */
	public static void visit(EditPart part, IEditPartVisitor visitor) {
		//visit root
		doVisit(part, visitor);

		//visit children
		for (Iterator iter = part.getChildren().iterator(); iter.hasNext();) {
			EditPart child = (EditPart) iter.next();
			visit(child, visitor);
		}
	}

	private static void doVisit(EditPart part, IEditPartVisitor visitor) {
		visitor.visit(part);
	}
	

	
	
	
	
	
}
