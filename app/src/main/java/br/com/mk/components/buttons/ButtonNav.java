package br.com.mk.components.buttons;

import javafx.scene.control.Button;

public class ButtonNav extends Button {
	private boolean active = false;

	public ButtonNav(String buttonName) {
		super(buttonName);
		this.getStyleClass().add("nav-button");
	}

	public void setActive(boolean active) {
		this.active = active;
		if (active) {
			this.getStyleClass().add("active");
//            this.setDisable(true); // opcional, se quiser impedir clique
		} else {
			this.getStyleClass().remove("active");
			this.setDisable(false);
		}
	}

	public boolean isActive() {
		return active;
	}
}
