package refaco.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import refaco.TextColumn;

import org.eclipse.jface.viewers.*;
import javax.inject.Inject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;

/**
 * This view show the refactoring opportunities list
 *
 */
public class RefactoringListTableView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "refaco.views.RefactoringListTableView";

	private CheckboxTableViewer tableViewer;	// checkbox table view
	private boolean allSelected;				// flag to check if all the refactoring are selected 
	private TableViewer fViewer;
	
	/**
	 * Getters & Setters
	 */
	
	public CheckboxTableViewer getTableViewer() {
		return tableViewer;
	}

	public void setTableViewer(CheckboxTableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public boolean isAllSelected() {
		return allSelected;
	}

	public void setAllSelected(boolean allSelected) {
		this.allSelected = allSelected;
	}

	@Inject
	@Override
	public void createPartControl(Composite parent) {
		// create the table Viewer
		tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.getTable().setLinesVisible(true);
		getSite().setSelectionProvider(tableViewer);
		
		// Add the columns to the table Refactoring Sequence
		ViewUtils.addColumn(new TextColumn("Refactoring technique",1) , tableViewer);
		ViewUtils.addColumn(new TextColumn("Class Source",2) , tableViewer);
		ViewUtils.addColumn(new TextColumn("Class Target",3), tableViewer);
		ViewUtils.addColumn(new TextColumn("Method",4) , tableViewer);
		// By default, all the refactorings are selected
		allSelected = true;
		
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
	public void update()
	{
		tableViewer.refresh(true);
		//tableViewer.getTable().update();	
	}
	
}
