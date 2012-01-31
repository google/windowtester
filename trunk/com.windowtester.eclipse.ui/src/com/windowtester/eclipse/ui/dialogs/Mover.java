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
package com.windowtester.eclipse.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tracker;

import com.windowtester.internal.runtime.MouseConfig;

/**
 * Shell mover helper.
 */
public class Mover extends MouseAdapter  {

		private final Shell shell;
		private Tracker tracker;

		public Mover(Shell shell) {
			this.shell = shell;
		}
		
		public Shell getShell() {
			return shell;
		}
		
		public void mouseDown(MouseEvent e) {
			if (isPrimaryButton(e.button))
				performTrackerAction(SWT.NONE); //strange but it works: taken from:
											//org.eclipse.jface.dialogs.PopupDialog.MoveAction.run()
		}

		private boolean isPrimaryButton(int button) {
			return button == MouseConfig.PRIMARY_BUTTON;
		}

		/**
		 * Perform the requested tracker action (resize or move).
		 * 
		 * @param style
		 *            The track style (resize or move).
		 */
		private void performTrackerAction(int style) {
			Shell shell = getShell();
			if (shell == null || shell.isDisposed()) {
				return;
			}

			tracker = new Tracker(shell.getDisplay(), style);
			tracker.setStippled(true);
			Rectangle[] r = new Rectangle[] { shell.getBounds() };
			tracker.setRectangles(r);

			// Ignore any deactivate events caused by opening the tracker.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=120656
			//boolean oldListenToDeactivate = listenToDeactivate;
			//listenToDeactivate = false;
			if (tracker.open()) {
				if (shell != null && !shell.isDisposed()) {
					shell.setBounds(tracker.getRectangles()[0]);
				}
			}
			tracker = null;
//			listenToDeactivate = oldListenToDeactivate;
		}

		public static Mover forShell(Shell shell) {
			return new Mover(shell);
		}
		
		public boolean isMoving() {
			return tracker != null;
		}
		
		
}