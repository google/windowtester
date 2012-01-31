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
package com.windowtester.runtime.swt.internal.abbot;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.WaitTimedOutError;
import abbot.finder.swt.BasicFinder;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.TestHierarchy;
import abbot.finder.swt.WidgetNotFoundException;
import abbot.script.Condition;
import abbot.swt.utilities.ExceptionHelper;
import abbot.tester.swt.Robot;
import abbot.tester.swt.RunnableWithResult;
import abbot.tester.swt.TreeTester;

import com.windowtester.runtime.swt.internal.abbot.matcher.TreeItemByPathMatcher;
import com.windowtester.runtime.swt.internal.operation.SWTTreeItemOperation;
import com.windowtester.runtime.swt.internal.util.PathStringTokenizer;
import com.windowtester.runtime.swt.internal.util.TextUtils;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;

//introduces various fixes to the base abbot tester
public class TreeItemTester extends abbot.tester.swt.TreeItemTester {

	//public for testing
	public static final String NO_CHILDREN_EXPANSION_ERROR_MSG_DETAIL = "contains no children";

	private static final TreeItem[] DISPOSED_ITEMS = new TreeItem[0];

	//temporarily public to faciliate user configuration (if necessary)
	public static long TREE_ITEM_EXPANSION_TIMEOUT = 15000;
	
	private static String NEW_LINE = System.getProperty("line.separator", "\n");

    private TreeItem lastFoundTreeItem;

	private Tree parentTree;

	public TreeItem actionClickTreeItem(final String path,
            final String delimiter, final Tree parentTree,
            final Decorations parentDecorations, final int delay,
            final int check, final int x, final int y, final int clicks)
            throws WidgetNotFoundException, MultipleWidgetsFoundException {
        // @todo: do we need the parent decorations, can we just get it from the
        // parentTree?
        // @todo: do we need the delay, can this be handled by conditions?
        final boolean[] done = { false };
        final StringTokenizer st = new PathStringTokenizer(path);
        final int expectedNumberOfSelectionEvents = st.countTokens();
        final Watcher watcher = new Watcher(WATCHER_NAME);
        addWatcher(parentTree, watcher);
        final List throwables = new ArrayList();
        final TreeItem[] lastItem = new TreeItem[1];
        // final List<Throwable> throwables = new ArrayList<Throwable>();
        Thread t = new Thread() {
            public void run() {
                try {
                    lastItem[0] = actionClickTreeItemByPathImp(st, parentTree,
                            parentDecorations, delay, check, x, y, clicks);
                } catch (WaitTimedOutError e) {
                    //Log.warn(e);
                    throwables.add(e);
                } catch (WidgetNotFoundException e) {
                    //Log.warn(e);
                    throwables.add(e);
                } catch (MultipleWidgetsFoundException e) {
                    //Log.warn(e);
                    throwables.add(e);
                } catch (TreeItemExpansionFailedException e) {
                	throwables.add(e);
                } finally {
                    removeWatcher(parentTree, watcher);
                    done[0] = true;
                }
            }
        };
        t.start();
        try {
            Robot.wait(new Condition() {
                public boolean test() {
                    return done[0];
                    /*
                     * This was returning BEFORE lastItem[0] was set, meaning a null item was returned
                     */        
                    //|| watcher.numberOfSelectionEvents == expectedNumberOfSelectionEvents;
                }

                public String toString() {
                    return path + " TreeItem path to be clicked:"
                            + watcher.name + "["
                            + watcher.numberOfSelectionEvents + "]!="
                            + expectedNumberOfSelectionEvents;
                }
            }, getSelectionTimeout());
        } catch (WaitTimedOutError e) {
            //Log.warn(e);
            throwables.add(e);
        }
        if (throwables.size() > 0) {
            Throwable chain = ExceptionHelper.chainThrowables(throwables);
            TreeItemExpansionFailedException expansionTimeout = findExpansionTimeout(chain);
            if (expansionTimeout != null)
            	throw newExpansionSearchException(expansionTimeout);
            if (chain instanceof WidgetNotFoundException)
                throw (WidgetNotFoundException) chain;
            if (chain instanceof MultipleWidgetsFoundException)
                throw (MultipleWidgetsFoundException) chain;
            if (chain instanceof WaitTimedOutError) {
            	throw (WaitTimedOutError) chain;
            }
            	
        }
        if (lastItem[0] == null)
        	throw new AssertionError("clicked item is null!");
        return lastItem[0];
    }

		private long getSelectionTimeout() {
		int defaultWaitTimeout = Robot.getDefaultWaitTimeout();
		return Math.max(defaultWaitTimeout, TREE_ITEM_EXPANSION_TIMEOUT + 5000);
	}

	private WidgetNotFoundException newExpansionSearchException(TreeItemExpansionFailedException ex) {
		return ex.asSearchException();
	}

	private TreeItemExpansionFailedException findExpansionTimeout(Throwable chain) {
		if (chain == null)
			return null;
		if (chain instanceof TreeItemExpansionFailedException)
			return (TreeItemExpansionFailedException)chain;
		return findExpansionTimeout(chain.getCause());
	}



    /**
     * Select and set the checked state of a TreeItem.
     * 
     * @param item
     * @param checkstate
     */
    public void actionCheckItem(final TreeItem item, final int checkstate) {
        Point p = getDefaultRelativeClickLocation(item);
        actionClick(item, p.x, p.y);
        checkItem(item, checkstate);
    }

    /**
     * expands the given tree item
     * 
     * FIXME: OS-SPECIFIC WORKAROUNDS
     * 
     * The abbot.swt tester is
     * failing on linux b/c ARROW_RIGHT does not expand the tree item. ' '
     * should work for linux, and be inoccuous on windows.
     */
    public void actionExpandItem(final TreeItem item) {
//        final String text = getText(item);
        
//        boolean expanded = getExpanded(item);
//        if (!expanded) {

    	
        	new SWTTreeItemOperation(new TreeItemReference(item), -1).expand().execute();
			
			// Wait for the expansion
			// [Dan] Is there a better way? A condition that we could use?
        	// Is this needed? Print out debugging to see if it is needed
//        	boolean first = true;
//			for (int tries = 0; tries < 500; tries++) {
//				if (getExpanded(item))
//					return;
//				sleep();
//				if (first) {
//					first = false;
//					System.out.println("Waiting for tree item to be expanded");
//				}
//			}
//			throw new RuntimeException("Failed to expand tree item");
//		}
        
//        
//        if (!expanded) {
//            final Display display = item.getDisplay();
//			display.syncExec(new Runnable() {
//                public void run() {
//                    Tree parent = item.getParent();
//                    parent.showItem(item);
//                    
//                    Point p = getDefaultRelativeClickLocation(item);
//                    clickToSelectItemToExpand(item, p);
//                }
//            });
//                    
//            Event eV = new Event();
//            eV.type = SWT.KeyDown;
//   
//            eV.keyCode = getExpandKey();
//			new SWTPushEventOperation(eV).execute();
//                    
//			if (Platform.isOSX())
//            	Robot.waitForIdle(display);
//            else
//            	Robot.delay(200);
//
//            eV = new Event();
//            eV.type = SWT.KeyUp;
//            eV.keyCode = getExpandKey();
//			new SWTPushEventOperation(eV).execute();
//            
//			Robot.delay(200);
//        }
//
//        try {
//        	Robot.wait(new Condition() {
//        		public boolean test() {
//        			return getExpanded(item);
//        		}
//        		public String toString() {
//        			return text + " to be expanded.";
//        		}
//        	}, TREE_ITEM_EXPANSION_TIMEOUT);
//        } catch (WaitTimedOutError timeout) {
//        	
//        	/*
//        	 * Test if there are no items
//        	 */
//        	String reason = (getItemCount(item) == 0) ? NO_CHILDREN_EXPANSION_ERROR_MSG_DETAIL : "expand timed out";
//        	reason += " after "+ TREE_ITEM_EXPANSION_TIMEOUT + " ms"; 
//			throw new TreeItemExpansionFailedException(getText(item)).withReason(reason);
//        }
//        expanded = getExpanded(item);

    }


	protected int getExpandKey() {
        if (Platform.isOSX()) // Mac testing
        	return SWT.ARROW_RIGHT;
        return SWT.KEYPAD_ADD;
	}


	private TreeItem actionClickTreeItemByPathImp(StringTokenizer path,
            final Tree parentTree, Decorations parentDecorations, int delay,
            int check, int x, int y, // the relative click location for EVERY
            // item in the path.
            int clicks) // the number of clicks for the LAST item in the path.
            throws WaitTimedOutError, WidgetNotFoundException,
            MultipleWidgetsFoundException {
    	
    	cacheTreeForDebugging(parentTree);
    	
        TreeItem lastItem = null;
        String pathString = null;
        do {
        	/* Escaped path delims:
             */
        	if (pathString == null)
        		pathString = TextUtils.escapeSlashes(path.nextToken());
        	else
        		pathString += "/" + TextUtils.escapeSlashes(path.nextToken());
        	
            lastItem = resolveAndClickItem(pathString, parentTree, parentDecorations,
                    ((lastItem == null) ? (Widget) parentDecorations
                            : (Widget) lastItem), !path.hasMoreTokens(), check,
                    x, y, clicks);
//            text = path.nextToken();
//            lastItem = resolveAndClickItem(text, parentTree, parentDecorations,
//                    ((lastItem == null) ? (Widget) parentDecorations
//                            : (Widget) lastItem), !path.hasMoreTokens(), check,
//                    x, y, clicks);
         
            actionDelay(delay);
        } while (path.hasMoreTokens());
        return lastItem;
    }

    private void cacheTreeForDebugging(Tree parentTree) {
		this.parentTree = parentTree;
	}

	/* checks the given tree item */
    private void checkItem(final TreeItem item, final int checkstate) {
        final String text = getText(item);
        final boolean checked = getChecked(item);
        item.getDisplay().syncExec(new Runnable() {
            /* don't have to actionclick -- item already selected */
            // TODO: find appropriate keys for platforms other than Windows
            public void run() {
                switch (checkstate) {
                case SETCHECKED:
                    // item.setChecked(true);
                    if (!item.getChecked())
                        toggleItem(item);
                    break;
                case SETUNCHECKED:
                    if (item.getChecked())
                        toggleItem(item);
                    break;
                case TOGGLECHECKED:
                    toggleItem(item);
                    break;
                }
            }
        });
        final boolean[] expected = { false };
        switch (checkstate) {
        case SETCHECKED:
            expected[0] = true;
            break;
        case SETUNCHECKED:
            expected[0] = false;
            break;
        case TOGGLECHECKED:
            expected[0] = !checked;
            break;
        }

        Robot.wait(new Condition() {
            public boolean test() {
                return getChecked(item) == expected[0];
            }

            public String toString() {
                return text + " TreeItem to change check state:expected["
                        + expected[0] + "]actual[" + getChecked(item) + "]";
            }
        });
    }

    
	//fixed for ambiguous path matching with parents and children sharing a name
    //RUB: using accumulated path string rather than just widget text
	// create a WidgetReference, resolve it, and click the widget
    private TreeItem resolveAndClickItem(String pathString, Tree parentTree,
            Decorations parentDecorations, Widget parent, boolean isLast,
            int check, int x, int y, int clicks)
            throws WidgetNotFoundException, MultipleWidgetsFoundException,
            WaitTimedOutError {
        // @todo: we might want to get this finder from somewhere else.
        BasicFinder finder = new BasicFinder(new TestHierarchy(
                parentDecorations.getDisplay()));
        TreeItem item = null;
        try {
        	
            //item = (TreeItem) finder.find(parentTree, new TextMatcher(text));
        	try {
        		item = (TreeItem) finder.find(parentTree, new TreeItemByPathMatcher(pathString));
        	} catch (WidgetNotFoundException wnfe) {
        		throw new WidgetNotFoundException(getExceptionMsg(pathString, "not found"));
        	} catch (MultipleWidgetsFoundException mwfe) {
        		throw new MultipleWidgetsFoundException(getExceptionMsg(pathString, "ambiguous"), getTreeItemContextForCurrentClick());
        	}
        	showItem(parentTree, item);
            cacheItemForDebugging(item);
            if (isLast) {
                if (clicks > 0) {
                    if ((x > 0) && (y > 0)) {
                        click(item, x, y, SWT.BUTTON1, clicks);
                    } else {
                        Point p = getDefaultRelativeClickLocation(item);
                        click(item, p.x, p.y, SWT.BUTTON1, clicks);
                    }
                    if (check != NONCHECKABLE) {
                        /* check the item as well */
                        checkItem(item, check);
                    }
                }
            } else {
                actionExpandItem(item);
            }
        } catch (WidgetNotFoundException e) {
            //Log.warn("Widget TreeItem {" + text + "} not found");
            //Log.warn(e);
            //finder.dbPrintWidgets();
            throw e;
        } catch (MultipleWidgetsFoundException e) {
            //Log.warn("Multiple Widgets found when looking for " + " MenuItem {"
            //        + text + "}");
            //Log.warn(e);
            throw e;
        } catch (WaitTimedOutError e) {
            //Log.warn("TreeItem operation timed out for:" + text);
            //Log.warn(e);
            throw e;
        }

        return item;
    }

    private String getExceptionMsg(String pathString, String detail) {
    	
    	String itemToken = getLastPathToken(pathString);
    	
    	StringBuffer sb = new StringBuffer("Item: [" + itemToken + "] " + detail + " in item list: " + NEW_LINE);
    	TreeItem[] items = getTreeItemContextForCurrentClick();
    	if (items == DISPOSED_ITEMS)
    		return sb.append("\t\t[*disposed*]").append(NEW_LINE).toString();
    	int numChildren = items.length;
    	for (int i = 0; i < numChildren; i++) {
    		sb.append("\t\t");
			sb.append("[").append(getText(items[i])).append("]");
			sb.append(NEW_LINE);
		}
    	return sb.toString();
	}

	private TreeItem[] getTreeItemContextForCurrentClick() {
		try {
			if (lastFoundTreeItem != null)
				return getItems(lastFoundTreeItem);
			if (parentTree == null)
				return new TreeItem[]{};
			return new TreeTester().getItems(parentTree);
		} catch (SWTException e) {
			/*
			 * This is to guard against the situation where the parent item
			 * or tree are disposed while were inspecting...
			 */
			return DISPOSED_ITEMS;
		}
	}

	
	private String getLastPathToken(String pathString) {    	
    	PathStringTokenizer path = new PathStringTokenizer(pathString);
    	String token = null;
    	while (path.hasMoreTokens()) {
    		token = path.nextToken();
    	}
    	return token;
	}

	private void cacheItemForDebugging(TreeItem item) {
		lastFoundTreeItem = item;
	}

    
    
    
	private void toggleItem(TreeItem item) {
        // FIXME assert item has focus
        // Arrow left 3 times b/c linux native widget has 3 widgets, with most
        // left being the checkbox.
    	if(Platform.isLinux())
    	{
    		actionKey(SWT.ARROW_LEFT, item.getDisplay());
    		actionKey(SWT.ARROW_LEFT, item.getDisplay());
    		actionKey(SWT.ARROW_LEFT, item.getDisplay());
    	}
        actionKey(' ', item.getDisplay());
    }

    public static class TreeItemExpansionFailedException extends
            abbot.tester.swt.ActionFailedException {

		private static final long serialVersionUID = 6965001575680795863L;
		private static final String MESSAGE_PREFIX = "TreeItem expansion failed for: ";
		private String reason;

        
        public TreeItemExpansionFailedException(String treeItemText) {
            super(MESSAGE_PREFIX + treeItemText);
        }


		public WidgetNotFoundException asSearchException() {
			return new WidgetNotFoundException(getMessage());
		}


		public TreeItemExpansionFailedException withReason(String reason) {
			this.reason = reason;
			return this;
		}
		
		public String getMessage() {
			String message = super.getMessage();
			if (reason != null)
				return message += " (" + reason +")";
			return message;
		}
		

    }

    private class Watcher implements SelectionListener {
        //public boolean gotSelection = false;

        public int numberOfSelectionEvents = 0;

        public String name;

        protected Watcher(String name) {
            this.name = name;
        }

        public void widgetSelected(SelectionEvent e) {
            //Log.debug("SWT.Selection");
            //gotSelection = true;
            numberOfSelectionEvents++;
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            //Log.debug("DEFAULT: SWT.Selection");
        }
    }

    // adds a watcher-listener to the given tree item
    private void addWatcher(final Tree tree, final Watcher watcher) {
        //Log.debug("addWatcher..." + watcher.name);
        tree.getDisplay().syncExec(new Runnable() {
            public void run() {
                tree.setData(watcher.name, watcher);
                tree.addSelectionListener(watcher);
            }
        });
    }

    // removes the given item's watcher-listener
    private boolean removeWatcher(final Tree tree, final Watcher watcher) {
        //Log.debug("removeWatcher..." + watcher.name);
        boolean result = false;
        try {
            final Display display = tree.getDisplay();
            result = ((Boolean) Robot.syncExec(display,
                    new RunnableWithResult() {
                        // @Override
                        public Object runWithResult() {
                            boolean result = false;
                            try {
                                Watcher treeWatcher = (Watcher) tree
                                        .getData(watcher.name);
                                if (treeWatcher != null
                                        && watcher == treeWatcher) {
                                    //Log
//                                            .debug("removing..."
//                                                    + treeWatcher.name
//                                                    + "["
//                                                    + treeWatcher.numberOfSelectionEvents
//                                                    + "]");
                                    tree.removeSelectionListener(treeWatcher);
                                    result = true;
                                }
                            } catch (SWTException e) {
                                //Log
                                //        .debug("Trying to remove a watcher from a disposed Tree. This is ok.");
                                result = false;
                            }
                            return new Boolean(result);
                        }
                    })).booleanValue();
        } catch (SWTException e) {
            //Log
            //        .debug("Trying to remove a watcher from a disposed Tree. This is ok.");
        }
        return result;
    }


	protected void clickToSelectItemToExpand(final TreeItem item, Point p) {
		actionClick(item, p.x, p.y);
	}

}
