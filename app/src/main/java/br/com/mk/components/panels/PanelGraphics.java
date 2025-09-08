package br.com.mk.components.panels;

import br.com.mk.components.cardGraphics.CpuLinearGraphics;
import br.com.mk.components.cardGraphics.MemoryLinearGraphics;
import br.com.mk.components.cardGraphics.NetworkLinearGraphics;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PanelGraphics extends VBox {

	private final CpuLinearGraphics cpuLinearGraphics;
	private final MemoryLinearGraphics memoryLinearGraphics;
	private final NetworkLinearGraphics networkLinearGraphics;

	public PanelGraphics() {
		this.setSpacing(15);

		this.getStyleClass().add("panel-card");
		cpuLinearGraphics = new CpuLinearGraphics();
		memoryLinearGraphics = new MemoryLinearGraphics();
		networkLinearGraphics = new NetworkLinearGraphics();

		VBox.setVgrow(cpuLinearGraphics, Priority.ALWAYS);
		VBox.setVgrow(memoryLinearGraphics, Priority.ALWAYS);
		VBox.setVgrow(networkLinearGraphics, Priority.ALWAYS);

		this.getChildren().addAll(cpuLinearGraphics, memoryLinearGraphics, networkLinearGraphics);
	}

}
