/*
 * Created on 30.05.2005
 * by Richard Birenheide
 *
 * Copyright SAP AG 2005
 */
package abbot.tester.swt;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Test for ToolItems.
 * <p/>
 * @author Richard Birenheide
 */
public class ToolItemTester extends ItemTester {
	
	/**
	 * Proxy for {@link ToolItem#getBounds()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return the bounding rectangle relative to the parent.
	 */
	public Rectangle getBounds(final ToolItem item) {
		Rectangle result = (Rectangle) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getBounds();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link ToolItem#getControl()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return the control for the separator.
	 */
	public Control getControl(final ToolItem item) {
		Control result = (Control) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getControl();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link ToolItem#getDisabledImage()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return the disabled image of the item.
	 */
	public Image getDisabledImage(final ToolItem item) {
		Image result = (Image) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getDisabledImage();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link ToolItem#getEnabled()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return the enabled state of the item.
	 */
	public boolean getEnabled(final ToolItem item) {
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(item.getEnabled());
			}
		});
		return result.booleanValue();
	}
	/**
	 * Proxy for {@link ToolItem#getHotImage()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return the hot image of the item.
	 */
	public Image getHotImage(final ToolItem item) {
		Image result = (Image) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getHotImage();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link ToolItem#getParent()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return the parent of the item.
	 */
	public ToolBar getParent(final ToolItem item) {
		ToolBar result = (ToolBar) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getParent();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link ToolItem#getSelection()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return true if the item is selected.
	 */
	public boolean getSelection(final ToolItem item) {
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(item.getSelection());
			}
		});
		return result.booleanValue();
	}
	/**
	 * Proxy for {@link ToolItem#getToolTipText()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return the tool tip text of the item.
	 */
	public String getToolTipText(final ToolItem item) {
		String result = (String) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getToolTipText();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link ToolItem#getWidth()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return the width of the item.
	 */
	public int getWidth(final ToolItem item) {
		Integer result = (Integer) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(item.getWidth());
			}
		});
		return result.intValue();	
	}
	/**
	 * Proxy for {@link ToolItem#isEnabled()}.
	 * <p/>
	 * @param item the tool item under test.
	 * @return true if the item and all of its ancestors are enabled.
	 */
	public boolean isEnabled(final ToolItem item) {
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(item.isEnabled());
			}
		});
		return result.booleanValue();
	}
}
