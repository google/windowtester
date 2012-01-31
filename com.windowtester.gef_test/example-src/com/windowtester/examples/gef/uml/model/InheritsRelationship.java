package com.windowtester.examples.gef.uml.model;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class InheritsRelationship extends AbstractClassRelationship {


	private static final long serialVersionUID = 1L;
	
	private boolean _isConnected;
	
	public InheritsRelationship(ClassElement source, ClassElement target) {
		setSource(source);
		setTarget(target);
		reconnect();
	}

	public void disconnect() {
		if (_isConnected) {
			getSource().setSuper(null);
			getTarget().removeSub(this);
			_isConnected = false;
		}
	}

	public void reconnect() {
		if (!_isConnected) {
			getSource().setSuper(this);
			getTarget().addSub(this);
			_isConnected = true;
		}
	}
	
	
}
