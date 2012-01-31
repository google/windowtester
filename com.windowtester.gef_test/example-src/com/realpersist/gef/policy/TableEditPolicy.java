/*
 * Created on Jul 15, 2004
 */
package com.realpersist.gef.policy;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.realpersist.gef.command.DeleteTableCommand;
import com.realpersist.gef.model.Schema;
import com.realpersist.gef.model.Table;
import com.realpersist.gef.part.TablePart;

/**
 * Handles deletion of tables
 * @author Phil Zoio
 */
public class TableEditPolicy extends ComponentEditPolicy
{

	protected Command createDeleteCommand(GroupRequest request)
	{
		TablePart tablePart = (TablePart) getHost();
		Rectangle bounds = tablePart.getFigure().getBounds().getCopy();
		Schema parent = (Schema) (tablePart.getParent().getModel());
		DeleteTableCommand deleteCmd = new DeleteTableCommand();
		deleteCmd.setSchema(parent);
		deleteCmd.setTable((Table) (tablePart.getModel()));
		deleteCmd.setOriginalBounds(bounds);
		return deleteCmd;
	}
	
}