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
package com.windowtester.runtime.gef.internal.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;

/**
 * A utility class for getting string dumps of editpart hierarchies.
 */
public class EditPartDescriber extends AbstractDescriber {

	private final EditPart _rootPart;

	public EditPartDescriber(EditPart rootPart) {
		_rootPart = rootPart;
	}
	
	public void describeTo(StringBuffer buffer) {
		describe(_rootPart, 0, buffer);
	}

	private void describe(EditPart part, int indent, StringBuffer buffer) {
		indent(indent, buffer);
		describePartTo(part, buffer);
		List children = part.getChildren();
		if (children.size() > 0)
			newline(buffer);
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			EditPart childPart = (EditPart) iter.next();
			describe(childPart, indent + getIndent(), buffer);
			newline(buffer);
		}
	}
	
	private void describePartTo(EditPart part, StringBuffer buffer) {
		buffer.append(part);
		if (part instanceof GraphicalEditPart) {
			describeGraphicalPartTo((GraphicalEditPart)part, buffer);
		}
	}

	private void describeGraphicalPartTo(GraphicalEditPart part, StringBuffer buffer) {
		separate(buffer);
		describeFigureTo(part.getFigure(), buffer);
	}

	private void describeFigureTo(IFigure figure, StringBuffer buffer) {
		separate(buffer);
		buffer.append(figure.toString());
	}
	
}
