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
package com.windowtester.swt.codegen.wizards;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;

import com.windowtester.codegen.CodeGenPlugin;
import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.internal.debug.Logger;

@SuppressWarnings("restriction")
public class NewTestTypeWizard extends NewElementWizard {

    /** The test type wizard page */
    private NewTestTypeWizardPage _wizardPage;
    /** The list of events to use for codegen */
	private List _events;
	/** An execution profile for this list of events */
	private ExecutionProfile profile;

    public NewTestTypeWizard(List events, ExecutionProfile profile) {
        this();
        this._events = events;
        this.profile = profile;
    }

    public NewTestTypeWizard() {
        super();
        setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
        setDialogSettings(CodeGenPlugin.getDefault().getDialogSettings());
        setWindowTitle("New UI Test");
    }
    
    /*
     * @see Wizard#addPages
     */ 
    public void addPages() {
        super.addPages();       
        _wizardPage= new NewTestTypeWizardPage(_events, profile);
        addPage(_wizardPage);
        _wizardPage.init(getSelection()); 
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#canRunForked()
     */
    protected boolean canRunForked() {
        return !_wizardPage.isEnclosingTypeSelected();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
    	try {
			// analyse the container resources for existence 
			// and create them if they do not yet exists
			_wizardPage.createContanerResources(monitor);
			
			//calling create here is wrongly removing our imports (because it thinks they are unused)
			// create the type, use the full progress monitor
			_wizardPage.createType(monitor); //<--- renabled... calls createTypeMembers which is required
			
			//now that the type is created, add our contents to it...
			_wizardPage.addTypeContents(monitor);
			
			// if all went OK save dialog settings
			_wizardPage.saveDialogSettings();
		} catch (Throwable e) {
			Logger.log(e);
		}
    }
        
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    public boolean performFinish() {
    	_wizardPage.setFinish(true);
        warnAboutTypeCommentDeprecation();
        boolean res= super.performFinish();
        if (res) {
            IResource resource= _wizardPage.getModifiedResource();
            updateClasspath(resource);
            if (resource != null) {
                selectAndReveal(resource);
                openResource((IFile) resource);
            }   
        }
        return res;
    }
    
    private void updateClasspath(IResource resource) {
    	try {
    		if(_wizardPage.isRcpApplication()){
    			IPath requiredPluginsPath = new Path("org.eclipse.pde.core.requiredPlugins");
    			IJavaProject javaProject = JavaCore.create(resource.getProject());
    			IClasspathEntry[] entries = javaProject.getRawClasspath();
    			for (int i = 0; i < entries.length; i++) {
					IClasspathEntry entry = entries[i];
					if(entry.getEntryKind()==IClasspathEntry.CPE_CONTAINER){
						if(entry.getPath().equals(requiredPluginsPath)){
							return; // no need to process
						}
					}
				}
    			IClasspathEntry requiredPlugins = JavaCore.newContainerEntry(requiredPluginsPath);
    			IClasspathEntry[] newEntries = new IClasspathEntry[entries.length+1];
    			System.arraycopy(entries, 0, newEntries, 0, entries.length);
    			newEntries[entries.length] = requiredPlugins;
    			javaProject.setRawClasspath(newEntries, null);
    		}
		} catch (CoreException e) {
			Logger.log(e);
		}
	}

	/**
     * @return the package for the new type
     */
    public String getPackageName() {
        return _wizardPage.getPackageText();
    }
    
    /**
     * @return the name of the new type
     */
    public String getTypeName() {
        return _wizardPage.getTypeName();
    }
    
    /**
     * @return the file containining the generated type
     */
    public File getOutputFile() {
        IResource resource = _wizardPage.getModifiedResource();
        return resource.getRawLocation().toFile();
    }
    
    /**
     * Get the project corresponding to selected source folder
     * 
     * @return Java project
     */
    public IJavaProject getProject(){
    	IPackageFragmentRoot root = _wizardPage.getPackageFragmentRoot();
    	if(root!=null)
    		return root.getJavaProject();
    	return null;
    }
}
