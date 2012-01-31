/*
 * Created on Jul 15, 2004
 */
package com.realpersist.gef.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.realpersist.gef.command.DeleteColumnCommand;
import com.realpersist.gef.model.Column;
import com.realpersist.gef.model.Table;

/**
 * Column component EditPolicy - handles column deletion
 * @author Phil Zoio
 */
public class ColumnEditPolicy extends ComponentEditPolicy
{

	protected Command createDeleteCommand(GroupRequest request)
	{
		Table parent = (Table) (getHost().getParent().getModel());
		DeleteColumnCommand deleteCmd = new DeleteColumnCommand();
		deleteCmd.setTable(parent);
		deleteCmd.setColumn((Column) (getHost().getModel()));
		return deleteCmd;
	}
}