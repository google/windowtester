package com.windowtester.examples.gef.uml.model;

import com.windowtester.examples.gef.common.model.AbstractRelationship;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class AbstractClassRelationship extends AbstractRelationship<ClassElement, ClassElement>{

	private static final long serialVersionUID = 1L;
	
	private ClassElement _source;
	private ClassElement _target;

	
	@Override
	public ClassElement getSource() {
		return _source;
	}

	@Override
	public ClassElement getTarget() {
		return _target;
	}

	public void setSource(ClassElement source) {
		_source = source;
	}

	public void setTarget(ClassElement target) {
		_target = target;
	}


	
	
}
