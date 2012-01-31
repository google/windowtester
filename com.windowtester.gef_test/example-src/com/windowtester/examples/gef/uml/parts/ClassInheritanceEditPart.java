package com.windowtester.examples.gef.uml.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.examples.shapes.model.commands.ConnectionDeleteCommand;
import org.eclipse.gef.requests.GroupRequest;

import com.windowtester.examples.gef.uml.commands.InheritsDeleteCommand;
import com.windowtester.examples.gef.uml.model.AbstractClassRelationship;
import com.windowtester.examples.gef.uml.model.InheritsRelationship;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassInheritanceEditPart extends AbstractRelationshipPart {

	@Override
	protected void createEditPolicies() {
		// Makes the connection show a feedback, when selected by the user.
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		// Allows the removal of the connection model element
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
			protected Command getDeleteCommand(GroupRequest request) {
				return new InheritsDeleteCommand(getRelationship());
			}
		});
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
	}

	@Override
	public InheritsRelationship getRelationship() {
		return (InheritsRelationship) super.getRelationship();
	}
	
}
