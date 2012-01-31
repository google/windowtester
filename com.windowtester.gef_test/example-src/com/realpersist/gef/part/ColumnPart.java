/*
 * Created on Jul 14, 2004
 */
package com.realpersist.gef.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;

import com.realpersist.gef.directedit.ColumnNameTypeCellEditorValidator;
import com.realpersist.gef.directedit.ExtendedDirectEditManager;
import com.realpersist.gef.directedit.LabelCellEditorLocator;
import com.realpersist.gef.directedit.ValidationMessageHandler;
import com.realpersist.gef.editor.ValidationEnabledGraphicalViewer;
import com.realpersist.gef.figures.EditableLabel;
import com.realpersist.gef.model.Column;
import com.realpersist.gef.policy.ColumnDirectEditPolicy;
import com.realpersist.gef.policy.ColumnEditPolicy;

/**
 * Represents an editable Column object in the model
 * @author Phil Zoio
 */
public class ColumnPart extends PropertyAwarePart
{

	protected DirectEditManager manager;

	/**
	 * @return the ColumnLabel representing the Column
	 */
	protected IFigure createFigure()
	{
		Column column = (Column) getModel();
		String label = column.getLabelText();
		EditableLabel columnLabel = new EditableLabel(label);
		return columnLabel;
	}

	/**
	 * Creats EditPolicies for the column label
	 */
	protected void createEditPolicies()
	{
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ColumnEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ColumnDirectEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
	}

	public void performRequest(Request request)
	{
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
		{
			if (request instanceof DirectEditRequest
					&& !directEditHitTest(((DirectEditRequest) request).getLocation().getCopy()))
				return;
			performDirectEdit();
		}
	}

	private boolean directEditHitTest(Point requestLoc)
	{
		IFigure figure = getFigure();
		figure.translateToRelative(requestLoc);
		if (figure.containsPoint(requestLoc))
			return true;
		return false;
	}

	protected void performDirectEdit()
	{
		if (manager == null)
		{
			ValidationEnabledGraphicalViewer viewer = (ValidationEnabledGraphicalViewer) getViewer();
			ValidationMessageHandler handler = viewer.getValidationHandler();

			Label l = (Label) getFigure();
			ColumnNameTypeCellEditorValidator columnNameTypeCellEditorValidator = new ColumnNameTypeCellEditorValidator(
					handler);

			manager = new ExtendedDirectEditManager(this, TextCellEditor.class, new LabelCellEditorLocator(l), l,
					columnNameTypeCellEditorValidator);
		}
		manager.show();
	}

	/**
	 * Sets the width of the line when selected
	 */
	public void setSelected(int value)
	{
		super.setSelected(value);
		EditableLabel columnLabel = (EditableLabel) getFigure();
		if (value != EditPart.SELECTED_NONE)
			columnLabel.setSelected(true);
		else
			columnLabel.setSelected(false);
		columnLabel.repaint();
	}

	/**
	 * @param Handles
	 *            name change during direct edit
	 */
	public void handleNameChange(String textValue)
	{
		EditableLabel label = (EditableLabel) getFigure();
		label.setVisible(false);
		setSelected(EditPart.SELECTED_NONE);
		label.revalidate();
	}

	/**
	 * Handles when successfully applying direct edit
	 */
	protected void commitNameChange(PropertyChangeEvent evt)
	{
		EditableLabel label = (EditableLabel) getFigure();
		label.setText(getColumn().getLabelText());
		setSelected(EditPart.SELECTED_PRIMARY);
		label.revalidate();
	}


	/**
	 * Reverts state back to prior edit state
	 */
	public void revertNameChange(String oldValue)
	{
		EditableLabel label = (EditableLabel) getFigure();
		label.setVisible(true);
		setSelected(EditPart.SELECTED_PRIMARY);
		label.revalidate();
	}

	/**
	 * We don't need to explicitly handle refresh visuals because the times when
	 * this needs to be done it is handled by the table e.g. handleNameChange()
	 */
	protected void refreshVisuals()
	{
		Column column = (Column) getModel();
		EditableLabel columnLabel = (EditableLabel) getFigure();
		columnLabel.setText(column.getLabelText());
	}
	
	

	private Column getColumn()
	{
		return (Column) getModel();
	}
	
	

}