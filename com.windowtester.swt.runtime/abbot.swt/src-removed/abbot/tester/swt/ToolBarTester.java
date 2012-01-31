
package abbot.tester.swt;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type ToolBar.
 */
public class ToolBarTester extends CompositeTester{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/* Widget properties that are obtainable by member getter functions */
//	protected ToolItem item;
//	protected int itemCount;
//	protected ToolItem[] items;
//	protected int rowCount;
	
//	/**
//	 * Sets the above properties to their current values for the given widget. 
//	 * NOTE: This should be called in a block of code synchronized on this
//	 * tester.
//	 */	
//	protected synchronized void getProperties(final ToolBar bar){
//		super.getProperties(bar);
//		Robot.syncExec(bar.getDisplay(),this,new Runnable(){
//			public void run(){
//				itemCount = bar.getItemCount();
//				items = bar.getItems();
//				rowCount = bar.getRowCount();
//			}
//		});
//	}

	/*
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget   
	 */ 
	/* Begin getters */	
	
	/**
	 * Proxy for {@link ToolBar#getItem(int)}.
	 * <p/>
	 * @param bar the toolbar under test.
	 * @param index the index for the item.
	 * @return the item at the index given.
	 */
	public ToolItem getItem(final ToolBar bar, final int index){
		ToolItem result = (ToolItem) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return bar.getItem(index);
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link ToolBar#getItem(org.eclipse.swt.graphics.Point)}.
	 * <p/>
	 * @param bar the toolbar under test.
	 * @param point the point to locate an item.
	 * @return the item under the point.
	 */
	public ToolItem getItem(final ToolBar bar, final Point point){
		ToolItem result = (ToolItem) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return bar.getItem(point);
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link ToolBar#getItemCount()}.
	 * <p/>
	 * @param bar the toolbar under test.
	 * @return the number of items in the toolbar.
	 */
	public int getItemCount(final ToolBar bar){
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.getItemCount());
			}
		});
		return result.intValue();	
	}

	/**
	 * Proxy for {@link ToolBar#getItems()}.
	 * <p/>
	 * @param bar the toolbar under test.
	 * @return the items in the toolbar.
	 */
	public ToolItem[] getItems(final ToolBar bar){
		ToolItem[] result = (ToolItem[]) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return bar.getItems();
			}
		});
		return result;
	}	
	
	/**
	 * Proxy for {@link ToolBar#getRowCount()}.
	 * <p/>
	 * @param bar the toolbar under test.
	 * @return the number of rows.
	 */
	public int getRowCount(final ToolBar bar){
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.getRowCount());
			}
		});
		return result.intValue();
	}
	/**
	 * Proxy for {@link ToolBar#indexOf(org.eclipse.swt.widgets.ToolItem)}.
	 * <p/>
	 * @param bar the toolbar under test.
	 * @param item the search item.
	 * @return the index of the item.
	 */
	public int indexOf(final ToolBar bar, final ToolItem item) {
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.indexOf(item));
			}
		});
		return result.intValue();
	}
	/* End getters */
}
