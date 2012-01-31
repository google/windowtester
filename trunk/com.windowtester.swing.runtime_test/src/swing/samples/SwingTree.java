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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.windowtester.internal.swing.WidgetLocatorService;
import com.windowtester.runtime.WidgetLocator;



public class SwingTree extends JFrame {

	class PopupTrigger extends MouseAdapter
	  {
	    public void mouseReleased(MouseEvent e)
	    {
	      if (e.isPopupTrigger())
	      {
	        int x = e.getX();
	        int y = e.getY();
	        TreePath path = tree2.getPathForLocation(x, y);
	  
	          popup.show(tree2, x, y);
	         // m_clickedPath = path;
	        }
	      }
	    }

	
	JTree tree1,tree2;
	private JPopupMenu popup;
	private JMenuItem menuItem1,menuItem2;
	boolean choice1,choice2;
	
//	 for debugging widget locators
	WidgetLocatorService service = new WidgetLocatorService();
	MouseAdapter listener = new MouseAdapter(){
		public void mouseClicked(MouseEvent e){
			WidgetLocator locator = service.inferIdentifyingInfo((Component)e.getSource());
			System.out.println(locator.toString());
		}
	};
	
	
	
	public SwingTree(String title){
		super(title);
		Box box = Box.createHorizontalBox();
		  
	  	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

	    for (int i = 0;i < 5;i++){
	    	DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Parent"+ i);
	    	for (int j = 0;j< 2;j++){
	    		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("Child"+i+j);
	    		for (int k = 0;k < 3; k++){
	    			DefaultMutableTreeNode gNode = new DefaultMutableTreeNode("grandChild"+i+j+k);
	    			childNode.add(gNode);
	    		}
	    		treeNode.add(childNode);
	    	}
	    	root.add(treeNode);
	    }
	    tree1 = new JTree(root);
	    tree1.getSelectionModel().setSelectionMode
  		(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//	   tree1.addMouseListener(listener);
	   tree1.setName("tree1");
	    JScrollPane scrollPane1 = new JScrollPane(tree1);
	    scrollPane1.setName("scrollPane1");
	    
	    // tree 2
	    DefaultMutableTreeNode root2 = new DefaultMutableTreeNode("Root");
	    
	    for (int i = 0;i < 5;i++){
	    	DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Item "+ i);
	    	for (int j = 0;j< 2;j++){
	    		DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode("Node "+ i + j);
	    		treeNode.add(leafNode);
	    	}
	    	root2.add(treeNode);
	    }
	    tree2 = new JTree(root2);
	    tree2.getSelectionModel().setSelectionMode
      	(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
//	    tree2.addMouseListener(listener);
	    tree2.setName("tree2");
	    // add popop menu
	    popup = new JPopupMenu();
		menuItem1 = new JMenuItem("choice1");
		menuItem1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
		        choice1 = !choice1;
		    }
		});
      popup.add(menuItem1);
      
      menuItem2 = new JMenuItem("choice2");
      menuItem2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
		        choice2 = !choice2;
		    }
		});
      popup.add(menuItem2);
	   // tree2.add(popup);
	    tree2.addMouseListener(new PopupTrigger());
	    
	    JScrollPane scrollPane2 = new JScrollPane(tree2);
	    scrollPane2.setName("scrollPane2");
	    box.add(scrollPane1, BorderLayout.WEST);
	    box.add(scrollPane2, BorderLayout.EAST);
	    getContentPane().add(box, BorderLayout.CENTER);
	    setSize(400, 250);
	}

	public JTree getTree1(){
		return tree1;
	}
	
	public JTree getTree2(){
		return tree2;
	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Suggest that the L&F (rather than the system)
        //decorate all windows.  This must be invoked before
        //creating the JFrame.  Native look and feels will
        //ignore this hint.
    //    JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        SwingTree frame = new SwingTree("Swing Tree Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Display the window.
        frame.pack();
        frame.setSize(400,300);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
	
	
}
