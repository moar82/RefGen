package refaco.exceptions;

public class ProjectSelectionException extends Exception  {

	/**
	 */
	private static final long serialVersionUID = 1L;
	
	 public ProjectSelectionException(String message){
		 super(message);
	 }
	 
	 public ProjectSelectionException(){
		 super("Select a project and execute again");
	 }


}
