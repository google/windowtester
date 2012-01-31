package abbot.tester.swt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author nntp_ds@fastmail.fm
 * @version $Id: TableItemTester.java,v 1.1 2005-12-19 20:28:31 pq Exp $
 */
public class TableItemTester extends ItemTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";

	/** properties **/
	protected String[] text;
		
	/**
	 * Sets the above properties to their current values for the given widget. 
	 * NOTE: This should be called in a block of code synchronized on this
	 * tester.
	 */	
//	protected synchronized void getProperties(final TableItem item){
//		// 08/04/2004 nntp_ds@fastmail.fm: For now I am just implementing
//		// this like the Testers are implemented. However seems to me
//		// this pattern accomplishes little more than reducing the amount
//		// of code one needs to write, because with each call to an
//		// accessor everything is re-fetched, even if it isn't needed.
//		// This pattern is not reducing thread shuttling.
//		super.getProperties(item);
//		Robot.syncExec(item.getDisplay(),this,new Runnable() {
//			public void run() {
//				final Table tbl = item.getParent();
//				int width = tbl.getColumnCount();
//				// is this the best way to handle Table's without columns?
//				if (width <= 0) width = 1;
//				text = new String[width];
//				for (int i = 0; i < width; i++) {
//						text[i] = item.getText(i);
//				}
//			}
//		});
//	}

	/*
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget		
	 */ 
	/* Begin getters */
	public static TableItemTester getTableItemTester() {
		return (TableItemTester)(getTester(TableItem.class));
	}

/*
 * getText() not necessary, already done in ItemTester.
 */
	/**
	 * Proxy for {@link TableItem#getBackground()}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @return the background color.
	 */
	public Color getBackground(final TableItem item) {
		Color result = (Color) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getBackground();
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link TableItem#getBackground(int)}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @param column the column to check.
	 * @return the background color.
	 */
	public Color getBackground(final TableItem item, final int column) {
		Color result = (Color) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getBackground(column);
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link TableItem#getBounds(int)}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @param column the column to check.
	 * @return the bounds of the column within the item.
	 */
	public Rectangle getBounds(final TableItem item, final int column) {
		Rectangle result = (Rectangle) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getBounds(column);
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link TableItem#getChecked()}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @return the checked status of the checkbox.
	 */
	public boolean getChecked(final TableItem item){
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(item.getChecked());				
			}
		});
		return result.booleanValue();		
	}
	/**
	 * Proxy for {@link TableItem#getFont()}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @return the font of the item under test.
	 */
	public Font getFont(final TableItem item) {
		Font result = (Font) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getFont();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link TableItem#getFont(int)}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @param column the column to check.
	 * @return the font of the column within the item.
	 */
	public Font getFont(final TableItem item, final int column) {
		Font result = (Font) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getFont(column);
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link TableItem#getForeground()}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @return the foreground color.
	 */
	public Color getForeground(final TableItem item) {
		Color result = (Color) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getForeground();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link TableItem#getForeground(int)}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @param column the column to check
	 * @return the foreground color of the column within the item.
	 */
	public Color getForeground(final TableItem item, final int column) {
		Color result = (Color) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getForeground(column);
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link TableItem#getGrayed()}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @return the grayed state of the checkbox.
	 */
	public boolean getGrayed(final TableItem item){
		Boolean result = (Boolean) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(item.getGrayed());				
			}
		});
		return result.booleanValue();		
	}
	/**
	 * Proxy for {@link TableItem#getImageBounds(int)}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @param column the column to check.
	 * @return the image bounds of the column within the item.
	 */
	public Rectangle getImageBounds(final TableItem item, final int column) {
		Rectangle result = (Rectangle) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getImageBounds(column);
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link TableItem#getImageIndent()}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @return the image indent.
	 */
	public int getImageIndent(final TableItem item){
		Integer result = (Integer) Robot.syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(item.getImageIndent());				
			}
		});
		return result.intValue();	
	}
	/**
	 * Proxy for {@link TableItem#getParent()}.
	 * <p/>
	 * @param item the TableItem under test.
	 * @return the parent table of the item under test.
	 */
	public Table getParent(final TableItem item) {
		Table result = (Table) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getParent();
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link TableItem#getText(int)}.
	 * <p/>
	 * @param item the item to retrieve the text from.
	 * @param col the column for which the text will be retrieved.
	 * @return the text.
	 */
	public String getText(final TableItem item, final int col ){
		String result = (String) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getText(col);
			}
		});
		return result;
	}
	/**
	 * Proxy for {@link TableItem#getImage(int)}.
	 * <p/>
	 * @param item the item to retrieve the image from.
	 * @param col the column for which the image will be retrieved.
	 * @return the image for the column.
	 */
	public Image getImage(final TableItem item, final int col) {
		Image result = (Image) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return item.getImage(col);
			}
		});
		return result;
	}
	/* End getters */
	
	/**
	 * Clicks in the center of the TableItem and the column given.
	 * <p/>
	 * @param item the item to click onto.
	 * @param columnIndex the column to click onto.
	 */
	public void actionClickTableItem(final TableItem item, final int columnIndex) {
		Robot.syncExec(item.getDisplay(), this, new Runnable() {
			public void run() {
				if (item.getParent().getColumnCount() < columnIndex || columnIndex < 0) {
					return;
				}
				Rectangle itemBounds = item.getBounds(columnIndex);
				TableItemTester.this.actionClick(item.getParent(), 
						                             itemBounds.x + itemBounds.width/2, 
																				 itemBounds.y + itemBounds.height/2);
			}
		});
	}
}
