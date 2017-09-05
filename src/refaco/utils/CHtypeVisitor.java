package refaco.utils;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class CHtypeVisitor extends ASTVisitor {

	private ASTRewrite rewrite;
	private String childTypeName;
	private TypeDeclaration targetType;
	
	public CHtypeVisitor(ASTRewrite rewrite, String childTypeName, TypeDeclaration targetType) {
		super();
		this.rewrite = rewrite;
		this.childTypeName = childTypeName;
		this.targetType = targetType;
	}

	@Override
	public boolean visit(SimpleType node) {
		if (node.toString().equals(childTypeName)) {
	        System.out.println("child type detected: "
	                + node.getStartPosition());
	        // 1
	        rewrite.replace(node,
	        		rewrite.getAST().newPrimitiveType(PrimitiveType.INT), null);
	    }
	    return true;
	}

}
