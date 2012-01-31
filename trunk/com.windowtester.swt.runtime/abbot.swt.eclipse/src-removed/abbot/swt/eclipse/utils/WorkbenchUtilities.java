package abbot.swt.eclipse.utils;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import abbot.tester.swt.Robot;
import abbot.tester.swt.RunnableWithResult;

/**
 * Contains utilities for getting and moving workbenches.
 * 
 * @author ktdale
 * @version $Id: WorkbenchUtilities.java,v 1.1 2005-12-19 20:28:33 pq Exp $
 */
public class WorkbenchUtilities {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	public static IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	public static IWorkbenchWindow getWorkbenchWindow() {
		IWorkbenchWindow result = (IWorkbenchWindow) 
				Robot.syncExec(getWorkbench().getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			}
		}); 
		return result;
	}

	/**
	 * Brings the workbench to the top of the drawing order so that it
	 * receives keyboard focus.
	 * 
	 */
	public static void bringWorkbenchToFront() {
		Shell workbenchShell = getWorkbenchWindow().getShell(); 
		bringToFront(workbenchShell);
	}

	/**
	 * Brings the passed Shell to the top of the drawing order so that it
	 * receives keyboard focus.
	 */
	public static void bringToFront(final Shell s) {
		Robot.syncExec(s.getDisplay(), null, new Runnable(){
			public void run(){
				s.forceActive();
				s.setFocus();
			}
		});
		Robot.waitForIdle(s.getDisplay());
	}

}
