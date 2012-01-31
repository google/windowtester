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
package com.windowtester.swt.gef.codegen;

import java.util.Iterator;
import java.util.List;

import com.windowtester.codegen.assembly.unit.ClassUnit;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.generator.ICodegenAdvisor;
import com.windowtester.codegen.generator.LocatorJavaStringFactory;
import com.windowtester.codegen.generator.PluggableCodeGenerator;
import com.windowtester.codegen.util.IBuildPathUpdater;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.DelegatingLocator;
import com.windowtester.runtime.gef.internal.locator.provisional.api.AnchorLocator;
import com.windowtester.runtime.gef.internal.locator.provisional.api.ResizeHandleLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.IFigureLocator;
import com.windowtester.runtime.gef.locator.LRLocator;
import com.windowtester.runtime.gef.locator.NamedEditPartFigureLocator;
import com.windowtester.runtime.gef.locator.NamedFigureLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.locator.eclipse.ActiveEditorLocator;

/**
 * Codegen support for GEF locators.
 */
public class GEFCodegenerator implements ICodegenAdvisor {

	public static final String GEF_RUNTIME_PLUGIN_ID = "com.windowtester.swt.runtime.gef";
	
	
	//an accessor to upstream codegen functionality
	private class StringFactory extends LocatorJavaStringFactory {
		
		private String figureCanvasXYtoString(FigureCanvasXYLocator locator) {
			StringBuffer sb = new StringBuffer();
			appendCons(sb, locator);
			appendXY(sb, locator);
			optionallyAppendScope(sb, locator);
			appendClose(sb);
			return sb.toString();
		}


		public String figureCanvasToString(FigureCanvasLocator locator) {
			StringBuffer sb = new StringBuffer();
			appendCons(sb, locator);
			IWidgetLocator scope = adaptToScopeLocator(locator);
			//note optionallyAppendScope wrongly prepends a ", " ...
			if (locator != null && !( scope instanceof ActiveEditorLocator))
				sb.append(toJavaString(scope));
			appendClose(sb);
			return sb.toString();
		}
		
		
		private void appendClose(StringBuffer sb) {
			sb.append(")");
		}

		private void appendXY(StringBuffer sb, FigureCanvasXYLocator locator) {
			sb.append(locator.x()).append(", ").append(locator.y());
		}

		public String lrLocatorToString(LRLocator locator) {
			StringBuffer sb = new StringBuffer();
			appendCons(sb, locator);
			appendIndex(sb, locator);
			appendIndexedLocator(sb, locator);
			appendClose(sb);
			return sb.toString();
		}


		//TODO: consider a new interface IPositioningLocator so we can collapse these methods...
		public String anchorLocatorToString(AnchorLocator locator) {
			StringBuffer sb = new StringBuffer();
			appendCons(sb, locator);
			appendPosition(sb, locator);
			appendOwner(sb, locator);
			appendClose(sb);
			return sb.toString();
		}
		
		public String resizeLocatorToString(ResizeHandleLocator locator) {
			StringBuffer sb = new StringBuffer();
			appendCons(sb, locator);
			appendPosition(sb, locator);
			appendOwner(sb, locator);
			appendClose(sb);
			return sb.toString();
		}
		
		public String namedEditPartLocatorToString(NamedEditPartFigureLocator locator) {
			StringBuffer sb = new StringBuffer();
			appendCons(sb, locator);
			sb.append('"' + locator.getEditPartId() + '"');
			appendClose(sb);
			return sb.toString();
		}

		public String namedFigureLocatorToString(NamedFigureLocator locator) {
			StringBuffer sb = new StringBuffer();
			appendCons(sb, locator);
			sb.append('"' + locator.getFigureId() + '"');
			appendClose(sb);
			return sb.toString();
		}
		
		
		private void appendOwner(StringBuffer sb, ResizeHandleLocator locator) {
			sb.append(", ").append(toJavaString(locator.getOwner()));
		}

		private void appendPosition(StringBuffer sb, ResizeHandleLocator locator) {
			sb.append("Position." + locator.getPosition());
		}

		private void appendPosition(StringBuffer sb, AnchorLocator locator) {
			sb.append("Position." + locator.getPosition());
		}

		private void appendOwner(StringBuffer sb, AnchorLocator locator) {
			sb.append(", ").append(toJavaString(locator.getOwner()));
		}

		private void appendIndexedLocator(StringBuffer sb, LRLocator locator) {
			sb.append(toJavaString(locator.getLocator()));
		}

		private void appendIndex(StringBuffer sb, LRLocator locator) {
			sb.append(locator.getIndex()).append(", ");
		}

		public String figureClassToString(FigureClassLocator locator) {
			
			ClassUnit classUnit = getLocatorMap().get(locator);
			if (classUnit == null)
				return null;
			return "new " + classUnit.getName() + "()";
		}

	}
	
	private final StringFactory helper       = new StringFactory();
	private final InnerLocatorMap locatorMap = new InnerLocatorMap();
	
	protected StringFactory getStringFactory() {
		return helper;
	}
	
	protected InnerLocatorMap getLocatorMap() {
		return locatorMap;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.ICodegenParticipant#toJavaString(com.windowtester.runtime.locator.ILocator)
	 */
	public String toJavaString(ILocator locator) {
		if (locator instanceof FigureCanvasXYLocator)
			return getStringFactory().figureCanvasXYtoString((FigureCanvasXYLocator)locator);
		if (locator instanceof FigureCanvasLocator)
			return getStringFactory().figureCanvasToString((FigureCanvasLocator)locator);
		if (locator instanceof LRLocator)
			return getStringFactory().lrLocatorToString((LRLocator)locator);
		if (locator instanceof FigureClassLocator)
			return getStringFactory().figureClassToString((FigureClassLocator)locator);
		if (locator instanceof AnchorLocator)
			return getStringFactory().anchorLocatorToString((AnchorLocator) locator);
		if (locator instanceof ResizeHandleLocator)
			return getStringFactory().resizeLocatorToString((ResizeHandleLocator)locator);
		if (locator instanceof NamedEditPartFigureLocator)
			return getStringFactory().namedEditPartLocatorToString((NamedEditPartFigureLocator)locator);
		if (locator instanceof NamedFigureLocator)
			return getStringFactory().namedFigureLocatorToString((NamedFigureLocator)locator);
		
		return null;
	}


	public void handleSelection(ISemanticEvent event, PluggableCodeGenerator generator, Advice advice) {
		if (!(event instanceof SemanticWidgetSelectionEvent))
			return;
		SemanticWidgetSelectionEvent selection = (SemanticWidgetSelectionEvent)event;
		IWidgetIdentifier locator = selection.getHierarchyInfo();		
		handleLocator(locator, generator, advice);
	}


	private void handleLocator(IWidgetIdentifier locator, PluggableCodeGenerator generator, Advice advice) {
		/*
		 * TODO: this conditional logic is a mess --- this should be revisited and fixed.
		 */
		
		if (locator == null)
			return;
		if (locator instanceof IdentifierAdapter) {
			ILocator adapted = ((IdentifierAdapter)locator).getLocator();
			if (adapted instanceof LRLocator) {
				addImportForClass(generator, LRLocator.class);
				handleLocator(getParent(adapted), generator, advice);
				return;
			}
			if (adapted instanceof FigureClassLocator) {
				handleFigureClassLocator((FigureClassLocator)adapted, generator);
				//advice.override();
				return;
			}
			if (adapted instanceof AnchorLocator) {
				handleAnchorLocator((AnchorLocator)adapted, generator, advice);
			}
			if (adapted instanceof ResizeHandleLocator) {
				handleResizeLocator((ResizeHandleLocator)adapted, generator, advice);
			}
		}
		if (locator instanceof FigureClassLocator) {
			handleFigureClassLocator((FigureClassLocator)locator, generator);
			//advice.override();
		} else {
			handleLocator(getParent(locator), generator, advice);
		}
	}

	private void handleResizeLocator(ResizeHandleLocator locator,
			PluggableCodeGenerator generator, Advice advice) {
		addImportForClass(generator, Position.class);	
		handleLocator(new IdentifierAdapter(locator.getOwner()), generator, advice);
	}

	private void handleAnchorLocator(AnchorLocator locator, PluggableCodeGenerator generator, Advice advice) {
		addImportForClass(generator, Position.class);	
		handleLocator(new IdentifierAdapter(locator.getOwner()), generator, advice);
	}

	private void handleFigureClassLocator(FigureClassLocator locator, PluggableCodeGenerator generator) {
		generator.getTestBuilder().addClass(getLocatorMap().get(locator));
		addImportForClass(generator, FigureClassLocator.class);
	}

	private void addImportForClass(PluggableCodeGenerator generator, Class cls) {
		generator.getTestBuilder().addImport(new ImportUnit(cls.getName()));
	}
	
	private static IWidgetIdentifier getParent(Object locator) {
		if (locator instanceof LRLocator) 
			return new IdentifierAdapter(((LRLocator)locator).getLocator());
		return null;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.ICodegenAdvisor#addPluginDependencies(java.util.List, com.windowtester.codegen.util.IBuildPathUpdater)
	 */
	public void addPluginDependencies(List events, IBuildPathUpdater updater) throws Exception {
		for (Iterator iterator = events.iterator(); iterator.hasNext();) {
			ISemanticEvent event = (ISemanticEvent) iterator.next();
			if (isGEFEvent(event)) {
				updater.addPluginDependency(GEF_RUNTIME_PLUGIN_ID);
				return; //only need to add it once!
			}
		}
	}

	//TODO: verify this...
	private boolean isGEFEvent(ISemanticEvent event) {
		if (!(event instanceof IUISemanticEvent)) 
			return false;
		IUISemanticEvent semantic = (IUISemanticEvent)event;
		IWidgetIdentifier widget = semantic.getHierarchyInfo();		
		return isGEFRelated(widget);
	}

	private boolean isGEFRelated(IWidgetIdentifier widget) {
		//this is a bit clumsy...
		if (widget == null)
			return false;
		if (!(widget instanceof IdentifierAdapter))
			return false;
		ILocator locator = ((IdentifierAdapter)widget).getLocator();
		if (isGEFLocator(locator))
			return true;
		if (locator instanceof XYLocator)
			return isGEFLocator(((XYLocator)locator).locator());
		return false;
	}

	private boolean isGEFLocator(ILocator locator) {
		if (locator instanceof IFigureLocator)
			return true;
		if (locator instanceof FigureCanvasXYLocator)
			return true;
		if (locator instanceof PaletteItemLocator)
			return true;
		if (locator instanceof DelegatingLocator)
			return true;
		return false;
	}
	

}
