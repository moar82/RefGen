package refaco.refactorings;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import refaco.RefactoringData;
import refaco.exceptions.RefactoringException;
import refaco.handlers.CodeSmellHandler;
import refaco.utils.CHtypeVisitor;
import refaco.utils.SaveInTextFile;

/**
 * Inline Class Refactoring 
 * @author Christian Kabulo (POLYMORSE)
 *
 */
public class InlineClassRefactoring extends refaco.refactorings.Refactoring{
	
	IJavaElement[] methodElements;
	IJavaElement[] fieldElements;
	 IField[] field;
	 IMethod[] method;
	SaveInTextFile saved;
	public InlineClassRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
		saved = new SaveInTextFile(_refactoringData, _projectName);
	}
	public void apply() throws RefactoringException {
		// Get the package and class name (Source)
				String tempSrcName = getRefactoringData().getClassSource();
				int index = tempSrcName.lastIndexOf('.');
				String classSourceName;
				String packageSourceName;
				if (index==-1){
					classSourceName = tempSrcName;
					packageSourceName = IPackageFragment.DEFAULT_PACKAGE_NAME;
				}
				else{
					packageSourceName = tempSrcName.substring(0, index);
					classSourceName = tempSrcName.substring(index + 1, tempSrcName.length());	
				}
				// Get the package and class name (Target)
				tempSrcName = getRefactoringData().getClassTarget();
				index = tempSrcName.lastIndexOf('.');
				String classTargetName;
				String packageTargetName;
				if (index==-1){
					classTargetName = tempSrcName;
					packageTargetName = IPackageFragment.DEFAULT_PACKAGE_NAME;
				}
				else{
				 packageTargetName = tempSrcName.substring(0, index);
				 classTargetName = tempSrcName.substring(index + 1, tempSrcName.length());
				}
				// Get the IProject from the projectName
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IProject project = root.getProject(getProjectName());
		try {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				// Get the IType
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder(CodeSmellHandler.javasrc));
				IPackageFragment classPackage = rootpackage.getPackageFragment(packageSourceName);
				ICompilationUnit srcClassICU = classPackage.getCompilationUnit(classSourceName + ".java");
				IType typeSource = srcClassICU.getType(classSourceName);
				// Get the Class Target
				IPackageFragment classTargetPackage = rootpackage.getPackageFragment(packageTargetName);
				ICompilationUnit tgtClassICU = classTargetPackage.getCompilationUnit(classTargetName + ".java");
				IType typeTarget = tgtClassICU.getType(classTargetName);
				if (typeSource != null) {
					ASTParser parser = ASTParser.newParser(AST.JLS8);
					parser.setSource(srcClassICU);
					parser.setKind(ASTParser.K_COMPILATION_UNIT);
			        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			        cu.accept(new ASTVisitor() {
						public boolean visit(MethodDeclaration node) {
							 try
							 {
								methodElements =  typeSource.getMethods();
								fieldElements = typeSource.getFields();
								method = new IMethod[methodElements.length];
								field =  new IField[fieldElements.length];	
								for(int i=0; i < method.length;++i)
								{
									if(methodElements[i].getElementType()==IJavaElement.METHOD)
									{			
										method[i] = (IMethod) methodElements[i];
									}
								}
								for(int i=0; i < field.length;++i)
								{
									if(fieldElements[i].getElementType()==IJavaElement.FIELD)
									{			
										field[i] = (IField) fieldElements[i];
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
					IProgressMonitor monitor = new NullProgressMonitor();
					boolean canDelete = false;
					if(field != null)
					{
						if( field.length > 0)
						{
							for (int j = 0; j < field.length ; ++j)
								if(field[j] !=null && field[j].exists())
									field[j].move(typeTarget, null, null, false, monitor);
						}
					}
					if(method != null)
					{
						if( method.length > 0)
						{
							for (int j = 0; j < method.length ; ++j)
								if(method[j] !=null)
									//if(!typeSource.hasChildren() )
									//{
										method[j].move(typeTarget, null, null, false, monitor);
										canDelete = true;
									//}

									/*else{
										saved.saving("InlineClassRefactoring", "InlineClassRefactoring cannot be applied cause there is only a constructor in the class ");
										canDelete = false;
									}*/
						}
						//for update method references (replace deleted child class with parent class type) 
						ASTRewrite rewrite;
						// Get the package and class name (Target)
						String tempTgtName = getRefactoringData().getClassTarget();
						IPackageFragment[] packages = javaProject.getPackageFragments();
				        for (IPackageFragment mypackage : packages) {
				            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				            	for (ICompilationUnit aICUnit : mypackage.getCompilationUnits()) {
				            		ASTParser parserClassChanged = ASTParser.newParser(AST.JLS8);
				            		parserClassChanged.setSource(aICUnit);
				            		parserClassChanged.setKind(ASTParser.K_COMPILATION_UNIT);
				            		parserClassChanged.setResolveBindings(true); // we need bindings later on
				            		CompilationUnit cuClassChanged = (CompilationUnit) parserClassChanged.createAST(null);
				            		cuClassChanged.recordModifications();
				            		rewrite = ASTRewrite.create(cuClassChanged.getAST());
				            		cuClassChanged.accept(new CHtypeVisitor(rewrite,classSourceName,classTargetName));
				            			Document doc= new Document(aICUnit.getSource());
				            			TextEdit edits = rewrite.rewriteAST(doc, null);
				            			if (edits.getLength()>0){
				            			try {
				            				edits.apply(doc);
				            			} catch (MalformedTreeException e) {
				            				e.printStackTrace();
				            			} catch (BadLocationException e) {
				            				e.printStackTrace();
				            			}
					            			aICUnit.getBuffer().setContents(doc.get());
					            			IImportDeclaration[] iImportDclaICUnit = aICUnit.getImports();
					            			boolean targetFound = false;
					            			for(int u = 0; u<iImportDclaICUnit.length;++u){
					            				String line = iImportDclaICUnit[u].toString().split(" ")[1];
					            				if(line.equals(tempSrcName))
					            					iImportDclaICUnit[u].delete(true, monitor);
					            				else if(line.equals(tempTgtName)){
					            					targetFound = true;							            					
					            				}
				            				}
					            			if(!targetFound)
					            				//if they belong to the same package skip
				            					if (mypackage.getElementName().equals(packageTargetName)==false)
				            						aICUnit.createImport(tempTgtName, null,monitor);
					            			aICUnit.save(null, true);
					            			aICUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
				            		}
				            	}

				            }
				        }
						//We need to add the packages of the deleted class to the target class 
				        IImportDeclaration[] sourceClassImports = srcClassICU.getImports();
				        IImportDeclaration[] targetClassImports = tgtClassICU.getImports();
				        if (typeTarget != null && sourceClassImports!=null) {
				        	parser.setSource(tgtClassICU);
				        	parser.setKind(ASTParser.K_COMPILATION_UNIT);
				        	//iterate imports of target class
				        	for(int u = 0; u<sourceClassImports.length;++u){
				        		IImportDeclaration srcDeclaration=  sourceClassImports[u];
				        		boolean importExists = false;
				        		for (int v=0; v<targetClassImports.length;++v){
				        			IImportDeclaration tgtDeclaration=  targetClassImports[v];
				        			if (srcDeclaration.equals(tgtDeclaration)){
				        				importExists=true;
				        				break;
				        			}
				        		}
				        		// this line does not work: ImportDeclaration sourceNode= ASTNodeSearchUtil.getImportDeclarationNode(srcDeclaration, tgtClassCU);
				        		if (importExists==false){
				        			String line = srcDeclaration.toString().split(" ")[1];
				        			tgtClassICU.createImport(line, null,monitor);
				        		}
				        	}
				        }
				        
						if(canDelete)
							srcClassICU.delete(true, monitor);
					}
					else{
						System.err.println("Class Empty");
						throw new RefactoringException("Class Empty");}	
		
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
