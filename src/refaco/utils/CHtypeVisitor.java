package refaco.utils;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class CHtypeVisitor extends ASTVisitor {

	private ASTRewrite rewrite;
	private String childTypeName;
	private TypeDeclaration targetType;
	private String targetTypeName;
	private IType typeTarget;
	private boolean isThereAChange;

	
	public CHtypeVisitor(ASTRewrite rewrite, String childTypeName, TypeDeclaration targetType,String targetTypeNewName, IType typeTarget, boolean isThereAChange) {
		super();
		this.rewrite = rewrite;
		this.childTypeName = childTypeName;
		this.targetType = targetType;
		this.targetTypeName = targetTypeNewName;
		this.typeTarget = typeTarget;
		this.isThereAChange=isThereAChange;
	}

	@Override
	public boolean visit(SimpleType node) {
		if (node.toString().equals(childTypeName)) {
	        // 1
	        AST ast= rewrite.getAST();
	    	SimpleName parentName = ast.newSimpleName(targetTypeName);
	        rewrite.replace(node,parentName, null);
	        isThereAChange=true;
	    }
	    return true;
	}

	@Override
	public void endVisit(ClassInstanceCreation node) {
		// AST ast= rewrite.getAST();
	    	//SimpleName parentName = ast.newSimpleName(targetTypeName);
	        
		//node.setType(typeTarget);
		//rewrite.replace(node,parentName, null);
		/*SimpleType parentType =  targetType.getTypes();
		System.out.println(node.toString());*/
	}

	@Override
	public void endVisit(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		//super.endVisit(node);
	}
	
	
	
	
}
