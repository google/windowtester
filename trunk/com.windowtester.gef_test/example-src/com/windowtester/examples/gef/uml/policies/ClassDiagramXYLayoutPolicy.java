package com.windowtester.examples.gef.uml.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.windowtester.examples.gef.uml.commands.ClassElementCreateCommand;
import com.windowtester.examples.gef.uml.commands.ClassElementSetConstraintCommand;
import com.windowtester.examples.gef.uml.model.ClassDiagram;
import com.windowtester.examples.gef.uml.model.ClassElement;
import com.windowtester.examples.gef.uml.parts.ClassElementEditPart;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassDiagramXYLayoutPolicy extends XYLayoutEditPolicy {

	
	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		if (child instanceof ClassElementEditPart && constraint instanceof Rectangle) {
			// return a command that can move and/or resize a Shape
			return new ClassElementSetConstraintCommand(
					(ClassElement) child.getModel(), request, (Rectangle) constraint);
		}
		return super.createChangeConstraintCommand(request, child, constraint);
	}
	
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Object newObject = request.getNewObject();
		if (newObject instanceof ClassElement) {
			return new ClassElementCreateCommand((ClassElement)newObject, (ClassDiagram)getHost().getModel(), (Rectangle)getConstraintFor(request));
		}
		return null;
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		// TODO Auto-generated method stub -- not used?
		return null;
	}

}
