/**
 * 
 */
package com.windowtester.examples.gef.uml.editor;

import org.eclipse.gef.requests.CreationFactory;

import com.windowtester.examples.gef.uml.model.ClassElement;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassCreationFactory implements CreationFactory {

	private static final String DEFAULT_NEW_CLASS_NAME_PROPOSAL = "MyClass";

	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject() {
		ClassElement elem = new ClassElement();
		elem.setName(DEFAULT_NEW_CLASS_NAME_PROPOSAL);
		return elem;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType() {
		return ClassElement.class;
	}
	
	
}