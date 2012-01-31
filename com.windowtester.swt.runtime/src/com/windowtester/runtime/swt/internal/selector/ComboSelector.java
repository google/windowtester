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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.ComboTester;

import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.ExceptionHandlingHelper;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.operation.SWTControlLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.widgets.ComboReference;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;

/**
 * A Selector for Combos.
 * 
 */
public class ComboSelector extends BasicWidgetSelector {
	
	
	private static final long SELECTION_TIMEOUT  = 5000;
	private static final int WAIT_INTERVAL      = 200;
	
	private static final int ITEM_NOT_FOUND = -1;
	
	private ComboTester _comboTester = new ComboTester();
	private final IUIContext _ui;
	
	
	public ComboSelector() {
		this(null);
	}
	
	//create with a backpointer to the ui for implementing conditional waits
	public ComboSelector(IUIContext ui) {
		_ui = ui;
	}
	
	protected IUIContext getUI() {
		return _ui;
	}
	
	
	
	/**
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget click(Widget w, String itemLabel, int mask) throws WidgetNotFoundException {
		//TODO: notice mask is ignored here...
		Combo combo = (Combo)w;
		
		/* We used to perform selections with keystrokes but this was error prone in cases where the 
		 * selection failed and we tried a force (and the original combo was disposed).
		 * Now instead of trying to select the item with keystrokes we just do the force.
		 */		
		mouseMove(w);
		forceSelection(combo, itemLabel);
		verifySelection(combo, itemLabel);
		return w; //NOTE: this may in fact be NULL
	}
	

	/**
	 * Verify that the proper item is selected.
	 */
	private void verifySelection(final Combo combo, final String expectedSelection) throws WaitTimedOutException{	
		
		//TODO: verify that screen capture is still properly handled
		
		IUIContext ui = getUI();
		if (ui == null)
			return; //no verification
			
		class ItemSelectionCondition implements ICondition, IDiagnosticParticipant {
			int index;
			String actualSelection;
			/* (non-Javadoc)
			 * @see com.windowtester.runtime.condition.ICondition#test()
			 */
			public boolean test() {
				index = _comboTester.getSelectionIndex(combo);
				if (index == -1)
					return false; //there's a bit of a race here: we return false if we're "between" selections
				actualSelection = _comboTester.getItem(combo, index);
				return actualSelection.equals(expectedSelection);
			}
			
			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			public String toString() {
				return "selection of index for Combo: " +  actualSelection+ " != " + expectedSelection;
			}
			
			/* (non-Javadoc)
			 * @see com.windowtester.internal.runtime.IDiagnosticParticipant#diagnose(com.windowtester.internal.runtime.IDiagnostic)
			 */
			public void diagnose(IDiagnostic diagnostic) {
				diagnostic.diagnose("Combo Item Selection", toString());
				doScreenCapture(combo);
			};
		}
		ui.wait(new ItemSelectionCondition(), SELECTION_TIMEOUT, WAIT_INTERVAL);
	}

	/*
	 * FORCE a selection in case selection failed for some reason.
	 */
	public void forceSelection(final Combo combo, final int index) {
		combo.getDisplay().asyncExec(new Runnable(){
			public void run() {
				combo.select(index);
			}
		});
	}

	/*
	 * FORCE a selection in case selection failed for some reason.
	 */
	public void forceSelection(Combo combo, String item) throws WidgetNotFoundException {
		int index = getIndex(item, combo);
		if (index == ITEM_NOT_FOUND)
			throw itemNotFoundException(combo, item);
		forceSelection(combo, index);
	}

	private WidgetNotFoundException itemNotFoundException(Combo combo, String item) {
		return  new WidgetNotFoundException("item: \"" + item + "\" not found in combo [" + getItemListString(combo) + "]");
	}
	
	
//	/**
//	 * Select the given item from the Combo.
//	 * 
//	 * @param combo Combo from which to select
//	 * @param item String to select
//	 * @throws WidgetNotFoundException 
//	 */
//	public void actionSelectItem(final Combo combo, String item) throws WidgetNotFoundException{
//        String [] items = UIProxy.getItems(combo);
//        boolean found = false;
//		for (int i = 0; i < items.length; i++){
//			if(item.equals(items[i])){
//                found = true;
//				actionSelectIndex(combo,i);
//				break;						
//			}
//		}
//        if (!found) {
//			LogHandler.log("actionSelectItem: item \""+item+"\" not found");
//			throw itemNotFoundException(combo, item);
//        }
//	}
	
	
    private String getItemListString(Combo combo) {
    	String[] items = new ComboReference(combo).getItems();
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < items.length; i++) {
			sb.append('"').append(items[i]).append('"');
			if (i < items.length-1)
				sb.append(", ");
		}
    	return sb.toString();
	}

//	/**
//     * Select the item from the Combo at the given index.
//     * 
//     * @param combo Combo from which to select
//     * @param index Index of item to select
//     */
//    public void actionSelectIndex(final Combo combo, final int index){
//        setFocus(combo);
//        Display display = combo.getDisplay(); 
//        int current = UIProxy.getSelectionIndex(combo);
//        
//        dropDownCombo(combo);
//        while (current != index) {
//            if (current < index) {
//                keyClick(SWT.ARROW_DOWN);
//                waitForIdle(display);
//                current++;
//            } else {
//                keyClick(SWT.ARROW_UP);
//                waitForIdle(display);
//                current--;
//            }
//        }
//        setFocus(combo);
//        keyClick(SWT.CR); 
//        dismissCombo(combo);
//
//        waitForIdle(display);
//        
//    }
	

	/**
	 * Dismiss the combo (by clicking outside it).
	 */
	protected void dismissCombo(Combo combo) {
		if (combo.isDisposed()) {
			LogHandler.log("attempt to dimiss combo ignored (combo disposed)");
			return;
		}
			
		int style = getStyle(combo);
		if((style&SWT.DROP_DOWN)==SWT.DROP_DOWN){
//			Rectangle bounds = getGlobalBounds(combo);
//			
//			//move the mouse outside the combo
//			mouseMove(	bounds.x+bounds.width+2,
//					bounds.y+bounds.height+2);
//
//			//click
//			mousePress(SWT.BUTTON1);
//			mouseRelease(SWT.BUTTON1);
			new SWTMouseOperation(WT.BUTTON1).at(new SWTControlLocation(combo, WTInternal.BOTTOMRIGHT).offset(2, 2)).execute();

			//wait for the combo to disappear
			waitForIdle(combo.getDisplay());
		} else {
			LogHandler.log("attempt to dimiss down combo ignored (unhandled style bit set:" + style+ ")");
		}
	}

	private int getStyle(Combo combo) {
		return SWTWidgetReference.forWidget(combo).getStyle();
	}

	/** 
	 * Drop down the menu for the given Combo box 
	 * WARNING: This method is platform-dependent.
	 */
	protected void dropDownCombo(Combo combo){
		int style = getStyle(combo);
//		final int BUTTON_SIZE = 16;
		if((style&SWT.DROP_DOWN)==SWT.DROP_DOWN){
//			Rectangle bounds = getGlobalBounds(combo);
//			
//			//move the mouse to the caret
//			mouseMove(	bounds.x+bounds.width-BUTTON_SIZE/2,
//					bounds.y+bounds.height-BUTTON_SIZE/2);
//
//			//click
//			mousePress(SWT.BUTTON1);
//			mouseRelease(SWT.BUTTON1);
			
			new SWTMouseOperation(WT.BUTTON1).at(new SWTControlLocation(combo, WTInternal.RIGHT).offset(-8, 0)).execute();

			//wait for the combo to appear
			waitForIdle(combo.getDisplay());
			
		} else {
			LogHandler.log("attempt to drop down combo ignored (unhandled style bit set:" + style+ ")");
		}
	}

	
	/**
	 * Get the index of the given item in the combo's item list.
	 * @return the item's index or {@link #ITEM_NOT_FOUND} if is not found
	 */
	private int getIndex(final String item, final Combo combo) {
		final int[] index = new int[]{ITEM_NOT_FOUND};
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				String[] items = combo.getItems();
				for (int i = 0; i < items.length; i++){
					if (item.equals(items[i])){
						index[0] = i;
						return;						
					}
				}
			}
		});
		return index[0];
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Exception handling
	//
	///////////////////////////////////////////////////////////////////////////////////////////////

	private void doScreenCapture(Combo combo) {
	
		if (combo.isDisposed()) {
			LogHandler.log("attempt to open combo on failed selection skipped (combo disposed)");
			return;
		}
		dropDownCombo(combo);
		ExceptionHandlingHelper.doScreenCapture("(combo selection failure)");
		dismissCombo(combo);
	}
	
	
	
	
	

	
}
