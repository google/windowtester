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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class SwingText extends JPanel {
	
	private JTextField textField;
	private JPopupMenu popup;
	private JMenuItem menuItem1,menuItem2;
	String text;
	
	private boolean choice1;
	private boolean choice2;
	
	public SwingText(){
		super();
		
	//	JLabel label = new JLabel("Password");
	//	add(label);
		textField = new JTextField("",20);
		textField.setName("textField");
		textField.setText("");
		textField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				textField.setText(((JTextField) evt.getSource()).getText()); 
			    text = textField.getText();
			}
		});
		add(textField);
		
		JLabel label2 = new JLabel("Name");
		add(label2);
		JTextField textField2 = new JTextField("",20);
		textField2.setText("Jane");
		add(textField2);
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
//      Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        textField.addMouseListener(popupListener);
        
        
	}
	
	public String getText() {
		return text;
	}
	
	public JTextField getTextComponent(){
		return textField;
	}
	
	class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
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
        JFrame frame = new JFrame("Swing Text");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        		
       //Create and set up the content pane.
        SwingText newContentPane = new SwingText();
        newContentPane.setOpaque(true); //content panes must be opaque
        newContentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(newContentPane);

        //      AWTEventListener
/*       frame.getToolkit().addAWTEventListener(
          new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
              System.out.println(e+"\n");
              if (e.getID()== KeyEvent.KEY_RELEASED)
            	 System.out.println("The component is "+ e.getSource()+ "\nKey is " + ((KeyEvent)e).getKeyChar()+ "\n");
            	  // System.out.println("The current value is " + ((JSlider)e.getSource()).getValue()+ "\n");
              	
            }
          }, AWTEvent.ACTION_EVENT_MASK | AWTEvent.CONTAINER_EVENT_MASK |
          	 AWTEvent.COMPONENT_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK |
               AWTEvent.FOCUS_EVENT_MASK |AWTEvent.WINDOW_EVENT_MASK |
               AWTEvent.KEY_EVENT_MASK |AWTEvent.INPUT_METHOD_EVENT_MASK |
               AWTEvent.ITEM_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK 
               
           );
  */     
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

 
    
    public static void main(String[] args) {
    		
    	createAndShowGUI();
    }


}
