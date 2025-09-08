package br.com.mk.data;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.*;

public class ProcessMonitor {

	private final OperatingSystem os;
	private final HardwareAbstractionLayer hal;
	private final CpuMonitor cpuMonitor;
	private final List<OSProcess> previousProcesses = new ArrayList<>();

	// Históricos de CPU por PID para média móvel
	private final Map<Integer, Deque<Double>> cpuHistory = new HashMap<>();
	private final int HISTORY_SIZE = 3; // últimos 3 ciclos

	public ProcessMonitor() {
		SystemInfo si = new SystemInfo();
		this.os = si.getOperatingSystem();
		this.hal = si.getHardware();
		this.cpuMonitor = new CpuMonitor();
	}

	/**
	 * Retorna os N processos em execução, com CPU suavizada
	 */
	public List<ProcessInfo> getRunningProcesses(int limit) {
		List<OSProcess> processes = os.getProcesses();

		// Ordena por CPU cumulativa decrescente
		processes.sort(Comparator.comparingDouble(OSProcess::getProcessCpuLoadCumulative).reversed());

		int count = Math.min(limit, processes.size());
		List<ProcessInfo> processesInfo = new ArrayList<>();
		int logicalCores = hal.getProcessor().getLogicalProcessorCount();

		for (int i = 0; i < count; i++) {
			OSProcess process = processes.get(i);

			// Busca processo anterior
			OSProcess prev = previousProcesses.stream().filter(p -> p.getProcessID() == process.getProcessID())
					.findFirst().orElse(null);

			// CPU real proporcional ao total de núcleos
			double cpuPercent = (prev != null) ? 100.0 * process.getProcessCpuLoadBetweenTicks(prev) / logicalCores
					: 0.0;

			// Atualiza histórico de média móvel
			Deque<Double> history = cpuHistory.computeIfAbsent(process.getProcessID(), k -> new ArrayDeque<>());
			history.addLast(cpuPercent);
			if (history.size() > HISTORY_SIZE)
				history.removeFirst();

			// Calcula média
			double cpuSmoothed = history.stream().mapToDouble(d -> d).average().orElse(0.0);

			processesInfo.add(buildProcessInfo(process, cpuSmoothed));
		}

		// Atualiza lista de processos anteriores
		previousProcesses.clear();
		previousProcesses.addAll(processes.subList(0, count));

		return processesInfo;
	}

	public ProcessInfo buildProcessInfo(OSProcess process, double cpuPercent) {
		long totalMemory = hal.getMemory().getTotal();
		double memoryPercent = (totalMemory > 0) ? 100d * process.getResidentSetSize() / totalMemory : 0.0;

		return new ProcessInfo(process.getProcessID(), process.getParentProcessID(), process.getName(),
				process.getCommandLine(), process.getUser(), process.getUserID(), process.getGroup(),
				process.getGroupID(), process.getState().name(), process.getPriority(), process.getThreadCount(),
				process.getBitness(), process.getUpTime(), process.getStartTime(), cpuPercent, process.getKernelTime(),
				process.getUserTime(), memoryPercent, process.getResidentSetSize(), process.getVirtualSize(),
				process.getBytesRead(), process.getBytesWritten(), process.getOpenFiles());
	}

	/**
	 * Calcula o nível hierárquico de um processo com base no PPID.
	 */
	public int getHierarchyLevel(ProcessInfo process, Map<Integer, ProcessInfo> processByPid) {
		int level = 0;
		int ppid = process.ppid();

		while (ppid > 0 && processByPid.containsKey(ppid)) {
			level++;
			ppid = processByPid.get(ppid).ppid();
		}

		return level;
	}

	public record ProcessInfo(int pid, int ppid, String name, String commandLine, String user, String userID,
			String group, String groupID, String state, int priority, int threadCount, int bitness, long upTimeMillis,
			long startTimeMillis, double cpuUsagePercent, long kernelTimeMillis, long userTimeMillis,
			double memoryUsagePercent, long residentMemoryBytes, long virtualMemoryBytes, long bytesRead,
			long bytesWritten, long openFiles) {
	}
}
