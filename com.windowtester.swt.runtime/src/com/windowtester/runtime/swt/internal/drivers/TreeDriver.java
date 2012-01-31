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
package com.windowtester.runtime.swt.internal.drivers;

import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReferenceContainer;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.StringComparator;

/**
 * Given a path, this class drives tree selection by interacting with tree and tree item
 * references. This class collects state and thus should be re-used.
 */
public class TreeDriver
{
	/**
	 * Used internally by
	 * {@link #getNextTreeItem(TreeItemReferenceContainer, int, String)}
	 */
	private TreeItemReference[] items;

	/**
	 * Used internally by
	 * {@link #getNextTreeItem(TreeItemReferenceContainer, int, String)}
	 */
	private String[][] textForItems;

	/**
	 * Expand the items in the tree to show the specified tree item
	 * 
	 * @param tree the tree containing the tree item
	 * @param itemPath the slash separated path to the tree item
	 * @return the tree item revealed
	 */
	public TreeItemReference reveal(TreeReference tree, String itemPath) throws WidgetNotFoundException,
		MultipleWidgetsFoundException
	{

		//		TreeItemReference[] items = tree.getItemsWithText();
		//		PathString path = new PathString(itemPath);
		//		for (String nodeText : path) {
		//			for (TreeItemReference item : items){
		//				if (item.isMatchedBy(new ByTextMatcher(nodeText))){
		////					System.out.println("item: " + item + " matched by: " + nodeText);
		//					if (!path.hasNext())
		//						return item;
		////					show(tree, item);
		//					item.expand();
		////					//debugging
		////					try {
		////						Thread.sleep(2000);
		////					} catch(Exception e){ }
		//					//need a wait here -- perhaps just until items appear?
		//					items = item.getItemsWithText();
		//					break;
		//				}
		//			}
		//		}
		//		
		//		//mirrors call in com.windowtester.runtime.swt.internal.selector.TreeItemSelector2.click(int, Tree, String, int, Point, int)
		//		ScreenCapture.createScreenCapture();
		//		// TODO[pq]: improve error message
		//		throw new WidgetNotFoundException("Path: \'"+ itemPath + "\' not found in " + tree);

		PathString path = new PathString(itemPath);
		TreeItemReferenceContainer container = tree;
		int columnCount = Math.max(tree.getColumnCount(), 1);
		while (true) {
			String nodeText = path.next();
			TreeItemReference itemRef;
			long startTime = System.currentTimeMillis();
			while (true) {
				try {
					itemRef = getNextTreeItem(container, columnCount, nodeText);
					break;
				}
				catch (WidgetNotFoundException e) {
					// Wait for up to 5 seconds for a dynamic tree to populate its elements
					// TODO make this configurable?
					if (System.currentTimeMillis() - startTime < 5000) {
						System.out.println("No tree items found for \'" + nodeText + "\' - wait then try again");
						try {
							Thread.sleep(100);
						}
						catch (InterruptedException ignored) {
							// Ignored
						}
						continue;
					}
					ScreenCapture.createScreenCapture();
					throw e;
				}
				catch (MultipleWidgetsFoundException e) {
					ScreenCapture.createScreenCapture();
					throw e;
				}
			}
			if (!path.hasNext())
				return itemRef;
			container = itemRef;
		}
	}

	/**
	 * Execute the specified operation then find a tree item with the matching node text.
	 * 
	 * @param container the reference to the container of the tree items to be searched
	 * @param columnCount the number of columns in the tree (1 or greater)
	 * @param nodeText the text to be matched
	 * @return the matching tree item (not <code>null</code>)
	 */
	private TreeItemReference getNextTreeItem(final TreeItemReferenceContainer container, final int columnCount, String nodeText)
		throws WidgetNotFoundException, MultipleWidgetsFoundException
	{
		container.expand();
		container.getDisplayRef().execute(new VoidCallable() {
			public void call() throws Exception {
				items = container.getItems();
				textForItems = new String[items.length][columnCount];
				for (int row = 0; row < items.length; row++) {
					TreeItemReference item = items[row];
					String[] rowText = textForItems[row];
					for (int column = 0; column < columnCount; column++)
						rowText[column] = item.getText(column);
				}
			}
		});

		// Look for an exact match

		TreeItemReference found = null;
		for (int row = 0; row < textForItems.length; row++) {
			String[] rowTexts = textForItems[row];
			for (String text : rowTexts) {
				if (nodeText.equals(text)) {
					if (found == null) {
						found = items[row];
					}
					else
						throw new MultipleWidgetsFoundException("Multiple tree items found for \'" + nodeText
							+ "\' in " + getAllItemText(textForItems));
				}
			}
		}
		if (found != null)
			return found;

		// Look for a pattern match

		for (int row = 0; row < textForItems.length; row++) {
			String[] rowTexts = textForItems[row];
			for (String text : rowTexts) {
				if (StringComparator.matches(StringUtils.trimMenuText(text), nodeText)) {
					if (found == null)
						found = items[row];
					else
						throw new MultipleWidgetsFoundException("Multiple tree items found for \'" + nodeText
							+ "\' in " + getAllItemText(textForItems));
				}
			}
		}
		if (found != null)
			return found;

		throw new WidgetNotFoundException("No tree items found for \'" + nodeText + "\' in "
			+ getAllItemText(textForItems));
	}

	/**
	 * Answer all text used in the text comparison as a single string.
	 * 
	 * @param op the operation containing the matching text
	 * @return a string for inclusion in a {@link WidgetNotFoundException} or
	 *         {@link MultipleWidgetsFoundException} message
	 */
	private StringBuilder getAllItemText(String[][] itemTexts) {
		StringBuilder allItemText = new StringBuilder(128);
		for (String[] rowTexts : itemTexts) {
			String separator = "\n";
			for (String text : rowTexts) {
				allItemText.append(separator);
				allItemText.append(text);
				separator = " | ";
			}
		}
		return allItemText;
	}
}
