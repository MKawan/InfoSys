package br.com.mk.components.process.contextMenu;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.mk.config.ConfigManager;
import br.com.mk.utils.ThemeManager;

/**
 * Janela para definir afinidade de CPU de um processo
 */
public class SetAffinityWindow {

	private final int pid;
	private int totalCores;

	public SetAffinityWindow(int pid, int totalCores) {
		this.pid = pid;
		this.totalCores = totalCores;
	}

	@SuppressWarnings("unused")
	public void show() {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Set CPU Affinity - PID " + pid);

		Label label = new Label("Select CPU cores to use:");

		List<CheckBox> coreCheckboxes = new ArrayList<>();
		for (int i = 0; i < totalCores; i++) {
			CheckBox cb = new CheckBox("CPU " + i);
			cb.setSelected(true); // por padrão todos ativados
			coreCheckboxes.add(cb);
//            cb.getStyleClass().add("custom-checkbox");
		}

		if (totalCores <= 0) {

			totalCores = 1;
		}

		Button applyBtn = new Button("Apply");
		applyBtn.getStyleClass().add("nav-button");

		applyBtn.setOnAction(e -> {
			List<Integer> selectedCores = new ArrayList<>();
			for (int i = 0; i < coreCheckboxes.size(); i++) {
				if (coreCheckboxes.get(i).isSelected()) {
					selectedCores.add(i);
				}
			}
			boolean success = setAffinity(pid, selectedCores);
			Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(success ? "Affinity applied!" : "Failed to apply affinity!");
			alert.show();
		});

		VBox container = new VBox(label);
		container.getChildren().addAll(coreCheckboxes);
		container.getChildren().add(applyBtn);
		container.setSpacing(10);
		container.getStyleClass().add("windowns-pops");

		Scene scene = new Scene(container, 250, 130 + 30 * totalCores);
		scene.getStylesheets().add(
			    getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
		);
		ThemeManager.registerScene(scene); // registra a scene principal

		stage.setScene(scene);
		stage.show();
	}

	private boolean setAffinity(int pid, List<Integer> cores) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				return setAffinityWindows(pid, cores);
			} else if (os.contains("linux") || os.contains("mac")) {
				return setAffinityUnix(pid, cores);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Linux/macOS via taskset ou pthread APIs (Linux só)
	private boolean setAffinityUnix(int pid, List<Integer> cores) throws IOException, InterruptedException {
		if (cores.isEmpty())
			return false;

		// Calcula a máscara em hexadecimal
		int mask = 0;
		for (int c : cores)
			mask |= 1 << c;
		Process p;

		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("linux")) {
			p = new ProcessBuilder("taskset", "-p", Integer.toHexString(mask), String.valueOf(pid)).inheritIO().start();
			int exit = p.waitFor();
			return exit == 0;
		} else if (os.contains("mac")) {
			// macOS não tem taskset, podemos usar `cpuset` se disponível ou apenas alertar
			System.err.println("Set affinity not supported natively on macOS");
			return false;
		}
		return false;
	}

	// Windows via PowerShell
	private boolean setAffinityWindows(int pid, List<Integer> cores) throws IOException, InterruptedException {
		if (cores.isEmpty())
			return false;

		// Cria a máscara (cada bit = core)
		int mask = 0;
		for (int c : cores)
			mask |= 1 << c;

		String command = "powershell -Command " + "Get-Process -Id " + pid
				+ " | ForEach-Object { $_.ProcessorAffinity = " + mask + " }";

		Process p = new ProcessBuilder("cmd", "/c", command).inheritIO().start();
		int exit = p.waitFor();
		return exit == 0;
	}
}
