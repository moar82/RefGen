package unitTests;

import static org.junit.Assert.*;
import org.junit.Test;

import refaco.ProjectData;

public class ProjectDataTests {
	
	@Test
	public void testEmptyConstructor(){
		ProjectData projectData = new ProjectData();
		assertNull(projectData.getName());
		assertNull(projectData.getPath());
	}
	
	@Test
	public void testConstructor(){
		String projectName = "project1";
		String path = "/Users/user1/project1";
		ProjectData projectData = new ProjectData(projectName, path);
		assertEquals(projectName, projectData.getName());
		assertEquals(path, projectData.getPath());
	}
	

	@Test
	public void testConstructor2(){
		String projectName = "project1";
		String packageName = "packageFoo";
		String path = "/Users/user1/project1";
		ProjectData projectData = new ProjectData(projectName, path, packageName) ;
		assertEquals(projectName, projectData.getName());
		assertEquals(path, projectData.getPath());
	}
	
	@Test
	public void testFieldName(){
		ProjectData projectData = new ProjectData();
		String projectName = "project1";
		projectData.setName(projectName);
		assertEquals(projectName, projectData.getName());
	}
	
	@Test
	public void testFieldPath(){
		ProjectData projectData = new ProjectData();
		String projectPath = "/Users/user1/project1";
		projectData.setPath(projectPath);
		assertEquals(projectPath, projectData.getPath());
	}
	
	@Test
	public void testFieldPackage(){
		ProjectData projectData = new ProjectData();
		String packageName = "packageFoo";

		projectData.setPackageName(packageName);
		assertEquals(packageName, projectData.getPackageName());
	}
	
	@Test
	public void testToString(){
		String projectName = "project1";
		String path = "/Users/user1/project1";
		ProjectData projectData = new ProjectData(projectName, path);
		assertEquals("ProjectName: " + projectName + " || Package: null"  + " || Path: " + path, projectData.toString());
		
	}
	
	@Test
	public void testToStringNull(){
		ProjectData projectData = new ProjectData();
		assertEquals("ProjectName: null || Package: null || Path: null", projectData.toString());
		
	}
	
	
	
	

}
