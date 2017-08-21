package refaco.refactorings;

import java.lang.reflect.Array;
//import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
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
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceManipulation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ITrackedNodePosition;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.EncapsulateFieldDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.PullUpDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.PushDownDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor.Field;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.changes.DynamicValidationStateChange;
import org.eclipse.jdt.internal.corext.refactoring.reorg.MoveCuUpdateCreator;
import org.eclipse.jdt.internal.corext.refactoring.structure.HierarchyProcessor;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInstanceMethodProcessor;
import org.eclipse.jdt.internal.corext.refactoring.structure.PullUpRefactoringProcessor;
import org.eclipse.jdt.internal.corext.refactoring.structure.PushDownRefactoringProcessor;
import org.eclipse.jdt.internal.corext.refactoring.util.TextChangeManager;
import org.eclipse.jdt.internal.corext.refactoring.util.TextEditBasedChangeManager;
import org.eclipse.jdt.internal.ui.refactoring.PushDownWizard;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextEditBasedChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.internal.core.refactoring.RefactoringCoreMessages;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;

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
			String fieldNames;
			try {
					if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) 
					{
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
						Class clas = typeTarget.getClass();
						int modifiers = clas.getModifiers();
						System.out.println( Modifier.isStatic(modifiers));
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
									System.out.println("test:"+ node.modifiers());
									System.out.println(node.getModifiers());
									System.out.println(node.getFlags());
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
											targetJavaElements = typeTarget.getChildren();
											members = new IMember[javaElements.length];
											method = new IMethod[javaElements.length];
											field = new IField[javaElements.length];
											targetMethod = new IMethod[targetJavaElements.length];
											
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
											for(int i=0; i < targetJavaElements.length;++i)
											{
												if(targetJavaElements[i].getElementType()==IJavaElement.METHOD)							
													targetMethod[i] = (IMethod) targetJavaElements[i];		
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
										targetMethod = new IMethod[targetJavaElements.length];
										
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
										for(int i=0; i < targetJavaElements.length;++i)
										{
											if(targetJavaElements[i].getElementType()==IJavaElement.METHOD)							
												targetMethod[i] = (IMethod) targetJavaElements[i];		
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
											PullUpRefactoringProcessor processor = new org.eclipse.jdt.internal.corext.refactoring.structure.PullUpRefactoringProcessor(
													members, new CodeGenerationSettings());
											Refactoring refactoring = new ProcessorBasedRefactoring(processor);
											IProgressMonitor monitor = new NullProgressMonitor();
											RefactoringStatus status = new RefactoringStatus();								
											processor.setDestinationType(typeTarget);
											RefactoringContribution contribution = RefactoringCore
													.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD);
											MoveMethodDescriptor descriptor = (MoveMethodDescriptor) contribution.createDescriptor();											List<String> parsingImport = null;
											List<String>  parsingImportT = new ArrayList<>();
											String[] sourceImport =  importValueSource.split(";");
											String[] targetImport = importValueTarget.split(";");
											String[] tempo = new String[parsingImportT.size()];
											for(int t = 0;t<sourceImport.length;++t)
											if(sourceImport[t].contains("import"))
											{
												parsingImportT.add(sourceImport[t]);
											}
											tempo=parsingImportT.toArray(new String[0]);
												targetImport = combineString(tempo,targetImport);
												Document document = null;
												for(int i = 0;i<targetImport.length;++i)
												{
													if(targetImport[i].contains("import"))
													{
														String[] valueImportTar = targetImport[i].split(";");
														String[] line = valueImportTar[0].split(" ");
														classTargetCU.createImport(line[1], null, monitor);
													}
												}
												System.out.println(classTargetCU.getElementName());
												classTargetCU.rename("URLDocument", false, monitor);
												  AST ast = AST.newAST(AST.JLS3);
												  CompilationUnit unit = ast.newCompilationUnit();
												  TypeDeclaration type = ast.newTypeDeclaration();
												  type.setInterface(false);
												  type.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
												  type.setName(ast.newSimpleName(classTargetName));

												  
												System.out.println(typeTarget.getFlags());
												boolean isAbstract = Flags.isAbstract(typeTarget.getFlags());
												Flags.toString(typeTarget.getFlags()).replace("public Abstract", "public");
												System.out.println("Abstract:"+descriptor.getFlags());
											IMethod[] same = new IMethod[javaElements.length];
											for (int j = 0; j < field.length ; ++j)
												if(field[j] !=null)
													field[j].move(typeTarget, null, null, false, monitor);
											for (int j = 0; j < method.length ; ++j)
												if(method[j] !=null )
													//if(!method[j].isConstructor())
													{
														same = typeTarget.findMethods(method[j]);
														if(same != null)
															for(int k = 0; k < same.length ; ++k)
																same[k].delete(true, monitor);
														method[j].move(typeTarget, null, null, true, monitor);
													}
													/*else
														method[j].delete(true, monitor);*/
												processor.setDeletedMethods(method);												
												typeSource.getPackageFragment();
											System.out.println(typeTarget.getFlags());
												classCU.delete(true, monitor);
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