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
package com.windowtester.codegen.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.windowtester.controller.ControllerManager;
import com.windowtester.controller.IControllerAction;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderMetaEvent;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;

/**
 * Copied from org.eclipse.tptp.test.auto.gui.internal.editor.AutoGUITestControllerDialog
 * and modified.
 * 
 */
public class ControllerDialog implements SelectionListener, MouseListener, MouseMoveListener, KeyListener, DisposeListener{

	
	// icon pointers
	
	
	private final List _usedHookNames = new ArrayList(); //a list to keep track of used assertion names

	private Shell _sShell;
	private Composite _toolbarComposite;
	private Group _verificationGrp;
	private Group _controlGrp;
	private ToolBar _toolBar;
	private Text _verificationName;
	private Button _insertButton;
	private Label _nameLbl;
	private ToolItem _start;
	private ToolItem _terminate;
	private ToolItem _restart;
	private ToolItem _assert;
//	private ToolItem _write;
	private ToolItem _pause;
//	private ToolItem _generate;
	private ToolItem _positionBasedRecording;
	private ToolItem _recordWaitTime;
	private Composite _statusComposite;
	private Label _statuslbl;
	
	
	/* The display listener that will be listening in for new active shells (set only if isRoot is true) */
	private DisplayListener _displayListener; 
	
	/* The control delegator (maybe set only if isRoot is true) */
	private ControllerDialog _delegator;
	
	/* The dialog is moved relative to this position */
	private Point _originalPosition;
	
	/* Indicates that the mouse button is pushed on the shell */ 
	private boolean _isMouseDown;
	
	/* The first control center dialog will have this set and the successive ones will not */
	private boolean _isRoot;
		
	/* The last status of the control center dialog */
	private String _lastStatus;
	
	/* The last location of the control dialog box */
	private Point _lastLocation;
	
	/* Indicates whether the control center shell is disposed or not */	
	private boolean _isShellDisposed;
	
	/* Indicates whether position based should be on (should be set before shell is created)*/
	private boolean _positionBasedOn;
	
	/* Indicates whether wait times should be recorded */
	private boolean _waitTimeOn;
	
	
	/* Event types */
	protected final static byte TERMINATE 						= 0x01;
	protected final static byte VERIFICATION_HOOK_INSERT		= 0x02;
	protected final static byte RESTART							= 0x03;
	protected final static byte POSITION_BASED					= 0x04;
	protected final static byte WAIT_TIME						= 0x05;
	protected final static byte START							= 0x06; //pq
	protected final static byte WRITE							= 0x07; //pq
	protected final static byte GENERATE						= 0x08; //pq
	protected final static byte ASSERT				    		= 0x09; //pq
	
	
	
	private final static int DO_NOT_DISPOSE = 1020;
	
	/** A label that identifies components of this widget */
	static public final String ID_LABEL = "controller.dialog.component";
	/** A value for the label key (ignored; but must be non-null) */
	static final String ID_LABEL_VALUE = "val";
	
	//map toolitems to contributed actions
	private final Map /* <ToolItem,IControllerAction>*/ _actionMap = new HashMap();
	
	
	/**
	 * @param parent
	 */
	public ControllerDialog() {
		_sShell = new Shell(SWT.ON_TOP); //keep the dialog on top!
		_isShellDisposed = false;
		_isRoot = true;
		_positionBasedOn = false;
	}

	public Point getLastLocation ()
	{
		if (_isRoot && _delegator != null)
			return _delegator.getLastLocation();
		if (_sShell != null && !_sShell.isDisposed())
			return _sShell.getLocation();
		
		return _lastLocation;
	}
	
	
	/**
	 * A non-blocking method that opens the control center dialog
	 */
	public void openDialog() 
	{
		_originalPosition = new Point(0, 0);
		_sShell = new Shell(SWT.ON_TOP);
		_sShell.setText("Control Center");
		_sShell.setLayout(new GridLayout());
		if (_lastLocation != null)
			_sShell.setLocation(_lastLocation);
		_sShell.addMouseListener(this);
		_sShell.addMouseMoveListener(this);
		_sShell.addDisposeListener(this);
		
		/* We would like the macro recorder to ignore this shell */
		//sShell.setData(MacroManager.IGNORE, Boolean.TRUE);
		
		_sShell.setFocus();		
		createToolbarComposite();
		createStatusComposite();

		/* We need to always stay on top of every shell */
		if (_isRoot)
		{
			_displayListener = new DisplayListener();
			Display.getCurrent().addFilter(SWT.Activate, _displayListener);
		}
		
		/* Set initial status to idle */
		setStatus("idle");
		
		makeTestFriendly();
		
		
		/* Open the dialog */
		_sShell.pack();
 		_sShell.open();
	}
	
	private void makeTestFriendly() {
		RecorderDialogTestHelper.tagAsRecorderShell(_sShell);
		RecorderDialogTestHelper.tagAsRecorderStartButton(_start);
	}

	public void keyPressed(KeyEvent e)
	{
		keyReleased (null);
	}
	
	public void keyReleased(KeyEvent e)
	{		
		if (_verificationName.getText().trim().equals(""))
		{
			_insertButton.setEnabled(false);
			return;
		}
		_insertButton.setEnabled(true);		
	}
	
	public void mouseDoubleClick(MouseEvent e)
	{
		/* Doesn't need to be implemented */
	}
	
	public void mouseDown(MouseEvent e)
	{
		_isMouseDown = true;
		_originalPosition.x = e.x;
		_originalPosition.y = e.y;
	}


	public void mouseUp(MouseEvent e)
	{
		_isMouseDown = false;
	}

	/**
	 * Move the dialog box when the user clicks on the shell and moves the cursor
	 */
	public void mouseMove(MouseEvent e)
	{
		_sShell.setFocus();
		if (_isMouseDown)
		{
			int xMove = e.x - _originalPosition.x;
			int yMove = e.y - _originalPosition.y;
			
			Point currentLocation = _sShell.getLocation();
			_sShell.setLocation(currentLocation.x + xMove, currentLocation.y + yMove);
			_lastLocation = _sShell.getLocation();
		}		
	}
	
	/**
	 * This method initializes toolbarComposite	
	 *
	 */    
	private void createToolbarComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		_toolbarComposite = new Composite(_sShell, SWT.NONE);		   
		createControlGrp();
		//createVerificationGrp();
		_toolbarComposite.addMouseListener(this);
		_toolbarComposite.addMouseMoveListener(this);
		_toolbarComposite.setLayout(gridLayout);
		
	}
	
	private void createStatusComposite()
	{
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		
		GridLayout gridLayout = new GridLayout();
		_statusComposite = new Composite (_sShell, SWT.NONE);		   
		_statusComposite.setLayout(gridLayout);
		_statusComposite.setLayoutData(gd);
		_statusComposite.addMouseListener(this);
		_statusComposite.addMouseMoveListener(this);
		
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = SWT.FILL;
		gd2.grabExcessHorizontalSpace = true;
		_statuslbl = new Label (_statusComposite, SWT.NONE);
		_statuslbl.setAlignment(SWT.CENTER);
		_statuslbl.setLayoutData(gd2);
		_statuslbl.addMouseListener(this);
		_statuslbl.addMouseMoveListener(this);
	}
	
	/**
	 * This method initializes verificationGrp	
	 *
	 */    
	private void createVerificationGrp() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 3;
		_verificationGrp = new Group(_toolbarComposite, SWT.NONE);		   
		_verificationGrp.setText("verifier");
		_verificationGrp.setLayout(gridLayout1);
		_verificationGrp.addMouseListener(this);
		_verificationGrp.addMouseMoveListener(this);
		_nameLbl = new Label(_verificationGrp, SWT.NONE);
		_nameLbl.setText("new");
		_nameLbl.addMouseListener(this);
		_nameLbl.addMouseMoveListener(this);
		_verificationName = new Text(_verificationGrp, SWT.BORDER);
		_verificationName.addKeyListener(this);
		_insertButton = new Button(_verificationGrp, SWT.NONE);
		_insertButton.setText("insert verification hook");
		_insertButton.addSelectionListener(this);
		_insertButton.setEnabled(false);
		_sShell.setDefaultButton(_insertButton);
	}
	/**
	 * This method initializes controlGrp	
	 *
	 */    
	private void createControlGrp() {
		_controlGrp = new Group(_toolbarComposite, SWT.NONE);		   
		_controlGrp.setLayout(new GridLayout());
		_controlGrp.addMouseListener(this);
		_controlGrp.addMouseMoveListener(this);
		createToolBar();
		_controlGrp.setText("Control");
	}
	
	/**
	 * This method initializes toolBar	
	 *
	 */    
	private void createToolBar() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		_toolBar = new ToolBar(_controlGrp, SWT.NONE);

		/* The start button */
		_start = new ToolItem(_toolBar, SWT.PUSH);
		_start.setImage(RecorderUIImages.INSTANCE.getImage(RecorderUIImages.START));
		_start.setToolTipText("Start/Resume Recording");
		_start.addSelectionListener(this);
		
		
		/* The pause events button */
		_pause = new ToolItem(_toolBar, SWT.PUSH);
		_pause.setImage(RecorderUIImages.INSTANCE.getImage(RecorderUIImages.PAUSE));
		_pause.setToolTipText("Pause Recording");
		_pause.addSelectionListener(this);
		

		/* The insert assertion button */
		_assert = new ToolItem(_toolBar, SWT.PUSH);
		_assert.setImage(RecorderUIImages.INSTANCE.getImage(RecorderUIImages.WRITE)); //for now, this ico will do...
		_assert.setToolTipText("Add Assertion Hook");
		_assert.addSelectionListener(this);
		
		
		/**
		 * termination is tricky since we need to kill the app under test...
		 * disabled for now
		 */
		
		/* The terminate button */
//		_terminate = new ToolItem(_toolBar, SWT.PUSH);
//		_terminate.setImage(RecorderUIImages.INSTANCE.getImage(RecorderUIImages.TERMINATE));
//		_terminate.setToolTipText("Stop Recording");
//		_terminate.addSelectionListener(this);
//		tag(_terminate);
		
		/* The restart button */
		_restart = new ToolItem(_toolBar, SWT.PUSH);
		_restart.setImage(RecorderUIImages.INSTANCE.getImage(RecorderUIImages.RESTART));
		_restart.setToolTipText("Restart Recording");
		_restart.addSelectionListener(this);
		
		/* The write events button */
//		_write = new ToolItem(_toolBar, SWT.PUSH);
//		_write.setImage(RecorderUIImages.INSTANCE.getImage(RecorderUIImages.WRITE));
//		_write.setToolTipText("Write Captured Events");
//		_write.addSelectionListener(this);
//		tag(_write);
		
	
		addContributedItems();
		
		
//		PQ: needs to be contributed by codegen plugin				
		/* The generate button */
//		_generate = new ToolItem(_toolBar, SWT.PUSH);
//		_generate.setImage(RecorderUIImages.INSTANCE.getImage(RecorderUIImages.RESTART));
//		_generate.setToolTipText("Generate Test Case");
//		_generate.addSelectionListener(this);
		
		
//		/* A separator */
//		ToolItem sep = new ToolItem(toolBar, SWT.SEPARATOR);
//		sep.setText("");
//		
//		/* The position-based recording button */
//		positionBasedRecording = new ToolItem (toolBar, SWT.CHECK);
//		positionBasedRecording.setImage(AutoGUIImages.INSTANCE.getImage(AutoGUIImages.POSITION_BASED));
//		positionBasedRecording.setToolTipText(AutoGUIMessages.AUTO_GUI_CONTROL_POSITION_BASED);
//		positionBasedRecording.setSelection(positionBasedOn);
//		positionBasedRecording.addSelectionListener(this);
//				
//		/* The wait time toggle button */		
//		recordWaitTime = new ToolItem (toolBar, SWT.CHECK);
//		recordWaitTime .setImage(AutoGUIImages.INSTANCE.getImage(AutoGUIImages.WAIT_TIME));
//		recordWaitTime.setToolTipText(AutoGUIMessages.AUTO_GUI_CONTROL_WAIT_TIME);
//		recordWaitTime.setSelection(waitTimeOn);
//		recordWaitTime.addSelectionListener(this);
		
		
		_toolBar.setLayoutData(gridData);
	}
	
	/**
	 * Add tool items/actions, contributed via the Controller Action extension point
	 */
	private void addContributedItems() {
		
		IControllerAction[] contributedActions = ControllerManager.getContributedActions();
		if (contributedActions == null || contributedActions.length == 0)
			return; //nothing to do!
		//TODO: if non-empty- want to create a new group for these actions...
		//create a new group...
		
		//get contributed actions and make associated tool items
		IControllerAction action;
		ToolItem contrib;
		for (int i = 0; i < contributedActions.length; i++) {
			action = contributedActions[i];
			contrib = new ToolItem(_toolBar, SWT.PUSH);
			//map this tool-item to its action (for later dispatch)
			_actionMap.put(contrib,action);
			//fill in presentation details
			contrib.setImage(action.getImage());
			contrib.setToolTipText(action.getToolTipText());
			contrib.addSelectionListener(this);
		}
	}


	public void widgetDefaultSelected(SelectionEvent e)
	{
		/* Doesn't need to be implemented */		
	}
	
	/**
	 * A chance to clean up after our selves 
	 */
	public void dispose()
	{		
		stopRecorder(); //TODO: is stopping enough? should we prompt to write if there are stored events?
		
		_isShellDisposed = true;		
		
		if (_sShell != null && !_sShell.isDisposed())
		{
			_sShell.close();
			_toolBar.dispose();
//			_verificationName.dispose();
//			_insertButton.dispose();
//			_nameLbl.dispose();
			_start.dispose();
//			_write.dispose();
			_pause.dispose();
//			_generate.dispose();
//			_terminate.dispose();
			_restart.dispose();
			disposeContributedControls();
			
//			_positionBasedRecording.dispose();
			//_verificationGrp.dispose();
			_controlGrp.dispose();
			_toolbarComposite.dispose();
			_sShell.dispose();  //  @jve:decl-index=0:visual-constraint="10,10"
		}
		
		_toolBar = null;
		_verificationName = null;
		_insertButton = null;
		_nameLbl = null;
		_start = null;
		_terminate = null; 
		_verificationGrp = null;
		_controlGrp = null;
		_toolbarComposite = null;
		_sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
		
		_originalPosition = null;
	}
	
	/**
	 * Dispose of all the contributed controls.
	 * Note: it's funny that this is required...  aren't all widgets created
	 * with a specified parent disposed by that parent?
	 */
	private void disposeContributedControls() {
		Set contrbs = _actionMap.keySet();
		for (Iterator iter = contrbs.iterator(); iter.hasNext();) {
			ToolItem item = (ToolItem) iter.next();
			item.dispose();
		}
	}


//	/**
//	 * A helper method used to notify the registered listeners
//	 * 
//	 * @param eventType The type of event that caused this invokation
//	 */
//	protected void notifyListeners (byte eventType, Object value)
//	{
//		
//		/* If we're not the root control center, then we're expected to delegate the tasks */
//		if (!_isRoot)
//		{
//			_rootControl.notifyListeners(eventType, value);			
//		}
//		else
//		{
//			/* Remove the listener if we were terminated */
//			if (eventType == TERMINATE)
//				Display.getCurrent().removeFilter(SWT.Activate, _displayListener);
//			
//			/* Update appropriate fields if delegator updated the position-based or wait-time-recording options */
//			if (_delegator != null)
//			{
//				if (eventType == POSITION_BASED)
//					_positionBasedOn = _delegator.isPositionBasedOn();
//				else if (eventType == WAIT_TIME)
//					_waitTimeOn = _delegator.isWaitTimeOn();
//			}
//			
//			/* Start notifying the listeners */
//			int size = _listenerBucket.size();
//			for (int i = 0; i < size; i++)
//			{
//				((AutoGUIControllerListener)listenerBucket.get(i)).handleEvent(eventType, value);
//			}
//		}
//	}

	public void widgetSelected(SelectionEvent e)
	{
		byte eventType = 0;

		/* Determine the type of event */
		
		// handle contributed actions
		IControllerAction contribedAction = (IControllerAction)_actionMap.get(e.widget);
		if (contribedAction != null) {
			contribedAction.perform();
			return;
		}
		
		// then go on to the hard-wired, recorder-provided ones...
		
		if (e.widget == _terminate)
		{
			eventType = TERMINATE;
//			notifyListeners (eventType, null);			
			stopRecorder();
			
//			if (e.detail != DO_NOT_DISPOSE)
//				dispose();
			
			return;
		}
		if (e.widget == _start)
		{
			eventType = START;
			startRecorder();			
			return;						
		}
		
//		if (e.widget == _write)
//		{
//			eventType = WRITE;
//			writeEvents();			
//			return;						
//		}

		if (e.widget == _pause)
		{
			eventType = WRITE;
			pauseRecorder();			
			return;									
		}
		
		if (e.widget == _restart)
		{
			eventType = RESTART;
			restartRecorder();			
			return;						
		}

		if (e.widget == _assert)
		{
			eventType = ASSERT;
			addAssertion();			
			return;						
		}
		/* This is the verification insertion event */
		else if (e.widget == _insertButton)
			eventType = VERIFICATION_HOOK_INSERT;
//		else if (e.widget == _restart)
//			eventType = RESTART;
		else if (e.widget == _positionBasedRecording)
		{
			_positionBasedOn = ((ToolItem)e.widget).getSelection();
			eventType = POSITION_BASED;
		}
		else if (e.widget == _recordWaitTime)
		{
			_waitTimeOn = ((ToolItem)e.widget).getSelection();
			eventType = WAIT_TIME;
		}
		
//		if (eventType != 0)
//			notifyListeners (eventType, _verificationName.getText());
	}
	

	private TaggedInputDialog _assertionDialog;
	
	/** the port that the application under recording controller is listening */
	private int port;
	
	/**
	 * Add an assertion to the event stream.
	 */
	private void addAssertion() {
		TaggedInputDialog d = getAssertionDialog();
		int result = d.open();
		if (result == Window.OK) {
			String hookName = d.getValue();
			_usedHookNames.add(hookName);
			EventRecorderPlugin.send(new RecorderAssertionHookAddedEvent(hookName), port);	
		}
	}


	private TaggedInputDialog getAssertionDialog() {
		if (_assertionDialog == null) {
			_assertionDialog = new TaggedInputDialog(_sShell, "Insert Assertion Hook", 
					"Input the name of an assertion method hook.", getHookNameSuggestion(), 
					new IInputValidator() {
						public String isValid(String newText) {
							// TODO do we want to validate more (e.g., hook exists, etc?)
					        return (newText == null || newText.length() == 0) ? " " : null; //$NON-NLS-1$
						}
			});
		}
		_assertionDialog.setValue(getHookNameSuggestion());
		return _assertionDialog;
	}

	
	private String getHookNameSuggestion() {
		String name = "assert_1";
		for (; ;) {
			if (!_usedHookNames.contains(name))
				return name;
			name = increment(name);			
		}
	}


	/**
	 * Restart the recorder.
	 */
	private void restartRecorder() {
		EventRecorderPlugin.send(RecorderMetaEvent.RESTART, port);
	}

	
	/**
	 * Pause the recorder.
	 */
	private void pauseRecorder() {
		setStatus("paused");
		EventRecorderPlugin.send(RecorderMetaEvent.PAUSE, port);
	}

	
	public void widgetDisposed(DisposeEvent e) 
	{
		if (!_isShellDisposed && _isRoot)
		{
			_isShellDisposed = true;
			
			/* The shell is been disposed.  Simulate a termination */
//			Event event = new Event();
//			event.widget = _terminate;
//			event.detail = DO_NOT_DISPOSE;
//			widgetSelected(new SelectionEvent(event));			
			//TODO PQ: removed above as a workaround for widget disposal errors...  should have a better strategy
		}
	}	
	
	
	/**
	 * Updates the status of the control dialog center
	 * 
	 * @param status The status to be printed
	 */
	public void setStatus (String status)
	{		
		/* The delegator has to handle this (if one exists) */
		if (_isRoot && _delegator != null)
		{
			_delegator.setStatus(status);
			return;
		}
			
		if (status == null)
			return;
		
		_lastStatus = status;
		String finalStatus = "Status: " + status;
		boolean toolong = false;
		if (finalStatus.length() > 30)
			toolong = true;
		
		
		_statuslbl.setToolTipText(finalStatus);
		if (toolong)
			finalStatus = finalStatus.substring (0, 30) + "...";
		
			
		_statuslbl.setText (finalStatus);
	}


	public String getLastStatus()
	{
		if (_isRoot && _delegator != null)
			return _delegator.getLastStatus();
		return _lastStatus;
	}

	
	public void setLocation (Point location)
	{
		_lastLocation = location;
	}
	
	
	private class DisplayListener implements Listener
	{
		public void handleEvent(final Event event) 
		{				
				
			class ChangeDialogParentOp implements Runnable
			{
				private DisplayListener listener;
				public ChangeDialogParentOp(DisplayListener listener)
				{
					this.listener = listener;
				}
				
				public void run() 
				{
//					try
//					{
//						boolean isNewShellPresent = event.widget instanceof Shell && 
//													!event.widget.isDisposed() && 
//													/* pq: event.widget.getData(MacroManager.IGNORE) == null && */
//													(_lastParent.isDisposed() || !event.widget.equals(_lastParent));
//						
//						if (!isNewShellPresent)
//							return;
//												
//						Point location = getLastLocation();	
//						String lastStatus = getLastStatus();
//						
//						/* Get rid of the last delegator if one was created.  Otherwise dispose this control dialog */					
//						if (_delegator != null)
//							_delegator.dispose();
//						else
//							dispose();
//													
//						_delegator = new ControllerDialog((Shell)event.widget, ControllerDialog.this);																
//						_delegator.setLocation(location);
//						_delegator.setPositionBasedOn(isPositionBasedOn());
//						_delegator.setWaitTimeOn(isWaitTimeOn());
//						_delegator.openDialog();
//						_delegator.setStatus(lastStatus);					
//						_lastParent = (Shell)event.widget;
//					}
//
//					catch (Throwable t)
//					{
//						/* If at any point an error occurs, then de-register this listener */
//						Display.getCurrent().removeFilter(SWT.Activate, listener);
//					}
//				
				}
			}
		
			/* We need to run this as a timer operation because it may otherwise cause a widget disposed exeception.  The exception
			 * is caused in cases where we get an activation event on shells that are activated only for short periods of time.  Running
			 * this operation as a timer operation will guarantee that an activated shell is present and not disposed after 50 milliseconds.  
			 * This ultimately avoids processing activated shells that have a very short life span. */
			EventRecorderPlugin.getDefault().getDisplay().timerExec(50, new ChangeDialogParentOp(this));
		}		
	}
	
	public void setPositionBasedOn(boolean isPositionBasedOn) {
		_positionBasedOn = isPositionBasedOn;
	}


	public boolean isPositionBasedOn() {
		return _positionBasedOn;
	}


	public boolean isWaitTimeOn() {
		return _waitTimeOn;
	}


	public void setWaitTimeOn(boolean waitTimeOn) {
		_waitTimeOn = waitTimeOn;
	}


	/**
	 * Start the event recorder
	 */
	public void startRecorder() {
		setStatus("recording");
		EventRecorderPlugin.send(RecorderMetaEvent.START, port);
	}
	
	/**
	 * Start the event recorder
	 */
	public void stopRecorder() {
		setStatus("stopped");
		EventRecorderPlugin.send(RecorderMetaEvent.STOP, port);
	}
	
	/**
	 * Write the recorded events
	 */
	private void writeEvents() {
		setStatus("writing");
		EventRecorderPlugin.writeRecording(); // remove this if not needed: the contract was changed
		setStatus("stopped");
	}	
		
	
	//TODO: this should be moved into a utility class...
    public static String increment(String methodName) {
    	ParsedName name = parseName(methodName);
    	if (name.index == -1)
    		name.index = 1; //skip zero
    	else 
    		name.index++;
    	return name.toString();
	}


	/**
     * Parse this name into a name piece and an index
     * @param name - the name to parse
     * @return a ParsedName
     */
    public static ParsedName parseName(String name) {
        boolean done = false;
        StringBuffer sb = new StringBuffer();
        int i;
        for (i=name.length()-1; !done && i >= 0; --i) {
            char ch = name.charAt(i);
            if (Character.isDigit(ch))
                sb.append(ch);
            else
                done = true;
        }
        ParsedName parsedName = new ParsedName();
        parsedName.index = sb.length() == 0 ? -1   : Integer.parseInt(sb.reverse().toString());
        parsedName.name  = sb.length() == 0 ? name : name.substring(0,i+2);
        return parsedName;
    }
	
	
	/**
     * A data holder class for parsed names. 
     */
    static class ParsedName {
        /** The name component */
        public String name;
        /** The integer index */
        public int index;
        
        public String toString() {
        	return name + index;
        }
    }

	public void setEnabled(int port) {
		this.port = port;
	}


	public boolean isDisposed() {
		return _sShell.isDisposed();
	}
	
	
}
