package refaco.utils;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class CHtypeVisitor extends ASTVisitor {

	private ASTRewrite rewrite;
	private String childTypeName;
	private TypeDeclaration targetType;
	private String targetTypeName;
	
	public CHtypeVisitor(ASTRewrite rewrite, String childTypeName, TypeDeclaration targetType,String targetTypeNewName) {
		super();
		this.rewrite = rewrite;
		this.childTypeName = childTypeName;
		this.targetType = targetType;
		this.targetTypeName = targetTypeNewName;
	}

	@Override
	public boolean visit(SimpleType node) {
		if (node.toString().equals(childTypeName)) {
	        System.out.println("child type detected: "
	                + node.getStartPosition());
	        // 1
	        AST ast= rewrite.getAST();
	    	SimpleName NewName = ast.newSimpleName(targetTypeName);
	        rewrite.replace(node,NewName, null);
	    }
	    return true;
	}
	
}
