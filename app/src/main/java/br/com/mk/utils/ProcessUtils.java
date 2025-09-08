package br.com.mk.utils;

import br.com.mk.data.ProcessMonitor.ProcessInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProcessUtils {

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
	 * Cria as colunas de ProcessInfo
	 */
	public static Map<String, TableColumn<ProcessInfo, String>> createColumns() {
		Map<String, TableColumn<ProcessInfo, String>> allColumns = new LinkedHashMap<>();

		addColumn(allColumns, "PID", p -> String.valueOf(p.pid()));
		addColumn(allColumns, "PPID", p -> String.valueOf(p.ppid()));
		addColumn(allColumns, "Name", ProcessInfo::name);
		addColumn(allColumns, "Command Line", ProcessInfo::commandLine);
		addColumn(allColumns, "User", ProcessInfo::user);
		addColumn(allColumns, "UserID", ProcessInfo::userID);
		addColumn(allColumns, "Group", ProcessInfo::group);
		addColumn(allColumns, "GroupID", ProcessInfo::groupID);
		addColumn(allColumns, "State", ProcessInfo::state);
		addColumn(allColumns, "Priority", p -> String.valueOf(p.priority()));
		addColumn(allColumns, "Thread Count", p -> String.valueOf(p.threadCount()));
		addColumn(allColumns, "Bitness", p -> String.valueOf(p.bitness()));
		addColumn(allColumns, "Up Time", p -> String.valueOf(p.upTimeMillis()));
		addColumn(allColumns, "Start Time", p -> String.valueOf(p.startTimeMillis()));
		addColumn(allColumns, "CPU (%)", p -> df.format(p.cpuUsagePercent()));
		addColumn(allColumns, "Kernel Time", p -> String.valueOf(p.kernelTimeMillis()));
		addColumn(allColumns, "User Time", p -> String.valueOf(p.userTimeMillis()));
		addColumn(allColumns, "Memory (%)", p -> df.format(p.memoryUsagePercent()));
		addColumn(allColumns, "Resident Memory", p -> formatBytes(p.residentMemoryBytes()));
		addColumn(allColumns, "Virtual Memory", p -> formatBytes(p.virtualMemoryBytes()));
		addColumn(allColumns, "Disk Read", p -> formatBytes(p.bytesRead()));
		addColumn(allColumns, "Disk Write", p -> formatBytes(p.bytesWritten()));
		addColumn(allColumns, "Open Files", p -> String.valueOf(p.openFiles()));

		return allColumns;
	}

	private static void addColumn(Map<String, TableColumn<ProcessInfo, String>> map, String title,
			Function<ProcessInfo, String> mapper) {
		TableColumn<ProcessInfo, String> col = new TableColumn<>(title);
		col.setCellValueFactory(cell -> new SimpleStringProperty(mapper.apply(cell.getValue())));
		map.put(title, col);
	}

	/**
	 * Aplica filtro de busca sobre a lista original
	 */
	public static List<ProcessInfo> filterProcesses(List<ProcessInfo> originalList, String filter) {
		if (filter == null || filter.isEmpty()) {
			return new ArrayList<>(originalList);
		}
		return originalList.stream().filter(p -> p.name().toLowerCase().contains(filter.toLowerCase()))
				.sorted(Comparator.comparing(ProcessInfo::name)).collect(Collectors.toList());
	}

	/**
	 * Cria uma ObservableList a partir de uma Collection
	 */
	public static <T> ObservableList<T> toObservableList(Collection<T> list) {
		return FXCollections.observableArrayList(list);
	}

	/** Retorna a chave usada no config.properties para uma coluna */
	public static String getConfigKey(String columnName) {
		// Remove espaços e parênteses e substitui "%" por "Percent"
		String key = columnName.replace("%", "Percent").replaceAll("[\\s()]", "");
		return "process." + key;
	}

}
