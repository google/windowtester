package com.windowtester.examples.gef.uml.figures;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassDiagramFigure extends FreeformLayer {

	public ClassDiagramFigure(){
		setOpaque(true);
		setLayoutManager(new FreeformLayout());
	}

}