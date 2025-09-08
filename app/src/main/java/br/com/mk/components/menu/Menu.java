package br.com.mk.components.menu;

import br.com.mk.components.window.Process;
import br.com.mk.config.ConfigManager;
import javafx.scene.control.*;

public class Menu {

	public static ContextMenu createMenu(Process processPanel, boolean processON) {
		ContextMenu processMenu = new ContextMenu();

		// Itens do menu
		RadioMenuItem allProcess = new RadioMenuItem("All process");
		RadioMenuItem myProcess = new RadioMenuItem("My process");

//	    ToggleGroup group = new ToggleGroup();
//	    allProcess.setToggleGroup(group);
//	    myProcess.setToggleGroup(group);

		MenuItem update = new MenuItem("Update");
		MenuItem preferences = new MenuItem("Preferences");
		MenuItem help = new MenuItem("Help");
		MenuItem systemShortcuts = new MenuItem("System shortcuts");
		MenuItem about = new MenuItem("About");

		// carrega valor salvo
		allProcess.setSelected(ConfigManager.getBoolean("process.allProcesses", false));
		myProcess.setSelected(ConfigManager.getBoolean("process.myProcesses", false));

		// Ações
		allProcess.setOnAction(e -> {
			processPanel.setAllProcesses(allProcess.isSelected());
			processPanel.setMyProcesses(myProcess.isDisable());
			e.consume();
		});

		myProcess.setOnAction(e -> {
			processPanel.setMyProcesses(myProcess.isSelected());
			processPanel.setAllProcesses(allProcess.isDisable());
			e.consume();
		});

		update.setOnAction(e -> processPanel.manualRefresh());
		preferences.setOnAction(e -> new Preferences(processPanel));
		systemShortcuts.setOnAction(e -> new SystemShortcuts());
		help.setOnAction(e -> Help.openLink("https://github.com/MKawan/InfoSys"));
		about.setOnAction(e -> new About());

		if (processON) {
			processMenu.getItems().addAll(update, new SeparatorMenuItem(), allProcess, myProcess,
					new SeparatorMenuItem(), preferences, help, systemShortcuts, new SeparatorMenuItem(), about);
		} else {
			processMenu.getItems().addAll(preferences, help, systemShortcuts, new SeparatorMenuItem(), about);
		}

		return processMenu;
	}

}
