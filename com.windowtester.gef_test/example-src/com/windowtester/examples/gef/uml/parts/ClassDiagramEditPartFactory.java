package com.windowtester.examples.gef.uml.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.windowtester.examples.gef.uml.model.ClassDiagram;
import com.windowtester.examples.gef.uml.model.ClassElement;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassDiagramEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof ClassElement) {
			return new ClassElementEditPart((ClassElement)model);
		}
		if (model instanceof ClassDiagram) {
			return new ClassDiagramEditPart((ClassDiagram)model);
		}
		return null;
	}

}
