package br.com.mk.components.window;

import br.com.mk.data.FilesSystemMonitor;
import br.com.mk.utils.FilesSytemServeces;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;

import oshi.SystemInfo;
import oshi.software.os.OSFileStore;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FilesSystem extends FlowPane {

	private final FilesSystemMonitor fsMonitor;
	private final SystemInfo si;

	public FilesSystem() {
		this.setHgap(15);
		this.setVgap(15);
		this.setPadding(new Insets(10));
		this.setAlignment(Pos.TOP_LEFT);

		fsMonitor = new FilesSystemMonitor();
		si = new SystemInfo();

		updateCards();

		// Atualiza em tempo real a cada 5 segundos
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> Platform.runLater(this::updateCards), 0, 5, TimeUnit.SECONDS);
	}

	private void updateCards() {

		FilesSytemServeces filesSytemServeces = new FilesSytemServeces();
		this.getChildren().clear();

		List<FilesSystemMonitor.DiskInfo> disks = fsMonitor.getDisks();
		List<OSFileStore> fileStores = si.getOperatingSystem().getFileSystem().getFileStores();

		List<FilesSystemMonitor.DiskInfo> internalDisks = new ArrayList<>();
		List<FilesSystemMonitor.DiskInfo> externalDisks = new ArrayList<>();

		for (FilesSystemMonitor.DiskInfo d : disks) {
			if (filesSytemServeces.isInternal(d))
				internalDisks.add(d);
			else
				externalDisks.add(d);
		}

		// Internos: raiz primeiro
		internalDisks.sort((a, b) -> filesSytemServeces.containsRoot(a) ? -1
				: filesSytemServeces.containsRoot(b) ? 1 : a.name().compareToIgnoreCase(b.name()));

		// Externos: HD externo → pendrive → SD
		externalDisks.sort(Comparator.comparingInt(filesSytemServeces::externalPriority));

		// Adicionar cards
		for (FilesSystemMonitor.DiskInfo d : internalDisks)
			this.getChildren().add(filesSytemServeces.buildDiskCard(d, fileStores));
		for (FilesSystemMonitor.DiskInfo d : externalDisks)
			this.getChildren().add(filesSytemServeces.buildDiskCard(d, fileStores));
	}

}
