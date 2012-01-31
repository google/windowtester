/*
 * Created on Jul 13, 2004
 */
package com.realpersist.gef.part.factory;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.realpersist.gef.model.Column;
import com.realpersist.gef.model.Relationship;
import com.realpersist.gef.model.Schema;
import com.realpersist.gef.model.Table;
import com.realpersist.gef.part.ColumnPart;
import com.realpersist.gef.part.RelationshipPart;
import com.realpersist.gef.part.SchemaDiagramPart;
import com.realpersist.gef.part.TablePart;

/**
 * Edit part factory for creating EditPart instances as delegates for model objects
 * 
 * @author Phil Zoio
 */
public class SchemaEditPartFactory implements EditPartFactory
{
	public EditPart createEditPart(EditPart context, Object model)
	{
		EditPart part = null;
		if (model instanceof Schema)
			part = new SchemaDiagramPart();
		else if (model instanceof Table)
			part = new TablePart();
		else if (model instanceof Relationship)
			part = new RelationshipPart();
		else if (model instanceof Column)
			part = new ColumnPart();
		part.setModel(model);
		return part;
	}
}