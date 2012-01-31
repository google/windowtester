package com.windowtester.examples.gef.uml.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.examples.shapes.model.Connection;
import org.eclipse.gef.examples.shapes.model.Shape;
import org.eclipse.gef.examples.shapes.model.commands.ConnectionCreateCommand;
import org.eclipse.gef.examples.shapes.model.commands.ConnectionReconnectCommand;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import com.windowtester.examples.gef.uml.commands.InheritsCreateCommand;
import com.windowtester.examples.gef.uml.model.ClassElement;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassNodeEditPolicy extends GraphicalNodeEditPolicy {

	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		InheritsCreateCommand cmd = (InheritsCreateCommand) request.getStartCommand();
		cmd.setTarget((ClassElement) getHost().getModel());
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCreateCommand(
			CreateConnectionRequest request) {
		ClassElement source = (ClassElement) getHost().getModel();
		InheritsCreateCommand cmd = new InheritsCreateCommand();
		cmd.setSource(source);
		request.setStartCommand(cmd);
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectSourceCommand(
			ReconnectRequest request) {
//		Connection conn = (Connection) request
//				.getConnectionEditPart().getModel();
//		Shape newSource = (Shape) getHost().getModel();
//		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(
//				conn);
//		cmd.setNewSource(newSource);
//		return cmd;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectTargetCommand(
			ReconnectRequest request) {
//		Connection conn = (Connection) request
//				.getConnectionEditPart().getModel();
//		Shape newTarget = (Shape) getHost().getModel();
//		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(
//				conn);
//		cmd.setNewTarget(newTarget);
//		return cmd;
		return null;
	}


}
