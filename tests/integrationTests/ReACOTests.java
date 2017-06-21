package integrationTests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import refaco.ProjectData;
import refaco.exceptions.ReACOExecutionException;
import refaco.handlers.CodeSmellHandler;


public class ReACOTests {
	
	
	@Test //CP14 
	public void prepareReACOResources(){
		CodeSmellHandler codeSmellHandler = new CodeSmellHandler();
		try {
			String project = "JChoco";
			String path =  "/Users/juan/workspace/TFM/ptidej-5/JChoco";
			ProjectData projectData = new ProjectData(project,path);
			codeSmellHandler.prepareReACOResources(projectData.getPath(), projectData.getPath() + "/src/");
			
			// Check the config file exist
			File file = new File(path + "/refaco/config.txt");
			assertNotNull(file);
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test //CP15
	public void testExecuteReACO(){
		CodeSmellHandler codeSmellHandler = new CodeSmellHandler();
		try {
			String project = "JChoco";
			String path =  "/Users/juan/workspace/TFM/ptidej-5/JChoco";
			ProjectData projectData = new ProjectData(project,path);
			codeSmellHandler.executeReACO(projectData, new NullProgressMonitor());
			
			// Check the results file exist
			File file = new File("/Users/juan/workspace/TFM/ptidej-5/JChoco/refaco/FitnessReport-0"+project+".txt");
			assertNotNull(file);
		} catch (ReACOExecutionException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test // CP16
	public void testReadResultsReACO(){
		CodeSmellHandler codeSmellHandler = new CodeSmellHandler();
		try {
			String project = "JChoco";
			String path =  "/Users/juan/workspace/TFM/ptidej-5/JChoco";
			ProjectData projectData = new ProjectData(project,path);
			codeSmellHandler.getResultsFromFile(projectData.getPath() + "/refaco/",projectData.getName());
			
			// Check the results are read and the fields aren't null  
			assertNotNull(codeSmellHandler.getCodeSmells());
			assertNotNull(codeSmellHandler.getRefactoringOpps());
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
	}


}
