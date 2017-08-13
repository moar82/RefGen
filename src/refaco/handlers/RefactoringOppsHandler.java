package refaco.handlers;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import refaco.RefactoringData;
import refaco.RowData;
import refaco.views.CodeSmellTableView;
import refaco.views.RefactoringListTableView;

/**
 * This class manage the compute of the refactoring opportunities
 */
public class RefactoringOppsHandler extends AbstractHandler{
	
	private List<RowData> refactoringOppsRows;			// rows of Refactoring Sequence view
	private List<RefactoringData> refactoringDataRows;	// rows with format RefactoringData
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		// views implied
		RefactoringListTableView refactoringOppsTV;
		CodeSmellTableView codeSmellTV;
		
		try {
			// get Code Smell view to get the refactoringOpps and the project data
			codeSmellTV = (CodeSmellTableView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.findView(CodeSmellTableView.ID);
			
			if(codeSmellTV != null){
				List<String> refactoringOpps = codeSmellTV.getRefactoringOpps();
				String path = codeSmellTV.getLastProjectAnalized();
				if(refactoringOpps != null && path != null){
					// transform the strings to RefactoringData
					refactoringDataRows = stringToRefactoringData(refactoringOpps);
					// transform RefactorinData to RowData for show in the table
					refactoringOppsRows = processRefactoringsOpps(refactoringDataRows);
					// show Refactoring Opportunities table view
					refactoringOppsTV = (RefactoringListTableView) HandlerUtil.getActiveWorkbenchWindow(event).
							getActivePage().showView(RefactoringListTableView.ID);
					// Update the table View with the refactoring opportunities
					updateTable(refactoringOppsTV.getTableViewer(), refactoringOppsRows);
					//refactoringOppsTV.update();
					
				}else{
					HandlersUtils.showError("Refactorings opps or path null");
				}
			}else{
				HandlersUtils.showError("View not found");
			}
		} catch (PartInitException e) {
			e.printStackTrace();
			HandlersUtils.showError(e.getMessage());
		}
		return null;
	}
	
	/** This method transform each string of FitnessReport to a RefactoringData object
	 * 
	 * @param refactoringOpps Strings with the refactorings operations (extracted from FitnessReport)
	 * @return List<RefactoringData>
	 */
	private List<RefactoringData> stringToRefactoringData(List<String> refactoringOpps){
		// result
		List<RefactoringData> res = new ArrayList<RefactoringData>();
		// for each line of FitnessReport
		for(String s: refactoringOpps){
			// get each field 
			String[] line = s.split(":");
			RefactoringData refactoringTemp = new RefactoringData();
			refactoringTemp.setRefactoringType(RefactoringData.getTypeRefactoring(line[2].substring(line[2].indexOf('=')+1)));
			refactoringTemp.setClassSource(line[3].substring(line[3].indexOf('=')+1));
			refactoringTemp.setClassTarget(line[4].substring(line[4].indexOf('=')+1));
			
			
			// Check if the method target is the last field of the line
			if(line.length==7){
				refactoringTemp.setMethodTarget(line[5].substring(line[5].indexOf('=')+1,line[5].length()));
				res.add(refactoringTemp);

			}else{
				refactoringTemp.setMethodTarget(" ");
				res.add(refactoringTemp);
			}			
		}
		return res;
	}
	
	/** Transform the RefactoringData objects to RowData and check if the refactoring operation
	 * is implemented or not.
	 * 
	 * @param refactoringOpps List of RefactoringData objects
	 * @return List<RowData>
	 */
	private List<RowData> processRefactoringsOpps(List<RefactoringData> refactoringOpps) {
		List<RowData> res = new ArrayList<RowData>();
		for(RefactoringData r: refactoringOpps){
			res.add(new RowData(
					// check if the refactoring is implemented
					RefactoringData.isImplemented(r.getRefactoringType())?"1":"0",
					r.getRefactoringType().toString(),
					r.getClassSource(),
					r.getClassTarget().equals("null")?"":r.getClassTarget(),
					r.getMethodTarget().equals("null")?"":r.getMethodTarget()
					));
		}
		return res;
	}
	
	/** This method update the table with the rowdata list
	 * 
	 * @param table The checkbox table
	 * @param data Rowdata list
	 */
	public void updateTable(CheckboxTableViewer table, List<RowData> data ){
		table.setInput(data);
		table.setAllChecked(false);
		for(int i=0; i < data.size(); i++){
			if(RefactoringData.isImplemented(refactoringDataRows.get(i).getRefactoringType())){
				// By default, select all the refactorings 
				table.setChecked(table.getElementAt(i), true);
			}else{
				// the refactoring isn't implemented
				table.setGrayed(table.getElementAt(i), true);
			}
		}
		
	}

}
