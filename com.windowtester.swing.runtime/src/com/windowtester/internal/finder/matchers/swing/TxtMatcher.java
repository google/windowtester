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
package com.windowtester.internal.finder.matchers.swing;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextComponent;

import javax.swing.AbstractButton;
import javax.swing.JLabel;

import abbot.finder.matchers.AbstractMatcher;

public class TxtMatcher extends AbstractMatcher {

	private final String text;
	private String ctext = null;
	/**
     * Constructs a Matcher for the text given.
     * <p/>
     * The component must be visible.
     * @param text the text to match.
     */
    public TxtMatcher(String text) {
        this(text, true);
    }
	
    
    /**
     * Constructs a Matcher with the text and the visibility given.
     * <p/>
     * @param text the text to match.
     * @param mustBeShowing true if the widget must be visible.
     */
    public TxtMatcher(String text, boolean mustBeShowing) {
    	this.text = text;
    }
    
    
    public boolean matches(final Component w) {
    	
    	
    	// AWT Components
    	if (w instanceof Button)
    		ctext = ((Button)w).getLabel();
    	if (w instanceof Checkbox)
    		ctext = ((Checkbox)w).getLabel();
    	if (w instanceof Label)
    		ctext = ((Label)w).getText();
    	if (w instanceof TextComponent)
    		ctext = ((TextComponent)w).getText();
    	if (w instanceof Dialog)
    		ctext = ((Dialog)w).getTitle();
    	if (w instanceof Frame)
    		ctext = ((Frame)w).getTitle();
    	
    	// Swing Components
    	if (w instanceof AbstractButton) // button,menuitem,togglebutton
    		ctext = ((AbstractButton)w).getText();
    	if (w instanceof JLabel)
    		ctext = ((JLabel)w).getText();
    	// popupmenu getLabel ?
    	
    //	if (w instanceof JTextComponent)
    //		ctext = ((JTextComponent)w).getText();
    	
    	if (ctext == null) return false;
    	if (text == null)
            return ctext == null;
    	
    	return stringsMatch(text, ctext);
    	
    }
	
    /**
	 * Retrieve the text of this matcher.
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}
    
    
    
}
