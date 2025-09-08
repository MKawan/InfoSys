package br.com.mk.components.process.contextMenu;

import br.com.mk.data.ProcessMonitor.ProcessInfo;
import br.com.mk.utils.Property;
import br.com.mk.utils.ThemeManager;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProcessPropertiesWindow {

	@SuppressWarnings("unchecked")
	public static void show(ProcessInfo processInfo) {

		Stage stage = new Stage();
		stage.setTitle("Process Properties: " + processInfo.name());

		TableView<Property> table = new TableView<>();
		table.setItems(FXCollections.observableArrayList(new Property("PID", String.valueOf(processInfo.pid())),
				new Property("PPID", String.valueOf(processInfo.ppid())), new Property("Name", processInfo.name()),
				new Property("User", processInfo.user()),
				new Property("CPU (%)", String.format("%.2f", processInfo.cpuUsagePercent())),
				new Property("Memory (%)", String.format("%.2f", processInfo.memoryUsagePercent())),
				new Property("Threads", String.valueOf(processInfo.threadCount())),
				new Property("RAM", formatBytes(processInfo.residentMemoryBytes())),
				new Property("Disk Read", formatBytes(processInfo.bytesRead())),
				new Property("Disk Write", formatBytes(processInfo.bytesWritten())),
				new Property("Open Files", String.valueOf(processInfo.openFiles())),
				new Property("State", processInfo.state())));

		// Coluna Key
		TableColumn<Property, String> keyCol = new TableColumn<>("Key");
		keyCol.setCellValueFactory(cell -> cell.getValue().keyProperty());

		// Coluna Value
		TableColumn<Property, String> valueCol = new TableColumn<>("Value");
		valueCol.setCellValueFactory(cell -> cell.getValue().valueProperty());

		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.getColumns().addAll(keyCol, valueCol);

		VBox vbox = new VBox(table);
		vbox.getStyleClass().add("windowns-pops");

		Scene scene = new Scene(vbox, 450, 300);
		ThemeManager.registerScene(scene); // registra a scene principal

		stage.setScene(scene);
		stage.show();
	}

	private static String formatBytes(long bytes) {
		double kb = bytes / 1024.0;
		if (kb < 1024)
			return String.format("%.2f KB", kb);
		double mb = kb / 1024.0;
		if (mb < 1024)
			return String.format("%.2f MB", mb);
		return String.format("%.2f GB", mb / 1024.0);
	}
}
