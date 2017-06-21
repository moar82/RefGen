package refaco.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

import refaco.TextColumn;

public class ViewUtils {

	/**Add a new column to the table tableViewer
	 * 
	 * @param columnLabelProvider the new column
	 * @param tableViewer		  the table viewer
	 * @return
	 */
	public static TableViewerColumn addColumn(TextColumn columnLabelProvider, TableViewer tableViewer){
        TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer,SWT.NONE);
        TableColumn column = tableViewerColumn.getColumn();
        column.setMoveable(true);
        column.setResizable(true);
        column.setText(columnLabelProvider.getTitle());
        column.setWidth(columnLabelProvider.getWidth());
        tableViewerColumn.setLabelProvider(columnLabelProvider);
        return tableViewerColumn;
    }
}
