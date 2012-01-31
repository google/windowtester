/*
 * Created on Jul 13, 2004
 */
package com.realpersist.gef.layout;

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import com.realpersist.gef.part.SchemaDiagramPart;


/**
 * Uses the DirectedGraphLayoutVisitor to automatically lay out figures on diagram
 * @author Phil Zoio
 */
public class GraphLayoutManager extends AbstractLayout
{

	private SchemaDiagramPart diagram;
	private Map figureToBoundsMap;
	private Map partsToNodeMap;

	public GraphLayoutManager(SchemaDiagramPart diagram)
	{
		this.diagram = diagram;
	}

	
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint)
	{		
		container.validate();
		List children = container.getChildren();
		Rectangle result = new Rectangle().setLocation(container.getClientArea().getLocation());
		for (int i = 0; i < children.size(); i++)
			result.union(((IFigure) children.get(i)).getBounds());
		result.resize(container.getInsets().getWidth(), container.getInsets().getHeight());
		return result.getSize();		
	}

	
	public void layout(IFigure container)
	{

		GraphAnimation.recordInitialState(container);
		if (GraphAnimation.playbackState(container))
			return;
	
		new DirectedGraphLayoutVisitor().layoutDiagram(diagram);
		diagram.setTableModelBounds();

	}
	
}