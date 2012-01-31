package com.windowtester.examples.gef.uml.model;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.examples.gef.common.model.AbstractModelElement;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassDiagram extends AbstractModelElement {

	private static final long serialVersionUID = 1L;

	/** Property ID to use when a child is added to this diagram. */
	public static final String CHILD_ADDED_PROP = "ClassDiagramEditPart.ChildAdded";
	
	/** Property ID to use when a child is removed from this diagram. */
	public static final String CHILD_REMOVED_PROP = "ClassDiagramEditPart.ChildRemoved";

	
	private final List<ClassElement> elements = new ArrayList<ClassElement>();
	
	/** 
	 * Add an element to this diagram.
	 * @param elem a non-null element instance
	 * @return true, if the element was added, false otherwise
	 */
	public boolean addChild(ClassElement elem) {
		if (elem != null && getElements().add(elem)) {
			firePropertyChange(CHILD_ADDED_PROP, null, elem);
			return true;
		}
		return false;
	}
	
	/** 
	 * Remove an element from this diagram.
	 * @param elem a non-null element instance
	 * @return true, if the element was removed, false otherwise
	 */
	public boolean removeChild(ClassElement elem) {
		if (elem != null && getElements().remove(elem)) {
			firePropertyChange(CHILD_ADDED_PROP, null, elem);
			return true;
		}
		return false;
	}
	
	public List<ClassElement> getElements() {
		return elements;
	}
	
	
}
