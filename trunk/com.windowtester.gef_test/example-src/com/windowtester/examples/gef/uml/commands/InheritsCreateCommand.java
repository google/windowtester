package com.windowtester.examples.gef.uml.commands;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.examples.shapes.model.Connection;

import com.windowtester.examples.gef.uml.model.ClassElement;
import com.windowtester.examples.gef.uml.model.InheritsRelationship;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class InheritsCreateCommand extends Command {

	
	private ClassElement _source;
	private ClassElement _target;
	
	private InheritsRelationship _relationship;
	
	
	public void setSource(ClassElement source) {
		_source = source;
	}
	
	public ClassElement getSource() {
		return _source;
	}
	
	public void setTarget(ClassElement target) {
		_target = target;
	}
	
	public ClassElement getTarget() {
		return _target;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	 @Override
	public boolean canExecute() {
		
		ClassElement source = getSource();
		ClassElement target = getTarget();
		
		
		// disallow source -> source connections
		if (source.equals(target)) {
			return false;
		}
		
		if (source.getSuper() != null)
			return false;
		
		return true;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		setRelationship(new InheritsRelationship(getSource(), getTarget()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		getRelationship().reconnect();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		getRelationship().disconnect();
	}

	
	private void setRelationship(InheritsRelationship relationship) {
		_relationship = relationship;
	}

	private InheritsRelationship getRelationship() {
		return _relationship;
	}
	
	
}
