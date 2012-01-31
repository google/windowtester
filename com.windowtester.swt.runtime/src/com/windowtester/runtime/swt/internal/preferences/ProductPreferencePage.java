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
package com.windowtester.runtime.swt.internal.preferences;

import java.net.URL;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.windowtester.internal.product.ISupport;
import com.windowtester.runtime.util.PluginUtilities;

/**
 * Main preference page for a product
 * <p>
 * 
 * @author Dan Rubel
 */
public abstract class ProductPreferencePage extends PreferencePage
	implements IWorkbenchPreferencePage
{
	/**
	 * The product associated with this preference page (not <code>null</code>)
	 */
	private final ISupport support;

	/**
	 * The path to the banner image relative to this product's primary plugin or
	 * <code>null</code> if no associated banner image.
	 */
	private final String bannerImagePath;

	/**
	 * The banner image associated with this preference page or <code>null</code> if no
	 * banner image is specified or the specified banner image has not yet been loaded.
	 */
	private Image bannerImage;

	/**
	 * Construct a new product preference page
	 * 
	 * @param support the product support information (not <code>null</code>)
	 * @param bannerImagePath The path to the banner image relative to this product's
	 *            primary plugin or <code>null</code> if no associated banner image.
	 */
	protected ProductPreferencePage(ISupport support, String bannerImagePath) {
		if (support == null)
			throw new IllegalArgumentException("support argument must not be null");
		this.support = support;
		this.bannerImagePath = bannerImagePath;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// UI Creation
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializes this preference page for the given workbench.
	 * <p>
	 * This method is called automatically as the preference page is being created and
	 * initialized. Clients must not call this method.
	 * </p>
	 * 
	 * @param workbench the workbench
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Creates and returns the SWT control for the customized body of this preference page
	 * under the given parent composite.
	 * 
	 * @param parent the parent composite
	 * @return the new control
	 */
	public Control createContents(Composite parent) {

		// create the outermost panel for the page
		Composite mainPanel = new Composite(parent, SWT.NONE);
		GridLayout mainLayout = new GridLayout();
		mainLayout.marginWidth = 0;
		mainLayout.marginHeight = 0;
		mainPanel.setLayout(mainLayout);

		// create plugin banner graphic
		Label bannerLabel = new Label(mainPanel, SWT.BORDER);
		Image image = getBannerImage();
		if (image != null) {
			bannerLabel.setImage(image);
			bannerLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER));
		}
		else {
			bannerLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
			bannerLabel.setVisible(false);
		}
		
		// create the plugin version description
		Label label = new Label(mainPanel, SWT.CENTER);
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
		label.setText(support.getPrefPageInfo());
		
		// create a license group
//		Group licenseGroup = new Group(mainPanel, SWT.NONE);
//		licenseGroup.setText("License Information");
//		GridData data = new GridData(GridData.FILL_HORIZONTAL);
//		licenseGroup.setLayoutData(data);
//		licenseGroup.setLayout(new GridLayout());
//		
//		label = new Label(licenseGroup, SWT.NONE);
//		label.setText(support.getLicenseInfoSummary());
//		
//		Button button = new Button(licenseGroup, SWT.PUSH);
//		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL));
//		button.setText("&Registration and Activation");
//		button.addSelectionListener(new SelectionListener() {
//			public void widgetSelected(SelectionEvent e) {
//				ActivationWizardDialog.openOneNow(getShell(), support);
//			}
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
		
		// create panel with company info on the left and buttons on the right
//		Group feedbackGroup = new Group(mainPanel, SWT.NONE);
//		feedbackGroup.setText("Feedback");
//		GridData data = new GridData(GridData.FILL_HORIZONTAL);
//		feedbackGroup.setLayoutData(data);
//		feedbackGroup.setLayout(new GridLayout(3, false));
		
		// create company information
//		label = new Label(feedbackGroup, 0);
//		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
//		label.setText(support.getPrefPageFeedback());
		
//		label = new Label(feedbackGroup, SWT.NONE);
//		label.setText(" ");
//		data = new GridData(GridData.VERTICAL_ALIGN_FILL);
//		data.widthHint = 40;
//		label.setLayoutData(data);
		
		// create email button panel
//		createEmailButtonPanel(feedbackGroup);
		
		label = new Label(mainPanel, SWT.CENTER);
		label.setText("Installed at " + support.getInstallationLocation());
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_END);
		/* $codepro.preprocessor.if version >= 3.1 $ */
		data.verticalIndent = 6;
		/* $codepro.preprocessor.endif $ */
		label.setLayoutData(data);
		
		noDefaultAndApplyButton();
		
		return mainPanel;
	}

//	protected Composite createEmailButtonPanel(Composite parent) {
//
//		// create the panel to hold the buttons
//		Composite buttonPanel = new Composite(parent, SWT.NONE);
//		GridLayout layout = new GridLayout();
//		layout.marginWidth = 0;
//		layout.marginHeight = 0;
//		layout.verticalSpacing = 1;
//		buttonPanel.setLayout(layout);
//		buttonPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END | GridData.HORIZONTAL_ALIGN_BEGINNING));
//		
//		if (!SWT.getPlatform().equals("carbon")) {// Can't send mail on Mac
//			try {
//				createButton(buttonPanel, new MailToInfo(support.getProduct()));
//				createButton(buttonPanel, new MailToSales(support.getProduct()));
//				createButton(buttonPanel, new MailToTechSupport(support));
//			}
//			catch (Throwable e) {
//				Logger.log(e);
//			}
//		}
//		
//		return buttonPanel;
//	}
	
//	protected Button createButton(Composite parent, final IAction action) {
//		Button button = new Button(parent, SWT.PUSH);
//		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
//		button.setText("E-Mail " + action.getText());
//		button.addSelectionListener(new SelectionListener() {
//			public void widgetSelected(SelectionEvent e) {
//				run();
//			}
//			public void widgetDefaultSelected(SelectionEvent e) {
//				run();
//			}
//			private void run() {
//				action.run();
//			}
//		});
//		return button;
//	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Answer the product's banner image, creating it if necessary. The image is cached,
	 * managed, and disposed by the receiver when the receiver is disposed.
	 * 
	 * @return the banner image or <code>null</code> if no banner image is defined for
	 *         this product
	 */
	protected Image getBannerImage() {
		if (bannerImage == null) {
			ImageDescriptor imageDescriptor = getBannerImageDescriptor();
			if (imageDescriptor == null)
				return null;
			bannerImage = imageDescriptor.createImage();
		}
		return bannerImage;
	}

	/**
	 * Answer the product's banner image descriptor.
	 * 
	 * @return the banner image descriptor or <code>null</code> if no banner image is
	 *         defined for this product
	 */
	protected ImageDescriptor getBannerImageDescriptor() {
		String pluginId = support.getProduct().getPluginId();
		String imagePath = getBannerImagePath();
		if (imagePath == null)
			return null;
		URL bannerImageUrl = PluginUtilities.getUrl(pluginId, imagePath);
		if (bannerImageUrl == null)
			return null;
		return ImageDescriptor.createFromURL(bannerImageUrl);
	}

	/**
	 * Answer the product's banner image path relative to this product's primary plugin.
	 * 
	 * @return the banner image relative path or <code>null</code> if no banner image is
	 *         defined for this product
	 */
	protected String getBannerImagePath() {
		return bannerImagePath;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Cleanup
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Clean up any OS resources managed by the receiver such as the banner image.
	 */
	public void dispose() {
		if (bannerImage != null) {
			bannerImage.dispose();
			bannerImage = null;
		}
		super.dispose();
	}
}
