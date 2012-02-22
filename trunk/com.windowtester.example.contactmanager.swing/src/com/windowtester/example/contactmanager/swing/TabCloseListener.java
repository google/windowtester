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

package com.windowtester.example.contactmanager.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import com.swtdesigner.SwingResourceManager;

/**
 * Specialized icon/listener simulating a tab icon close button
 * 
 * @author Leman Reagan
 */
public class TabCloseListener
	implements Icon
{
	private Icon closeIcon = SwingResourceManager.getIcon(ContactManagerSwing.class, "close.png");
	private JTabbedPane tabbedPane = null;
	private Rectangle closeBox = null;

	/**
	 * Construct a new instance for displaying a close icon on a tab
	 * and closing the "editor" tab when the user clicks the close icon
	 * 
	 * @param pane the tabbed panel
	 */
	public TabCloseListener(JTabbedPane pane) {
		tabbedPane = pane;
		tabbedPane.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (!(e.isConsumed()) && closeBox.contains(e.getX(), e.getY())) {
					int index = tabbedPane.getSelectedIndex();
					tabbedPane.remove(index);
					ContactManagerSwing.getInstance().refreshList();
					tabbedPane.removeMouseListener(this);
					e.consume();
				}
			}
		});
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		closeIcon.paintIcon(c, g, x, y);
		closeBox = new Rectangle(x, y, getIconWidth(), getIconHeight());
	}

	public int getIconHeight() {
		return closeIcon.getIconHeight();
	}

	public int getIconWidth() {
		return closeIcon.getIconWidth();
	}
}
