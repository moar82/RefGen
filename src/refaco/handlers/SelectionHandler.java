package refaco.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.ui.handlers.HandlerUtil;

import refaco.views.RefactoringListTableView;

/**
 * This class manage the button for select/deselect all the refactorings in the Refactoring Sequence view
 *
 */
public class SelectionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		// Get the view Refactoring Sequence
		RefactoringListTableView refactoringOppsTV = (RefactoringListTableView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
				.findView(RefactoringListTableView.ID);
		// Get the checkbox table view
		CheckboxTableViewer table = refactoringOppsTV.getTableViewer();
		
		if(refactoringOppsTV.isAllSelected()){
			// Deselect all
			refactoringOppsTV.setAllSelected(false);
			table.setAllChecked(false);
			
		}else{
			// Select All
			refactoringOppsTV.setAllSelected(true);
			refactoringOppsTV.getTableViewer().setAllChecked(true);
			Object[] notimplementedRefactorings = table.getGrayedElements();
			for(int i=0; i< notimplementedRefactorings.length; i++){
				table.setChecked(notimplementedRefactorings[i], false);
			}
		}
		return null;
	}

}
