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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class SwingMenus extends JFrame {

	private JMenuBar menuBar;
	private JMenu parentMenu, submenu, submenu1,submenu2;
	private JMenu topMenu;
	private JMenuItem childMenuItem, item1,item2;
	private JMenuItem grandchildMenuItem;
	private JRadioButtonMenuItem rbMenuItem;
	private JCheckBoxMenuItem cbMenuItem;

	Component selectedMenuItem;
	
	public SwingMenus(String title){
		super(title);
	
		ActionListener actionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
		        selectedMenuItem = (Component)e.getSource();
		    }
		};
		
		MouseAdapter mouseAdapter = new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				selectedMenuItem = (Component)e.getSource();   
			}
		};
		
		//	Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        parentMenu = new JMenu("parent");
        menuBar.add(parentMenu);
        // a group of JMenuItems
        childMenuItem = new JMenuItem("child 1");
        childMenuItem.addActionListener(actionListener);
        parentMenu.add(childMenuItem);
        rbMenuItem = new JRadioButtonMenuItem("child 2");
        rbMenuItem.addActionListener(actionListener);
        rbMenuItem.setEnabled(false);
        parentMenu.add(rbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("child 3");
        cbMenuItem.addActionListener(actionListener);
        parentMenu.add(cbMenuItem);
        
        submenu = new JMenu("submenu");
        grandchildMenuItem = new JMenuItem("grandchild");
        grandchildMenuItem.addActionListener(actionListener);
        submenu.add(grandchildMenuItem);
        parentMenu.add(submenu);
        
        // Build second menu in the menu bar.
        topMenu = new JMenu("top");
        topMenu.addMouseListener(mouseAdapter);
        menuBar.add(topMenu);
        submenu1 = new JMenu("submenu1");
        topMenu.add(submenu1);
        submenu2 = new JMenu("submenu2");
        submenu1.add(submenu2);
        item1 = new JMenuItem("item1");
        item2 = new JMenuItem("item2");
        submenu2.add(item1);
        submenu2.add(item2);
        setJMenuBar(menuBar);    
        
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
        SwingMenus frame = new SwingMenus("Swing Menus Example");
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
