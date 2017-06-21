package functionalTests;
import static org.junit.Assert.*;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class UITest {
	
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
	
	@Test // CP08
	public void testShowRefactoringOpps(){
		// Show Code Smell View
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell shell = bot.shell("Show View");
		shell.activate();
		bot.tree().expandNode("Refactor").select("Code Smell");
		bot.button("OK").click();
		SWTBotView codeSmellView = bot.viewByTitle("Code Smell");
		assertNotNull(codeSmellView);
		// Click Show Refactoring Opportunities list
		SWTBotToolbarButton refactoringListButton = codeSmellView.toolbarButton("Show Refactoring opportunities list");
		assertNotNull(refactoringListButton);
		refactoringListButton.click();
		
		// Check the message error
		bot.sleep(500);
		shell = bot.shell("Error");
		shell.activate();
		bot.button(0).click();
	}
	
	@Test  //CP11
	public void testApplyRefactoringSequenceEmpty(){
		// Show Refactoring Sequence View
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell shell = bot.shell("Show View");
		shell.activate();
		bot.tree().expandNode("Refactor").select("Refactoring Sequence");
		bot.button("OK").click();
		SWTBotView refactoringListView = bot.viewByTitle("Refactoring Sequence");
		assertNotNull(refactoringListView);
		
		// Click Apply Refactoring Sequence
		SWTBotToolbarButton applyRefactoringListButton = refactoringListView.toolbarButton("Apply refactoring sequence");
		assertNotNull(applyRefactoringListButton);
		applyRefactoringListButton.click();
	}

	@Test
	public void testOpenCodeSmellTableView() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell shell = bot.shell("Show View");
		shell.activate();
		bot.tree().expandNode("Refactor").select("Code Smell");
		bot.button("OK").click();
		SWTBotView codeSmellView = bot.viewByTitle("Code Smell");
		assertNotNull(codeSmellView);
	}
	
	@Test
	public void testOpenRefactoringSequenceTableView() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell shell = bot.shell("Show View");
		shell.activate();
		bot.tree().expandNode("Refactor").select("Refactoring Sequence");
		bot.button("OK").click();
		SWTBotView refactoringSequenceView = bot.viewByTitle("Refactoring Sequence");
		assertNotNull(refactoringSequenceView);
	}

	

	

}
