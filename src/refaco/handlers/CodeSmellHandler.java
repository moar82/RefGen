package refaco.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import refaco.ProjectData;
import refaco.RowData;
import refaco.StreamReader;
import refaco.TitleAreaDialogs.SourceFolderTitleAreaDialog;
import refaco.exceptions.*;
import refaco.views.CodeSmellTableView;

/**
 * This class manage the execution of ReACO
 */
public class CodeSmellHandler extends AbstractHandler {

	private static final String JARFOLDER = "/refgen/";
	private List<String> refactoringOpps; 	// list of the refactoring opportunities (ReACO results)
	private List<RowData> codeSmells; 		// Code smells and antipatterns detected by ReACO in RowData format
	private ProjectData projectData;		// project selected

	// Constants for each Code Smell/ Antipattern
	private static final int BLOB = 0;
	private static final int LAZYCLASS = 1;
	private static final int LONGPARAMETERLIST = 2;
	private static final int SPAGUETTICODE = 3;
	private static final int SPECULATIVEGENERALITY = 4;
	
	int nameToInt(String in)
	{
		int num = 0;
		switch(in)
		{
			case "Blob":
				num=BLOB;
			break;
			case "LazyClass":
				num=LAZYCLASS;
			break;
			case "LongParameterList":
				num=LONGPARAMETERLIST;
			break;
			case "SpaghettiCode":
				num=SPAGUETTICODE;
			break;
			case "SpeculativeGenerality":
				num=SPECULATIVEGENERALITY;
			break;
				
		}
		return num;
		
	}
	private String nameProject;
	public static String javasrc;
	
	// Getters & Setters
	public List<String> getRefactoringOpps() {
		return refactoringOpps;
	}

	public void setRefactoringOpps(List<String> refactoringOpps) {
		this.refactoringOpps = refactoringOpps;
	}

	public List<RowData> getCodeSmells() {
		return codeSmells;
	}

	public void setCodeSmells(List<RowData> codeSmells) {
		this.codeSmells = codeSmells;
	}

	private String javaDirectory;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			// Get the tableview (initialize the View with the previous project
			// selected by the user)
			
			javaDirectory = null;
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		    IWorkbenchPage activePage = window.getActivePage();
		    ISelection selection = activePage.getSelection();
		    if (selection != null) {
		    	//System.out.println("Got selection");
		    	if (selection instanceof IStructuredSelection) {
		    		//System.out.println("Got a structured selection");
		    		Object element = ((IStructuredSelection) selection).getFirstElement();
		    		if (element instanceof IPackageFragmentRoot){
		    			IPackageFragmentRoot project = (IPackageFragmentRoot)((IAdaptable)element).getAdapter(IPackageFragmentRoot.class);
		    				IResource rs = null;
							try {
								rs = project.getCorrespondingResource();
							} catch (JavaModelException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    				javaDirectory = rs.getLocation().toPortableString();
		    				//System.out.println(project.getSourceAttachmentRootPath());
		    		}
		    		/*this works, but causes problem when concatenating the path for refgen
		    		 * The source does not exist R:/git/ganttWkspaceSoccer/ganttproject/ganttproject/src/net/sourceforge/ganttproject/actionnet/sourceforge/ganttproject/action/
		    		 * net.sourceforge.ganttproject.action
		    		 * 
		    		 * else if (element instanceof IPackageFragment)
		    		{
		    			IPackageFragment project = (IPackageFragment)((IAdaptable)element).getAdapter(IPackageFragment.class);
	    				IResource rs = null;
						try {
							rs = project.getCorrespondingResource();
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    				javaDirectory = rs.getLocation().toPortableString();
		    		}*/
		    	}
		    }
			//SourceFolderTitleAreaDialog myDialog = new SourceFolderTitleAreaDialog(Display.getCurrent().getActiveShell());
			CodeSmellTableView view = initSelectionView(event);

			if (javaDirectory!=null){
				File varTmpDir = new File(javaDirectory);
				if (varTmpDir.exists()==false){
					throw new ProjectSelectionException();
				}
			}
			else{
				throw new ProjectSelectionException();
			}
			String src= javaDirectory.substring(javaDirectory.lastIndexOf("/")+1);		
			 javasrc = src;//this variable is used in the application of collapse hierarchy
			// Job for execute RefGen in background
			Job job = new Job("RefGen") {
				protected IStatus run(IProgressMonitor monitor) {
					try {
						int totalUnitsOfWork = 250;
						monitor.beginTask("Running RefGen", totalUnitsOfWork);
						// Call to execute SACO
						executeReACO(view.getProjectData(), monitor, javaDirectory);

						// read the results from file
						monitor.subTask("Processing the results..");
						getResultsFromFile(projectData.getPath() + JARFOLDER,projectData.getName());
						
						monitor.worked(250);
						monitor.done();
					} catch ( IOException | ReACOExecutionException e) {
						if (e.getMessage().equals("Task cancelled")) {
							return Status.CANCEL_STATUS;
						} 
					}
					return Status.OK_STATUS;
				}
			
			};
			job.setPriority(Job.SHORT);
			job.setUser(true);
			job.addJobChangeListener(new JobChangeAdapter() {
				// when the job finish...
				public void done(IJobChangeEvent jobEvent) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (jobEvent.getResult().isOK()) {
								// Get the Code Smell view
								CodeSmellTableView view = null;
								try {
									view = (CodeSmellTableView) HandlerUtil.getActiveWorkbenchWindow(event)
											.getActivePage().showView(CodeSmellTableView.ID);
								} catch (PartInitException e) {}
								
								// print the results in the table view
								view.getTableViewer().setInput(codeSmells);
								view.setRefactoringOpps(refactoringOpps);
								view.setLastProjectAnalized(projectData.getName());
								// if 0 code smells detected -> show error
								if(codeSmells.isEmpty()){
									HandlersUtils.showInfo("0 Code smells detected!");
								}

							}else if(jobEvent.getResult().getSeverity() ==  Status.CANCEL){
								HandlersUtils.showInfo("Task cancelled");
							}
						}
					});
				}
			});
			job.schedule(); // start as soon as possible

		} catch (ProjectSelectionException e) {
			HandlersUtils.showError(e.getMessage());
		}
		return null;
	}

	/**
	 * Open the view if it is close and initialize the project selected and path
	 * 
	 * @param event
	 * @return CodeSmellTableView the table view
	 * @throws ProjectSelectionException
	 */
	private CodeSmellTableView initSelectionView(ExecutionEvent event) throws ProjectSelectionException {
		
		CodeSmellTableView view = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ProjectData dataSelection;
		
		try {
			// Get the current selection
			dataSelection = CodeSmellTableView.getSelection(window.getSelectionService().getSelection());
		} catch (CoreException e) {
			throw new ProjectSelectionException(e.getMessage());
		}
		
		// check the data selection is valid
		if (dataSelection == null || dataSelection.getPath() == null || dataSelection.getName() == null) {
			throw new ProjectSelectionException();
		} else {
			try {
				// open the view and initialize the project data
				view = (CodeSmellTableView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
						.showView(CodeSmellTableView.ID);
				view.getTableViewer().setInput(null);
				view.setProjectData(dataSelection);
				projectData = dataSelection;
			} catch (PartInitException e) {
				throw new ProjectSelectionException(e.getMessage());
			}
		}
		return view;
	}

	/**
	 * Execute the ReACO library over the project selected
	 * 
	 * @param path 			Path to the project
	 * @param targetName 	Name of the project
	 * @param monitor		Task monitor
	 * @param event 
	 * @throws ReACOExecutionException
	 */
	public void executeReACO(ProjectData projectData, IProgressMonitor monitor,String src ) throws ReACOExecutionException {

		try {
			monitor.subTask("Preparing ReACO files...");
			// Check if analyze a package or the full project
			String pathToAnalyze;
			
			if(projectData.getPackageName() != null){
				// Analyze a package
				String packageFormat = projectData.getPackageName().replace('.', '/');
				pathToAnalyze =  src + packageFormat + "/";
			}else{
				// Analyze the src folder
				pathToAnalyze =  src;
			}
			prepareReACOResources(projectData.getPath(), pathToAnalyze);

			monitor.subTask("Analizing project. Please wait...");

			// execute the command
			Process proc;
			String command = "java -jar " + "RefGen.jar  " + "config.prop 0"+ projectData.getName();
			proc = Runtime.getRuntime().exec(command, null, new File(projectData.getPath() + JARFOLDER));

			// get ErrorStream
			StreamReader errorReader = new StreamReader(proc.getErrorStream(), "ERROR");
			// get InputStream
			StreamReader inputReader = new StreamReader(proc.getInputStream(), "INPUT", monitor);
			// get OutputStream
			proc.getOutputStream();
			
			errorReader.start();
			inputReader.start();

			// wait and check if the process is canceled and update the monitor
			while (proc.isAlive() && !monitor.isCanceled()) {
				Thread.sleep(500);
				monitor.worked(1);
			}
			
			// check if the task was cancelled
			if (monitor.isCanceled()) {
				proc.destroy();
				throw new ReACOExecutionException("Task cancelled");
			}
			
		} catch (IOException | InterruptedException e) {
			throw new ReACOExecutionException(e.getMessage());
		}
	}

	/**
	 * Prepare all the resources needed for execute ReACO
	 * @param path	 Path of the project
	 * @param pathProjecttoAnalize Path of the folder to analyze
	 * @throws IOException
	 */
	public void prepareReACOResources(String path, String pathProjecttoAnalize) throws IOException {
		/*RMA validate if in the working directory we have a configuration file if so
		read it.
		Note that we assume that the config.prop file located in the working directory
		does not contains pathProjecttoAnalize, since this is dynamically added 
		according to the selection of the user.*/
		List<String> lines = null;
		org.eclipse.core.runtime.Path pathConfigFile = new org.eclipse.core.runtime.Path("/config.prop");
		URL fileURL2 = FileLocator.find(Platform.getBundle("RefACo"), pathConfigFile, null);
		if (fileURL2!=null){
			String relativePath = FileLocator.resolve(fileURL2).getPath();
			String absolutePath =new java.io.File(relativePath).getAbsoluteFile().toString();
			//lines.addAll(Files.readAllLines(Paths.get(absolutePath)) );
			lines = Files.readAllLines(Paths.get(absolutePath)) ;
			lines.add("pathProjecttoAnalize = " + pathProjecttoAnalize);
			//lines = Arrays.asList("pathProjecttoAnalize = " + pathProjecttoAnalize);
		}
		else{
			lines = Arrays.asList("###Automatically generated by plug-in with default settings","pathProjecttoAnalize = " + pathProjecttoAnalize,
					"steps = 1", "qmood = 0", "initialcountAntipatterns = 1", 
					"generateFromSourceCode=1",  
					"detectedAntipatterns=LazyClass,LongParameterList,SpaghettiCode,Blob,SpeculativeGenerality",
					"fitnessFunction_class=modelManager.fitnessFunctions.earmoFitness","refactoringScheme_class=modelManagerSCC.Schemes.SCCRefactoringSequence",
					"printStepIteration= 1");
		}

		//List<String> lines = Files.readAllLines(Paths.get(path + "DetectionResults in 0"+ nameProject+ " for Blob.ini"));
		File workingDirectory =new File(path + "/refgen");
		if (workingDirectory.exists())
			FileUtils.cleanDirectory(workingDirectory);
		// create a folder where to execute ReACO
		workingDirectory.mkdir();
		/*File ser = new File(path+"/refgen/scc.ser");
		File ses = new File(path+"/refgen/dependencyGraph.ses");
		boolean bool = ser.delete();
		ses.delete();*/
		// write the configuration file
		Path file = Paths.get(path + "/refgen/" + "config.prop");
		Files.write(file, lines, Charset.forName("UTF-8"));

		// copy ReACO library to file 
		org.eclipse.core.runtime.Path path2 = new org.eclipse.core.runtime.Path("/lib/RefGen.jar");
		URL fileURL = FileLocator.find(Platform.getBundle("RefACo"), path2, null);
		InputStream is = fileURL.openStream();
		OutputStream os = new FileOutputStream(path + "/refgen/RefGen.jar");
		copyResourcetoPath(is, os);
	}

	/**
	 * Copy the resource is to os
	 * @param is	Input file
	 * @param os	Output file
	 * @throws IOException
	 */
	private void copyResourcetoPath(InputStream is, OutputStream os) throws IOException {
		byte[] b = new byte[2048];
		int length;
		// copy the content 
		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}
		// close the stream
		is.close();
		os.close();
	}

	/**
	 * Read the FitnessReport (results of ReACO)
	 * @param path			Path where the results were written
	 * @param targetName	Project or package analyzed
	 * @throws IOException
	 */
	public void getResultsFromFile(String path, String targetName) throws IOException {
		
		// offset in the FitnessReport of each element
		int[] offsetCodeSmells = new int[] { 4, 1, 2, 3, 5};
		int oppRefactoringLine = 0;//20; 
		int firstRefactoringLine = 22; 

		nameProject = targetName;
		if (nameProject.indexOf(" ") != -1) {
			// if it has spaces get the first word
			nameProject = nameProject.substring(0, nameProject.indexOf(" "));
		}
		
		// Read the file
		refactoringOpps = new ArrayList<String>();
		List<String> lines = Files.readAllLines(Paths.get(path + "FitnessReport-0" + nameProject + ".txt"));
		if (lines != null)// && lines.size() > 20) 
			{
			// get the number of refactoring opp.
			String line = lines.get(oppRefactoringLine);
			String fitnessabstract[]=line.split(",");
			line =fitnessabstract[1].split(":")[1];
			//line = line.substring(line.indexOf("s:") + 2);
			//int numRefactoringOpp = Integer.parseInt(line.substring(0, line.indexOf(" ")));
			int numRefactoringOpp = Integer.parseInt(line);
			// get the refactoring opp.
			List<String> linesRefactorings = Files.readAllLines(Paths.get(path + "BestRefSequence-0" + nameProject + ".txt"));
			if (numRefactoringOpp > 0) {
				for (int i = 1; i <= numRefactoringOpp; i++) {
					refactoringOpps.add(linesRefactorings.get(i));
				}
			}
			// For each Smell read the results file read and parse OriginaCount.txt
			int[] numCodeSmells = new int[5];
			codeSmells = new ArrayList<RowData>();
			List<String> original = Files.readAllLines(Paths.get(path + "OriginalCount" + ".txt"));
			int begining = 3;
			for(int i = begining;i<original.size();++i)
			{
				String lineReading = original.get(i);
				String originalAbstract[]=lineReading.split(" ");
				int antiPatternsNumber =Integer.parseInt(originalAbstract[0]);
				String antiPatternsType =originalAbstract[3];

				getCodeSmells(nameToInt(antiPatternsType), path, antiPatternsNumber);
			}
		}
	}

	/** Read a specify code smell file (results of ReACO)
	 * 
	 * @param type	 		constant of code smell / antipattern
	 * @param path			Path to file
	 * @param num			Number of times of code smell / antipattern
	 * @throws IOException
	 */
	private void getCodeSmells(int type, String path, int num ) throws IOException {
		switch (type) {
		case BLOB:
			getBlobSmells(path, num);
			break;
		case LONGPARAMETERLIST:
			getLongParameterListSmells(path, num);
			break;
		case LAZYCLASS:
			getLazyClassSmells(path, num);
			break;
		case SPAGUETTICODE:
			getSpaguettiCodeSmells(path, num);
			break;
		case SPECULATIVEGENERALITY:
			getSpeculativeGeneralitySmells(path, num);
			break;
		}
	}

	/** 
	 * Read the Blob DetectionResutls file
	 * @param path			Path to file
	 * @param num			Number of times of code smell / antipattern
	 * @throws IOException
	 */
	private void getBlobSmells(String path, int num) throws IOException {

		List<String> lines = Files.readAllLines(Paths.get(path + "DetectionResults in 0"+ nameProject+ " for Blob.ini"));
		int cont = 10;
		boolean first = true;

		for (int i = 0; i < num; i++) {
			String line = lines.get(cont);
			String type = line.substring(line.indexOf('=') + 1, line.length());
			while (line.length() > 2 && !line.startsWith("#")) {
				type = line.substring(line.indexOf('=') + 1, line.length());
				codeSmells.add(new RowData(first ? "Blob" : "",
						line.substring(line.indexOf(':') + 1, line.indexOf('=')) + " (" + type + ")", null));
				cont += 5;
				if (type.equals("ControllerClass")) {
					cont--;
				}
				line = lines.get(cont);
				first = false;
			}
			first = true;
			cont += 6;
		}
	}

	/** 
	 * Read the Long Parameter List DetectionResutls file
	 * @param path			Path to file
	 * @param num			Number of times of code smell / antipattern
	 * @throws IOException
	 */
	private void getLongParameterListSmells(String path, int num) throws IOException {
		List<String> lines = Files
				.readAllLines(Paths.get(path + "DetectionResults in 0"+ nameProject+  " for LongParameterList.ini"));
		for (int i = 0; i < num; i++) {
			String line = lines.get(10 * i + 9);
			codeSmells.add(
					new RowData("Long Parameter List", line.substring(line.indexOf(':') + 1, line.indexOf('=')), null));
		}
	}

	/** 
	 * Read the Lazy Class DetectionResutls file
	 * @param path			Path to file
	 * @param num			Number of times of code smell / antipattern
	 * @throws IOException
	 */
	private void getLazyClassSmells(String path, int num) throws IOException {
		List<String> lines = Files
				.readAllLines(Paths.get(path + "DetectionResults in 0"+ nameProject+  " for LazyClass.ini"));
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if(line.contains("Entity"))
				codeSmells.add(
						new RowData("Lazy Class", line.substring(line.indexOf(':') + 1, line.indexOf('=')), null));
		}
		
	}

	/** 
	 * Read the SpaguettiCode DetectionResutls file
	 * @param path			Path to file
	 * @param num			Number of times of code smell / antipattern
	 * @throws IOException
	 */
	private void getSpaguettiCodeSmells(String path, int num) throws IOException {
		
		List<String> lines = Files
				.readAllLines(Paths.get(path + "DetectionResults in 0"+ nameProject+  " for SpaghettiCode.ini"));
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if(line.contains("Entity"))
				codeSmells.add(
						new RowData("SpaghettiCode", line.substring(line.indexOf(':') + 1, line.indexOf('=')), null));
		}
	}

	/** 
	 * Read the SpeculativeGenerality DetectionResutls file
	 * @param path			Path to file
	 * @param num			Number of times of code smell / antipattern
	 * @throws IOException
	 */
	private void getSpeculativeGeneralitySmells(String path, int num) throws IOException {
		List<String> lines = Files
				.readAllLines(Paths.get(path + "DetectionResults in 0"+ nameProject+ " for SpeculativeGenerality.ini"));
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if(line.contains("=AbstractClass"))
				codeSmells.add(
						new RowData("Speculative Generality", line.substring(line.indexOf(':') + 1, line.indexOf('=')), null));
		}
	}

}
