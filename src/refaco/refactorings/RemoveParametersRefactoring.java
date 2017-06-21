package refaco.refactorings;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.ChangeMethodSignatureDescriptor;
import org.eclipse.jdt.internal.corext.refactoring.ParameterInfo;
import org.eclipse.jdt.internal.corext.refactoring.structure.ChangeSignatureProcessor;
import org.eclipse.jdt.internal.ui.refactoring.ChangeSignatureWizard;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Display;
import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;

/**
 * Remove Parameter refactoring
 *
 */
public class RemoveParametersRefactoring extends refaco.refactorings.Refactoring {

	public RemoveParametersRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
	}

	public void apply() throws RefactoringException {

		// Get the package and class name
		String temp = getRefactoringData().getClassSource();
		int index = temp.lastIndexOf('.');
		String packageName = temp.substring(0, index);
		String className = temp.substring(index + 1, temp.length());
		String argumentName = getRefactoringData().getArgument();

		// Get the method and parameters
		String methodAndParameters = getRefactoringData().getMethodTarget();
		String methodName = methodAndParameters.substring(0, methodAndParameters.indexOf('(')).replaceAll("\\s", "");
		if (methodName.equals("<init>")) {
			methodName = className;
		}
		String[] parameters = methodAndParameters
				.substring(methodAndParameters.indexOf('(') + 1, methodAndParameters.indexOf(')')).split(",");

		// Get the IProject from the projectName
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(getProjectName());

		try {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {

				// Get the IType
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder("src"));
				IPackageFragment classPackage = rootpackage.getPackageFragment(packageName);
				ICompilationUnit classCU = classPackage.getCompilationUnit(className + ".java");
				IType type = classCU.getType(className);

				IMethod[] methods = type.getMethods();
				IMethod method = null;
				int i = 0;
				while (method == null && i < methods.length) {
					IMethod me = methods[i];
					if (me.getElementName().equals(methodName)
							&& me.getNumberOfParameters() == parameters.length) {
						method = me;
					}
					i++;
				}

				if (method.exists()) {

					// Initialize the refactoring descriptor
					RefactoringContribution contribution = RefactoringCore
							.getRefactoringContribution(IJavaRefactorings.CHANGE_METHOD_SIGNATURE);
					ChangeMethodSignatureDescriptor descriptor = (ChangeMethodSignatureDescriptor) contribution
							.createDescriptor();
					descriptor.setProject(javaProject.getElementName());

					// mark the parameter as deleted
					boolean atLeastOne = false;
					ChangeSignatureProcessor processor = new ChangeSignatureProcessor(method);
					List<ParameterInfo> parametersMethod = processor.getParameterInfos();
					for (int j = 0; j < parametersMethod.size(); j++) {
						if (parametersMethod.get(j).getOldName().equals(argumentName)) {
							parametersMethod.get(j).markAsDeleted();
							atLeastOne = true;
							break;
						}
					}
					
					if(atLeastOne){
						// Create the classes needed for apply the refactoring
						Refactoring refactoring = new ProcessorBasedRefactoring(processor);
						ChangeSignatureWizard wizard = new ChangeSignatureWizard(processor, refactoring);
						RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);

						// Execute the refactoring
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								try {
									op.run(Display.getDefault().getActiveShell(), "");
								} catch (InterruptedException e) {
									e.printStackTrace();
									// operation was cancelled
								}
							}
						});
					}else{
						throw new RefactoringException("Argument not exist");
					}
				} else {
					throw new RefactoringException("Method not exist");
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
