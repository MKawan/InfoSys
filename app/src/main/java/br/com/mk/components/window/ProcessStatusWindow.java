package br.com.mk.components.window;

import br.com.mk.components.graphics.DonutChart;
import br.com.mk.config.ConfigManager;
import br.com.mk.data.ProcessMonitor;
import br.com.mk.data.ProcessMonitor.ProcessInfo;
import br.com.mk.utils.ProcessStatusUtils;
import br.com.mk.utils.ThemeManager;
import br.com.mk.data.MemoryMonitor;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessStatusWindow {

	private final int pid;
	private final ProcessMonitor monitor = new ProcessMonitor();
	private final MemoryMonitor memoryMonitor = new MemoryMonitor();

	private final DonutChart cpuDonut;
	private final DonutChart ramDonut;
	private final VBox infoTable;
	private final Text textTitle;
	private final Text textPorcenTextCPU;
	private final Text textPorcenTextRAM;
	private final Label loadingLabel;
	private final DecimalFormat df = new DecimalFormat("0.00");
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public ProcessStatusWindow(int pid) {
		this.pid = pid;

		textPorcenTextCPU = new Text();
		textPorcenTextRAM = new Text();

		textTitle = new Text();
		textTitle.getStyleClass().add("text-window-status-data");

		cpuDonut = new DonutChart(0, 100, 4, textPorcenTextCPU);
		ramDonut = new DonutChart(0, 100, 4, textPorcenTextRAM);

		infoTable = new VBox(5);
		infoTable.setPadding(new Insets(10));
		infoTable.getStyleClass().add("card");
		infoTable.setPrefWidth(250);

		loadingLabel = new Label("Wait...");
		loadingLabel.getStyleClass().add("text-window-process-loadlabel");

		// Atualização periódica
		executor.scheduleAtFixedRate(this::refresh, 0, 3, TimeUnit.SECONDS);
	}

	public void show() {
		Stage stage = new Stage();
		stage.setTitle("Process Status - PID " + pid);

		StackPane root = new StackPane(loadingLabel);
		StackPane.setAlignment(loadingLabel, Pos.CENTER);

		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(
			    getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
		);
		ThemeManager.registerScene(scene); // registra a scene principal

		stage.setScene(scene);
		stage.show();

		// Carrega dados em background
		new Thread(() -> {
			final List<ProcessInfo>[] processesHolder = new List[1];
			do {
				processesHolder[0] = monitor.getRunningProcesses(Integer.MAX_VALUE).stream().filter(p -> p.pid() == pid)
						.toList();
				try {
					Thread.sleep(200);
				} catch (InterruptedException ignored) {
				}
			} while (processesHolder[0].isEmpty());

			Platform.runLater(() -> {
				ProcessInfo p = processesHolder[0].get(0);

				// Atualiza donuts
				textPorcenTextCPU.setText(df.format(p.cpuUsagePercent()) + "%");
				textPorcenTextRAM.setText(df.format(p.memoryUsagePercent()) + "%");
				cpuDonut.updateValues(p.cpuUsagePercent(), 100 - p.cpuUsagePercent());

				long totalRamBytes = memoryMonitor.getTotalMemoryMB() * 1024 * 1024L;
				double ramPercent = ((double) p.residentMemoryBytes() / totalRamBytes) * 100;
				ramDonut.updateValues(ramPercent, 100 - ramPercent);

				textTitle.setText(p.name());
				ProcessStatusUtils.updateInfoTable(p, infoTable);

				VBox cpuContainerBox = new VBox(new Label("CPU USAGE"), cpuDonut.getChart());
				cpuContainerBox.setAlignment(Pos.CENTER);
				VBox ramContainerBox = new VBox(new Label("RAM USAGE"), ramDonut.getChart());
				ramContainerBox.setAlignment(Pos.CENTER);

				HBox donutsBox = new HBox(20, cpuContainerBox, ramContainerBox);
				donutsBox.setAlignment(Pos.CENTER);
				donutsBox.getStyleClass().add("card");

				VBox contentBox = new VBox(10, textTitle, donutsBox, infoTable);
				contentBox.setPadding(new Insets(10));
				contentBox.setAlignment(Pos.TOP_CENTER);

				root.getChildren().clear();
				root.getChildren().add(contentBox);

			});
		}).start();
	}

	private void refresh() {
		Platform.runLater(() -> {
			List<ProcessInfo> processes = monitor.getRunningProcesses(Integer.MAX_VALUE).stream()
					.filter(p -> p.pid() == pid).toList();

			if (!processes.isEmpty()) {
				ProcessInfo p = processes.get(0);
				loadingLabel.setVisible(false);
				textTitle.setText(p.name());
				textPorcenTextCPU.setText(df.format(p.cpuUsagePercent()) + "%");
				textPorcenTextRAM.setText(df.format(p.memoryUsagePercent()) + "%");

				ProcessStatusUtils.updateInfoTable(p, infoTable);

				double cpuPercent = p.cpuUsagePercent();
				cpuDonut.updateValues(cpuPercent, 100 - cpuPercent);

				long totalRamBytes = memoryMonitor.getTotalMemoryMB() * 1024 * 1024L;
				double ramPercent = ((double) p.residentMemoryBytes() / totalRamBytes) * 100;
				ramDonut.updateValues(ramPercent, 100 - ramPercent);
			}
		});
	}

}
