package unitTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import refaco.RefactoringData;
import refaco.RefactoringType;

public class RefactoringDataTests {
	
	@Test
	public void testEmptyConstructor(){
		RefactoringData refactoringData = new RefactoringData();
		assertNull(refactoringData.getClassSource());
		assertNull(refactoringData.getClassTarget());
		assertNull(refactoringData.getMethodTarget());
		assertNull(refactoringData.getFields());
		assertNull(refactoringData.getArgument());
		assertNull(refactoringData.getRefactoringType());
		assertEquals(0,refactoringData.getSelectionStart());
		assertEquals(0,refactoringData.getSelectionLength());
	}
	
	@Test
	public void testFieldClassSource(){
		RefactoringData refactoringData = new RefactoringData();
		String classSource = "package1.subpackage1.classFoo";
		refactoringData.setClassSource(classSource);
		assertEquals(classSource, refactoringData.getClassSource());
	}
	
	@Test
	public void testFieldClassTarget(){
		RefactoringData refactoringData = new RefactoringData();
		String classTarget = "package1.subpackage1.classFoo";
		refactoringData.setClassTarget(classTarget);
		assertEquals(classTarget, refactoringData.getClassTarget());
	}
	
	@Test
	public void testFieldMethodTarget(){
		RefactoringData refactoringData = new RefactoringData();
		String methodTarget = "methodFoo";
		refactoringData.setMethodTarget(methodTarget);
		assertEquals(methodTarget, refactoringData.getMethodTarget());
	}
	
	@Test
	public void testFieldFields(){
		RefactoringData refactoringData = new RefactoringData();
		List<String> fields = new ArrayList<String>();
		refactoringData.setFields(fields);
		assertEquals(fields, refactoringData.getFields());
	}
	
	@Test
	public void testFieldArgument(){
		RefactoringData refactoringData = new RefactoringData();
		String argument = "argumentFoo";
		refactoringData.setArgument(argument);
		assertEquals(argument, refactoringData.getArgument());
	}
	
	@Test
	public void testFieldSelection(){
		RefactoringData refactoringData = new RefactoringData();
		int selectionStart = 1;
		int selectionLength = 8;
		refactoringData.setSelectionStart(selectionStart);
		refactoringData.setSelectionLength(selectionLength);
		assertEquals(selectionStart, refactoringData.getSelectionStart());
		assertEquals(selectionLength, refactoringData.getSelectionLength());
	}
	

	@Test
	public void testFieldRefactoringType(){
		RefactoringData refactoringData = new RefactoringData();
		RefactoringType refactoring = RefactoringType.COLLAPSE_HIERARCHY;
		refactoringData.setRefactoringType(refactoring);
		assertEquals(refactoring,refactoringData.getRefactoringType());
	}
	
	@Test
	public void testMethodGetTypeRefactoring(){
		RefactoringType refactoring = RefactoringData.getTypeRefactoring("CollapseHierarchy");
		assertEquals(RefactoringType.COLLAPSE_HIERARCHY,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("InlineClass");
		assertEquals(RefactoringType.INLINE_CLASS,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("introduceParameterObject");
		assertEquals(RefactoringType.INTRODUCE_PARAMETER_OBJECT,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("extractMethod");
		assertEquals(RefactoringType.EXTRACT_METHOD,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("moveMethod");
		assertEquals(RefactoringType.MOVE_METHOD,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("refactBlob");
		assertEquals(RefactoringType.MOVE_METHOD,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("extractClass");
		assertEquals(RefactoringType.EXTRACT_CLASS,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("RemoveParameter");
		assertEquals(RefactoringType.REMOVE_PARAMETER,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("ReplaceMethodWithMethodObject");
		assertEquals(RefactoringType.REPLACE_METHOD_WITH_METHOD_OJBECT,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("refactSpaghettiCode");
		assertEquals(RefactoringType.REFACTSPAGHETTICODE,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("removeParameter");
		assertEquals(null,refactoring);
		refactoring = RefactoringData.getTypeRefactoring("newRefactor");
		assertEquals(null,refactoring);
		assertNull(RefactoringData.getTypeRefactoring(null));
	}
	
	@Test
	public void testMethodIsImplemented(){ 
		RefactoringType refactoring = RefactoringType.INTRODUCE_PARAMETER_OBJECT;
		assertTrue(RefactoringData.isImplemented(refactoring));
		refactoring = RefactoringType.REMOVE_PARAMETER;
		assertTrue(RefactoringData.isImplemented(refactoring));
		refactoring = RefactoringType.MOVE_METHOD;
		assertTrue(RefactoringData.isImplemented(refactoring));
		refactoring = RefactoringType.EXTRACT_CLASS;
		assertTrue(RefactoringData.isImplemented(refactoring));
		refactoring = RefactoringType.COLLAPSE_HIERARCHY;
		assertFalse(RefactoringData.isImplemented(refactoring));
		refactoring = RefactoringType.EXTRACT_METHOD;
		assertTrue(RefactoringData.isImplemented(refactoring));
		refactoring = RefactoringType.INLINE_CLASS;
		assertFalse(RefactoringData.isImplemented(refactoring));
		refactoring = RefactoringType.REFACTSPAGHETTICODE;
		assertFalse(RefactoringData.isImplemented(refactoring));
		refactoring = RefactoringType.REPLACE_METHOD_WITH_METHOD_OJBECT;
		assertFalse(RefactoringData.isImplemented(refactoring));
		assertFalse(RefactoringData.isImplemented(null)); 
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
