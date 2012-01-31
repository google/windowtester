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
package com.windowtester.runtime.swt.locator.eclipse;

import java.io.Serializable;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.condition.IsVisible;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.condition.LocatorClosingHandler;
import com.windowtester.runtime.swt.internal.condition.eclipse.EditorCondition;
import com.windowtester.runtime.swt.internal.condition.eclipse.EditorCondition.Active;
import com.windowtester.runtime.swt.internal.condition.eclipse.EditorCondition.Dirty;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;
import com.windowtester.runtime.swt.internal.finder.legacy.InternalMatcherBuilder;
import com.windowtester.runtime.swt.internal.locator.ICloseableLocator;
import com.windowtester.runtime.swt.internal.matchers.eclipse.EditorComponentMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates eclipse workbench <code>Editor</code> parts.
 * <p>
 * Editors are identified by their part name as it shown on the associated 
 * tab (as defined here: {@link IWorkbenchPartReference#getPartName()}.
 * 
 * <pre>
 *   new SWTWidgetLocator(StyledText.class, 
 *                  new EditorLocator("Smoke.java"));
 * </pre>
 * <p>
 * "Smart matching" is used by default to handle the common case where the part name may or may not
 * be prefixed with an asterix ('*') to indicate dirty state. The above locator matches parts named "Smoke.java" 
 * as well as its dirty counterpart "*Smoke.java".
 * <p>
 * These new locators may also be used to click the close "X" of an editor, as in
 * 
 * <pre>
 *   ui.click(new EditorLocator("Smoke.java", WT.CLOSE));
 * </pre>
 */
public class EditorLocator extends SWTWidgetLocator implements IEditorLocator, IsVisible {

	private static class Closer implements ICloseableLocator, Serializable {

		private static final long serialVersionUID = 3502312171322974878L;
		private final transient EditorLocator editorLocator;

		public Closer(EditorLocator editorLocator) {
			this.editorLocator = editorLocator;
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.locator.ICloseableLocator#doClose(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
		 */
		public void doClose(IUIContext ui) throws WidgetSearchException {
			editorLocator.doClose(ui);
		}
		
		
	}
	
	
//TODO: consider a match that takes an IPath...
//  --> this will probably give cause for a pluggable matcher:
//			EditorComponentMatcher.byName()
//         EditorComponentMatcher.byPath()
	
	private static final long serialVersionUID = 8106851292164382419L;
	
	/** The name of the target editor part */
	private final String partName;

	/**
	 * A bit flag indicating how the locator should behave.<br>
	 * Possible values include: <br>
	 * {@link WT#NO_SMART_MATCH} - turn off smart matching as described in
	 * {@link #EditorLocator(String)}<br>
	 * {@link WT#CLOSE} - locate the "X" in the editor locator tab used to close the
	 * editor
	 */
	private final int variation;
	
	/**
	 * Create an instance that locates the given editor by matching the name of 
	 * the part, as it shown on the associated tab (as defined here: {@link IWorkbenchPartReference#getPartName()}.
	 * <p>
	 * By default, "smart matching" of the name is used to handle the common case where the part name may or may not
	 * be prefixed with an asterix ('*') to indicate dirty state.  This constructor is equivalent to
	 * <pre>
	 * EditorLocator("(\\*)?" + partName);
	 * </pre>
	 * If this is not the desired behavior, smart matching can be turned off using the 
	 * two argument constructor {@link #EditorLocator(String, int)}.
	 * <p>
	 * Note also that wild cards are supported in the part name.  In the case where the smart
	 * asterix handling might conflict with a chosen wildcard, prefer the two argument constructor
	 * with smart matching disabled.
	 * 
	 * @param partName the name of the editor to locate
	 *   (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public EditorLocator(String partName) {
		this(partName, WT.NONE);
	}
	
	/**
	 * Create an instance that locates the given editor by matching the name of the part,
	 * as it shown on the associated tab (as defined here:
	 * {@link IWorkbenchPartReference#getPartName()}.
	 * 
	 * @param partName the name of the editor to locate (can be a regular expression as
	 *            described in the {@link StringComparator} utility)
	 * @param variation a bit flag indicating how the locator should behave.<br>
	 *            Possible values include: <br>
	 *            {@link WT#NO_SMART_MATCH} - turn off smart matching as described in
	 *            {@link #EditorLocator(String)}<br>
	 *            {@link WT#CLOSE} - locate the "X" in the editor locator tab used to
	 *            close the editor
	 */
	public EditorLocator(String partName, int variation) {
		super(Control.class);
		this.partName  = partName;
		this.variation = variation;
	}
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "EditorLocator ["+ getPartName() +"]";
	}
	
	/**
	 * Get this editor locator's part name
	 */
	public String getPartName() {
		return partName;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
		String matchString = buildMatchString(getPartName());
		if (isClose())
			return InternalMatcherBuilder.build2(new CTabItemLocator(matchString));
		return new EditorComponentMatcher(matchString);
	}
	
	private String buildMatchString(String partName) {
		return buildMatchString(partName, isSmartMatch());
	}

	private static String buildMatchString(String name, boolean smartMatch) {
		if (!smartMatch)
			return name;
		return "(\\*)?" + name;
	}

	/*
	 * Name is just the part name...
	 */
	public String getNameOrLabel() {
		return getPartName();
	}
	
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click)
		throws WidgetSearchException
	{		
		//NOTE [02.05.2009: close is now done exclusively programmatically
		if (isClose()) {
			doClose(ui);
			return widget; //NOTE: this reference will be invalidated by the close
		} 
		return super.click(ui, widget, click);
	}

	/**
	 * Closes the given editor.
	 * NOTE: the actual close is done asynchronously since dirty editors may
	 * force a prompt blocking the UI thread.
	 */
	private void doClose(IUIContext ui) throws WaitTimedOutException {
		ui.wait(new SWTIdleCondition());
		if (ui.findAll(this).length > 0) {
			final IEditorReference[] references = EditorFinder.findEditors(buildMatchString(getPartName()));
			if (references != null && references.length == 1) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				
				final IEditorPart[] editor = new IEditorPart[1];
				final IWorkbenchPage[] page = new IWorkbenchPage[1]; 
				display.syncExec(new Runnable() {
					public void run() {
						editor[0] = references[0].getEditor(true);
						if (editor != null /*&& !editor.isDirty() */) {
							IWorkbench workbench = PlatformUI.getWorkbench();
							if (workbench != null) {
								IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
								if (window != null) {
									page[0] = window.getActivePage();
								}
							}
						}					
					}
				});
				if (page[0] == null || editor[0] == null)
					return;
				//notice that this is done as an async in case the editor is dirty and forces a prompt
				display.asyncExec(new Runnable() {
					public void run() {
						page[0].closeEditor(editor[0], true);
					}	
				});
			}
		}
	}

	protected final boolean isSmartMatch() {
		return (variation & WT.NO_SMART_MATCH) == 0;
	}

	protected final boolean isClose() {
		return (variation & WT.CLOSE) != 0;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#isVisible(com.windowtester.runtime.IUIContext)
	 */
	public boolean isVisible(IUIContext ui) throws WidgetSearchException {
		return EditorCondition.isVisible(this).test();
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Condition factories
	//
	//////////////////////////////////////////////////////////////////////////////
	
	
	public ICondition isActive() {
		return EditorCondition.isActive(this);
	}
	
	public ICondition isActive(boolean expected) {
		Active active = EditorCondition.isActive(this);
		if (!expected)
			return active.not();
		return active;
	}
	
	public ICondition isDirty() {
		return EditorCondition.isDirty(this);
	}
	
	public ICondition isDirty(boolean expected) {
		Dirty dirty = EditorCondition.isDirty(this);
		if (!expected)
			return dirty.not();
		return dirty;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == ICloseableLocator.class)
			return new Closer(this);
		return super.getAdapter(adapter);
	}

	
	/**
	 * Create a condition handler that ensures that this Editor is closed.
	 * 
	 * @since 5.0.0
	 */
	public IConditionHandler isClosed() {
		return new LocatorClosingHandler(this);
	}
	
}
