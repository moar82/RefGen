package refaco.utils;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class CHtypeVisitor extends ASTVisitor {

	private ASTRewrite rewrite;
	private String childTypeName;
	private String targetTypeName;
	private IMethod[] methods;
	/**
	 * @param rewrite
	 * @param childTypeName
	 * @param targetTypeNewName
	 * @param methods methods that were transfered to the targettype
	 */
	public CHtypeVisitor(ASTRewrite rewrite, String childTypeName,String targetTypeNewName, IMethod[] methods) {
		super();
		this.rewrite = rewrite;
		this.childTypeName = childTypeName;
		this.targetTypeName = targetTypeNewName;
		this.methods = methods;
	}

	@Override
	public void endVisit(MethodInvocation node) {
		if (methods==null)//IN CH this is not necessary but in IC it is
			return;
		for (IMethod aMethod: methods){
			if (aMethod.getElementName().equals(node.getName().getIdentifier())){
				AST ast= rewrite.getAST();
				SimpleName parentName = ast.newSimpleName(targetTypeName);
				//node.setExpression(parentName);
				rewrite.set(node, MethodInvocation.EXPRESSION_PROPERTY, parentName,null);
			}
		}
	}

	@Override
	public boolean visit(SimpleType node) {
		if (node.toString().equals(childTypeName)) {
	        // 1
	        AST ast= rewrite.getAST();
	    	SimpleName parentName = ast.newSimpleName(targetTypeName);
	        rewrite.replace(node,parentName, null);
	    }
	    return true;
	}

	/*@Override
	public void endVisit(ClassInstanceCreation node) {
		 AST ast= rewrite.getAST();
	    	SimpleName parentName = ast.newSimpleName(targetTypeName);
	        
		node.setType(typeTarget);
		rewrite.replace(node,parentName, null);
		SimpleType parentType =  targetType.getTypes();
		System.out.println(node.toString());
	}*/

	/*@Override
	public void endVisit(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		//super.endVisit(node);
	}
	*/
	
	
	
}
