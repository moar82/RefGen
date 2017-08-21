package refaco;

import java.util.List;

import org.eclipse.jdt.core.IType;

/**
 * 	This class contain all the values needed for apply a refactoring operation except the project.
 *  All the fields aren't necessary for each refactoring
 */
public class RefactoringData {

	private RefactoringType refactoringType;
	private String classSource;					
	private String classTarget;
	private String methodTarget;
	private List<String> fields;			// it's used for extract class refactoring
	private String argument;				// it's used for remove parameter refactoring
	private int selectionStart;				// line of start selection (extract method)
	private int selectionLength;			// number of lines of selection (extract method)
	private IType classSourceTy;
	private IType classTargetTy;

	/**
	 * Constructors
	 */
	public RefactoringData() {
	}

	/**
	 * Getters & Setters
	 */
	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public int getSelectionStart() {
		return selectionStart;
	}

	public void setSelectionStart(int selectionStart) {
		this.selectionStart = selectionStart;
	}

	public int getSelectionLength() {
		return selectionLength;
	}

	public void setSelectionLength(int selectionLength) {
		this.selectionLength = selectionLength;
	}

	public RefactoringType getRefactoringType() {
		return refactoringType;
	}

	public void setRefactoringType(RefactoringType refactoringType) {
		this.refactoringType = refactoringType;
	}

	public String getClassSource() {
		return classSource;
	}
	public IType getClassSourceType() {
		return classSourceTy;
	}
	public void setClassSource(String classSource) {
		this.classSource = classSource;
	}
	public void setClassSourceTy(IType classSource) {
		this.classSourceTy = classSource;
	}
	public String getClassTarget() {
		return classTarget;
	}
	public IType getClassTargetType() {
		return classTargetTy;
	}
	public void setClassTarget(String classTarget) {
		this.classTarget = classTarget;
	}
	public void setClassTargetTy(IType classTarget) {
		this.classTargetTy = classTarget;
	}
	public String getMethodTarget() {
		return methodTarget;
	}

	public void setMethodTarget(String methodTarget) {
		this.methodTarget = methodTarget;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	/**This method return the RefactoringType enum from the name 
	 * of the refactoring in FitnessReport
	 * 
	 * @param s	name of the refactoring in the FitnessReport (ReACO)
	 * @return RefactoringType
	 */
	public static RefactoringType getTypeRefactoring(String s) {
		if (s == null)
			return null;
		else {
			RefactoringType res;
			switch (s) {
			case "CollapseHierarchy":
				res = RefactoringType.COLLAPSE_HIERARCHY; break;
			case "InlineClass":
				res = RefactoringType.INLINE_CLASS; break;
			case "introduceParameterObject":
				res = RefactoringType.INTRODUCE_PARAMETER_OBJECT; break;
			case "extractMethod":
				res = RefactoringType.EXTRACT_METHOD; break;
			case "moveMethod":
				res = RefactoringType.MOVE_METHOD; break;
			case "refactBlob":
				res = RefactoringType.MOVE_METHOD; break;
			case "extractClass":
				res = RefactoringType.EXTRACT_CLASS; break;
			case "RemoveParameter":
				res = RefactoringType.REMOVE_PARAMETER; break;
			case "ReplaceMethodWithMethodObject":
				res = RefactoringType.REPLACE_METHOD_WITH_METHOD_OJBECT; break;
			case "refactSpaghettiCode":
				res = RefactoringType.EXTRACT_CLASS; break;
			default:
				System.err.println("New Smell detected: " + s);
				return null;
			}
			return res;
		}
	}
	
	/**This method return the RefactoringType enum from the name 
	 * of the refactoring in the view Refactoring Sequence
	 * 
	 * @param s	name of the refactoring in the view Refactoring Sequence
	 * @return RefactoringType
	 */
	public static RefactoringType getTypeRefactoringFromColumn(String s) {
		if (s == null)
			return null;
		else {
			RefactoringType res;
			switch (s) {
			case "Collapse Hierarchy":
				res = RefactoringType.COLLAPSE_HIERARCHY; break;
			case "Inline Class":
				res = RefactoringType.INLINE_CLASS; break;
			case "Introduce Parameter Object":
				res = RefactoringType.INTRODUCE_PARAMETER_OBJECT; break;
			case "Extract Method":
				res = RefactoringType.EXTRACT_METHOD; break;
			case "Move Method":
				res = RefactoringType.MOVE_METHOD; break;
			case "Extract Class":
				res = RefactoringType.EXTRACT_CLASS; break;
			case "refactSpaghettiCode":
				res = RefactoringType.EXTRACT_CLASS; break;
			case "Remove Parameter":
				res = RefactoringType.REMOVE_PARAMETER; break;
			case "Replace Method with Method Object (manual)":
				res = RefactoringType.REPLACE_METHOD_WITH_METHOD_OJBECT; break;
			default:
				System.err.println("New Smell detected: " + s);
				return null;
			}
			return res;
		}
	}

	/** This method return a boolean value depend of the refactoring 
	 * operation is implemented or not.
	 * 
	 * @param refactoringType
	 * @return boolean
	 */
	public static boolean isImplemented(RefactoringType refactoringType) {
		if (refactoringType == null)
			return false;
		else {
			switch (refactoringType) {
			case INTRODUCE_PARAMETER_OBJECT:
				return true;
			case REMOVE_PARAMETER:
				return true;
			case MOVE_METHOD:
				return true;
			case EXTRACT_CLASS:
				return true;
			case EXTRACT_METHOD:
				return true;
			case INLINE_CLASS:
				return true;
			case COLLAPSE_HIERARCHY:
				return true;
			default:
				return false;
			}
		}
	}

}
