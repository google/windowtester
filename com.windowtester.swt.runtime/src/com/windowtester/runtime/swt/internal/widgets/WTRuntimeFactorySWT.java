/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc., Phillip Jensen
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *  Phillip Jensen - added visitSlider method
 *******************************************************************************/
package com.windowtester.runtime.swt.internal.widgets;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.windowtester.runtime.internal.factory.WTRuntimeFactory;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * A factory for creating instances of SWT widget references.
 */
public class WTRuntimeFactorySWT implements WTRuntimeFactory
{
	/**
	 * Instantiates a new {@link IWidgetReference} for the specified widget
	 * if possible, or returns <code>null</code> if not.
	 * 
	 * @param widget the widget
	 * @return the widget reference or <code>null</code> if no widget reference can be
	 *         created for the specified widget.
	 */
	public IWidgetReference createReference(Object widget) {
		if (widget instanceof Widget)
			return new ReferenceMapper((Widget) widget).findReference();
		return null;
	}
	
	public class ReferenceMapper extends SWTWidgetHierarchy.Visitor {
		
		private ISWTWidgetReference<?> reference;
		private final Widget widget;
		
		ReferenceMapper(Widget widget){
			this.widget = widget;
		}
		
		public ISWTWidgetReference<?> getReference() {
			return reference;
		}
		
		@Override
		public void visitButton(Button widget) {
			reference = new ButtonReference(widget);
		}
		
		@Override
		public void visitControl(Control widget) {
			reference = new ControlReference<Control>(widget);
		}
		
		@Override
		public void visitDecorations(Decorations widget) {
			reference = new DecorationsReference<Decorations>(widget);
		}
		
		@Override
		public void visitShell(Shell widget) {
			reference = new ShellReference(widget);
		}
		
		@Override
		public void visitItem(Item widget) {
			reference = new ItemReference<Item>(widget);
		}

		@Override
		public void visitCCombo(CCombo widget) {
			reference = new CComboReference(widget);
		}
		
		@Override
		public void visitCombo(Combo widget) {
			reference = new ComboReference(widget);
		}

		@Override
		public void visitCoolBar(CoolBar widget) {
			reference = new CoolBarReference(widget);
		}
		
		@Override
		public void visitComposite(Composite widget) {
			reference = new CompositeReference<Composite>(widget);
		}
		
		@Override
		public void visitList(List widget) {
			reference = new ListReference(widget);
		}
		
		@Override
		public void visitMenu(Menu widget) {
			reference = new MenuReference(widget);
		}
		
		@Override
		public void visitMenuItem(MenuItem widget) {
			reference = new MenuItemReference(widget);
		}
		
		@Override
		public void visitScrollable(Scrollable widget) {
			reference = new ScrollableReference<Scrollable>(widget);
		}
		
		@Override
		public void visitScrollBar(ScrollBar widget) {
			reference = new ScrollBarReference(widget);
		}

		@Override
		public void visitToolBar(ToolBar widget) {
			reference = new ToolBarReference(widget);
		}
		
		@Override
		public void visitToolItem(ToolItem widget) {
			reference = new ToolItemReference(widget);
		}
		
		@Override
		public void visitCTabFolder(CTabFolder widget) {
			reference = new CTabFolderReference(widget);
		}
		
		@Override
		public void visitTable(Table widget) {
			reference = new TableReference(widget);
		}
		
		@Override
		public void visitTableItem(TableItem widget) {
			reference = new TableItemReference(widget);
		}
		
		@Override
		public void visitTableColumn(TableColumn widget) {
			reference = new TableColumnReference(widget);
		}
		
		@Override
		public void visitTabFolder(TabFolder widget) {
			reference = new TabFolderReference(widget);
		}
		
		@Override
		public void visitTreeItem(TreeItem widget) {
			reference = new TreeItemReference(widget);
		}
		
		@Override
		public void visitCoolItem(CoolItem widget) {
			reference = new CoolItemReference(widget);
		}
		
		@Override
		public void visitTabItem(TabItem widget) {
			reference = createTabItemReference(widget);
		}
		
		@Override
		public void visitCTabItem(CTabItem widget) {
			reference = new CTabItemReference(widget);
		}
		
		@Override
		public void visitTree(Tree widget) {
			reference = new TreeReference(widget);
		}
		
		@Override
		public void visitWidget(Widget widget) {
			reference = new SWTWidgetReference<Widget>(widget);
		}
		
		@Override
		public void visitCaret(Caret widget) {
			reference = new CaretReference(widget);		
		}
		
		@Override
		public void visitHyperlink(Hyperlink widget) {
//			reference = new HyperlinkControlReference(widget);
			// TODO[pq]: this will get upgraded to a hyperlink control 
			reference = new CanvasReference<Hyperlink>(widget);

		}

		@Override
		public void visitSlider(Slider widget) {
			reference = new SliderReference(widget);
		}

		@Override
		public void visitStyledText(StyledText widget) {
			reference = new StyledTextReference(widget);
		}

		public ISWTWidgetReference<?> findReference() {
			SWTWidgetHierarchy.accept(widget, this);
			return getReference();
		}
	}

	protected TabItemReference createTabItemReference(TabItem widget) {
		return new TabItemReference(widget);
	}
}
