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
package com.windowtester.eclipse.ui.inspector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.recorder.event.user.IWidgetDescription;

/**
 * Properties Section.
 */
public class PropertiesSection extends AbstractSection {

	
	private static class LinkHandler extends HyperlinkAdapter {
		private final IWidgetDescription widget;

		public LinkHandler(IWidgetDescription widget) {
			this.widget = widget;
		}
		
		public void linkActivated(HyperlinkEvent e) {
			widget.getProperties().flag(PropertyMapping.fromString((String)e.getHref()));
		}
	}
	
	
	
	public void addTo(IWidgetDescription description, final ScrolledForm form, InspectorFormToolkit toolkit) {
		Section section = createSection(form, toolkit, Section.DESCRIPTION);
		section.setText(getExpandedTitle());
		
		addTableContent(description, form, toolkit, section);
		
//		addTextContent(description, form, toolkit, section);
	}

	
	private static class Flagger {
		
		private final PropertyMapping mapping;
		private final TableItem item;
		private final IWidgetDescription widget;
		private final Link link;

		public Flagger(TableItem item, Link link, IWidgetDescription widget, PropertyMapping mapping) {
			this.item = item;
			this.link = link;
			this.widget = widget;
			this.mapping = mapping;
		}
		
		void flag(boolean toFlag) {
			PropertySet propertySet = widget.getProperties();
			System.out.println("flag: " + toFlag);
			if (toFlag) {
				propertySet.flag(mapping);
			} else {
				propertySet.unflag(mapping);
			}	
			updateAssertTooltipData(link, !toFlag);
			updateAssertTooltipData(item, !toFlag);
		}
		
	}
	
	
	private static class LinkListener implements Listener, SelectionListener {

		private final TableItem item;
		private final Flagger flagger;
		
		
		public LinkListener(TableItem item, Link link, IWidgetDescription widget, PropertyMapping mapping) {
			this.item = item;
			this.flagger = new Flagger(item, link, widget, mapping);
		}
		
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event) {
			System.out.println("Selection: " + event.text);
			doFlag(!item.getChecked());
			toggleCheck();
		}


		private void toggleCheck() {
			item.setChecked(!item.getChecked());
			item.getParent().forceFocus();
		}


		private void doFlag(boolean toFlag) {
			flagger.flag(toFlag);
		}
		
		public void widgetDefaultSelected(SelectionEvent e) {
			System.out.println(".widgetDefaultSelected()");
			widgetSelected(e);
	
		}
		public void widgetSelected(SelectionEvent e) {
			if (e.item != item)
				return;
			System.out.println(".widgetSelected()");
			doFlag(item.getChecked());
		}
		
	}
	
	
	private void addTableContent(final IWidgetDescription widget, final ScrolledForm form, InspectorFormToolkit toolkit, final Section section) {
		final Table table = toolkit.createTable(section, SWT.CHECK);
		
		section.setClient(table);
		Object layoutData = setLayout(section);
		section.setLayoutData(layoutData);
		
		section.setDescription("Specify properties to assert.");
		
		
		//table.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		
		final ToolTipHandler tooltip = new ToolTipHandler(section.getShell());
		tooltip.activateHoverHelp(table);
		
		
		final PropertyMapping[] properties = widget.getProperties().toArray();
		for (int i = 0; i < properties.length; i++) {
			PropertyMapping property = properties[i];
			final TableItem item = new TableItem (table, SWT.CHECK);
			item.setImage(getIdeaIcon());
	
			updateAssertTooltipData(item, true);
			
			
			TableEditor editor = new TableEditor (table);
			final Link link = new Link (table, SWT.NONE);
			link.setLayoutData(layoutData);
			toolkit.adapt(link, true, true);
			String assertion = new StringBuffer().append(" <a href=").append(quoted(property.asString())).append(">Assert ").append(getPropertyDescription(property)).append("</a>").toString();
			
			link.setText(assertion);
			updateAssertTooltipData(link, true);
			tooltip.activateHoverHelp(link);
			link.pack ();
			LinkListener listener = new LinkListener(item, link, widget, property);

			link.addListener(SWT.Selection, listener);
			table.addSelectionListener(listener);
			
			
			item.setData(new Flagger(item, link, widget, property));	
			
			
//			if (i %2 != 0)
//				link.setBackground(toolkit.getColors().getColor(IFormColors.TB_BG));
			editor.minimumWidth = link.getSize ().x;
//			editor.minimumWidth = 500;
			
			editor.horizontalAlignment = SWT.LEFT;
			editor.setEditor (link, item, 0);	
			
			

		}
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				if (e.getState() == false) {
					section.setText("Properties (" + properties.length +") ...");
					section.layout();
				} else {
					section.setText(getExpandedTitle());
					section.layout();
				}
				table.layout();
				form.getShell().pack(true);
			}
		});
				
		MenuManager mm = new MenuManager();
		mm.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PropertiesSection.this.fillContextMenu(manager, table);
			}
		});
		mm.setRemoveAllWhenShown(true);
		Menu menu = mm.createContextMenu(table);
		table.setMenu(menu);
		section.setMenu(menu);
		//createContextMenu(section, table);
		
		
	}

	protected void fillContextMenu(IMenuManager manager, final Table table) {
		final boolean toSelect = !testForSelections(table);
		manager.add(new Action((toSelect ? "Select" : "Deselect") + " all") {
			public void run() {
				TableItem[] items = table.getItems();
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					Object data = item.getData();
					if (data instanceof Flagger) {
						((Flagger)data).flag(toSelect);
					}
					item.setChecked(toSelect);
				}
			}
		});
		
		
	}

	private void createContextMenu(final Section section, final Table table) {
		
		final boolean toSelect = !testForSelections(table);
		
		Menu menu = new Menu(section.getShell());
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText((toSelect ? "Select" : "Deselect") + " all");
		menuItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getItems();
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					Object data = item.getData();
					if (data instanceof Flagger) {
						((Flagger)data).flag(toSelect);
					}
					item.setChecked(toSelect);
				}
			}
		});
		
		
		table.setMenu(menu);
	}

	private boolean testForSelections(Table table) {
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			if (item.getChecked())
				return true;
		}
		return false;
	}

	private static void updateAssertTooltipData(Widget item, boolean doAssert) {
		String text = doAssert ? "Add assertion to recording" : "Remove assertion from recording";
		ToolTipHandler.setTextForItem(text, item);
		Image img = doAssert ? getAssertIcon() : getRemoveAssertIcon();
		ToolTipHandler.setImageForItem(img, item);
	}



	private String getExpandedTitle() {
		return "Properties";
	}
	
	private void addTextContent(IWidgetDescription description,
			final ScrolledForm form, InspectorFormToolkit toolkit,
			Section section) {
		final FormText text = createText(toolkit, section);
		section.setClient(text);
		
		Object layoutData = setLayout(section);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>"); //$NON-NLS-1$
		
		appendDetails(description, buffer);
		
		text.setImage("idea", getIdeaIcon()); //$NON-NLS-1$
		buffer.append("</form>"); //$NON-NLS-1$
		text.setText(buffer.toString(), true, false);
		
		text.setLayoutData(layoutData);
		
		text.addHyperlinkListener(new LinkHandler(description));
		text.layout();
		
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				text.layout();
				form.getShell().pack(true);
			}
		});
	}

	private void appendDetails(IWidgetDescription widget, StringBuffer buffer) {
		
		PropertyMapping[] properties = widget.getProperties().toArray();
		for (int i = 0; i < properties.length; i++) {
			PropertyMapping property = properties[i];
			buffer.append("<p>").append(property.getName()).append("</p>"); //$NON-NLS-1$			
			appendAssertionAction(buffer, property);
		}		
	}

	private void appendAssertionAction(StringBuffer buffer, PropertyMapping property) {
		buffer.append("<li bindent=\"3\" style=\"image\" value=\"idea\">"); //$NON-NLS-1$
		buffer.append("<a href=").append(quoted(property.asString())).append("> Add assertion: ").append(getPropertyDescription(property)).append("</a>");
		buffer.append("</li>"); //$NON-NLS-1$
	}

	private String getPropertyDescription(PropertyMapping property) {
		String value = property.getValue();
		String detail = "";
		if (property.isBoolean()) {
			if ("false".equals(value))
				detail = " (false)";
		} else {
			detail = " \"" + value +'"';
		}
		
		return property.getKey() + detail;
	}


	private static Image getIdeaIcon() {
		return ImageManager.getImage("idea.gif");
	}
	
	private static Image getAssertIcon() {
		return ImageManager.getImage("assertion.gif");
	}
	
	private static Image getRemoveAssertIcon() {
		return ImageManager.getImage("remove_assertion.gif");
	}
	
}
