package br.com.mk.components.cardGraphics;

import br.com.mk.components.graphics.LinearChart;
import br.com.mk.data.CpuMonitor;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CpuLinearGraphics extends VBox {

	private LinearChart linearChart;

	public CpuLinearGraphics() {
		this.setAlignment(Pos.CENTER_RIGHT);
		this.setSpacing(10);
		this.getStyleClass().add("card-dark-graphics");

		CpuMonitor cpuMonitor = new CpuMonitor();
		int cores = cpuMonitor.getLogicalCores();

		// Cria LinearChart com linhas por núcleo

		// Textos de legenda por núcleo
		HBox legendBox = new HBox(10);
		Color[] colors = { 
			    Color.web("#ff8c00"), Color.web("#ffd700"), Color.web("#1e90ff"), Color.web("#32cd32"),
			    Color.web("#ff69b4"), Color.web("#8a2be2"), Color.web("#00ced1"), Color.web("#ff4500"),
			    Color.web("#00b7eb"), Color.web("#ff4040"), Color.web("#9acd32"), Color.web("#ba55d3"),
			    Color.web("#20b2aa"), Color.web("#ff6347"), Color.web("#4682b4"), Color.web("#ffa500"),
			    Color.web("#6a5acd"), Color.web("#00ff7f"), Color.web("#dc143c"), Color.web("#7b68ee"),
			    Color.web("#00fa9a"), Color.web("#ff1493"), Color.web("#4169e1"), Color.web("#ff00ff"),
			    Color.web("#3cb371"), Color.web("#ff4500"), Color.web("#9932cc"), Color.web("#00ff00"),
			    Color.web("#b22222"), Color.web("#1c86ee"), Color.web("#adff2f"), Color.web("#c71585"),
			    Color.web("#00bfff"), Color.web("#ff0000"), Color.web("#6b8e23"), Color.web("#ff83fa"),
			    Color.web("#87ceeb"), Color.web("#ff7f50"), Color.web("#228b22"), Color.web("#da70d6"),
			    Color.web("#48d1cc"), Color.web("#cd5c5c"), Color.web("#7cfc00"), Color.web("#9400d3"),
			    Color.web("#87cefa"), Color.web("#ff6a6a"), Color.web("#90ee90"), Color.web("#c71585"),
			    Color.web("#00b7eb"), Color.web("#ff4040"), Color.web("#9acd32"), Color.web("#ba55d3"),
			    Color.web("#20b2aa"), Color.web("#ff6347"), Color.web("#4682b4"), Color.web("#ffa500"),
			    Color.web("#6a5acd"), Color.web("#00ff7f"), Color.web("#dc143c"), Color.web("#7b68ee"),
			    Color.web("#00fa9a"), Color.web("#ff1493"), Color.web("#4169e1"), Color.web("#ff00ff"),
			    Color.web("#3cb371"), Color.web("#ff4500"), Color.web("#9932cc"), Color.web("#00ff00"),
			    Color.web("#b22222"), Color.web("#1c86ee"), Color.web("#adff2f"), Color.web("#c71585"),
			    Color.web("#00bfff"), Color.web("#ff0000"), Color.web("#6b8e23"), Color.web("#ff83fa"),
			    Color.web("#87ceeb"), Color.web("#ff7f50"), Color.web("#228b22"), Color.web("#da70d6"),
			    Color.web("#48d1cc"), Color.web("#cd5c5c"), Color.web("#7cfc00"), Color.web("#9400d3"),
			    Color.web("#87cefa"), Color.web("#ff6a6a"), Color.web("#90ee90"), Color.web("#c71585"),
			    Color.web("#40e0d0"), Color.web("#ff3030"), Color.web("#32cd32"), Color.web("#d02090"),
			    Color.web("#00ced1"), Color.web("#ff4500"), Color.web("#6b8e23"), Color.web("#ff69b4"),
			    Color.web("#1e90ff"), Color.web("#ffa500"), Color.web("#9932cc"), Color.web("#00fa9a"),
			    Color.web("#dc143c"), Color.web("#4169e1"), Color.web("#00ff7f"), Color.web("#ff1493")
			};
		
		Text[] coreTexts = new Text[cores];
		for (int i = 0; i < cores; i++) {
			coreTexts[i] = new Text("Core " + (i + 1) + ": 0%");
			coreTexts[i].setFill(colors[i % colors.length]);
			legendBox.getChildren().add(coreTexts[i]);
		}

		linearChart = new LinearChart("CPU", true, cores, colors, 1);

//        VBox.setVgrow(linearChart.getChart(), Priority.ALWAYS);
//        VBox.setVgrow(legendBox, Priority.ALWAYS);
		linearChart.getStyleClass().add("text-table-graphics-linear");
		this.getChildren().addAll(linearChart.getChart(), legendBox);

		// Atualiza a cada segundo
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> {
			double[] loads = cpuMonitor.getCpuLoadPerCore();

			Platform.runLater(() -> {
				for (int i = 0; i < cores; i++) {
					double load = i < loads.length ? loads[i] : 0;
					linearChart.addValue(i, load);
					coreTexts[i].setText(String.format("Core %d: %.1f%%", i + 1, load));
				}
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

}
