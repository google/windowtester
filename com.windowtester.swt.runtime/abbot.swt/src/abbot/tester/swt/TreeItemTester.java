package abbot.tester.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.Log;
import abbot.WaitTimedOutError;
import abbot.finder.matchers.swt.TextMatcher;
import abbot.finder.swt.BasicFinder;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.TestHierarchy;
import abbot.finder.swt.WidgetNotFoundException;
import abbot.script.Condition;
import abbot.swt.utilities.ExceptionHelper;

import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.state.MouseConfig;
import com.windowtester.runtime.swt.internal.util.PathStringTokenizer;

/**
 * Allows tree items to be automatically selected and expanded. Similar to
 * MenuItemTester, but works for TreeItems instead of MenuItems.
 * 
 * 
 * @author Henry McEuen
 * @author Chris Jaun
 * @version $Id: TreeItemTester.java,v 1.13 2008-11-24 18:52:22 pq Exp $
 */
public class TreeItemTester extends ItemTester {

    public static final String copyright = "Licensed Materials	-- Property of IBM\n"
            + "(c) Copyright International Business Machines Corporation, 2003\nUS Government "
            + "Users Restricted Rights - Use, duplication or disclosure restricted by GSA "
            + "ADP Schedule Contract with IBM Corp.";

    public static final String ARM_LISTENER_NAME = "a4sArmListener";

    public static final String SELECTION_LISTENER_NAME = "a4sSelectionListener";

    public static final String WATCHER_NAME = "a4sWatcher";

    public static final int PATH_CLICKING_WAIT_TIME = 500000;

    public static final int NONCHECKABLE = 0;

    public static final int SETCHECKED = 1;

    public static final int SETUNCHECKED = 2;

    public static final int TOGGLECHECKED = 3;
    
    public static final String DEFAULT_TREEITEM_PATH_DELIMITER = "/";
    
    public static final int DEFAULT_TREEITEM_DELAY = 300;

    //!pq: flag to indicate whether to emit verbose trace info to the console
	private static final boolean TRACE = false;
	
	//used for selection
	private static final int PRIMARY_BUTTON_MASK = MouseConfig.BUTTONS_REMAPPED ? SWT.BUTTON3 : SWT.BUTTON1;

    /**
     * Factory method.
     */
    public static TreeItemTester getTreeItemTester() {
        return (TreeItemTester) (getTester(TreeItem.class));
    }

    /**
     * Proxy for {@link TreeItem#getBackground()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the background color.
     */
    public Color getBackground(final TreeItem item) {
        Color result = (Color) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getBackground();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link TreeItem#getBounds()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the bounds of the item.
     */
    public Rectangle getBounds(final TreeItem item) {
        Rectangle result = (Rectangle) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getBounds();
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
    public boolean getExpanded(final TreeItem item) {
        Boolean result = (Boolean) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(item.getExpanded());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link TreeItem#getForeground()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the foreground color.
     */
    public Color getForeground(final TreeItem item) {
        Color result = (Color) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getForeground();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link TreeItem#getGrayed()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the grayed state of the item.
     */
    public boolean getGrayed(final TreeItem item) {
        Boolean result = (Boolean) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(item.getGrayed());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link TreeItem#getItemCount()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the number of direct children.
     */
    public int getItemCount(final TreeItem item) {
        Integer result = (Integer) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(item.getItemCount());
                    }
                });
        return result.intValue();
    }

    /**
     * Proxy for {@link TreeItem#getItems()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the direct children of the item.
     */
    public TreeItem[] getItems(final TreeItem item) {
        TreeItem[] result = (TreeItem[]) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getItems();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link TreeItem#getParent()}. <p/>
     * 
     * @param item
     *            the tree item under test.
     * @return the parent tree.
     */
    public Tree getParent(final TreeItem item) {
        Tree result = (Tree) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getParent();
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
    public TreeItem getParentItem(final TreeItem item) {
        TreeItem result = (TreeItem) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return item.getParentItem();
                    }
                });
        return result;
    }

    public TreeItem getTreeItemByPath(String path, Tree tree) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException
    {
        return getTreeItemByPath(path, DEFAULT_TREEITEM_PATH_DELIMITER, tree);
    }
    
    public TreeItem getTreeItemByPath(String path, String delimiter, Tree tree) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException
    {
        Shell parentShell = new TreeTester().getShell(tree);
        return actionClickTreeItem(path, delimiter, tree, parentShell, 200, NONCHECKABLE , -1, -1, 0);
    }

    /* end getters new */
    
    /**
     * Proxy for
     * {@link TreeItem#setExpanded(boolean)}.
     */
    public void setExpanded(final TreeItem t, final boolean expanded) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.setExpanded(expanded);
            }
        });
    }
    
    /**
     * Clicks all tree items in path to the given TreeItem, and clicks the given
     * tree item.
     * 
     * @param item
     *            the TreeItem to be clicked
     * @param parentControl
     *            the Control that owns the menu if it is a popup menu (or null
     *            othewise)
     * @param delay
     *            the delay in milliseconds between each pair of clicks
     * 
     */
    public void actionClickSubTreeItem(TreeItem item, Control parentControl,
            final int delay) {
        // get all items to be clicked along the path
        final Stack pathStack = new Stack();
        TreeItem parentItem;
        while (true) {
            pathStack.push(item);
            parentItem = getParentItem(item);
            if (parentItem == null)
                break;
            item = parentItem;
        }

        // doesn't apply to trees?
        // rt-click root parent control if root is a popup
        // if(((TreeTester)WidgetTester.getTester(Tree.class)).getStyle(parent)==SWT.POP_UP){
        // actionClick(parentControl);
        // actionDelay(delay);
        // }

        // click all items along the path
        while (pathStack.size() > 0) {
            TreeItem current = (TreeItem) pathStack.pop();
            setExpanded(current,true);
            actionDelay(delay);
            if (pathStack.size() == 0) {
                actionClick(current);
            }
        }
    }

    public void actionCheckSubTreeItem(TreeItem item, Control parentControl,
            final int delay) {
        // get all items to be clicked along the path
        final Stack pathStack = new Stack();
        TreeItem parentItem;
        while (true) {
            pathStack.push(item);
            parentItem = getParentItem(item);
            if (parentItem == null)
                break;
            item = parentItem;
        }

        // click all items along the path
        // System.out.println("Checking:");
        while (pathStack.size() > 0) {
            TreeItem current = (TreeItem) pathStack.pop();
            Log.log(getText(current));
            setExpanded(current,true);
            actionDelay(delay);
            if (pathStack.size() == 0) {
                actionClick(current);
                // System.out.println("checking: " + current.getText());
                setChecked(current,true);
            }
        }
    }

    /**
     * Invoke context menu on a tree item. Right-clicks on a tree and selects a
     * item from the context menu. Code adapted from <a
     * href="https://sourceforge.net/mailarchive/message.php?msg_id=11680274">
     * Jörg Weingarten</a>
     * 
     * @param item
     *            the tree item to select
     * @param tiText
     *            the text of the tree item to click
     * @param miText
     *            the text of the menu item to select
     */
    public void actionContextClickTreeItem(TreeItem item, String tiText,
            String miText) {

    	
    	trace("action-clicking item: " + item);

        //select the tree item
        actionClickSubTreeItem(item, getParent(item), 100);
        final Menu m = getParent(item).getMenu();
        // Thread to close the popup menu by pressing Escape
        final TreeItem aItem = item;
        Thread menuClick = new Thread() {
            public void run() {
                aItem.getDisplay().syncExec(new Runnable() {
                    public void run() {
                        // TODO: fix WidgetTester.actionKey(int, Display)
                        // actionKey(SWT.ESC, aItem.getDisplay());
                        
                    	//!pq: trying keyClick fix
                    	//new Robot().keyPress(KeyEvent.VK_ESCAPE);
                    	delay(2000);
                    	trace("clicking ESC to dismiss pop-up");
                    	keyClick(SWT.ESC);
                    }
                });
            }
        };
        menuClick.start();
        
        trace("right clicking item: " + item);
        
        // Right mouse click on TreeItem
        actionClick(item, 1, 1, "BUTTON3");
        
        //!pq: race condition here? ....
        
        //ISSUE: does not seem to work with dynamic menus
        //do we need to find the associated menu manager?
        
        
        // Now the Menu actually has all the items in it. --pq: but it doesn't!!!
        MenuItem[] aItems = new MenuTester().getItems(m);

        for (int i = 0; i < aItems.length; i++) {
            if (getText(aItems[i]).equals(miText)) {
                actionSelectPopupMenuItem(aItems[i], 1, 1);
                break;
            }
        }
    }
    
    
    /**
     * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items from left 
     * to right. Finally, clicking/selecting the last TreeItem in the path. 
     * 
     * The TreeItem path delimiter for the given path is "/".
     * 
     * The click location for EVERY TreeItem is determined by TreeItemTester.getDefaultRelativeClickLocation(final TreeItem item),
     * which clicks on the middle left of the TreeItem, in order to handled horizontally scrolled items.
     * 
     * @param path                  The full path to the TreeItem; each item is represented by its getText() value.
     * @param parentTree            The Tree which contains ALL of the items in the given path.
     * 
     * @throws WidgetNotFoundException
     * @throws MultipleWidgetsFoundException
     * 
     * NOTE:   "/item1/" resolves to a path of length 3, 
     *         with item texts "", "item1", and "".
     *         
     * FIXME:  It also uses a Windows-specific workaround to expand the TreeItems
     *         along the path; sending a right arrow key event once the TreeItem is selected.
     *         This does not work on GTK and possibly other window systems.
     *         TreeItem.setExpanded(true) does not have the same effect as
     *         actually clicking and expanding the tree item.
     *         
     * FIXME:  This method currently has the wrong behavior for
     *         multi-select trees since it possibly causes items along the path to be
     *         selected, whether or not this is the intent. 
     *            
     */
    public void actionSelectTreeItem(
            final String path, 
            final Tree parentTree) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        actionSelectTreeItem(path,DEFAULT_TREEITEM_PATH_DELIMITER,parentTree);
    }
    
    /**
     * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items from left 
     * to right. Finally, clicking/selecting the last TreeItem in the path. 
     * 
     * The click location for EVERY TreeItem is determined by TreeItemTester.getDefaultRelativeClickLocation(final TreeItem item),
     * which clicks on the middle left of the TreeItem, in order to handled horizontally scrolled items.
     * 
     * @param path                  The full path to the TreeItem; each item is represented by its getText() value.
     * @param delimiter             The String which will separate TreeItems in the given path.
     * @param parentTree            The Tree which contains ALL of the items in the given path.
     * 
     * @throws WidgetNotFoundException
     * @throws MultipleWidgetsFoundException
     * 
     * NOTE:   "/item1/" resolves to a path of length 3, 
     *         with item texts "", "item1", and "".
     *         
     * FIXME:  It also uses a Windows-specific workaround to expand the TreeItems
     *         along the path; sending a right arrow key event once the TreeItem is selected.
     *         This does not work on GTK and possibly other window systems.
     *         TreeItem.setExpanded(true) does not have the same effect as
     *         actually clicking and expanding the tree item.
     *         
     * FIXME:  This method currently has the wrong behavior for
     *         multi-select trees since it possibly causes items along the path to be
     *         selected, whether or not this is the intent. 
     *            
     */
    public void actionSelectTreeItem(
            final String path, 
            final String delimiter,
            final Tree parentTree)
    throws WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        actionClickTreeItem(path,delimiter,parentTree,new TreeTester().getShell(parentTree),DEFAULT_TREEITEM_DELAY,NONCHECKABLE,-1,-1,1);
    }
    
    /**
     * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items from left 
     * to right. Finally, clicking the last TreeItem in the path. 
     * 
     * The click location for EVERY TreeItem is determined by TreeItemTester.getDefaultRelativeClickLocation(final TreeItem item),
     * which clicks on the middle left of the TreeItem, in order to handled horizontally scrolled items.
     * 
     * @param path                  The full path to the TreeItem; each item is represented by its getText() value.
     * @param delimiter             The String which will separate TreeItems in the given path.
     * @param parentTree            The Tree which contains ALL of the items in the given path.
     * @param delay                 A delay in between selection of each TreeItem in the path, in milliseconds.
     * @param clicks                The number of clicks the LAST item in the given path should receive.
     * 
     * @throws WidgetNotFoundException
     * @throws MultipleWidgetsFoundException
     * 
     * NOTE:   "/item1/" resolves to a path of length 3, 
     *         with item texts "", "item1", and "".
     *         
     * FIXME:  It also uses a Windows-specific workaround to expand the TreeItems
     *         along the path; sending a right arrow key event once the TreeItem is selected.
     *         This does not work on GTK and possibly other window systems.
     *         TreeItem.setExpanded(true) does not have the same effect as
     *         actually clicking and expanding the tree item.
     *         
     * FIXME:  This method currently has the wrong behavior for
     *         multi-select trees since it possibly causes items along the path to be
     *         selected, whether or not this is the intent. 
     *            
     */
    public void actionClickTreeItem(
            final String path, 
            final String delimiter,
            final Tree parentTree,
            final int clicks) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        actionClickTreeItem(path,delimiter,parentTree,NONCHECKABLE,-1,-1,clicks);
    }
  
    /**
     * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items from left 
     * to right. Finally, clicking the last TreeItem in the path. 
     * 
     * The TreeItem path delimiter for the given path is "/".
     * 
     * The click location for EVERY TreeItem is determined by TreeItemTester.getDefaultRelativeClickLocation(final TreeItem item),
     * which clicks on the middle left of the TreeItem, in order to handled horizontally scrolled items.
     * 
     * @param path                  The full path to the TreeItem; each item is represented by its getText() value.
     * @param parentTree            The Tree which contains ALL of the items in the given path.
     * @param check                 Should the LAST item in the given path be checked {NONCHECKABLE | SETCHECKED | SETUNCHECKED | TOGGLECHECKED}
     * @param clicks                The number of clicks the LAST item in the given path should receive.
     * @return 
     * 
     * @throws WidgetNotFoundException
     * @throws MultipleWidgetsFoundException
     * 
     * NOTE:   "/item1/" resolves to a path of length 3, 
     *         with item texts "", "item1", and "".
     *         
     * FIXME:  It also uses a Windows-specific workaround to expand the TreeItems
     *         along the path; sending a right arrow key event once the TreeItem is selected.
     *         This does not work on GTK and possibly other window systems.
     *         TreeItem.setExpanded(true) does not have the same effect as
     *         actually clicking and expanding the tree item.
     *         
     * FIXME:  This method currently has the wrong behavior for
     *         multi-select trees since it possibly causes items along the path to be
     *         selected, whether or not this is the intent. 
     *            
     */
    public TreeItem actionClickTreeItem(
            final String path, 
            final Tree parentTree,  
            final int clicks) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        return actionClickTreeItem(path,DEFAULT_TREEITEM_PATH_DELIMITER,parentTree,NONCHECKABLE,-1,-1,clicks);
    }
    
    /**
     * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items from left 
     * to right. Finally, clicking, and/or checking the last TreeItem in the path. 
     * 
     * The TreeItem path delimiter for the given path is "/".
     * 
     * The click location for EVERY TreeItem is determined by TreeItemTester.getDefaultRelativeClickLocation(final TreeItem item),
     * which clicks on the middle left of the TreeItem, in order to handled horizontally scrolled items.
     * 
     * @param path                  The full path to the TreeItem; each item is represented by its getText() value.
     * @param parentTree            The Tree which contains ALL of the items in the given path.
     * @param check                 Should the LAST item in the given path be checked {NONCHECKABLE | SETCHECKED | SETUNCHECKED | TOGGLECHECKED}
     * @param clicks                The number of clicks the LAST item in the given path should receive.
     * 
     * @throws WidgetNotFoundException
     * @throws MultipleWidgetsFoundException
     * 
     * NOTE:   "/item1/" resolves to a path of length 3, 
     *         with item texts "", "item1", and "".
     *         
     * FIXME:  It also uses a Windows-specific workaround to expand the TreeItems
     *         along the path; sending a right arrow key event once the TreeItem is selected.
     *         This does not work on GTK and possibly other window systems.
     *         TreeItem.setExpanded(true) does not have the same effect as
     *         actually clicking and expanding the tree item.
     *         
     * FIXME:  This method currently has the wrong behavior for
     *         multi-select trees since it possibly causes items along the path to be
     *         selected, whether or not this is the intent. 
     *            
     */
    public void actionClickTreeItem(
            final String path, 
            final Tree parentTree,
            final int check,
            final int clicks) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        actionClickTreeItem(path,DEFAULT_TREEITEM_PATH_DELIMITER,parentTree,check,-1,-1,clicks);
    }
    
    /**
     * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items from left 
     * to right. Finally, clicking, and/or checking the last TreeItem in the path. 
     * 
     * The click location for EVERY TreeItem is determined by TreeItemTester.getDefaultRelativeClickLocation(final TreeItem item),
     * which clicks on the middle left of the TreeItem, in order to handled horizontally scrolled items.
     * 
     * @param path                  The full path to the TreeItem; each item is represented by its getText() value.
     * @param delimiter             The String which will separate TreeItems in the given path.
     * @param parentTree            The Tree which contains ALL of the items in the given path.
     * @param check                 Should the LAST item in the given path be checked {NONCHECKABLE | SETCHECKED | SETUNCHECKED | TOGGLECHECKED}
     * @param clicks                The number of clicks the LAST item in the given path should receive.
     * 
     * @throws WidgetNotFoundException
     * @throws MultipleWidgetsFoundException
     * 
     * NOTE:   "/item1/" resolves to a path of length 3, 
     *         with item texts "", "item1", and "".
     *         
     * FIXME:  It also uses a Windows-specific workaround to expand the TreeItems
     *         along the path; sending a right arrow key event once the TreeItem is selected.
     *         This does not work on GTK and possibly other window systems.
     *         TreeItem.setExpanded(true) does not have the same effect as
     *         actually clicking and expanding the tree item.
     *         
     * FIXME:  This method currently has the wrong behavior for
     *         multi-select trees since it possibly causes items along the path to be
     *         selected, whether or not this is the intent. 
     *            
     */
    public void actionClickTreeItem(
            final String path, 
            final String delimiter,
            final Tree parentTree, 
            final int check,
            final int clicks) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        actionClickTreeItem(path,delimiter,parentTree,new TreeTester().getShell(parentTree),DEFAULT_TREEITEM_DELAY,check,-1,-1,clicks);
    }
    
    /**
     * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items from left 
     * to right. Finally, clicking, and/or checking the last TreeItem in the path. 
     * 
     * @param path                  The full path to the TreeItem; each item is represented by its getText() value.
     * @param delimiter             The String which will separate TreeItems in the given path.
     * @param parentTree            The Tree which contains ALL of the items in the given path.
     * @param check                 Should the LAST item in the given path be checked {NONCHECKABLE | SETCHECKED | SETUNCHECKED | TOGGLECHECKED}
     * @param x                     The x location which should be clicked for EVERY TreeItem in given the path; relatively within the TreeItem.
     * @param y                     The y location which should be clicked for EVERY TreeItem in the given path; relatively within the TreeItem.
     * @param clicks                The number of clicks the LAST item in the given path should receive.
     * 
     * @throws WidgetNotFoundException
     * @throws MultipleWidgetsFoundException
     * 
     * NOTE:   "/item1/" resolves to a path of length 3, 
     *         with item texts "", "item1", and "".
     *         
     * FIXME:  It also uses a Windows-specific workaround to expand the TreeItems
     *         along the path; sending a right arrow key event once the TreeItem is selected.
     *         This does not work on GTK and possibly other window systems.
     *         TreeItem.setExpanded(true) does not have the same effect as
     *         actually clicking and expanding the tree item.
     *         
     * FIXME:  This method currently has the wrong behavior for
     *         multi-select trees since it possibly causes items along the path to be
     *         selected, whether or not this is the intent. 
     *            
     */
    public TreeItem actionClickTreeItem(
            final String path, 
            final String delimiter,
            final Tree parentTree, 
            final int check,
            final int x,
            final int y,
            final int clicks) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        return actionClickTreeItem(path,delimiter,parentTree,new TreeTester().getShell(parentTree),DEFAULT_TREEITEM_DELAY,check,x,y,clicks);
    }
    
    /**
     * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items from left 
     * to right. Finally, clicking, and/or checking the last TreeItem in the path. 
     * 
     * @param path                  The full path to the TreeItem; each item is represented by its getText() value.
     * @param delimiter             The String which will separate TreeItems in the given path.
     * @param parentTree            The Tree which contains ALL of the items in the given path.
     * @param parentDecorations     The Shell or Decorations which contains the Tree.
     * @param delay                 A delay in between selection of each TreeItem in the path, in milliseconds.
     * @param check                 Should the LAST item in the given path be checked {NONCHECKABLE | SETCHECKED | SETUNCHECKED | TOGGLECHECKED}
     * @param x                     The x location which should be clicked for EVERY TreeItem in given the path; relatively within the TreeItem.
     * @param y                     The y location which should be clicked for EVERY TreeItem in the given path; relatively within the TreeItem.
     * @param clicks                The number of clicks the LAST item in the given path should receive.
     * 
     * @throws WidgetNotFoundException
     * @throws MultipleWidgetsFoundException
     * 
     * NOTE:   "/item1/" resolves to a path of length 3, 
     *         with item texts "", "item1", and "".
     *         
     * FIXME:  It also uses a Windows-specific workaround to expand the TreeItems
     *         along the path; sending a right arrow key event once the TreeItem is selected.
     *         This does not work on GTK and possibly other window systems.
     *         TreeItem.setExpanded(true) does not have the same effect as
     *         actually clicking and expanding the tree item.
     *         
     * FIXME:  This method currently has the wrong behavior for
     *         multi-select trees since it possibly causes items along the path to be
     *         selected, whether or not this is the intent. 
     *            
     */
    public TreeItem actionClickTreeItem(
            final String path, 
            final String delimiter,
            final Tree parentTree, 
            final Decorations parentDecorations, 
            final int delay, 
            final int check,
            final int x,
            final int y,
            final int clicks) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        //@todo: do we need the parent decorations, can we just get it from the parentTree?
        //@todo: do we need the delay, can this be handled by conditions?
        final boolean [] done = {false};
        
        //fix to handle escaped '\' delims in path
        //final StringTokenizer st = new StringTokenizer(path,delimiter);
        final StringTokenizer st = new PathStringTokenizer(path);
        
        
        final int expectedNumberOfSelectionEvents = st.countTokens();
        final Watcher watcher = new Watcher(WATCHER_NAME);
        addWatcher(parentTree,watcher);
        final List throwables = new ArrayList();
        final TreeItem [] lastItem = new TreeItem[1];
        //final List<Throwable> throwables = new ArrayList<Throwable>();
        Thread t = new Thread(){
            public void run(){
                try {
                   lastItem[0] = actionClickTreeItemByPathImp(
                            st,
                            parentTree,
                            parentDecorations, 
                            delay, 
                            check,
                            x, 
                            y,
                            clicks);
                } catch (WaitTimedOutError e) {
                    //Log.warn(e);
                    throwables.add(e);
                } catch (WidgetNotFoundException e) {
                    //Log.warn(e);
                    throwables.add(e);
                } catch (MultipleWidgetsFoundException e) {
                    //Log.warn(e);
                    throwables.add(e);
                } finally {
                    removeWatcher(parentTree,watcher);
                    done[0] = true;
                }
            }
        };
        t.start();
        try {
        	trace("waiting for tree item selection condition...");
            Robot.wait(new Condition() {
                public boolean test() {
                    return done[0] || watcher.numberOfSelectionEvents == expectedNumberOfSelectionEvents;
                }
                
                public String toString() {
                    return path+" TreeItem path to be clicked:"+watcher.name+"["+watcher.numberOfSelectionEvents+"]!="+expectedNumberOfSelectionEvents;
                }
            });
            trace("...tree item selection condition got");
        } catch (WaitTimedOutError e) {
        	trace("tree item selection condition timed out!");
            Log.warn(e);
            throwables.add(e);
        }  
        if (throwables.size() > 0) {
            Throwable chain = ExceptionHelper.chainThrowables(throwables);
            if (chain instanceof WidgetNotFoundException) throw (WidgetNotFoundException)chain;
            if (chain instanceof MultipleWidgetsFoundException) throw (MultipleWidgetsFoundException)chain;
            if (chain instanceof WaitTimedOutError) throw (WaitTimedOutError)chain;
        }
        return lastItem[0];
    }

    
    protected void trace(String msg) {
    	if (TRACE)
    		System.out.println(msg);
    }

    private TreeItem actionClickTreeItemByPathImp(
            StringTokenizer path,
            final Tree parentTree, 
            Decorations parentDecorations, 
            int delay,
            int check, 
            int x, int y, //the relative click location for EVERY item in the path.
            int clicks) //the number of clicks for the LAST item in the path.
    throws WaitTimedOutError, WidgetNotFoundException, MultipleWidgetsFoundException 
    {
        String text = null;
        TreeItem lastItem = null;
        do {
            text = path.nextToken();
            lastItem = resolveAndClickItem(
                    text, 
                    parentTree, 
                    parentDecorations,
                    ((lastItem == null) ? (Widget) parentDecorations : (Widget) lastItem),
                    !path.hasMoreTokens(), 
                    check,
                    x, y,
                    clicks);
            actionDelay(delay);
        } while (path.hasMoreTokens());
        return lastItem;
    }

    // create a WidgetReference, resolve it, and click the widget
    private TreeItem resolveAndClickItem(
            String text, 
            Tree parentTree,
            Decorations parentDecorations, 
            Widget parent, 
            boolean isLast,
            int check, 
            int x, int y,
            int clicks) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException, WaitTimedOutError
    {
        //@todo: we might want to get this finder from somewhere else.
        BasicFinder finder = new BasicFinder(new TestHierarchy(
                parentDecorations.getDisplay()));
        TreeItem item = null;
        try {
            item = (TreeItem) finder.find(parentTree, new TextMatcher(text));
            showItem(parentTree, item);
            if (isLast) {
                if ( clicks > 0) {
                    if ((x > 0) && (y > 0)) {
                        click(item, x, y, PRIMARY_BUTTON_MASK /*SWT.BUTTON1*/,clicks);
                    } else {
                        Point p = getDefaultRelativeClickLocation(item);
                        click(item, p.x, p.y, PRIMARY_BUTTON_MASK /*SWT.BUTTON1*/,clicks);
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
    
    /**
     * Return a Point which decribes where this item should be clicked.
     * 
     * @param item The TreeItem to be clicked.
     * @return a Point which is relative to the TreeItem.
     */
    public static Point getDefaultRelativeClickLocation(final TreeItem item) {
        Point p = (Point) Robot.syncExec(item.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                Rectangle relativeBounds = item.getBounds();
                int plusX = Math.min( (relativeBounds.width / 2), 10 );
                int plusY = Math.min( (relativeBounds.height / 2), 10 );
                return new Point(plusX,plusY );
            }
        });
        return p;
    }

    /**
     * Shows the item.
     * 
     * If the item is already showing in the receiver, this method simply
     * returns. Otherwise, the items are scrolled and expanded until the item is
     * visible.
     * 
     * @param tree
     * @param item
     */
    public void showItem(final Tree tree, final TreeItem item) {
        new TreeTester().showItem(tree, item);

        Robot.wait(new TreeItemVisibleCondition(tree, item), 20000);
    }

    public boolean getChecked(final TreeItem item) {
        boolean result = ((Boolean) Robot.syncExec(item.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Boolean(item.getChecked());
                    }
                })).booleanValue();
        return result;
    }

    public void setChecked(final TreeItem item, final boolean checked) {
        item.getDisplay().syncExec(new Runnable() {
            public void run() {
                item.setChecked(checked);
            }
        });
    }

    /**
     * Select and set the checked state of a TreeItem.
     * 
     * @param item
     * @param checkstate
     */
    public void actionCheckItem(final TreeItem item, final int checkstate) {
        Point p = getDefaultRelativeClickLocation(item);
        actionClick(item,p.x,p.y);
        checkItem(item,checkstate);
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
                        actionKey(' ', item.getDisplay());
                    break;
                case SETUNCHECKED:
                    if (item.getChecked())
                        actionKey(' ', item.getDisplay());
                    break;
                case TOGGLECHECKED:
                    actionKey(' ', item.getDisplay());
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

    // expands the given tree item
    public void actionExpandItem(final TreeItem item) {
        final String text = getText(item);
        final boolean[] expanded = { getExpanded(item) };
        if (!expanded[0]) {
            item.getDisplay().syncExec(new Runnable() {
                public void run() {
                    item.getParent().showItem(item);
                    TreeItem[] items = item.getItems();
                    Point p = getDefaultRelativeClickLocation(item);
                    actionClick(item,p.x,p.y);
                    // only use hack if necessary
                    if (items.length == 0 || items[0].getText().equals("")) {
                        /*
                         * This only works on platforms that use the right arrow
                         * as a keyboard shortcut for tree expansion. The tree
                         * item can be expanded programmatically but in this
                         * case the expansion listeners are not notified, which
                         * is a problem for trees that are dynamically populated
                         * by JFace TreeViewers. The method call below doesn't
                         * solve the problem either; apparently it's not
                         * notifying the proper listeners.
                         */
                    	//!pq: this actionKey(..) call is causing us problems...
                        //actionKey(SWT.ARROW_RIGHT, item.getDisplay());
                        keyClick(SWT.ARROW_RIGHT);
                        Robot.wait(new Condition() {
                            public boolean test() {
                                return expanded[0] || getExpanded(item);
                            }

                            public String toString() {
                                return text + " to be expanded.";
                            }
                        });
                    }
                    if (!item.getExpanded()) {
                        //Log.warn(text + " TreeItem setExpanded(true) by api.");
                        item.setExpanded(true);
                        if (!item.getExpanded()) {
//                            Log
//                                    .warn(text
//                                            + " TreeItem setExpanded(true) by api failed b/c ignoreExpand must be set.");
                            expanded[0] = true;
                        }
                    }
                }
            });
        }
        Robot.wait(new Condition() {
            public boolean test() {
                return expanded[0] || getExpanded(item);
            }

            public String toString() {
                return text + " to be expanded.";
            }
        });
        
    }    
    
   private class Watcher implements SelectionListener {
        //public boolean gotSelection = false;

        public int numberOfSelectionEvents = 0;

        public String name;

        protected Watcher(String name) {
            this.name = name;
        }

        public void widgetSelected(SelectionEvent e) {
            Log.debug("SWT.Selection");
            //gotSelection = true;
            numberOfSelectionEvents++;
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            Log.debug("DEFAULT: SWT.Selection");
        }
    }

    // adds a watcher-listener to the given tree item
    private void addWatcher(final Tree tree, final Watcher watcher) {
        Log.debug("addWatcher..." + watcher.name);
        tree.getDisplay().syncExec(new Runnable() {
            public void run() {
                tree.setData(watcher.name, watcher);
                tree.addSelectionListener(watcher);
            }
        });
    }

    // removes the given item's watcher-listener
    private boolean removeWatcher(final Tree tree, final Watcher watcher) {
        Log.debug("removeWatcher..." + watcher.name);
        boolean result = false;
        try {
           final Display display = tree.getDisplay();
        result = ((Boolean) Robot.syncExec(display,
                new RunnableWithResult() {
            //@Override
            public Object runWithResult() {
                boolean result = false;
                try {
                    Watcher treeWatcher = (Watcher) tree
                    .getData(watcher.name);
                    if (treeWatcher != null && watcher == treeWatcher) {
                        Log.debug("removing..." + treeWatcher.name
                                + "["
                                + treeWatcher.numberOfSelectionEvents
                                + "]");
                        tree.removeSelectionListener(treeWatcher);
                        result = true;
                    }
                } catch (SWTException e) {
                    Log
                    .debug("Trying to remove a watcher from a disposed Tree. This is ok.");
                    result = false;
                }
                return new Boolean(result);
            }
        })).booleanValue();
	} catch (SWTException e) {
		Log.debug("Trying to remove a watcher from a disposed Tree. This is ok.");
        }
        return result;
    }

    public class TreeItemVisibleCondition implements Condition {
        private Tree _parentTree;
        private TreeItem _item;
        private WidgetTester _tester = new WidgetTester();

        public TreeItemVisibleCondition(Tree parentTree, TreeItem item) {
            _parentTree = parentTree;
            _item = item;
        }

        public boolean test() {
            Rectangle treeBounds = _tester.getGlobalBounds(_parentTree);
            Rectangle itemBounds = _tester.getGlobalBounds(_item);

            int maxTreeY = treeBounds.y + treeBounds.height;
            int approxItemY = itemBounds.y + (itemBounds.height / 2);

            boolean success = approxItemY < maxTreeY;
            Log.debug("Checking visibility for:" + getText(_item) + ":"
                    + success);
            return success;
        }
        
        public String toString() {
        	return "tree item: " + UIProxy.getToString(_item) + " to become visible";
        }
    }


}
