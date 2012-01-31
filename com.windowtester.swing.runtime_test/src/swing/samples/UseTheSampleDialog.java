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
package swing.samples;

//Fri Oct 25 18:07:43 EST 2004
//
// Written by Sean R. Owens, released to the public
// domain.  Share and enjoy.  http://darksleep.com/player

// This is just a very simple driver to show the use of CustomDialog,
// but I suppose it also demonstrates (minimally) how to use a
// JButton.

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

public class UseTheSampleDialog implements ActionListener {
    JFrame mainFrame = null;
    JButton myButton = null;

    public UseTheSampleDialog() {
        mainFrame = new JFrame("TestTheDialog Tester");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {System.exit(0);}
            });
        myButton = new JButton("Test the dialog!");
        myButton.addActionListener(this);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getContentPane().add(myButton);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(myButton == e.getSource()) {
            System.err.println("Opening dialog.");
            SampleDialog myDialog = new SampleDialog(mainFrame, "Do you like Java?");
            System.err.println("After opening dialog.");
            if(myDialog.getAnswer()) {
                System.err.println("The answer stored in CustomDialog is 'true' (i.e. user clicked yes button.)");
            }
            else {
                System.err.println("The answer stored in CustomDialog is 'false' (i.e. user clicked no button.)");
            }
        }
    }

    public static void main(String argv[]) {

        UseTheSampleDialog tester = new UseTheSampleDialog();
    }
}