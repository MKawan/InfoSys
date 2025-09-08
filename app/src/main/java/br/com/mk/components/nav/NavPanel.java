package br.com.mk.components.nav;

import br.com.mk.components.buttons.ButtonMenu;
import br.com.mk.components.buttons.ButtonNav;
import br.com.mk.components.menu.Menu;
import br.com.mk.components.window.Process;
import br.com.mk.utils.KeyMaps;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

public class NavPanel extends HBox {
	private static ContextMenu menuGeral;
	private final List<ButtonNav> buttons = new ArrayList<>();
	private OnNavChangeListener listener;
	private static boolean processOn;
	static ButtonMenu menu = new ButtonMenu();

	public NavPanel(Process processPanel) {

		processOn = false;

		this.setFocusTraversable(true);
		this.requestFocus();
		this.getStyleClass().add("nav-panel");

		ButtonNav process = new ButtonNav("Process");
		ButtonNav recourses = new ButtonNav("Recourses");
		ButtonNav fileSystem = new ButtonNav("Files System");
		menuGeral = Menu.createMenu(processPanel, processOn);

		buttons.add(process);
		buttons.add(recourses);
		buttons.add(fileSystem);

		for (ButtonNav btn : buttons) {
			btn.setOnAction(e -> {
				switchEnvironment(btn);
				if (btn == process) {
					processOn = true;
				} else {
					processOn = false;
				}
			});
		}

		this.setOnKeyPressed(e -> {
			String activeEnv = KeyMaps.handleNavPanel(e);
			for (ButtonNav btn : buttons) {
				if (btn.getText().equals(activeEnv)) {
					switchEnvironment(btn);
					break;
				}
			}
		});

		HBox buttonPRS = new HBox();
		buttonPRS.setSpacing(30);

		buttonPRS.getChildren().addAll(process, recourses, fileSystem);
		// Spacer que vai empurrar o ButtonMenu para a direita
		buttonPRS.setAlignment(Pos.CENTER);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		Region spacer2 = new Region();
		HBox.setHgrow(spacer2, Priority.ALWAYS);

		menu.setOnAction(e -> {
			ContextMenu menuGeral = Menu.createMenu(processPanel, processOn);
			menuGeral.show(menu, javafx.geometry.Side.BOTTOM, 0, 0);
		});

		this.setAlignment(Pos.CENTER);
		this.getChildren().addAll(spacer, buttonPRS, spacer2, menu);
	}

	private void switchEnvironment(ButtonNav clickedButton) {
		for (ButtonNav btn : buttons) {
			btn.setActive(btn == clickedButton);
		}

		if (listener != null) {
			listener.onNavChange(clickedButton.getText());
		}
	}

	public void setOnNavChangeListener(OnNavChangeListener listener) {
		this.listener = listener;
	}

	public interface OnNavChangeListener {
		void onNavChange(String environmentName);
	}

	// NavPanel.java
	public void updateActiveButton(String activeEnv) {
		for (ButtonNav btn : buttons) {
			if (btn.getText().equals(activeEnv)) {
				if (!btn.getStyleClass().contains("nav-button-active")) {
					btn.getStyleClass().add("nav-button-active");
				}
				btn.setDisable(true);
			} else {
				btn.getStyleClass().remove("nav-button-active");
				btn.setDisable(false);
			}
		}
	}

	public static void MenuShow(Process processPanel) {

		if (menuGeral == null) {
			menuGeral = Menu.createMenu(processPanel, processOn);
		}

		if (menuGeral.isShowing()) {
			menuGeral.hide(); // Fecha o menu se já estiver aberto
		} else {
			menuGeral.show(menu, javafx.geometry.Side.BOTTOM, 0, 0); // Mostra o menu
		}
	}
}
