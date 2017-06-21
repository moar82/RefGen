package functionalTests;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class AnalizeProjectTest {
	
	private static SWTWorkbenchBot bot;
	
	@BeforeClass
	public static void beforeClass(){
		bot = new SWTWorkbenchBot();
		try{
			SWTBotPreferences.TIMEOUT = 200000; 
			bot.viewByTitle("Welcome").close();
		}catch(WidgetNotFoundException e){
			// ignore
		}
	}
	
	/**Import the project if it not exist or select (focus) if it exists.
	 * 
	 * @param path
	 * @param nameProject
	 * @return
	 */
	public static IProject importProject(SWTWorkbenchBot bot, String path, String nameProject){
		// Check the project exist
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("JChoco");
		SWTBotShell shell;
		if(!project.exists()){
			bot.menu("File").menu("Import...").click();
			shell = bot.shell("Import");
			assertNotNull(shell);
			shell.activate();
			bot.sleep(100);
			bot.tree().expandNode("General").select("Existing Projects into Workspace");
			bot.button("Next >").click();
			bot.radio("Select root directory:").click();
			bot.comboBox(0).setText(path);
			bot.sleep(500);
			
			// fix to update the list of projects (equals to pulse enter)
			bot.radio("Select archive file:").click();
			bot.radio("Select root directory:").click();
			bot.button("Finish").click();
		}

		// Focus Package Explorer
		bot.sleep(3400);
		shell = bot.shell("Resource - Eclipse Platform");
		shell.activate();
		bot.menu("Window").menu("Show View").menu("Other...").click();
		shell = bot.shell("Show View");
		shell.activate();
		bot.tree().expandNode("General").select("Project Explorer");
		bot.button("OK").click();
		bot.viewByTitle("Project Explorer").show();
		bot.viewByTitle("Project Explorer").setFocus();
		
		// Select Project
		bot.sleep(1000);
		bot.tree().select(0);
		//bot.tree().select(nameProject);
		return project;
	}
	
	@Test //CP01 - CP04
	public void testAnalizeProject(){
		
		// Import the project
		importProject(bot,"/Users/juan/workspace/TFM/ptidej-5/JChoco", "JChoco");
		
		// Analize Project with ReACO and wait to finish
		SWTBotToolbarButton analizeButton = bot.toolbarButtonWithTooltip("Analyze Code Smells");
		assertNotNull(analizeButton);
		analizeButton.click();
		SWTBotShell shell = bot.activeShell();
		bot.waitUntil(Conditions.shellCloses(shell));
		
		// Check the results exist
		File file = new File("/Users/juan/workspace/TFM/ptidej-5/JChoco/refaco/FitnessReport-0JChoco.txt");
		assertNotNull(file);
	}
	
	@Test // CP02  - CP05
	public void testAnalizeProjectEmpty() {

		SWTBotToolbarButton analizeButton = bot.toolbarButtonWithTooltip("Analyze Code Smells");
		assertNotNull(analizeButton);
		analizeButton.click();
		SWTBotShell shell = bot.shell("Error");
		assertNotNull(shell);
		shell.activate();
		assertNotNull(bot.button("OK"));
		bot.button("OK").click();
	}
	
	@Test //CP03 - CP06
	public void testAnalizeProjectAndCancel(){
		
		// Import the project		
		importProject(bot,"/Users/juan/workspace/TFM/ptidej-5/JChoco", "JChoco");
		
		// Analize Project wit Refaco and wait to finish
		SWTBotToolbarButton analizeButton = bot.toolbarButtonWithTooltip("Analyze Code Smells");
		assertNotNull(analizeButton);
		analizeButton.click();
		SWTBotShell shell = bot.activeShell();
		
		// Cancel the task
		bot.sleep(4000);
		shell = bot.shell("RefACo");
		shell.activate();
		bot.button(0).click();
		
		// Check the cancel message and close
		shell = bot.activeShell();
		bot.button(0).click();
		
		
	}
	
	

}
