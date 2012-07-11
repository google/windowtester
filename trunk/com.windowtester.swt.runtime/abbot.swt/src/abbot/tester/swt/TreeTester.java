
package abbot.tester.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/* $codepro.preprocessor.if version >= 3.1 $ */
import org.eclipse.swt.widgets.TreeColumn;
/* $codepro.preprocessor.endif $ */


/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Tree.
 */
public class TreeTester extends CompositeTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
    
    /**
     * Proxy for
     * {@link Tree#addSelectionListener(SelectionListener)}.
     */
    public void addSelectionListener(final Tree t, final SelectionListener listener) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.addSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link Tree#addTreeListener(TreeListener)}.
     */
    public void addTreeListener(final Tree t, final TreeListener listener) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.addTreeListener(listener);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link Tree#removeSelectionListener(SelectionListener)}.
     */
    public void removeSelectionListener(final Tree t, final SelectionListener listener) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.removeSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link Tree#removeTreeListener(TreeListener)}.
     */
    public void removeTreeListener(final Tree t, final TreeListener listener) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.removeTreeListener(listener);
            }
        });
    }

	/**
	 * Proxy for {@link Tree#getColumnCount()}.
	 */
    /* $codepro.preprocessor.if version >= 3.1 $ */
	public int getColumnCount(final Tree tree) {
		Integer result = (Integer) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(tree.getColumnCount());
			}
		});
		return result.intValue();
	}
	/* $codepro.preprocessor.endif $ */

	/**
	 * Proxy for {@link Tree#getColumn(int)}.
	 */
	/* $codepro.preprocessor.if version >= 3.1 $ */
	public TreeColumn getColumn(final Tree tree, final int index) {
		TreeColumn result = (TreeColumn) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return tree.getColumn(index);
			}			
		});
		return result;	
	}
    /* $codepro.preprocessor.endif $ */
	
    /**
     * Proxy for {@link Tree#getColumns()}.
     */
	/* $codepro.preprocessor.if version >= 3.1 $ */
    public TreeColumn [] getColumns(final Tree t) {
        List result = (List) Robot.syncExec(t.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        TreeColumn [] items = t.getColumns();
                        List list = new ArrayList(items.length);
                       
                        for (int i = 0; i < items.length; i++) {
							list.add(items[i]);
						}
                        return list;
                    }
                });
        TreeColumn [] items = new TreeColumn [result.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = (TreeColumn)result.get(i);
        }
        return items;
    }
    /* $codepro.preprocessor.endif $ */

	/**
	 * Proxy for {@link Tree#getGridLineWidth()()}.
	 */
	/* $codepro.preprocessor.if version >= 3.1 $ */
	public int getGridLineWidth(final Tree tree) {
		Integer result = (Integer) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(tree.getGridLineWidth());
			}
		});
		return result.intValue();
	}
	/* $codepro.preprocessor.endif $ */

	/**
	 * Proxy for {@link Tree#getHeaderHeight()()}.
	 */
	/* $codepro.preprocessor.if version >= 3.1 $ */
	public int getHeaderHeight(final Tree tree) {
		Integer result = (Integer) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(tree.getHeaderHeight());
			}
		});
		return result.intValue();
	}
	/* $codepro.preprocessor.endif $ */

	/**
	 * Proxy for {@link Tree#getHeaderVisible()()}.
	 */
	/* $codepro.preprocessor.if version >= 3.1 $ */
	public boolean getHeaderVisible(final Tree tree) {
		Boolean result = (Boolean) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Boolean(tree.getHeaderVisible());
			}
		});
		return result.booleanValue();
	}
	/* $codepro.preprocessor.endif $ */
    
	/**
	 * Proxy for {@link Tree#getItem(org.eclipse.swt.graphics.Point)}.
	 * <p/>
	 * @param tree the tree under test.
	 * @param point the point under which the item to find.
	 * @return the tree item under the point.
	 */
	public TreeItem getItem(final Tree tree, final Point point){
		TreeItem result = (TreeItem) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return tree.getItem(point);
			}			
		});
		return result;		
	}

	/**
	 * Proxy for {@link Tree#getItemCount()}.
	 * <p/>
	 * @param tree the tree under test.
	 * @return the number of items in the tree.
	 */
	public int getItemCount(final Tree tree) {
		Integer result = (Integer) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(tree.getItemCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Tree#getItemHeight()}.
	 * <p/>
	 * @param tree the tree under test.
	 * @return the height of the items.
	 */
	public int getItemHeight(final Tree tree) {
		Integer result = (Integer) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(tree.getItemHeight());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Tree#getItems()}.
	 * <p/>
	 * @param tree the tree under test.
	 * @return the children.
	 */
	public TreeItem[] getItems(final Tree tree) {
		TreeItem result[] = (TreeItem[]) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return tree.getItems();
			}			
		});
		return result;
	}

	/**
	 * Proxy for {@link TreeItem#getItems()}.
	 * <p/>
	 * @param tree the tree under test.
	 * @return the children.
	 */
	public TreeItem[] getItems(final TreeItem treeItem) {
		TreeItem result[] = (TreeItem[]) Robot.syncExec(treeItem.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return treeItem.getItems();
			}			
		});
		return result;
	}
	
	
	/**
	 * Proxy for {@link Tree#getParentItem()}.
	 * <p/>
	 * @param tree the tree under test.
	 * @return the parent tree item.
	 */
	public TreeItem getParentItem(final Tree tree) {
		TreeItem result = (TreeItem) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return tree.getParentItem();
			}			
		});
		return result;	
	}

	public boolean isSelected(Tree tree, TreeItem item) {
		TreeItem[] items = getSelection(tree);
		for (int i = 0; i < items.length; i++) {
			if (items[i] == item)
				return true;
		}
		return false;
	}
	
	/**
	 * Proxy for {@link Tree#getSelection()}.
	 * <p/>
	 * @param tree the tree under test.
	 * @return the selected items.
	 */
	public TreeItem[] getSelection(final Tree tree) {
		TreeItem[] result = (TreeItem[]) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return tree.getSelection();
			}			
		});
		return result;
	}

	/**
	 * Proxy for {@link Tree#getSelectionCount()}.
	 * <p/>
	 * @param tree the tree under test.
	 * @return the number of selected items.
	 */
	public int getSelectionCount(final Tree tree) {
		Integer result = (Integer) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(tree.getSelectionCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Tree#getTopItem()}.
	 * <p/>
	 * @param tree the tree under test.
	 * @return the top item.
	 */
	public TreeItem getTopItem(final Tree tree) {
		TreeItem result = (TreeItem) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return tree.getTopItem();
			}			
		});
		return result;
	}

	/**
	 * Proxy for {@link Tree#indexOf(TreeColumn)}.
	 */
	/* $codepro.preprocessor.if version >= 3.1 $ */
	public int indexOf(final Tree tree, final TreeColumn column) {
		Integer result = (Integer) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(tree.indexOf(column));
			}
		});
		return result.intValue();
	}
	/* $codepro.preprocessor.endif $ */

	/**
	 * Proxy for {@link Tree#indexOf(TreeItem)}.
	 */
	/* $codepro.preprocessor.if version >= 3.1 $ */
	public int indexOf(final Tree tree, final TreeItem item) {
		Integer result = (Integer) Robot.syncExec(tree.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				return new Integer(tree.indexOf(item));
			}
		});
		return result.intValue();
	}
	/* $codepro.preprocessor.endif $ */
    
    /**
     * Proxy for
     * {@link Tree#selectAll()}.
     */
    public void selectAll(final Tree t) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.selectAll();
            }
        });
    }
    
    /**
     * Proxy for
     * {@link Tree#showColumn(TreeColumn)}.
     */
	/* $codepro.preprocessor.if version >= 3.1 $ */
    public void showColumn(final Tree t, final TreeColumn column) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.showColumn(column);
            }
        });
    }
    /* $codepro.preprocessor.endif $ */
    
    /**
     * Proxy for
     * {@link Tree#showItem(TreeItem)}.
     */
    public void showItem(final Tree t, final TreeItem item) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.showItem(item);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link Tree#showSelection()}.
     */
    public void showSelection(final Tree t) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.showSelection();
            }
        });
    }
    
	/** Checks if the SWT.CHECK style bit is set for the given tree **/
	public boolean isCheckStyleBitSet(final Tree tree) {
		Boolean result = (Boolean) Robot.syncExec(tree.getDisplay(),new RunnableWithResult(){
			public Boolean runWithResult(){
				return (tree.getStyle() & SWT.CHECK) != 0;				
			}
		});
		return result;
	}

}
