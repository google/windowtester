/*******************************************************************************
 *
 *   Copyright (c) 2012 Google, Inc.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *   Google, Inc. - initial API and implementation
 *  
 *******************************************************************************/

package com.windowtester.example.contactmanager.rcp;


import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ViewPart;

import com.windowtester.example.contactmanager.rcp.action.CopyContactAction;
import com.windowtester.example.contactmanager.rcp.action.CutContactAction;
import com.windowtester.example.contactmanager.rcp.action.DeleteAction;
import com.windowtester.example.contactmanager.rcp.action.NewContactAction;
import com.windowtester.example.contactmanager.rcp.action.PasteContactAction;
import com.windowtester.example.contactmanager.rcp.editor.ContactEditorInput;
import com.windowtester.example.contactmanager.rcp.editor.ContactsEditor;
import com.windowtester.example.contactmanager.rcp.model.Contact;
import com.windowtester.example.contactmanager.rcp.model.ContactsManager;
import com.windowtester.example.contactmanager.rcp.preferences.PreferenceConstants;



public class View extends ViewPart {
	public static final String ID = "com.windowtester.example.contactmanager.rcp.view";

	private TableViewer viewer;
	private CopyContactAction copyAction;
	private CutContactAction cutAction;
	private PasteContactAction pasteAction;
	private DeleteAction removeAction;
	private NewContactAction newContactAction;
	private IWorkbenchAction copyWAction;
	private IWorkbenchAction cutWAction;
	private IWorkbenchAction pasteWAction;
	private IWorkbenchAction deleteAction;
	
	
	private final IPropertyChangeListener propertyChangeListener
	= new IPropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent event){
			if (event.getProperty().equals(
				PreferenceConstants.CONTACTS_DISPLAY_BY__FIRST_NAME))
				viewer.refresh();
		}
};
	

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	public View(){
	};
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ContactsViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(ContactsManager.getManager());
		getSite().setSelectionProvider(viewer);
		viewer.addDoubleClickListener(new IDoubleClickListener(){
			public void doubleClick(org.eclipse.jface.viewers.DoubleClickEvent event){
				try {
					Contact c = null;
					try {
						c = (Contact)((StructuredSelection)event.getSelection()).getFirstElement();
					} catch(Exception e){
						e.printStackTrace();
						c = null;
					}
					IEditorInput input = new ContactEditorInput(c);
					getViewSite().getWorkbenchWindow().getActivePage().openEditor(input,ContactsEditor.ID);
				}catch (Throwable e){
					e.printStackTrace();
				}
				
			}
		});
		createActions();
		createContextMenu();
		hookGlobalActions();
		
		ContactManagerRCPPlugin
			.getDefault()
			.getPreferenceStore()
			.addPropertyChangeListener(propertyChangeListener);
		
	}

	/**
	 *  Create the actions for the view
	 */
	private void createActions(){
		
		copyAction = new CopyContactAction(this,"Copy");
		removeAction = new DeleteAction(getSite().getWorkbenchWindow(),"Delete");
		cutAction = new CutContactAction(copyAction,removeAction,"Cut");
				
		pasteAction = new PasteContactAction(this,"Paste");
		newContactAction = new NewContactAction(
				getSite().getWorkbenchWindow(),"New Contact..."); 
		copyWAction = ActionFactory.COPY.create(getSite().getWorkbenchWindow());
		cutWAction = ActionFactory.CUT.create(getSite().getWorkbenchWindow());
		pasteWAction = ActionFactory.PASTE.create(getSite().getWorkbenchWindow());
		deleteAction = ActionFactory.DELETE.create(getSite().getWorkbenchWindow());
	
	}

	/***
	 * 
	 */
	private void createContextMenu(){
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager m){
				View.this.fillContextMenu(m);
			}
		});
		Menu menu =
			menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr,viewer);
	}
	
	private void fillContextMenu(IMenuManager menuMgr){
		menuMgr.add(
			new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuMgr.add(new Separator());	
		menuMgr.add(newContactAction);
		menuMgr.add(new Separator());	
		menuMgr.add(cutWAction);
		menuMgr.add(copyWAction);
		menuMgr.add(pasteWAction);
		menuMgr.add(new Separator());
		menuMgr.add(deleteAction);
		menuMgr.add(new Separator());
		menuMgr.add(
			new PropertyDialogAction(
				(IShellProvider)this.getViewSite(),viewer));	
		
	}
	
	
	/**
	 *  hook the global cut, copy etc
	 */
	protected void hookGlobalActions(){
		
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.DELETE.getId(),
				removeAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.COPY.getId(),copyAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.CUT.getId(),cutAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.PASTE.getId(),pasteAction);	
	
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}