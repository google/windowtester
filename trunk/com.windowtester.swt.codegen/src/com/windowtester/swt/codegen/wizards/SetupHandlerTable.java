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
package com.windowtester.swt.codegen.wizards;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.generator.SetupHandlerProvider;
import com.windowtester.codegen.generator.SetupHandlers;
import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

/**
 * A Table for presenting setup handlers.
 */
public class SetupHandlerTable {

	
	private class HandlerLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage (Object element, int columnIndex) {
			return null;
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object obj, int index) {
			if (obj instanceof ISetupHandler)
				return getHandlerLabel((ISetupHandler)obj);
			return getText(obj);
		}

		private String getHandlerLabel(ISetupHandler handler) {
			return handler.getDescription();
		}
	}
	
	
	private CheckboxTableViewer viewer;
	private SetupHandlerTableStore store = SetupHandlerTableStore.forDefaultPreferences();
	private SetupHandlerProvider handlers = SetupHandlers.NONE;
	private final Composite parent;

	private SetupHandlerTable(Composite parent) {
        this.parent = parent;
	}

	public SetupHandlerTable build() {
		createViewer();
		setState();
		return this;
	}

	private void setState() {
        if (!store.isActivated())
        	setDefaults();
        else
        	restoreState();
	}
	

	private void restoreState() {
		SetupHandlerSet selected = store.findMatches(getAllHandlers());
		viewer.setCheckedElements(selected.toArray());
	}


	private void createViewer() {
		viewer = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.BORDER | SWT.CHECK);
        Table table = viewer.getTable();
        //TODO: visible or not visible?
        //table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,1));
        viewer.setContentProvider (new ArrayContentProvider());
        viewer.setLabelProvider (new HandlerLabelProvider());
        viewer.setInput(getHandlerInput());
	}


	//the handlers for setting up the viewer
	private Object[] getHandlerInput() {
		return handlers.getHandlers();
	}


	public static SetupHandlerTable forParent(Composite parent) {
		return new SetupHandlerTable(parent);
	}
	
	
	public SetupHandlerTable inContext(ExecutionProfile profile) {
		handlers = SetupHandlers.forContext(profile);
		return this;
	}

	
	public SetupHandlerTable withStore(SetupHandlerTableStore store) {
		this.store = store;
		return this;
	}
	
	
	private ISetupHandler[] getSelection() {
		return (ISetupHandler[]) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				Object[] elements = viewer.getCheckedElements();
				ISetupHandler[] handlers = new ISetupHandler[elements.length];
				for (int i = 0; i < handlers.length; i++) {
					handlers[i] = (ISetupHandler)elements[i];
				}
				return handlers;
			}
		});
	}

	
	public SetupHandlerSet getSelectedHandlers() {
		return SetupHandlerSet.forHandlers(getSelection());
	}
	
	public void setDefaults() {
		viewer.setCheckedElements(handlers.getDefaults());
	}
	
	
	public void persistSelections() {
		store.setSelectedHandlers(getSelection());
	}
	
	
	public ISetupHandler[] getAllHandlers() {
		return (ISetupHandler[]) DisplayExec.sync(new RunnableWithResult() {
			/* (non-Javadoc)
			 * @see com.windowtester.runtime.swt.internal.display.RunnableWithResult#runWithResult()
			 */
			public Object runWithResult() {
		        TableItem[] items = getTable().getItems();
		        ISetupHandler[] handlers = new ISetupHandler[items.length];
		        for (int i = 0; i < items.length; i++) {
		            handlers[i] = (ISetupHandler) items[i].getData();
		        }
		        return handlers;
			}
		});
	}



	private Table getTable() {
		return viewer.getTable();
	}

	
}
