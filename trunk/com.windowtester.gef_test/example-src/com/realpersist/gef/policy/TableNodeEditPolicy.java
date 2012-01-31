/*
 * Created on Jul 15, 2004
 */
package com.realpersist.gef.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import com.realpersist.gef.command.ReconnectForeignKeyCommand;
import com.realpersist.gef.command.ReconnectPrimaryKeyCommand;
import com.realpersist.gef.command.RelationshipCreateCommand;
import com.realpersist.gef.model.Relationship;
import com.realpersist.gef.part.TablePart;

/**
 * Handles manipulation of relationships between tables
 * @author Phil Zoio
 */
public class TableNodeEditPolicy extends GraphicalNodeEditPolicy
{

	/**
	 * @see GraphicalNodeEditPolicy#getConnectionCreateCommand(CreateConnectionRequest)
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request)
	{
		RelationshipCreateCommand cmd = new RelationshipCreateCommand();
		TablePart part = (TablePart) getHost();
		cmd.setForeignTable(part.getTable());
		request.setStartCommand(cmd);
		return cmd;
	}

	/**
	 * @see GraphicalNodeEditPolicy#getConnectionCompleteCommand(CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request)
	{
		RelationshipCreateCommand cmd = (RelationshipCreateCommand) request.getStartCommand();
		TablePart part = (TablePart) request.getTargetEditPart();
		cmd.setPrimaryTable(part.getTable());
		return cmd;
	}

	/**
	 * @see GraphicalNodeEditPolicy#getReconnectSourceCommand(ReconnectRequest)
	 */
	protected Command getReconnectSourceCommand(ReconnectRequest request)
	{
		
		ReconnectForeignKeyCommand cmd = new ReconnectForeignKeyCommand();
		cmd.setRelationship((Relationship) request.getConnectionEditPart().getModel());
		TablePart tablePart = (TablePart) getHost();
		cmd.setSourceForeignKey(tablePart.getTable());
		return cmd;
	}

	/**
	 * @see GraphicalNodeEditPolicy#getReconnectTargetCommand(ReconnectRequest)
	 */
	protected Command getReconnectTargetCommand(ReconnectRequest request)
	{
		ReconnectPrimaryKeyCommand cmd = new ReconnectPrimaryKeyCommand();
		cmd.setRelationship((Relationship) request.getConnectionEditPart().getModel());
		TablePart tablePart = (TablePart) getHost();
		cmd.setTargetPrimaryKey(tablePart.getTable());
		return cmd;
	}

}