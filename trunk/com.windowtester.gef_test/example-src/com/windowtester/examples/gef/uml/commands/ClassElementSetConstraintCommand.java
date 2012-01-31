package com.windowtester.examples.gef.uml.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import com.windowtester.examples.gef.uml.model.ClassElement;

/**
 * A command to resize or move a class element.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassElementSetConstraintCommand extends Command {

	
	private final ClassElement _elem;
	private final ChangeBoundsRequest _request;
	private final Rectangle _newBounds;
	private Rectangle _oldBounds;

	public ClassElementSetConstraintCommand(ClassElement elem, ChangeBoundsRequest req, Rectangle newBounds) {
		if (elem == null || req == null || newBounds == null) {
			throw new IllegalArgumentException();
		}
		_elem = elem;
		_request = req;
		_newBounds = newBounds;
	}
	
	public ChangeBoundsRequest getRequest() {
		return _request;
	}
	
	public Rectangle getNewBounds() {
		return _newBounds;
	}
	
	public ClassElement getElement() {
		return _elem;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		Object type = getRequest().getType();
		// make sure the Request is of a type we support:
		return (RequestConstants.REQ_MOVE.equals(type)
				|| RequestConstants.REQ_MOVE_CHILDREN.equals(type)
				|| RequestConstants.REQ_RESIZE.equals(type) || RequestConstants.REQ_RESIZE_CHILDREN
				.equals(type));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		setOldBounds(new Rectangle(getElement().getLocation(), getDimensions(getElement())));
		redo();
	}

	private Dimension getDimensions(ClassElement element) {
		Dimension size = element.getSize();
		if (size == null)
			size = new Dimension();
		return size;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		//getElement().setSize(getNewBounds().getSize());
		getElement().setLocation(getNewBounds().getLocation());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		//getElement().setSize(_oldBounds.getSize());
		getElement().setLocation(getOldBounds().getLocation());
	}

	private void setOldBounds(Rectangle oldBounds) {
		_oldBounds = oldBounds;
	}

	private Rectangle getOldBounds() {
		return _oldBounds;
	}
	
	
	
}
