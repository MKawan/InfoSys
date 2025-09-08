package br.com.mk.components.panels;

import br.com.mk.components.card.CpuCard;
import br.com.mk.components.card.MemoryCard;
import br.com.mk.components.card.NetworkCard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class PanelCard extends HBox {
	public PanelCard() {
		this.setSpacing(15);

		this.getStyleClass().add("panel-card");
		CpuCard cpuCard = new CpuCard();
		MemoryCard memoryCard = new MemoryCard();
		NetworkCard networkCard = new NetworkCard();

		HBox.setHgrow(cpuCard, Priority.ALWAYS);
		HBox.setHgrow(memoryCard, Priority.ALWAYS);
		HBox.setHgrow(networkCard, Priority.ALWAYS);

		this.getChildren().addAll(cpuCard, memoryCard, networkCard);
	}
}
