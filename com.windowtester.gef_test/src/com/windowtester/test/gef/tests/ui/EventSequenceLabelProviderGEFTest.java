package com.windowtester.test.gef.tests.ui;

import org.eclipse.swt.graphics.Image;

import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticDropEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.runtime.gef.internal.locator.FigureLabelProvider;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.test.PDETestCase;
import com.windowtester.ui.internal.corel.model.Event;
import com.windowtester.ui.internal.corel.model.EventSequenceLabelProvider;

import static com.windowtester.runtime.gef.internal.locator.FigureLabelProvider.*;

/**
 * Label provider tests for GEF components.
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class EventSequenceLabelProviderGEFTest extends PDETestCase {

	private static final EventSequenceLabelProvider provider = new EventSequenceLabelProvider();
	
	public void testPaletteItemText() {
		assertEquals("Palette Item: \"foo/bar\" clicked", getText(new PaletteItemLocator("foo/bar")));
	}
	
	public void testFigureClassText() {
		assertEquals("Figure (MyFigure.class) clicked", getText(new FigureClassLocator("MyFigure.class")));
	}
	
//TODO: context clicks	
//
//	public void testFigureClassContextText() {
//		assertEquals("Figure (MyFigure.class) clicked", getText(new FigureClassLocator("MyFigure.class")));
//	}

	
	public void testFigureCanvasText() {
		assertEquals("Figure Canvas clicked", getText(new FigureCanvasLocator()));
	}
	
	public void testFigureCanvasXYText() {
		assertEquals("Figure Canvas clicked", getText(new FigureCanvasXYLocator(3,3)));
	}
	
	public void testFigureCanvasDragToText() {
		assertEquals("Dragged to Figure Canvas", getText(new SemanticDropEvent(SemanticWidgetSelectionEvent.forLocator(new FigureCanvasXYLocator(3,3)))));
	}
	
	
	public void testPaletteItemImage() {
		assertEquals(getExpectedImage(PALETTE_ITEM), getImage(new PaletteItemLocator("foo/bar")));
	}

	public void testFigureClassImage() {
		assertEquals(getExpectedImage(FIGURE), getImage(new FigureClassLocator("MyFigure.class")));
	}

	public void testFigureCanvasImage() {
		assertEquals(getExpectedImage(CANVAS), getImage(new FigureCanvasLocator()));
	}
	
	public void testFigureCanvasXYImage() {
		assertEquals(getExpectedImage(CANVAS), getImage(new FigureCanvasXYLocator(3,3)));
	}

	
	private String getText(IUISemanticEvent semanticEvent) {
		return provider.getText(new Event(semanticEvent));
	}

	private String getText(Event event) {
		return provider.getText(event);
	}
	
	private String getText(ILocator locator) {
		return getText(event(locator));
	}

	private Event event(ILocator locator) {
		return new Event(SemanticWidgetSelectionEvent.forLocator(locator));
	}
	
	private Image getImage(ILocator locator) {
		return provider.getImage(event(locator));
	}
	
	private Image getExpectedImage(String imagePath) {
		return FigureLabelProvider.getImage(imagePath);
	}
	
}
