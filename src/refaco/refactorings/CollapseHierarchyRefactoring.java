package refaco.refactorings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.structure.PullUpRefactoringProcessor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;
import refaco.handlers.CodeSmellHandler;

/**
 * Collapse Hiearchy refactoring 
 * @author Christian Kabulo (POLYMORSE)
 *
 */
public class CollapseHierarchyRefactoring extends refaco.refactorings.Refactoring {


	IMember[] members;
	IJavaElement[] javaElements;
	IJavaElement[] targetJavaElements;
	 IField[] field;
	 IMethod[] targetMethod;
	 IMethod[] method;
	 String importValueSource;
	 String importValueTarget;

	 public CollapseHierarchyRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
	}

		public void apply() throws RefactoringException {
	
			// Get the package and class name (Source)
			String temp = getRefactoringData().getClassTarget();
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
				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) 
				{
					// Get the src package
					IJavaProject javaProject = JavaCore.create(project);
					IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder(CodeSmellHandler.javasrc));
					// Get the Class Source
					IPackageFragment classPackage = rootpackage.getPackageFragment(packageSourceName);
					ICompilationUnit classCU = classPackage.getCompilationUnit(classSourceName + ".java");						
					IType typeSource = classCU.getType(classSourceName);

					// Get the Class Target
					IPackageFragment classTargetPackage = rootpackage.getPackageFragment(packageTargetName);
					ICompilationUnit classTargetCU = classTargetPackage.getCompilationUnit(classTargetName + ".java");
					IType typeTarget = classTargetCU.getType(classTargetName);
					if (typeSource != null)
					{
						ASTParser parserTarget = ASTParser.newParser(AST.JLS8);
						parserTarget.setSource(classTargetCU);
						parserTarget.setKind(ASTParser.K_COMPILATION_UNIT);
						parserTarget.setResolveBindings(true); // we need bindings later on
						final CompilationUnit cuTarget = (CompilationUnit) parserTarget.createAST(null);
						cuTarget.accept(new ASTVisitor() {
							public boolean visit(MethodDeclaration node)
							{
								importValueTarget = node.getRoot().toString();

								try {
									targetJavaElements = typeTarget.getChildren();
									targetMethod = new IMethod[targetJavaElements.length];

									for(int i=0; i < targetJavaElements.length;++i)
									{
										if(targetJavaElements[i].getElementType()==IJavaElement.METHOD)							
											targetMethod[i] = (IMethod) targetJavaElements[i];		
									}
								} catch (JavaModelException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}


								return true;
							}
							public boolean visit(MethodInvocation node)
							{
								try {
									targetJavaElements = typeTarget.getChildren();
									targetMethod = new IMethod[targetJavaElements.length];

									for(int i=0; i < targetJavaElements.length;++i)
									{
										if(targetJavaElements[i].getElementType()==IJavaElement.METHOD)							
											targetMethod[i] = (IMethod) targetJavaElements[i];		
									}
								} catch (JavaModelException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}


								return true;
							}
						});

						ASTParser parser = ASTParser.newParser(AST.JLS8);
						parser.setSource(classCU);
						parser.setKind(ASTParser.K_COMPILATION_UNIT);
						parser.setResolveBindings(true); // we need bindings later on
						final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
						cu.accept(new ASTVisitor() {
							public boolean visit(MethodDeclaration node)
							{
								// Get All the members
								importValueSource = node.getRoot().toString();
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
							public boolean visit(MethodInvocation node)
							{
								// Get All the members
								try
								{

									javaElements =  typeSource.getChildren();
									targetJavaElements = typeTarget.getChildren();
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

						if (members != null ) 
						{
							if( members.length > 0)
							{

								AST ast = cuTarget.getAST();
								// create the descriptive ast rewriter
								ASTRewrite rewrite= ASTRewrite.create(ast);
								TypeDeclaration typeDecla = (TypeDeclaration) cuTarget.types().get(0);
								String[] splitin = typeDecla.toString().split("\n");

								for(int t = 0;t<splitin.length;++t)
									if(splitin[t].contains("abstract") && typeDecla.modifiers().toString().contains("abstract"))
									{

										Object[] array = typeDecla.modifiers().toArray();
										int indexModi = -1;
										for (int i=0;i<array.length;i++){
											String modifi = array[i].toString();
											if (modifi.equals("abstract")){
												indexModi = i;
												break;
											}
										}
										array = null;
										typeDecla.modifiers().remove(indexModi);
										// apply the text edits to the compilation unit
										try {
											Document documents= new Document(typeDecla.toString());
											TextEdit res= rewrite.rewriteAST();
											res.apply(documents);
											classTargetCU.getBuffer().setContents(documents.get());

										} catch (MalformedTreeException e) {
											e.printStackTrace();
										} catch (BadLocationException e) {
											e.printStackTrace();
										}
									}

								PullUpRefactoringProcessor processor = new org.eclipse.jdt.internal.corext.refactoring.structure.PullUpRefactoringProcessor(
										members, new CodeGenerationSettings());
								new ProcessorBasedRefactoring(processor);
								IProgressMonitor monitor = new NullProgressMonitor();
								new RefactoringStatus();								
								processor.setDestinationType(typeTarget);
								RefactoringContribution contribution = RefactoringCore
										.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD);
								contribution.createDescriptor();											
								List<String>  parsingImportT = new ArrayList<>();
								String[] sourceImport =  importValueSource.split(";");
								String[] targetImport = importValueTarget.split(";");
								String[] tempo = new String[parsingImportT.size()];
								for(int t = 0;t<sourceImport.length;++t)
									if(sourceImport[t].contains("import") || sourceImport[t].contains("package"))
									{
										parsingImportT.add(sourceImport[t]);
									}

								for (int j = 0; j < field.length ; ++j)
									if(field[j] !=null)
										field[j].move(typeTarget, null, null, false, monitor);
								for (int j = 0; j < method.length ; ++j)
								{
									if(method[j] !=null )
									{

										typeTarget.findMethods(method[j]);

										for(int r = 0; r < targetMethod.length ; ++r)
											if( targetMethod[r]!=null && targetMethod[r].getElementName() == method[j].getElementName())
												targetMethod[r].delete(true, monitor);

										method[j].move(typeTarget, null, null, false, monitor);

									}
									//method[j].move(typeTarget, null, null, true, monitor);
									/*MoveInstanceMethodProcessor processorM = new MoveInstanceMethodProcessor(method[j], new CodeGenerationSettings());
														Refactoring refactoringM = new ProcessorBasedRefactoring(processorM);
														// Set the target class
														IProgressMonitor monitorM = new NullProgressMonitor();
														refactoring.checkInitialConditions(monitorM);
														IVariableBinding[] targets = processorM.getPossibleTargets();
														IVariableBinding targetArgument = null;

														for(IVariableBinding target: targets){
														 if (target.getType().getName().equals(descriptor.getClass().getName())){
																processorM.setTarget(target);
																targetArgument = target;
																break;
															}

														}*/
									/*if (targetArgument != null){
															refaco.refactorings.MoveInstanceMethodWizard wizard = new refaco.refactorings.MoveInstanceMethodWizard(
																	processorM, refactoringM, targetArgument);

															RefactoringContribution contributionM = RefactoringCore
																	.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD);
															MoveMethodDescriptor descriptorM = (MoveMethodDescriptor) contributionM.createDescriptor();
															RefactoringStatus statusM = new RefactoringStatus();
															refactoring.checkFinalConditions(monitorM);
															//status = new RefactoringStatus();
															Change changeM = refactoring.createChange(monitorM);
															changeM.initializeValidationData(monitorM);
															changeM.perform(monitorM);
														}*/
								}
								/*else
														method[j].delete(true, monitor);*/
								//processor.setDeletedMethods(method);	



								tempo=parsingImportT.toArray(new String[0]);
								targetImport = combineString(tempo,targetImport);
								for(int i = 0;i<targetImport.length;++i)
								{
									if(targetImport[i].contains("package"))
									{
										String[] valueImportTar = targetImport[i].split(";");
										String[] line = valueImportTar[0].split(" ");
										classTargetCU.createPackageDeclaration(line[1], monitor);
									}
									if(targetImport[i].contains("import") )
									{
										String[] valueImportTar = targetImport[i].split(";");
										String[] line = valueImportTar[0].split(" ");
										classTargetCU.createImport(line[1], null, monitor);
									}

								}

								/*RenameCompilationUnitProcessor processorR = new RenameCompilationUnitProcessor(classCU);
												processorR.setUpdateReferences(true);
												processorR.setNewElementName(classTargetName);
												RefactoringContribution contributionR = RefactoringCore
														.getRefactoringContribution(IJavaRefactorings.RENAME_COMPILATION_UNIT);
												RenameJavaElementDescriptor descriptorR = (RenameJavaElementDescriptor) contributionR.createDescriptor();
												Refactoring fRefactoring = new  ProcessorBasedRefactoring(processorR);
												RefactoringStatus statusR = new RefactoringStatus();

												//  final PerformRefactoringOperation operation = new PerformRefactoringOperation(fRefactoring, CheckConditionsOperation.ALL_CONDITIONS);
												try {
													//fRefactoring.checkFinalConditions(monitor);
													Change fChange= fRefactoring.createChange(monitor);
													//fChange.initializeValidationData(monitor);
													fChange.perform(monitor);
												} catch (Exception e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													System.out.println("error");
												}*/
								classCU.delete(true, monitor);

								/*RenameCompilationUnitProcessor processorR = new RenameCompilationUnitProcessor(classCU);
												processorR.setUpdateReferences(true);
												processorR.setNewElementName(classTargetName+".java");*/
								/*RenameRefactoring fRefactoring = new RenameRefactoring(processorR);
												RefactoringStatus statusR = new RefactoringStatus();
												fRefactoring.checkFinalConditions(monitor);
												//  final PerformRefactoringOperation operation = new PerformRefactoringOperation(fRefactoring, CheckConditionsOperation.ALL_CONDITIONS);
												Change fChange= fRefactoring.createChange(monitor);
												fChange.initializeValidationData(monitor);
												fChange.perform(monitor);*/

							}
						}
						else 
						{
							System.err.println("Class Empty");
							throw new RefactoringException("Class Empty");
						} 
					}
					else 
					{
						System.err.println("Class Empty");
						throw new RefactoringException("Class Empty");
					} 
				} 
				else 
				{
					System.err.println("Nature disabled");
					throw new RefactoringException("Java Nature disabled");
				}
			} 
			catch (CoreException e1) 
			{
				throw new RefactoringException(e1.getMessage());
			}
	
		}

		void setSuperClass(TypeDeclaration typeDecl, String qualifiedName) {
		    AST ast = typeDecl.getAST();
		    Name name = ast.newName(qualifiedName);
		    Type type = ast.newSimpleType(name);
		    typeDecl.setSuperclassType(type);
		}
		public static String[] combineString(String[] first, String[] second){
	        int length = first.length + second.length;
	        String[] result = new String[length];
	        System.arraycopy(first, 0, result, 0, first.length);
	        System.arraycopy(second, 0, result, first.length, second.length);
	        return result;
	    }
}