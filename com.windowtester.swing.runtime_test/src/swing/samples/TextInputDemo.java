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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;

/**
 * TextInputDemo.java is a 1.4 application that uses
 * these additional files:
 *   SpringUtilities.java
 *   ...
 */
public class TextInputDemo extends JPanel
                                          implements ActionListener,
                                                     FocusListener {
    JTextField streetField, cityField;
    JFormattedTextField zipField;
    JSpinner stateSpinner;
    boolean addressSet = false;
    Font regularFont, italicFont;
    JLabel addressDisplay;
    final static int GAP = 10;

    public TextInputDemo() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        JPanel leftHalf = new JPanel() {
            //Don't allow us to stretch vertically.
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE,
                                     pref.height);
            }
        };
        leftHalf.setLayout(new BoxLayout(leftHalf,
                                         BoxLayout.PAGE_AXIS));
        leftHalf.add(createEntryFields());
        leftHalf.add(createButtons());

        add(leftHalf);
        add(createAddressDisplay());
    }

    protected JComponent createButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        JButton button = new JButton("Set address");
        button.addActionListener(this);
        panel.add(button);

        button = new JButton("Clear address");
        button.setName("clear");
        button.addActionListener(this);
        button.setActionCommand("clear");
        panel.add(button);

        //Match the SpringLayout's gap, subtracting 5 to make
        //up for the default gap FlowLayout provides.
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0,
                                                GAP-5, GAP-5));
        return panel;
    }

    /**
     * Called when the user clicks the button or presses
     * Enter in a text field.
     */
    public void actionPerformed(ActionEvent e) {
        if ("clear".equals(e.getActionCommand())) {
            addressSet = false;
            streetField.setText("");
            cityField.setText("");

            //We can't just setText on the formatted text
            //field, since its value will remain set.
            zipField.setValue(null);
        } else {
            addressSet = true;
        }
        updateDisplays();
    }

    protected void updateDisplays() {
        addressDisplay.setText(formatAddress());
        if (addressSet) {
            addressDisplay.setFont(regularFont);
        } else {
            addressDisplay.setFont(italicFont);
        }
    }

    protected JComponent createAddressDisplay() {
        JPanel panel = new JPanel(new BorderLayout());
        addressDisplay = new JLabel();
        addressDisplay.setHorizontalAlignment(JLabel.CENTER);
        regularFont = addressDisplay.getFont().deriveFont(Font.PLAIN,
                                                            16.0f);
        italicFont = regularFont.deriveFont(Font.ITALIC);
        updateDisplays();

        //Lay out the panel.
        panel.setBorder(BorderFactory.createEmptyBorder(
                                GAP/2, //top
                                0,     //left
                                GAP/2, //bottom
                                0));   //right
        panel.add(new JSeparator(JSeparator.VERTICAL),
                  BorderLayout.LINE_START);
        panel.add(addressDisplay,
                  BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(200, 150));

        return panel;
    }

    protected String formatAddress() {
        if (!addressSet) return "No address set.";

        String street = streetField.getText();
        String city = cityField.getText();
        String state = (String)stateSpinner.getValue();
        String zip = zipField.getText();
        String empty = "";

        if ((street == null) || empty.equals(street)) {
            street = "<em>(no street specified)</em>";
        }
        if ((city == null) || empty.equals(city)) {
            city = "<em>(no city specified)</em>";
        }
        if ((state == null) || empty.equals(state)) {
            state = "<em>(no state specified)</em>";
        } else {
            int abbrevIndex = state.indexOf('(') + 1;
            state = state.substring(abbrevIndex,
                                    abbrevIndex + 2);
        }
        if ((zip == null) || empty.equals(zip)) {
            zip = "";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<html><p align=center>");
        sb.append(street);
        sb.append("<br>");
        sb.append(city);
        sb.append(" ");
        sb.append(state); //should format
        sb.append(" ");
        sb.append(zip);
        sb.append("</p></html>");

        return sb.toString();
    }

    //A convenience method for creating a MaskFormatter.
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    /**
     * Called when one of the fields gets the focus so that
     * we can select the focused field.
     */
    public void focusGained(FocusEvent e) {
        Component c = e.getComponent();
        if (c instanceof JFormattedTextField) {
            selectItLater(c);
        } else if (c instanceof JTextField) {
            ((JTextField)c).selectAll();
        }
    }

    //Workaround for formatted text field focus side effects.
    protected void selectItLater(Component c) {
        if (c instanceof JFormattedTextField) {
            final JFormattedTextField ftf = (JFormattedTextField)c;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ftf.selectAll();
                }
            });
        }
    }

    //Needed for FocusListener interface.
    public void focusLost(FocusEvent e) { } //ignore

    protected JComponent createEntryFields() {
        JPanel panel = new JPanel(new SpringLayout());

        String[] labelStrings = {
            "Street address: ",
            "City: ",
            "State: ",
            "Zip code: "
        };

        JLabel[] labels = new JLabel[labelStrings.length];
        JComponent[] fields = new JComponent[labelStrings.length];
        int fieldNum = 0;

        //Create the text field and set it up.
        streetField  = new JTextField();
        streetField.setColumns(20);
        fields[fieldNum++] = streetField;

        cityField = new JTextField();
        cityField.setColumns(20);
        // name the field
        cityField.setName("city");
        fields[fieldNum++] = cityField;

        String[] stateStrings = getStateStrings();
        stateSpinner = new JSpinner(new SpinnerListModel(stateStrings));
        fields[fieldNum++] = stateSpinner;

        zipField = new JFormattedTextField(
                            createFormatter("#####"));
        fields[fieldNum++] = zipField;

        //Associate label/field pairs, add everything,
        //and lay it out.
        for (int i = 0; i < labelStrings.length; i++) {
            labels[i] = new JLabel(labelStrings[i],
                                   JLabel.TRAILING);
            labels[i].setLabelFor(fields[i]);
            panel.add(labels[i]);
            panel.add(fields[i]);

            //Add listeners to each field.
            JTextField tf = null;
            if (fields[i] instanceof JSpinner) {
                tf = getTextField((JSpinner)fields[i]);
            } else {
                tf = (JTextField)fields[i];
            }
            tf.addActionListener(this);
            tf.addFocusListener(this);
        }
        SpringUtilities.makeCompactGrid(panel,
                                        labelStrings.length, 2,
                                        GAP, GAP, //init x,y
                                        GAP, GAP/2);//xpad, ypad
        return panel;
    }

    public String[] getStateStrings() {
        String[] stateStrings = {
            "Alabama (AL)",
            "Alaska (AK)",
            "Arizona (AZ)",
            "Arkansas (AR)",
            "California (CA)",
            "Colorado (CO)",
            "Connecticut (CT)",
            "Delaware (DE)",
            "District of Columbia (DC)",
            "Florida (FL)",
            "Georgia (GA)",
            "Hawaii (HI)",
            "Idaho (ID)",
            "Illinois (IL)",
            "Indiana (IN)",
            "Iowa (IA)",
            "Kansas (KS)",
            "Kentucky (KY)",
            "Louisiana (LA)",
            "Maine (ME)",
            "Maryland (MD)",
            "Massachusetts (MA)",
            "Michigan (MI)",
            "Minnesota (MN)",
            "Mississippi (MS)",
            "Missouri (MO)",
            "Montana (MT)",
            "Nebraska (NE)",
            "Nevada (NV)",
            "New Hampshire (NH)",
            "New Jersey (NJ)",
            "New Mexico (NM)",
            "New York (NY)",
            "North Carolina (NC)",
            "North Dakota (ND)",
            "Ohio (OH)",
            "Oklahoma (OK)",
            "Oregon (OR)",
            "Pennsylvania (PA)",
            "Rhode Island (RI)",
            "South Carolina (SC)",
            "South Dakota (SD)",
            "Tennessee (TN)",
            "Texas (TX)",
            "Utah (UT)",
            "Vermont (VT)",
            "Virginia (VA)",
            "Washington (WA)",
            "West Virginia (WV)",
            "Wisconsin (WI)",
            "Wyoming (WY)"
        };
        return stateStrings;
    }

    public JFormattedTextField getTextField(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            return ((JSpinner.DefaultEditor)editor).getTextField();
        } else {
            System.err.println("Unexpected editor type: "
                               + spinner.getEditor().getClass()
                               + " isn't a descendant of DefaultEditor");
            return null;
        }
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
        JFrame frame = new JFrame("TextInputDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        //      AWTEventListener
 /*       frame.getToolkit().addAWTEventListener(
          new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
              System.out.println(e+"\n");
              if (e.getID()== KeyEvent.KEY_RELEASED)// &&(e.getSource() instanceof JSlider))
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
        //Create and set up the content pane.
        JComponent newContentPane = new TextInputDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
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
