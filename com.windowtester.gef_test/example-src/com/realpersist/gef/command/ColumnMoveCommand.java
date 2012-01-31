/*
 * Created on Jul 19, 2004
 */
package com.realpersist.gef.command;

import org.eclipse.gef.commands.Command;

import com.realpersist.gef.model.Column;
import com.realpersist.gef.model.Table;

/**
 * Command to move a column to a different index location within the table
 * 
 * @author Phil Zoio
 */
public class ColumnMoveCommand extends Command
{

	private int oldIndex, newIndex;
	private Column childColumn;
	private Table parentTable;

	public ColumnMoveCommand(Column child, Table parent, int oldIndex, int newIndex)
	{
		this.childColumn = child;
		this.parentTable = parent;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
		if (newIndex > oldIndex)
			newIndex--; //this is because the column is deleted before it is
						// added
	}

	public void execute()
	{
		parentTable.switchColumn(childColumn, newIndex);
	}

	public void undo()
	{
		parentTable.switchColumn(childColumn, oldIndex);
	}

}