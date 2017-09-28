package refaco.refactorings;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.Signature;

import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;

/**
 * Abstract class for apply a refactoring 
 */
public abstract class Refactoring{
	
	private String projectName;					// name of the project where the refactoring is applied
	private RefactoringData refactoringData;		// refactoring info
	
	private static String path;

	
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

	/**
	 * @param parameters a String array that contains the parameters of a method
	 * @return a String array without the qualified name
	 */
	protected String[] cleanQualifiedName(String[] parameters) {
		String[] cleanArray = new String[parameters.length];
		for (int j =0; j<cleanArray.length;j++){
			//replace $ by .
			String paramWithoutStringSign = new String(parameters[j].replace("$", ".").trim()); 
			if (paramWithoutStringSign.indexOf(".")!=-1){
				cleanArray[j] = new String(paramWithoutStringSign.substring(paramWithoutStringSign.lastIndexOf(".")+1));
			}
			else
				cleanArray[j] = new String(paramWithoutStringSign);
		}
		return cleanArray;
	}

	public static String getPath() {
		return path;
	}

	/**
	 * It is the path where the log will be saved, that is
	 * the eclipse workspace folder of the program analyzed
	 * @param path
	 */
	public static void setPath(String path) {
		Refactoring.path = path;
	}
	
	public static String[] getParameterTypesOfActualMethod(
	        IMethod actualMethod) {
	    String[] actualMethodParamTypes = actualMethod.getParameterTypes();
	    String[] readableActualMethodParamTypes = new String[actualMethodParamTypes.length];
	    for (int idy = 0; idy < actualMethodParamTypes.length; idy++) {
	        // convert parameter types into readable names
	        readableActualMethodParamTypes[idy] = Signature
	                .toString(actualMethodParamTypes[idy]);
	    }
	    return readableActualMethodParamTypes;
	}

}
