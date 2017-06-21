package refaco;

/**
 * ProjectData class is used for provide a object with the project info when the selection change
 */
public class ProjectData {
	
	private String projectName;			// name of the project selected
	private String projectPath;			// full path of the project selected
	private String packageName;			// name of the package selected
	
	/**Constructors
	 * 
	 */
	public ProjectData(){
	}
	
	public ProjectData(String name, String path){
		this.projectName = name;
		this.projectPath = path;
	}
	
	public ProjectData(String name, String path, String packageName){
		this.projectName = name;
		this.projectPath = path;
		this.packageName = packageName;
	}

	/**
	 * Getters & Setters
	 */
	public String getName() {
		return projectName;
	}

	public void setName(String name) {
		this.projectName = name;
	}

	public String getPath() {
		// delete the first '/' char if the OS is Windows
		return System.getProperty("os.name").contains("indow")?projectPath.substring(1):projectPath;
	}

	public void setPath(String path) {
		this.projectPath = path;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 *  toString method
	 */
	@Override
	public String toString(){
		return "ProjectName: " + projectName + " || Package: " + packageName  + " || Path: " + projectPath;
	}

}
