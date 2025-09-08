package br.com.mk.components.menu;

import br.com.mk.components.process.contextMenu.KillWindow;
import br.com.mk.components.process.contextMenu.MemoryMapWindow;
import br.com.mk.components.process.contextMenu.OpenFilesWindow;
import br.com.mk.components.process.contextMenu.ProcessPriorityWindow;
import br.com.mk.components.process.contextMenu.ProcessPropertiesWindow;
import br.com.mk.components.process.contextMenu.ResumeWindow;
import br.com.mk.components.process.contextMenu.SetAffinityWindow;
import br.com.mk.components.process.contextMenu.SuspendWindow;
import br.com.mk.components.process.contextMenu.TerminateWindow;
import br.com.mk.data.ProcessMonitor.ProcessInfo;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;

public class ProcessContextMenu {

	/**
	 * Cria um ContextMenu para uma linha de processo
	 */
	public static ContextMenu createMenu(TableRow<ProcessInfo> row) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.getStyleClass().add("context-menu");

		MenuItem properties = new MenuItem("Properties");
		MenuItem memoryMap = new MenuItem("Memory Map");
		MenuItem openFiles = new MenuItem("Open Files");
		MenuItem changePriority = new MenuItem("Change Priority");
		MenuItem setAffinity = new MenuItem("Set Affinity");
		MenuItem suspend = new MenuItem("Suspend");
		MenuItem resume = new MenuItem("Resume");
		MenuItem terminate = new MenuItem("Terminate");
		MenuItem kill = new MenuItem("Kill");

		// Aqui você pode adicionar ações com setOnAction
		// Exemplo:
		// properties.setOnAction(e -> showProperties(row.getItem()));
		properties.setOnAction(e -> {
			if (!row.isEmpty()) {
				ProcessPropertiesWindow.show(row.getItem());
			}
		});

		memoryMap.setOnAction(e -> {
			if (!row.isEmpty()) {
				MemoryMapWindow.show(row.getItem());
			}
		});

		openFiles.setOnAction(e -> {
			if (!row.isEmpty()) {
				OpenFilesWindow window = new OpenFilesWindow(row.getItem().pid());
				window.show();
			}
		});

		changePriority.setOnAction(e -> {
			if (!row.isEmpty()) {
				ProcessPriorityWindow window = new ProcessPriorityWindow(row.getItem().pid());
				window.show();
			}
		});

		setAffinity.setOnAction(e -> {
			if (!row.isEmpty()) {
				int totalCores = Runtime.getRuntime().availableProcessors();
				SetAffinityWindow window = new SetAffinityWindow(row.getItem().pid(), totalCores);
				window.show();
			}
		});

		suspend.setOnAction(e -> {
			if (!row.isEmpty()) {
				SuspendWindow window = new SuspendWindow(row.getItem().pid());
				window.show();
			}
		});

		resume.setOnAction(e -> {
			if (!row.isEmpty()) {
				ResumeWindow window = new ResumeWindow(row.getItem().pid());
				window.show();
			}
		});

		terminate.setOnAction(e -> {
			if (!row.isEmpty()) {
				TerminateWindow window = new TerminateWindow(row.getItem().pid());
				window.show();
			}
		});

		kill.setOnAction(e -> {
			if (!row.isEmpty()) {
				KillWindow window = new KillWindow(row.getItem().pid());
				window.show();
			}
		});

		contextMenu.getItems().addAll(properties, memoryMap, openFiles, changePriority, setAffinity,
				new SeparatorMenuItem(), suspend, resume, terminate, kill);

		return contextMenu;
	}
}
