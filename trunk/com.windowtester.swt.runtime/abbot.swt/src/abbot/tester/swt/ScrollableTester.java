
package abbot.tester.swt;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Scrollable.
 */
public class ScrollableTester extends ControlTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/* Begin getters */
	/**
	 * Proxy for {@link Scrollable#getClientArea()}.
	 * <p/>
	 * @param s the scrollable under test.
	 * @return the client area
	 */
	public Rectangle getClientArea(final Scrollable s) {
		Rectangle result = (Rectangle) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return s.getClientArea();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Scrollable#getHorizontalBar()}.
	 * <p/>
	 * @param s the scrollable under test.
	 * @return the horizontal bar.
	 */
	public ScrollBar getHorizontalBar(final Scrollable s) {
		ScrollBar result = (ScrollBar) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return s.getHorizontalBar();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Scrollable#getVerticalBar()}.
	 * <p/>
	 * @param s the scrollable under test.
	 * @return the vertical bar.
	 */
	public ScrollBar getVerticalBar(final Scrollable s) {
		ScrollBar result = (ScrollBar) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return s.getVerticalBar();
			}
		});
		return result;
	}
	/* End getters */

}
