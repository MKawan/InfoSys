package br.com.mk.components.buttons;

import br.com.mk.config.ConfigManager;
import br.com.mk.utils.ThemeManager;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ButtonMenu extends Button {

	private final ImageView icon;

	public ButtonMenu() {
		ConfigManager configManager = new ConfigManager();
		String iconPath = "/icons/systemFiles/" + ConfigManager.getString("themes", "Default") + "/menu.png";
		// Carrega o ícone
		Image image = new Image(getClass().getResourceAsStream(iconPath));
		icon = new ImageView(image);

		// Define tamanho do ícone
		icon.setFitWidth(16);
		icon.setFitHeight(16);

		// Coloca o ícone no botão
		this.getStyleClass().add("menu-button");
		this.setGraphic(icon);

		// update ThemeManager
		ThemeManager.addThemeChangeListener(this::updateIcon);
	}

	private void updateIcon(String theme) {
		String iconPath = "/icons/systemFiles/" + theme + "/menu.png";
		var stream = getClass().getResourceAsStream(iconPath);

		if (stream != null) {
			icon.setImage(new Image(stream));
		} else {
			System.err.println("Ícone não encontrado para tema: " + theme);
		}
	}
}
