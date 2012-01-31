
package abbot.tester.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.operation.SWTControlLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;

import abbot.Log;
import abbot.script.Condition;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Combo.
 */
public class ComboTester extends CompositeTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/* Begin getters */
	/**
	 * Proxy for {@link Combo#getItemCount()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return the number of items.
	 */
	public int getItemCount(final Combo combo) {
		Integer result = (Integer) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(combo.getItemCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Combo#getItemHeight()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return the height of one item.
	 */
	public int getItemHeight(final Combo combo) {
		Integer result = (Integer) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(combo.getItemHeight());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Combo#getItems()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return the items in the combo's list.
	 */
	public String[] getItems(final Combo combo) {
		String[] result = (String[]) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return combo.getItems();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Combo#getSelection()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return a point representing the selection start and end.
	 */
	public Point getSelection(final Combo combo) {
		Point result = (Point) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return combo.getSelection();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Combo#getSelectionIndex()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return the selected index.
	 */
	public int getSelectionIndex(final Combo combo) {
		Integer result = (Integer) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(combo.getSelectionIndex());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Combo#getText()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return the contents of the text field.
	 */
	public String getText(final Combo combo) {
		String result = (String) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return combo.getText();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Combo#getTextHeight()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return the text height.
	 */
	public int getTextHeight(final Combo combo) {
		Integer result = (Integer) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(combo.getTextHeight());
			}
		});
		return result.intValue();
	}
    
	/**
	 * Proxy for {@link Combo#getTextLimit()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return the text limit.
	 */
	public int getTextLimit(final Combo combo) {
		Integer result = (Integer) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(combo.getTextLimit());
			}
		});
		return result.intValue();
	}
	/* End getters */
	
	/** 
	 * Drop down the menu for the given Combo box 
	 * WARNING: This method is platform-dependent.
	 */
	protected void dropDownCombo(Combo combo){
//		int style = getStyle(combo);
//		final int BUTTON_SIZE = 16;
//		if((style&SWT.DROP_DOWN)==SWT.DROP_DOWN){
//			Rectangle bounds = getGlobalBounds(combo);
//			int x = bounds.x+bounds.width-BUTTON_SIZE/2;
//			int y = bounds.y+bounds.height-BUTTON_SIZE/2;
			
//			//!pq: event posting fixes
//			//mouseMove(	bounds.x+bounds.width-BUTTON_SIZE/2,
//			//			bounds.y+bounds.height-BUTTON_SIZE/2);
//			mouseMove2(x, y);
//			//mousePress(SWT.BUTTON1);
//			//mouseRelease(SWT.BUTTON1);
//			mousePress2(SWT.BUTTON1);
//			//mouseRelease2(SWT.BUTTON1); <-- this doesn't seem right...
			
	        new SWTMouseOperation(WT.BUTTON1).at(new SWTControlLocation(combo, WTInternal.RIGHT).offset(-8, 0)).execute();
			actionWaitForIdle(combo.getDisplay());
//		}
	}
	
	/** Move the mouse pointer over the item with the given index **/
	public void mouseMoveIndex(Combo combo, int index){
		int style = getStyle(combo);
		if((style&SWT.DROP_DOWN)==SWT.DROP_DOWN){
			// TODO Add code to scroll down and move the mouse pointer;  
			// may not be possible b/c combo.getVerticalBar() returns null even when the 
			// bar on the drop-down is visible
		}	
		else{// SWT.SIMPLE
			// TODO Add code to scroll so item is visible and move
			// pointer over item
		}
	}
	
    /**
     * Select the item from the Combo at the given index.
     * 
     * @param combo Combo from which to select
     * @param index Index of item to select
     */
    public void actionSelectIndex(final Combo combo, final int index){
        actionFocus(combo);
        Display display = combo.getDisplay(); 
        int current = getSelectionIndex(combo);
        dropDownCombo(combo);
        while (current != index) {
            if (current < index) {
            	//!pq: keyClick fix
            	// actionKeyPress(SWT.ARROW_DOWN,display);
            	// actionKeyRelease(SWT.ARROW_DOWN,display);
                keyClick(SWT.ARROW_DOWN);
                waitForIdle(display);
                
                current++;
            } else {
            	//!pq: keyClick fix
            	//actionKeyPress(SWT.ARROW_UP,display);
                //actionKeyRelease(SWT.ARROW_UP,display);
                keyClick(SWT.ARROW_UP);
                waitForIdle(display);
                current--;
            }
        }
        //!pq: keyClick fix
        //actionKeyChar(SWT.CR,display);
        keyClick(SWT.CR);
        actionWaitForIdle(display);
        int selected = getSelectionIndex(combo);
        if (selected != index) {
            String msg = "Was not able to select the correct index for Combo:"+selected+"!="+index;
            throw new AssertionError(msg);
        }
    }
	
	/**
	 * Select the given item from the Combo.
	 * 
	 * @param combo Combo from which to select
	 * @param item String to select
	 */
	public void actionSelectItem(final Combo combo, String item){
        String [] items = getItems(combo);
        boolean found = false;
		for (int i = 0; i < items.length; i++){
			if(item.equals(items[i])){
                found = true;
				actionSelectIndex(combo,i);
				break;						
			}
		}
        if (!found) {
            /* @todo: i think we should REALLY throw a WidgetNotFound exception, here,
             * but that would require an api change, and i think that should be part of
             * a change that tries to standardize on that, i.e. in TreeItemTester, and MenuItemTester.
             * And the signature should, also, throw a MultipleWidgetsFoundException.
             */
			Log.debug("actionSelectItem: item \""+item+"\" not found");
        }
	}
	
	/**
	 * Returns the item at the given index.
	 * 
	 * @param combo Combo from which to obtain the item	
	 * @param index Index of the item 
	 * @return the item at the given index, or null if index is out-of-bounds
	 */
	public String getItem(final Combo combo, final int index){
        String result = (String) Robot.syncExec(combo.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return combo.getItem(index);
            }
        });
        return result;
	}
	
	/**
	 * Indicates if the given index is the index of the currently selected item.
	 * 
	 * @param combo Combo to check
	 * @param index Index of the item to check
	 * @return whether the item at the given index is selected
	 */
	public boolean assertIndexSelected(final Combo combo, int index){
        /* @todo: this might be better served as a Condition */
        int selected = getSelectionIndex(combo);
		return selected == index;
	}
	
	/**
	 * Indicates if the given item is currently selected.
	 * 
	 * @param combo Combo to check
	 * @param item Item to check
	 * @return whether the given item is selected
	 */
	public boolean assertItemSelected(final Combo combo, String item){
        /* @todo: this might be better served as a Condition */
        int selected = getSelectionIndex(combo);
        if (selected < 0) {
            Log.debug("No item was selected while trying to assert "+item+" was selected.");
            return false;
        }
        String selectedItem = getItem(combo,selected);
        return item.equals(selectedItem);    
	}	 
    
    /**
     * Proxy for {@link Combo#setText(String)}.
     */
	public void setText(final Combo combo, final String text) {
	    actionFocus(combo);
	    Robot.syncExec(combo.getDisplay(),null,new Runnable(){
	        public void run(){
	            combo.setText(text);
	        }
	    });
        Robot.wait(new Condition() {
            public boolean test() {
                return text.equals(getText(combo));
            }
            //@Override
            public String toString() {
                return "Combo to have text set to " + text;
            }
        });
    }
	

}
