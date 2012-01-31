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
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class DragListDemo extends JPanel {
  ArrayListTransferHandler arrayListHandler;

  public DragListDemo() {
    arrayListHandler = new ArrayListTransferHandler();
    JList list1, list2;

    DefaultListModel list1Model = new DefaultListModel();
    list1Model.addElement("0 (list 1)");
    list1Model.addElement("1 (list 1)");
    list1Model.addElement("2 (list 1)");
    list1Model.addElement("3 (list 1)");
    list1Model.addElement("4 (list 1)");
    list1Model.addElement("5 (list 1)");
    list1Model.addElement("6 (list 1)");
    list1 = new JList(list1Model);
    list1.setName("list1");
    list1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    list1.setTransferHandler(arrayListHandler);
    list1.setDragEnabled(true);
    JScrollPane list1View = new JScrollPane(list1);
    list1View.setPreferredSize(new Dimension(200, 100));
    JPanel panel1 = new JPanel();
    panel1.setLayout(new BorderLayout());
    panel1.add(list1View, BorderLayout.CENTER);
    panel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    DefaultListModel list2Model = new DefaultListModel();
    list2Model.addElement("0 (list 2)");
    list2Model.addElement("1 (list 2)");
    list2Model.addElement("2 (list 2)");
    list2Model.addElement("3 (list 2)");
    list2Model.addElement("4 (list 2)");
    list2Model.addElement("5 (list 2)");
    list2Model.addElement("6 (list 2)");
    list2 = new JList(list2Model);
    list2.setName("list2");
    list2.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    list2.setTransferHandler(arrayListHandler);
    list2.setDragEnabled(true);
    JScrollPane list2View = new JScrollPane(list2);
    list2View.setPreferredSize(new Dimension(200, 100));
    JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout());
    panel2.add(list2View, BorderLayout.CENTER);
    panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    setLayout(new BorderLayout());
    add(panel1, BorderLayout.LINE_START);
    add(panel2, BorderLayout.LINE_END);
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be
   * invoked from the event-dispatching thread.
   */
  private static void createAndShowGUI() {
    //Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);

    //Create and set up the window.
    JFrame frame = new JFrame("DragListDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    DragListDemo demo = new DragListDemo();
    frame.setContentPane(demo);

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
