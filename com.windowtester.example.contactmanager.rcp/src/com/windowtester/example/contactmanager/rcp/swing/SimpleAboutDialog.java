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

package com.windowtester.example.contactmanager.rcp.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimpleAboutDialog extends JDialog {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public SimpleAboutDialog() {
  //  super(parent, "About Dialog", true);
    super();
    setTitle("Swing Dialog");
    Box b = Box.createVerticalBox();
    b.add(Box.createGlue());
    b.add(new JLabel("This is a Swing dialog"));
    b.add(new JLabel("in an RCP application"));
    b.add(Box.createGlue());
    getContentPane().add(b, "Center");

    JPanel p2 = new JPanel();
    JButton ok = new JButton("OK");
    p2.add(ok);
    getContentPane().add(p2, "South");

    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        setVisible(false);
      }
    });

    setSize(250, 150);
  }

 /* public static void main(String[] args) {
    JDialog f = new SimpleAboutDialog();
    f.setVisible(true);
  }
*/
}
           