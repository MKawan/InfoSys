package br.com.mk.components.process.contextMenu;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;

import br.com.mk.config.ConfigManager;
import br.com.mk.utils.OpenFile;
import br.com.mk.utils.ThemeManager;

public class OpenFilesWindow {

	private final int pid;

	public OpenFilesWindow(int pid) {
		this.pid = pid;
	}

	@SuppressWarnings("unchecked")
	public void show() {
		Stage stage = new Stage();
		stage.setTitle("Open Files - PID " + pid);

		TableView<OpenFile> table = new TableView<>();
		ObservableList<OpenFile> data = getOpenFiles(pid);
		table.setItems(data);

		// Colunas
		TableColumn<OpenFile, String> fdCol = new TableColumn<>("FD");
		fdCol.setCellValueFactory(cell -> cell.getValue().fdProperty());
		fdCol.setMaxWidth(70);

		TableColumn<OpenFile, String> pathCol = new TableColumn<>("Path");
		pathCol.setCellValueFactory(cell -> cell.getValue().pathProperty());
		// Cell factory para cortar com reticências
		pathCol.setCellFactory(new Callback<TableColumn<OpenFile, String>, TableCell<OpenFile, String>>() {
			@Override
			public TableCell<OpenFile, String> call(TableColumn<OpenFile, String> param) {
				return new TableCell<>() {
					private final Text text = new Text();

					{
						text.wrappingWidthProperty().bind(param.widthProperty().subtract(10));
						text.setTextAlignment(TextAlignment.LEFT);
					}

					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setGraphic(null);
						} else {
							text.setText(item);
							setGraphic(text);
						}
					}
				};
			}
		});

		TableColumn<OpenFile, String> typeCol = new TableColumn<>("Type");
		typeCol.setCellValueFactory(cell -> cell.getValue().typeProperty());
		typeCol.setMaxWidth(100);

		TableColumn<OpenFile, String> modeCol = new TableColumn<>("Mode");
		modeCol.setCellValueFactory(cell -> cell.getValue().modeProperty());
		modeCol.setMaxWidth(80);

		TableColumn<OpenFile, String> posCol = new TableColumn<>("Position");
		posCol.setCellValueFactory(cell -> cell.getValue().positionProperty());
		posCol.setMaxWidth(80);

		table.getColumns().addAll(fdCol, pathCol, typeCol, modeCol, posCol);

		// Política de redimensionamento: só Path cresce
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

		VBox container = new VBox(table);
		container.setSpacing(5);
		VBox.setVgrow(table, Priority.ALWAYS);
		container.getStyleClass().add("windowns-pops");

		Scene scene = new Scene(container, 650, 500);
		scene.getStylesheets().add(
			    getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
		);
		ThemeManager.registerScene(scene); // registra a scene principal

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Coleta os arquivos abertos no Linux (/proc/[pid]/fd)
	 */
	private ObservableList<OpenFile> getOpenFiles(int pid) {
		ObservableList<OpenFile> list = javafx.collections.FXCollections.observableArrayList();
		File fdDir = new File("/proc/" + pid + "/fd");
		if (fdDir.exists() && fdDir.isDirectory()) {
			for (File f : fdDir.listFiles()) {
				try {
					String path = f.getCanonicalPath();
					list.add(new OpenFile(f.getName(), path, "File", "r/w", "-"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
