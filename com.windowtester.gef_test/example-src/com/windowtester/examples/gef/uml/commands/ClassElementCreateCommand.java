package com.windowtester.examples.gef.uml.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.windowtester.examples.gef.uml.model.ClassDiagram;
import com.windowtester.examples.gef.uml.model.ClassElement;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassElementCreateCommand extends Command {

	
	private final ClassElement _newClass;
	private final ClassDiagram _parent;
	private final Rectangle _bounds;

	public ClassElementCreateCommand(ClassElement newClass, ClassDiagram parent, Rectangle bounds) {
		_newClass = newClass;
		_parent = parent;
		_bounds = bounds;
	}
	
	
	public ClassElement getNewClass() {
		return _newClass;
	}
	
	public ClassDiagram getParent() {
		return _parent;
	}
	
	public Rectangle getBounds() {
		return _bounds;
	}
	
	/**
	 * Can execute if all the necessary information has been provided. 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return getNewClass() != null && getParent() != null && getBounds() != null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		ClassElement newClass = getNewClass();
		Rectangle bounds = getBounds();
		newClass.setLocation(bounds.getLocation());
//		Dimension size = bounds.getSize();
//		if (size.width > 0 && size.height > 0)
//			newClass.setSize(size);
		redo();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		getParent().addChild(getNewClass());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getParent().removeChild(getNewClass());
	}
	
	
}
