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
package com.windowtester.runtime.swt.internal.widgets;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.*;
import org.eclipse.ui.forms.widgets.*;

/**
 * Provides visitation support to the SWT widget hierarchy.
 */
@SuppressWarnings("deprecation")
public class SWTWidgetHierarchy {

	public static interface IVisitor {

		void visitWidget(Widget widget);
		void visitCaret(Caret widget);

		//- controls
		void visitControl(Control widget);
		void visitButton(Button widget);
		void visitLabel(Label widget);
		void visitLink(Link widget);
		void visitProgressBar(ProgressBar widget);
		void visitSash(Sash widget);
		void visitScale(Scale widget);

		//-- scrollables
		void visitScrollable(Scrollable widget);
		void visitComposite(Composite widget);

		//--- composites
		void visitBrowser(Browser widget);
		//---- canvases
		void visitCanvas(Canvas widget);
		//----- hyperlinks
		void visitHyperlink(Hyperlink widget);
		void visitToggleHyperlink(ToggleHyperlink widget);
		//
		void visitCLabel(CLabel widget);
		//------ decorations
		void visitDecorations(Decorations widget);
		void visitShell(Shell widget);

		//
		//------ expandables
		void visitExpandableComposite(ExpandableComposite widget);
		void visitSection(Section widget);
		//-----
		void visitFormText(FormText widget);
		void visitStyledText(StyledText widget);

		//--- composites
		void visitCBanner(CBanner widget);
		void visitCCombo(CCombo widget);
		void visitCombo(Combo widget);
		void visitCoolBar(CoolBar widget);
		void visitCTabFolder(CTabFolder widget);
		void visitFilteredTree(FilteredTree widget);
		void visitForm(Form widget);
		void visitGroup(Group widget);
		void visitTabFolder(TabFolder widget);
		void visitTable(Table widget);
		void visitTableTree(TableTree widget);
		void visitToolBar(ToolBar widget);
		void visitTree(Tree widget);
		void visitViewForm(ViewForm widget);

		//-- scrollables
		void visitList(List widget);
		void visitText(Text widget);

		//- controls
		void visitSlider(Slider widget);

		//- items
		void visitItem(Item widget);
		void visitCoolItem(CoolItem widget);
		void visitCTabItem(CTabItem widget);
		void visitMenuItem(MenuItem widget);
		void visitTabItem(TabItem widget);
		void visitTableColumn(TableColumn widget);
		void visitTableItem(TableItem widget);
		void visitToolItem(ToolItem widget);
		void visitTrayItem(TrayItem widget);
		void visitTreeColumn(TreeColumn widget);
		void visitTreeItem(TreeItem widget);

		// widgets
		void visitMenu(Menu widget);
		void visitScrollBar(ScrollBar widget);
		void visitToolTip(ToolTip widget);
		void visitTracker(Tracker widget);
		void visitTray(Tray widget);

	}
	
	
	/**
	 * A no-op or fall/through visitor.
	 */
	public static class VisitorAdapter implements IVisitor {

		public void visitBrowser(Browser widget) {
			//no-op
			
		}

		public void visitButton(Button widget) {
			//no-op
			
		}

		public void visitCBanner(CBanner widget) {
			//no-op
			
		}

		public void visitCCombo(CCombo widget) {
			//no-op
			
		}

		public void visitCLabel(CLabel widget) {
			//no-op
			
		}

		public void visitCTabFolder(CTabFolder widget) {
			//no-op
			
		}

		public void visitCTabItem(CTabItem widget) {
			//no-op
			
		}

		public void visitCanvas(Canvas widget) {
			//no-op
			
		}

		public void visitCaret(Caret widget) {
			//no-op
			
		}

		public void visitCombo(Combo widget) {
			//no-op
			
		}

		public void visitComposite(Composite widget) {
			//no-op
			
		}

		public void visitControl(Control widget) {
			//no-op
			
		}

		public void visitCoolBar(CoolBar widget) {
			//no-op
			
		}

		public void visitCoolItem(CoolItem widget) {
			//no-op
			
		}

		public void visitDecorations(Decorations widget) {
			//no-op
			
		}

		public void visitExpandableComposite(ExpandableComposite widget) {
			//no-op
			
		}

		public void visitFilteredTree(FilteredTree widget) {
			//no-op
			
		}

		public void visitForm(Form widget) {
			//no-op
			
		}

		public void visitFormText(FormText widget) {
			//no-op
			
		}

		public void visitGroup(Group widget) {
			//no-op
			
		}

		public void visitHyperlink(Hyperlink widget) {
			//no-op
			
		}

		public void visitItem(Item widget) {
			//no-op
			
		}

		public void visitLabel(Label widget) {
			//no-op
			
		}

		public void visitLink(Link widget) {
			//no-op
			
		}

		public void visitList(List widget) {
			//no-op
			
		}

		public void visitMenu(Menu widget) {
			//no-op
			
		}

		public void visitMenuItem(MenuItem widget) {
			//no-op
			
		}

		public void visitProgressBar(ProgressBar widget) {
			//no-op
			
		}

		public void visitSash(Sash widget) {
			//no-op
			
		}

		public void visitScale(Scale widget) {
			//no-op
			
		}

		public void visitScrollBar(ScrollBar widget) {
			//no-op
			
		}

		public void visitScrollable(Scrollable widget) {
			//no-op
			
		}

		public void visitSection(Section widget) {
			//no-op
			
		}

		public void visitShell(Shell widget) {
			//no-op
			
		}

		public void visitSlider(Slider widget) {
			//no-op
			
		}

		public void visitStyledText(StyledText widget) {
			//no-op
			
		}

		public void visitTabFolder(TabFolder widget) {
			//no-op
			
		}

		public void visitTabItem(TabItem widget) {
			//no-op
			
		}

		public void visitTable(Table widget) {
			//no-op
			
		}

		public void visitTableColumn(TableColumn widget) {
			//no-op
			
		}

		public void visitTableItem(TableItem widget) {
			//no-op
			
		}

		public void visitTableTree(TableTree widget) {
			//no-op
			
		}

		public void visitText(Text widget) {
			//no-op
			
		}

		public void visitToggleHyperlink(ToggleHyperlink widget) {
			//no-op
			
		}

		public void visitToolBar(ToolBar widget) {
			//no-op
			
		}

		public void visitToolItem(ToolItem widget) {
			//no-op
			
		}

		public void visitToolTip(ToolTip widget) {
			//no-op
			
		}

		public void visitTracker(Tracker widget) {
			//no-op
			
		}

		public void visitTray(Tray widget) {
			//no-op
			
		}

		public void visitTrayItem(TrayItem widget) {
			//no-op
			
		}

		public void visitTree(Tree widget) {
			//no-op
			
		}

		public void visitTreeColumn(TreeColumn widget) {
			//no-op
			
		}

		public void visitTreeItem(TreeItem widget) {
			//no-op
			
		}

		public void visitViewForm(ViewForm widget) {
			//no-op
			
		}

		public void visitWidget(Widget widget) {
			//no-op
			
		}

		
	}
	
	
	/**
	 * A hierarchy visitor that does upward chaining to ensure that the most specific
	 * type is communicated to the visitor.
	 */
	public static class Visitor extends VisitorAdapter {
		public void visitWidget(Widget widget){}
		
		@Override
		public void visitCaret(Caret widget){
			visitWidget(widget);
		}
		
		@Override
		public void visitControl(Control widget){}
		@Override
		public void visitButton(Button widget){
			visitControl(widget);
		}
		@Override
		public void visitLabel(Label widget){
			visitControl(widget);
		}
		@Override
		public void visitLink(Link widget){
			visitControl(widget);
		}
		@Override
		public void visitProgressBar(ProgressBar widget){
			visitControl(widget);
		}
		@Override
		public void visitSash(Sash widget){
			visitControl(widget);
		}
		@Override
		public void visitScale(Scale widget){
			visitControl(widget);
		}
		//-- scrollables
		@Override
		public void visitScrollable(Scrollable widget){}
		@Override
		public void visitComposite(Composite widget){
			visitScrollable(widget);
		}
		//--- composites
		@Override
		public void visitBrowser(Browser widget){
			visitComposite(widget);
		}
		//---- canvases
		@Override
		public void visitCanvas(Canvas widget){
			visitComposite(widget);
		}
		//----- hyperlinks
		@Override
		public void visitHyperlink(Hyperlink widget){
			visitCanvas(widget);
		}
		@Override
		public void visitToggleHyperlink(ToggleHyperlink widget){
			visitCanvas(widget);
		}
		//
		@Override
		public void visitCLabel(CLabel widget){
			visitCanvas(widget);
		}
		//------ decorations
		@Override
		public void visitDecorations(Decorations widget){
			visitCanvas(widget);
		}
		@Override
		public void visitShell(Shell widget){
			visitDecorations(widget);
		}
		//
		//------ expandables
		@Override
		public void visitExpandableComposite(ExpandableComposite widget){
			visitCanvas(widget);
		}
		@Override
		public void visitSection(Section widget){
			visitExpandableComposite(widget);
		}
		//-----
		@Override
		public void visitFormText(FormText widget){
			visitCanvas(widget);
		}
		@Override
		public void visitStyledText(StyledText widget){
			visitCanvas(widget);
		}
		
		//--- composites
		@Override
		public void visitCBanner(CBanner widget){
			visitComposite(widget);
		}
		@Override
		public void visitCCombo(CCombo widget){
			visitComposite(widget);
		}
		@Override
		public void visitCombo(Combo widget){
			visitComposite(widget);
		}
		@Override
		public void visitCoolBar(CoolBar widget){
			visitComposite(widget);
		}
		@Override
		public void visitCTabFolder(CTabFolder widget){
			visitComposite(widget);
		}
		@Override
		public void visitFilteredTree(FilteredTree widget){
			visitComposite(widget);
		}
		@Override
		public void visitForm(Form widget){
			visitComposite(widget);
		}
		@Override
		public void visitGroup(Group widget){
			visitComposite(widget);
		}
		@Override
		public void visitTabFolder(TabFolder widget){
			visitComposite(widget);
		}
		@Override
		public void visitTable(Table widget){
			visitComposite(widget);
		}
		@Override
		public void visitTableTree(TableTree widget){
			visitComposite(widget);
		}
		@Override
		public void visitToolBar(ToolBar widget){
			visitComposite(widget);
		}
		@Override
		public void visitTree(Tree widget){
			visitComposite(widget);
		}
		@Override
		public void visitViewForm(ViewForm widget){
			visitComposite(widget);
		}
		
		//-- scrollables
		@Override
		public void visitList(List widget){
			visitScrollable(widget);
		}
		@Override
		public void visitText(Text widget){			
			visitScrollable(widget);
		}
	
		//- controls
		@Override
		public void visitSlider(Slider widget){}

		//- items
		@Override
		public void visitItem(Item widget){
			visitWidget(widget);
		}
		@Override
		public void visitCoolItem(CoolItem widget){
			visitItem(widget);
		}
		@Override
		public void visitCTabItem(CTabItem widget){
			visitItem(widget);
		}
		@Override
		public void visitMenuItem(MenuItem widget){
			visitItem(widget);
		}
		@Override
		public void visitTabItem(TabItem widget){
			visitItem(widget);
		}
		@Override
		public void visitTableColumn(TableColumn widget){
			visitItem(widget);
		}
		@Override
		public void visitTableItem(TableItem widget){
			visitItem(widget);
		}
		@Override
		public void visitToolItem(ToolItem widget){
			visitItem(widget);
		}
		@Override
		public void visitTrayItem(TrayItem widget){
			visitItem(widget);
		}
		@Override
		public void visitTreeColumn(TreeColumn widget){
			visitItem(widget);
		}
		@Override
		public void visitTreeItem(TreeItem widget){
			visitItem(widget);
		}
	
		// widgets
		@Override
		public void visitMenu(Menu widget){
			visitWidget(widget);
		}
		@Override
		public void visitScrollBar(ScrollBar widget){
			visitWidget(widget);
		}
		@Override
		public void visitToolTip(ToolTip widget){
			visitWidget(widget);
		}
		@Override
		public void visitTracker(Tracker widget){
			visitWidget(widget);
		}
		@Override
		public void visitTray(Tray widget){
			visitWidget(widget);
		}
		
	}

	
	public static void accept(Widget widget, IVisitor visitor){
		
		if (widget instanceof Item)
			acceptItemVisitor(widget, visitor);		
		else if (widget instanceof Control) 
			acceptControlVisitor(widget, visitor);
		else if (widget instanceof Menu)
			acceptMenuVisitor(widget, visitor);
		else
			acceptDefaultWidgetVisitor(widget, visitor);
	}
	
	private static void acceptDefaultWidgetVisitor(Widget widget,
			IVisitor visitor) {
		visitor.visitWidget(widget);
	}
	private static void acceptControlVisitor(Widget widget, IVisitor visitor) {
		if (widget instanceof Scrollable)
			acceptScrollableVisitor(widget, visitor);
		else if (widget instanceof Button)
			visitor.visitButton((Button) widget);
		else if (widget instanceof Label)
			visitor.visitLabel((Label) widget);
		else if (widget instanceof Link)
			visitor.visitLink((Link) widget);
		else if (widget instanceof ProgressBar)
			visitor.visitProgressBar((ProgressBar) widget);
		else if (widget instanceof Sash)
			visitor.visitSash((Sash) widget);
		else if (widget instanceof Slider)
			visitor.visitSlider((Slider) widget);
		else
			visitor.visitControl((Control) widget);
		
	}


	private static void acceptScrollableVisitor(Widget widget, IVisitor visitor) {
		if (widget instanceof Composite)
			acceptCompositeVisitor(widget, visitor);
		else if (widget instanceof List)
			visitor.visitList((List) widget);
		else if (widget instanceof Text)
			visitor.visitText((Text) widget);
		else
			visitor.visitScrollable((Scrollable) widget);
	}

	private static void acceptCompositeVisitor(Widget widget, IVisitor visitor) {
		if (widget instanceof Browser)
			visitor.visitBrowser((Browser) widget);
		else if (widget instanceof Canvas)
			acceptCanvasVisitor((Canvas)widget, visitor);
		else if (widget instanceof CBanner)
			visitor.visitCBanner((CBanner) widget);
		else if (widget instanceof CCombo)
			visitor.visitCCombo((CCombo) widget);
		else if (widget instanceof Combo)
			visitor.visitCombo((Combo) widget);
		else if (widget instanceof CoolBar)
			visitor.visitCoolBar((CoolBar) widget);
		else if (widget instanceof CTabFolder)
			visitor.visitCTabFolder((CTabFolder) widget);
		else if (widget instanceof FilteredTree)
			visitor.visitFilteredTree((FilteredTree) widget);
		else if (widget instanceof Form)
			visitor.visitForm((Form) widget);
		else if (widget instanceof Group)
			visitor.visitGroup((Group) widget);
		else if (widget instanceof Sash)
			visitor.visitSash((Sash) widget);
		else if (widget instanceof TabFolder)
			visitor.visitTabFolder((TabFolder) widget);
		else if (widget instanceof Table)
			visitor.visitTable((Table) widget);
		else if (widget instanceof TableTree)
			visitor.visitTableTree((TableTree) widget);
		else if (widget instanceof ToolBar)
			visitor.visitToolBar((ToolBar) widget);
		else if (widget instanceof Tree)
			visitor.visitTree((Tree) widget);
		else if (widget instanceof ViewForm)
			visitor.visitViewForm((ViewForm) widget);
		else
			visitor.visitComposite((Composite) widget);
	}

	private static void acceptCanvasVisitor(Canvas widget, IVisitor visitor) {
		// TODO[pq]: baked in Forms support will get factored out 
		if (widget instanceof Hyperlink)
			visitor.visitHyperlink((Hyperlink)widget);
		else if (widget instanceof StyledText)
			visitor.visitStyledText((StyledText)widget);
		else
			visitor.visitCanvas(widget);
		
	}

	private static void acceptItemVisitor(Widget widget, IVisitor visitor) {
		if (widget instanceof CoolItem)
			visitor.visitCoolItem((CoolItem) widget);
		else if (widget instanceof CTabItem)
			visitor.visitCTabItem((CTabItem) widget);
		else if (widget instanceof MenuItem)
			visitor.visitMenuItem((MenuItem) widget);
		else if (widget instanceof TabItem)
			visitor.visitTabItem((TabItem) widget);
		else if (widget instanceof TableColumn)
			visitor.visitTableColumn((TableColumn) widget);
		else if (widget instanceof TableItem)
			visitor.visitTableItem((TableItem) widget);
		else if (widget instanceof ToolItem)
			visitor.visitToolItem((ToolItem) widget);
		else if (widget instanceof TrayItem)
			visitor.visitTrayItem((TrayItem) widget);
		else if (widget instanceof TreeColumn)
			visitor.visitTreeColumn((TreeColumn) widget);
		else if (widget instanceof TreeItem)
			visitor.visitTreeItem((TreeItem) widget);
		else
			visitor.visitItem((Item) widget);
	}
	
	private static void acceptMenuVisitor(Widget widget, IVisitor visitor) {
		visitor.visitMenu((Menu) widget);
	}
	
}
