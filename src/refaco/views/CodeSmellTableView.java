package refaco.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import refaco.ProjectData;
import refaco.TextColumn;

import org.eclipse.jface.viewers.*;
import java.util.List;
import javax.inject.Inject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

/**
 * This class show the code smells and the antipatterns detected
 *
 */
public class CodeSmellTableView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "refaco.views.CodeSmellTableView";

	private TableViewer tableViewer;				// table view
	protected ProjectData projectData;				// data poject selected
	private String lastProjectAnalized;				// last project analized 
	private List<String> refactoringOpps;			// list of possible refactorings
	private ISelectionListener selectionListener; 	// selection listener

	/**
	 *   Getters & setters
	 */
	
	public ProjectData getProjectData() {
		return projectData;
	}

	public void setProjectData(ProjectData projectData) {
		this.projectData = projectData;
	}
	
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}
	
	public String getLastProjectAnalized() {
		return lastProjectAnalized;
	}

	public void setLastProjectAnalized(String lastProjectAnalized) {
		this.lastProjectAnalized = lastProjectAnalized;
	}
	
	public List<String> getRefactoringOpps() {
		return refactoringOpps;
	}

	public void setRefactoringOpps(List<String> refactoringOpps) {
		this.refactoringOpps = refactoringOpps;
	}

	@Inject
	@Override
	public void createPartControl(Composite parent) {
		// create the table Viewer
		tableViewer = new TableViewer(parent,SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.getTable().setLinesVisible(true);
		getSite().setSelectionProvider(tableViewer);
		
		// Add the columns to the table Code Smell
		ViewUtils.addColumn(new TextColumn("Code Smell/Antipattern to solve",0), tableViewer);
		ViewUtils.addColumn(new TextColumn("Class",1), tableViewer);
		//ViewUtils.addColumn(new TextColumn("Method",2), tableViewer);
		
		// Add the listener for the users project selection 
		addSelectionListener();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
	
	/**
	 * Add the selection listener to the view
	 */
	private void addSelectionListener(){
		selectionListener = new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
				try{
					if (sourcepart != CodeSmellTableView.this && selection instanceof IStructuredSelection) {
						projectData = getSelection(selection);
					}
				}catch(RuntimeException | CoreException e){
					// ignore
				}	
			}
		};
	}
	
	/** 
	 * Get the project or package selected by the user
	 * @param selection
	 * @return ProjectData
	 * @throws CoreException
	 */
	public static ProjectData getSelection(ISelection selection) throws CoreException{
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		ProjectData res = null;
		
		if(element instanceof IProject){
			IProject iProject = (IProject) element;
			res =  new ProjectData(iProject.getName(),iProject.getDescription().getLocationURI().getPath());
		}else if(element instanceof IJavaProject){
			IJavaProject iJavaProject = (IJavaProject) element;
			res = new ProjectData(iJavaProject.getElementName(),iJavaProject.getResource().getLocationURI().getPath());
		}else if(element instanceof IPackageFragmentRoot){
			IJavaProject iJavaProject =  ((IPackageFragmentRoot) element).getJavaProject();
			res = new ProjectData(iJavaProject.getElementName(),iJavaProject.getResource().getLocationURI().getPath());
		}else if(element instanceof IPackageFragment){
			IPackageFragment iPackageFragment = (IPackageFragment) element;
			res = new ProjectData(iPackageFragment.getJavaProject().getElementName(),
					iPackageFragment.getJavaProject().getResource().getLocationURI().getPath(),
					iPackageFragment.getElementName());
		}
		return res;
	}
	
	@Override
	public void dispose() {
		// unregister the listener when the view is disposed
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
		selectionListener = null;
		super.dispose();
	}
	
	
}
