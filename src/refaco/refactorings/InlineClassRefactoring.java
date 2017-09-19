package refaco.refactorings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.InlineMethodDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.MoveDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.PushDownDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractClassDescriptor.Field;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineMethodRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineMethodRefactoring.Mode;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInstanceMethodProcessor;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.refactoring.ExtractClassWizard;
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
		
		final java.util.Random rand = new java.util.Random();
		// Get the package and class name (Source)
		// Get the package and class name (Source)
				String temp = getRefactoringData().getClassSource();
				int index = temp.lastIndexOf('.');
				String packageSourceName = temp.substring(0, index);
				String classSourceName = temp.substring(index + 1, temp.length());

				// Get the package and class name (Target)
				temp = getRefactoringData().getClassTarget();
				index = temp.lastIndexOf('.');
				String packageTargetName = temp.substring(0, index);
				String classTargetName = temp.substring(index + 1, temp.length());

				// Get the IProject from the projectName
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IProject project = root.getProject(getProjectName());
		String fieldNames;
		try {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				
				// Get the IType
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder(CodeSmellHandler.javasrc));
				IPackageFragment classPackage = rootpackage.getPackageFragment(packageSourceName);
				ICompilationUnit classCU = classPackage.getCompilationUnit(classSourceName + ".java");
				IType typeSource = classCU.getType(classSourceName);
				// Get the Class Target
				IPackageFragment classTargetPackage = rootpackage.getPackageFragment(packageTargetName);
				ICompilationUnit classTargetCU = classTargetPackage.getCompilationUnit(classTargetName + ".java");
				IType typeTarget = classTargetCU.getType(classTargetName);
				if (typeSource != null) {
					ASTParser parser = ASTParser.newParser(AST.JLS8);
					parser.setSource(classCU);
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
								if(field[j] !=null && !field[j].exists())
									field[j].move(typeTarget, null, null, false, monitor);
						}
					}
					
					if(method != null)
					{
						if( method.length > 0)
						{
							for (int j = 0; j < method.length ; ++j)
								if(method[j] !=null)
									if(!typeSource.hasChildren() )
									{
										method[j].move(typeTarget, null, null, false, monitor);
										canDelete = true;
									}

									else{
										saved.saving("InlineClassRefactoring", "InlineClassRefactoring cannot be applied cause there is only a constructor in the class ");
										canDelete = false;
									}
						}
						if(canDelete)
						classCU.delete(true, monitor);
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
