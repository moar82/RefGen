package refaco.handlers;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import refaco.RefactoringData;
import refaco.RefactoringOperationStatus;
import refaco.RowData;
import refaco.exceptions.RefactoringException;
import refaco.refactorings.CollapseHierarchyRefactoring;
import refaco.refactorings.ExtractClassRefactoring;
import refaco.refactorings.ExtractMethodRefactoring;
import refaco.refactorings.InlineClassRefactoring;
import refaco.refactorings.IntroduceParameterObjectRefactoring;
import refaco.refactorings.MoveMethodRefactoring;
import refaco.refactorings.Refactoring;
import refaco.refactorings.RemoveParametersRefactoring;
import refaco.views.CodeSmellTableView;
import refaco.views.RefactoringListTableView;

/**
 * This class manage the apply of the refactorings
 *
 */
public class ApplyRefactoringHandler extends AbstractHandler {
	
	int refactoringIndex;				// the current refactoring
	private Thread threadObject;	// thread for synchronize the runnable with the next button
	private CodeSmellHandler code;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get Refactoring Sequence view
		RefactoringListTableView refactoringListTV = (RefactoringListTableView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().findView(RefactoringListTableView.ID);
		// Get the checked refactorings in the table
		Object[] selected = refactoringListTV.getTableViewer().getCheckedElements();
		refactoringListTV.getTableViewer().remove(selected);
		 
		
		List<RefactoringData> selectedRefactoring = new ArrayList<RefactoringData>();
		
		
		List<RefactoringOperationStatus> refactoringStatus = new ArrayList<RefactoringOperationStatus>();
		for (Object o : selected) {
			selectedRefactoring.add(((RowData) o).getRefactoringData());
			
		}
		if (selectedRefactoring != null  ) {
			// get the project data from Code Smell view
			CodeSmellTableView codeSmellTV = (CodeSmellTableView) HandlerUtil.getActiveWorkbenchWindow(event)
					.getActivePage().findView(CodeSmellTableView.ID);
			String projectName = codeSmellTV.getLastProjectAnalized();
			refactoringIndex = 0;			

			// Runnable task
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InterruptedException {
					int totalUnitsOfWork = selectedRefactoring.size();
					monitor.beginTask("Running RefGen", totalUnitsOfWork);
					int cont = 1;
					for (RefactoringData r : selectedRefactoring) {
						monitor.subTask("Apply Code refactoring " + cont + " de " + totalUnitsOfWork);
						
						// wait for the user click next button
						if(refactoringIndex < cont){
							/*synchronized(threadObject){
                                try{
                                    threadObject.wait();
                                } 
                                catch (InterruptedException e) {}
                            }*/
						}
						// if the user has cancelled the operation finish
						if(monitor.isCanceled()){
							return;
						}
						monitor.worked(1);
						monitor.subTask("Applying Code refactoring " + cont + " de " + totalUnitsOfWork);
						refactoringStatus.add(applyRefactoring( r, projectName));
						
						//refactoringListTV.update();

						cont++;

					}
					monitor.done();
				}
			};
			threadObject = new Thread();
	        threadObject.start();
			// Create the monitor dialog
			IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			Shell shell = win != null ? win.getShell() : null;
			try {
				new ProgressMonitorDialog(shell) {
					@Override
					public Composite createDialogArea(Composite parent) {
						Composite container = (Composite) super.createDialogArea(parent);
						setCancelable(true);
						return container;
					}
					
					@Override
					protected void createButtonsForButtonBar(Composite parent) {
						// create the next button
						/*super.createButtonsForButtonBar(parent);
						createButton(parent, IDialogConstants.OK_ID, "Next", false); 
					}
					
					@Override
					protected void buttonPressed(int buttonId) {
						/*if(buttonId == IDialogConstants.OK_ID){
							refactoringIndex++;
							// Resume (next is pressed)
			                synchronized(threadObject)
			                {
			                    threadObject.notify();
			                }
						}else if(buttonId == IDialogConstants.CANCEL_ID){
							// Cancel is pressed
							cancelPressed();
							this.getProgressMonitor().setCanceled(true);
							synchronized(threadObject)
			                {
			                    threadObject.notify();
			                }
						}*/
					}
				}.run(true, true, runnable);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				HandlersUtils.showError(e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
				HandlersUtils.showError(e.getMessage());
			}
		}
//		code.execute(null);
		
		return null;
	}
	/** This method apply a refactoring operation 
	 * 
	 * @param refactoringData The refactoring
	 * @param projectName	  The project name	
	 * @return
	 */
	
	private RefactoringOperationStatus applyRefactoring(RefactoringData refactoringData, String projectName) {
		RefactoringOperationStatus status = new RefactoringOperationStatus();
		try {
			switch (refactoringData.getRefactoringType()) {
			case INTRODUCE_PARAMETER_OBJECT:
				IntroduceParameterObjectRefactoring ipoR = new IntroduceParameterObjectRefactoring(refactoringData,
						projectName);
				ipoR.apply();
				break;
			case  REMOVE_PARAMETER:
				RemoveParametersRefactoring rpR = new RemoveParametersRefactoring(refactoringData, projectName);
				rpR.apply();
				break;
			case MOVE_METHOD:
				Refactoring mmR = new MoveMethodRefactoring(refactoringData, projectName);
				mmR.apply();	
						
				break;
			case EXTRACT_CLASS:
				ExtractClassRefactoring ecR = new ExtractClassRefactoring(refactoringData, projectName);
				ecR.apply();
				break;
			case INLINE_CLASS:
				InlineClassRefactoring ilR = new InlineClassRefactoring(refactoringData, projectName);
				ilR.apply();
				break;
			case COLLAPSE_HIERARCHY:
				CollapseHierarchyRefactoring chR = new CollapseHierarchyRefactoring(refactoringData, projectName);
				chR.apply();
				break;
			case EXTRACT_METHOD:
				String selection = refactoringData.getMethodTarget();
				
				// selection format: firstLine:numLines
				refactoringData.setSelectionStart(Integer.parseInt(selection.substring(0, selection.indexOf(':'))));
				refactoringData.setSelectionLength(Integer.parseInt(selection.substring(selection.indexOf(':') + 1, selection.length())));
				ExtractMethodRefactoring emR = new ExtractMethodRefactoring(refactoringData, projectName);
				emR.apply();
				break;
			default:
				System.err.println("Refactoring: " + refactoringData.getRefactoringType() + " not implemented");
				status.setCode(-1);
				status.setMessage("Refactoring: " + refactoringData.getRefactoringType() + " not implemented");
				return status;
			}
		} catch (RefactoringException e) {
			e.printStackTrace();
			status.setCode(-1);
			status.setMessage(e.getMessage());
			HandlersUtils.showError(e.getMessage());
			return status;
		}
		status.setCode(0);
		status.setMessage("Task completed");
		return status;
	}
	
	public  void pressed () {
		Display display = new Display ();
		Shell shell = new Shell(display);
		shell.setText("SWT KeyEvent Example");
	 
		shell.setLayout(new FillLayout());

		Button button = new Button(shell, SWT.CENTER);
		
		button.setText("Type Something");
		
		button.addKeyListener(new KeyAdapter()
		{	
			public void keyPressed(KeyEvent e)
			{
				String string = "";
				
				//check click together?
				if ((SWT.ALT) != 0) string += "ALT - keyCode = " + e.getKeyCode();
				if (( SWT.CTRL) != 0) string += "CTRL - keyCode = " + e.getKeyCode();
				if (( SWT.SHIFT) != 0) string += "SHIFT - keyCode = " + e.getKeyCode();
				
				if(e.getKeyCode() == SWT.BS)
				{
					string += "BACKSPACE - keyCode = " + e.getKeyCode();
				}
				
				if(e.getKeyCode() == SWT.ESC)
				{
					string += "ESCAPE - keyCode = " + e.getKeyCode();
				}
				
				//check characters 
				if(e.getKeyCode() >=97 && e.getKeyCode() <=122)
				{
					string +=  " - keyCode = " + e.getKeyCode();
				}


				
				if(!string.equals(""))
					System.out.println (string);
			}
		});
		
		shell.open();
	 
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
