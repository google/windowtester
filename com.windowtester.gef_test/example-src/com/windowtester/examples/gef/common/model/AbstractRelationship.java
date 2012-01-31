package com.windowtester.examples.gef.common.model;


/**
 * A basic relationship base class.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public abstract class AbstractRelationship<S,T> extends AbstractModelElement {

	private static final long serialVersionUID = 1L;

	public abstract S getSource();
	
	public abstract T getTarget();
	
	
}
