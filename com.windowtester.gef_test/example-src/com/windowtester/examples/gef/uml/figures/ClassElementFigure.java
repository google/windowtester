package com.windowtester.examples.gef.uml.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.windowtester.examples.gef.common.figures.EditableLabel;
import com.windowtester.test.gef.GEFTestPlugin;



/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassElementFigure extends Figure {

	private static final String CLASS_HEADER_FONT_ID = "class-header";
	
	private static FontRegistry REGISTRY;
	
	private final CompartmentFigure _attributeFigure = new CompartmentFigure();
	private final CompartmentFigure _methodFigure = new CompartmentFigure();
	
	private EditableLabel _nameLabel;

	public ClassElementFigure(String name){
	
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(true);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(ColorConstants.yellow);
		setForegroundColor(ColorConstants.black);
		setOpaque(true);
		
		createNameFigure(name);
		
		add(getNameFigure());
		add(getAttributeFigure());
		add(getMethodFigure());
	}


	public EditableLabel getNameFigure() {
		return _nameLabel;
	}
	
	private void createNameFigure(String name) {
		_nameLabel = new EditableLabel(GEFTestPlugin.getDefault().getImage("icons/class.gif"), name);
		_nameLabel.setFont(getClassFont()); //TODO: this font override is not working...
	}

	
	public CompartmentFigure getAttributeFigure() {
		return _attributeFigure;
	}
	
	public CompartmentFigure getMethodFigure() {
		return _methodFigure;
	}
	
	private Font getClassFont() {
		FontRegistry registry = getFonts();
		Font font = registry.get(CLASS_HEADER_FONT_ID);
		if (font == null) {		
			registry.put(CLASS_HEADER_FONT_ID, new FontData[]{new FontData("Arial", 12, SWT.BOLD)} );
			font = registry.get(CLASS_HEADER_FONT_ID);
		}
		return font;
	}

	private static FontRegistry getFonts() {
		if (REGISTRY == null)
			REGISTRY = new FontRegistry(Display.getDefault());
		return REGISTRY;
	}	
}
