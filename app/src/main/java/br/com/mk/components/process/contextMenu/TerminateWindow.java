package br.com.mk.components.process.contextMenu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import br.com.mk.utils.ThemeManager;

public class TerminateWindow {

	private final int pid;

	public TerminateWindow(int pid) {
		this.pid = pid;
	}

	public void show() {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Terminate Process - PID " + pid);

		Label label = new Label("Do you want to terminate this process?");

		Button confirmBtn = new Button("Confirm");
		confirmBtn.getStyleClass().add("nav-button");

		Button cancelBtn = new Button("Cancel");
		cancelBtn.getStyleClass().add("nav-button");

		confirmBtn.setOnAction(e -> {
			boolean success = terminateProcess(pid);
			Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(success ? "Process terminated!" : "Failed to terminate process.");
			alert.show();
			stage.close();
		});

		cancelBtn.setOnAction(e -> stage.close());

		HBox hbox = new HBox(70, confirmBtn, cancelBtn);
		hbox.setAlignment(Pos.BOTTOM_CENTER);
		hbox.setStyle("-fx-padding: 10;");
		VBox vbox = new VBox(10, label, hbox);
		vbox.getStyleClass().add("windowns-pops");

		Scene scene = new Scene(vbox, 300, 120);

		ThemeManager.registerScene(scene); // registra a scene principal

		stage.setScene(scene);
		stage.show();
	}

	private boolean terminateProcess(int pid) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				return terminateWindows(pid);
			} else if (os.contains("linux") || os.contains("mac")) {
				return terminateUnix(pid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Linux/macOS
	private boolean terminateUnix(int pid) throws IOException, InterruptedException {
		Process p = new ProcessBuilder("kill", "-TERM", String.valueOf(pid)).inheritIO().start();
		return p.waitFor() == 0;
	}

	// Windows via PowerShell
	private boolean terminateWindows(int pid) throws IOException, InterruptedException {
		String command = "powershell -Command Stop-Process -Id " + pid + " -Force";
		Process p = new ProcessBuilder("cmd", "/c", command).inheritIO().start();
		return p.waitFor() == 0;
	}
}
