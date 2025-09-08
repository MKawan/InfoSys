package br.com.mk.components.card;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.com.mk.components.graphics.DonutChart;
import br.com.mk.data.CpuMonitor;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class CpuCard extends VBox {
	public CpuCard() {
		this.setAlignment(Pos.CENTER_RIGHT);
		this.setSpacing(10);
		this.getStyleClass().add("card");

		CpuMonitor cpuMonitor = new CpuMonitor();
		DonutChart donutChart = new DonutChart(0, 100, 3, null);

		// Criando textos
		Text userText = new Text("User: 0%");
		userText.setFill(Color.web("#ff8c00")); // mesma cor do donut (laranja)

		Text freeText = new Text("Free: 100%");
		freeText.setFill(Color.web("#ffd700")); // mesma cor do donut (amarelo)

		Text typeText = new Text("CPU");
		typeText.getStyleClass().add("title");

		HBox boxLayersBox = new HBox();
		boxLayersBox.setSpacing(10);
		boxLayersBox.getStyleClass().add("box-layers-text");
		boxLayersBox.getChildren().addAll(userText, freeText);

//        VBox.setVgrow(donutChart.getChart(),  Priority.ALWAYS);
//        VBox.setVgrow(boxLayersBox,  Priority.ALWAYS);
//        
//        // Empilha donut + textos
//        StackPane stack = new StackPane();
//        stack.getChildren().addAll();

		this.getChildren().addAll(typeText, donutChart.getChart(), boxLayersBox);

		// Atualiza CPU em tempo real
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> {
			double used = cpuMonitor.getCpuLoad();
			double free = 100 - used;

			Platform.runLater(() -> {
				donutChart.updateValues(used, free);
				userText.setText(String.format("User: %.1f%%", used));
				userText.getStyleClass().add("text-dontus-infor2");
				freeText.setText(String.format("Free: %.1f%%", free));
				freeText.getStyleClass().add("text-dontus-infor");
			});
		}, 0, 1, TimeUnit.SECONDS);
	}
}
