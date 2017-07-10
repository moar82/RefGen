package refaco.refactorings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor.Field;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInstanceMethodProcessor;
import org.eclipse.jdt.internal.ui.refactoring.*;
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
import refaco.utils.SaveInTextFile;

/**
* Extract Class refactoring
*
*/
public class ExtractClassRefactoring extends refaco.refactorings.Refactoring {


	MoveMethodRefactoring move;
	SaveInTextFile saved;
	String methodName = null;
	List<String> test = new ArrayList<>();
	public ExtractClassRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
		move = new MoveMethodRefactoring(_refactoringData, _projectName);
		saved = new SaveInTextFile(_refactoringData, _projectName);

	}
	private static String createNewClass(String sourceClass) {
		//final java.util.Random rand = new java.util.Random();
        String newClassName = ( "Class"+ sourceClass );
        return newClassName;
    }

	public void apply() throws RefactoringException  {
		final java.util.Random rand = new java.util.Random();
		// Get the package and class name (Source)
		String temp = getRefactoringData().getClassSource();
		int index = temp.lastIndexOf('.');
		String packageSourceName = temp.substring(0, index);
		String classSourceName = temp.substring(index + 1, temp.length());
		Expression expression = null;

		

		// Get the IProject from the projectName
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(getProjectName());
		String fieldNames;
		try {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				
				// Get the IType
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder("src"));
				IPackageFragment classPackage = rootpackage.getPackageFragment(packageSourceName);
				ICompilationUnit classCU = classPackage.getCompilationUnit(classSourceName + ".java");
				IType typeSource = classCU.getType(classSourceName);
				System.out.println(classSourceName);
				// Get the method name and parameters
				String methodAndParameters = getRefactoringData().getMethodTarget();
				
				String[] parameters = null;
				try{
					methodName = methodAndParameters.substring(0, methodAndParameters.indexOf('(')).replaceAll("\\s","");
					if(methodName.equals("<init>")){
						methodName = classSourceName;
					}
					parameters = methodAndParameters.substring(methodAndParameters.indexOf('(') +1,methodAndParameters.indexOf(')')).split(",");
					if(parameters.length == 1 && parameters[0].length()==0)
						parameters = null;
				}catch(StringIndexOutOfBoundsException e){
					throw new RefactoringException("Method name format exception");
				}
				// Get the name of all the fields
				List<String> nameFields = getRefactoringData().getFields();
				
				if (typeSource != null) {
					// Get the start and end position
					ASTParser parser = ASTParser.newParser(AST.JLS8);
				//    parser.setKind(ASTParser.K_COMPILATION_UNIT);
				    parser.setSource(classCU);
		        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

				cu.accept(new ASTVisitor() {
		            public boolean visit(MethodDeclaration node) {
		                if (node.getName().getIdentifier().equals(methodName)) {
		                    Block block = node.getBody();
		                    block.accept(new ASTVisitor() {
		                        public boolean visit(MethodInvocation node) {
		                          //  System.out.println("Name: " + node.getName());

		                        	Expression expression = node.getExpression();
		                            if (expression != null) {
		                              		test.add(expression.toString());
		                            }
		                            return true;
		                        }
		                    });
		                }
						return true;
		            }
		        });

			
					// Initialize the refactoring descriptor
					RefactoringContribution contribution = RefactoringCore
							.getRefactoringContribution(IJavaRefactorings.EXTRACT_CLASS);
					ExtractClassDescriptor descriptor = (ExtractClassDescriptor) contribution.createDescriptor();
					String name = "Extracted"+rand.nextInt(15)+createNewClass(classSourceName);
					String fieldName = "fieldEx"+rand.nextInt(15);
					descriptor.setClassName(name);
					descriptor.setFieldName(fieldName);
					descriptor.setProject(javaProject.getElementName());
					descriptor.setPackage(classPackage.getElementName());
					descriptor.setType(typeSource);
					boolean atLeastOne = false;
					boolean done = false;
					// Set the fields in the descriptor
					Field[] allFields = ExtractClassDescriptor.getFields(typeSource);
					Method[] met = ExtractClassDescriptor.getFields(typeSource).getClass().getMethods();

					for(int i =0; i < allFields.length; i++){
						if(test.contains(allFields[i].getFieldName())){
							System.out.println("done::"+allFields[i]);
							allFields[i].setCreateField(true);
							allFields[i].isCreateField();
							Field field= allFields[i];
							 fieldNames= field.getFieldName();
							//IField declField= type.getField(fieldName);
							atLeastOne = true;
						}else{
							allFields[i].setCreateField(false);
						}
					}
					if(atLeastOne){
								
					descriptor.setFields(allFields);

					// Create the classes needed for apply the refactoring
					Refactoring refactoring = new org.eclipse.jdt.internal.corext.refactoring.structure.ExtractClassRefactoring(descriptor);
				//	ExtractClassWizard wizard = new ExtractClassWizard(descriptor, refactoring);
					//RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
					IProgressMonitor monitor = new NullProgressMonitor();
					refactoring.checkInitialConditions(monitor);
					RefactoringStatus status = new RefactoringStatus();
					refactoring.checkFinalConditions(monitor);
					//status = new RefactoringStatus();
					Change change = refactoring.createChange(monitor);
					change.initializeValidationData(monitor);
					change.perform(monitor);
					done = true;
										// Execute the refactoring
					//op.run(Display.getDefault().getActiveShell(), "");
					}else{
						saved.saving("ExtracteClassRefactoring", "cannot be extracted cause the argument is "
								+ "already extracted in another class.");
					}
					if(done){

				
						
						// Get the package and class name (Target)
						temp = getRefactoringData().getClassTarget();
						index = temp.lastIndexOf('.');
						String classTargetName = temp.substring(index + 1, temp.length());
						
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
									if (me.getElementName().equals(methodName) && (parameters == null && me.getNumberOfParameters()==0)
											|| (parameters!=null && me.getNumberOfParameters() == parameters.length)) {
										method = me;
									}
									i++;
								}
										
									if (method != null && method.exists()) {

										// Create the classes needed for apply the refactoring
										MoveInstanceMethodProcessor processor = new MoveInstanceMethodProcessor(method, new CodeGenerationSettings());
										Refactoring refactoring = new ProcessorBasedRefactoring(processor);
										// Set the target class
										//refactoring.checkInitialConditions(null);
										IProgressMonitor monitorM = new NullProgressMonitor();
										refactoring.checkInitialConditions(monitorM);
										IVariableBinding[] targets = processor.getPossibleTargets();
										IVariableBinding targetArgument = null;
									
										for(IVariableBinding target: targets){
											//System.out.println("kajik");

										 if (target.getType().getName().equals(descriptor.getClassName())){
												processor.setTarget(target);
												targetArgument = target;
												break;
											}
										 
										}
										if (targetArgument != null){
											refaco.refactorings.MoveInstanceMethodWizard wizard = new refaco.refactorings.MoveInstanceMethodWizard(
													processor, refactoring, targetArgument);
								
											RefactoringContribution contributionM = RefactoringCore
													.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD);
											MoveMethodDescriptor descriptorM = (MoveMethodDescriptor) contributionM.createDescriptor();
											RefactoringStatus status = new RefactoringStatus();
											refactoring.checkFinalConditions(monitorM);
											//status = new RefactoringStatus();
											Change change = refactoring.createChange(monitorM);
											change.initializeValidationData(monitorM);
											change.perform(monitorM);
											//in visual studio code the part missing
										}
										else{
											
										}
											//TODO LOG ERROR OF REFACTORING THAT COULD NOT BE APPLIED
											
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
						}//here
					}
				} else {
					System.err.println("Class Empty");
					throw new RefactoringException("Class Empty");
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
