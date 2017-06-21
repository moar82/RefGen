package unitTests;

import static org.junit.Assert.*;
import org.junit.Test;

import refaco.RefactoringOperationStatus;

public class RefactoringOperationStatusTests {

	@Test
	public void testConstructorEmpty(){
		RefactoringOperationStatus operationStatus = new RefactoringOperationStatus();
		assertEquals(0,operationStatus.getCode());
		assertNull(operationStatus.getMessage());
	}
	
	@Test
	public void testConstructor2(){
		String message = "fooMessage";
		int code = 1;
		RefactoringOperationStatus operationStatus = new RefactoringOperationStatus(code,message);
		assertEquals(code,operationStatus.getCode());
		assertEquals(message,operationStatus.getMessage());
	}
	
	@Test
	public void testFieldCode(){
		RefactoringOperationStatus operationStatus = new RefactoringOperationStatus();
		int code = 2;
		operationStatus.setCode(code);
		assertEquals(code,operationStatus.getCode());
	}
	
	@Test
	public void testFieldMessage(){
		RefactoringOperationStatus operationStatus = new RefactoringOperationStatus();
		String message = "fooMessage";
		operationStatus.setMessage(message);
		assertEquals(message,operationStatus.getMessage());
	}
	
	
	
	
	
}
