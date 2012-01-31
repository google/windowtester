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

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class SwingList extends JPanel {

	private JList list1,list2,list3;
    private DefaultListModel listModel;
    
    public SwingList(){
    	
    	Box box = Box.createHorizontalBox();
    	listModel = new DefaultListModel();
        listModel.addElement("one");
        listModel.addElement("two");
        listModel.addElement("/three/two/one");
        listModel.addElement("four");
        listModel.addElement("five");
        listModel.addElement("six");
        listModel.addElement("seven");

//      Create the list1 and put it in a scroll pane.
        list1 = new JList(listModel);
        list1.setSelectedIndex(0);
        list1.setVisibleRowCount(5);
        list1.setName("list1");
        JScrollPane listScrollPane1 = new JScrollPane(list1);

        // Create list 2
        list2 = new JList(listModel);
        list2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list2.setSelectedIndex(0);
        list2.setVisibleRowCount(5);
        list2.setName("list2");
        JScrollPane listScrollPane2 = new JScrollPane(list2);
        
        //Create list 3
        list3 = new JList(listModel);
        list3.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list3.setSelectedIndex(0);
        list3.setVisibleRowCount(5);
        list3.setName("list3");
        JScrollPane listScrollPane3 = new JScrollPane(list3);
        
       
        box.add(listScrollPane1);
        box.add(Box.createRigidArea(new Dimension(15,0)));
        box.add(listScrollPane2);
        box.add(Box.createRigidArea(new Dimension(15,0)));
        box.add(listScrollPane3);
        
        add(box);
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
        JFrame frame = new JFrame("Swing List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        		
       //Create and set up the content pane.
        SwingList newContentPane = new SwingList();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setSize(400,170);
        frame.setVisible(true);
    }

 
    
    public static void main(String[] args) {
    	System.out.println(System.getProperty("java.class.path"));	
    	createAndShowGUI();
    }
    
    
    
}
