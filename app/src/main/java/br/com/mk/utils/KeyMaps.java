package br.com.mk.utils;

import javafx.scene.control.TableRow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.Map;

import br.com.mk.components.menu.SystemShortcuts;
import br.com.mk.components.process.contextMenu.KillWindow;
import br.com.mk.components.process.contextMenu.MemoryMapWindow;
import br.com.mk.components.process.contextMenu.OpenFilesWindow;
import br.com.mk.components.process.contextMenu.ProcessPriorityWindow;
import br.com.mk.components.process.contextMenu.ProcessPropertiesWindow;
import br.com.mk.components.process.contextMenu.ResumeWindow;
import br.com.mk.components.process.contextMenu.SetAffinityWindow;
import br.com.mk.components.process.contextMenu.SuspendWindow;
import br.com.mk.components.process.contextMenu.TerminateWindow;
import br.com.mk.components.window.ProcessStatusWindow;
import br.com.mk.data.ProcessMonitor.ProcessInfo;

public class KeyMaps {

	// Map de atalhos simples (KeyCode -> nome/função)
	private static final Map<KeyCode, String> simpleKeys = new HashMap<>();

	// Map de combinações (KeyCombination -> nome/função)
	private static final Map<KeyCombination, String> comboKeys = new HashMap<>();

	static {

		// Exemplos de combinações
		comboKeys.put(KeyCombination.keyCombination("CTRL+ENTER"), "properties");
		comboKeys.put(KeyCombination.keyCombination("CTRL+M"), "memoryMap");
		comboKeys.put(KeyCombination.keyCombination("CTRL+O"), "openFiles");
		comboKeys.put(KeyCombination.keyCombination("CTRL+S"), "changePriority");
		comboKeys.put(KeyCombination.keyCombination("ALT+U"), "setAffinity");
		comboKeys.put(KeyCombination.keyCombination("CTRL+S"), "suspend");
		comboKeys.put(KeyCombination.keyCombination("CTRL+C"), "resume");
		comboKeys.put(KeyCombination.keyCombination("ALT+E"), "terminate");
		comboKeys.put(KeyCombination.keyCombination("ALT+K"), "kill");
		comboKeys.put(KeyCombination.keyCombination("CTRL+W"), "shortcuts");
		comboKeys.put(KeyCombination.keyCombination("ALT+1"), "process");
		comboKeys.put(KeyCombination.keyCombination("ALT+2"), "recourses");
		comboKeys.put(KeyCombination.keyCombination("ALT+3"), "filesSystem");
	}

	/** Retorna o nome do atalho simples */
	public static String getSimpleKeyName(KeyCode keyCode) {
		return simpleKeys.getOrDefault(keyCode, keyCode.getName());
	}

	/** Retorna o nome da combinação de teclas */
	public static String getComboKeyName(KeyEvent event) {
		for (KeyCombination kc : comboKeys.keySet()) {
			if (kc.match(event)) {
				return comboKeys.get(kc);
			}
		}
		return null; // não encontrado
	}

	/** Adiciona um atalho simples */
	public static void addSimpleKey(KeyCode keyCode, String name) {
		simpleKeys.put(keyCode, name);
	}

	/** Adiciona uma combinação de teclas */
	public static void addComboKey(String combo, String name) {
		comboKeys.put(KeyCombination.keyCombination(combo), name);
	}

	/** Verifica se uma combinação existe */
	public static boolean isComboKey(KeyEvent event) {
		for (KeyCombination kc : comboKeys.keySet()) {
			if (kc.match(event))
				return true;
		}
		return false;
	}

	public static String handleNavPanel(KeyEvent event) {
		String combo = KeyMaps.getComboKeyName(event);
		if (combo == null)
			return "null";
		switch (combo) {
		case "process": {
			return "Process";
		}
		case "recourses": {
			return "Recourses";
		}
		case "filesSystem": {
			return "Files System";
		}
		case "shortcuts": {
			new SystemShortcuts();
			return ""; // retorna uma String válida
		}
		default: {
			throw new IllegalArgumentException("Unexpected value: " + combo);
		}
		}
	}

	public static void handleComboKey(TableRow<ProcessInfo> row, KeyEvent event) {

		String combo = KeyMaps.getComboKeyName(event);
		if (combo == null)
			return;

		if (!isComboKey(event)) {
			handleSimpleKey(row, event);
		}

		switch (combo) {
		case "properties": {
			if (!row.isEmpty()) {
				ProcessPropertiesWindow.show(row.getItem());
			}
			break;
		}
		case "memoryMap": {
			if (!row.isEmpty()) {
				MemoryMapWindow.show(row.getItem());
			}
			break;
		}
		case "openFiles": {
			if (!row.isEmpty()) {
				OpenFilesWindow window = new OpenFilesWindow(row.getItem().pid());
				window.show();
			}
			break;
		}
		case "changePriority": {
			if (!row.isEmpty()) {
				ProcessPriorityWindow window = new ProcessPriorityWindow(row.getItem().pid());
				window.show();
			}
			break;
		}
		case "setAffinity": {
			if (!row.isEmpty()) {
				int totalCores = Runtime.getRuntime().availableProcessors();
				SetAffinityWindow window = new SetAffinityWindow(row.getItem().pid(), totalCores);
				window.show();
			}
			break;
		}
		case "suspend": {
			if (!row.isEmpty()) {
				SuspendWindow window = new SuspendWindow(row.getItem().pid());
				window.show();
			}
			break;
		}

		case "resume": {
			if (!row.isEmpty()) {
				ResumeWindow window = new ResumeWindow(row.getItem().pid());
				window.show();
			}
			break;
		}
		case "terminate": {
			if (!row.isEmpty()) {
				TerminateWindow window = new TerminateWindow(row.getItem().pid());
				window.show();
			}
			break;
		}
		case "kill": {
			if (!row.isEmpty()) {
				KillWindow window = new KillWindow(row.getItem().pid());
				window.show();
			}
			break;
		}

		default:
			throw new IllegalArgumentException("Unexpected value: " + combo);
		}
	}

	public static void handleSimpleKey(TableRow<ProcessInfo> row, KeyEvent event) {
		if (event.getCode() == null)
			return; // nada a fazer, tecla não registrada

		switch (event.getCode()) {
		case KeyCode.ENTER: {
			if (!row.isEmpty()) {
				ProcessStatusWindow window = new ProcessStatusWindow(row.getItem().pid());
				window.show();
			}
			break;
		}

		default:
			throw new IllegalArgumentException("Unexpected value: " + KeyCode.ENTER);
		}
	}

}
