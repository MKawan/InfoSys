package br.com.mk.components.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

import br.com.mk.utils.ThemeManager;

public class SystemShortcuts extends Stage {

	public SystemShortcuts() {

		this.initModality(Modality.APPLICATION_MODAL);
		this.setTitle("System Shortcuts");

		// Container principal
		HBox card = new HBox();
		card.getStyleClass().add("root");
		card.setAlignment(Pos.TOP_CENTER);
		card.setPadding(new Insets(20));
		card.setSpacing(5);
		// Colunas
		VBox geralBox = new VBox(10);
		VBox processBox = new VBox(10);

		geralBox.setAlignment(Pos.TOP_LEFT);
		processBox.setAlignment(Pos.TOP_RIGHT);

		// Spacer flexível entre as colunas
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// Lista de atalhos
		List<List<String>> atalhosGeral = Arrays.asList(Arrays.asList("General"), Arrays.asList("F1"),
				Arrays.asList("F10"), Arrays.asList("Alt", "+", "↵"), Arrays.asList("Ctrl", "+", "W"),
				Arrays.asList("Alt", "+", "1"), Arrays.asList("Alt", "+", "2"), Arrays.asList("Alt", "+", "3"),
				Arrays.asList("Process"), Arrays.asList("↵"), Arrays.asList("Alt", "+", "M"),
				Arrays.asList("Alt", "+", "O"), Arrays.asList("Alt", "+", "S"), Arrays.asList("CTRL", "+", "U"),
				Arrays.asList("CTRL", "+", "S"), Arrays.asList("Alt", "+", "C"), Arrays.asList("Alt", "+", "E"),
				Arrays.asList("Alt", "+", "K"));
		List<List<String>> descrition = Arrays.asList(Arrays.asList(""), // geral
				Arrays.asList("Menu"), Arrays.asList("Help"), Arrays.asList("Properties"), Arrays.asList("Shortcuts"),
				Arrays.asList("Process"), Arrays.asList("Recourses"), Arrays.asList("Files System"), Arrays.asList(""), // process
				Arrays.asList("Row select + Enter -> Process Status"), Arrays.asList("Alt", "+", "↵"),
				Arrays.asList("Memory Map"), Arrays.asList("Open Files"), Arrays.asList("SetAffinity"),
				Arrays.asList("Suspend"), Arrays.asList("Resume"), Arrays.asList("Terminate"), Arrays.asList("Kill"));

		boolean depoisDoProcess = false;

		for (int i = 0; i < atalhosGeral.size(); i++) {
			List<String> atalho = atalhosGeral.get(i);
			String desc = descrition.get(i).isEmpty() ? null : descrition.get(i).get(0);

			HBox hBox = new HBox(10);
			hBox.setAlignment(Pos.CENTER_LEFT);

			// Caso título "Process"
			if (atalho.contains("Process")) {
				Text text = new Text("Process");
				text.getStyleClass().add("text-shortcut");
				VBox key = new VBox(text);
				key.getStyleClass().add("title");
				hBox.getChildren().add(key);
				processBox.getChildren().add(hBox);
				depoisDoProcess = true;
				continue;
			}

			// Adiciona as teclas
			for (String tecla : atalho) {
				Text text = new Text(tecla);
				text.getStyleClass().add("text-shortcut");

				VBox key = new VBox(text);
				key.setAlignment(Pos.CENTER);

				if (!"+".equals(tecla) && !"General".equals(tecla)) {
					key.getStyleClass().add("card-shortcut");
				} else if ("General".equals(tecla)) {
					key.getStyleClass().add("title");
				}

				hBox.getChildren().add(key);
			}

			// Adiciona descrição ao lado, no mesmo HBox
			if (desc != null) {
				Text comment = new Text(desc);
				comment.getStyleClass().add("text-shortcut");
				comment.setWrappingWidth(200); // opcional, controla quebra de linha
				hBox.getChildren().add(comment);
			}

			if (depoisDoProcess) {
				processBox.getChildren().add(hBox);
			} else {
				geralBox.getChildren().add(hBox);
			}
		}

		// Adiciona as colunas e o spacer
		card.getChildren().addAll(geralBox, spacer, processBox);

		Scene scene = new Scene(card, 800, 600);
//        scene.getStylesheets().add("/css/windXP.css");
		ThemeManager.registerScene(scene); // registra a scene principal

		this.setTitle("Shortcuts");
		this.setScene(scene);
		this.show();
	}
}
