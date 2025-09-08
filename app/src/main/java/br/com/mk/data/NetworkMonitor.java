package br.com.mk.data;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.util.List;

public class NetworkMonitor {

	private final List<NetworkIF> networkInterfaces;

	public NetworkMonitor() {
		SystemInfo si = new SystemInfo();
		this.networkInterfaces = si.getHardware().getNetworkIFs();
		updateNetworkStats();
	}

	private void updateNetworkStats() {
		for (NetworkIF net : networkInterfaces) {
			try {
				net.updateAttributes();
			} catch (Exception e) {
				System.err.println("Erro ao atualizar interface " + net.getName() + ": " + e.getMessage());
			}
		}
	}

	public List<String> getNetworkInterfaceNames() {
		return networkInterfaces.stream().map(NetworkIF::getName).toList();
	}

	/**
	 * Mede tráfego de rede (Download e Upload em KB/s) em um intervalo
	 */
	public NetworkSpeed getNetworkSpeed(String interfaceName, long intervalMillis) {
		try {
			for (NetworkIF net : networkInterfaces) {
				if (net.getName().equals(interfaceName)) {
					long rxBefore = net.getBytesRecv();
					long txBefore = net.getBytesSent();

					Thread.sleep(intervalMillis);

					net.updateAttributes();
					long rxAfter = net.getBytesRecv();
					long txAfter = net.getBytesSent();

					double downloadKBs = (rxAfter - rxBefore) / 1024.0 / (intervalMillis / 1000.0);
					double uploadKBs = (txAfter - txBefore) / 1024.0 / (intervalMillis / 1000.0);

					return new NetworkSpeed(downloadKBs, uploadKBs);
				}
			}
		} catch (Exception e) {
			System.err.println("Erro ao medir tráfego de rede: " + e.getMessage());
		}
		return new NetworkSpeed(-1, -1);
	}

	/**
	 * Classe auxiliar para guardar download/upload
	 */
	public record NetworkSpeed(double downloadKBs, double uploadKBs) {
	}
}
