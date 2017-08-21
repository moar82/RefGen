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
import org.eclipse.jdt.core.Flags;
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
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor.Field;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInstanceMethodProcessor;
import org.eclipse.jdt.internal.corext.refactoring.structure.PullUpRefactoringProcessor;
import org.eclipse.jdt.internal.ui.refactoring.*;
import org.eclipse.jface.text.Document;
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
* @author Christian Kabulo (POLYMORSE)
*/
public class ExtractClassRefactoring extends refaco.refactorings.Refactoring {

	IMember[] members;
	IJavaElement[] javaElements;
	 IField[] field;
	 IMethod[] targetMethod;
	MoveMethodRefactoring move;
	SaveInTextFile saved;
	String methodName = null;
	List<String> test = new ArrayList<>();
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
		final java.util.Random rand = new java.util.Random();
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
						parameters = null;
				}catch(StringIndexOutOfBoundsException e){
					throw new RefactoringException("Method name format exception");
				}
				
				
				// Get the package and class name (Target)
				temp = getRefactoringData().getClassTarget();
				index = temp.lastIndexOf('.');
				String packageTargetName = temp.substring(0, index);
				String classTargetName = temp.substring(index + 1, temp.length());

		Expression expression = null;

		

		// Get the IProject from the projectName
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(getProjectName());
		// Get the IType
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder("source"));
		IPackageFragment classPackage = rootpackage.getPackageFragment(packageSourceName);
		ICompilationUnit classCU = classPackage.getCompilationUnit(classSourceName + ".java");
		IType typeSource = classCU.getType(classSourceName);
	
		// Get the Class Target
		IPackageFragment classTargetPackage = rootpackage.getPackageFragment(packageTargetName);
		ICompilationUnit classTargetCU = classTargetPackage.getCompilationUnit(classTargetName + ".java");
		IType typeTarget = classTargetCU.getType(classTargetName);
	
		String fieldNames;
		try {
			
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {

			
				// Get the name of all the fields
				List<String> nameFields = getRefactoringData().getFields();
				
				if (typeSource != null) {
					ASTParser parserTarget = ASTParser.newParser(AST.JLS8);
					parserTarget.setSource(classTargetCU);
					parserTarget.setKind(ASTParser.K_COMPILATION_UNIT);
					parserTarget.setResolveBindings(true); // we need bindings later on
			        final CompilationUnit cuTarget = (CompilationUnit) parserTarget.createAST(null);
			        cuTarget.accept(new ASTVisitor() {
						public boolean visit(MethodDeclaration node)
						{
							importValueTarget = node.getRoot().toString();
							return true;
						}
			        });
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
									field = new IField[javaElements.length];									
									for(int i=0; i < javaElements.length;++i)
									{
										if(javaElements[i].getElementType()==IJavaElement.FIELD)
										{
											members[i] = (IMember) javaElements[i];		
											field[i] = (IField) members[i];
										}
										else
										{								
											members[i] = (IMember) javaElements[i];	
											method[i] = (IMethod) javaElements[i];
										}
										
									}
									if (node.getName().equals(method)) {
					                    Block block = node.getBody();
					                    block.accept(new ASTVisitor() {
					                        public boolean visit(MethodInvocation node) {

					                        	Expression expression = node.getExpression();
					                            if (expression != null) {
					                              		test.add(expression.toString());
					                            }
					                            return true;
					                        }
					                    });
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
								field = new IField[javaElements.length];
								
								for(int i=0; i < javaElements.length;++i)
								{
									if(javaElements[i].getElementType()==IJavaElement.FIELD)
									{
										members[i] = (IMember) javaElements[i];		
										field[i] = (IField) members[i];
									}
									else
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
					refactoringExtract.checkInitialConditions(monitorExtract);
					RefactoringStatus statusExtract = new RefactoringStatus();
					refactoringExtract.checkFinalConditions(monitorExtract);
					Change change = refactoringExtract.createChange(monitorExtract);
					change.initializeValidationData(monitorExtract);
					change.perform(monitorExtract);

					if (members != null ) 
					{
						if( members.length > 0)
						{
							// Get the IMethod
							IMethod[] methods = null;
							try {
								methods = typeSource.getMethods();
							} catch (JavaModelException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				
							try {
								if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
									for(int u = 0; u<methods.length;u++){
											
										if (methods[u].getElementName().matches(methodName)) 
										{
											method_ = methods[u];
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
												refaco.refactorings.MoveInstanceMethodWizard wizard = new refaco.refactorings.MoveInstanceMethodWizard(
														processor, refactoring, targetArgument);
									
												RefactoringContribution contributionM = RefactoringCore
														.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD);
												MoveMethodDescriptor descriptorM = (MoveMethodDescriptor) contributionM.createDescriptor();
												RefactoringStatus status = new RefactoringStatus();
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
										
										
										else {
											saved.saving("ExtractClassRefactoring", "cannot be applied cause the method doesn't exist");
										}
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
				else 
				{
					System.err.println("Class Empty");
					throw new RefactoringException("Class Empty");
				} 

			}
					
		} 
		else {
				System.err.println("Class Empty");
				throw new RefactoringException("Class Empty");
			}
		} catch (CoreException e1) {
			e1.printStackTrace();
			throw new RefactoringException(e1.getMessage());
		}
	}
	public static String[] combineString(String[] first, String[] second){
        int length = first.length + second.length;
        String[] result = new String[length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
