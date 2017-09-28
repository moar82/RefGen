package refaco.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	/**
	 * Saves a .log file concatenating the name of the project
	 * from the workspace.
	 * @param fileName
	 * @param messageError
	 */
	public void saving(String fileName,String messageError) {
	BufferedWriter writer = null;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
	//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
     try {
         //create a temporary file
         File logFile = new File(getPath() + fileName+
        		 ".txt");
         writer = new BufferedWriter(new FileWriter(logFile,true));
         final String anError = "\n"+dateFormat.format(date)+":"+messageError;
		writer.write(anError);
         System.out.println(anError);
     } catch (Exception e) {
         e.printStackTrace();
     } finally {
         try {
             // Close the writer regardless of what happens...
             writer.close();
         } catch (Exception e) {
        	 e.printStackTrace();
         }
     }
	}
	@Override
	public void apply() throws RefactoringException {
		// TODO Auto-generated method stub
		
	}

}
