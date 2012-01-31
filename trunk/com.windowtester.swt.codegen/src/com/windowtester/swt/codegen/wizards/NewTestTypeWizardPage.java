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
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Constants;

import com.windowtester.codegen.CodeGenPlugin;
import com.windowtester.codegen.CodeGeneratorFactory;
import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.ICodeGenPluginTraceOptions;
import com.windowtester.codegen.eventstream.EventStream;
import com.windowtester.codegen.generator.CodegenContributionManager;
import com.windowtester.codegen.generator.CodegenSettings;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;
import com.windowtester.codegen.util.BuildPathUtil;
import com.windowtester.codegen.util.BundleClassPathMananger;
import com.windowtester.codegen.util.IBuildPathUpdater;
import com.windowtester.codegen.util.ProjectUtil;
import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.debug.Tracer;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.swt.codegen.dialogs.SubclassSelectionDialog;

/**
 * copied from NewInterfaceWizardPage and modified
 */
public class NewTestTypeWizardPage extends NewTypeWizardPage {


    private static final String DIALOG_SETTING_ADD_DEPENDENCIES = "addDependenciesBoolean";
	private static final String DIALOG_SETTING_CREATE_CONTAINER = "createContainerBoolean";
	private static final String DIALOG_SETTING_LAST_TYPE = "last.type";
	private static final String DIALOG_SETTING_PROJECT = "project";
	private static final String DIALOG_SETTING_SUPERCLASS = "superclass";
	private static final String DIALOG_SETTING_SUPERCLASS_SWING = "superclassSwing";
	private static final String DIALOG_SETTING_SUPERCLASS_CHOICES = "superclassChoices";
	private static final String DIALOG_SETTING_SUPERCLASS_CHOICES_SWING = "superclassChoicesSwing";
	private static final String DIALOG_SETTING_WELCOME_PAGE_CLOSE = "closeWelcomePage";
	
	private final static String PAGE_NAME= "NewTestTypeWizardPage"; //$NON-NLS-1$
    private final static String PLUGIN_NATURE="org.eclipse.pde.PluginNature";
    
    private static final String SWT_BASECLASS = "com.windowtester.runtime.swt.UITestCaseSWT";
    private static final String SWING_BASECLASS = "com.windowtester.runtime.swing.UITestCaseSwing";
    
    /** check flag for adding build pass dependencies*/ 
    private boolean addDependencies = true;
    /** check flag for create container automatically */
    private boolean createContainers = true;
    /** the superclass for the test class */
    private String superclass ;
    /** the array of superclass choices */
    private String[] superclassChoices;
    /** check flag to add code to close welcome page at start of test */
    private boolean addWelcomePageClose = true;
    
    
    /** The list of events to use for codegen */
	private List events;
	/** An execution profile for this list of events */
	private ExecutionProfile profile;

	private String project;
	private String source;
	
	private Combo superClassCombo;
	private Button fBrowseButton;
	
	private Button dependenciesCheck;
	private Button createContainersCheck;
	private Button welcomePageCheck;
	
	private IDialogSettings section;

	private SetupHandlerSection setupSection;
	/** 
	 * The base class type for the test. Either UITestcaseSWT or UITestcaseSwing
	 */
	private IType baseClassType = null;
	/**
	 * set when the finish button is pressed
	 */
	private boolean finish = false;

    
	/**
     * Create a new <code>NewInterfaceWizardPage</code>
     * @param profile2 
     * @param _events2 
     */ 
    public NewTestTypeWizardPage(List events, ExecutionProfile profile) {
        super(false, PAGE_NAME);
        
        setTitle("Window Tester UI Test"); 
        setDescription("Create a new Window Tester UI test");
        
        this.events = events;
        this.profile = profile;
    }

    /**
     * The wizard owning this page is responsible for calling this method with the
     * current selection. The selection is used to initialize the fields of the wizard 
     * page.
     * 
     * @param selection used to initialize the fields
     */
    public void init(IStructuredSelection selection) {
    	// get precedence from the current selection
        IJavaElement jelem = getInitialJavaElement(selection);      
        initContainerPage(jelem);
        initTypePage(jelem);
        IStructuredSelection newSelection = initDialogSettings();
        // if current selection does not provide package fragment root 
        // get it from the DialogSettings if exists  
        if(getPackageFragmentRoot()==null){
        	if(newSelection!=null){
	        	jelem = getInitialJavaElement(newSelection);      
	            initContainerPage(jelem);
	            initTypePage(jelem);
        	}
        }
        
        doStatusUpdate();
    }       
    
    public void saveDialogSettings(){
    	section.put(DIALOG_SETTING_PROJECT, project);
    	section.put(DIALOG_SETTING_LAST_TYPE, getPackageText()+"."+getTypeName());
    	section.put(DIALOG_SETTING_CREATE_CONTAINER, createContainers);
    	section.put(DIALOG_SETTING_ADD_DEPENDENCIES, addDependencies);
    	if (isSwingApplication()){
    		section.put(DIALOG_SETTING_SUPERCLASS_SWING, superclass);
        	section.put(DIALOG_SETTING_SUPERCLASS_CHOICES_SWING, superclassChoices);
    	}
    	else {
    		section.put(DIALOG_SETTING_SUPERCLASS, superclass);
    		section.put(DIALOG_SETTING_SUPERCLASS_CHOICES, superclassChoices);
    	}
    	section.put(DIALOG_SETTING_WELCOME_PAGE_CLOSE, addWelcomePageClose);
    }
    
    private void doStatusUpdate() {
        // all used component status
        IStatus[] status= new IStatus[] {
            fContainerStatus,
            isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
            fTypeNameStatus,
            fModifierStatus,
            fSuperInterfacesStatus
        };
        
        // the mode severe status will be displayed and the ok button enabled/disabled.
        updateStatus(status);
    }

            
    /*
     * @see NewContainerWizardPage#handleFieldChanged
     */
    protected void handleFieldChanged(String fieldName) {
        super.handleFieldChanged(fieldName);
        
        doStatusUpdate();
    }
    
    
    // ------ ui --------
    
    /*
     * @see WizardPage#createControl
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        Composite composite= new Composite(parent, SWT.NONE);
  
        int nColumns= 4;
        
        GridLayout layout= new GridLayout();
        layout.numColumns= nColumns;        
        composite.setLayout(layout);
        
        createContainerControls(composite, nColumns);   
        createPackageControls(composite, nColumns); 
        //createEnclosingTypeControls(composite, nColumns);
                
        createSeparator(composite, nColumns);
        
        createTypeNameControls(composite, nColumns);
        createModifierControls(composite, nColumns);

        createSuperClassControls(composite, nColumns);
        createSetupTable(composite,nColumns);
        
        createSeparator(composite, nColumns);
        
        createAddContainerControl(composite, nColumns);
        createAddDependenciesControl(composite, nColumns);
       
        
        //createSuperInterfacesControls(composite, nColumns);
                        
        setControl(composite);
        
        Dialog.applyDialogFont(composite);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, "com.windowtester.eclipse.help.newTest_wizard");    

    }
    
    
    /**
	 * Creates the controls for the superclass name field. Expects a <code>GridLayout</code> 
	 * with at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createSuperClassControls(Composite composite, int nColumns) {
		Label label = new Label(composite,SWT.NONE);
		label.setText("Superclass:");
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 1;
		label.setLayoutData(gd);
		
		createSuperClassCombo(composite);
		
		fBrowseButton= new Button(composite, SWT.PUSH);
		fBrowseButton.setFont(composite.getFont());
		GridData gd_1= new GridData();
		gd_1.horizontalAlignment= GridData.FILL;
		gd_1.grabExcessHorizontalSpace= false;
		gd_1.horizontalSpan= 1;
		fBrowseButton.setLayoutData(gd_1);
		fBrowseButton.setText("Browse...");
		fBrowseButton.setEnabled(baseClassType!= null && !isSwingApplication());
		
		fBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String result = queryUserForSuperclass();
				if (result != null && result.length() > 0){
					superclass = result;
					ComboHelper.addIfNotPresent(superclass, superClassCombo);
					superClassCombo.setText(superclass);
					superclassChoices = superClassCombo.getItems();
				}
			}
		});	
		
	}

	private void createSuperClassCombo(Composite composite) {
		superClassCombo = new Combo(composite,SWT.READ_ONLY | SWT.DROP_DOWN);
		superClassCombo.setFont(composite.getFont());
		superClassCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,2,1));
		setSuperclassChoices();
		superClassCombo.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
					superclass = superClassCombo.getText();
			}
		});
		// 3/18/09 : Swing superclass not implemented for this release
		superClassCombo.setEnabled(!isSwingApplication());
	}

	private void setSuperclassChoices() {
		if (finish)
			return;
		if (isSwingApplication()){
			superclassChoices = new String[]{"com.windowtester.runtime.swing.UITestCaseSwing"};
			superclass = SWING_BASECLASS;
			// 3/18/09 : for this release, swing superclass not supported
			/*if (baseClassType != null & section != null){
				String className = section.get(DIALOG_SETTING_SUPERCLASS_SWING);
				if (className != null){
					IType type = getType(className);
					if (type != null){
						superclass = className;
						superclassChoices = section.getArray(DIALOG_SETTING_SUPERCLASS_CHOICES_SWING);
					}	
				}
			}	*/
				
		}
		else {	
				superclassChoices = new String[]{"com.windowtester.runtime.swt.UITestCaseSWT"};
				superclass = SWT_BASECLASS;
				if (baseClassType != null & section != null){
					String className = section.get(DIALOG_SETTING_SUPERCLASS);
					if (className != null){
						IType type = getType(className);
						if (type != null){
							superclass = className;
							superclassChoices = section.getArray(DIALOG_SETTING_SUPERCLASS_CHOICES);
						}	
					}
				}			
		}
		if (superClassCombo != null ){
			superClassCombo.setItems(superclassChoices);
			superClassCombo.setText(superclass);
		}
		
	}

	private IType getType(String className) {
		IJavaProject project = getJavaProject();
		IType type = null;
		try {
			type = project.findType(className);
		} catch (JavaModelException e) {
			
			e.printStackTrace();
		}
		return type;
	}

    
	
	private String queryUserForSuperclass(){		
		String selection = SubclassSelectionDialog.forType(baseClassType).inProject(getJavaProject()).getSelection();
		return selection;
	}
    
    private IStructuredSelection initDialogSettings() {

		// init Dialog Settings section
    	IDialogSettings settings = getDialogSettings();
    	String sessionProject = getSessionProjectName();
    	section = settings.getSection(sessionProject);
    	if (section==null)
    		section = settings.addNewSection(sessionProject);
    	// get project, source and package from settings
		String prj = section.get(DIALOG_SETTING_PROJECT);
		String type = section.get(DIALOG_SETTING_LAST_TYPE);

		return setSelection(prj, type);
	}

	private String getSessionProjectName() {
		ILaunch launch = profile.getLaunch();
		if (launch == null)
			return "*"; //a sentinel -- this will be used as a map lookup that will return no value
		return launch.getLaunchConfiguration().getName();
	}

	private IStructuredSelection setSelection(String project, String type){
		
		// nothing to select if the project null
		if(project==null)
			return null;
		// get the resource for selection
		IResource resource = getWorkspaceRoot().findMember("/"+project);
		if(resource==null||!(resource instanceof IProject)){
			return null;
		}
		try {
			if(type!=null){
				IJavaProject javaProject = JavaCore.create((IProject)resource);
				IType foundType = javaProject.findType(type);
				//in some cases this is null, in which case we bail
				if (foundType != null)
					return new StructuredSelection(foundType);
			}
		} catch (CoreException e) {
			// do nothing - project will be in selection.
		}
		return new StructuredSelection(resource);
	}

	private void createAddContainerControl(Composite composite, int columns){
    	createContainersCheck = new Button(composite, SWT.CHECK);
		createContainersCheck.setSelection(true);
		createContainersCheck.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
					createContainers = createContainersCheck.getSelection();
					IStatus status = containerChanged();
					updateStatus(status);
			}
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
		});
    	boolean c = section.get(DIALOG_SETTING_CREATE_CONTAINER)==null?true:section.getBoolean(DIALOG_SETTING_CREATE_CONTAINER);
    	createContainersCheck.setSelection(c);
    	createContainers = c;
    	
		Label label = new Label(composite, SWT.NONE);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = columns - 1;
		label.setLayoutData(gd);
		label.setText("Create folders automatically if they do not exist");
    }

	
	private void createSetupTable(Composite composite, int columns) {
		setupSection = SetupHandlerSection.forParent(composite).inContext(profile).build();
	}

	
	
    private void createAddDependenciesControl(Composite composite, int columns) {
		dependenciesCheck = new Button(composite, SWT.CHECK);
		dependenciesCheck.setSelection(true);
		dependenciesCheck.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				if(!dependenciesCheck.getSelection()){
					addDependencies = false;
					IPackageFragmentRoot root = getPackageFragmentRoot();
			    	if(root!=null){
			    		IStatus status = BuildPathUtil.validateDependencies(root.getJavaProject(), false, isRcpApplication());
			    		if(status.getSeverity()!=IStatus.OK){
			    			setMessage(status.getMessage()+" Please add required libraries manually.", WARNING);
			    		}else{
			    			setMessage(null, WARNING);
			    		}
			    	}
				}else{
					setMessage(null, WARNING);
					addDependencies = true;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
    	boolean c = section.get(DIALOG_SETTING_ADD_DEPENDENCIES)==null?true:section.getBoolean(DIALOG_SETTING_ADD_DEPENDENCIES);
    	dependenciesCheck.setSelection(c);
    	addDependencies = c;
		Label label = new Label(composite, SWT.NONE);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = columns - 1;
		label.setLayoutData(gd);
		label.setText("Add build path dependencies automatically");
	}
	
    
    
    /*
     * @see WizardPage#becomesVisible
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            setFocus();
        }
    }   

    
    public IJavaProject getJavaProject() {
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root != null) {
			return root.getJavaProject();
		}
		return null;
	}
    
    
	protected void createTypeMembers(IType newType, ImportsManager imports, IProgressMonitor monitor){
		try {
			super.createTypeMembers(newType, imports, monitor);
			
//!pq: moved to createTypeContents			
			
//			monitor.worked(1);
//			
//			// first add corresponding required jars if needed to RCP application test
//			if(isRcpApplication())
//				BuildPathUtil.validateDependencies(getPackageFragmentRoot().getJavaProject(), addDependencies, true);
//			else
//				BuildPathUtil.validateDependencies(getPackageFragmentRoot().getJavaProject(), profile.getProjectName(), addDependencies, false);
//						
//			monitor.worked(1);
//			
//			// then generate the sources
//			codeGen(_events, getPackageText(), getTypeName(), getModifiedResource().getRawLocation().toFile());
//			
//			monitor.worked(1);
//			
//			// resynch the class resource
//			getModifiedResource().refreshLocal(1, monitor);
//			
//			// update CompilationUnit content
//			newType.getCompilationUnit().discardWorkingCopy();
//			newType.getCompilationUnit().becomeWorkingCopy(null, monitor);

		} catch (CoreException e) {
			Logger.log(e);
		}
	}
	
	public boolean isRcpApplication(){
		if(profile.getExecType()==ExecutionProfile.RCP_EXEC_TYPE)
			return true;
		return false;
	}
	
	private boolean isSwingApplication(){
		if (profile.getExecType()== ExecutionProfile.SWING_EXEC_TYPE)
			return true;
		return false;
	}
	
    /**
     * @param events
     * @param packageName
     * @param typeName
     */
    private void codeGen(List events, String packageName, String typeName, File codegen) throws CoreException {
        try {
			String output = CodeGeneratorFactory.getGenerator(typeName, packageName, superclass, getSettings()).generate(new EventStream(events));
			Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "***codegen results:");
			Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, output);
			FileWriter fw = null;
			try {
			    fw = new FileWriter(codegen);
			    fw.write(output);
			} finally {
			    fw.close();
			}
		} catch (Throwable e) {
			IStatus status = new Status(IStatus.ERROR, CodeGenPlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
			throw new CoreException(status);
		}
    }


	private CodegenSettings getSettings() {
		return CodegenSettings.forPreferences().forProfile(profile).withHandlers(getSelectedHandlers());
	}


	private SetupHandlerSet getSelectedHandlers() {
		return setupSection.getSelectedHandlers();
	}

	public void createContanerResources(IProgressMonitor monitor) throws CoreException {	
		if(createContainers){
			if(isRcpApplication()){
				createPluginProject(project, source, monitor);
			}else{
				createJavaProject(project, source, monitor);
			}
		}
	}
	
	public void createPluginProject(String projectName, String sourcePath, IProgressMonitor monitor) throws CoreException{
		IProject projectResource = createJavaProject(projectName, sourcePath, monitor);
		if(projectResource.getNature(PLUGIN_NATURE)==null)
			ProjectUtil.addNature(projectResource, PLUGIN_NATURE, monitor);
		JavaCore.create(projectResource);
		IFile manifest = projectResource.getFile("META-INF/MANIFEST.MF");
		if(!manifest.exists())
			generateBundleManifest(manifest);
		IFile buildProperties = projectResource.getFile("build.properties");
		if (!buildProperties.exists())
			generateBuildProperties(buildProperties);
		monitor.worked(2);
		projectResource.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}
	
	private void generateBundleManifest(IFile manifest) throws CoreException {
		Properties prop = new Properties();
		prop.put(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
		prop.put(Constants.BUNDLE_NAME, "Test Bundle");
		prop.put(Constants.BUNDLE_SYMBOLICNAME, project);
		prop.put(Constants.BUNDLE_VERSION, "1.0.0");
		BuildPathUtil.writeManifest(manifest.getLocation().toFile(), prop);
	}

	private void generateBuildProperties(IFile buildProperties) throws CoreException {
		Properties prop = new Properties();
		prop.put("source..", "src/");
		prop.put("output..", "bin/");
		prop.put("bin.includes", "META-INF/,\\.");
		BuildPathUtil.writeBuildProperties(buildProperties.getLocation().toFile(), prop);
	}
	
	public IProject createJavaProject(String projectName, String sourcePath, IProgressMonitor monitor) throws CoreException{
		IProject project = getWorkspaceRoot().getProject(projectName);
		final IFolder sourceFolder;
		final IJavaProject javaProject;
		if (!project.exists()) {
			sourceFolder = project.getFolder(new Path(sourcePath));
			
			ProjectUtil.createProject(project, null, monitor);
			ProjectUtil.addNature(project, JavaCore.NATURE_ID, monitor);
			javaProject = JavaCore.create(project);
			ProjectUtil.addDefaultEntries(javaProject, sourceFolder, monitor);
		}else{
			javaProject = JavaCore.create(project);
			sourceFolder = project.getFolder(new Path(sourcePath));
			if(!sourceFolder.exists()){
				ProjectUtil.addDefaultEntries(javaProject, sourceFolder, monitor);
			}
		}
		
		Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable(){
			public void run() {
				setPackageFragmentRoot(javaProject.getPackageFragmentRoot(sourceFolder), true);
			}
		});
		return project;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#containerChanged()
	 */
	protected IStatus containerChanged() {
		IStatus superStatus = super.containerChanged();
		
		
		if(createContainers){
			
			StatusInfo status = new StatusInfo();
			String sourceFolder = getPackageFragmentRootText();
			if (sourceFolder.length() == 0) 
				return superStatus;
			
			int idx = sourceFolder.indexOf('/');
			
			if(idx > 0){
				project = sourceFolder.substring(0, idx);
				source = sourceFolder.substring(idx+1);
				
				if(isRcpApplication())
					status.setWarning("Plugin project '"+project+"' does not exist. It will be created automatically with '"+source+"' source folder.");
				else
					status.setWarning("Java project '"+project+"' does not exist. It will be created automatically with '"+source+"' source folder.");
			}else{
				project = sourceFolder;
				source = "/src";
				if(isRcpApplication())
					status.setWarning("Plugin project '"+project+"' does not exist. It will be created automatically with 'src' and 'bin' folders.");
				else
					status.setWarning("Java project '"+project+"' does not exist. It will be created automatically with 'src' and 'bin' folders.");
			}

			IProject projectResource = getWorkspaceRoot().getProject(project);
			if(projectResource.exists()){
				IJavaProject javaProject = JavaCore.create(projectResource);
				setBaseClassType(javaProject);
				if(projectResource.findMember(new Path(source))==null){
					if(isRcpApplication())
						status.setWarning("Plugin project '"+project+"' exists, but source folder does not exist. It will be created automatically.");
					else
						status.setWarning("Java project '"+project+"' exists, but source folder does not exist. It will be created automatically.");
				}else{
					setSuperclassChoices();
					setBrowseButtonStatus();
					return superStatus;
				}
				setSuperclassChoices();
				setBrowseButtonStatus();
			}
			
			return status;
		}
		
		return superStatus;
	}

	private void setBrowseButtonStatus() {
		if (fBrowseButton != null)
			fBrowseButton.setEnabled(baseClassType!=null & !isSwingApplication());
	}

	private void setBaseClassType(IJavaProject javaProject) {
		try {
			if (isSwingApplication())
					baseClassType  = javaProject.findType(SWING_BASECLASS);
			else
				baseClassType = javaProject.findType(SWT_BASECLASS);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#packageChanged()
	 */
	protected IStatus packageChanged() {
		IStatus status = super.packageChanged();
		if(createContainers){
			if(status.getSeverity()==IStatus.ERROR&&"".equals(status.getMessage())){
				StatusInfo newStatus = new StatusInfo();
				newStatus.setOK();
				return newStatus;
			}
		}
		return status;
	}

	
	
	public void addTypeContents(IProgressMonitor monitor) {
		try {

			monitor.worked(1);

			IJavaProject project = getPackageFragmentRoot().getJavaProject();
			// first add corresponding required jars if needed to RCP
			// application test
			if (isRcpApplication()) {
				BuildPathUtil.validateDependencies(project, addDependencies, true);
				if (addDependencies) {
					IBuildPathUpdater updater = BuildPathUtil.getUpdater(project);
					//add contributed deps
					CodegenContributionManager.addPluginDependencies(events, updater);
					//add resolved bundles
					BundleClassPathMananger.addPluginDependencies(events, updater);
				}
			}
			else
				BuildPathUtil.validateDependencies(project, profile.getProjectName(),
						addDependencies, false);

			monitor.worked(1);

			// then generate the sources
			codeGen(events, getPackageText(), getTypeName(),
					getModifiedResource().getRawLocation().toFile());

			monitor.worked(1);

			// resync the class resource
			getModifiedResource().refreshLocal(1, monitor);

			// update CompilationUnit content

			// newType.getCompilationUnit().discardWorkingCopy();
			// newType.getCompilationUnit().becomeWorkingCopy(null, monitor);

		} catch (Throwable e) {
			LogHandler.log(e);
		}
		
		persistWizardProperties();
		
	}

	private void persistWizardProperties() {
		setupSection.persistSelections();
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	
	
	

}
	
	
	
	

