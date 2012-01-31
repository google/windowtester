package com.windowtester.examples.gef.uml.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import com.windowtester.examples.gef.common.part.connector.BottomAnchor;
import com.windowtester.examples.gef.common.part.connector.TopAnchor;
import com.windowtester.examples.gef.uml.figures.ClassElementFigure;
import com.windowtester.examples.gef.uml.model.ClassElement;
import com.windowtester.examples.gef.uml.model.InheritsRelationship;
import com.windowtester.examples.gef.uml.policies.ClassNodeEditPolicy;
import com.windowtester.examples.gef.uml.policies.UMLElementLayoutPolicy;


/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassElementEditPart extends AbstractDiagramNodePart {

	public ClassElementEditPart(ClassElement model) {
		setModel(model);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		ClassElement classElement = getClassElement();
		return new ClassElementFigure(classElement.getName());
	}

	protected ClassElement getClassElement() {
		return (ClassElement) getModel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,  new UMLElementLayoutPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ClassNodeEditPolicy());
		
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (ClassElement.SIZE_PROP.equals(prop) || ClassElement.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
		} 
		else if (ClassElement.SOURCE_CONNECTIONS_PROP.equals(prop)) {
			refreshSourceConnections();
		} else if (ClassElement.TARGET_CONNECTIONS_PROP.equals(prop)) {
			refreshTargetConnections();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		Point location = getClassElement().getLocation();
		Rectangle bounds = new Rectangle(location.x, location.y, -1, -1);
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
	}
	
	
	/**
	 * @see NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection)
	{
		return new TopAnchor(getFigure());
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request)
	{
		return new TopAnchor(getFigure());
	}

	/**
	 * @see NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection)
	{
		return new BottomAnchor(getFigure());
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request)
	{
		return new BottomAnchor(getFigure());
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelSourceConnections() { 
		InheritsRelationship s = getClassElement().getSuper();
		List supers = new ArrayList();
		supers.add(s);
		return supers;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelTargetConnections() {
		return getClassElement().getSubs();
	}
}
