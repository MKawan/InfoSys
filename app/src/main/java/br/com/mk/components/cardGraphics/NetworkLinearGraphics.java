package br.com.mk.components.cardGraphics;

import br.com.mk.components.graphics.LinearChart;
import br.com.mk.data.NetworkMonitor;
import br.com.mk.data.NetworkMonitor.NetworkSpeed;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NetworkLinearGraphics extends VBox {

	private LinearChart linearChart;

	public NetworkLinearGraphics() {
		this.setAlignment(Pos.CENTER);
		this.setSpacing(10);
		this.getStyleClass().add("card-dark-graphics");

		NetworkMonitor networkMonitor = new NetworkMonitor();
		String interfaceName = networkMonitor.getNetworkInterfaceNames().stream().findFirst().orElse("");

		Color[] colors = { Color.web("#1e90ff"), Color.web("#ff4500") }; // Azul = download, Vermelho = upload
		Text downloadText = new Text("Download: 0 KB/s");
		downloadText.setFill(colors[0]);

		Text uploadText = new Text("Upload: 0 KB/s");
		uploadText.setFill(colors[1]);

		// LinearChart com duas séries, sem normalização
		LinearChart linearChart = new LinearChart("Network Traffic (KB/s)", false, 2, colors, 1);

		HBox legendBox = new HBox(15);
		legendBox.getChildren().addAll(downloadText, uploadText);

		VBox.setVgrow(linearChart.getChart(), Priority.ALWAYS);

		linearChart.getStyleClass().add("text-table-graphics-linear");

		this.getChildren().addAll(linearChart.getChart(), legendBox);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> {
			if (!interfaceName.isEmpty()) {
				NetworkSpeed speed = networkMonitor.getNetworkSpeed(interfaceName, 1000);

				double downloadKBs = speed.downloadKBs();
				double uploadKBs = speed.uploadKBs();

				Platform.runLater(() -> {
					linearChart.addValue(0, downloadKBs);
					linearChart.addValue(1, uploadKBs);

					downloadText.setText(String.format("Download: %.1f KB/s", downloadKBs));
					uploadText.setText(String.format("Upload: %.1f KB/s", uploadKBs));
				});
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

}
