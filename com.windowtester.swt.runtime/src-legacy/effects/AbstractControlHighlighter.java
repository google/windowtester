/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.internal.effects;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.swt.internal.preferences.ColorManager;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.runtime.swt.internal.selector.HighlightingDriver;

/**
 * An abstract highlighter that drwas a highlighted bounding box around a control target.  To account
 * for clipping issues (due to shading, shadowing and other effects), a fudge factor can be set by implementing
 * getPixelBuffer().
 * 
 * @author Phil Quitslund
 */
public abstract class AbstractControlHighlighter implements IHighlighter {

	/** The color of the highlight -- to be moved to configurable settings */
	//private static final int HIGHLIGHT_COLOR = SWT.COLOR_RED;
	
	/** The control to highlight */ 
	protected final Control _widget;
	
	/** The active display */
	protected final Display _display;
	
	/** The painter for drawing the highlight */
	protected final Painter _painter;

	/** Settings for playback */
	private final PlaybackSettings _settings;
	
	/**
	 * Create an instance.
	 * @param label
	 */
	public AbstractControlHighlighter(Control control, PlaybackSettings settings) {
		_widget  = control;
		_settings = settings;
		_display = control.getDisplay();
		_painter = new Painter();
	}

	/**
	 * Get the number of pixels to buffer the highlighting bounding box to avoid clipping.
	 * @return - the number of pixels  
	 */
	protected abstract int getPixelBuffer();
	
	
	/**
	 * Get playback settings.
	 * @return the current playback settings.
	 */
	protected PlaybackSettings getSettings() {
		return _settings;
	}

	/**
	 * @see com.windowtester.runtime.swt.internal.effects.IHighlighter#doPaint(com.windowtester.runtime.swt.internal.selector.HighlightingDriver)
	 */
	public void doPaint(HighlightingDriver driver) {
		paint();
		driver.highlightPause();
		unPaint();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.effects.IHighlighter#paint()
	 */
	public void paint() {
		//System.out.println("<calling highlight>");
		//needs to happen in the UI thread
		_display.syncExec(new Runnable() {
			public void run() {
				addPaintListener();
				update();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.effects.IHighlighter#unPaint()
	 */
	public void unPaint() {
		//System.out.println("<calling un-highlight>");
		//needs to happen in the UI thread
		_display.syncExec( new Runnable() {
			public void run() {
				removePaintListener();
				update();
			}
		});
	}
	
	/**
	 * Add our custom paint listener to the highlighted control.
	 */
	protected void addPaintListener() {
		_widget.addPaintListener(_painter);
	}
	
	/**
	 * Remove our custom paint listener from the highlighted control.
	 */
	protected void removePaintListener() {
		_widget.removePaintListener(_painter);
	}
		
	/**
	 * Tell the control to update, so out highlighting effect gets drawn.
	 */
	protected void update() {
		_widget.redraw();
		_widget.update();
	}
	
	/**
	 * @return
	 */
	protected Rectangle calculateBoundingBox() {
		Rectangle r = _widget.getBounds();
		return new Rectangle(0,0,r.width-getPixelBuffer(), r.height-getPixelBuffer()); //need to subtract a few pixels, else bottom and right clip
	}

	/**
	 * Action performed before drawing the outline.
	 */
	public void preOutline() {
		//default is a no-op
	}

	/**
	 * Action performed after drawing the outline.
	 */
	public void postOutline() {
		//default is a no-op
	}
		
	/**
	 * A custom paint listener that draws a rectangle highlight around the target control.
	 */
	class Painter implements PaintListener {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		public void paintControl(PaintEvent e) {
			preOutline();
			//System.out.println("<painting highlight>");
			//Rectangle r = _widget.getBounds();
			e.gc.setForeground(getColor());
			Rectangle outlineBounds = calculateBoundingBox(); 
			e.gc.drawRectangle(outlineBounds);
			postOutline();
		}

		private Color getColor() {
			Color color = ColorManager.getDefault().getColor(_settings.getHighlightColor());
			return color;
		}
	}	
}
