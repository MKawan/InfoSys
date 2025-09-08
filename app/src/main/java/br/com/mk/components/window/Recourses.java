package br.com.mk.components.window;

import br.com.mk.components.panels.PanelCard;
import br.com.mk.components.panels.PanelGraphics;
import javafx.scene.layout.VBox;

public class Recourses extends VBox {
	PanelCard panelCard = new PanelCard();
	PanelGraphics panelGraphics = new PanelGraphics();

	public Recourses() {
		this.getChildren().addAll(panelCard, panelGraphics);
	}

}
