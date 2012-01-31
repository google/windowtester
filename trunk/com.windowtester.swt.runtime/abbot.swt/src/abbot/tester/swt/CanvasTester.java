
package abbot.tester.swt;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Canvas.
 */
public class CanvasTester extends CompositeTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/*
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget   
	 */ 
	/* Begin getters */
	/**
	 * Proxy for {@link Canvas#getCaret()}.
	 * <p/>
	 * @param canvas the canvas under test.
	 * @return the caret.
	 */
	public Caret getCaret(final Canvas canvas) {
		Caret result = (Caret) Robot.syncExec(canvas.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return canvas.getCaret();
			}
		});
		return result;
	}
	/* End getters */
    
    /**
     * Proxy for {@link Canvas.scroll(int destX, int destY, int x, int y, int width, int height, boolean all)}
     */
    public void scroll(final Canvas c, final int destX, final int destY, final int x, final int y, final int width, final int height, final boolean all) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.scroll(destX, destY, x, y, width, height, all);
            }
        });
    }
    
    /**
     * Proxy for {@link Canvas.setCaret(Caret caret)}
     */
    public void setCaret(final Canvas c, final Caret caret) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setCaret(caret);
            }
        });
    }
    
    /**
     * Proxy for {@link Canvas.setFont(Font font)}
     */
    public void setFont(final Canvas c, final Font font) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setFont(font);
            }
        });
    }
}
