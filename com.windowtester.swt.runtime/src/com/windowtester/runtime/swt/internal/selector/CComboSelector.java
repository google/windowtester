/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.internal.selector;

import junit.framework.Assert;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.TestHierarchy;
import abbot.tester.swt.Robot;
import abbot.tester.swt.RunnableWithResult;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.operation.SWTCComboOperation;
import com.windowtester.runtime.swt.internal.widgets.CComboReference;

/**
 * A Selector for CCombos.
 * 
 * @deprecated use {@link CComboReference} instead.
 */
public class CComboSelector extends BasicWidgetSelector {

	protected Object objT;
	
	    
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget click(Widget w, String itemLabel) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		actionSelectItem((CCombo)w, itemLabel);
		return w;
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
    	cCombo.getDisplay().syncExec(new Runnable() {
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
     * Select the item from the Combo at the given index.
     * 
     * @param cCombo
     *            Combo from which to select
     * @param index
     *            Index of item to select
     */
    public void actionSelectIndex(final CCombo cCombo, final int index) {
//        new SWTCComboOperation(cCombo).selectIndex(index).execute();
        // TODO [Dan] should not be creating a new reference object, but ...
        new SWTCComboOperation(new CComboReference(cCombo)).select(index).execute();
//    	actionFocus(cCombo);
//        cCombo.getDisplay().syncExec(new Runnable() {
//            public void run() {
//                if (cCombo.getItemCount() > index)
//                    selectItemThroughKey(cCombo, index);
//                else
//                    Assert.fail("actionSelectIndex: index \"" + index
//                            + "\" not found");
//            }
//        });
        //FIXME[author=pq]: this dealy is hanging; find if it is required...
        //System.out.println("WidgetTester delay");
        //WidgetTester.delay(getAutoDelay());
        //System.out.println("ending WidgetTester delay");
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
//    private void selectItemThroughKey(final CCombo cCombo, final int index) {
//        int style = getStyle(cCombo);
//
//        int currentIndex = cCombo.getSelectionIndex();
//        int keyins = 0;
//        boolean upDown = false;
//        // System.out.println("CurrentIndex: " +currentIndex);
//        // System.out.println("index: "+ index);
//        if (currentIndex < index)
//            keyins = index - currentIndex;
//        else if (currentIndex > index) {
//            keyins = currentIndex - index;
//            upDown = true;
//        } else {
//            // NOOP. Trying to select the item that is already selected. Not
//            // sure if this what a user would want though.
//        }
//
//        final int loop = keyins;
//        final boolean up = upDown;
//        // Question: Why does a CCombo that is created with SWT.DROP_DOWN return
//        // 0 when you do (style & SWT_DROPDOWN). It should return 4, which is
//        // SWT.DROP_DOWN
//        // if ((cCombo.getStyle () & SWT.DROP_DOWN) != 0) {
//        // System.out.println ("DropDown");
//        // }
//        // else
//        // System.out.println ("No Drop Down");
//        // THIS SHOULD WORK, but doesn't.
//        // if((style&SWT.DROP_DOWN)==SWT.DROP_DOWN){
//        if ((style & SWT.DROP_DOWN) == 0) {
//            actionFocus(cCombo);
//            dropDownCombo(cCombo);
////            cCombo.getDisplay().syncExec( new Runnable() {
////                public void run() {
//                    //abbot.swt.Robot aRobot = new abbot.swt.Robot();
//                    for (int i = 0; i < loop; i++) {
//                        if (!up) {
//                            //!pq: keyClick fix
//                        	//aRobot.keyPress(SWT.ARROW_DOWN);
//                            //aRobot.keyRelease(SWT.ARROW_DOWN);
//                            keyClick(SWT.ARROW_DOWN);
//                            //keyClick(SWT.ARROW_DOWN);
//                            //keyClick(SWT.ARROW_DOWN);
//                        } else {
//                        	//!pq: keyClick fix
//                            //aRobot.keyPress(SWT.ARROW_UP);
//                            //aRobot.keyRelease(SWT.ARROW_UP);
//                        	keyClick(SWT.ARROW_UP);
//                        }
//                    }
//                    //!pq: keyClick fix
//                    //aRobot.keyPress(SWT.CR);
//                    //aRobot.keyRelease(SWT.CR);
//                    keyClick(SWT.CR);
////                }
////            });
//            trace("wating for idle");
//            waitForIdle(cCombo.getDisplay());
//            trace("done wating for idle");
//            
//        } else {// SWT.SIMPLE
//            // TODO
//        }
//        pauseCurrentThread(1000);
//    }

    
    
	/** Set the focus on to the given component. */
	/* TODO MAY NEED TO CHECK THAT THE CONTROL DOES INDEED HAVE FOCUS */
	public void actionFocus(Widget widget) {
		TestHierarchy hierarchy = new TestHierarchy(Display.getDefault());
		while(!(widget instanceof Control))
			widget = hierarchy.getParent(widget);
		focus((Control)widget);
		waitForIdle(widget.getDisplay());
	}

	
//    /**
//     * Drop down the list for the given Combo box WARNING: This has only been
//     * tested on Windows XP.
//     */
//    private void dropDownCombo(CCombo cCombo) {
////        //int style = getStyle(cCombo);
////    	// [author=Dan] On Linux, click in the text area
////    	// and use the arrow down and up keys to change selection
////        if (Platform.isLinux()) {
////			mouseMove(cCombo, 10, 10);
////		} 
////        else {
////        	final int BUTTON_SIZE = 16;
////        	Rectangle bounds = getGlobalBounds(cCombo);
////			mouseMove(bounds.x + bounds.width - BUTTON_SIZE / 2, bounds.y
////                + bounds.height - BUTTON_SIZE / 2);
////		}   
////        //System.out.println("mouse down");
////        mousePress(SWT.BUTTON1);
////        /*
////         * Causing issues in 3.5-carbon
////         */
////        if (!Platform.isOSX())
////        	waitForIdle(cCombo.getDisplay());
////        pauseDisplayThread(cCombo.getDisplay(), 500);
////        //System.out.println("mouse up");
////        mouseRelease(SWT.BUTTON1); 
//        new SWTMouseOperation(WT.BUTTON1).at(new SWTControlLocation(cCombo, WTInternal.RIGHT).offset(-8, 0)).execute();
//        
//        waitForIdle(cCombo.getDisplay());
//    }
	
    
    /**
	 * Proxy for {@link Widget#getStyle()}.
	 * <p/>
	 * @param w the widget to obtain the style for.
	 * @return the style.
	 */
	public int getStyle(final Widget w){
		Integer result = (Integer) Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(w.getStyle());
			}
		});
		return result.intValue();
	}
    
}
