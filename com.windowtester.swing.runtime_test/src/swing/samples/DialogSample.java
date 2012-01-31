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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DialogSample extends JDialog {
    private JPanel myPanel = null;
    private JButton yesButton = null;
    private JButton noButton = null;
    private boolean answer = false;
    public boolean getAnswer() { return answer; }

    ActionListener actionListener = new ActionListener(){
    	 public void actionPerformed(ActionEvent e) {
    	        if(yesButton == e.getSource()) {
    	            System.err.println("User chose yes.");
    	            answer = true;
    	            JOptionPane.showMessageDialog(yesButton.getParent(),
    	            	    "Eggs are not supposed to be green.",
    	            	    "Inane error",
    	            	    JOptionPane.ERROR_MESSAGE);

    	         //   setVisible(false);
    	        }
    	        else if(noButton == e.getSource()) {
    	            System.err.println("User chose no.");
    	            answer = false;
    	         //   setVisible(false);
    	        }
    	    }
    };
    
    
    public DialogSample(JFrame frame,String myMessage) {
        super(frame);
        myPanel = new JPanel();
        getContentPane().add(myPanel);
        myPanel.add(new JLabel(myMessage));
        yesButton = new JButton("Yes");
        yesButton.addActionListener(actionListener);
        myPanel.add(yesButton);        
        noButton = new JButton("No");
        noButton.addActionListener(actionListener);
        myPanel.add(noButton);
        setTitle("Question");
        System.out.println("pack");
        pack();
        //setLocationRelativeTo(frame);
        System.out.println("show");
        setVisible(true);
    }

    
   
    
    public static void main(String argv[]) {
    	JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DialogSample myDialog = new DialogSample(mainFrame, "Do you like Java?");
        mainFrame.pack();
        mainFrame.setVisible(true);

    }
    
}
