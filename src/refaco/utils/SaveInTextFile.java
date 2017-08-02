package refaco.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;

public class SaveInTextFile extends refaco.refactorings.Refactoring {
	public SaveInTextFile(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
		// TODO Auto-generated constructor stub
	}
	public void saving(String fileName,String messageError) {
	// Get the IProject from the projectName
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	IWorkspaceRoot root = workspace.getRoot();
	IProject project = root.getProject(getProjectName());

	 BufferedWriter writer = null;
     try {
         //create a temporary file
         File logFile = new File(project.getLocation() + fileName);

         writer = new BufferedWriter(new FileWriter(logFile));
         writer.write(messageError);
     } catch (Exception e) {
         e.printStackTrace();
     } finally {
         try {
             // Close the writer regardless of what happens...
             writer.close();
         } catch (Exception e) {
         }
     }
	}
	@Override
	public void apply() throws RefactoringException {
		// TODO Auto-generated method stub
		
	}

}
