package br.com.mk.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OpenFile {
	private final StringProperty fd; // File descriptor ou handle
	private final StringProperty path; // Caminho do arquivo
	private final StringProperty type; // Tipo (regular, socket, pipe)
	private final StringProperty mode; // Leitura/escrita
	private final StringProperty position; // Posição do ponteiro (se aplicável)

	public OpenFile(String fd, String path, String type, String mode, String position) {
		this.fd = new SimpleStringProperty(fd);
		this.path = new SimpleStringProperty(path);
		this.type = new SimpleStringProperty(type);
		this.mode = new SimpleStringProperty(mode);
		this.position = new SimpleStringProperty(position);
	}

	public StringProperty fdProperty() {
		return fd;
	}

	public StringProperty pathProperty() {
		return path;
	}

	public StringProperty typeProperty() {
		return type;
	}

	public StringProperty modeProperty() {
		return mode;
	}

	public StringProperty positionProperty() {
		return position;
	}

	public String getFd() {
		return fd.get();
	}

	public String getPath() {
		return path.get();
	}

	public String getType() {
		return type.get();
	}

	public String getMode() {
		return mode.get();
	}

	public String getPosition() {
		return position.get();
	}
}
