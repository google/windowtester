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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JTree;

import abbot.script.Action;
import abbot.script.ComponentReference;
import abbot.script.Resolver;
import abbot.script.Step;
import abbot.tester.JTreeTester;

import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.UISemanticEventFactory;


/**
 * Record basic semantic events you might find on an JTree. <p>
 * <ul>
 * <li>Click one or more times in a cell
 * </ul>
 * Added windowtester semantic event generation
 */
public class JTreeRecorder extends JComponentRecorder {
	 public JTreeRecorder(Resolver resolver) {
	        super(resolver);
	    }

	    /** Normally, a click in a tree results in selection of a given row. */
	    protected Step createClick(Component target, int x, int y,
	                               int mods, int count) {
	        
	    	 
	    	String mask = null;
	    	
	    	JTree tree = (JTree)target;
	        ComponentReference cr = getResolver().addComponent(target);
	        String methodName = "actionSelectRow";
	        ArrayList args = new ArrayList();
	        args.add(cr.getID());
	        args.add(getLocationArgument(target, x, y));
	        if (tree.getRowForLocation(x, y) == -1) {
	            if (JTreeTester.isLocationInExpandControl(tree, x, y)
	                && count == 1){
	                methodName = "actionToggleRow";
	              }
	            else
	                methodName = "actionClick";
	        }
	        if ((mods != 0 && mods != MouseEvent.BUTTON1_MASK)
	            || count > 1) {
	        	// using methodName as indication for generation
	        	// of windowtester semantic events
	            //methodName = "actionClick";
	            mask = abbot.util.AWT.getMouseModifiers(mods);
	            args.add(mask);
	            if (count > 1) {
	                args.add(String.valueOf(count));
	            }
	        }
	        // create semantic event
	        if (methodName.equals("actionToggleRow")){
	        	// do nothing, ignore tree expand/collapse
	        }
	        else if (!methodName.equals("actionClick")){
	      //  else {
	        	IUISemanticEvent semanticEvent = UISemanticEventFactory.createTreeItemSelectionEvent((JTree)target,x,y,mask,count,getButton());
	        	notify(semanticEvent);
	        }
	        
	        
	        return new Action(getResolver(), null, methodName,
	                          (String[])args.toArray(new String[args.size()]),
	                          javax.swing.JTree.class);
	    }

		
		/**
		 * Override
		 * Handle context menu selections 
		 */
		protected Step createPopupMenuSelection(Component invoker, int x, int y, Component menuItem) {
			
			IUISemanticEvent semanticEvent = 
				UISemanticEventFactory.createTreeItemContextMenuSelectionEvent((JTree)invoker,x,y,(JMenuItem)menuItem);
			notify(semanticEvent);
			//semantic event has been generated
			doneEventGeneration = true;
			return super.createPopupMenuSelection(invoker, x, y, menuItem);
		}
	    
	    
}
