package refaco;

/**
 * All the refactorings of this project (some aren't implemented).
 *
 */
public enum RefactoringType {
	COLLAPSE_HIERARCHY ("Collapse Hierarchy"),
	INLINE_CLASS ("Inline Class"),
	INTRODUCE_PARAMETER_OBJECT ("Introduce Parameter Object"),
	EXTRACT_METHOD ("Extract Method"),
	MOVE_METHOD ("Move Method"),
	EXTRACT_CLASS ("Extract Class"),
	REMOVE_PARAMETER ("Remove Parameter"),
	REPLACE_METHOD_WITH_METHOD_OJBECT ("Replace Method with Method Object (manual)"),
	REFACTSPAGHETTICODE ("REfact Spagheti Code");	// Not implemented
	
	private String nameToString; 
    private RefactoringType(String nameToString) { 
        this.nameToString = nameToString;
    } 
    
    @Override 
    public String toString(){ 
        return nameToString; 
    } 
    


}
