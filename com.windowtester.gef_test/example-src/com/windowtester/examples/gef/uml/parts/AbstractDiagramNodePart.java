package com.windowtester.examples.gef.uml.parts;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.windowtester.examples.gef.common.model.AbstractModelElement;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public abstract class AbstractDiagramNodePart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart {

	
	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelSourceConnections() {
		return new ArrayList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelTargetConnections() {
		return new ArrayList();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	public void activate() {
		if (isActive())
			return;	
		super.activate();
		((AbstractModelElement) getModel()).addPropertyChangeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	public void deactivate() {
		if (!isActive())
			return;
		super.deactivate();
		((AbstractModelElement) getModel()).removePropertyChangeListener(this);
	}


	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		// TODO Auto-generated method stub
		return null;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		// TODO Auto-generated method stub
		return null;
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

}
