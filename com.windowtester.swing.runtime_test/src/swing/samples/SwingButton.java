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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;


public class SwingButton extends JPanel {
	private boolean buttonClicked;
	private boolean checkboxClicked;
	private boolean radioButtonClicked;
	private boolean toggleButtonClicked;
	
	/**
	 * Create the panel
	 */
	public SwingButton() {
		super();

		final JButton button = new JButton();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("button clicked");
				buttonClicked = true;
			}
		});
		
		button.setText("Test Button");
		add(button);
		
		JCheckBox checkbox = new JCheckBox("CheckBox",false);
		checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("checkbox clicked");
				checkboxClicked = true;
			}
		});
		
		add(checkbox);
		
		JRadioButton radioButton = new JRadioButton("RadioButton",false);
		radioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("radio button clicked");
				radioButtonClicked = true;
			}
		});
		
		add(radioButton);
		
		JToggleButton toggleButton = new JToggleButton("ToggleButton",false);
		toggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("toggle button clicked");
				toggleButtonClicked = true;
			}
		});
		
		add(toggleButton);
		
		
	}
	
	private boolean getButtonClicked(){
		return buttonClicked;
	}
	
	private boolean getCheckboxClicked(){
		return checkboxClicked;
	}

	private boolean getRadioButtonClicked(){
		return radioButtonClicked;
	}
	
	private boolean getToggleButtonClicked(){
		return toggleButtonClicked;
	}
	
	
	/**
     * Create the GUI and show it.  For thread safety, 
     * this method should be invoked from the 
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Swing Buttons");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        		
       //Create and set up the content pane.
        SwingButton newContentPane = new SwingButton();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

 /*   public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
    }
    
   */
    
    public static void main(String[] args) {
    		
    	createAndShowGUI();
    }
    
    
}