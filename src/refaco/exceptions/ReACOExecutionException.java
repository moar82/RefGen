package refaco.exceptions;

public class ReACOExecutionException extends Exception  {

	/**
	 */
	private static final long serialVersionUID = 1L;
	
	 public ReACOExecutionException(String message){
		 super(message);
	 }
	 
	 public ReACOExecutionException(){
		 super("Exception at execute refactoring jar file");
	 }


}