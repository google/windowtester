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
package com.windowtester.eclipse.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.UIPlugin;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.WTUI;
import com.windowtester.eclipse.ui.actions.LaunchRecorderViewAction;
import com.windowtester.eclipse.ui.layout.CardLayout;
import com.windowtester.eclipse.ui.session.ISession;
import com.windowtester.eclipse.ui.session.ISessionMonitor;
import com.windowtester.eclipse.ui.usage.ProfiledAction;
import com.windowtester.eclipse.ui.usage.ProfiledDelegateAction;
import com.windowtester.eclipse.ui.viewers.EventSequenceTreeViewer;
import com.windowtester.eclipse.ui.views.DashboardController.IRecorderDashActionProvider;
import com.windowtester.recorder.ui.EventSequenceModel;
import com.windowtester.recorder.ui.IEventSequenceView;
import com.windowtester.recorder.ui.IRecorderConsoleActionHandler;
import com.windowtester.recorder.ui.IRecorderPanelView;
import com.windowtester.recorder.ui.RecorderPanelModel;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.ui.core.model.ISemanticEvent;

/**
 * View that presents recorded events.
 */
@SuppressWarnings("restriction")
public class RecorderConsoleView extends ViewPart implements IRecorderPanelView, IEventSequenceView, IRecorderDashActionProvider, IShellProvider  {

	private static final boolean INIT_WITH_STUB_EVENTS = false;
	
	public static final String MENU_MANAGER_NAME = "consoleMenuManager";
	
	
	//TODO: move me! (used to id tool item contribs to be ignored in recording (temp))
	public static final String ACTION_TAG_PREFIX = "com.windowtester.util.ui.views.recorder.actions.";
	
	
	private static final String HELP_CONTEXT_ID = "com.windowtester.eclipse.help.recording_console";

	
	/**
	 * The id used to display the event tree in the console panel.
	 */
	private static final String EVENT_TREE_PANE_ID = "eventTree";

	
	private static class ContextMenuAction extends ProfiledDelegateAction {
		public ContextMenuAction(Action action) {
			super(action, "contextMenu");
		}	
	}
	
	
	//the presenter
	private RecorderConsolePresenter presenter;

	//actions/buttons
	private Action recordAction;
	private Action pauseAction;
	private Action codegenAction;
	private Action deleteAction;
	private Action hookAction;
	private Action spyAction;
	private LaunchRecorderViewAction launchAction;
	
	private EventSequenceTreeViewer treeViewer;
	
	private EmptyRecorderConsoleControl emptyConsoleViewer;;
	
	private AssertionHookActionHandler hookHandler;

	private IDashboardController dashController;

	private Composite consoleArea;

	private CardLayout consoleAreaLayout;	
	
	
	private class EmptyEventTreeStateUpdater {

		public void update() {		
			DisplayExec.sync(new Runnable(){
				public void run() {
					if (inEmptyState()) {
						showEmptyConsolePane();	
					} else {
						showEventTreePane();
					}
					updateContentDescription();
				}
			});
		}

		private boolean inEmptyState() {
			return !getSessionMonitor().inSession() && getViewer().getSequence().isEmpty();
		}
	}
	
	
	private final EmptyEventTreeStateUpdater emptyStateUpdater = new EmptyEventTreeStateUpdater();


	public RecorderConsoleView() {
		//NOTE: presenter and controller set lazily
	}

	
	public IDashboardController getDashController() {
		if (dashController == null)
			dashController = new DashboardController(this);
		return dashController;
	}
	
	public RecorderConsolePresenter getPresenter() {
		if (presenter == null)
			presenter = new RecorderConsolePresenter(new RecorderPanelModel(), this, new EventSequenceModel(), this);
		return presenter;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		
		addToolItems();
		createConsoleArea(parent);
		createActionBarMenu();
		
		if (INIT_WITH_STUB_EVENTS)
			addStubEvents();
		
		updateContentDescription();
		
		hookupDynamicHelp(parent);
		addSessionProgressFeedback();
		update();
		
	}






	private void createConsoleArea(Composite parent) {
		emptyConsoleViewer = new EmptyRecorderConsoleControl(launchAction);
		
		consoleArea = new Composite(parent, SWT.FILL);
		consoleAreaLayout = new CardLayout(consoleArea);
		consoleArea.setLayout(consoleAreaLayout);

		createEventTreePane(consoleArea);
		createEmptyResultPane(consoleArea);
		
		consoleAreaLayout.show(emptyConsoleViewer.getId());
	}

	private void createEmptyResultPane(Composite parent) {
		emptyConsoleViewer.createControl(parent);
	}


	private void createEventTreePane(Composite parent) {
		addTree(parent);
		addContextMenus();
	}

	

	private void addSessionProgressFeedback() {
		/* $if eclipse.version >= 3.3 $ */		
		getSessionMonitor().addListener(new ISessionMonitor.ISessionListener() {
			public void started(ISession session) {
				getProgressService().incrementBusy();
			}			
			public void ended(ISession session) {
				getProgressService().decrementBusy();
			}
		});
		/* $endif $ */
	}


	protected ISessionMonitor getSessionMonitor() {
		return UiPlugin.getDefault().getSessionMonitor();
	}
	
	
	protected void showEmptyResultPage() {
		
		
	}
	
	private void createActionBarMenu() {
		IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager menuManager = actionBars.getMenuManager();
        menuManager.add(new OpenWTPreferencesPageAction(this));
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.IShellProvider#getShell()
	 */
	public Shell getShell() {
		return getSite().getShell();
	}


	private void hookupDynamicHelp(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID); 
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		getPresenter().dispose();
		super.dispose();
	}
	
	
	private void addTree(Composite parent) {
		//this is not quite right -- this wiring of model to view should happen
		//in the presenter...
		treeViewer = new EventSequenceTreeViewer(parent, getPresenter().getSequenceModel());
		Tree tree = treeViewer.getTreeViewer().getTree();
		tree.setLayoutData(EVENT_TREE_PANE_ID);		
		tree.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				//no-op
			}
			public void keyReleased(KeyEvent e) {
				if (e.character == WT.DEL || e.character == WT.BS) {
					deleteAction.run();
				}
			}
		});
	}

	
	IWorkbenchSiteProgressService getProgressService() {
		IWorkbenchSiteProgressService progressService = null;
		Object siteService = getSite().getAdapter(IWorkbenchSiteProgressService.class);
		if (siteService != null)
			progressService = (IWorkbenchSiteProgressService) siteService;
		return progressService;
	}
	
	//for debuggging
	private void addStubEvents() {
		getPresenter().getSequenceModel().add(
					FakeEventFactory.fakeSelectEvent(MenuItem.class, new MenuItemLocator("File/New"))).add(
					FakeEventFactory.fakeSelectEvent(Button.class, new ButtonLocator("New"))).add(
					FakeEventFactory.fakeSelectEvent(Button.class, new MenuItemLocator("Cancel"))).add(
					FakeEventFactory.fakeKeyEntry('x')).add(
					FakeEventFactory.fakeKeyEntry('y')).add(
					FakeEventFactory.fakeKeyEntry('z')).add(				
					FakeEventFactory.fakeSelectEvent(Button.class, new MenuItemLocator("Cancel"))
		);
		
	}


	private void addContextMenus() {
		
		final TreeViewer viewer = treeViewer.getTreeViewer();
		
		MenuManager mm = new MenuManager(MENU_MANAGER_NAME);
		mm.setRemoveAllWhenShown(true);
		mm.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {	
				if (viewer == null)
					return;
				Tree tree = viewer.getTree();
				if (tree == null)
					return;
				final TreeItem[] selection = tree.getSelection();
				if (selection.length == 0) {
					return;
				}
				manager.add(new ContextMenuAction(deleteAction));
				
//				manager.add(new Action("Group") {
//					public void run() {
//						super.run();
//						System.out.println("DO group!");
//
//						ISemanticEvent[] selected = getSelection();
//						IEvent[] events = new IEvent[selected.length];
//						for (int i = 0; i < selection.length; i++) {
//							events[i] = (IEvent)selected[i];
//						}
//						IEventGroup group = getPresenter().group(events);						
//						getViewer().setGroupedState(group);
//
//					}
//				});
			}
		});
        mm.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
		Control control = viewer.getControl();
		Menu menu = mm.createContextMenu(control);
		control.setMenu(menu);
		//re-enable if we want to accept contributions
//		getSite().registerContextMenu(mm, viewer);
	}
	
	
	public EventSequenceTreeViewer getViewer() {
		return treeViewer;
	}
	
	private void update() {
		getPresenter().update();
	}

	private void addToolItems() {
		
		addRecordItem();
		addPauseItem();
		
		//NOTE: disabled item for now (optionally enabled via pref)
		addSpyItem();
		
		addHookItem();
		addDeleteItem();
		
		addToolItemSeparator();
		addCodegenItem();
		
		addToolItemSeparator();
		addLaunchItem();
		
		getViewSite().getActionBars().updateActionBars();
		
	}


	private void addHookItem() {
		hookAction = new ProfiledAction("Add Assertion Hook...") {
			public void doRun() {
				clickAddHook();
			}
		};
		hookAction.setImageDescriptor(imageDescriptor("assertion_hook.gif"));
		hookAction.setDisabledImageDescriptor(imageDescriptor("assertion_hook_dis.gif"));
		hookAction.setId(actionTag("hook"));
		addToolItemAction(hookAction);
	}


	private void addCodegenItem() {
		codegenAction = new ProfiledAction("Generate Test...") {
			public void doRun() {
				clickCodegen();
			}
		};
		codegenAction.setImageDescriptor(imageDescriptor("codegen_gears.gif"));
		codegenAction.setDisabledImageDescriptor(imageDescriptor("codegen_gears_dis.gif"));
		codegenAction.setId(actionTag("codegen"));
		addToolItemAction(codegenAction);
	}



	private void addDeleteItem() {
		deleteAction = new ProfiledAction("Delete") {
			public void doRun() {
				clickDelete();
			}
		};
		deleteAction.setText("Delete");
		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		deleteAction.setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		deleteAction.setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		deleteAction.setId(ACTION_TAG_PREFIX + "delete");
		addToolItemAction(deleteAction);
	}


	private void addPauseItem() {
		pauseAction = new ProfiledAction("Pause") {
			public void doRun() {
				clickPause();
			}
		};
		pauseAction.setImageDescriptor(imageDescriptor("pause.gif"));
		pauseAction.setDisabledImageDescriptor(imageDescriptor("pause_dis.gif"));
		pauseAction.setId(actionTag("pause"));
		addToolItemAction(pauseAction);
	}


	private void addRecordItem() {
		recordAction = new ProfiledAction("Record") {
			public void doRun() {
				clickRecord();
			}
		};
		recordAction.setImageDescriptor(imageDescriptor("start_recording.gif"));
		recordAction.setDisabledImageDescriptor(imageDescriptor("start_recording_dis.gif"));
		recordAction.setId(actionTag("record"));
		addToolItemAction(recordAction);
	}

	
	private void addSpyItem() {
		spyAction = new ProfiledAction("Inspect" /*, SWT.TOGGLE */) {
			public void doRun() {
				clickSpyMode();
			}
		};
		spyAction.setImageDescriptor(imageDescriptor("spy.gif"));
		spyAction.setDisabledImageDescriptor(imageDescriptor("spy_dis.gif"));
		spyAction.setId(actionTag("spy"));
		//TODO: enable when this is more backed --- can be enabled for the remote via a preference
		//addToolItemAction(spyAction);
	}
	
	
	private void addLaunchItem() {
		launchAction = new LaunchRecorderViewAction(imageDescriptor("manual_run_wiz.gif"));
		launchAction.init(getSite().getWorkbenchWindow());
		launchAction.setId(actionTag("launchRecorder"));
		addToolItemAction(launchAction);
	}
	
	private String actionTag(String id) {
		return ACTION_TAG_PREFIX + id;
	}

	private void addToolItemAction(Action action) {
		IToolBarManager tm = getViewSite().getActionBars().getToolBarManager();
		tm.add(action);
	}
	
	private void addToolItemSeparator() {
		IToolBarManager tm = getViewSite().getActionBars().getToolBarManager();
		tm.add(new Separator());
	}
	
	private ImageDescriptor imageDescriptor(String imageFilePath) {
		return UIPlugin.imageDescriptorFromPlugin(UiPlugin.PLUGIN_ID, "icons/full/obj16/" + imageFilePath);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	
	public void addHandler(IRecorderConsoleActionHandler listener) {
		getPresenter().addHandler(listener);
	}
	
	public void removeHandler(IRecorderConsoleActionHandler listener) {
		getPresenter().removeHandler(listener);
	}
	
	///////////////////////////////////////////////////////////////////////
	//
	// Selection (click) actions
	//
	///////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderActions#clickPause()
	 */
	public void clickPause() {
		getPresenter().clickPause();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderActions#clickRecord()
	 */
	public void clickRecord() {
		getPresenter().clickRecord();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderActions#clickRestart()
	 */
	public void clickRestart() {
		getPresenter().clickRestart();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderActions#clickDelete()
	 */
	public void clickDelete() {
		getPresenter().clickDelete();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceView#clickCodegen()
	 */
	public void clickCodegen() {
		getPresenter().clickCodegen();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderConsoleActionHandler#clickAddHook()
	 */
	public void clickAddHook() {
		//TODO: and here our neat separation of concerns breaks...  (can we restore it?)
		/*
		 * rather than delegating to the presenter, we're hooking in dreictly here (ugh
		 */
		
		getAssertionHookHandler().addAssertion();
		//still call presenter to make sure updates get fired
		getPresenter().clickAddHook();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderConsoleActionHandler#clickSpyMode()
	 */
	public void clickSpyMode() {
		getPresenter().clickSpyMode();
	}
	
	///////////////////////////////////////////////////////////////////////
	//
	// UI enablement
	//
	///////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelView#setDeleteEnabled(boolean)
	 */
	public void setDeleteEnabled(boolean isEnabled) {
		deleteAction.setEnabled(isEnabled);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelView#setPauseEnabled(boolean)
	 */
	public void setPauseEnabled(boolean isEnabled) {
		pauseAction.setEnabled(isEnabled);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IRecorderPanelView#setRecordEnabled(boolean)
	 */
	public void setRecordEnabled(boolean isEnabled) {
		recordAction.setEnabled(isEnabled);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceView#setCodegenEnabled(boolean)
	 */
	public void setCodegenEnabled(boolean enabled) {
		codegenAction.setEnabled(enabled);
	}


	/**
	 * Accessor to surface events in the tree viewer.
	 */
	public ISemanticEvent[] getEvents() {
		return getViewer().getSequence().getEvents();
	}

	/**
	 * Get the current sequence selection.
	 */
	public ISemanticEvent[] getSelection() {
		return getViewer().getSelection();
	}
	
	
	
	public AssertionHookActionHandler getAssertionHookHandler() {
		if (hookHandler == null)
			hookHandler = new AssertionHookActionHandler(getPresenter().getSequenceModel());
		return hookHandler;
	}
	
	int refreshed = 0;
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceView#refresh()
	 */
	public void refresh() {
		getViewer().refresh();
		emptyStateUpdater.update();

	}

	private void updateContentDescription() {
		setContentDescription(SessionSummaryLabelProvider.getSummary(this));
	}



	private void showEventTreePane() {
		consoleAreaLayout.show(EVENT_TREE_PANE_ID);
	}


	private void showEmptyConsolePane() {
		emptyConsoleViewer.aboutToShow();
		consoleAreaLayout.show(emptyConsoleViewer.getId());
	}

	
	

	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.views.DashboardController.IRecorderDashActionProvider#getActions()
	 */
	public IAction[] getActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(recordAction);
		actions.add(pauseAction);
		if (WTUI.isInspectorEnabled())
			actions.add(spyAction);
		actions.add(hookAction);
		return (IAction[]) actions.toArray(new IAction[]{});
	}
	
	
}
