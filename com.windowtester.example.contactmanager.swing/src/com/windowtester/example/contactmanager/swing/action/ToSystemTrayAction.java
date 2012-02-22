/*******************************************************************************
 *
 *   Copyright (c) 2012 Google, Inc.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *   Google, Inc. - initial API and implementation
 *******************************************************************************/

package com.windowtester.example.contactmanager.swing.action;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;


/**
 * Action to iconize application to System Tray
 * uses Java 6 AWT Components
 * @author keertip
 * 
 */
public class ToSystemTrayAction implements ActionListener {

	public void actionPerformed(ActionEvent event) {
		
		// Construct Menu
		PopupMenu menu = new PopupMenu("Menu");
		MenuItem menuItem1 = new MenuItem("Open");
		menuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Frame f = null;
				Frame[] frames = Frame.getFrames();
				for (int i = 0; i < frames.length; i++){
					if (frames[i].getTitle().equals("Contact Manager"))
						f = frames[i];
				}
				if (f != null)
					f.setVisible(true);
			}});
		
		menu.add(menuItem1);
		
		MenuItem menuItem2 = new MenuItem("Exit");
		menuItem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}});
		
		menu.add(menuItem2);
		
	
		Image image = Toolkit.getDefaultToolkit().getImage("duke.gif");
		
		// Java 6 specific... use reflection so that this will compile in pre - Java 6 environment
		try {
			// java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "Contact Manager", menu);
			Object trayIcon = Class.forName("java.awt.TrayIcon").getConstructor(new Class[]{
				Image.class, String.class, PopupMenu.class
			}).newInstance(new Object[]{
				image, "Contact Manager", menu
			});
			
			// java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
			Class trayClass = Class.forName("java.awt.SystemTray");
			Object tray = trayClass.getMethod("getSystemTray", new Class[]{}).invoke(null,
				new Object[]{});
			
			// tray.add(trayIcon);
			trayClass.getMethod("add", new Class[] {trayIcon.getClass()}).invoke(tray, new Object[] {trayIcon});
		
		
			//	now hide the frame
			Component c = (Component)event.getSource();
			// first disable menuitem
			((JMenuItem)c).setEnabled(false);
			
			while (!(c instanceof Frame)){
				if (c instanceof JPopupMenu)
					c = ((JPopupMenu)c).getInvoker();
				else c = c.getParent();
			}
			((Frame)c).setVisible(false);
		
		} catch (Exception e) {
			String message = "This menu option not supported, requires JRE 1.6";
			JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
			        JOptionPane.INFORMATION_MESSAGE);
		} 
		
		
	}

}
