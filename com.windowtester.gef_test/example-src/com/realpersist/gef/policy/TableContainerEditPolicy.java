/*
 * Created on Jul 15, 2004
 */
package com.realpersist.gef.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import com.realpersist.gef.command.ColumnCreateCommand;
import com.realpersist.gef.model.Column;
import com.realpersist.gef.model.Table;
import com.realpersist.gef.part.TablePart;

/**
 * Edit policy for Table as a container, handling functionality for dropping new columns into tables
 * 
 * @author Phil Zoio
 */
public class TableContainerEditPolicy extends ContainerEditPolicy
{

	/**
 * @return command to handle adding a new column
 */
	protected Command getCreateCommand(CreateRequest request)
	{
		Object newObject = request.getNewObject();
		if (!(newObject instanceof Column))
		{
			return null;
		}
		
		TablePart tablePart = (TablePart) getHost();
		Table table = tablePart.getTable();
		Column column = (Column) newObject;
		ColumnCreateCommand command = new ColumnCreateCommand();
		command.setTable(table);
		command.setColumn(column);
		return command;
	}

}