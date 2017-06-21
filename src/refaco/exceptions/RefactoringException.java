package refaco.exceptions;

public class RefactoringException extends Exception  {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefactoringException(String message){
		 super(message);
	 }
	 
	 public RefactoringException(){
		 super("Error Refactoring");
	 }


}