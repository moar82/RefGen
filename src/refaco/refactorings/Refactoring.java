package refaco.refactorings;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;

import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;

/**
 * Abstract class for apply a refactoring 
 */
public abstract class Refactoring{
	
	private String projectName;					// name of the project where the refactoring is applied
	private RefactoringData refactoringData;		// refactoring info
	
	

	
	/**
	 * Constructor
	 * @param _refactoringData 
	 * @param _projectName
	 */
	public Refactoring(RefactoringData _refactoringData, String _projectName) {
		setProjectName(_projectName);
		setRefactoringData(_refactoringData);
	}

	/**
	 * Apply the refactoring operation
	 * @throws RefactoringException
	 */
	public abstract void apply() throws RefactoringException;

	/**
	 * Getters & Setters
	 */
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public RefactoringData getRefactoringData() {
		return refactoringData;
	}

	public void setRefactoringData(RefactoringData refactoringData) {
		this.refactoringData = refactoringData;
	}

}
