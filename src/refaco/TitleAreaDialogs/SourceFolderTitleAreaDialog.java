/**
 * 
 */
package refaco.TitleAreaDialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/**
 * @author Rodrigo Morales
 * This class generates a dialog to receive the source folder
 * in case it is not located in the eclipse's default folder
 * "src"
 *
 */
public class SourceFolderTitleAreaDialog extends Dialog {

	String sourceFolderText;
	
	public SourceFolderTitleAreaDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public static void main(String[] args) {
	    Shell shell = new Shell();
	    SourceFolderTitleAreaDialog dialog = new SourceFolderTitleAreaDialog(shell);
	    System.out.println(dialog.open());
	  }

	public String open() {
		Shell parent = getParent();
	    final Shell shell =
	      new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
	    shell.setText("Not src folder found!");

	    shell.setLayout(new GridLayout(2, true));

	    Label label = new Label(shell, SWT.NULL);
	    label.setText("We could not locate the src folder associated to the project\n"
	    		+ "in the workspace, please specify the relative path (e.g., source");

	    final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);

	    final Button buttonOK = new Button(shell, SWT.PUSH);
	    buttonOK.setText("Ok");
	    buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	    Button buttonCancel = new Button(shell, SWT.PUSH);
	    buttonCancel.setText("Cancel");

	    text.addListener(SWT.Modify, new Listener() {
	      public void handleEvent(Event evsourceFolderTextent) {
	        try {
	        	sourceFolderText = text.getText();
	          buttonOK.setEnabled(true);
	        } catch (Exception e) {
	          buttonOK.setEnabled(false);
	        }
	      }
	    });

	    buttonOK.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	        shell.dispose();
	      }
	    });

	    buttonCancel.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	    	  sourceFolderText = null;
	        shell.dispose();
	      }
	    });
	    
	    shell.addListener(SWT.Traverse, new Listener() {
	      public void handleEvent(Event event) {
	        if(event.detail == SWT.TRAVERSE_ESCAPE)
	          event.doit = false;
	      }
	    });

	    text.setText("");
	    shell.pack();
	    shell.open();

	    Display display = parent.getDisplay();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }

	    return sourceFolderText;
	}

}
