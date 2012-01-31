package com.windowtester.examples.gef.uml.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import com.windowtester.examples.gef.uml.figures.ClassDiagramFigure;
import com.windowtester.examples.gef.uml.model.ClassDiagram;
import com.windowtester.examples.gef.uml.model.ClassElement;
import com.windowtester.examples.gef.uml.policies.ClassDiagramXYLayoutPolicy;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassDiagramEditPart extends AbstractDiagramNodePart {
	
	public ClassDiagramEditPart(ClassDiagram model) {
		setModel(model);
	}

	protected ClassDiagram getDiagram() {
		return (ClassDiagram) getModel();
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new ClassDiagramFigure();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ClassDiagramXYLayoutPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);	
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List<ClassElement> getModelChildren() {
		return getDiagram().getElements();
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (ClassDiagram.CHILD_ADDED_PROP.equals(prop)
				|| ClassDiagram.CHILD_REMOVED_PROP.equals(prop)) {
			refreshChildren();
		}
	}
	
	
	
}
