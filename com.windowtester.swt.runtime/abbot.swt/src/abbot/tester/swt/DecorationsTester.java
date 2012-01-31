
package abbot.tester.swt;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Decorations.
 */
public class DecorationsTester extends CanvasTester{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/**
	 * Proxy for {@link Decorations#getBounds()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the bounds.
	 */
	public Rectangle getBounds(final Decorations decorations) {
		Rectangle result = (Rectangle) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getBounds();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Decorations#getClientArea()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the client area bounds.
	 */
	public Rectangle getClientArea(final Decorations decorations) {
		Rectangle result = (Rectangle) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getClientArea();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Decorations#getDefaultButton()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the default button.
	 */
	public Button getDefaultButton(final Decorations decorations) {
		Button result = (Button) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getDefaultButton();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Decorations#getImage()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the image.
	 */
	public Image getImage(final Decorations decorations) {
		Image result = (Image) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getImage();
			}
		});
		return result;
	}
    
	/**
	 * Proxy for {@link Decorations#getImages()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the images.
	 */
	public Image[] getImages(final Decorations decorations) {
		Image[] result = (Image[]) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getImages();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Decorations#getLocation()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the loacation.
	 */
	public Point getLocation(final Decorations decorations) {
		Point result = (Point) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getLocation();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Decorations#getMaximized()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the maximized state.
	 */
	public boolean getMaximized(final Decorations decorations) {
		Boolean result = (Boolean) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(decorations.getMaximized());
			}
		});
		return result.booleanValue();
	}

	/**
	 * Proxy for {@link Decorations#getMenuBar()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the menu bar.
	 */
	public Menu getMenuBar(final Decorations decorations) {
		Menu result = (Menu) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getMenuBar();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Decorations#getMinimized()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the minimized state.
	 */
	public boolean getMinimized(final Decorations decorations) {
		Boolean result = (Boolean) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(decorations.getMinimized());
			}
		});
		return result.booleanValue();
	}

	/**
	 * Proxy for {@link Decorations#getSize()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the size of the decorations.
	 */
	public Point getSize(final Decorations decorations) {
		Point result = (Point) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getSize();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Decorations#getText()}.
	 * <p/>
	 * @param decorations the decorations under test.
	 * @return the text.
	 */
	public String getText(final Decorations decorations) {
		String result = (String) Robot.syncExec(decorations.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return decorations.getText();
			}
		});
		return result;
	}
}
