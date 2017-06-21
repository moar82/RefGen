package refaco.refactorings;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.PushDownDescriptor;
import org.eclipse.jdt.internal.corext.refactoring.structure.PushDownRefactoringProcessor;
import org.eclipse.jdt.internal.ui.refactoring.PushDownWizard;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Display;
import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;

/**
 * Collapse Hiearchy refactoring (not implemented yet)
 *
 */
public class CollapseHierarchyRefactoring extends refaco.refactorings.Refactoring {

	public CollapseHierarchyRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
	}

	public void apply() throws RefactoringException {

		// Get the package and class name (Source)
		String temp = getRefactoringData().getClassSource();
		int index = temp.lastIndexOf('.');
		String packageSourceName = temp.substring(0, index);
		String classSourceName = temp.substring(index + 1, temp.length());

		// Get the package and class name (Target)
		temp = getRefactoringData().getClassSource();
		index = temp.lastIndexOf('.');
		String packageTargetName = temp.substring(0, index);
		String classTargetName = temp.substring(index + 1, temp.length());

		// Get the IProject from the projectName
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(getProjectName());
		try {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				// Get the src package
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder("src"));
				// Get the Class Source
				IPackageFragment classPackage = rootpackage.getPackageFragment(packageSourceName);
				ICompilationUnit classCU = classPackage.getCompilationUnit(classSourceName + ".java");
				IType typeSource = classCU.getType(classSourceName);
				// Get the Class Target
				IPackageFragment classTargetPackage = rootpackage.getPackageFragment(packageTargetName);
				ICompilationUnit classTargetCU = classTargetPackage.getCompilationUnit(classTargetName + ".java");
				IType typeTarget = classTargetCU.getType(classTargetName);
				typeTarget.getChildren();

				// Get All the members
				IJavaElement[] javaElements =  typeSource.getChildren();
				IMember[] members = new IMember[javaElements.length];
				for(int i=0; i < javaElements.length;i++){
					members[i] = (IMember) javaElements[i];
				}

				if (members != null & members.length > 0) {
					System.out.println("Members");

					RefactoringContribution contribution = RefactoringCore
							.getRefactoringContribution(IJavaRefactorings.PUSH_DOWN);
					PushDownDescriptor descriptor = (PushDownDescriptor) contribution.createDescriptor();
					descriptor.setProject(javaProject.getElementName());
//					descriptor.setClassName("NewClass");
//					descriptor.setParameterName("newObjectClass");
//					descriptor.setPackageName(classPackage.getElementName());
//					descriptor.setMethod(method);

					PushDownRefactoringProcessor processor = new PushDownRefactoringProcessor(members);
					Refactoring refactoring = new ProcessorBasedRefactoring(processor);
					PushDownWizard wizard = new PushDownWizard(processor, refactoring);
					RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);

					String titleForFailedChecks = "";
					op.run(Display.getDefault().getActiveShell(), titleForFailedChecks);


				} else {
					System.err.println("Class Empty");
					throw new RefactoringException("Class Empty");
				}
			} else {
				System.err.println("Nature disabled");
				throw new RefactoringException("Java Nature disabled");
			}
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RefactoringException(e1.getMessage());
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// operation was cancelled
			throw new RefactoringException("Operation was cancelled");
		}
	}

}
