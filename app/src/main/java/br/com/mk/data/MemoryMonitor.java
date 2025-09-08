package br.com.mk.data;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

public class MemoryMonitor {

	private final GlobalMemory memory;

	public MemoryMonitor() {
		SystemInfo si = new SystemInfo();
		this.memory = si.getHardware().getMemory();
	}

	/**
	 * Retorna a memória total em GB
	 */
	public long getTotalMemoryGB() {
		try {
			return memory.getTotal() / (1024 * 1024 * 1024);
		} catch (Exception e) {
			System.err.println("Error retrieving total memory: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Retorna a memória livre em GB
	 */
	public long getFreeMemoryGB() {
		try {
			return memory.getAvailable() / (1024 * 1024 * 1024);
		} catch (Exception e) {
			System.err.println("Error retrieving free memory: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Retorna a memória usada em GB
	 */
	public long getUsedMemoryGB() {
		try {
			return getTotalMemoryGB() - getFreeMemoryGB();
		} catch (Exception e) {
			System.err.println("Error calculating used memory: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Retorna o uso da memória em percentual (0–100)
	 */
	public double getMemoryUsagePercent() {
		try {
			double total = memory.getTotal();
			double available = memory.getAvailable();
			return ((total - available) / total) * 100.0;
		} catch (Exception e) {
			System.err.println("Error calculating memory usage percent: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Retorna a memória total em MB
	 */
	public long getTotalMemoryMB() {
		try {
			return memory.getTotal() / (1024 * 1024);
		} catch (Exception e) {
			System.err.println("Erro ao obter memória total: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Retorna a memória disponível em MB
	 */
	public long getAvailableMemoryMB() {
		try {
			return memory.getAvailable() / (1024 * 1024);
		} catch (Exception e) {
			System.err.println("Erro ao obter memória disponível: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Retorna a memória usada em MB
	 */
	public long getUsedMemoryMB() {
		try {
			return getTotalMemoryMB() - getAvailableMemoryMB();
		} catch (Exception e) {
			System.err.println("Erro ao calcular memória usada: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Retorna a swap total em GB
	 */
	public long getTotalSwapGB() {
		try {
			return memory.getVirtualMemory().getSwapTotal() / (1024 * 1024 * 1024);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Retorna a swap usada em GB
	 */
	public long getUsedSwapGB() {
		try {
			return memory.getVirtualMemory().getSwapUsed() / (1024 * 1024 * 1024);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Retorna a swap livre em GB
	 */
	public long getFreeSwapGB() {
		try {
			return getTotalSwapGB() - getUsedSwapGB();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Retorna a swap usada em percentual
	 */
	public double getSwapUsagePercent() {
		try {
			long total = getTotalSwapGB();
			long used = getUsedSwapGB();
			return total > 0 ? ((double) used / total) * 100.0 : 0;
		} catch (Exception e) {
			return 0;
		}
	}

}
