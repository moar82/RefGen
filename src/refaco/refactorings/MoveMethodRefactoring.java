package refaco.refactorings;

import java.awt.Window;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Iterator;
import java.lang.Iterable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInstanceMethodProcessor;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.internal.ui.refactoring.RefactoringUIMessages;
import org.eclipse.ltk.ui.refactoring.RefactoringUI;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import refaco.RefactoringData;
import refaco.RefactoringOperationStatus;
import refaco.exceptions.RefactoringException;
import refaco.handlers.CodeSmellHandler;
import refaco.utils.SaveInTextFile;

/**
* Move Method refactoring
*
** @author Christian Kabulo (POLYMORSE)
*/
public class MoveMethodRefactoring extends refaco.refactorings.Refactoring{
	
	SaveInTextFile saved;
	public MoveMethodRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
		saved = new SaveInTextFile(_refactoringData, _projectName);
	}

	public void apply() throws RefactoringException {
		
		// Get the package and class name (Source)
		String packageAndClass = getRefactoringData().getClassSource();
		int index = packageAndClass.lastIndexOf('.');
		String packageSourceName;
		String classSourceName;
		if (index==-1){
			classSourceName = packageAndClass;
			packageSourceName = IPackageFragment.DEFAULT_PACKAGE_NAME;
		}
		else{
			packageSourceName = packageAndClass.substring(0, index);
			classSourceName = packageAndClass.substring(index + 1, packageAndClass.length());	
		}
		
		// Get the method name and parameters
		String methodAndParameters = getRefactoringData().getMethodTarget();
		String methodName = null;
		String[] parameters = null;
		try{
			methodName = methodAndParameters.substring(0, methodAndParameters.indexOf('(')).replaceAll("\\s","");
			if(methodName.equals("<init>")){
				methodName = classSourceName;
			}
			parameters = methodAndParameters.substring(methodAndParameters.indexOf('(') +1,methodAndParameters.indexOf(')')).split(",");
			if(parameters.length == 1 && parameters[0].length()==0)
				parameters = new String[0];
		}catch(StringIndexOutOfBoundsException e){
			throw new RefactoringException("Method name format exception");
		}
		
		// Get the package and class name (Target)
		
		String classTargetName;
		packageAndClass = getRefactoringData().getClassTarget();
		index = packageAndClass.lastIndexOf('.');
		if (index==-1){
			classTargetName = packageAndClass;
		}
		else{
			classTargetName = packageAndClass.substring(index + 1, packageAndClass.length());	
		}
		
		// Get the IProject from the projectName
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(getProjectName());
		// Get the IType
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder(CodeSmellHandler.javasrc));
		IPackageFragment classPackage = rootpackage.getPackageFragment(packageSourceName);
		ICompilationUnit classCU = classPackage.getCompilationUnit(classSourceName + ".java");
		IType typeSource = classCU.getType(classSourceName);
	
		// Get the IMethod
		IMethod[] methods = null;
		try {
			methods = typeSource.getMethods();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IMethod method = null;
		try {
				//for(IMethod method : methods){
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				
			
				int i = 0;
				while(method == null && i < methods.length){
					IMethod me = methods[i];
					String[] paramTypes =getParameterTypesOfActualMethod(me);
					if (me.getElementName().equals(methodName)){
						//because we have qualified name from padl we clean both arrays
						String[] tmp_paramTypes =cleanQualifiedName(paramTypes);
						String[] tmp_parameters =cleanQualifiedName(parameters);
						
						if  (Arrays.equals(tmp_paramTypes,tmp_parameters)) {
							method = me;
						}
					}
					i++;
				}
		
						
					if (method != null && method.exists()) {
						
						// Create the classes needed for apply the refactoring
						MoveInstanceMethodProcessor processor = new MoveInstanceMethodProcessor(method, new CodeGenerationSettings());
						Refactoring refactoring = new ProcessorBasedRefactoring(processor);
						// Set the target class
						//refactoring.checkInitialConditions(null);
						IProgressMonitor monitor = new NullProgressMonitor();
						refactoring.checkInitialConditions(monitor);
						IVariableBinding[] targets = processor.getPossibleTargets();
						IVariableBinding targetArgument = null;
						for(IVariableBinding target: targets){
						 if (target.getType().getName().equals(classTargetName)){
								processor.setTarget(target);
								targetArgument = target;
								break;
							}
						 
						}
						if (targetArgument != null){
							refaco.refactorings.MoveInstanceMethodWizard wizard = new refaco.refactorings.MoveInstanceMethodWizard(
									processor, refactoring, targetArgument);
				
							RefactoringContribution contribution = RefactoringCore
									.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD);
							MoveMethodDescriptor descriptor = (MoveMethodDescriptor) contribution.createDescriptor();
							RefactoringStatus status = new RefactoringStatus();
							refactoring.checkFinalConditions(monitor);
							status = new RefactoringStatus();
							Change change = refactoring.createChange(monitor);
							change.initializeValidationData(monitor);
							change.perform(monitor);
							//in visual studio code the part missing
						}
						else{final String messageError = "cannot be moved cause target class is not valid for "
									+ "Eclipse.\nSource method:"+getRefactoringData().getClassSource()+
									"."+methodAndParameters+"\tTarget class:"+packageAndClass;
						saved.saving("MoveMethodRefactoring", messageError);
						System.out.println(messageError);
						}
							
					} else {
						final String messageError = "Cannot find Source method:"+getRefactoringData().getClassSource()+
								"."+methodAndParameters+"\tTarget class:"+packageAndClass;
					saved.saving("MoveMethodRefactoring", messageError);
					System.out.println(messageError);
					}
				} else {
					System.err.println("Nature disabled");
					
					throw new RefactoringException("Java Nature disabled");
				}
		} catch (CoreException e1) {
			e1.printStackTrace();
			
			throw new RefactoringException(e1.getMessage());
		}
	}		
}
