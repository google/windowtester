package com.realpersist.gef.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GEFPlugin;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SaveAction;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.actions.StackAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.realpersist.gef.action.SchemaContextMenuProvider;
import com.realpersist.gef.model.Schema;

/**
 * Editor implementation based on the the example editor skeleton that is built in <i>Building
 * an editor </i> in chapter <i>Introduction to GEF </i>
 */
public class SchemaDiagramEditor extends GraphicalEditorWithFlyoutPalette
		implements
			CommandStackListener,
			ISelectionListener
{

	private Schema schema;

	/** the undoable <code>IPropertySheetPage</code> */
	private PropertySheetPage undoablePropertySheetPage;

	/** the graphical viewer */
	private GraphicalViewer graphicalViewer;

	/** the list of action ids that are to EditPart actions */
	private List editPartActionIDs = new ArrayList();

	/** the list of action ids that are to CommandStack actions */
	private List stackActionIDs = new ArrayList();

	/** the list of action ids that are editor actions */
	private List editorActionIDs = new ArrayList();

	/** the overview outline page */
	private OverviewOutlinePage overviewOutlinePage;

	/** the editor's action registry */
	private ActionRegistry actionRegistry;

	/** the <code>EditDomain</code> */
	private DefaultEditDomain editDomain;

	/** the dirty state */
	private boolean isDirty;

	/**
	 * No-arg constructor
	 */
	public SchemaDiagramEditor()
	{
		editDomain = new DefaultEditDomain(this);
		setEditDomain(editDomain);
	}

	/**
	 * Initializes the editor.
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		// store site and input
		setSite(site);
		setInput(input);

		// add CommandStackListener
		getCommandStack().addCommandStackListener(this);

		// add selection change listener
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);

		// initialize actions
		createActions();

	}

	/** the selection listener implementation */
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		updateActions(editPartActionIDs);
	}

	/**
	 * The <code>CommandStackListener</code> that listens for
	 * <code>CommandStack </code> changes.
	 */
	public void commandStackChanged(EventObject event)
	{
		updateActions(stackActionIDs);
		setDirty(getCommandStack().isDirty());
	}

	/**
	 * Returns the <code>GraphicalViewer</code> of this editor.
	 * 
	 * @return the <code>GraphicalViewer</code>
	 */
	public GraphicalViewer getGraphicalViewer()
	{
		return graphicalViewer;
	}

	public void dispose()
	{
		// remove CommandStackListener
		getCommandStack().removeCommandStackListener(this);
		// remove selection listener
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		// dispos3 the ActionRegistry (will dispose all actions)
		getActionRegistry().dispose();
		// important: always call super implementation of dispose
		super.dispose();
	}

	/**
	 * Adaptable implementation for Editor
	 */
	public Object getAdapter(Class adapter)
	{
		// we need to handle common GEF elements we created
		if (adapter == GraphicalViewer.class || adapter == EditPartViewer.class)
			return getGraphicalViewer();
		else if (adapter == CommandStack.class)
			return getCommandStack();
		else if (adapter == EditDomain.class)
			return getEditDomain();
		else if (adapter == ActionRegistry.class)
			return getActionRegistry();
		else if (adapter == IPropertySheetPage.class)
			return getPropertySheetPage();
		else if (adapter == IContentOutlinePage.class)
			return getOverviewOutlinePage();

		// the super implementation handles the rest
		return super.getAdapter(adapter);
	}

	/**
	 * Saves the schema model to the file
	 * 
	 * @see EditorPart#doSave
	 */
	public void doSave(IProgressMonitor monitor)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream objectOut = new ObjectOutputStream(out);
			objectOut.writeObject(schema);
			objectOut.close();
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, monitor);
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		getCommandStack().markSaveLocation();
	}

	/**
	 * Save as not allowed
	 */
	public void doSaveAs()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Save as not allowed
	 */
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/**
	 * Indicates if the editor has unsaved changes.
	 * 
	 * @see EditorPart#isDirty
	 */
	public boolean isDirty()
	{
		return isDirty;
	}

	/**
	 * Returns the <code>CommandStack</code> of this editor's
	 * <code>EditDomain</code>.
	 * 
	 * @return the <code>CommandStack</code>
	 */
	public CommandStack getCommandStack()
	{
		return getEditDomain().getCommandStack();
	}

	/**
	 * Returns the schema model associated with the editor
	 * 
	 * @return an instance of <code>Schema</code>
	 */
	public Schema getSchema()
	{
		return schema;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	protected void setInput(IEditorInput input)
	{
		super.setInput(input);

		IFile file = ((IFileEditorInput) input).getFile();
		try
		{
			setPartName(file.getName());
			InputStream is = file.getContents(true);
			ObjectInputStream ois = new ObjectInputStream(is);
			schema = (Schema) ois.readObject();
			ois.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			schema = getContent();
		}
	}

	/**
	 * Creates a PaletteViewerProvider that will be used to create palettes for
	 * the view and the flyout.
	 * 
	 * @return the palette provider
	 */
	protected PaletteViewerProvider createPaletteViewerProvider()
	{
		return new SchemaPaletteViewerProvider(editDomain);
	}
	

	/**
	 * Creates a new <code>GraphicalViewer</code>, configures, registers and
	 * initializes it.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return a new <code>GraphicalViewer</code>
	 */
	protected void createGraphicalViewer(Composite parent)
	{

		IEditorSite editorSite = getEditorSite();
		GraphicalViewer viewer = new GraphicalViewerCreator(editorSite).createViewer(parent);

		GraphicalViewerKeyHandler graphicalViewerKeyHandler = new GraphicalViewerKeyHandler(viewer);
		KeyHandler parentKeyHandler = graphicalViewerKeyHandler.setParent(getCommonKeyHandler());
		viewer.setKeyHandler(parentKeyHandler);

		// hook the viewer into the EditDomain
		getEditDomain().addViewer(viewer);

		// acticate the viewer as selection provider for Eclipse
		getSite().setSelectionProvider(viewer);

		viewer.setContents(schema);

		ContextMenuProvider provider = new SchemaContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(provider);
		getSite().registerContextMenu("com.realpersist.gef.editor.contextmenu", provider, viewer);

		this.graphicalViewer = viewer;

	}

	protected KeyHandler getCommonKeyHandler()
	{

		KeyHandler sharedKeyHandler = new KeyHandler();
		sharedKeyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0), getActionRegistry().getAction(
				GEFActionConstants.DELETE));
		sharedKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0), getActionRegistry().getAction(
				GEFActionConstants.DIRECT_EDIT));

		return sharedKeyHandler;
	}

	/**
	 * Sets the dirty state of this editor.
	 * 
	 * <p>
	 * An event will be fired immediately if the new state is different than the
	 * current one.
	 * 
	 * @param dirty
	 *            the new dirty state to set
	 */
	protected void setDirty(boolean dirty)
	{
		if (isDirty != dirty)
		{
			isDirty = dirty;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	/**
	 * Creates actions and registers them to the ActionRegistry.
	 */
	protected void createActions()
	{
		addStackAction(new UndoAction(this));
		addStackAction(new RedoAction(this));
		addEditPartAction(new DeleteAction((IWorkbenchPart) this));
		addEditorAction(new SaveAction(this));
		addEditorAction(new PrintAction(this));
	}

	/**
	 * Adds an <code>EditPart</code> action to this editor.
	 * 
	 * <p>
	 * <code>EditPart</code> actions are actions that depend and work on the
	 * selected <code>EditPart</code>s.
	 * 
	 * @param action
	 *            the <code>EditPart</code> action
	 */
	protected void addEditPartAction(SelectionAction action)
	{
		getActionRegistry().registerAction(action);
		editPartActionIDs.add(action.getId());
	}

	/**
	 * Adds an <code>CommandStack</code> action to this editor.
	 * 
	 * <p>
	 * <code>CommandStack</code> actions are actions that depend and work on
	 * the <code>CommandStack</code>.
	 * 
	 * @param action
	 *            the <code>CommandStack</code> action
	 */
	protected void addStackAction(StackAction action)
	{
		getActionRegistry().registerAction(action);
		stackActionIDs.add(action.getId());
	}

	/**
	 * Adds an editor action to this editor.
	 * 
	 * <p>
	 * <Editor actions are actions that depend and work on the editor.
	 * 
	 * @param action
	 *            the editor action
	 */
	protected void addEditorAction(WorkbenchPartAction action)
	{
		getActionRegistry().registerAction(action);
		editorActionIDs.add(action.getId());
	}

	/**
	 * Adds an action to this editor's <code>ActionRegistry</code>. (This is
	 * a helper method.)
	 * 
	 * @param action
	 *            the action to add.
	 */
	protected void addAction(IAction action)
	{
		getActionRegistry().registerAction(action);
	}

	/**
	 * Updates the specified actions.
	 * 
	 * @param actionIds
	 *            the list of ids of actions to update
	 */
	protected void updateActions(List actionIds)
	{
		for (Iterator ids = actionIds.iterator(); ids.hasNext();)
		{
			IAction action = getActionRegistry().getAction(ids.next());
			if (null != action && action instanceof UpdateAction)
				((UpdateAction) action).update();

		}
	}

	/**
	 * Returns the action registry of this editor.
	 * 
	 * @return the action registry
	 */
	protected ActionRegistry getActionRegistry()
	{
		if (actionRegistry == null)
			actionRegistry = new ActionRegistry();

		return actionRegistry;
	}

	/**
	 * Returns the overview for the outline view.
	 * 
	 * @return the overview
	 */
	protected OverviewOutlinePage getOverviewOutlinePage()
	{
		if (null == overviewOutlinePage && null != getGraphicalViewer())
		{
			RootEditPart rootEditPart = getGraphicalViewer().getRootEditPart();
			if (rootEditPart instanceof ScalableFreeformRootEditPart)
			{
				overviewOutlinePage = new OverviewOutlinePage((ScalableFreeformRootEditPart) rootEditPart);
			}
		}

		return overviewOutlinePage;
	}

	/**
	 * Returns the undoable <code>PropertySheetPage</code> for this editor.
	 * 
	 * @return the undoable <code>PropertySheetPage</code>
	 */
	protected PropertySheetPage getPropertySheetPage()
	{
		if (null == undoablePropertySheetPage)
		{
			undoablePropertySheetPage = new PropertySheetPage();
			undoablePropertySheetPage.setRootEntry(GEFPlugin.createUndoablePropertySheetEntry(getCommandStack()));
		}

		return undoablePropertySheetPage;
	}

	/*
	 */
	protected void firePropertyChange(int propertyId)
	{
		super.firePropertyChange(propertyId);
		updateActions(editorActionIDs);
	}

	/**
	 * @return the preferences for the Palette Flyout
	 */
	protected FlyoutPreferences getPalettePreferences()
	{
		return new PaletteFlyoutPreferences();
	}

	/**
	 * @return the PaletteRoot to be used with the PaletteViewer
	 */
	protected PaletteRoot getPaletteRoot()
	{
		return new PaletteViewerCreator().createPaletteRoot();
	}

	/**
	 * Returns the content of this editor
	 * 
	 * @return the model object
	 */
	private Schema getContent()
	{
		return new ContentCreator().getContent();
	}

	private static ImageDescriptor create(String iconPath, String name)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.realpersist.gef.schemaeditor", iconPath + name);
	}

	/* $codepro.preprocessor.if version <= 3.1 $ 

	//Necessary for Eclipse 3.1
	protected org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences getPalettePreferences() {
		// TODO what should be returned here?
		return null;
	}
	$codepro.preprocessor.endif $ */
	
	
}