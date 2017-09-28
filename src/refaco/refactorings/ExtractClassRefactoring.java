package refaco.refactorings;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInstanceMethodProcessor;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveStaticMembersProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;
import refaco.handlers.CodeSmellHandler;
import refaco.utils.SaveInTextFile;

/**
* Extract Class refactoring
* @author Christian Kabulo (POLYMORSE)
*/
public class ExtractClassRefactoring extends refaco.refactorings.Refactoring {

	IMember[] members;
	IJavaElement[] javaElements;
	 IField[] fields;
	 IMethod[] targetMethod;
	refaco.refactorings.Refactoring move;
	SaveInTextFile saved;
	String methodName = null;
	IMethod[] method = null;
	IMethod method_ = null;

	 String importValueSource;
	 String importValueTarget;
	public ExtractClassRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
		move = new MoveMethodRefactoring(_refactoringData, _projectName);
		saved = new SaveInTextFile(_refactoringData, _projectName);

	}

	public void apply() throws RefactoringException  {
		// Get the package and class name (Source)
				String temp = getRefactoringData().getClassSource();
				int index = temp.lastIndexOf('.');
				String packageSourceName = temp.substring(0, index);
				String classSourceName = temp.substring(index + 1, temp.length());
				
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
				temp = getRefactoringData().getClassTarget();
				index = temp.lastIndexOf('.');
				String packageTargetName = temp.substring(0, index);
				String classTargetName = temp.substring(index + 1, temp.length());

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
	
		try {
			
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {

				if (typeSource != null) {
					// Get the start and end position
					ASTParser parser = ASTParser.newParser(AST.JLS8);
					parser.setSource(classCU);
					parser.setKind(ASTParser.K_COMPILATION_UNIT);
					parser.setResolveBindings(true); // we need bindings later on
					final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
					cu.accept(new ASTVisitor() {
						public boolean visit(MethodDeclaration node)
						{
							importValueSource = node.getRoot().toString();
							// Get All the members
							try
							{
								javaElements =  typeSource.getChildren();
								members = new IMember[javaElements.length];
								method = new IMethod[javaElements.length];
								fields = new IField[javaElements.length];									
								for(int i=0; i < javaElements.length;++i)
								{
									if(javaElements[i].getElementType()==IJavaElement.FIELD)
									{
										members[i] = (IMember) javaElements[i];		
										fields[i] = (IField) members[i];
									}
									else if(javaElements[i].getElementType()==IJavaElement.METHOD)
									{								
										members[i] = (IMember) javaElements[i];	
										method[i] = (IMethod) javaElements[i];
									}

								}
							} 
							catch (Exception e) 
							{
								System.out.println("Error Cast");
							}

							return true;
						}		
						public boolean visit(MethodInvocation node)
						{
							// Get All the members
							try
							{

								javaElements =  typeSource.getChildren();
								members = new IMember[javaElements.length];
								method = new IMethod[javaElements.length];
								fields = new IField[javaElements.length];

								for(int i=0; i < javaElements.length;++i)
								{
									if(javaElements[i].getElementType()==IJavaElement.FIELD)
									{
										members[i] = (IMember) javaElements[i];		
										fields[i] = (IField) members[i];
									}
									else if(javaElements[i].getElementType()==IJavaElement.METHOD)
									{								
										members[i] = (IMember) javaElements[i];	
										method[i] = (IMethod) javaElements[i];
									}

								}
							} 
							catch (Exception e) 
							{
								System.out.println("Error Cast");
							}

							return true;
						}
					});	
					
					IPackageFragment classTargetPackage = rootpackage.getPackageFragment(packageTargetName);
					ICompilationUnit classTargetCU = classTargetPackage.getCompilationUnit(classTargetName + ".java");
					IType typeTarget = classTargetCU.getType(classTargetName);
					/*if (typeSource != null)
					{
						ASTParser parserTarget = ASTParser.newParser(AST.JLS8);
						parserTarget.setSource(classTargetCU);
						parserTarget.setKind(ASTParser.K_COMPILATION_UNIT);
						parserTarget.setResolveBindings(true); // we need bindings later on
						final CompilationUnit cuTarget = (CompilationUnit) parserTarget.createAST(null);
					}*/
					
					RefactoringContribution contributionExtract = RefactoringCore
							.getRefactoringContribution(IJavaRefactorings.EXTRACT_CLASS);
					ExtractClassDescriptor descriptorExtract = (ExtractClassDescriptor) contributionExtract.createDescriptor();
					descriptorExtract.setClassName(classTargetName);
					//descriptorExtract.setFields();
					descriptorExtract.setFieldName(classTargetName);
					descriptorExtract.setProject(javaProject.getElementName());
					descriptorExtract.setPackage(packageTargetName);
					descriptorExtract.setType(typeSource);
					Refactoring refactoringExtract = new org.eclipse.jdt.internal.corext.refactoring.structure.ExtractClassRefactoring(descriptorExtract);
					IProgressMonitor monitorExtract = new NullProgressMonitor();
					try {
						refactoringExtract.checkInitialConditions(monitorExtract);
						//RefactoringStatus statusExtract = new RefactoringStatus();
						refactoringExtract.checkFinalConditions(monitorExtract);
						Change change = refactoringExtract.createChange(monitorExtract);
						change.initializeValidationData(monitorExtract);
						change.perform(monitorExtract);
					} catch (OperationCanceledException | NullPointerException e2) {
						e2.printStackTrace();
					}

					if (members != null ) 
					{
						if( members.length > 0)
						{
							// Get the IMethod
							IMethod[] methods = null;
							try {
								methods = typeSource.getMethods();
							} catch (JavaModelException e) {
								e.printStackTrace();
							}
							boolean methodFound =false;
							try {
								if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
									for(int u = 0; u<methods.length;u++){
										if (methods[u].getElementName().matches(methodName)) 
										{
											//because we have qualified name from padl we clean both arrays
											String[] paramTypes =getParameterTypesOfActualMethod(methods[u]);
											String[] tmp_paramTypes =cleanQualifiedName(paramTypes);
											String[] tmp_parameters =cleanQualifiedName(parameters);
											if (Arrays.equals(tmp_paramTypes,tmp_parameters)) {
												method_ = methods[u];
												methodFound = true;
												//validate if the method is static
												if (Modifier.isStatic(method_.getFlags() ) ) {
													// method is static method
													IMember[] static_members = new IMember[1];
													static_members[0]=method_;
													//we need to call other processor.
													MoveStaticMembersProcessor processor = new MoveStaticMembersProcessor(static_members, new CodeGenerationSettings());
													processor.setDestinationTypeFullyQualifiedName(typeTarget.getFullyQualifiedName());
													Refactoring refactoring = new ProcessorBasedRefactoring(processor);
													// Set the target class
													IProgressMonitor monitorM = new NullProgressMonitor();
													refactoring.checkInitialConditions(monitorM);
													refactoring.checkFinalConditions(monitorM);
													//status = new RefactoringStatus();
													Change changeM = refactoring.createChange(monitorM);
													changeM.initializeValidationData(monitorM);
													changeM.perform(monitorM);
												} 

												else{											
													// Create the classes needed for apply the refactoring
													MoveInstanceMethodProcessor processor = new MoveInstanceMethodProcessor(method_, new CodeGenerationSettings());
													Refactoring refactoring = new ProcessorBasedRefactoring(processor);
													// Set the target class
													IProgressMonitor monitorM = new NullProgressMonitor();
													refactoring.checkInitialConditions(monitorM);
													IVariableBinding[] targets = processor.getPossibleTargets();
													IVariableBinding targetArgument = null;
													for(IVariableBinding target: targets){
														if (target.getType().getName().equals(descriptorExtract.getClassName())){
															processor.setTarget(target);
															targetArgument = target;
															break;
														}
													}
													if (targetArgument != null){
														/*refaco.refactorings.MoveInstanceMethodWizard wizard = new refaco.refactorings.MoveInstanceMethodWizard(
																processor, refactoring, targetArgument);*/
														/*RefactoringContribution contributionM = RefactoringCore
																.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD);*/
														//MoveMethodDescriptor descriptorM = (MoveMethodDescriptor) contributionM.createDescriptor();
														//RefactoringStatus status = new RefactoringStatus();
														refactoring.checkFinalConditions(monitorM);
														//status = new RefactoringStatus();
														Change changeM = refactoring.createChange(monitorM);
														changeM.initializeValidationData(monitorM);
														changeM.perform(monitorM);

													}
													else{
														//NOTHING TO DO
														saved.saving("ExtractClassRefactoring", "cannot be applied cause the target doesn't exist");
													}

												}
												break;
											} 
										}
									}
									if (!methodFound)
										System.out.println("Method "+ methodName + " Not found") ;
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
					else 
					{
						System.err.println("Class Empty");
						throw new RefactoringException("Class Empty");
					} 

				}

			} 
		
		} catch (CoreException e1) {
			e1.printStackTrace();
			throw new RefactoringException(e1.getMessage());
		}
	}
}
