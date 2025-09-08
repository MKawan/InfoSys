package br.com.mk.components.card;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.com.mk.components.graphics.DonutChart;
import br.com.mk.data.NetworkMonitor;
import br.com.mk.data.NetworkMonitor.NetworkSpeed;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class NetworkCard extends VBox {

	private static final double MAX_SCALE_KIB = 10240; // 10 MiB/s (em KiB/s)

	public NetworkCard() {

		this.setAlignment(Pos.CENTER);
		this.setSpacing(15);
		this.getStyleClass().add("card");

		NetworkMonitor networkMonitor = new NetworkMonitor();

		// Donuts separados
		DonutChart donutDownload = new DonutChart(0, 100, 4, null);
		DonutChart donutUpload = new DonutChart(0, 100, 4, null);

		// Textos
		Text downloadText = new Text("Download: 0 KiB/s");
		downloadText.getStyleClass().add("text-dontus-infor");

		Text uploadText = new Text("Upload: 0 KiB/s");
		uploadText.getStyleClass().add("text-dontus-infor2");

		Text titleText = new Text("Network");
		titleText.getStyleClass().add("title");

		// Layout
		HBox donutBox = new HBox(10, donutDownload.getChart(), donutUpload.getChart());
		donutBox.setAlignment(Pos.CENTER);

		HBox boxLayersText = new HBox();
		boxLayersText.setSpacing(10);
		boxLayersText.getStyleClass().add("box-layers-text");
		boxLayersText.getChildren().addAll(downloadText, uploadText);

//        VBox.setVgrow(boxLayersText,  Priority.ALWAYS);
//        VBox.setVgrow(donutBox, Priority.ALWAYS);

		this.getChildren().addAll(titleText, donutBox, boxLayersText);

		// Pega a primeira interface de rede
		String iface = networkMonitor.getNetworkInterfaceNames().get(0);

		// Atualiza em tempo real
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> {
			NetworkSpeed speed = networkMonitor.getNetworkSpeed(iface, 1000); // 1s

			Platform.runLater(() -> {
				String downStr = formatSpeed(speed.downloadKBs());
				String upStr = formatSpeed(speed.uploadKBs());

				downloadText.setText("Download: " + downStr);
				uploadText.setText("Upload: " + upStr);

				// Normaliza valores para a escala
				double downUsed = Math.min(speed.downloadKBs(), MAX_SCALE_KIB);
				double downFree = MAX_SCALE_KIB - downUsed;

				double upUsed = Math.min(speed.uploadKBs(), MAX_SCALE_KIB);
				double upFree = MAX_SCALE_KIB - upUsed;

				donutDownload.updateValues(downUsed, downFree);
				donutUpload.updateValues(upUsed, upFree);
			});
		}, 0, 2, TimeUnit.SECONDS);
	}

	private String formatSpeed(double kbps) {
		if (kbps < 0)
			return "N/A";
		if (kbps < 1024)
			return String.format("%.1f KiB/s", kbps);
		return String.format("%.1f MiB/s", kbps / 1024.0);
	}
}
