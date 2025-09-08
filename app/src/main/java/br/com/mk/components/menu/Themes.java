package br.com.mk.components.menu;

import br.com.mk.config.ConfigManager;
import br.com.mk.utils.ThemeManager;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Themes extends ScrollPane {

	private final VBox container;
	private final ToggleGroup toggleGroup;

	public Themes() {

		container = new VBox(10); // espaçamento vertical de 10px
		toggleGroup = new ToggleGroup();

		String[] themesText = { "Default", "WindXP", "Bespin", "Dracula", "Mono", "Retro", "SynthWave84" };

		// Valor inicial salvo no config
		String selectedTheme = ConfigManager.getString("themes", "Default");

		for (String theme : themesText) {

			HBox seleBox = new HBox(10);
			RadioButton rb = new RadioButton(theme);
			rb.setContentDisplay(ContentDisplay.LEFT);
			rb.getStyleClass().add("radio-button");
			ImageView icon = new ImageView(
					new Image(getClass().getResourceAsStream("/icons/preferences/" + theme + ".png")));
			icon.getStyleClass().add("disk-icon"); // classe CSS

			rb.setToggleGroup(toggleGroup);

			// Seleciona o tema salvo no config
			if (theme.equals(selectedTheme)) {
				rb.setSelected(true);
			}

			// Atualiza o config e o CSS da Scene dinamicamente
			rb.setOnAction(e -> ThemeManager.setTheme(theme));

			seleBox.getChildren().addAll(icon, rb);
			container.getChildren().add(seleBox);
		}

		container.getStyleClass().add("root");
		this.setContent(container);
		this.setFitToWidth(true);

		this.getStyleClass().add("root");
	}
}
