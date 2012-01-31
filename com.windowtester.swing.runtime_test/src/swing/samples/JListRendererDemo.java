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
import java.util.Locale;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class JListRendererDemo {
  public static void main(String[] args) {
    JFrame frame = new JFrame("JList Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
    DataItem[] items = {
          new DataItem("EN 1", "DE 1"),
          new DataItem("EN 2", "DE 2"),
          new DataItem("EN 3", "DE 3"),
          new DataItem("EN 4", "DE 4")
    };
    JList list = new JList(items);
    list.setName("JList1");
    list.setCellRenderer(new JListCellRenderer(Locale.ENGLISH));
    //list.setCellRenderer(new JListCellRenderer(Locale.GERMAN));
    frame.add(new JScrollPane(list));
   
    frame.pack();
    frame.setVisible(true);
  }
 
  /**
   * Renderer for JList
   */
  private static class JListCellRenderer extends DefaultListCellRenderer {
        private Locale userLocale;
    
        public JListCellRenderer(Locale userLocale) {
           this.userLocale = userLocale;
        }
    
      @Override
      public Component getListCellRendererComponent(JList list,
              Object value,
              int index,
              boolean isSelected,
              boolean cellHasFocus) {
         
           JLabel renderer = (JLabel) super.getListCellRendererComponent(list,
                 value,
                 index,
                 isSelected,
                 cellHasFocus);
          
           if(value != null) {
              DataItem dataItem = (DataItem) value;
             
              if(userLocale.equals(Locale.ENGLISH)) {
                 renderer.setText(dataItem.getNameEN());
              }
              else if(userLocale.equals(Locale.GERMAN)) {
                 renderer.setText(dataItem.getNameDE());
              }
           }
          
           return renderer;
      }
  }
 
  /**
   * Item to show in JList
   */
  private static class DataItem {
   private String nameEN;
   private String nameDE;
   
   public DataItem(String nameEN, String nameDE) {
      this.nameEN = nameEN;
      this.nameDE = nameDE;
   }
   public String getNameEN() {
      return nameEN;
   }
   public String getNameDE() {
      return nameDE;
   }
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("DataItem [nameEN=");
      builder.append(nameEN);
      builder.append(", nameDE=");
      builder.append(nameDE);
      builder.append("]");
      return builder.toString();
   }
  }
}
