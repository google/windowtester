/*
 * Created on Jul 15, 2004
 */
package com.realpersist.gef.command;

import org.eclipse.gef.commands.Command;

import com.realpersist.gef.model.Column;
import com.realpersist.gef.model.Table;

/**
 * Command to create a new table
 * 
 * @author Phil Zoio
 */
public class ColumnCreateCommand extends Command
{

	private Column column;
	private Table table;

	public void setColumn(Column column)
	{
		this.column = column;
		this.column.setName("COLUMN " + (table.getColumns().size() + 1));
		this.column.setType(Column.VARCHAR);
	}

	public void setTable(Table table)
	{
		this.table = table;
	}

	public void execute()
	{
		table.addColumn(column);
	}

	public void undo()
	{
		table.removeColumn(column);
	}

}