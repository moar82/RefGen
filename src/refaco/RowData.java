package refaco;

import java.util.ArrayList;

/**
 * This class is used like support in the views (tables) of the plug-in. it's represent each row of the table
 *
 */
public class RowData {

	private String[] columns;

	/**
	 * Constructors
	 */
	public RowData(String... args){
		columns = args;
	}

	public String getColumn(int index) {
		return columns[index];
	}

	/** return a RefactoringData object from the row data (used in Refactoring Sequence view)
	 * 
	 * @return RefactoringData
	 */
	public RefactoringData getRefactoringData(){
		RefactoringData res = new RefactoringData();
		res.setRefactoringType(RefactoringData.getTypeRefactoringFromColumn(columns[1]));
		res.setClassSource(columns[2]);
		res.setClassTarget(columns[3]);
		res.setMethodTarget(columns[4]);
		// if the refactoring Type is Extract Classs, the field to extract is on method target
		if(res.getRefactoringType().equals(RefactoringType.EXTRACT_CLASS)){
			ArrayList<String> fields = new ArrayList<String>();
			fields.add(columns[4]);
			res.setFields(fields);
		}
		return res;
	}
	
	@Override
	public String toString(){
		return columns.toString();
	}

}
