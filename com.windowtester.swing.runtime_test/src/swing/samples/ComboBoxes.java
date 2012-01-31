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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.windowtester.internal.swing.WidgetLocatorService;
import com.windowtester.runtime.WidgetLocator;

public class ComboBoxes extends JPanel {

	JComboBox petList,colorsList;
	
//	 for debugging widget locators
	WidgetLocatorService service = new WidgetLocatorService();
	ActionListener listener = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			WidgetLocator locator = service.inferIdentifyingInfo((Component)e.getSource());
			System.out.println(locator.toString());
		}
	};
	
	public ComboBoxes(){
		
		Box box = Box.createVerticalBox();
		  String[] petStrings = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

	        //Create the combo box, select the item at index 4.
	        //Indices start at 0, so 4 specifies the pig.
	        petList = new JComboBox(petStrings);
	        petList.setSelectedIndex(4);
//	        petList.addActionListener(listener);
	        petList.setName("pets");
	        box.add(petList,BorderLayout.CENTER);
	        //add(petList, BorderLayout.PAGE_START);
	        
	        String[] colors = { "red","blue","yellow","white","black"};
	        colorsList = new JComboBox(colors);
//	        colorsList.addActionListener(listener);
	        colorsList.setEditable(true);
	        Box box2 = Box.createHorizontalBox();
	        box2.add(colorsList,BorderLayout.CENTER);
	        add(box,BorderLayout.NORTH);
	        add(box2,BorderLayout.SOUTH);
	}
	
	public JComboBox getComboBox1(){
		return petList;
	}
	
	public JComboBox getComboBox2(){
		return colorsList;
	}
	
	
	/**
     * Create the GUI and show it.  For thread safety, 
     * this method should be invoked from the 
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
       // JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Swing Combo Boxes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        		
       //Create and set up the content pane.
        ComboBoxes newContentPane = new ComboBoxes();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setSize(200,200);
        frame.setVisible(true);
    }

 
    
    public static void main(String[] args) {
    		
    	createAndShowGUI();
    }
    
	
	
}
