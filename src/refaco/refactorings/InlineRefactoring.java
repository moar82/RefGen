package refaco.refactorings;

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
import org.eclipse.jdt.core.IJavaProject;
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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.InlineMethodDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineMethodRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineMethodRefactoring.Mode;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInstanceMethodProcessor;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
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

public class InlineRefactoring extends refaco.refactorings.Refactoring{
	
	public InlineRefactoring(RefactoringData _refactoringData, String _projectName) {
		super(_refactoringData, _projectName);
	}
	
	
	@SuppressWarnings("restriction")
	public void apply() throws RefactoringException {
		// Get the package and class name (Source)
				String temp = getRefactoringData().getClassSource();
				int index = temp.lastIndexOf('.');
				String packageSourceName = temp.substring(0, index);
				String classSourceName = temp.substring(index + 1, temp.length());

				// Get the IProject
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IProject project = root.getProject(getProjectName());
				
				try {
					if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
						
						// Get the ICompilationUnit
						IJavaProject javaProject = JavaCore.create(project);
						IPackageFragmentRoot rootpackage = javaProject.getPackageFragmentRoot(project.getFolder("src"));
						IPackageFragment classPackage = rootpackage.getPackageFragment(packageSourceName);
						ICompilationUnit classCU = classPackage.getCompilationUnit(classSourceName + ".java");

						if (classCU != null) {
							
							// Get the start and end position
							ASTParser parser = ASTParser.newParser(AST.JLS8);
						    parser.setKind(ASTParser.K_COMPILATION_UNIT);
						    parser.setSource(classCU);
						    final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
						    int fstart = cu.getPosition(getRefactoringData().getSelectionStart(), 0);
						    int fend = cu.getPosition(getRefactoringData().getSelectionStart() + getRefactoringData().getSelectionLength(),0);
						    
						    if(fstart >= 0 && fend >=0){
						    	// Create the classes needed for apply the refactoring
								//org.eclipse.jdt.internal.corext.refactoring.code.InlineTempRefactoring refactoring =
									//	new org.eclipse.jdt.internal.corext.refactoring.code.InlineTempRefactoring(classCU , fstart, fend - fstart);
								
								//InlineRefactoring wizard = new InlineRefactoring(refactoring);
								
								//RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
						    	
								RefactoringContribution contribution =
									    RefactoringCore.getRefactoringContribution(IJavaRefactorings.INLINE_METHOD);
								
								InlineMethodDescriptor descriptor = (InlineMethodDescriptor) contribution.createDescriptor();
								
								InlineMethodRefactoring refactoring= InlineMethodRefactoring.create(classCU, new RefactoringASTParser(ASTProvider.SHARED_AST_LEVEL).parse(classCU, true), fstart, fend - fstart);
								refactoring.setDeleteSource(true);
								refactoring.setCurrentMode(Mode.INLINE_ALL); // or INLINE SINGLE based on the user's intervention

								
								descriptor.setProject(classCU.getResource().getProject().getName());
								
								
								IProgressMonitor monitor = new NullProgressMonitor();
								RefactoringStatus status = new RefactoringStatus();
								//  Refactoring refactoring = descriptor.createRefactoring(status);

								refactoring.checkFinalConditions(monitor);
								status = new RefactoringStatus();
								Change change = refactoring.createChange(monitor);
								change.initializeValidationData(monitor);
								change.perform(monitor);
								// Execute the refactoring
							//	op.run(Display.getDefault().getActiveShell(), "");
								
						    }else{
						    	throw new RefactoringException("Invalid selection");
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
