/*
 * Created on Jul 15, 2004
 */
package com.realpersist.gef.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.realpersist.gef.command.DeleteRelationshipCommand;
import com.realpersist.gef.model.Relationship;
import com.realpersist.gef.model.Table;

/**
 * EditPolicy to handle deletion of relationships
 * @author Phil Zoio
 */
public class RelationshipEditPolicy extends ComponentEditPolicy
{

	protected Command createDeleteCommand(GroupRequest request)
	{
		Relationship relationship = (Relationship) getHost().getModel();
		Table primaryKeyTarget = relationship.getPrimaryKeyTable();
		Table foreignKeySource = relationship.getForeignKeyTable();
		DeleteRelationshipCommand deleteCmd = new DeleteRelationshipCommand(foreignKeySource, primaryKeyTarget, relationship);
		return deleteCmd;
	}
	
}