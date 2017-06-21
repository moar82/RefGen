package refaco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Class to read the input and error streams
 */
public class StreamReader extends Thread {

	InputStream is;
	String type;
	IProgressMonitor monitor;

	/**
	 * Constructors
	 */
	public StreamReader(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}
	
	public StreamReader(InputStream is, String type, IProgressMonitor monitor) {
		this.is = is;
		this.type = type;
		this.monitor = monitor;
	}

	
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			// write the input in the console
			while ((line = br.readLine()) != null){
				if(line.length()==3 && monitor!=null){
					monitor.worked(1);
				}
				if(line.startsWith("Analysing") && monitor!=null){
					monitor.worked(10);
				}
				System.out.println(type + ">" + line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
