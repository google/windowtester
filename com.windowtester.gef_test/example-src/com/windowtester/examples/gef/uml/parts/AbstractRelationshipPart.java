package com.windowtester.examples.gef.uml.parts;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractConnectionEditPart;

import com.windowtester.examples.gef.common.model.AbstractModelElement;
import com.windowtester.examples.gef.uml.model.AbstractClassRelationship;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public abstract class AbstractRelationshipPart extends AbstractConnectionEditPart implements PropertyChangeListener {

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
	
	public AbstractClassRelationship getRelationship() {
		return (AbstractClassRelationship) getModel();
	}
	
	
	
}
