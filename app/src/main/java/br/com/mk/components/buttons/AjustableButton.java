package br.com.mk.components.buttons;

import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.text.DecimalFormat;

import br.com.mk.components.window.Recourses;
import br.com.mk.config.ConfigManager;

public class AjustableButton extends HBox {
	private double passValue; // tamanho do passo (fração ou inteiro)
	private double middayValue;
	private double finalValue;
	private double valueSalve; // chave usada no .properties

	private Slider adjustableButton;
	private TextField field;

	private final DecimalFormat formatter; // controla formatação de saída

	private String configKey; // chave usada no .properties

	public AjustableButton(String configKey, double valueDefault, double passValue, double middayValue,
			double finalValue, Recourses recourses) {
		this.passValue = passValue;
		this.middayValue = middayValue;
		this.finalValue = finalValue;
		this.configKey = configKey;

		// 🔹 valor inicial: busca no config ou usa middayValue
		this.valueSalve = ConfigManager.getDouble(configKey, valueDefault);

		// formata de acordo com o tamanho do passo (2 casas se for decimal)
		if (passValue % 1 == 0) {
			formatter = new DecimalFormat("0"); // só inteiro
		} else {
			formatter = new DecimalFormat("0.##"); // até 2 casas decimais
		}

		// --- Botão regulável (Slider + TextField) ---
		adjustableButton = new Slider(0, finalValue, middayValue);
		adjustableButton.setShowTickMarks(false);
		adjustableButton.setShowTickLabels(false);
		adjustableButton.setBlockIncrement(passValue);
		adjustableButton.setSnapToTicks(true);
		adjustableButton.setPrefWidth(200);
		adjustableButton.getStyleClass().add("card-shortcut");

		field = new TextField(formatter.format(adjustableButton.getValue()));
		field.setPrefWidth(60);
		field.getStyleClass().add("text-shortcut");

		// Atualiza o campo e salva no config quando o slider muda
		adjustableButton.valueProperty().addListener((obs, oldVal, newVal) -> {
			double snapped = snapValue(newVal.doubleValue());
			adjustableButton.setValue(snapped);
			field.setText(formatter.format(snapped));

			valueSalve = snapped;
			ConfigManager.setDouble(configKey, snapped);
		});

		// Atualiza o slider e salva no config quando o campo muda
		field.textProperty().addListener((obs, oldVal, newVal) -> {
			try {
				double val = Double.parseDouble(newVal);
				if (val < 0)
					val = 0;
				if (val > finalValue)
					val = finalValue;

				double snapped = snapValue(val);
				adjustableButton.setValue(snapped);

				// 🔹 salva no config e atualiza valueSalve
				valueSalve = snapped;
				ConfigManager.setDouble(configKey, snapped);
			} catch (NumberFormatException ignored) {
			}
		});

		this.setStyle("-fx-alignment: center; -fx-padding: 10;");
		this.setSpacing(10);

		this.getChildren().addAll(adjustableButton, field);
	}

	// arredonda para múltiplo do passo definido
	private double snapValue(double value) {
		return Math.round(value / passValue) * passValue;
	}

	// pega o valor atual (sempre sincronizado com valueSalve)
	public double getValue() {
		return valueSalve;
	}

	// define valor manualmente (e salva no config)
	public void setValue(double value) {
		double snapped = snapValue(value);
		adjustableButton.setValue(snapped);
		field.setText(formatter.format(snapped));

		// 🔹 salva no config e atualiza valueSalve
		valueSalve = snapped;
		ConfigManager.setDouble(configKey, snapped);
	}
}
