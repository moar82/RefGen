package unitTests;

import static org.junit.Assert.*;
import org.junit.Test;
import refaco.RowData;
import refaco.TextColumn;

public class TextColumnTests {
	
	@Test 
	public void testConstructor1(){
		String title = "fooTitle";
		int columnIndex = 0;
		int defaultWidth = 250;
		TextColumn textColumn = new TextColumn(title,columnIndex);
		assertNotNull(textColumn);
		assertEquals(title,textColumn.getTitle());
		assertEquals(defaultWidth,textColumn.getWidth());
	}
	
	@Test
	public void testConstructor2(){
		String title = "fooTitle";
		int columnIndex = 0;
		int width = 0;
		TextColumn textColumn = new TextColumn(title,columnIndex,width);
		assertNotNull(textColumn);
		assertEquals(title,textColumn.getTitle());
		assertEquals(250, textColumn.getWidth());
	}
	
	@Test
	public void testGetText(){
		String title = "fooRefactoringType";
		int columnIndex = 1;
		int width = 0;
		TextColumn textColumn = new TextColumn(title,columnIndex,width);
		String[] row = new String[]{"0","fooRefactoringType","fooClassSource","fooClassTarget", "fooMethod"};
		RowData rowData = new RowData(row);
		assertEquals(title, textColumn.getText(rowData)); 
	}
	
	

}
