package br.com.mk.components.graphics;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
//import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * DonutChart class generates a responsive donut chart given used and free
 * values.
 */
public class DonutChart {

	private final PieChart pieChart;
	private final Circle innerCircle;
	private final StackPane chartPane;

	/**
	 * Constructor receives used and free values (e.g., CPU usage)
	 */
	public DonutChart(double used, double free, double interValuer, Text innerText) {
		// Create PieChart data
		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(new PieChart.Data("", free),
				new PieChart.Data("", used));
//        Text innerText = new Text(text);

		pieChart = new PieChart(pieData);
		pieChart.setLabelsVisible(false);
		pieChart.setLegendVisible(false);

		// Make PieChart take full size of StackPane
		pieChart.setMinSize(0, 0); // allows shrinking
		pieChart.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		pieChart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		// Inner circle for donut effect
		innerCircle = new Circle();
		innerCircle.getStyleClass().add("inner-circle-donut");

		// StackPane to overlay PieChart + inner circle
		chartPane = new StackPane();
		chartPane.getChildren().addAll(pieChart, innerCircle); // ordem importa

		// adiciona o texto apenas se não for nulo
		if (innerText != null) {
			innerText.getStyleClass().add("text-window-process-data");
			chartPane.getChildren().add(innerText);
			StackPane.setAlignment(innerText, Pos.CENTER);
		}

		// Bind the circle radius to 1/3 of the smaller dimension (width or height)
		innerCircle.radiusProperty()
				.bind(Bindings.createDoubleBinding(
						() -> Math.min(chartPane.getWidth(), chartPane.getHeight()) / interValuer,
						chartPane.widthProperty(), chartPane.heightProperty()));

		// Make chartPane grow with parent container
		chartPane.setMinSize(0, 0);
		chartPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		chartPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	/**
	 * Updates the donut values dynamically
	 */
	public void updateValues(double used, double free) {
		ObservableList<PieChart.Data> data = pieChart.getData();
		data.get(0).setPieValue(free);
		data.get(1).setPieValue(used);
	}

	/**
	 * Returns the Node to be added to a Scene
	 */
	public Node getChart() {
		return chartPane;
	}
}
