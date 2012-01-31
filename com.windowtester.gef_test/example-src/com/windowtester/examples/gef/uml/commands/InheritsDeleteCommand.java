package com.windowtester.examples.gef.uml.commands;

import org.eclipse.gef.commands.Command;
import com.windowtester.examples.gef.uml.model.InheritsRelationship;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class InheritsDeleteCommand extends Command {

	private final InheritsRelationship _relationship;

	public InheritsDeleteCommand(InheritsRelationship relationship) {
		_relationship = relationship;
	}

	public InheritsRelationship getRelationship() {
		return _relationship;
	}
	
	@Override
	public void execute() {
		getRelationship().disconnect();
	}
	
	
	@Override
	public void undo() {
		getRelationship().reconnect();
	}
	
	
}
