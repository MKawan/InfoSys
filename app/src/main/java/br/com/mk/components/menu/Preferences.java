package br.com.mk.components.menu;

import br.com.mk.components.window.Process;
import br.com.mk.config.ConfigManager;
import br.com.mk.utils.ThemeManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Preferences extends Stage {

	VBox geralBox = new VBox(20);
	VBox boxTheme = new VBox(20);
	VBox boxSelec = new VBox(20);

	public Preferences(Process process) {

		// 0.5, 300.0, 600.0
		// 0.25, 0.5, 10.0
//		AjustableButton adjustableButtonGraphics = new AjustableButton("value-graphics-inteval", 10, 0.5, 300.0, 600.0);
//		AjustableButton adjustableButtonUpdate = new AjustableButton("value-update-inteval", 3, 0.25, 0.5, 10.0);
//		
//		geralBox.getChildren().addAll(adjustableButtonGraphics, adjustableButtonUpdate);

		// --- CheckBoxes para colunas ---
		Themes themes = new Themes();
		ProcessSelect processSelect = new ProcessSelect(process);

		Text textThemes = new Text("Themes");
		textThemes.getStyleClass().add("title");
		textThemes.setStyle("-fx-padding: 20;");

		Text textProcess = new Text("Process Table Select");
		textProcess.getStyleClass().add("title");
		textProcess.setStyle("-fx-padding: 20;");

		// Vbox themes
		boxTheme.getChildren().addAll(textThemes, themes);
		boxTheme.setStyle("-fx-max-height: 300;");
		boxTheme.setAlignment(Pos.CENTER);

		// Vbox Select
		boxSelec.getChildren().addAll(textProcess, processSelect);
		boxSelec.setStyle("-fx-max-height: 300;");
		boxSelec.setAlignment(Pos.CENTER);

		VBox.setVgrow(boxTheme, Priority.ALWAYS);
		VBox.setVgrow(boxSelec, Priority.ALWAYS);

		geralBox.getStyleClass().add("root");
		geralBox.getChildren().addAll(boxTheme, boxSelec);
		geralBox.setAlignment(Pos.CENTER);

		// --- Scene ---
		this.setMaxHeight(600);
		this.setMaxWidth(500);

		// Torna a janela modal (bloqueia interação com a principal)
		this.initModality(Modality.APPLICATION_MODAL);

		Scene scene = new Scene(geralBox, 500, 600);
		scene.getStylesheets().add(
			    getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
		);

		ThemeManager.registerScene(scene); // registra a scene principal
		this.setScene(scene);
		this.setTitle("Preferences");
		this.show();
	}
}
