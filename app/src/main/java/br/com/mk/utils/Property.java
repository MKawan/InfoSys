package br.com.mk.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Property {
	private final StringProperty key;
	private final StringProperty value;

	public Property(String key, String value) {
		this.key = new SimpleStringProperty(key);
		this.value = new SimpleStringProperty(value);
	}

	public StringProperty keyProperty() {
		return key;
	}

	public StringProperty valueProperty() {
		return value;
	}

	public String getKey() {
		return key.get();
	}

	public String getValue() {
		return value.get();
	}

	public void setKey(String key) {
		this.key.set(key);
	}

	public void setValue(String value) {
		this.value.set(value);
	}
}
