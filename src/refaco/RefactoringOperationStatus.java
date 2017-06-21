package refaco;

/**
 * With this class we can know if a refactoring operation has been applied successfully or cancelled.
 */
public class RefactoringOperationStatus {
	
	private int code;			// termination status code
	private String message;		// error message
	
	/**
	 * Constructors
	 */
	public RefactoringOperationStatus(){
	}
	
	public RefactoringOperationStatus(int code, String message){
		this.code = code;
		this.message = message;
	}

	
	/**
	 * Getters & Setters
	 */
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
