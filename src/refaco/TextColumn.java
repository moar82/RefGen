package refaco;

import org.eclipse.jface.viewers.ColumnLabelProvider;


/**
 * Class to represent each cell of a table
 *
 */
public class TextColumn extends ColumnLabelProvider {
	
	private String title;
	private int column;			// number of column in the row
	private int widthColumn;
	
	/**
	 * Constructors
	 */
	public TextColumn(String _title, int _column){
		title = _title;
		column = _column;
		widthColumn = 250;
	}
	
	public TextColumn(String _title, int _column, int _widthColum){
		title = _title;
		column = _column;
		widthColumn = _widthColum;
	}
	
	@Override
	public String getText(Object element) {
		return ((RowData) element).getColumn(column);
	}

	/**
	 * Getters & Setters
	 */
	public String getTitle() {
		return title;
	}
	
	public int getWidth() {
		return widthColumn!=0?widthColumn:250;
	}

}
