package refaco.handlers;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * This class contains some mutual methods
 */
public class HandlersUtils {
	
	/**Show a message error dialog with the error message
	 * 
	 * @param message
	 */
	public static void showError(String message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openError(null, "Error", message);
			}
		});
	}
	
	/**Show a message info dialog with the info message
	 * 
	 * @param message
	 */
	public static void showInfo(String message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(null, "Info", message);
			}
		});
	}
}
