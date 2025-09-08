package br.com.mk.components.process.contextMenu;

import br.com.mk.data.ProcessMonitor.ProcessInfo;
import br.com.mk.utils.Property;
import br.com.mk.utils.ThemeManager;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MemoryMapWindow {

	@SuppressWarnings("unchecked")
	public static void show(ProcessInfo processInfo) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Memory Map: " + processInfo.name());

		TableView<Property> table = new TableView<>();

		// Preenche a tabela com informações de memória
		table.setItems(FXCollections.observableArrayList(
				new Property("Resident Memory", formatBytes(processInfo.residentMemoryBytes())),
				new Property("Virtual Memory", formatBytes(processInfo.virtualMemoryBytes())),
				new Property("Heap Memory (Approx.)", formatBytes(processInfo.residentMemoryBytes() / 2)), // estimativa
				new Property("Stack Size", "Not Available"), // OSHI não fornece direto
				new Property("Memory Usage (%)", String.format("%.2f", processInfo.memoryUsagePercent()))));

		TableColumn<Property, String> keyCol = new TableColumn<>("Property");
		keyCol.setCellValueFactory(cell -> cell.getValue().keyProperty());

		TableColumn<Property, String> valueCol = new TableColumn<>("Value");
		valueCol.setCellValueFactory(cell -> cell.getValue().valueProperty());

		table.getColumns().addAll(keyCol, valueCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		VBox vbox = new VBox(table);

		vbox.getStyleClass().add("windowns-pops");

		Scene scene = new Scene(vbox, 300, 240);
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
