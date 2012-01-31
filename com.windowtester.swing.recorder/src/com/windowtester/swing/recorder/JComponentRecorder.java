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
package com.windowtester.swing.recorder;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import abbot.Log;
import abbot.script.ComponentReference;
import abbot.script.Resolver;
import abbot.script.Step;

import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.UISemanticEventFactory;


/**
 * Record basic semantic events you might find on an JComponent. <p>
 * 
 * Watches for events that trigger an action from the component's action map.  
 * As of 1.3.1, KEY_TYPED and KEY_RELEASED events can trigger an action.
 * 
 * added generation of windowtester semantic event
 */
public class JComponentRecorder extends ContainerRecorder {
	 private JComponent target;
	    private String actionKey;
	    // keystroke used to generate semantic event
	    private KeyStroke ks;
	    int code;
	    char ch;

	    public static final int SE_ACTION_MAP = 20;

	    public JComponentRecorder(Resolver resolver) {
	        super(resolver);
	    }

	    protected void init(int rtype) {
	        super.init(rtype);
	        target = null;
	        actionKey = null;
	    }

	    /** Add handling for JComponent input-mapped actions. */
	    public boolean accept(AWTEvent event) {
	        boolean accepted;
	        if ((event instanceof KeyEvent)
	            && (event.getSource() instanceof JComponent)
	            && isMappedEvent((KeyEvent)event)) {
	            init(SE_ACTION_MAP);
	            accepted = true;
	        }
	        else {
	            accepted = super.accept(event);
	        }
	        return accepted;
	    }

	    protected javax.swing.Action getAction(KeyEvent ke) {
	        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(ke);
	        JComponent comp = (JComponent)ke.getComponent();
	        Object binding = comp.getInputMap().get(ks);
	        return binding != null ? comp.getActionMap().get(binding) : null;
	    }

	    protected boolean isMappedEvent(KeyEvent ke) {
	        return getAction(ke) != null;
	    }

	    /** Add handling for JComponent input-mapped actions. */
	    public boolean parse(AWTEvent event) {
	        boolean consumed = true;
	        switch(getRecordingType()) {
	        case SE_ACTION_MAP:
	            consumed = parseActionMapEvent(event);
	            break;
	        default:
	            consumed = super.parse(event);
	            break;
	        }
	        return consumed;
	    }

	    /** Add handling for JComponent input-mapped actions. */
	    protected boolean parseActionMapEvent(AWTEvent event) {
	        if (target == null) {
	            target = (JComponent)event.getSource();
	            KeyStroke ks = KeyStroke.getKeyStrokeForEvent((KeyEvent)event);
	            ch = ((KeyEvent)event).getKeyChar();
	            code = ((KeyEvent)event).getKeyCode();
	            
	            Object binding = target.getInputMap().get(ks);
	            Log.debug("Binding is " + binding + " ("
	                      + binding.getClass() + ")");
	            if (binding instanceof String) {
	                actionKey = (String)binding;
	            }
	            else {
	                // 1.3 and prior sometimes used the actions themselves
	                // as the key
	                javax.swing.Action action = target.getActionMap().get(binding);
	                actionKey = (String)action.getValue(javax.swing.Action.NAME);
	            }
	            Log.debug("Keystroke '" + ks + "' mapped to " + actionKey);
	        }
	        // Make sure the entire KEY_PRESSED/KEY_TYPED/KEY_RELEASED
	        // sequence is eaten
	        // NOTE: This assumes that no component expects to respond to both the
	        // key shortcut AND accept the key input.
	        if (event.getID() == KeyEvent.KEY_RELEASED) {
	            setFinished(true);
	        }
	        return true;
	    }

	    /** Add handling for JComponent input-mapped actions. */
	    protected Step createStep() {
	        Step step;
	        switch(getRecordingType()) {
	        case SE_ACTION_MAP:
	            step = createActionMap(target, actionKey);
	            break;
	        default:
	            step = super.createStep();
	            break;
	        }
	        return step;
	    }

	    /** Create a JComponent input-mapped action invocation. */
	    protected Step createActionMap(JComponent target, String actionKey) {
	        // generate semantic event
	    	IUISemanticEvent semanticEvent =
	    					UISemanticEventFactory.createKeyDownEvent(target,code,ch);
	    	notify(semanticEvent);
	    	
	    	ComponentReference cr = getResolver().addComponent(target);
	        return new abbot.script.Action(getResolver(), null,
	                                       "actionActionMap",
	                                       new String[] { cr.getID(),
	                                                      actionKey },
	                                       JComponent.class);
	    }
}
