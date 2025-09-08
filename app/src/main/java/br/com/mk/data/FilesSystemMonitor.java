package br.com.mk.data;

import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class FilesSystemMonitor {

	private final HardwareAbstractionLayer hal;
	private final FileSystem fileSystem;

	public FilesSystemMonitor() {
		SystemInfo si = new SystemInfo();
		this.hal = si.getHardware();
		this.fileSystem = si.getOperatingSystem().getFileSystem();
	}

	/**
	 * Retorna informações sobre os discos e partições
	 */
	public List<DiskInfo> getDisks() {
		List<DiskInfo> disksInfo = new ArrayList<>();
		for (HWDiskStore disk : hal.getDiskStores()) {
			disksInfo.add(buildDiskInfo(disk));
		}
		return disksInfo;
	}

	/**
	 * Constrói o objeto DiskInfo a partir de um HWDiskStore
	 */
	private DiskInfo buildDiskInfo(HWDiskStore disk) {
		List<PartitionInfo> partitionsInfo = new ArrayList<>();
		for (HWPartition p : disk.getPartitions()) {
			partitionsInfo.add(
					new PartitionInfo(p.getIdentification(), p.getName(), p.getType(), p.getMountPoint(), p.getSize()));
		}

		String typeGuess = guessDiskType(disk.getModel());

		return new DiskInfo(disk.getName(), disk.getModel(), disk.getSerial(), disk.getSize(), typeGuess,
				partitionsInfo);
	}

	/**
	 * Tenta adivinhar o tipo de disco pelo modelo/nome
	 */
	private String guessDiskType(String model) {
		String m = model.toLowerCase();
		if (m.contains("nvme"))
			return "NVMe SSD";
		if (m.contains("ssd"))
			return "SSD";
		if (m.contains("mmc") || m.contains("sd"))
			return "SD Card";
		if (m.contains("usb"))
			return "USB Drive";
		return "HDD/Desconhecido";
	}

	/**
	 * Estrutura de informações do disco
	 */
	public record DiskInfo(String name, String model, String serial, long sizeBytes, String type,
			List<PartitionInfo> partitions) {
	}

	/**
	 * Estrutura de informações de partições
	 */
	public record PartitionInfo(String id, String name, String type, String mountPoint, long sizeBytes) {
	}

	// Exemplo de uso no console
	public static void main(String[] args) {
		FilesSystemMonitor fsm = new FilesSystemMonitor();
		List<DiskInfo> disks = fsm.getDisks();

		System.out.printf("%-10s %-25s %-15s %-12s %-10s%n", "Nome", "Modelo", "Serial", "Tipo", "Tamanho(GB)");

		for (DiskInfo d : disks) {
			System.out.printf("%-10s %-25s %-15s %-12s %-10.2f%n", d.name(), d.model(), d.serial(), d.type(),
					d.sizeBytes() / 1e9);

			for (PartitionInfo p : d.partitions()) {
				System.out.printf("   -> %-10s %-10s %-10s %-15s %-10.2f%n", p.id(), p.name(), p.type(), p.mountPoint(),
						p.sizeBytes() / 1e9);
			}
		}
	}
}
