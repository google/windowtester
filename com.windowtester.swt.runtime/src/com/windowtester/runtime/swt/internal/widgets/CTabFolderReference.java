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

import java.util.concurrent.Callable;

import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.renderers.swt.StackRenderer;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A {@link CTabFolder} reference.
 */
public class CTabFolderReference extends CompositeReference<CTabFolder> {

	public CTabFolderReference(CTabFolder menu) {
		super(menu);
	}

	/**
	 * Gets the tab item count.
	 * 
	 * @return the number of items in the menu.
	 */
	public int getItemCount() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getItemCount();
			}
		});
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CompositeReference<Composite> getParent() {
		return displayRef.execute(new Callable<CompositeReference<Composite>>() {
			public CompositeReference<Composite> call() throws Exception {
				Composite parent = widget.getParent();
				if (parent == null)
					return null;
				return new CompositeReference<Composite>(parent);
			}
		});
	}
	

	public CTabItemReference[] getItems() {
		return displayRef.execute(new Callable<CTabItemReference[]>() {
			public CTabItemReference[] call() throws Exception {
				return asReferencesOfType(widget.getItems(), CTabItemReference.class);
			}
		});
	}

	/**
	 * Returns "ViewMenu" ToolItemReference <br/>
	 * Works only with Eclipse 4.x!
	 *  
	 * @return
	 */
	public ToolItemReference getViewMenu(){
		return displayRef.execute(new Callable<ToolItemReference>() {
			public ToolItemReference call() throws Exception {
				for(Control control : widget.getChildren()){
					if(control instanceof Composite){
						Composite comp = (Composite) control;
						for(Control compChildren : comp.getChildren()){
							//System.out.println("compChildren: " + compChildren.getClass().getSimpleName() + " " + compChildren.getData());
							if(compChildren instanceof ToolBar){
								ToolBar compChildrenToolBar = (ToolBar) compChildren;
								//Works only with Eclipse 4.4
								if("ViewMenu".equals(compChildrenToolBar.getData())) {
									ToolItem compChildrenToolBarItem = compChildrenToolBar.getItems()[0];
									return asReferenceOfType(compChildrenToolBarItem, ToolItemReference.class);
								}
								
								//Works only with Eclipse 4.x - 4.3
								for(ToolItem compChildrenToolBarItem : compChildrenToolBar.getItems()){
									//System.out.println("compChildrenToolBarItem: " + compChildrenToolBarItem.getClass().getSimpleName() + " " + compChildrenToolBarItem.getData());
									//TODO: Improve check (without internal classes?)
									MMenu mmenu = (MMenu) compChildrenToolBarItem.getData("theMenu");
									if(mmenu != null && mmenu.getTags().contains(StackRenderer.TAG_VIEW_MENU)){
										//System.out.println("View Menu found!");
										return asReferenceOfType(compChildrenToolBarItem, ToolItemReference.class);
									}
								}
							}
						}
					}
				}
				return null;
			}
		});
		
	}

//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#accept(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor)
//	 */
//	@Override
//	public void accept(SWTWidgetReference.Visitor visitor) {
//		visitor.visit(this);
//		visitor.visitEnter(this);
//		for (CTabItemReference item: getItems())
//			item.accept(visitor);
//		for (SWTWidgetReference<?> child: getChildren())
//			child.accept(visitor);
//		visitor.visitLeave(this);
//	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.CompositeReference#setChildren(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.ChildSet)
	 */
	@Override
	protected void setChildren(ChildSet children) {
		children.add(getItems()).add(getChildControls());
	}

	
}
