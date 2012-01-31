
package abbot.tester.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Shell.
 */
public class ShellTester extends DecorationsTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/**
	 * Proxy for {@link Shell#getImeInputMode()}. 
	 * <p/>
	 * @param shell the shell under test.
	 * @return the input mode.
	 */
	public int getImeInputMode(final Shell shell) {
		Integer result = (Integer) Robot.syncExec(shell.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(shell.getImeInputMode());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Shell#getShell()}.
	 * <p/>
	 * @param shell the shell under test.
	 * @return the parent shell.
	 */
	public Shell getShell(final Shell shell) {
		Shell result = (Shell) Robot.syncExec(shell.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return shell.getShell();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Shell#getShells()}.
	 * <p/>
	 * @param shell the shell under test.
	 * @return the child shells.
	 */
	public Shell[] getShells(final Shell shell) {
		Shell[] result = (Shell[]) Robot.syncExec(shell.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return shell.getShell();
			}
		});
		return result;
	}
	/* End getters */

	/**
	 * Factory method.
	 */
	public static ShellTester getShellTester() {
		return (ShellTester)(getTester(Shell.class));
	}
	
	/**
	 * This method will see if a Shell has a modal style.
	 * 
	 * SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL | SWT.PRIMARY_MODAL
	 * 
	 * @param shell
	 * @return true if the Shell has a modal style.
	 */
	public static boolean isModal(final Shell shell) {
		Boolean isModal = (Boolean) Robot.syncExec(shell.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				int style = shell.getStyle();
		        if (style <= 0) return Boolean.FALSE;
		        int bitmask = SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL | SWT.PRIMARY_MODAL;
		        if ((style & bitmask) > 0) return Boolean.TRUE;
		        return Boolean.FALSE;
			}
		});
		return isModal.booleanValue();
	}

}
