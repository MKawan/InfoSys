package br.com.mk.utils;

import br.com.mk.data.ProcessMonitor.ProcessInfo;
import javafx.scene.control.Label;

import java.text.DecimalFormat;

public class ProcessStatusUtils {

	private static final DecimalFormat df = new DecimalFormat("0.00");

	/**
	 * Formata bytes em KB, MB ou GB
	 */
	public static String formatBytes(long bytes) {
		double kb = bytes / 1024.0;
		if (kb < 1024)
			return df.format(kb) + " KB";
		double mb = kb / 1024.0;
		if (mb < 1024)
			return df.format(mb) + " MB";
		return df.format(mb / 1024.0) + " GB";
	}

	/**
	 * Cria um Label estilizado para exibir informação de processo
	 */
	public static Label createProcessLabel(String text) {
		Label label = new Label(text);
		label.getStyleClass().add("text-window-process-data");
		return label;
	}

	/**
	 * Atualiza informações da tabela de um ProcessInfo em Labels
	 */
	public static void updateInfoTable(ProcessInfo p, javafx.scene.layout.VBox infoTable) {
		infoTable.getChildren().clear();
		infoTable.getChildren().addAll(createProcessLabel("PID: " + p.pid()), createProcessLabel("Name: " + p.name()),
				createProcessLabel("User: " + p.user()), createProcessLabel("Threads: " + p.threadCount()),
				createProcessLabel("CPU Usage: " + df.format(p.cpuUsagePercent()) + "%"),
				createProcessLabel("Memory Usage: " + df.format(p.memoryUsagePercent()) + "%"),
				createProcessLabel("RAM Used by Process: " + formatBytes(p.residentMemoryBytes())),
				createProcessLabel("Disk Read: " + formatBytes(p.bytesRead())),
				createProcessLabel("Disk Write: " + formatBytes(p.bytesWritten())));
	}
}
