package br.com.mk.components.process.contextMenu;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import br.com.mk.config.ConfigManager;
import br.com.mk.utils.ThemeManager;

/**
 * Janela para alterar a prioridade de um processo
 */
public class ProcessPriorityWindow {

	private final int pid;

	public ProcessPriorityWindow(int pid) {
		this.pid = pid;
	}

	public void show() {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Change Priority - PID " + pid);

		Label label = new Label("Select new priority:");

		ComboBox<String> priorityBox = new ComboBox<>(
				FXCollections.observableArrayList("Low", "Below Normal", "Normal", "Above Normal", "High", "Realtime"));

		priorityBox.getStyleClass().add("combo-box");
		priorityBox.setValue("Normal");

		Button applyBtn = new Button("Apply");
		applyBtn.getStyleClass().add("nav-button");

		applyBtn.setOnAction(e -> {
			String selected = priorityBox.getValue();
			boolean success = changePriority(pid, selected);
			Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(success ? "Priority changed!" : "Failed to change priority!");
			alert.show();
		});

		VBox container = new VBox(label, priorityBox, applyBtn);
		container.setSpacing(10);
		container.getStyleClass().add("windowns-pops");

		Scene scene = new Scene(container, 220, 160);
		scene.getStylesheets().add(
			    getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
		);
		ThemeManager.registerScene(scene); // registra a scene principal

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Altera a prioridade do processo de forma cross-platform
	 */
	private boolean changePriority(int pid, String priority) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				return changePriorityWindows(pid, priority);
			} else if (os.contains("linux") || os.contains("mac")) {
				return changePriorityUnix(pid, priority);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Linux / macOS via nice
	private boolean changePriorityUnix(int pid, String priority) throws IOException, InterruptedException {
		int niceValue;
		switch (priority) {
		case "Low" -> niceValue = 19;
		case "Below Normal" -> niceValue = 10;
		case "Normal" -> niceValue = 0;
		case "Above Normal" -> niceValue = -5;
		case "High" -> niceValue = -10;
		case "Realtime" -> niceValue = -20;
		default -> niceValue = 0;
		}

		Process p = new ProcessBuilder("renice", String.valueOf(niceValue), "-p", String.valueOf(pid)).inheritIO()
				.start();
		int exit = p.waitFor();
		return exit == 0;
	}

	// Windows via wmic
	private boolean changePriorityWindows(int pid, String priority) throws IOException, InterruptedException {
		// Valores aceitos: Idle, BelowNormal, Normal, AboveNormal, High, Realtime
		Process p = new ProcessBuilder("cmd", "/c",
				"wmic process where processid=" + pid + " CALL setpriority \"" + priority + "\"").inheritIO().start();
		int exit = p.waitFor();
		return exit == 0;
	}
}
