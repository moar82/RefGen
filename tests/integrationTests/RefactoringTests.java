package integrationTests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;
import refaco.refactorings.ExtractClassRefactoring;
import refaco.refactorings.ExtractMethodRefactoring;
import refaco.refactorings.IntroduceParameterObjectRefactoring;
import refaco.refactorings.MoveMethodRefactoring;
import refaco.refactorings.RemoveParametersRefactoring;

public class RefactoringTests {
	
	@Test //CP17
	public void testIntroduceParamterObject(){
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Movie");
		r.setMethodTarget("Movie");
		r.setMethodTarget("setPriceCode(int)");
		IntroduceParameterObjectRefactoring rpR = new IntroduceParameterObjectRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
		} catch (RefactoringException e) {
			fail();
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test //CP18
	public void testIntroduceParamterObjectFail(){
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Rental");
		r.setMethodTarget("Rental2(qqsq.Movie movie, int daysRented)");
		IntroduceParameterObjectRefactoring rpR = new IntroduceParameterObjectRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
			fail();
		} catch (RefactoringException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test //CP19
	public void testExtractClass() {
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Rental");
		List<String> fields = new ArrayList<String>();
		fields.add("_daysRented");
		r.setFields(fields);
		ExtractClassRefactoring rpR = new ExtractClassRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
		} catch (RefactoringException e) {
			fail();
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test //CP20
	public void testExtractClassFail() {
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Rental");
		List<String> fields = new ArrayList<String>();
		fields.add("daysRented");
		r.setFields(fields);
		ExtractClassRefactoring rpR = new ExtractClassRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
			fail();
		} catch (RefactoringException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test //CP21
	public void testRemoveParameter() {
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Movie");
		r.setMethodTarget("Movie(String title, int priceCode, int code)");
		r.setArgument("code");
		RemoveParametersRefactoring rpR = new RemoveParametersRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
			assertTrue(true);
		} catch (RefactoringException e) {
			fail();
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test //CP22
	public void testRemoveParameterFail() {
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Movie");
		r.setMethodTarget("Movie(String title, int priceCode, int code)");
		r.setArgument("code1");
		RemoveParametersRefactoring rpR = new RemoveParametersRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
			fail();
		} catch (RefactoringException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test //CP23
	public void testMoveMethod() {
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Rental");
		r.setClassTarget("qqsq.Movie");
		r.setMethodTarget("metodoPrueba()");
		MoveMethodRefactoring rpR = new MoveMethodRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
			assertTrue(true);
		} catch (RefactoringException e) {
			fail();
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test //CP24
	public void testMoveMethodFail() {
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Rental");
		r.setClassTarget("qqsq.Movie");
		r.setMethodTarget("metodoPrasdfba()");
		MoveMethodRefactoring rpR = new MoveMethodRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
			fail();
		} catch (RefactoringException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test //CP25
	public void testExtractMethod(){
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Rental");
		r.setSelectionStart(27); // first line
		r.setSelectionLength(3); // number of lines
		ExtractMethodRefactoring rpR = new ExtractMethodRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(1000);
			rpR.apply();
			assertTrue(true);
		} catch (RefactoringException e) {
			fail();
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test //CP26
	public void testExtractMethodFail(){
		RefactoringData r = new RefactoringData();
		r.setClassSource("qqsq.Rental");
		r.setSelectionStart(27); // first line
		r.setSelectionLength(100); // number of lines
		ExtractMethodRefactoring rpR = new ExtractMethodRefactoring(r, "PruebaCodeSmell");
		try {
			Thread.sleep(2000);
			rpR.apply();
			fail();
		} catch (RefactoringException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}
	

}
