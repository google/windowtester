package abbot.tester.swt;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;

import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.selector.ListHelper;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type List.
 */
public class ListTester extends ScrollableTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
    /**
     * Proxy for
     * {@link List#addSelectionListener(SelectionListener)}.
     */
    public void addSelectionListener(final List l, final SelectionListener listener) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.addSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link List#removeSelectionListener(SelectionListener)}.
     */
    public void removeSelectionListener(final List l, final SelectionListener listener) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.removeSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for {@link List#getItem(int i)}.
     */
    public String getItem(final List l, final int i) {
        String result = (String) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return l.getItem(i);
                    }
                });
        return result;
    }
    
    /**
     * Proxy for {@link List#getItemCount()}.
     */
    public int getItemCount(final List l) {
        Integer result = (Integer) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(l.getItemCount());
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link List#getItemHeight()}.
     */
    public int getItemHeight(final List l) {
        Integer result = (Integer) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(l.getItemHeight());
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link List#getItems()}.
     */
    public String [] getItems(final List l) {
        java.util.List result = (java.util.List) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        String [] items = l.getItems();
                        java.util.List list = new ArrayList(items.length);
                        //!pq: fix to actually *add* the items...
                        list.addAll(Arrays.asList(items));
                        return list;
                    }
                });
        String [] items = new String [result.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = (String)result.get(i);
        }
        return items;
    }
    
    /**
     * Proxy for {@link List#getSelection()}.
     */
    public String [] getSelection(final List l) {
        java.util.List result = (java.util.List) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        String [] items = l.getSelection();
                        java.util.List list = new ArrayList(items.length);
                        //!pq: fix to actually *add* the items...
                        list.addAll(Arrays.asList(items));
                        return list;
                    }
                });
        String [] items = new String [result.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = (String)result.get(i);
        }
        return items;
    }	
    
    /**
     * Proxy for {@link List#getSelectionCount()}.
     */
    public int getSelectionCount(final List l) {
        Integer result = (Integer) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(l.getSelectionCount());
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link List#getSelectionIndex()}.
     */
    public int getSelectionIndex(final List l) {
        Integer result = (Integer) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(l.getSelectionIndex());
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link List#getSelectionIndices()()}.
     */
    public int [] getSelectionIndices(final List l) {
        java.util.List result = (java.util.List) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        int [] items = l.getSelectionIndices();
                        //a little autoboxing would be nice!
                        java.util.List list = new ArrayList(items.length);
                        //!pq: fix to actually *add* the items...
                        for (int i=0; i < items.length; ++i)
                        	list.add(new Integer(items[i]));
                        return list;
                    }
                });
        int [] items = new int [result.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = ((Integer)result.get(i)).intValue();
        }
        return items;
    }
    
    /**
     * Proxy for {@link List#getTopIndex()}.
     */
    public int getTopIndex(final List l) {
        Integer result = (Integer) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(l.getTopIndex());
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link List#indexOf(String)}
     */
    public int indexOf(final List l, final String s) {
        Integer result = (Integer) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(l.indexOf(s));
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link List#indexOf(String,int)}
     */
    public int indexOf(final List l, final String s, final int i) {
        Integer result = (Integer) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(l.indexOf(s,i));
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link List#isSelected(int)}
     */
    public boolean isSelected(final List l, final int i) {
        Boolean result = (Boolean) Robot.syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Boolean(l.isSelected(i));
                    }
                });
        return result.booleanValue();
    }
    
    /**
     * Proxy for
     * {@link List#showSelection()}.
     */
    public void showSelection(final List l) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.showSelection();
            }
        });
    }
    
    /**
     * Proxy for
     * {@link List#select(int)}.
     */
    public void select(final List l, final int i) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.select(i);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link List#select(int,int)}.
     */
    public void select(final List l, final int start, final int end) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.select(start,end);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link List#select(int [])}.
     */
    public void select(final List l, final int [] i) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.select(i);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link List#selectAll()}.
     */
    public void selectAll(final List l) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.selectAll();
            }
        });
    }
    
	/** Move the mouse pointer over the item at the given index **/
	protected void mouseMoveIndex(final List list, final int index){
		actionFocus(list);
		Robot.syncExec(list.getDisplay(),null,new Runnable(){
			public void run(){
				list.setTopIndex(index);
			}
		});
		int topIndex = getTopIndex(list);
		int borderWidth = getBorderWidth(list);
		int itemHeight = getItemHeight(list);
		Rectangle bounds = getGlobalBounds(list);
		Rectangle clientArea = getClientArea(list);
		itemHeight = ListHelper.fixItemHeight(list.getDisplay(), itemHeight);
		mouseMove2(bounds.x+clientArea.width/2,bounds.y+borderWidth+itemHeight*(index-topIndex)+itemHeight/2);		
		actionWaitForIdle(list.getDisplay());
	}

	/** Click the first occurance given item **/
	public void actionClickItem(List list, String item){
		//FIXME[author=pq]: button presses are not selecting; KeyEvents work though...
		actionClickItem(list,item,SWT.BUTTON1);
	}

	public void actionDoubleClickItem(List list, String item) {
		actionDoubleClickItem(list, item, SWT.BUTTON1);
	}
	
	//!pq:
	public void actionDoubleClickItem(List list, String item, int accelerator){
		String[] items = getItems(list);
		int index = -1;
		for(int i=0; i<items.length;i++){
			if(item.equals(items[i])&&index==-1)
				index = i;
		}
		if(index==-1){
			throw new ActionFailedException("List item \""+item+"\" not found");
		}
		actionDoubleClickIndex(list, index);
	}	
	
	
	/** Click the item at the given index **/
	public void actionClickIndex(List list, int index){
		actionClickIndex(list,index,SWT.BUTTON1);
	}
	
	/** Click the first occurance of given item based on the given accelerator **/
	public void actionClickItem(List list, String item, int accelerator){
		String[] items = getItems(list);
		int index = -1;
		for(int i=0; i<items.length;i++){
			if(item.equals(items[i])&&index==-1)
				index = i;
		}
		if(index==-1){
			throw new ActionFailedException("List item \""+item+"\" not found");
		}
		actionClickIndex(list,index,accelerator);	
	}
	
	/** Click the item at the given index based on the given accelerator **/
	public void actionClickIndex(List list, int index, int accelerator){
		actionFocus(list);
		//System.out.println("moving mouse");
		mouseMoveIndex(list,index);
		//System.out.println("...done moving mouse");
		//System.out.println("waiting for idle");
		actionWaitForIdle(list.getDisplay());
		//System.out.println("...done waiting for idle");
		
		//System.out.println("clicking key");
//		!pq: keyClick fix
//		mousePress(accelerator);
//		mouseRelease(accelerator);
		
		
//		mousePress2(accelerator);
//		mouseRelease2(accelerator);
		new SWTMouseOperation(accelerator).execute();
		
		//keyClick(accelerator);
		//System.out.println("...done clicking key");
		//System.out.println("waiting for idle");
		actionWaitForIdle(list.getDisplay());
		//System.out.println("...done waiting for idle");
	}	


	/** 
	 * Select the first occurance of an item from the given list if it wasn't already 
	 * selected, and unselect it otherwise. 
	 */
	public void actionSelectItem(List list, String item){
		String[] items = getItems(list);
		int index = -1;		
		for(int i=0; i<items.length;i++){
			if(item.equals(items[i])&&index==-1)
				index = i;			
		}
		if(index==-1){
			throw new ActionFailedException("List item \""+item+"\" not found");
		}
		actionSelectIndex(list,index);	
	}
	
	/** 
	 * Selects the item at the given index, or unselects it if it was 
	 * already selected.
	 */
	public void actionSelectIndex(final List list, final int index){
		actionFocus(list);
		mouseMoveIndex(list,index);
		Robot.syncExec(list.getDisplay(),null,new Runnable(){
			public void run(){
				int[] selectionIndices = list.getSelectionIndices();
				boolean selected = false;
				//int indexIntoSelected = -1;
				for(int i=0; i<selectionIndices.length;i++){
					if(selectionIndices[i]==index){
						selected = true;
						//indexIntoSelected = i;
					}
				}
				if(selected){
					int[] setSelected = new int[selectionIndices.length-1];
					int x = 0;
					for(int i=0; i<setSelected.length;i++){
						if(selectionIndices[i]!=index){
							setSelected[x] = selectionIndices[i];
							x++;
						}						
					}
					list.setSelection(setSelected);
				}
				else{//!selected
					int[] setSelected = new int[selectionIndices.length+1];
					for(int i=0; i<selectionIndices.length;i++){
						setSelected[i] = selectionIndices[i];			
					}
					setSelected[setSelected.length-1]=index;
					list.setSelection(setSelected);
				}			
			}
		});
		actionWaitForIdle(list.getDisplay());
	}

	/**
	 * Double click the item at the given index.
	 * @author Markus Kuhn <markuskuhn@users.sourceforge.net>
	 */
	public void actionDoubleClickIndex(List list, int index){
		actionDoubleClickIndex(list,index,SWT.BUTTON1);
	}

	/*
	 * @author Markus Kuhn <markuskuhn@users.sourceforge.net>
	 */
	public void actionDoubleClickIndex(List list, int index, int accelerator){
		Display display = list.getDisplay();
		actionFocus(list);
		mouseMoveIndex(list,index);
		actionWaitForIdle(display);
//		!pq: keyClick fix
//		mousePress(accelerator);
//		mouseRelease(accelerator);
//		keyClick(accelerator);
//		mousePress2(accelerator);
		new SWTMouseOperation(accelerator).execute();
		delay(50);
//		!pq: keyClick fix
//		mousePress(accelerator);
//		mouseRelease(accelerator);
//		keyClick(accelerator);
//		mousePress2(accelerator);
		new SWTMouseOperation(accelerator).execute();
		actionWaitForIdle(display);		
	}	

}
