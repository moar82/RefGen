package refaco.refactorings;

import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;

import static org.mockito.Mockito.*;

import java.util.List;


//import static org.testng.Assert.*;

public class MockExtractClass  {
	ExtractClassRefactoring extract;
	String projectName = "/ganttproject";
	RefactoringData test = new RefactoringData();
	RefactoringData data;
	public static void main()
	{
		MockExtractClass tester = new MockExtractClass();
		tester.setTest();
		 System.out.println(tester.testExctractClass()?"pass":"fail");

		
	}
	public void setTest(){
		test.setClassSource( "/ganttproject/src/net/sourceforge/ganttproject/task/algorithm/RecalculateTaskScheduleAlgorithm.java");
		extract = new ExtractClassRefactoring(test, projectName);
		data = mock(RefactoringData.class);
		extract.setRefactoringData(data);
	}
	 public boolean testExctractClass(){
  	   
		 try {
			extract.apply();
			return true;
		} catch (RefactoringException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}
		 
	   }
}
