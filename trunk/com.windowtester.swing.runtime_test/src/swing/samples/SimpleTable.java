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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;


public class SimpleTable extends JPanel{
	
	JTable table;
	private JPopupMenu popup;
	private JMenuItem menuItem1,menuItem2,menuItem3;
	private JMenu menu;
	boolean choice1,choice2;
	
	
	
	static ActionListener actionListener = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			JPopupMenu menu = (JPopupMenu)((Component)e.getSource()).getParent();
			Component c = menu.getInvoker();
			while (! (c instanceof Frame))
				c = c.getParent();
	        System.out.println(c);
	        ((Frame)c).dispose();
	    }
	};
	
	class PopupTrigger extends MouseAdapter
	  {
	    public void mouseReleased(MouseEvent e)
	    {
	      if (e.isPopupTrigger())
	      {
	       
	    	  	int x = e.getX();
		        int y = e.getY();

		        popup.show(table, x, y);
	         // m_clickedPath = path;
	        }
	      }
	     public void mousePressed(MouseEvent e){
	    	 if (e.isPopupTrigger())
		      {
		       
		    	  	int x = e.getX();
			        int y = e.getY();

			        popup.show(table, x, y);
		         // m_clickedPath = path;
		        }
	     }
	    
	    }

	
	
	 class DataModel extends AbstractTableModel {
		    Object[][] data = { { "one", "two", "three", "four" },
		        { "five", "six", "seven", "eight" },
		        { "nine", "ten", "one", "twelve" },
		        {"thirteen","fourteen","fifteen","sixteen"},
		        {"seventeen","eighteen","ninteen","twenty"},
		        {"twenty-one","twenty-two","twenty-three","twenty-four"}};
		    
		    public DataModel() {
		        //addTableModelListener(new TML());
		      }

		      public int getColumnCount() {
		        return data[0].length;
		      }

		      public int getRowCount() {
		        return data.length;
		      }

		      public Object getValueAt(int row, int col) {
		        return data[row][col];
		      }

		      public void setValueAt(Object val, int row, int col) {
		        data[row][col] = val;
		        // Indicate the change has happened:
		        fireTableDataChanged();
		      }

		      public boolean isCellEditable(int row, int col) {
		        return true;
		      }
		    
	 }

	 public SimpleTable(){
		
		 setLayout(new GridLayout(1,0));

		 //		 add popop menu
		    popup = new JPopupMenu();
			menuItem1 = new JMenuItem("choice1");
			menuItem1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
			        choice1 = !choice1;
			    }
			});
	        popup.add(menuItem1);
	        
	        menuItem2 = new JMenuItem("choice2");
	        menuItem1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
			        choice2 = !choice2;
			    }
			});
	        popup.add(menuItem2);
		 
	        menu = new JMenu("submenu");
	        menuItem3 = new JMenuItem("choice3");
	        menu.add(menuItem3);
	        popup.add(menu);
	        
	        table = new JTable(new DataModel());
	        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
	        table.addMouseListener(new PopupTrigger());
	        
	        //Create the scroll pane and add the table to it.
	        JScrollPane scrollPane = new JScrollPane(table);

	        //Add the scroll pane to this panel.
	        add(scrollPane);

	       
		
	}
	 
	 
	 public static void main(String[] args) {
	      
		 JFrame.setDefaultLookAndFeelDecorated(true);

	        //Create and set up the window.
	        JFrame frame = new JFrame("TableDemo");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        
	        SimpleTable newContentPane = new SimpleTable();
	        newContentPane.setOpaque(true); //content panes must be opaque
	        frame.setContentPane(newContentPane);
	        
	        JMenuBar menuBar = new JMenuBar();

	        //Build the first menu.
	        JMenu fileMenu = new JMenu("File");
	        menuBar.add(fileMenu);
	        // a group of JMenuItems
	        JMenuItem exitMenuItem = new JMenuItem("Exit");
	        exitMenuItem.addActionListener(actionListener);
	        fileMenu.add(exitMenuItem);
	        frame.setJMenuBar(menuBar);   
	        
	        //Display the window.
	        frame.pack();
	        frame.setVisible(true);
	    }
	 
	

}
