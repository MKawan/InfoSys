package br.com.mk.components.card;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.com.mk.components.graphics.DonutChart;
import br.com.mk.data.MemoryMonitor;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class MemoryCard extends VBox {

	private final DonutChart swapDonutChart;
	private final Text swapPercentText;
	private final Text swapTotalText;
	private final Text swapUsedText;

	public MemoryCard() {
		this.setAlignment(Pos.CENTER);
		this.setSpacing(10);
		this.getStyleClass().add("card");

		MemoryMonitor memoryMonitor = new MemoryMonitor();
		boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");

		// Número de fatias: 4 para Linux (memória + swap), 3 para outros
		int numSlices = isLinux ? 4 : 3;

		// --- Donut de memória ---
		DonutChart memoryDonutChart = new DonutChart(0, 100, numSlices, null);

		// --- Textos de memória ---
		Text usedText = createText("Used: 0%", "#ff8c00", 14);
		Text freeText = createText("Free: 100%", "#ffd700", 14);

		Text totalMemText = createText("Total: 0 GB", "#ffffff", 12);
		Text usedMemText = createText("Used: 0 GB", "#ffffff", 12);
		Text freeMemText = createText("Free: 0 GB", "#ffffff", 12);

		Text typeText = new Text("Memory");
		typeText.getStyleClass().add("title");

		HBox memoryStatsRow1 = createHBox(10, usedText, freeText);
		HBox memoryStatsRow2 = createHBox(10, totalMemText, usedMemText, freeMemText);

		VBox memoryVBox = new VBox(5, memoryDonutChart.getChart(), memoryStatsRow1, memoryStatsRow2);
		memoryVBox.setAlignment(Pos.CENTER);

		// --- Donut de swap (Linux) ---
		DonutChart tmpSwapDonut = null;
		Text tmpSwapPercent = null;
		Text tmpSwapTotal = null;
		Text tmpSwapUsed = null;
		VBox swapVBox = null;

		if (isLinux) {
			tmpSwapDonut = new DonutChart(0, 100, numSlices, null);
			tmpSwapPercent = createText("Swap: 0%", "#ff4500", 14);
			tmpSwapTotal = createText("Swap Total: 0 GB", "#ffffff", 12);
			tmpSwapUsed = createText("Swap Used: 0 GB", "#ffffff", 12);

			HBox swapRow1 = createHBox(10, tmpSwapPercent);
			HBox swapRow2 = createHBox(10, tmpSwapTotal, tmpSwapUsed);
			swapVBox = new VBox(5, tmpSwapDonut.getChart(), swapRow1, swapRow2);
			swapVBox.setAlignment(Pos.CENTER);
		}

		swapDonutChart = tmpSwapDonut;
		swapPercentText = tmpSwapPercent;
		swapTotalText = tmpSwapTotal;
		swapUsedText = tmpSwapUsed;

		// --- Container principal ---
		HBox donutBox = new HBox(20);
		donutBox.setAlignment(Pos.CENTER);
		donutBox.getChildren().add(memoryVBox);
		if (isLinux)
			donutBox.getChildren().add(swapVBox);

		this.getChildren().addAll(typeText, donutBox);

		// --- Atualização periódica ---
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> {
			double usedPercent = memoryMonitor.getMemoryUsagePercent();
			double freePercent = 100 - usedPercent;

			long totalGB = memoryMonitor.getTotalMemoryGB();
			long usedGB = memoryMonitor.getUsedMemoryGB();
			long freeGB = memoryMonitor.getFreeMemoryGB();

			final double finalUsedPercent = usedPercent;
			final double finalFreePercent = freePercent;
			final long finalTotalGB = totalGB;
			final long finalUsedGB = usedGB;
			final long finalFreeGB = freeGB;

			final double swapPercentFinal = isLinux ? memoryMonitor.getSwapUsagePercent() : 0;
			final long swapTotalFinal = isLinux ? memoryMonitor.getTotalSwapGB() : 0;
			final long swapUsedFinal = isLinux ? memoryMonitor.getUsedSwapGB() : 0;

			Platform.runLater(() -> {
				memoryDonutChart.updateValues(finalUsedPercent, finalFreePercent);
				usedText.setText(String.format("Used: %.1f%%", finalUsedPercent));
				usedText.getStyleClass().add("text-dontus-infor2");
				freeText.setText(String.format("Free: %.1f%%", finalFreePercent));
				freeText.getStyleClass().add("text-dontus-infor");
				totalMemText.setText("Total: " + finalTotalGB + " GB");
				totalMemText.getStyleClass().add("text-dontus-infor3");
				usedMemText.setText("Used: " + finalUsedGB + " GB");
				usedMemText.getStyleClass().add("text-dontus-infor3");
				freeMemText.setText("Free: " + finalFreeGB + " GB");
				freeMemText.getStyleClass().add("text-dontus-infor3");

				if (isLinux) {
					swapDonutChart.updateValues(swapPercentFinal, 100 - swapPercentFinal);
					swapPercentText.setText(String.format("Swap: %.1f%%", swapPercentFinal));
					swapPercentText.getStyleClass().add("text-dontus-infor-swap");
					swapTotalText.setText("Swap Total: " + swapTotalFinal + " GB");
					swapTotalText.getStyleClass().add("text-dontus-infor-swap2");
					swapUsedText.setText("Swap Used: " + swapUsedFinal + " GB");
					swapUsedText.getStyleClass().add("text-dontus-infor-swap2");
				}
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	// --- Métodos utilitários ---
	private Text createText(String text, String color, int size) {
		Text t = new Text(text);
		t.setFill(Color.web(color));
		t.setFont(Font.font(size));
		return t;
	}

	private Text createText(String text, String color, int size, boolean bold) {
		Text t = new Text(text);
		t.setFill(Color.web(color));
		t.setFont(Font.font(size));
		if (bold)
			t.setStyle("-fx-font-weight: bold;");
		return t;
	}

	private HBox createHBox(double spacing, Text... children) {
		HBox box = new HBox(spacing);
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(children);
		return box;
	}

	private VBox createVBox(double spacing, Text... children) {
		VBox box = new VBox(spacing);
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(children);
		return box;
	}
}
