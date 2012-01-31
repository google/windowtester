package abbot.swt.eclipse.fixture;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class JavaProjectCreator {
	
	public void createNewJavaProject(String projectName) {
		// the project to create
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		// the project description
		IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
		
		try{
			// create and open the project based on its description
			project.create(desc, null);
	        project.open(null);
			
			// set the nature to java
			String natures[] = new String[1];
			natures[0] = JavaCore.NATURE_ID;
			desc.setNatureIds(natures);
			project.setDescription(desc, new NullProgressMonitor());
			
			// create the classpath file
			IClasspathEntry srcEntry = JavaCore.newSourceEntry(new Path("/" + projectName + "/"));
			IClasspathEntry conEntry = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"));
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] newClasspath = new IClasspathEntry[2];
			newClasspath[0] = srcEntry;
			newClasspath[1] = conEntry;
			javaProject.setRawClasspath(newClasspath, new NullProgressMonitor());
	      } catch (CoreException e){
			  e.printStackTrace();
	      } catch( Exception exp ) {
			  exp.printStackTrace();
	      } 
	 } 
}
