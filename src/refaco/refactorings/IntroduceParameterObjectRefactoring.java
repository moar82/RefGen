package refaco.refactorings;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.IntroduceParameterObjectDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.internal.corext.refactoring.structure.IntroduceParameterObjectProcessor;
import org.eclipse.jdt.internal.ui.refactoring.IntroduceParameterObjectWizard;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Display;
import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;
import refaco.handlers.CodeSmellHandler;
import refaco.utils.SaveInTextFile;

/**
 * Introduce Parameter Object refactoring
 *
 */
public class IntroduceParameterObjectRefactoring extends refaco.refactorings.Refactoring {

	SaveInTextFile saved;
	public IntroduceParameterObjectRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
		saved = new SaveInTextFile(_refactoringData, _projectName);
	}
	
	private static String createNewClass(String sourceClass) {
		final java.util.Random rand = new java.util.Random();
        String newClassName = ("Clazz" + rand.nextDouble()+sourceClass).replace('.', '0');
        return newClassName;
    }

	public void apply() throws RefactoringException  {


		// Get the package and class name
		String packageAndClass = getRefactoringData().getClassSource();
		int index = packageAndClass.lastIndexOf('.');
		String packageName;
		String className;
		if (index==-1){
			className = packageAndClass;
			packageName = IPackageFragment.DEFAULT_PACKAGE_NAME;
		}
		else{
			packageName = packageAndClass.substring(0, index);
			className = packageAndClass.substring(index + 1, packageAndClass.length());	
		}

		// Get the methods and parameters
		String[] methodsAndParameters = getRefactoringData().getMethodTarget().split(";");

		for (String methodAndParameters: methodsAndParameters){

			//String methodAndParameters = getRefactoringData().getMethodTarget();
			String methodName = methodAndParameters.substring(0, methodAndParameters.indexOf('(')).replaceAll("\\s","");
			if(methodName.equals("<init>")){
				methodName = className;
			}
			String[] parameters = methodAndParameters.substring(methodAndParameters.indexOf('(') +1,methodAndParameters.indexOf(')')).split(",");
			String[] parametersTypes = new String[parameters.length];
			for(int i=0; i < parameters.length; i++){
				// remove the white spaces
				parametersTypes[i] = Signature.getSimpleName(parameters[i].replaceAll("\\s",""));
				parametersTypes[i] = Signature.createTypeSignature(parametersTypes[i], false);
			}

			// Get the IProject from the projectName
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject project = root.getProject(getProjectName());

			try {
				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {

					// Get the IMethod
					IJavaProject javaProject = JavaCore.create(project);
					IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder(CodeSmellHandler.javasrc));
					IPackageFragment classPackage = rootpackage.getPackageFragment(packageName);
					ICompilationUnit classCU = classPackage.getCompilationUnit(className + ".java");
					IType type = classCU.getType(className);
					IMethod method = type.getMethod(methodName, parametersTypes);

					// check if the method has been found
					// if not search the method by name and number of parameters
					if(method==null || !method.exists()){
						IMethod[] methods = type.getMethods();
						method = null;
						int i = 0;
						while(method == null && i < methods.length){
							IMethod me = methods[i];
							if (me.getElementName().equals(methodName) && me.getNumberOfParameters() == parametersTypes.length) {
								method = me;
							}
							i++;
						}
					}

					// check if the method has been found
					if (method != null && method.exists()) {
						// Initialize the refactoring descriptor
						RefactoringContribution contribution = RefactoringCore
								.getRefactoringContribution(IJavaRefactorings.INTRODUCE_PARAMETER_OBJECT);
						IntroduceParameterObjectDescriptor descriptor = (IntroduceParameterObjectDescriptor) contribution
								.createDescriptor();
						String name = "New"+createNewClass(className);
/*						while(true) {
							name = "New"+createNewClass(className);
							ICompilationUnit targetClassCU = classPackage.getCompilationUnit(name + ".java");
							ASTParser parserClassChanged = ASTParser.newParser(AST.JLS8);
							parserClassChanged.setSource(targetClassCU);
							parserClassChanged.setKind(ASTParser.K_COMPILATION_UNIT);
							parserClassChanged.setResolveBindings(true);
							if (typeTarget==null)
								break;
						}
*/						descriptor.setClassName(name);
						descriptor.setParameterName("Object");
						descriptor.setPackageName(classPackage.getElementName());
						descriptor.setProject(javaProject.getElementName());
						descriptor.setMethod(method);
						descriptor.setGetters(true);
						descriptor.setSetters(true);
						// Create the classes needed for apply the refactoring
						IntroduceParameterObjectProcessor processor = new IntroduceParameterObjectProcessor(descriptor);
						Refactoring refactoring = new ProcessorBasedRefactoring(processor);
						IntroduceParameterObjectWizard wizard = new IntroduceParameterObjectWizard(processor, refactoring);
						RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
						IProgressMonitor monitor = new NullProgressMonitor();
						try {
							refactoring.checkInitialConditions(monitor);
							RefactoringStatus status = new RefactoringStatus();
							refactoring.checkFinalConditions(monitor);
							Change change = refactoring.createChange(monitor);
							change.initializeValidationData(monitor);
							change.perform(monitor);
						} catch (OperationCanceledException  | NullPointerException e) {
							//e.printStackTrace();
							saved.saving("IntroduceParameterObjectRefactoring", e.toString()+"\n"
									+ "src: "+packageAndClass+" method name:" + methodName );
						}

					} else{saved.saving("IntroduceParameterObjectRefactoring", "cannot be applied cause the argument is null "+
							"src: "+packageAndClass+" method name:" + methodName );
					}
				} else {
					throw new RefactoringException("Java Nature disabled");
				}
			} catch (CoreException e1 ) {
				e1.printStackTrace();
				throw new RefactoringException(e1.getMessage());
			} 
		}
	}
}
