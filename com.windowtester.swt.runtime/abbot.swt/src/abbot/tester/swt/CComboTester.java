/*
 * Created on 12.04.2005
 * by Richard Birenheide (D035816)
 *
 * Copyright SAP AG 2005
 */
package abbot.tester.swt;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.operation.SWTControlLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;

/**
 * Tester for CCombo widgets.
 * <p>
 * 
 * NOTE: various keyClick and mousePress updates made 
 */
public class CComboTester extends CompositeTester {

	
	//!pq:
	public int getSelectionIndex(final CCombo cCombo) {
        Integer result = (Integer) Robot.syncExec(cCombo.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(cCombo.getSelectionIndex());
                    }
                });
        return result.intValue();
    }
	
	//!pq:
	public Object getSelection(CCombo cCombo) {
		int index = getSelectionIndex(cCombo);
		return getItem(cCombo, index);
	}
	
	
    /**
     * Proxy for {@link CCombo#add(String)}. <p/>
     */
    public void add(final CCombo cCombo, final String s) {
        Robot.syncExec(cCombo.getDisplay(), null, new Runnable() {
            public void run() {
                cCombo.add(s);
            }
        });
    }

    /**
     * Proxy for {@link CCombo#add(String, int)}. <p/>
     */
    public void add(final CCombo cCombo, final String s, final int i) {
        Robot.syncExec(cCombo.getDisplay(), null, new Runnable() {
            public void run() {
                cCombo.add(s,i);
            }
        });
    }

    /**
     * Proxy for {@link CCombo#addModifyListener(ModifyListener)}. <p/>
     */
    public void addModifyListener(final CCombo cCombo, final ModifyListener listener) {
        Robot.syncExec(cCombo.getDisplay(), null, new Runnable() {
            public void run() {
                cCombo.addModifyListener(listener);
            }
        });
    }

    /**
     * Proxy for {@link CCombo#addSelectionListener(SelectionListener)}. <p/>
     */
    public void addSelectionListener(final CCombo cCombo, final SelectionListener listener) {
        Robot.syncExec(cCombo.getDisplay(), null, new Runnable() {
            public void run() {
                cCombo.addSelectionListener(listener);
            }
        });
    }

    /**
     * Proxy for {@link CCombo#clearSelection()}. <p/>
     * 
     * @param cCombo
     *            the CCombo to clear the selection for.
     */
    public void clearSelection(final CCombo cCombo) {
        Robot.syncExec(cCombo.getDisplay(), null, new Runnable() {
            public void run() {
                cCombo.clearSelection();
            }
        });
    }

    /**
     * Proxy for {@link CCombo#getEditable()}. <p/>
     * 
     * @param cCombo
     *            the CCombo to check the editable state for.
     * @return true if the CCombo is editable.
     */
    public boolean getEditable(final CCombo cCombo) {
        Boolean result = (Boolean) Robot.syncExec(cCombo.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(cCombo.getEditable());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link CCombo#getText()}. <p/>
     * @param cCombo
     * @return
     */
	public String getText(final CCombo cCombo) {
        String result = (String) Robot.syncExec(cCombo.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return cCombo.getText();
                    }
                });
        return result.toString();
	}
    
    /**
     * Proxy for {@link CCombo#getItem(int)}. <p/>
     * 
     * @param cCombo
     *            the CCombo for which the item to get.
     * @param index
     *            the index of the item to get.
     * @return the item value.
     */
    public String getItem(final CCombo cCombo, final int index) {
        String result = (String) Robot.syncExec(cCombo.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return cCombo.getItem(index);
                    }
                });
        return result;
    }
    
    /**
     * Proxy for {@link CCombo#getItemCount()}.
     */
    public int getItemCount(final CCombo cCombo) {
        Integer result = (Integer) Robot.syncExec(cCombo.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(cCombo.getItemCount());
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link CCombo#getItems()}.
     */
    public String [] getItems(final CCombo c) {
        List result = (List) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        String [] items = c.getItems();
                        List list = new ArrayList(items.length);
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
     * This will select a item from a combobox by using ARROW_DWON and ARROW_UP.
     * This is to actually select the item. Just calling CComboBox.Select() will
     * only set the text in the CComboBox and therefor not fire the
     * SelectionChanged event. When testing a actual select should happen. This
     * should eventually be replaced with a version that uses the mouse to click
     * an item because that is really what a user does in most cases. WARNING:
     * This is only tested for a SWT.DROPDOWN style CComboBox. See Question
     * below also. <p/>
     * 
     * @param cCombo
     *            the CCombo for which the item to get.
     * @param index
     *            the index of the item to get.
     */
    private void selectItemThroughKey(final CCombo cCombo, final int index) {
        int style = getStyle(cCombo);

        int currentIndex = cCombo.getSelectionIndex();
        int keyins = 0;
        boolean upDown = false;
        // System.out.println("CurrentIndex: " +currentIndex);
        // System.out.println("index: "+ index);
        if (currentIndex < index)
            keyins = index - currentIndex;
        else if (currentIndex > index) {
            keyins = currentIndex - index;
            upDown = true;
        } else {
            // NOOP. Trying to select the item that is already selected. Not
            // sure if this what a user would want though.
        }

        final int loop = keyins;
        final boolean up = upDown;
        // Question: Why does a CCombo that is created with SWT.DROP_DOWN return
        // 0 when you do (style & SWT_DROPDOWN). It should return 4, which is
        // SWT.DROP_DOWN
        // if ((cCombo.getStyle () & SWT.DROP_DOWN) != 0) {
        // System.out.println ("DropDown");
        // }
        // else
        // System.out.println ("No Drop Down");
        // THIS SHOULD WORK, but doesn't.
        // if((style&SWT.DROP_DOWN)==SWT.DROP_DOWN){
        if ((style & SWT.DROP_DOWN) == 0) {
            actionFocus(cCombo);
            dropDownCombo(cCombo);
            Robot.syncExec(cCombo.getDisplay(), this, new Runnable() {
                public void run() {
                    //abbot.swt.Robot aRobot = new abbot.swt.Robot();
                    for (int i = 0; i < loop; i++) {
                        if (!up) {
                            //!pq: keyClick fix
                        	//aRobot.keyPress(SWT.ARROW_DOWN);
                            //aRobot.keyRelease(SWT.ARROW_DOWN);
                            keyClick(SWT.ARROW_DOWN);
                            //keyClick(SWT.ARROW_DOWN);
                            //keyClick(SWT.ARROW_DOWN);
                        } else {
                        	//!pq: keyClick fix
                            //aRobot.keyPress(SWT.ARROW_UP);
                            //aRobot.keyRelease(SWT.ARROW_UP);
                        	keyClick(SWT.ARROW_UP);
                        }
                    }
                    //!pq: keyClick fix
                    //aRobot.keyPress(SWT.CR);
                    //aRobot.keyRelease(SWT.CR);
                    keyClick(SWT.CR);
                }
            });
            System.out.println("wating for idle");
            waitForIdle(cCombo.getDisplay());
            System.out.println("done wating for idle");
            
        } else {// SWT.SIMPLE
            // TODO
        }
    }

    /**
     * Drop down the list for the given Combo box WARNING: This has only been
     * tested on Windows XP.
     */
    private void dropDownCombo(CCombo cCombo) {
//        final int BUTTON_SIZE = 16;
//        Rectangle bounds = getGlobalBounds(cCombo);
//		int x = bounds.x + bounds.width - BUTTON_SIZE / 2;
//		int y = bounds.y + bounds.height - BUTTON_SIZE / 2;
        new SWTMouseOperation(WT.BUTTON1).at(new SWTControlLocation(cCombo, WTInternal.RIGHT).offset(-8, 0)).execute();
        actionWaitForIdle(cCombo.getDisplay());
    }

    /**
     * Select the item from the Combo at the given index.
     * 
     * @param cCombo
     *            Combo from which to select
     * @param index
     *            Index of item to select
     */
    public void actionSelectIndex(final CCombo cCombo, final int index) {
        actionFocus(cCombo);
        Robot.syncExec(cCombo.getDisplay(), null, new Runnable() {
            public void run() {
                if (cCombo.getItemCount() > index)
                    selectItemThroughKey(cCombo, index);
                else
                    Assert.fail("actionSelectIndex: index \"" + index
                            + "\" not found");
            }
        });
        //FIXME[author=pq]: this dealy is hanging; find if it is required...
        //System.out.println("WidgetTester delay");
        //WidgetTester.delay(getAutoDelay());
        //System.out.println("ending WidgetTester delay");
    }

    /**
     * Select the given item from the Combo.
     * 
     * @param cCombo
     *            Combo from which to select
     * @param item
     *            String to select
     */
    public synchronized void actionSelectItem(final CCombo cCombo, String item) {
        Robot.syncExec(cCombo.getDisplay(), this, new Runnable() {
            public void run() {
                objT = cCombo.getItems();
            }
        });
        String[] items = (String[]) objT;
        int i = 0;
        while (true) {
            if (i == items.length) {
                Assert
                        .fail("actionSelectItem: item \"" + item
                                + "\" not found");
                break;
            }
            if (item.equals(items[i])) {
                actionSelectIndex(cCombo, i);
                break;
            }
            i++;
        }
    }

    /**
     * Indicates if the given index is the index of the currently selected item.
     * 
     * @param cCombo
     *            Combo to check
     * @param index
     *            Index of the item to check
     * @return whether the item at the given index is selected
     */
    public synchronized boolean assertIndexSelected(final CCombo cCombo,
            int index) {
        Robot.syncExec(cCombo.getDisplay(), this, new Runnable() {
            public void run() {
                intT = cCombo.getSelectionIndex();
            }
        });
        return intT == index;
    }

    /**
     * Indicates if the given item is currently selected.
     * 
     * @param cCombo
     *            Combo to check
     * @param item
     *            Item to check
     * @return whether the given item is selected
     */
    public synchronized boolean assertItemSelected(final CCombo cCombo,
            String item) {
        objT = null;
        Robot.syncExec(cCombo.getDisplay(), this, new Runnable() {
            public void run() {
                int index = cCombo.getSelectionIndex();
                if (index != -1)
                    objT = cCombo.getItem(index);
            }
        });
        return objT != null && ((String) objT).equals(item);
    }






}
