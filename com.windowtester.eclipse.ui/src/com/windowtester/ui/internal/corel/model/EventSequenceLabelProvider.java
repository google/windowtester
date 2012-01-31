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
package com.windowtester.ui.internal.corel.model;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.internal.runtime.ICodegenParticipant;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.user.SemanticDropEvent;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticShellClosingEvent;
import com.windowtester.recorder.event.user.SemanticShellDisposedEvent;
import com.windowtester.recorder.event.user.SemanticShellEvent;
import com.windowtester.recorder.event.user.SemanticShellShowingEvent;
import com.windowtester.recorder.event.user.SemanticTextEntryEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetClosedEvent;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.locator.IPathLocator;
import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.locator.JCheckBoxLocator;
import com.windowtester.runtime.swing.locator.JComboBoxLocator;
import com.windowtester.runtime.swing.locator.JListLocator;
import com.windowtester.runtime.swing.locator.JMenuItemLocator;
import com.windowtester.runtime.swing.locator.JRadioButtonLocator;
import com.windowtester.runtime.swing.locator.JTabbedPaneLocator;
import com.windowtester.runtime.swing.locator.JTableItemLocator;
import com.windowtester.runtime.swing.locator.JTextComponentLocator;
import com.windowtester.runtime.swing.locator.JToggleButtonLocator;
import com.windowtester.runtime.swing.locator.JTreeItemLocator;
import com.windowtester.runtime.swt.locator.jface.WizardPageLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CComboItemLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.ComboItemLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.LabeledLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.ListItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TabItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.ui.core.model.IEvent;
import com.windowtester.ui.core.model.IEventGroup;

/**
 * A label provider for recorded events.
 *
 */
public class EventSequenceLabelProvider extends LabelProvider {

	static abstract class Type {
		abstract String getIntro();
		abstract String getDescription(IUISemanticEvent event);

		public static Type forEvent(IUISemanticEvent event) {
			if (event instanceof SemanticWidgetInspectionEvent)
				return ASSERT;
			if (event instanceof SemanticDropEvent)
				return DRAG;
			if (event instanceof SemanticWidgetClosedEvent)
				return CLOSED;
			return CLICK;
		}	
	}
	
	private static final Type DRAG  = new Type() {
		public String getIntro() {
			return "Dragged to";
		}
		public String getDescription(IUISemanticEvent event) {
			return null;
		}
	};
	
	private static final Type CLICK  = new Type() {
		public String getIntro() {
			return null;
		}
		public String getDescription(IUISemanticEvent event) {
			return "clicked";
		}
	};
	
	private static final Type CLOSED  = new Type() {
		public String getIntro() {
			return null;
		}
		public String getDescription(IUISemanticEvent event) {
			return "closed";
		}
	};
	
	private static final Type ASSERT = new Type(){
		public String getIntro() {
			return "Asserted";
		}
		public String getDescription(IUISemanticEvent event) {
			if (!(event instanceof SemanticWidgetInspectionEvent))
				return ""; //shouldn't happen...
			SemanticWidgetInspectionEvent assertion = (SemanticWidgetInspectionEvent)event;
			PropertyMapping[] flagged = assertion.getProperties().flagged().toArray();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < flagged.length; i++) {
				sb.append(flagged[i].asString());
				if (i+1 < flagged.length)
					sb.append(", ");
			}
			return sb.toString();
		}
	};
	
	private static final String ICON_ROOT = "icons/full/obj16/";
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object e) {
		
		ILabelProvider labelProvider = adaptToLabelProvider(e);
		if (labelProvider != null)
			return labelProvider.getText(e);
		
		if (e instanceof IEventGroup)
			return ((IEventGroup)e).getName();
		if (e instanceof IEvent)
			return getEventText((IEvent)e);
		
		return super.getText(e);
	}

	private ILabelProvider adaptToLabelProvider(Object e) {
		if (e instanceof IAdaptable) {
			return (ILabelProvider) ((IAdaptable)e).getAdapter(ILabelProvider.class);
		}
		return null;
	}

	private ICodegenParticipant adaptToCodegenParticipant(Object o) {
		if (o instanceof IAdaptable) {
			return (ICodegenParticipant) ((IAdaptable)o).getAdapter(ICodegenParticipant.class);
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object e) {
		
		ILabelProvider labelProvider = adaptToLabelProvider(e);
		if (labelProvider != null) {
			Image image = labelProvider.getImage(e);
			if (image != null)
				return image;
		}
		
		if (e instanceof IEvent) {
			return getEventImage((IEvent)e);
		}
		if (e instanceof IEventGroup) {
			return getPluginImage("eventGroup.gif");
		}
		if (e instanceof IWidgetIdentifier) {
			return getPluginImage("class_hi.gif");
		}
		return super.getImage(e);
	}
	
	
	private String getEventText(IEvent e) {
		
		ISemanticEvent event = e.getUIEvent();
		if (event instanceof RecorderAssertionHookAddedEvent)
			return asserted(((RecorderAssertionHookAddedEvent)event).getHookName());
		/*
		 * sanity: want to filter out all non- IUISemantic events
		 */
		if (!(event instanceof IUISemanticEvent))
			return "<null>";
		
		IUISemanticEvent uiEvent = (IUISemanticEvent)event;
		
		if (uiEvent instanceof SemanticKeyDownEvent)
			return toString(((SemanticKeyDownEvent)uiEvent)) + " pressed";
		if (uiEvent instanceof SemanticTextEntryEvent) {
			return toString(((SemanticTextEntryEvent)uiEvent)) + " entered";
		}
		if (uiEvent instanceof SemanticShellEvent)
			return uiEvent.toString();
		if (uiEvent.isContext())
			return contextClicked(uiEvent);
		
		return clicked(uiEvent);
	}

	private String toString(SemanticTextEntryEvent textEvent) {
		return EnteredKeyLabelProvider.getLabel(textEvent);
	}

	private String toString(SemanticKeyDownEvent keyEvent) {
		return EnteredKeyLabelProvider.getLabel(keyEvent);
		
	}

	private String clicked(IUISemanticEvent uiEvent) {
		
		Type type = Type.forEvent(uiEvent);
		String description = type.getDescription(uiEvent);
		
		Object info = uiEvent.getHierarchyInfo();
		
		ILabelProvider labelProvider = adaptToLabelProvider(info);
		if (labelProvider != null)
			return toString("", type, info, description);
		
		ICodegenParticipant cp = adaptToCodegenParticipant(info);
		if (cp != null) {
			if (description == null)
				description = "";
			return toString(cp, type, info, description);
		}
		if (info instanceof IdentifierAdapter)
			info = ((IdentifierAdapter)info).getLocator();
		
		if (info instanceof ButtonLocator || isJButton(info)) 
			return toString("Button: ", type, info, description);
		if (info instanceof ComboItemLocator || info instanceof JComboBoxLocator) 
			return toString("Combo Item: ", type, info, description);
		if (info instanceof CComboItemLocator) 
			return toString("CCombo Item: ", type, info, description);
		if (info instanceof CTabItemLocator)
			return toString("CTabItem: ", type, info, description);
		if (info instanceof TabItemLocator || info instanceof JTabbedPaneLocator)
			return toString("TabItem: ", type, info, description);
		if (info instanceof ListItemLocator || info instanceof JListLocator)
			return toString("List Item: ", type, info, description);
		if (info instanceof TreeItemLocator || info instanceof JTreeItemLocator)
			return toString("Tree Item: ", type, info, description);
		if (info instanceof FilteredTreeItemLocator)
			return toString("Tree Item: ", type, info, description);
		if (info instanceof MenuItemLocator || info instanceof JMenuItemLocator)
			return toString("Menu Item: ", type, info, description);
		if (info instanceof PullDownMenuItemLocator)
			return toString("Pull Down Menu Item: ", type, info, description);
		if (info instanceof TableItemLocator || info instanceof JTableItemLocator)
			return toString("Table Item: ", type, info, description);
		if (info instanceof ContributedToolItemLocator)
			return toString("Tool Item: ", type, info, description);
		if (info instanceof WizardPageLocator)
			return toString("Wizard Page:", type, info, description);
		if (info instanceof LabeledLocator) {
			String intro = type.getIntro();
			intro = intro == null ? "" : intro + " ";
			if (isType(((LabeledLocator)info).getTargetClass(), Text.class))
				return intro + "Text Labeled: " + getLocatorText(info) + " " + description;
			if (isType(((LabeledLocator)info).getTargetClass(), StyledText.class))
				return intro + "Text Labeled: " + getLocatorText(info) +  " " + description;
			return intro + "Labeled Widget: " + getLocatorText(info) +  " " + description;
		}
		if (info instanceof ViewLocator) {
			return toString("View:", type, info, description);
		}
		if (info instanceof EditorLocator) {
			return toString("Editor:", type, info, description);
		}
		if (info instanceof PerspectiveLocator) {
			return toString("Perspective:", type, info, description);
		}
		if (info instanceof LabeledTextLocator)
			return "Text Labeled: " + getLocatorText(info) +  " " + description;
		if (info instanceof JTextComponentLocator)
			return toString("Text : ",type,info,description);
		return defaultLocatorText(info);
	}



	private String toString(ICodegenParticipant cp, Type type, Object info, String description) {
		String intro = type.getIntro();
		if (intro == null)
			intro = "";
		else 
			intro += " ";
		if (description == null)
			description = "";
		else 
			description = " " + description;
		return intro + LabelTranslator.fromCodeString(cp) + description;
	}

	private String contextClicked(IUISemanticEvent event) {
		
		IWidgetIdentifier locator = event.getHierarchyInfo();
		
		if (event instanceof SemanticTreeItemSelectionEvent) {
			return treeContextClick((SemanticTreeItemSelectionEvent)event);
		}
		
		
		if (!(event instanceof SemanticMenuSelectionEvent)) //shouldn't happen
			return defaultContextClick(locator);
		
		SemanticMenuSelectionEvent menuSelect = (SemanticMenuSelectionEvent)event;
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("Context Menu Item '").append(menuSelect.getPathString());
		sb.append("' clicked in ").append(getLocatorText(locator));
		return sb.toString();
	}

	private String defaultContextClick(IWidgetIdentifier locator) {
		return "Context Menu Item '" + getLocatorText(locator) + "' clicked";
	}

	private String treeContextClick(SemanticTreeItemSelectionEvent event) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("Context Menu Item '").append(event.getContextMenuSelectionPath());
		sb.append("' clicked in Tree Item '").append(event.getPathString()).append("'");
		
		IWidgetIdentifier loc = event.getHierarchyInfo();
		if (loc instanceof WidgetLocator) {
			WidgetLocator parent = ((WidgetLocator)loc).getParentInfo();
			if (parent != null) {
				if (parent instanceof ViewLocator)
					sb.append(" in view '").append(((ViewLocator)parent).getViewId()).append("'");
			}
		}
		return sb.toString();
	}

	private String asserted(String hookName) {
		return hookName;
	}

	private String defaultLocatorText(Object info) {
		String detail = info == null ? "<unknown>" : info.toString();
		return "Widget (" + detail + ") clicked";
	}

//	private String clicked(String label, IWidgetIdentifier info) {
//		return toString(label, info, "clicked");
//	}
	
//	private String toString(String label, IWidgetIdentifier info, String description) {
//		return label + ": " + getLocatorText(info) + " " + description;
//	}
	
	private String toString(String label, Type type, Object info, String description) {
		String intro = type.getIntro();
		if (intro != null)
			label = intro + " " + label;
		if (description == null)
			description = "";
		else 
			description = " " + description;
		return label + getLocatorText(info) + description;
	}
	
	
	private String getLocatorText(Object info) {
		
		ILabelProvider labelProvider = adaptToLabelProvider(info);
		if (labelProvider != null)
			return labelProvider.getText(info);
		
		if (info instanceof PerspectiveLocator) {
			return "'" + ((PerspectiveLocator)info).getPerspectiveId() + "'";
		}
		if (!(info instanceof WidgetLocator)) {
			return "";
		}
		WidgetLocator locator  = (WidgetLocator)info;
		String detail = null;
		if (locator instanceof IPathLocator)
			detail = ((IPathLocator)locator).getPath();
		else
			detail = locator.getNameOrLabel();
		// special case JTableItemLocator
		if (locator instanceof JTableItemLocator){
			JTableItemLocator tableLocator = (JTableItemLocator)locator;
			detail = "row " + tableLocator.getRow() +", column " + tableLocator.getColumn();
		} else {
			detail = "'" + detail + "'";
		}
			
		//detail = "'" + detail + "'";
		
		WidgetLocator parentInfo = locator.getParentInfo();
		if (parentInfo != null) {
			if (parentInfo instanceof ViewLocator)
				detail += " in view '" + ((ViewLocator)parentInfo).getViewId() + "'";
		}
			
		return detail;
	}

	private Image getEventImage(IEvent e) {
		ISemanticEvent event = e.getUIEvent();
		if (event instanceof RecorderAssertionHookAddedEvent)
			return widget("assertion");
		if (event instanceof SemanticWidgetInspectionEvent)
			return widget("assertion");
		if (event instanceof SemanticDropEvent)
			return widget("dragTo");
		/*
		 * sanity: want to filter out all non- IUISemantic events
		 */
		if (!(event instanceof IUISemanticEvent))
			return null;
		
		IUISemanticEvent uiEvent = (IUISemanticEvent)event;
		
		if (uiEvent instanceof SemanticKeyDownEvent)
			return widget("key");
		if (uiEvent instanceof SemanticTextEntryEvent)
			return widget("key");
		if (uiEvent instanceof SemanticShellShowingEvent)
			return widget("shell_in");
		if (uiEvent instanceof SemanticShellDisposedEvent)
			return widget("shell_out");
		if (uiEvent instanceof SemanticShellClosingEvent)
			return widget("shell_out");
		if (uiEvent.isContext())
			return widget("popup_menu");
		
	
		IWidgetIdentifier info = uiEvent.getHierarchyInfo();
		
		ILabelProvider labelProvider = adaptToLabelProvider(info);
		if (labelProvider != null) {
			Image image = labelProvider.getImage(info);
			if (image != null)
				return image;
		}
		
		if (info instanceof ButtonLocator || isJButton(info)) 
			return widget("button");
		if (info instanceof ComboItemLocator || info instanceof JComboBoxLocator) 
			return widget("combo");
		if (info instanceof CComboItemLocator) 
			return widget("combo");
		if (info instanceof CTabItemLocator)
			return widget("tab_item");
		if (info instanceof TabItemLocator || info instanceof JTabbedPaneLocator)
			return widget("tab_item");
		if (info instanceof ListItemLocator || info instanceof JListLocator)
			return widget("list");
		if (info instanceof TreeItemLocator || info instanceof JTreeItemLocator)
			return widget("tree");
		if (info instanceof FilteredTreeItemLocator)
			return widget("tree");
		if (info instanceof MenuItemLocator || info instanceof JMenuItemLocator)
			return widget("menu");
		if (info instanceof PullDownMenuItemLocator)
			return widget("menu");
		if (info instanceof TableItemLocator || info instanceof JTableItemLocator)
			return widget("table");
		if (info instanceof ContributedToolItemLocator)
			return widget("tool_item");
		if (info instanceof LabeledLocator) {
			if (isType(info.getTargetClass(), Text.class))
				return widget("text");
			if (isType(info.getTargetClass(), StyledText.class))
				return widget("text");
			return widget("label");
		}
		if (info instanceof LabeledTextLocator || info instanceof JTextComponentLocator)
			return widget("text");
		
		
		//fall through
		return defaultEventImage();
	}



	private boolean isJButton(Object info){
		if (info instanceof JButtonLocator || info instanceof JRadioButtonLocator 
				|| info instanceof JToggleButtonLocator || info instanceof JCheckBoxLocator)
		return true;
		return false;
	}
	
	
	private boolean isType(Class targetClass, Class type) {
		if (targetClass == null || type == null)
			return false;
		return type.isAssignableFrom(targetClass);
	}

	private Image widget(String name) {
		return getPluginImage("widgets/" + name + ".gif");
	}

	private Image defaultEventImage() {
		return widget("default_widget");
//		return getPluginImage("event.gif");
	}

	private Image getPluginImage(String path) {
		if (!Platform.isRunning())
			return null;
		return UiPlugin.getDefault().getImage(ICON_ROOT + path);
	}
	
}
