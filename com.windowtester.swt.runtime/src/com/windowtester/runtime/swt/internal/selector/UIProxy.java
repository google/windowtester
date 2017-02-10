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

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.AssertionFailedError;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.RunnableWithResult;
import abbot.tester.swt.WidgetLocator;

import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.internal.debug.LogHandler;



/**
 * A service class that proxies calls to access Widgets by wrapping them in 
 * the UI thread.
 */
public class UIProxy {

	
	////////////////////////////////////////////////////////////////////////////
	//
	// Widget proxies
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get the bounds for the given widget.
	 * @param w - the widget in question
	 * @return the widget's bounds
	 */
	public static Rectangle getBounds(final Widget w) {
		return (Rectangle)syncExec(w.getDisplay(), new RunnableWithResult(){
			public Object runWithResult() {
				return WidgetLocator.getBounds(w);
			}
		});
	}
	/**
	 * Get location of the given widget.
	 * @param w - the widget in question
	 * @return the widget's location in global coordinates
	 */
	public static Point getLocation(final Widget w) {
		return (Point)syncExec(w.getDisplay(), new RunnableWithResult(){
			public Object runWithResult() {
				return WidgetLocator.getLocation(w);
			}
		});
	}
	////////////////////////////////////////////////////////////////////////////
	//
	// Styled Text proxies
	//
	////////////////////////////////////////////////////////////////////////////
	
    /**
     * Proxy for {@link StyledText#getText()}.
     */
    public static String getText(final StyledText s) {
        String result = (String) syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return s.getText();
            }
        });
        return result;
    }
	
    /**
     * Proxy for {@link Text#getText()}.
     */
    public static String getText(final Text t) {
        String result = (String) syncExec(t.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return t.getText();
            }
        });
        return result;
    }
    
    
    /**
     * Proxy for {@link Button#getText()}.
     */
    public static String getText(final Button b) {
        String result = (String) syncExec(b.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return b.getText();
            }
        });
        return result;
    }
    
    /**
     * Proxy for {@link StyledText#getSelection()}.
     */
    public static Point getSelection(final StyledText s) {
        Point result = (Point) syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return s.getSelection();
            }
        });
        return result;
    }
    
    /**
     * Proxy for {@link StyledText#getSelectionCount()}.
     */
    public static int getSelectionCount(final StyledText s) {
        Integer result = (Integer) syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getSelectionCount());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getSelectionText()}.
     */
    public static String getSelectionText(final StyledText s) {
        String result = (String) syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return s.getSelectionText();
            }
        });
        return result;
    }
    ////////////////////////////////////////////////////////////////////////////
	//
	// Table Item proxies
	//
	////////////////////////////////////////////////////////////////////////////
	
    /**
     * Proxy for {@link TableItem#getBounds(int)}. <p/>
     * 
     * @param item
     *            the table item under test.
     * @return the bounds of the item.
     */
    public static Rectangle getBounds(final TableItem item, final int columnIndex) {
        Rectangle result = (Rectangle) syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getBounds(columnIndex);
                    }
                });
        return result;
    }
    
	/**
	 * Proxy for {@link TableItem#getParent()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the item's parent.
	 */
	public static Table getParent(final TableItem item){
		Table result = (Table) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getParent();				
			}
		});
		return result;	
	}
	
	
	/**
	 * Proxy for {@link TableColumn#getParent()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the item's parent.
	 */
	public static Table getParent(final TableColumn item){
		Table result = (Table) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getParent();				
			}
		});
		return result;	
	}
    
	
	/**
	 * Proxy for {@link Control#getParent()}.
	 */
	public static Composite getParent(final Control c) {
		Composite result = (Composite) syncExec(c.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return c.getParent();				
			}
		});
		return result;	
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Tree Item proxies
	//
	////////////////////////////////////////////////////////////////////////////
	
    /**
     * Proxy for {@link TreeItem#getBounds()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the bounds of the item.
     */
    public static Rectangle getBounds(final TreeItem item) {
        Rectangle result = (Rectangle) syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getBounds();
                    }
                });
        return result;
    }

	public static Rectangle getBounds(final TreeItem item, final int column) {
        Rectangle result = (Rectangle) syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getBounds(column);
                    }
                });
        return result;
	}

    
    
    /**
     * Proxy for {@link TreeItem#getExpanded()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return
     */
    public static boolean getExpanded(final TreeItem item) {
        Boolean result = (Boolean) syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(item.getExpanded());
                    }
                });
        return result.booleanValue();
    }
    
    /**
     * Proxy for {@link Control#isEnabled()}.
     * @deprecated Use {@link IsEnabled} and {@link IsEnabledCondition} instead.
     * 		This method will be removed after Sept 2008
     */
    public static boolean isEnabled(final Control c) {
        Boolean result = (Boolean) syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(c.isEnabled());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link MenuItem#isEnabled()}.
     * @deprecated Use {@link IsEnabled} and {@link IsEnabledCondition} instead.
     * 		This method will be removed after Sept 2008
     */
    public static boolean isEnabled(final MenuItem item) {
        Boolean result = (Boolean) syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(item.isEnabled());
                    }
                });
        return result.booleanValue();
    }
    
    
	////////////////////////////////////////////////////////////////////////////////
	//
	// List Proxies
	//
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Proxy for {@link Scrollable#getClientArea()}.
	 * <p/>
	 * @param s the scrollable under test.
	 * @return the client area
	 */
	public static Rectangle getClientArea(final Scrollable s) {
		Rectangle result = (Rectangle) syncExec(s.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return s.getClientArea();
			}
		});
		return result;
	}
	
    /**
     * Proxy for {@link Control#getBorderWidth()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the border width.
     */
    public static int getBorderWidth(final Control c) {
        Integer result = (Integer) syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(c.getBorderWidth());
                    }
                });
        return result.intValue();
    }
	
    /**
     * Proxy for
     * {@link List#addSelectionListener(SelectionListener)}.
     */
    public void addSelectionListener(final List l, final SelectionListener listener) {
        syncExec(l.getDisplay(), new Runnable() {
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
        syncExec(l.getDisplay(), new Runnable() {
            public void run() {
                l.removeSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for {@link List#getItem(int i)}.
     */
    public String getItem(final List l, final int i) {
        String result = (String) syncExec(l.getDisplay(),
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
    public static int getItemCount(final List l) {
        Integer result = (Integer) syncExec(l.getDisplay(),
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
    public static int getItemHeight(final List l) {
        Integer result = (Integer) syncExec(l.getDisplay(),
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
    public static String [] getItems(final List l) {
        java.util.List result = (java.util.List) syncExec(l.getDisplay(),
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
    public static String [] getSelection(final List l) {
        java.util.List result = (java.util.List) syncExec(l.getDisplay(),
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
    public static int getSelectionCount(final List l) {
        Integer result = (Integer) syncExec(l.getDisplay(),
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
    public static int getSelectionIndex(final List l) {
        Integer result = (Integer) syncExec(l.getDisplay(),
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
    public static int [] getSelectionIndices(final List l) {
        java.util.List result = (java.util.List) syncExec(l.getDisplay(),
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
    public static int getTopIndex(final List l) {
        Integer result = (Integer) syncExec(l.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(l.getTopIndex());
                    }
                });
        return result.intValue();
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////
	//
	// Combo Proxies
	//
	////////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Proxy for {@link Combo#getItems()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return the items in the combo's list.
	 */
	public static String[] getItems(final Combo combo) {
		String[] result = (String[]) syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return combo.getItems();
			}
		});
		return result;
	}
	
    /**
	 * Proxy for {@link Widget#getStyle()}.
	 * <p/>
	 * @param w the widget to obtain the style for.
	 * @return the style.
	 */
	public static int getStyle(final Widget w){
		Integer result = (Integer) syncExec(w.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(w.getStyle());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Combo#getSelection()}.
	 * <p/>
	 * @param combo the combo under test.
	 * @return a point representing the selection start and end.
	 */
	public static Point getSelection(final Combo combo) {
		Point result = (Point) syncExec(combo.getDisplay(), new RunnableWithResult() {
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
	public static int getSelectionIndex(final Combo combo) {
		Integer result = (Integer) syncExec(combo.getDisplay(), new RunnableWithResult() {
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
	public static String getText(final Combo combo) {
		String result = (String) syncExec(combo.getDisplay(), new RunnableWithResult() {
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
	public static int getTextHeight(final Combo combo) {
		Integer result = (Integer) syncExec(combo.getDisplay(), new RunnableWithResult() {
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
	public static int getTextLimit(final Combo combo) {
		Integer result = (Integer) syncExec(combo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(combo.getTextLimit());
			}
		});
		return result.intValue();
	}
	
    ////////////////////////////////////////////////////////////////////////////////
	//
	// CCombo Proxies
	//
	////////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Proxy for {@link CCombo#getItems()}.
	 * <p/>
	 * @param ccombo the ccombo under test.
	 * @return the items in the ccombo's list.
	 */
	public static String[] getItems(final CCombo ccombo) {
		String[] result = (String[]) syncExec(ccombo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return ccombo.getItems();
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link CCombo#getSelection()}.
	 * <p/>
	 * @param ccombo the ccombo under test.
	 * @return a point representing the selection start and end.
	 */
	public static Point getSelection(final CCombo ccombo) {
		Point result = (Point) syncExec(ccombo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return ccombo.getSelection();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link CCombo#getSelectionIndex()}.
	 * <p/>
	 * @param ccombo the ccombo under test.
	 * @return the selected index.
	 */
	public static int getSelectionIndex(final CCombo ccombo) {
		Integer result = (Integer) syncExec(ccombo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(ccombo.getSelectionIndex());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link CCombo#getText()}.
	 * <p/>
	 * @param ccombo the ccombo under test.
	 * @return the contents of the text field.
	 */
	public static String getText(final CCombo ccombo) {
		String result = (String) syncExec(ccombo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return ccombo.getText();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link CCombo#getTextHeight()}.
	 * <p/>
	 * @param ccombo the ccombo under test.
	 * @return the text height.
	 */
	public static int getTextHeight(final CCombo ccombo) {
		Integer result = (Integer) syncExec(ccombo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(ccombo.getTextHeight());
			}
		});
		return result.intValue();
	}
    
	/**
	 * Proxy for {@link CCombo#getTextLimit()}.
	 * <p/>
	 * @param ccombo the ccombo under test.
	 * @return the text limit.
	 */
	public static int getTextLimit(final CCombo ccombo) {
		Integer result = (Integer) syncExec(ccombo.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(ccombo.getTextLimit());
			}
		});
		return result.intValue();
	}
	

	////////////////////////////////////////////////////////////////////////////////
	//
	// Menu Proxies
	//
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Proxy for {@link MenuItem#getParent()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the item's parent.
	 */
	public static Menu getParent(final MenuItem item){
		Menu result = (Menu) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getParent();				
			}
		});
		return result;	
	}
	
	/**
	 * Proxy for {@link Menu#getShell()}.
	 * <p/>
	 * @param menu the menu under test.
	 * @return the shell of the menu.
	 */
	public static Shell getShell(final Menu menu){
		Shell result = (Shell) syncExec(menu.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return menu.getShell();				
			}
		});
		return result;	
	}

	/**
	 * Proxy for {@link ToolItem#getControl()}.
	 * <p/>
	 * @param item the toolitem under test.
	 * @return the control of the toolitem.
	 */
	public static Control getControl(final ToolItem item) {
		Control result = (Control) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getControl();				
			}
		});
		return result;	
	}
	/**
	 * Proxy for {@link ToolItem#getParent()}.
	 * <p/>
	 * @param item the toolitem under test.
	 * @return the parent of the toolitem.
	 */
	public static ToolBar getParent(final ToolItem item) {
		ToolBar result = (ToolBar) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getParent();				
			}
		});
		return result;	
	}
	
	/**
	 * Proxy for {@link ToolItem#getParent()}.
	 * <p/>
	 * @param item the toolitem under test.
	 * @return the parent of the toolitem.
	 */
	public static CTabFolder getParent(final CTabItem item) {
		CTabFolder result = (CTabFolder) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getParent();				
			}
		});
		return result;	
	}
	
	public static TabFolder getParent (final TabItem c){
		TabFolder result = (TabFolder) syncExec(c.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return c.getParent();				
			}
		});
		return result;	
	}

	/**
	 * Proxy for {@link Control#getShell()}.
	 * <p/>
	 * @param ctrl the control under test.
	 * @return the shell of the control.
	 */
	public static Shell getShell(final Control ctrl) {
		Shell result = (Shell) syncExec(ctrl.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return ctrl.getShell();				
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
	public static int getWidth(final ToolItem item) {
		Integer result = (Integer) syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(item.getWidth());
			}
		});
		return result.intValue();	
	}

	/**
	 * Proxy for {@link Shell#close()}.
	 * <p/>
	 * @param s the shell to close.
	 */
	public static void closeShell(final Shell s) {
		if (s == null || s.isDisposed()) {
			LogHandler.log("attempt to close shell: " + s + " failed");
			return; 
		}
		// sync or async?
		// a work in progress, author: Jaime
		syncExec(s.getDisplay(), new Runnable() { // current
//		asyncExec(s.getDisplay(), new Runnable() { // proposed
			public void run() {
				try {
					s.close();
				} catch (SWTException e) {
					LogHandler.log("attempt to close shell: " + s + " failed");
				}
			}
		});
	}

	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Exec helpers
	//
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Executes the action given synchronously in the SWT thread and returns the
	 * eventual result of the computation.
	 * <p/>
	 * This is a helper method for testers which query Widget properties.<br/>
	 * @param display the display whichs Thread to use.
	 * @param action the action to be run.
	 * @return the result of the synchronously executed action.
	 */
	public static void syncExec(Display display, Runnable runnable) {
		try{
			display.syncExec(runnable);
		}catch(NullPointerException npe){
			//Logger.trace("Caught a NullPointerException");
			LogHandler.log(npe);
			/* Do nothing.  When DWF.getRootShells() is called, 
			 * sometimes an NPE is thrown when Display.syncExec() is
			 * called, despite the fact that the display wasn't null
			 * before the call, nor after.
			 * 
			 * This happens when we're checking displays that don't
			 * matter anyway- eg, when we're waiting for a shell to
			 * show that isn't opened yet.  So its fine to just skip
			 * it. 
			 */	
		}
		/*
		 * The following hoping to get cleaner test failure handling. 
		 */
		catch (SWTException swtEx) {
			if (swtEx.throwable instanceof AssertionFailedError) {
				throw (AssertionFailedError) swtEx.throwable;
			}
			else if (swtEx.getCause() instanceof AssertionFailedError) {
				throw (AssertionFailedError) swtEx.getCause();
			}
			else {
				throw swtEx;
			}
		}
	}

	/**
	 * Executes the action given synchronously in the SWT thread and returns the
	 * eventual result of the computation.
	 * <p/>
	 * This is a helper method for testers which query Widget properties.<br/>
	 * @param display the display whichs Thread to use.
	 * @param action the action to be run.
	 * @return the result of the synchronously executed action.
	 */
	public static Object syncExec(Display display, RunnableWithResult action) {
		display.syncExec(action);
		return action.getResult();
	}
	
	/**
	 * Proxy for {@link TreeItem#getParent()}.
	 * <p/>
	 * @param item the item under test.
	 * @return the item's parent.
	 */
	public static Tree getParent(final TreeItem item){
		Tree result = (Tree) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getParent();				
			}
		});
		return result;	
	}


	/**
	 * Proxy for {@link Widget#getData(String))}.
	 */
	public static String getData(final Widget item, final String key){
		String result = (String) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getData(key);				
			}
		});
		return result;	
	}
	
	
	/**
	 * Proxy for {@link Widget#getData())}.
	 */
	public static Object getData(final Widget item){
		Object result = (Object) syncExec(item.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return item.getData();				
			}
		});
		return result;	
	}
	
    /**
     * Proxy for {@link TreeItem#getParentItem()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the parent item.
     */
    public static TreeItem getParentItem(final TreeItem item) {
        TreeItem result = (TreeItem) syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getParentItem();
                    }
                });
        return result;
    }

    /**
     * Proxy for
     * {@link Tree#showColumn(TreeItem)}.
     */
    public static void showItem(final Tree t, final TreeItem item) {
    	t.getDisplay().syncExec(new Runnable() {
            public void run() {
                t.showItem(item);
            }
        });
    }

	public static String getToString(final Widget w) {
		if (w == null)
			return "<null>";
		String result = "";
		if (w.isDisposed())
			return w.getClass().getName() + "<" + w.hashCode() + "> [disposed]";
		try {
			result = (String) syncExec(w.getDisplay(),
					new RunnableWithResult() {
						public Object runWithResult() {
							if (w instanceof ToolItem){
								return getToolItemId((ToolItem)w);
							}
							if (w instanceof Text){
								return getWidgetName(w) + " {" + ((Text) w).getText() + "}";
							}
							return w.toString();
						}
					});
		} catch (SWTException e) {
			//sometime the widget gets disposed (and we ignore it)
		}
		
		return result;	
	}
	
	private static String getWidgetName (Widget w) {
		String string = w.getClass().getName();
		int index = string.lastIndexOf ('.');
		if (index == -1) return string;
		return string.substring (index + 1, string.length ());
	}

    /**
     * Proxy for {@link Control#setLocation(int x, int y)}.
     */
    public static void setLocation(final Control c, final int x, final int y) {
        syncExec(c.getDisplay(), new Runnable() {
            public void run() {
                c.setLocation(x,y);
            }
        });
    }

    /**
     * Proxy for {@link Control#setBounds(int x, int y, int width, int height)}.
     */
    public static void resize(Control c, int width, int height) {
		Rectangle b = getBounds(c);
		setBounds(c, b.x, b.y, width, height);
    }
	
    /**
     * Proxy for {@link Control#setBounds(int x, int y, int width, int height)}.
     */
    public static void setBounds(final Control c, final int x, final int y,
            final int width, final int height) {
        syncExec(c.getDisplay(), new Runnable() {
            public void run() {
                c.setBounds(x, y, width, height);
            }
        });
    }

    /**
     * Get the control associated with this widget.
     * @param target the widget in question
     * @return the associated control
     */
	public static Control getControl(final Widget target) {
		if (target instanceof Control)
			return (Control) target;
		
		Control result = (Control) syncExec(target.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				if (target instanceof Caret)
					return ((Caret)target).getParent();
				if (target instanceof DragSource)
					return ((DragSource)target).getControl();
				if (target instanceof DropTarget)
					return ((DropTarget)target).getControl();
				if (target instanceof CoolItem)
					return ((CoolItem)target).getControl();
//				if (target instanceof CTabItem)
//					return ((CTabItem)target).getControl();
				if (target instanceof CTabItem) {
					Control c = ((CTabItem)target).getControl();
					if(c == null)
						return getControl(((CTabItem)target).getParent());
					else
						return c;
				}
				if (target instanceof MenuItem)
					return getControl(((MenuItem)target).getParent());
				if (target instanceof TabItem) {
					Control c = ((TabItem)target).getControl();
					if(c == null)
						return getControl(((TabItem)target).getParent());
					else
						return c;
				}
				if (target instanceof TableColumn)
					return ((TableColumn)target).getParent();
				if (target instanceof TableItem)
					return ((TableItem)target).getParent();
				if (target instanceof ToolItem) {
					Control c =  ((ToolItem)target).getControl();
					if(c == null)
						return getControl(((ToolItem)target).getParent());
					else
						return c;
				}
//				if (target instanceof TrayItem)
//					return ((TrayItem)target) ... ?
				/* $codepro.preprocessor.if version >= 3.1 $ */
				if (target instanceof TreeColumn)
					return ((TreeColumn)target).getParent();
				/* $codepro.preprocessor.endif $ */
				if (target instanceof TreeItem)
					return ((TreeItem)target).getParent();
				if (target instanceof Menu)
					return ((Menu)target).getParent();
				if (target instanceof ScrollBar)
					return ((ScrollBar)target).getParent();
//				if (target instanceof Tracker)
//					return ((Tracker)target) ... ?
//				if (target instanceof Tray)
//					return ((Tray)target)  ... ?
				return null;				
			}
		});
		return result;	
	}
    
	public static Control getFocusControl(final Display d){
		Control result = (Control) syncExec( d, new RunnableWithResult(){
			public Object runWithResult() {
				return d.getFocusControl();
			}});
		return result;
	}
	
	public static Control getParentControl(final Control child){
		Control result = (Control) syncExec( child.getDisplay(), new RunnableWithResult(){
			public Object runWithResult() {
				return child.getParent();
			}});
		return result;
	}
	
	/**
	 * get an id for the ToolItem, the
	 * ActionContributionItem  id or the associated 
	 * Action's ActionDefinitionId 
	 * @param item
	 * @return
	 */
	public static String getToolItemId(ToolItem item){
		Object data = ((ToolItem)item).getData();	
		if (!(data instanceof ActionContributionItem))
			return item.toString() + " No ActionContributionItem " ;
		ActionContributionItem contrib = (ActionContributionItem)data;
		String id = contrib.getAction().getActionDefinitionId();
		if (id == null)
			id = contrib.getId();
		if (id == null)
			return item.toString() + " null id ";
		return item.toString() + " " + id + " ";  
	}

	
	public static String getText(final TreeItem item, final int columnIndex) {
        String result = (String) syncExec(item.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
            	if (columnIndex <= 0)
            		return item.getText();
                return item.getText(columnIndex);
            }
        });
        return result;
	}

	public static void asyncExec(Display display, Runnable runnable) {
		try {
			display.asyncExec(runnable);
		} catch (NullPointerException npe) {

			LogHandler.log(npe);
		}
	}
	
}
