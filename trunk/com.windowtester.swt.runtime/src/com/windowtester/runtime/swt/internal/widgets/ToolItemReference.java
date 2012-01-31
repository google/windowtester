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

import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/* $codepro.preprocessor.if version >= 3.3  $ */
import org.eclipse.ui.menus.CommandContributionItem;
/* $codepro.preprocessor.endif $ */


import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;

/**
 * A {@link ToolItem} reference.
 */
public class ToolItemReference extends ItemReference<ToolItem> 
{
	public ToolItemReference(ToolItem item) {
		super(item);
	}

	public boolean isEnabled() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.isEnabled();
			}
		});
	}

	public boolean getEnabled() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.getEnabled();
			}
		});
	}
	
	public boolean getSelection() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.getSelection();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CompositeReference<ToolBar> getParent() {
		return displayRef.execute(new Callable<CompositeReference<ToolBar>>() {
			public CompositeReference<ToolBar> call() throws Exception {
				return new ToolBarReference(widget.getParent());
			}
		});
	}
	
	
	/**
	 * Check for an associated action Id.
	 */
	public String getActionDefinitionId() {
		Object data = getDataDeeply();
		/* $codepro.preprocessor.if version >= 3.3  $ */
		if (data instanceof CommandContributionItem){
			String id = ((CommandContributionItem)data).getId();
			return id;
		}
		/* $codepro.preprocessor.endif $ */
		if (!(data instanceof ActionContributionItem))
			return null;
		ActionContributionItem contrib = (ActionContributionItem)data;
		String id = contrib.getAction().getActionDefinitionId();
		if (id == null)
			id = contrib.getId();
		return id;	
	}
	
	//get data that may be wrapped in a SubContributionItem
	private Object getDataDeeply(){
		Object data = getData();
		if (data instanceof SubContributionItem)
			data = ((SubContributionItem)data).getInnerItem();
		return data;
	}
	
	
	/**
	 * Check for a parameter map (in case the item has an associated
	 * <code>ParameterizedCommand</code>).
	 * @return <code>null</code> if there is no associated {@link CommandContributionItem}, otherwise
	 * it returns its map. 
	 */
	@SuppressWarnings("unchecked")
	public Map getCommandParameterMap(){
		Map map = null;
		/* $codepro.preprocessor.if version >= 3.5  $ */
		Object data = getDataDeeply();
		if (data instanceof CommandContributionItem){
			CommandContributionItem item = (CommandContributionItem)data;
			ParameterizedCommand command = item.getCommand();
			map = command.getParameterMap();
		}
		/* $codepro.preprocessor.endif $ */
		return map;		
	}
	
	

	/* (non-javadoc)
	 * @see ISWTWidgetReference#showPulldownMenu(IClickDescription)
	 */
	public MenuReference showPulldownMenu(IClickDescription click) {
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON1,
			new SWTWidgetLocation<ToolItemReference>(this, WTInternal.RIGHT).offset(-3, 0), false);
		op.execute();
		return op.getMenu();
	}
}
