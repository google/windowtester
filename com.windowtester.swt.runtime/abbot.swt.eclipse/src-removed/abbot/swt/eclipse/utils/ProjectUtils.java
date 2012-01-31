package abbot.swt.eclipse.utils;

import junit.framework.Assert;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import abbot.Log;

/**
 * @author tlroche
 * @version $Id: ProjectUtils.java,v 1.1 2005-12-19 20:28:33 pq Exp $
 */
public class ProjectUtils {
	public static final String copyright = "Licensed Materials -- Property of IBM\n(c) Copyright International Business Machines Corporation, 2000,2003\nUS Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	/**
	 * No instances  
	 */
	private ProjectUtils() {
	}

	/**
	 * Delete all projects from the current workspace.
	 * @throws CoreException
	 */
	public static void deleteAllProjects() throws CoreException {
		IProject[] projects = getAllProjects();
		for (int i = 0; i < projects.length; i++){
			projects[i].delete(true, true, null);
		}
		verifyNoProjects();
	}

	public static IProject[] getAllProjects() {
		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}

    public static boolean isEmptyWorkspace() {
		IProject[] projects = getAllProjects();
		return Utils.isEmpty(projects);
	}

    public static void verifyNoProjects() {
		IProject[] projects = getAllProjects();
		if (!Utils.isEmpty(projects)) {
			StringBuffer pns = new StringBuffer(); // project names
			for (int i = 0; i < projects.length; i++) {
				pns.append(" ").append(projects[i].getName());
			}
			Log.assertTrue(
				"ERROR: projects not deleted={" + pns.toString() + "}",
				projects.length == 0	
			);
		}
	}
    
    /* returns the project having the given name */
    public static IProject getProject(String projname) {
    	if (projname==null) return null;
//    	IProject [] projects = getAllProjects();
//    	for (int i=0;i<projects.length;i++) {
//    		if (projname.equals(projects[i].getName())) {
//    			return projects[i];
//    		}
//    	}
//    	return null;
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projname);

    }

	public static void deleteProject(String projectName) {
		Workspace workspace = (Workspace) ResourcesPlugin.getWorkspace();
		IResource oldWebProj = workspace.getRoot().getProject(projectName);
		try {
			workspace.delete(new IResource[] { oldWebProj }, true, null);
		} catch (CoreException ce) {
			ce.printStackTrace();
		}
	}

	public static void projectCreationTest(String projectName) {
		IProject project = getProject(projectName);
		Assert.assertTrue(project.exists());
		Assert.assertTrue(project.getFile(".classpath").exists());  //$NON-NLS-1$
	}

	public static void fileCreationTest(String projectName, String filePath) {
		IProject project = getProject(projectName);
		IFile file = project.getFile(filePath);
		Assert.assertTrue(file.exists());
	}
    
}
