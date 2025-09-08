package br.com.mk.components.window;

import br.com.mk.components.menu.ProcessContextMenu;
import br.com.mk.config.ConfigManager;
import br.com.mk.data.ProcessMonitor;
import br.com.mk.data.ProcessMonitor.ProcessInfo;
import br.com.mk.utils.KeyMaps;
import br.com.mk.utils.ProcessUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Process extends VBox {

	private final TableView<ProcessInfo> table;
	private final ObservableList<ProcessInfo> data;
	private final ObservableList<ProcessInfo> originalList;
	private final TextField searchField;

	private final ProcessMonitor monitor = new ProcessMonitor();
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private final Map<String, TableColumn<ProcessInfo, String>> allColumns;

	private boolean allProcesses;
	private boolean myProcesses;
	private final String currentUser = System.getProperty("user.name");

	public Process() {

		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

		data = FXCollections.observableArrayList();
		originalList = FXCollections.observableArrayList();
		table.setItems(data);
		table.setPlaceholder(new Label("Wait..."));

		searchField = new TextField();
		searchField.setPromptText("Search process...");

		HBox seacHBox = new HBox();
		seacHBox.setAlignment(Pos.TOP_LEFT);
		seacHBox.setPadding(new Insets(20));
		HBox.setHgrow(seacHBox, Priority.ALWAYS);
		seacHBox.getChildren().add(searchField);

		VBox.setVgrow(table, Priority.ALWAYS);
		this.getChildren().addAll(seacHBox, table);
		this.getStyleClass().add("process-view");

		allColumns = ProcessUtils.createColumns();
		setupInitialColumns();
		startSearchListener();

		// Carrega filtros
		allProcesses = ConfigManager.getBoolean("process.allProcesses", false);
		myProcesses = ConfigManager.getBoolean("process.myProcesses", false);

		setupRowClickListener();
		setupColumnsFromConfig();

		// Atualização periódica
		executor.scheduleAtFixedRate(this::refreshBackground, 0, 3, TimeUnit.SECONDS);
	}

	@SuppressWarnings("unchecked")
	private void setupInitialColumns() {
		table.getColumns().addAll(allColumns.get("PID"), allColumns.get("PPID"), allColumns.get("Name"),
				allColumns.get("User"), allColumns.get("CPU (%)"), allColumns.get("Memory (%)"),
				allColumns.get("Thread Count"), allColumns.get("Resident Memory"), allColumns.get("Disk Read"),
				allColumns.get("Disk Write"), allColumns.get("Open Files"), allColumns.get("State"));
	}

	private void setupRowClickListener() {
		table.setRowFactory(tv -> {
			TableRow<ProcessInfo> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				ProcessInfo clicked = row.getItem();
				if (clicked == null)
					return;

				if (event.getClickCount() == 1 && event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
					table.setOnKeyPressed(eventKey -> {
						System.out.println("Atalho detectado funciona ");
						KeyMaps.handleComboKey(row, eventKey);
					});
				}
				if (event.getClickCount() == 2 && event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
					new ProcessStatusWindow(clicked.pid()).show();
				} else if (event.getClickCount() == 1 && event.getButton() == javafx.scene.input.MouseButton.SECONDARY
						&& table.getSelectionModel().getSelectedItems().contains(clicked)) {
					ContextMenu menu = ProcessContextMenu.createMenu(row);
					menu.show(row, event.getScreenX(), event.getScreenY());
					event.consume();
				}

			});

			return row;
		});
	}

	private void setupColumnsFromConfig() {
		for (Map.Entry<String, TableColumn<ProcessInfo, String>> entry : allColumns.entrySet()) {
			String columnName = entry.getKey();
			String key = ProcessUtils.getConfigKey(columnName);
			boolean visible = ConfigManager.getBoolean(key, defaultColumnValue(columnName));
			setColumnVisible(columnName, visible);
		}
	}

	public void setColumnVisible(String columnName, boolean visible) {
		TableColumn<ProcessInfo, String> col = allColumns.get(columnName);
		if (col != null) {
			if (visible && !table.getColumns().contains(col))
				table.getColumns().add(col);
			else if (!visible)
				table.getColumns().remove(col);

			// Salva no config.properties
			ConfigManager.setBoolean(ProcessUtils.getConfigKey(columnName), visible);
		}
	}

	public void setAllProcesses(boolean value) {
		allProcesses = value;
		ConfigManager.setBoolean("process.allProcesses", value);
		refreshBackground();
	}

	public void setMyProcesses(boolean value) {
		myProcesses = value;
		ConfigManager.setBoolean("process.myProcesses", value);
		refreshBackground();
	}

	private void refreshBackground() {
		List<ProcessInfo> processes = monitor.getRunningProcesses(Integer.MAX_VALUE);
		if (!allProcesses)
			processes.removeIf(p -> p.userID().equals("0"));
		if (myProcesses)
			processes.removeIf(p -> !p.user().equals(currentUser));

		Platform.runLater(() -> {
			originalList.setAll(processes);
			applySearchFilter();
		});
	}

	private void applySearchFilter() {
		List<ProcessInfo> filtered = ProcessUtils.filterProcesses(originalList, searchField.getText());

		// Salva PIDs das linhas selecionadas atualmente
		List<Integer> selectedPids = table.getSelectionModel().getSelectedItems().stream().map(ProcessInfo::pid)
				.toList();

		// Salva a primeira linha visível para scroll aproximado
		int firstVisibleIndex = table.getSelectionModel().getSelectedIndex();

		// Atualiza a lista de dados
		data.setAll(filtered);

		// Restaura seleção
		table.getSelectionModel().clearSelection();
		for (int i = 0; i < filtered.size(); i++) {
			if (selectedPids.contains(filtered.get(i).pid())) {
				table.getSelectionModel().select(i);
			}
		}

		// Tenta restaurar o scroll aproximado
		if (!filtered.isEmpty() && firstVisibleIndex >= 0 && firstVisibleIndex < filtered.size()) {
			table.scrollTo(firstVisibleIndex);
		}
	}

	private void startSearchListener() {
		searchField.textProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(this::applySearchFilter));
	}

	public void manualRefresh() {
		refreshBackground();
	}

	public void shutdown() {
		executor.shutdownNow();
	}

	public void getEvent() {

	}

	private boolean defaultColumnValue(String columnName) {
		switch (columnName) {
		case "PID":
		case "PPID":
		case "Name":
		case "User":
		case "CPU (%)":
		case "Memory (%)":
		case "Thread Count":
		case "Resident Memory":
		case "Disk Read":
		case "Disk Write":
		case "Open Files":
		case "State":
			return true;
		default:
			return false;
		}
	}
}
