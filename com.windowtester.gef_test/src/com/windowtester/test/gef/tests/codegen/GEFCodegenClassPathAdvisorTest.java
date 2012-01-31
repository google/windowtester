package com.windowtester.test.gef.tests.codegen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.windowtester.codegen.util.IBuildPathUpdater;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.provisional.api.AnchorLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.swt.gef.codegen.GEFCodegenerator;

import junit.framework.TestCase;

/**
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFCodegenClassPathAdvisorTest extends TestCase {

	
	private boolean updated;
	
	private final IBuildPathUpdater updater = new IBuildPathUpdater() {
		public void addPluginDependency(String pluginId) throws CoreException {
			updated = true;
		}
	};
	
	private final GEFCodegenerator cg = new GEFCodegenerator();
	
	
	public void testFigureCanvas() throws Exception {
		assertUpdates(new FigureCanvasLocator("foo"));
	}

	public void testFigureCanvasXY() throws Exception {
		assertUpdates(new FigureCanvasXYLocator(1,1));
	}

	public void testPaletteItem() throws Exception {
		assertUpdates(new PaletteItemLocator("foo"));
	}
	
	public void testAnchor() throws Exception {
		assertUpdates(new AnchorLocator(Position.BOTTOM, new FigureClassLocator("foo")));
	}
	
	public void testFigureClass() throws Exception {
		assertUpdates(new FigureClassLocator("foo"));
	}
	
	public void testXYed() throws Exception {
		assertUpdates(new XYLocator(new FigureClassLocator("foo"), 3, 4));		
	}
	
	
	private void assertUpdates(ILocator locator) throws Exception {
		addDepFor(locator);
		assertTrue(updated);
	}
	
	private void addDepFor(ILocator locator) throws Exception {
		List<ISemanticEvent> events = new ArrayList<ISemanticEvent>();
		events.add(fakeSelection(locator));
		cg.addPluginDependencies(events, updater);
	}
	
	
	private SemanticWidgetSelectionEvent fakeSelection(ILocator locator) {
		EventInfo info = new EventInfo();
		info.hierarchyInfo = new IdentifierAdapter(locator);
		return new SemanticWidgetSelectionEvent(info);
	}
	
}
