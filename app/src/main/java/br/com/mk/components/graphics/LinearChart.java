package br.com.mk.components.graphics;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

import br.com.mk.config.ConfigManager;

public class LinearChart extends StackPane {

	private final LineChart<Number, Number> lineChart;
	private final XYChart.Series<Number, Number>[] seriesArray;
	private final Queue<Double>[] valuesQueues;
	private final int numSeries;
	private final int MAX_SECONDS = 60;
	private final int SMOOTH_WINDOW = 3;
	private final boolean percentageMode;
	private final Color[] colors;
	private final double lineWidth;
	private double[] dynamicMax;

	@SuppressWarnings("unchecked")
	public LinearChart(String title, boolean percentageMode, int numSeries, Color[] colors, double lineWidth) {

		this.percentageMode = percentageMode;
		this.numSeries = numSeries;
		this.colors = colors != null ? colors : new Color[] { Color.ORANGE, Color.YELLOW, Color.BLUE, Color.GREEN };
		this.lineWidth = lineWidth;
		this.seriesArray = new XYChart.Series[numSeries];
		this.valuesQueues = new Queue[numSeries];
		this.dynamicMax = new double[numSeries];

		double valueIntervalGraphics = ConfigManager.getDouble("value-graphics-inteval", 10);

		for (int i = 0; i < numSeries; i++) {
			valuesQueues[i] = new LinkedList<>();
			dynamicMax[i] = 1;
		}

		NumberAxis xAxis = new NumberAxis(0, valueIntervalGraphics * 6, valueIntervalGraphics);
		xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
			@Override
			public String toString(Number object) {
				int val = 60 - object.intValue(); // exibir de 60s → 0s
				return val == 0 ? "" : val + "s";
			}
		});

		NumberAxis yAxis = new NumberAxis();
		if (percentageMode) {
			yAxis.setAutoRanging(false);
			yAxis.setLowerBound(0);
			yAxis.setUpperBound(100);
			yAxis.setTickUnit(50);
			yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
				@Override
				public String toString(Number object) {
					return object.intValue() + "%";
				}
			});
		} else {
			yAxis.setAutoRanging(true);
			yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
				@Override
				public String toString(Number object) {
					return formatSpeed(object.doubleValue());
				}
			});
		}

		lineChart = new LineChart<>(xAxis, yAxis);
		lineChart.setAnimated(false);
		lineChart.setLegendVisible(false);
		lineChart.setTitle(title);
		lineChart.setCreateSymbols(false);
		lineChart.getStyleClass().add("linear-chart");

		for (int i = 0; i < numSeries; i++) {
			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			seriesArray[i] = series;
			lineChart.getData().add(series);

			final int index = i;
			Platform.runLater(() -> {
				Node lineNode = seriesArray[index].getNode();
				lineNode.getStyleClass().add("linear-node");

				if (lineNode != null) {
					String colorStr = toRgbString(this.colors[index % this.colors.length]);
					lineNode.setStyle("-fx-stroke: " + colorStr + "; -fx-stroke-width: " + lineWidth + "px;");
				}
			});
		}

		Platform.runLater(() -> {
			Node plot = lineChart.lookup(".chart-plot-background");
			if (plot != null) {
				plot.getStyleClass().add("chart-plot-background");
			}
		});

		this.getChildren().add(lineChart);

		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateChart()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	private String toRgbString(Color color) {
		return String.format("rgb(%d,%d,%d)", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	public void addValue(int seriesIndex, double value) {
		if (seriesIndex < 0 || seriesIndex >= numSeries)
			return;

		if (!percentageMode && value > dynamicMax[seriesIndex]) {
			dynamicMax[seriesIndex] = value;
			((NumberAxis) lineChart.getYAxis()).setUpperBound(dynamicMax[seriesIndex]);
		}

		valuesQueues[seriesIndex].add(value);
		if (valuesQueues[seriesIndex].size() > MAX_SECONDS) {
			valuesQueues[seriesIndex].poll();
		}
	}

	private void updateChart() {
		for (int s = 0; s < numSeries; s++) {
			seriesArray[s].getData().clear();
			Double[] arr = valuesQueues[s].toArray(new Double[0]);
			int size = arr.length;
			// 🔹 Atualiza o gráfico em tempo real

			updateXAxis(ConfigManager.getDouble("value-graphics-inteval", 10));

			for (int i = 0; i < size; i++) {
				double smoothed = smoothValue(arr, i, SMOOTH_WINDOW);
				seriesArray[s].getData().add(new XYChart.Data<>(MAX_SECONDS - size + i, smoothed));
			}
		}
	}

	private double smoothValue(Double[] array, int index, int window) {
		int start = Math.max(0, index - window + 1);
		int count = index - start + 1;
		double sum = 0;
		for (int i = start; i <= index; i++)
			sum += array[i];
		return sum / count;
	}

	public Node getChart() {
		return this;
	}

	public void updateXAxis(double valueIntervalGraphics) {
		NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(valueIntervalGraphics * 6);
		xAxis.setTickUnit(valueIntervalGraphics);

		xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
			@Override
			public String toString(Number object) {
				int val = (int) (valueIntervalGraphics * 6 - object.intValue());
				return val == 0 ? "" : val + "s";
			}
		});
	}

	// ----------------------------
	// Método para formatar unidades automáticas
	private String formatSpeed(double valueKBs) {
		double val = valueKBs;
		String unit = "KiB/s";

		if (val >= 1024) {
			val /= 1024;
			unit = "MiB/s";
		}
		if (val >= 1024) {
			val /= 1024;
			unit = "GiB/s";
		}

		return String.format("%.2f %s", val, unit);
	}
}
