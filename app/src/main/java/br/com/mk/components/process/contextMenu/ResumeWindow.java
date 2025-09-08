package br.com.mk.components.process.contextMenu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import br.com.mk.config.ConfigManager;
import br.com.mk.utils.ThemeManager;

public class ResumeWindow {

	private final int pid;

	public ResumeWindow(int pid) {
		this.pid = pid;
	}

	public void show() {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Resume Process - PID " + pid);

		Label label = new Label("Do you want to resume this process?");

		Button confirmBtn = new Button("Confirm");
		confirmBtn.getStyleClass().add("nav-button");

		Button cancelBtn = new Button("Cancel");
		cancelBtn.getStyleClass().add("nav-button");

		confirmBtn.setOnAction(e -> {
			boolean success = resumeProcess(pid);
			Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(success ? "Process resumed!" : "Failed to resume process.");
			alert.show();
			stage.close();
		});

		cancelBtn.setOnAction(e -> stage.close());

		HBox hbox = new HBox(70, confirmBtn, cancelBtn);
		hbox.setAlignment(Pos.BOTTOM_CENTER);
		hbox.setStyle("-fx-padding: 10;");
		VBox vbox = new VBox(10, label, hbox);
		vbox.setStyle("-fx-padding: 10;");

		vbox.getStyleClass().add("windowns-pops");

		Scene scene = new Scene(vbox, 300, 120);
		scene.getStylesheets().add(
			    getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
		);
		ThemeManager.registerScene(scene); // registra a scene principal

		stage.setScene(scene);
		stage.show();
	}

	private boolean resumeProcess(int pid) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				return resumeWindows(pid);
			} else if (os.contains("linux") || os.contains("mac")) {
				return resumeUnix(pid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Linux/macOS
	private boolean resumeUnix(int pid) throws IOException, InterruptedException {
		Process p = new ProcessBuilder("kill", "-CONT", String.valueOf(pid)).inheritIO().start();
		return p.waitFor() == 0;
	}

	// Windows via PowerShell
	private boolean resumeWindows(int pid) throws IOException, InterruptedException {
		String command = "powershell -Command " + "Resume-Process -Id " + pid;
		Process p = new ProcessBuilder("cmd", "/c", command).inheritIO().start();
		return p.waitFor() == 0;
	}
}
