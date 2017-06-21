package unitTests;

import static org.junit.Assert.*;
import org.junit.Test;

import refaco.RefactoringData;
import refaco.RefactoringType;
import refaco.RowData;

public class RowDataTests {
	
	@Test
	public void testConstructor(){
		String[] row = new String[]{"0","fooRefactoringType","fooClassSource","fooClassTarget", "fooMethod"};
		RowData rowData = new RowData(row);
		assertNotNull(rowData);
		for(int i=0; i < row.length; i++){
			assertEquals(row[i], rowData.getColumn(i));
		}
	}
	
	@Test
	public void testToString(){
		String[] row = new String[]{"0","fooRefactoringType","fooClassSource","fooClassTarget", "fooMethod"};
		RowData rowData = new RowData(row);
		assertEquals(row.toString(), rowData.toString());
	}
	
	@Test
	public void testMethodGetRefactoringData(){
		String[] row = new String[]{"0","COLLAPSE_HIERARCHY","fooClassSource","fooClassTarget", "fooMethod"};
		RowData rowData = new RowData(row);
		RefactoringData refactoringData = rowData.getRefactoringData();
		assertNotNull(refactoringData);
	}
	
	@Test
	public void testMethodGetRefactoringData2(){
		String[] row = new String[]{"0",RefactoringType.EXTRACT_CLASS.toString(),"fooClassSource","fooClassTarget", "fooMethod"};
		RowData rowData = new RowData(row);
		RefactoringData refactoringData = rowData.getRefactoringData();
		assertNotNull(refactoringData);
		assertEquals("fooMethod",refactoringData.getMethodTarget());
	}
	
	

}
