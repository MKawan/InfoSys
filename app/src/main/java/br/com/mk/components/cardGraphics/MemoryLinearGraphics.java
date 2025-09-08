package br.com.mk.components.cardGraphics;

import br.com.mk.components.graphics.LinearChart;
import br.com.mk.data.MemoryMonitor;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryLinearGraphics extends VBox {

	private LinearChart linearChart;

	public MemoryLinearGraphics() {
		this.setAlignment(Pos.CENTER_RIGHT);
		this.setSpacing(10);
		this.getStyleClass().add("card-dark-graphics");

		MemoryMonitor memoryMonitor = new MemoryMonitor();

		// Cores: RAM e Swap
		Color[] colors = { Color.web("#1e90ff"), Color.web("#ff4500") }; // Azul = RAM, Vermelho = Swap

		// Textos de legenda
		Text memoryText = new Text("Memory: 0%");
		memoryText.setFill(colors[0]);
		Text swapText = new Text("Swap: 0%");
		swapText.setFill(colors[1]);

		HBox legendBox = new HBox(15);
		legendBox.getChildren().addAll(memoryText, swapText);
		// LinearChart com 2 séries: RAM e Swap
		LinearChart linearChart = new LinearChart("Memory", true, 2, colors, 1);
		linearChart.getStyleClass().add("text-table-graphics-linear");
		VBox.setVgrow(linearChart.getChart(), Priority.ALWAYS);

		this.getChildren().addAll(linearChart.getChart(), legendBox);

		// Atualiza a cada segundo
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> {
			double ramUsage = memoryMonitor.getMemoryUsagePercent();
			long usedGB = memoryMonitor.getUsedMemoryGB();
			long totalGB = memoryMonitor.getTotalMemoryGB();

			// Swap (apenas se houver)
			double swapUsage = memoryMonitor.getSwapUsagePercent();
			long usedSwapGB = memoryMonitor.getUsedSwapGB();
			long totalSwapGB = memoryMonitor.getTotalSwapGB();

			Platform.runLater(() -> {
				linearChart.addValue(0, ramUsage);
				linearChart.addValue(1, swapUsage);

				memoryText.setText(String.format("Memory: %.1f%% (%dGB / %dGB)", ramUsage, usedGB, totalGB));
				swapText.setText(String.format("Swap: %.1f%% (%dGB / %dGB)", swapUsage, usedSwapGB, totalSwapGB));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

}
