package br.com.mk.components.menu;

import br.com.mk.config.ConfigManager;
import br.com.mk.utils.ProcessUtils;
import br.com.mk.components.window.Process;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class ProcessSelect extends ScrollPane {

	private final VBox container;

	public ProcessSelect(Process processTable) {
		container = new VBox(10);
		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(5);
		grid.setAlignment(Pos.CENTER);

		String[] columnKeys = { "PID", "PPID", "Name", "Command Line", "User", "UserID", "Group", "GroupID", "State",
				"Priority", "Thread Count", "Bitness", "Uptime", "Start Time", "Kernel Time", "User Time",
				"Resident Memory", "Virtual Memory", "Disk Read", "Disk Write", "Open Files" };

		CheckBox[] checkBoxes = new CheckBox[columnKeys.length];

		for (int i = 0; i < columnKeys.length; i++) {
			String columnName = columnKeys[i];
			String key = ProcessUtils.getConfigKey(columnName); // gera key sem espaços
			boolean selected = ConfigManager.getBoolean(key, false);

			CheckBox cb = new CheckBox(columnName); // mostra o nome legível
			cb.setSelected(selected);

			// Salva no array para o botão Default funcionar
			checkBoxes[i] = cb;

			cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
				ConfigManager.setBoolean(key, newVal); // salva usando key padronizada
				if (processTable != null) {
					processTable.setColumnVisible(columnName, newVal); // passa o nome legível para tabela
				}
			});

			grid.add(cb, i % 2, i / 2);
		}

		// Botão Default
		Button defaultBtn = new Button("Default");
		defaultBtn.getStyleClass().add("nav-button");

		defaultBtn.setOnAction(e -> {
			// Lista de colunas padrão usando os nomes legíveis
			List<String> defaultColumns = Arrays.asList("PID", "PPID", "Name", "User", "Thread Count",
					"Resident Memory", "Disk Read", "Disk Write", "Open Files", "State", "CPU (%)", "Memory (%)" // ✅
																													// Adicionando
																													// CPU
																													// e
																													// Memory
																													// com
																													// nome
																													// certo
			);

			for (int i = 0; i < columnKeys.length; i++) {
				String columnName = columnKeys[i];
				String key = ProcessUtils.getConfigKey(columnName);

				// Verifica se o nome legível está na lista
				boolean visible = defaultColumns.contains(columnName);

				// Atualiza CheckBox
				checkBoxes[i].setSelected(visible);

				// Salva no config.properties
				ConfigManager.setBoolean(key, visible);

				// Atualiza tabela se existir
				if (processTable != null) {
					processTable.setColumnVisible(columnName, visible);
				}
			}
		});

		HBox hBoxDefaultBtn = new HBox(defaultBtn);
		HBox.setHgrow(defaultBtn, Priority.ALWAYS);
		hBoxDefaultBtn.setAlignment(Pos.TOP_RIGHT);

		VBox.setVgrow(grid, Priority.ALWAYS);

		container.setAlignment(Pos.CENTER);
		container.setPadding(new Insets(20));

		container.getChildren().addAll(hBoxDefaultBtn, grid);
		container.getStyleClass().add("root");
		this.setContent(container);
		this.setFitToWidth(true);
		this.getStyleClass().add("root");
	}
}
