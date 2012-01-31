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
package com.windowtester.runtime.swt.locator;

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.*;

import java.awt.Point;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.ClickDescription;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.condition.IsSelectedCondition;
import com.windowtester.runtime.locator.IItemLocator;
import com.windowtester.runtime.locator.IPathLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.abbot.TreeItemTester;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.drivers.TreeDriver;
import com.windowtester.runtime.swt.internal.locator.ControlRelativeLocator;
import com.windowtester.runtime.swt.internal.locator.IModifiable;
import com.windowtester.runtime.swt.internal.matchers.SWTMatcherBuilder;
import com.windowtester.runtime.swt.internal.matchers.TreeItemByPathMatcher;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link TreeItem} widgets.
 * <p>
 * Example:
 * <pre>
 * ui.click(new TreeItemLocator("(Simple|General)/Project"));
 * </pre>
 */
public class TreeItemLocator extends ControlRelativeLocator implements IItemLocator, IPathLocator, IModifiable, IsSelected, HasText {

	private static final long serialVersionUID = -571020226870858459L;

	private String fullPath;

	private int selectionModifiers = WT.NONE;

	/** 
	 * Create a locator instance for the common case where no information is needed
	 * to disambiguate the parent control.
	 * <p>
	 * This convenience constructor is equivalent to the following:
	 * <pre>
	 * new TreeItemLocator(itemText, new SWTWidgetLocator(Tree.class));
	 * </pre>
	 * 
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public TreeItemLocator(String fullPath) {
		//notice we're building a parent locator to match the parent tree (since it's implied)
		super(TreeItem.class, new SWTWidgetLocator(Tree.class));
		setPath(fullPath);
	}

	/**
	 * Create a locator instance.
	 * @param modifiers the selection modifiers (e.g., WT.CHECK)
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public TreeItemLocator(int modifiers, String fullPath) {
		//notice we're building a parent locator to match the parent tree (since it's implied)
		super(TreeItem.class, new SWTWidgetLocator(Tree.class));
		setPath(fullPath);
		setSelectionModifiers(modifiers);
	}

	/**
	 * Create a locator instance.
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public TreeItemLocator(String fullPath, SWTWidgetLocator parent) {
		this(fullPath);
		//TODO: notice this || :: ouch! this should be fixed with a common interface
		if (parent instanceof ViewLocator || parent instanceof EditorLocator)
			parent = new SWTWidgetLocator(Tree.class, parent);
		if (parent == null)
			parent = new SWTWidgetLocator(Tree.class);
		setParentInfo(parent);
	}

	/**
	 * Create a locator instance.
	 * @param modifiers the selection modifiers (e.g., WT.CHECK)
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public TreeItemLocator(int modifiers, String fullPath, SWTWidgetLocator parent) {
		super(TreeItem.class, parent);
		setSelectionModifiers(modifiers);
		if (parent instanceof ViewLocator || parent instanceof EditorLocator)
			parent = new SWTWidgetLocator(Tree.class, parent);
		if (parent == null)
			parent = new SWTWidgetLocator(Tree.class);
		setPath(fullPath);
	}
	
	//child
	/**
	 * Create a locator instance.
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public TreeItemLocator(String fullPath, IWidgetLocator parent) {
		super(TreeItem.class, parent);
		if (parent == null)
			setParentInfo(new SWTWidgetLocator(Tree.class));
		setPath(fullPath);
	}

	/**
	 * Create a locator instance.
	 * @param modifiers the selection modifiers (e.g., WT.CHECK)
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public TreeItemLocator(int modifiers, String fullPath, IWidgetLocator parent) {
		super(TreeItem.class, parent);
		setSelectionModifiers(modifiers);
		if (parent == null)
			setParentInfo(new SWTWidgetLocator(Tree.class));
		setPath(fullPath);
	}
	
	//indexed child
	/**
	 * Create a locator instance.
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index the index relative to the parent locator
	 * @param parent the parent locator
	 */
	public TreeItemLocator(String fullPath, int index, IWidgetLocator parent) {
		super(TreeItem.class, index, parent);
		if (parent == null)
			setParentInfo(new SWTWidgetLocator(Tree.class));
		setPath(fullPath);
	}
	
	/**
	 * Create a locator instance.
	 * @param selectionMods the selection modifiers (e.g., WT.CHECK)
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index the index relative to the parent locator
	 * @param parent the parent locator
	 */
	public TreeItemLocator(int modifiers, String fullPath, int index, IWidgetLocator parent) {
		super(TreeItem.class, index, parent);
		setSelectionModifiers(modifiers);
		if (parent == null)
			setParentInfo(new SWTWidgetLocator(Tree.class));
		setPath(fullPath);
	}

	
	public TreeItemLocator in(SWTWidgetLocator parent) {
		return new TreeItemLocator(selectionModifiers, fullPath, parent);
	}

	public TreeItemLocator in(int index, SWTWidgetLocator parent) {
		return new TreeItemLocator(selectionModifiers, fullPath, index, parent);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.ControlRelativeLocator#getControlType()
	 */
	protected Class getControlType() {
		return Tree.class;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		
		Tree tree = (Tree)getControl(ui);
//		Widget clicked = null;
//		int clicks    = click.clicks();
		int modifiers = click.modifierMask();
		/*
		 * add selection modifiers 
		 * For now, only permit checks
		 */
		if (getSelectionModifiers() == WT.CHECK) {
			modifiers = modifiers | getSelectionModifiers();
			click = ClickDescription.copy(click).withModifiers(modifiers);
		}
		
		
		// TODO remove legacy PRE/POST click?
		preClick(tree, ui);
		
		TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), fullPath);
		item.click(click);
				
		postClick(item, ui);
		
		return item;
		
//		Point offset = getOffset(click);
		
//		clicked = new TreeItemSelector2().click(clicks, tree, fullPath, -1 /*no column */, offset, modifiers);
//	
//		postClick(clicked, ui);
//		
//		return WidgetReference.create(clicked, this);	
	}

	protected Point getOffset(IClickDescription click) {
		return (unspecifiedXY(click)) ? null : new Point(click.x(), click.y());
	}
	
	protected void preClick(Tree tree, IUIContext ui) {
		getLegacyUIDriver(ui).mouseMove(tree);
	}
	
	@Override
	protected void postClick(IWidgetReference reference,
			IUIContext ui) {
		getLegacyUIDriver(ui).highlight((Widget) reference.getWidget());
		super.postClick(reference, ui);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget,
			final IClickDescription click, String menuItemPath)
			throws WidgetSearchException {
		
		Tree tree = (Tree) getControl(ui);
		// [Dan] Not sure why widget reference is null, so initialize it
//		widget = new TreeReference(tree);
		
//		TreeReference treeRef = new TreeReference(tree);
//		WidgetPrinter printer = new WidgetPrinter();
//		treeRef.accept(printer);
		//System.out.println(printer.asString());
				
		final TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), fullPath);
		return new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return item.showContextMenu(click);
			}
		}, menuItemPath);
		
//		
//		
//		
//		
//		
////		preClick(tree, ui);
////		Point offset = getOffset(click);
////		Widget clicked = new TreeItemSelector2().contextClick(tree, fullPath, offset, menuItemPath);
//		SWTLocation location = unspecifiedXY(click)
//			? new SWTWidgetLocation((ISWTWidgetReference<?>) widget, WTInternal.TOPLEFT).offset(3, 3)
//			: new SWTWidgetLocation((ISWTWidgetReference<?>) widget, WTInternal.TOPLEFT).offset(click.x(), click.y());
//		MenuItem clicked = new MenuDriver().contextClick(location, menuItemPath).getWidget();
////		postClick(clicked, ui);
//		return WidgetReference.create(clicked, this);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
		//fix for visibility test
	
//		IWidgetMatcher matcher = new AdapterFactory().adapt(new TreeItemByPathMatcher(getPath()));
//		return new CompoundMatcher(matcher, VisibilityMatcher.create(true));
//		return new com.windowtester.runtime.swt.internal.matchers.TreeItemByPathMatcher(getPath()).and(IsVisibleMatcher.forValue(true));
		return SWTMatcherBuilder.buildMatcher(new TreeItemByPathMatcher(getPath()), visible());	
	}
	

	protected void setPath(String path) {
		fullPath = path;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.IModifiable#setSelectionModifiers(int)
	 */
	public void setSelectionModifiers(int modifiers) {
		selectionModifiers = modifiers;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.IModifiable#getSelectionModifiers()
	 */
	public int getSelectionModifiers() {
		return selectionModifiers;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IItemLocator#getPath()
	 */
	public String getPath() {
		return fullPath;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getToStringDetail()
	 */
	protected String getToStringDetail() {
		return "\"" + getPath() + "\"";
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsSelected#isSelected(com.windowtester.runtime.IUIContext)
	 */
	public boolean isSelected(IUIContext ui) throws WidgetSearchException {
		TreeItem item = (TreeItem) ((IWidgetReference)ui.find(this)).getWidget();
		return new TreeItemTester().getChecked(item);	
	}
	
	/**
	 * Create a condition that tests if the given tree item is selected.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isSelected(true)</code>
	 */
	public IUICondition isSelected() {
		return isSelected(true);
	}

	/**
	 * Create a condition that tests if the given tree item is selected.
	 * @param selected 
	 * @param expected <code>true</code> if the tree item is expected to be selected, else
	 *            <code>false</code>
	 */   
	public IUICondition isSelected(boolean selected) {
		return new IsSelectedCondition(this, selected);
	}
	
	/**
	 * Create a condition that tests if the given tree cell has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getText(com.windowtester.runtime.IUIContext)
	 */
	public String getText(IUIContext ui) throws WidgetSearchException {
		IWidgetReference ref = (IWidgetReference) ui.find(this);
		TreeItem item = (TreeItem) ref.getWidget();
		return UIProxy.getText(item, 0);
	}
	
}
