package br.com.mk.data;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OperatingSystem;

import java.util.Arrays;

public class CpuMonitor {

	// OSHI CPU object
	private final CentralProcessor cpu;
	// OSHI Operating System object (for uptime and system info)
	private final OperatingSystem os;

	// Previous CPU ticks for calculating CPU load
	private long[] prevTicks;
	// Previous ticks per core for calculating per-core CPU load
	private long[][] prevPerCoreTicks;

	// Constructor initializes OSHI objects and snapshots initial ticks
	public CpuMonitor() {
		SystemInfo si = new SystemInfo();
		this.cpu = si.getHardware().getProcessor();
		this.os = si.getOperatingSystem();
		this.prevTicks = cpu.getSystemCpuLoadTicks(); // initial snapshot of system ticks
		this.prevPerCoreTicks = cpu.getProcessorCpuLoadTicks(); // initial snapshot per core
	}

	/**
	 * Returns the CPU information (name, manufacturer, etc.)
	 */
	public String getProcessorInfo() {
		try {
			return cpu.getProcessorIdentifier().toString();
		} catch (Exception e) {
			return "Error retrieving processor info: " + e.getMessage();
		}
	}

	/**
	 * Returns the number of logical CPU cores
	 */
	public int getLogicalCores() {
		try {
			return cpu.getLogicalProcessorCount();
		} catch (Exception e) {
			System.err.println("Error retrieving logical cores: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Returns the number of physical CPU cores
	 */
	public int getPhysicalCores() {
		try {
			return cpu.getPhysicalProcessorCount();
		} catch (Exception e) {
			System.err.println("Error retrieving physical cores: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Returns the system uptime in seconds
	 */
	public long getSystemUptime() {
		try {
			return os.getSystemUptime(); // correct way to get uptime
		} catch (Exception e) {
			System.err.println("Error retrieving system uptime: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Returns the total CPU load (%) since the last snapshot
	 */
	public double getCpuLoad() {
		try {
			double load = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
			prevTicks = cpu.getSystemCpuLoadTicks(); // update snapshot
			return load;
		} catch (Exception e) {
			System.err.println("Error calculating CPU load: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Returns the CPU load per core (%) since the last snapshot
	 */
	public double[] getCpuLoadPerCore() {
		try {
			double[] loadPerCore = cpu.getProcessorCpuLoadBetweenTicks(prevPerCoreTicks);
			prevPerCoreTicks = cpu.getProcessorCpuLoadTicks(); // update snapshot
			return Arrays.stream(loadPerCore).map(l -> l * 100).toArray(); // convert to %
		} catch (Exception e) {
			System.err.println("Error calculating per-core CPU load: " + e.getMessage());
			return new double[0];
		}
	}

	/**
	 * Returns the current CPU ticks (user, system, idle, etc.)
	 */
	public long[] getCpuTicks() {
		try {
			return cpu.getSystemCpuLoadTicks();
		} catch (Exception e) {
			System.err.println("Error retrieving CPU ticks: " + e.getMessage());
			return new long[cpu.getProcessorCpuLoadTicks().length];
		}
	}
}
