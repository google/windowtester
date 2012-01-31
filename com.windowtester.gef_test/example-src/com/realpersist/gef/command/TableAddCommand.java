/*
 * Created on Jul 15, 2004
 */
package com.realpersist.gef.command;

import org.eclipse.gef.commands.Command;

import com.realpersist.gef.model.Column;
import com.realpersist.gef.model.ColumnType;
import com.realpersist.gef.model.Schema;
import com.realpersist.gef.model.Table;

/**
 * Command to create a new table table
 * 
 * @author Phil Zoio
 */
public class TableAddCommand extends Command
{

	private Schema schema;
	private Table table;
	private int index = -1;

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute()
	{
		this.table.setName("TABLE " + (schema.getTables().size() + 1));
		this.table.setSchema(schema);
		if (table.getColumns().size() < 2)
		{
			Column column1 = new Column("VARCHAR_FIELD", ColumnType.VARCHAR);
			Column column2 = new Column("NUMBER_FIELD", ColumnType.INTEGER);
			table.addColumn(column1);
			table.addColumn(column2);
		}
		schema.addTable(table);
	}

	/**
	 * Sets the index to the passed value
	 * 
	 * @param i
	 *            the index
	 */
	public void setIndex(int i)
	{
		index = i;
	}

	/**
	 * Sets the parent ActivityDiagram
	 * 
	 * @param sa
	 *            the parent
	 */
	public void setSchema(Schema schema)
	{
		this.schema = schema;
	}

	/**
	 * Sets the Activity to create
	 * 
	 * @param table
	 *            the Activity to create
	 */
	public void setTable(Table table)
	{
		this.table = table;
	}

	public void undo()
	{
		schema.removeTable(table);
	}

}