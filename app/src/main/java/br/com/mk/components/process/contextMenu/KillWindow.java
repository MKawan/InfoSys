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

public class KillWindow {

	private final int pid;

	public KillWindow(int pid) {
		this.pid = pid;
	}

	public void show() {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Kill Process - PID " + pid);

		Label label = new Label("Are you sure you want to kill this process?");

		Button confirmBtn = new Button("Confirm");
		confirmBtn.getStyleClass().add("nav-button");

		Button cancelBtn = new Button("Cancel");
		cancelBtn.getStyleClass().add("nav-button");

		confirmBtn.setOnAction(e -> {
			boolean success = killProcess(pid);
			Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(success ? "Process killed!" : "Failed to kill process.");
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
		scene.getStylesheets().add(
			    getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
		);
		stage.setScene(scene);
		stage.show();
	}

	private boolean killProcess(int pid) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				return killWindows(pid);
			} else if (os.contains("linux") || os.contains("mac")) {
				return killUnix(pid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Linux/macOS
	private boolean killUnix(int pid) throws IOException, InterruptedException {
		Process p = new ProcessBuilder("kill", "-9", String.valueOf(pid)).inheritIO().start();
		return p.waitFor() == 0;
	}

	// Windows via taskkill
	private boolean killWindows(int pid) throws IOException, InterruptedException {
		Process p = new ProcessBuilder("taskkill", "/PID", String.valueOf(pid), "/F").inheritIO().start();
		return p.waitFor() == 0;
	}
}
