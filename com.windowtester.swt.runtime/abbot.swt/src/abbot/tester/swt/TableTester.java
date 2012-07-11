package abbot.tester.swt;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import abbot.Log;

import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.abbot.SWTWorkarounds;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;

/** 
 * Provides widget-specific actions for testing Tables.  Note that 
 * actionShowTableColumn, actionSelectTableColumn, and actionResizeTableColumn 
 * are currently platform-dependent.
 * 
 * !pq: robot-related fixes made.
 * 
 * @version $Id: TableTester.java,v 1.3 2007-11-27 17:17:39 pq Exp $
 */
public class TableTester extends CompositeTester{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";	
	
	/* Begin getters */	
	/**
	 * Proxy for {@link Table#getItem(int)}.
	 * <p/>
	 * @param table the table under test.
	 * @param index the index of the item.
	 * @return the item at the index.
	 */
	public TableItem getItem(final Table table, final int index){	
		TableItem result = (TableItem) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return table.getItem(index);
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link Table#getItem(org.eclipse.swt.graphics.Point)}.
	 * <p/>
	 * @param table the table under test.
	 * @param point the point to find the item under.
	 * @return the item at the point.
	 */
	public TableItem getItem(final Table table, final Point point){	
		TableItem result = (TableItem) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return table.getItem(point);
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link Table#getColumn(int)}.
	 * <p/>
	 * @param table the table under test.
	 * @param index the index of the column.
	 * @return the column at the index.
	 */
	public TableColumn getColumn(final Table table, final int index) {
		TableColumn result = (TableColumn) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return table.getColumn(index);
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Table#getColumnCount()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the number of columns of this table.
	 */
	public int getColumnCount(final Table table) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.getColumnCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Table#getColumns()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the table columns.
	 */
	public TableColumn[] getColumns(final Table table) {
		TableColumn[] result = (TableColumn[]) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return table.getColumns();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Table#getGridLineWidth()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the grid line width.
	 */
	public int getGridLineWidth(final Table table) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.getGridLineWidth());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Table#getHeaderHeight()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the header height.
	 */
	public int getHeaderHeight(final Table table) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.getHeaderHeight());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Table#getHeaderVisible()}.
	 * <p/>
	 * @param table the table under test.
	 * @return true if the header is visible.
	 */
	public boolean getHeaderVisible(final Table table) {
		Boolean result = (Boolean) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(table.getHeaderVisible());
			}
		});
		return result.booleanValue();
	}

	/**
	 * Proxy for {@link Table#getItemCount()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the number of rows in the table.
	 */
	public int getItemCount(final Table table) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.getItemCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Table#getItemHeight()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the height of the items.
	 */
	public int getItemHeight(final Table table) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.getItemHeight());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Table#getItems()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the items in the table.
	 */
	public TableItem[] getItems(final Table table) {
		TableItem[] result = (TableItem[]) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return table.getItems();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Table#getLinesVisible()}.
	 * <p/>
	 * @param table the table under test.
	 * @return true if the lines are visible.
	 */
	public boolean getLinesVisible(final Table table) {
		Boolean result = (Boolean) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(table.getLinesVisible());
			}
		});
		return result.booleanValue();
	}

	/**
	 * Proxy for {@link Table#getSelection()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the selected items.
	 */
	public TableItem[] getSelection(final Table table) {
		TableItem[] result = (TableItem[]) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return table.getSelection();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Table#getSelectionCount()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the number of selected items.
	 */
	public int getSelectionCount(final Table table) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.getSelectionCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Table#getSelectionIndex()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the index of the selected item.
	 */
	public int getSelectionIndex(final Table table) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.getSelectionIndex());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Table#getTopIndex()}.
	 * <p/>
	 * @param table the table under test.
	 * @return the index of the top item.
	 */
	public int getTopIndex(final Table table) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.getTopIndex());
			}
		});
		return result.intValue();
	}
	
	/**
	 * Proxy for {@link Table#indexOf(org.eclipse.swt.widgets.TableColumn)}.
	 * <p/>
	 * @param table the table under test.
	 * @param column the column to return the index for.
	 * @return the index of the column given.
	 */
	public int indexOf(final Table table, final TableColumn column) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.indexOf(column));
			}
		});
		return result.intValue();
	}
	/**
	 * Proxy for {@link Table#indexOf(org.eclipse.swt.widgets.TableItem)}.
	 * <p/>
	 * @param table the table under test.
	 * @param item the item to return the index for.
	 * @return the index of the item given.
	 */
	public int indexOf(final Table table, final TableItem item) {
		Integer result = (Integer) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(table.indexOf(item));
			}
		});
		return result.intValue();
	}
	/**
	 * Proxy for {@link Table#isSelected(int)}.
	 * <p/>
	 * @param table the table under test.
	 * @param index the index to return the selected property for.
	 * @return true if the index given is selected.
	 */
	public boolean isSelected(final Table table, final int index) {
		Boolean result = (Boolean) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return Boolean.valueOf(table.isSelected(index));
			}
		});
		return result.booleanValue();
	}
	
	
	/**
	 * @since 3.9.1
	 */
	public boolean isSelected(Table table, TableItem item) {
		TableItem[] items = getSelectedTableItems(table);
		for (int i = 0; i < items.length; i++) {
			if (items[i] == item)
				return true;
		}
		return false;
	}
	
	
	/* End getters */
	
	/** Returns all selected items in the given table **/
	public TableItem[] getSelectedTableItems(final Table table){
		TableItem[] result = (TableItem[]) Robot.syncExec(table.getDisplay(),new RunnableWithResult(){
			public Object runWithResult(){
				return table.getSelection();				
			}
		});
		return result;		
	}
	
	/** Returns the indices of all selected items in the given table **/
	public int[] getSelectedTableIndices(final Table table){
		int[] result = (int[]) Robot.syncExec(table.getDisplay(),new RunnableWithResult(){
			public Object runWithResult(){
				return table.getSelectionIndices();				
			}
		});
		return result;			
	}
	

	
	/** Returns the all checked items in the given table **/
	public TableItem[] getCheckedTableItems(final Table table){
		TableItem[] result = (TableItem[]) Robot.syncExec(table.getDisplay(),new RunnableWithResult(){
			public Object runWithResult(){
				ArrayList checked = new ArrayList();
				TableItem[] items = table.getItems();
				for(int i=0; i<items.length;i++){
					if(items[i].getChecked())
						checked.add(items[i]);			
				}
				return checked.toArray(new TableItem[checked.size()]);
			}
		});
		return result;		
	}

	/** Returns the indices of all checked items in the given table **/
	public int[] getCheckedTableIndices(final Table table){
		Integer[] result = (Integer[]) Robot.syncExec(table.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				ArrayList checked = new ArrayList();
				TableItem[] items = table.getItems();
				for(int i=0; i<items.length;i++){
					if(items[i].getChecked())
						checked.add(new Integer(i));			
				}
				return checked.toArray(new Integer[checked.size()]);
			}
		});
		int[] res = new int[result.length];
		for(int i=0; i<res.length;i++)
			res[i] = result[i].intValue();
		return res;
	}

	/** Checks if the SWT.CHECK style bit is set for the given table **/
	public boolean isCheckStyleBitSet(final Table table) {
		Boolean result = (Boolean) Robot.syncExec(table.getDisplay(),new RunnableWithResult(){
			public Boolean runWithResult(){
				return (table.getStyle() & SWT.CHECK) != 0;				
			}
		});
		return result;
	}

	/** Move the mouse pointer over the given TableItem **/
	protected synchronized void mouseMoveTableItem(final Table table, final TableItem item){
		Robot.syncExec(table.getDisplay(),this,new Runnable(){
			public void run(){
				table.setFocus();
				table.showItem(item);
				Rectangle bounds = WidgetLocator.getBounds(table,true);	
				int col0Width = table.getColumn(0).getWidth();
				int itemHeight = table.getItemHeight();
				Point p = new Point(col0Width/2,itemHeight/2);
				TableItem itemAtPoint=null;
				while(true){
					if(p.y>bounds.y+bounds.height)
						break;
					itemAtPoint = table.getItem(p);		
					if(itemAtPoint==item)
						break;		
					p.y+=itemHeight;
				}		
				if(itemAtPoint==item)
					mouseMove(bounds.x+p.x,bounds.y+p.y);
			}			
		});
		actionWaitForIdle(table.getDisplay());			
	}
	
	/** Move the mouse pointer over the TableItem at the given index **/
	protected synchronized void mouseMoveTableIndex(final Table table, final int index){
		//synchronized(this){
		Robot.syncExec(table.getDisplay(),this,new Runnable(){
				public void run(){
					objT = table.getItem(index);
				}
			});
		//}
		if(objT!=null)
			mouseMoveTableItem(table,(TableItem)objT);
	}
	
	/** Move the mouse pointer to the top of the given column **/
	protected void mouseMoveTopOfColumn(final Table table, final TableColumn col){
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				table.setFocus();
				TableColumn[] cols = table.getColumns();
				int width = 0;
				int i;
				for(i=0; i<cols.length;i++){
					width+=cols[i].getWidth();
					if(col==cols[i])
						break;									
				}
				if(col==cols[i]){
					int height = table.getHeaderHeight()/2;
					width-=col.getWidth()/2;
					Point loc = WidgetLocator.getLocation(table);
					mouseMove2(loc.x+width,loc.y+height);
				}
			}
		});
		actionWaitForIdle(table.getDisplay());			
	}
	
	
	/** Click an item in the given table.  NOTE:  Support has been added for the case 
	 * when a table is being used as a list and has no columns, but this may not work 
	 * in all cases depending on the alignment of the text in the table.  The problem 
	 * is that on some platforms only the text is clickable for purposes of making 
	 * a selection, but the current SWT API provides no method for getting the bounds 
	 * of this text. **/
	public void actionClickTableItem(final Table table, final TableItem item){
        actionClickTableItem(table,item,0);
	}
    
    public void actionClickTableItem(final Table table, final TableItem item ,final int column){
        Point point = (Point)Robot.syncExec(table.getDisplay(),new RunnableWithResult(){
            public Object runWithResult(){
                /* Give the Table focus. */
                table.setFocus();
                table.showItem(item);
                
                /* Get relative bounds for table and item. */
                Rectangle relativeBoundsOfItem = item.getBounds(column);
                Log.log("relativeBoundsOfItem["+column+"]:"+relativeBoundsOfItem);
                
                /* Determine relative click point. */
                int x = relativeBoundsOfItem.x + (relativeBoundsOfItem.width / 2);
                int y = relativeBoundsOfItem.y + (relativeBoundsOfItem.height / 2);
                
                /* Convert to real coordinates on Display. */
                Point point = table.toDisplay(x,y);
                
                return point;
            }           
        });
        Log.log("actionClickTableItem["+column+"]:"+point);
        mouseMove2(point.x,point.y);
        mousePress(SWT.BUTTON1);
        mouseRelease(SWT.BUTTON1);
        actionWaitForIdle(table.getDisplay());
    }
	
	/** Click the item at the given index **/
	public void actionClickTableIndex(final Table table, final int index){
		Robot.syncExec(table.getDisplay(),this,new Runnable(){
			public void run(){
				objT = table.getItem(index);
			}
		});
		if(objT!=null)
			actionClickTableItem(table,(TableItem)objT);
	}

	/** Click the given column's header (if headers are visible) **/
	public void actionClickTableColumnHeader(final Table table, TableColumn column){
		Robot.syncExec(table.getDisplay(),this,new Runnable(){
			public void run(){
				objT = new Boolean(table.getHeaderVisible());
			}
		});
		if(((Boolean)objT).booleanValue()){
			actionShowTableColumn(table,column); // also positions pointer above header
//!pq: keyClick fix
//			mousePress(SWT.BUTTON1);
//			mouseRelease(SWT.BUTTON1);
//			mousePress2(SWT.BUTTON1);
//			mouseRelease2(SWT.BUTTON1);
            new SWTMouseOperation(WT.BUTTON1).execute();
			actionWaitForIdle(table.getDisplay());
		}	
	}
	
	/** Select an item from the given table, or deselect it if it was already selected. **/
	public void actionSelectTableItem(final Table table, final TableItem item){
// Tao Weng 04/25/2005 10:39:05 AM:
// > I have to comment out "mouseMoveTableItem(table,item);" from
// > actionSelectTableItem. It seems to me that if I have that line
// > uncommented, my script just hangs.
//		mouseMoveTableItem(table,item);
		System.out.println("calling Robot syncExec()");
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				System.out.println("setting focus");
				table.setFocus();
				System.out.println("getting items");
				TableItem[] items = table.getItems();
				int i;
				for(i=0; i<items.length;i++){
					if(items[i]==item)
						break;
				}
				int index = i;
				if(items[i]==item){
					System.out.println("showing item: " + item);
					table.showItem(item);
					System.out.println("getting selected items");
					TableItem[] selectedItems = getSelectedTableItems(table);
					boolean selected =false;
					for(i=0; i<selectedItems.length;i++){
						if(selectedItems[i]==item){
							selected = true;
							System.out.println("deselecting item: " + i);
							table.deselect(i);	
							break;
						}
					}
					if(!selected) {
						System.out.println("selecting item: " + index);
						table.select(index);
					}
				}				
			}
		});
		System.out.println("start wait for idle");
		actionWaitForIdle(table.getDisplay());
		System.out.println("stop wait for idle");
	}
	
	/** Select the item at the given index from the table, or deselect it if it was already selected.  **/
	public void actionSelectTableIndex(final Table table, final int index){
		//!pq: see note above
		//mouseMoveTableIndex(table,index);
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				table.setFocus();
				TableItem item = null;
				try{item = table.getItem(index);}				
				catch(Exception ignored){}
				if(item!=null){
					table.showItem(item);
					TableItem[] selectedItems = getSelectedTableItems(table);
					boolean selected =false;
					for(int i=0; i<selectedItems.length;i++){
						if(selectedItems[i]==item){
							selected = true;
							table.deselect(i);	
							break;
						}
					}
					if(!selected)
						table.select(index);	
				}				
			}
		});
		actionWaitForIdle(table.getDisplay());
	}	
	
	/** Put the item as high as possible in the given table's viewing window **/
	public void actionShowTableItem(final Table table, final TableItem item) {
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				table.setFocus();
				table.showItem(item);
				// Commented this out since it seems not to work. If any objections,
				// please complain, rbirenheide
//				TableItem[] items = table.getItems();
//				int i;
//				for(i=0; i<items.length;i++){
//					if(items[i]==item)
//						break;
//				}
//				if(items[i]==item){
//					table.setTopIndex(i);	
//				}									
			}
		});
		actionWaitForIdle(table.getDisplay());		
	}
	
	/** Put the item at the given index as high as possible in the table's viewing window **/
	public void actionShowTableIndex(final Table table, final int index){
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				table.setFocus();
				table.setTopIndex(index);					
			}
		});
		actionWaitForIdle(table.getDisplay());		
	}
	
	/** 
	 * Scroll so that the given table column is visible, and place the mouse over the
	 * column's heading, if visible.
	 **/
	public void actionShowTableColumn(final Table table, final TableColumn column){
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				table.setFocus();
				ScrollBar scrollBar = table.getHorizontalBar();
				
				int width = 0;
				int totalWidth = 0;
				TableColumn[] cols = table.getColumns();
				for(int i=0; i<cols.length;i++){
					totalWidth+=cols[i].getWidth();
				}
				for(int i=0; i<cols.length&&cols[i]!=column;i++){
					width+=cols[i].getWidth();					
				}
				width+=column.getWidth()/2;
				
				ScrollBarTester tester = (ScrollBarTester)WidgetTester.getTester(ScrollBar.class);
				Rectangle scrollBounds = SWTWorkarounds.getBounds(scrollBar);
				
				int max = scrollBar.getMaximum();
				int min = scrollBar.getMinimum();
				
				int tableWidth = table.getBounds().width;
				int setScroll =(int)( (double)((double)width /(double)totalWidth)*(max-min))-tableWidth/2;
//				System.out.println(	"setScroll="+setScroll+" min="
//									+scrollBar.getMinimum()+" max="+scrollBar.getMaximum()
//									+" thumb="+scrollBar.getThumb()
//									+" width="+width+" totalWidth="+totalWidth
//									+" tableWidth="+tableWidth+"scrollWidth="+scrollBounds.width);
								
				tester.actionScrollSetSelection(scrollBar,setScroll);

				// now move mouse to top of column
				int selection = scrollBar.getSelection();			
				//int thumb = scrollBar.getThumb();
				double thumbEdge = (double)((double)selection/(double)(max-min));
				int leftEdgeOfTableShowing = (int)(thumbEdge*totalWidth);
	
				Point tableLoc = WidgetLocator.getLocation(table);				
			
				//System.out.println("Selection="+selection+" leftEdge...="+leftEdgeOfTableShowing);
				int offset = width - leftEdgeOfTableShowing;
				if(offset>=scrollBounds.width){
					System.out.println("offset to large("+offset+")");	
					offset = scrollBounds.width-2;
				}
				mouseMove2(	tableLoc.x+(offset),
							tableLoc.y+table.getHeaderHeight()/2);
			}
		});
		actionWaitForIdle(table.getDisplay());		
	}
	
	/** Check the check box for an item in the given table,, or uncheck it if it 
	 *  was already checked.  
	 **/
	public void actionCheckTableItem(final Table table, final TableItem item){
		//!pq: see note above
		//mouseMoveTableIndex(table,index);
		System.out.println("start checking table item");
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				table.setFocus();
				table.showItem(item);
				if(item.getChecked())
					item.setChecked(false);
				else
					item.setChecked(true);					
			}
		});
		actionWaitForIdle(table.getDisplay());		
		System.out.println("end checking table item");
	}
	
	/** Check the check box for the item at the given index in the table, or
	 *  uncheck it if it was already checked. 
	 **/
	public void actionCheckTableIndex(final Table table, final int index){
		//!pq: see note above
		//mouseMoveTableIndex(table,index);
		System.out.println("start checking table item");
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				table.setFocus();
				TableItem[] items = table.getItems();
				if(index>=0 && index<items.length){
					table.showItem(items[index]);
					if(items[index].getChecked())
						items[index].setChecked(false);
					else
						items[index].setChecked(true);					
				}						
			}
		});
		actionWaitForIdle(table.getDisplay());	
		System.out.println("end checking table item");
	}

	/** Resize the column in the given table **/
	public void actionResizeTableColumn(final Table table, final TableColumn column, final int width){
		actionShowTableColumn(table,column);
		mouseMoveTopOfColumn(table,column);
		Robot.syncExec(table.getDisplay(),null,new Runnable(){
			public void run(){
				table.setFocus();
				column.setWidth(width);
				// TODO need to make the column visible here and maybe move the mouse over it									
			}
		});
		actionWaitForIdle(table.getDisplay());		
		mouseMoveTopOfColumn(table,column);
		actionWaitForIdle(table.getDisplay()); 
	}



}
