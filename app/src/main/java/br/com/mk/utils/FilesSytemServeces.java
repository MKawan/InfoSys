package br.com.mk.utils;

import java.io.File;
import java.util.List;

import br.com.mk.config.ConfigManager;
import br.com.mk.data.FilesSystemMonitor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import oshi.software.os.OSFileStore;

public class FilesSytemServeces {

	ConfigManager configManager = new ConfigManager();
	private String iconTheme;

	public HBox createInfoRow(String label, String value) {

		Text l = new Text(label + ": ");
		l.getStyleClass().add("info-label"); // classe CSS

		Text v = new Text(value);
		v.getStyleClass().add("info-value"); // classe CSS

		HBox row = new HBox(5, l, v);
		row.setAlignment(Pos.CENTER_LEFT);
		return row;
	}

	public boolean belongsToDisk(FilesSystemMonitor.DiskInfo disk, OSFileStore fs) {
		String diskName = disk.name().toLowerCase();
		return fs.getMount().toLowerCase().contains(diskName) || fs.getName().toLowerCase().contains(diskName);
	}

	public OSFileStore matchPartition(FilesSystemMonitor.PartitionInfo part, List<OSFileStore> stores) {
		for (OSFileStore fs : stores) {
			if (fs.getMount().equals(part.mountPoint())) {
				return fs;
			}
		}
		return null;
	}

	public String shortenMount(String mount) {
		if (mount == null || mount.isEmpty())
			return "desconhecido";
		String[] parts = mount.split("/");
		if (parts.length >= 2) {
			return parts[parts.length - 2] + "/" + parts[parts.length - 1];
		}
		return mount;
	}

	public boolean isExternal(FilesSystemMonitor.DiskInfo d) {
		String type = d.type().toLowerCase();
		String name = d.name().toLowerCase();
		return type.contains("usb") || type.contains("removable") || name.contains("sd");
	}

	public VBox buildDiskCard(FilesSystemMonitor.DiskInfo d, List<OSFileStore> fileStores) {
		VBox card = new VBox();
		card.setSpacing(10);
		card.setPadding(new Insets(12));
		card.setAlignment(Pos.TOP_CENTER);
		card.getStyleClass().add("disk-card"); // classe CSS principal

		// -------------------------
		// Adiciona o ícone do disco
		ImageView icon = new ImageView(getIconForDevice(d, ThemeManager.getCurrentTheme()));
		icon.getStyleClass().add("disk-icon"); // classe CSS
		card.getChildren().add(icon);
		// -------------------------

		// Adiciona listener para atualização em tempo real
		ThemeManager.addThemeChangeListener(newTheme -> {
			icon.setImage(getIconForDevice(d, newTheme));
		});

		// Subtítulo externo/desconhecido
		if (isExternal(d)) {
			Text extLabel = new Text("Dispositivo externo");
			extLabel.getStyleClass().add("subtitle");
			card.getChildren().add(extLabel);
		} else if (!containsRoot(d)) {
			Text unknownLabel = new Text("Desconhecido");
			unknownLabel.getStyleClass().add("subtitle");
			card.getChildren().add(unknownLabel);
		}

		// Barra de uso total do disco
		long totalSpace = 0, usedSpace = 0;
		for (OSFileStore fs : fileStores) {
			if (belongsToDisk(d, fs)) {
				totalSpace += fs.getTotalSpace();
				usedSpace += (fs.getTotalSpace() - fs.getUsableSpace());
			}
		}
		double totalPercent = totalSpace > 0 ? (usedSpace * 100.0 / totalSpace) : 0;

		ProgressBar totalBar = new ProgressBar(totalPercent / 100.0);
		totalBar.getStyleClass().add("progress-bar-total"); // classe CSS
		Text usageText = new Text(String.format("Uso total: %.1f%% (%.2f GB / %.2f GB)", totalPercent, usedSpace / 1e9,
				totalSpace / 1e9));
		usageText.getStyleClass().add("usage-text");

		VBox infoBox = new VBox(6);
		infoBox.setAlignment(Pos.TOP_LEFT);
		infoBox.getStyleClass().add("info-box"); // classe CSS
		infoBox.getChildren().addAll(totalBar, usageText, createInfoRow("Modelo", d.model()),
				createInfoRow("Serial", d.serial()),
				createInfoRow("Tamanho", String.format("%.2f GB", d.sizeBytes() / 1e9)));

		// Partições
		if (!d.partitions().isEmpty()) {
			Text partTitle = new Text("Partições:");
			partTitle.getStyleClass().add("subtitle");

			VBox partBox = new VBox(8);
			partBox.setAlignment(Pos.TOP_LEFT);
			partBox.getStyleClass().add("partition-box");

			for (FilesSystemMonitor.PartitionInfo p : d.partitions()) {
				OSFileStore store = matchPartition(p, fileStores);
				if (store != null) {
					long total = store.getTotalSpace();
					long used = total - store.getUsableSpace();
					double percent = total > 0 ? (used * 100.0 / total) : 0;

					String shortName = shortenMount(p.mountPoint());
					ProgressBar bar = new ProgressBar(percent / 100.0);
					bar.getStyleClass().add("partition-bar"); // classe CSS

					VBox partInfo = new VBox(2);
					partInfo.getStyleClass().add("partition-info"); // classe CSS
					partInfo.getChildren()
							.addAll(createInfoRow(shortName,
									String.format("%.1f%% (%.2f GB / %.2f GB)", percent, used / 1e9, total / 1e9)),
									bar);
					partBox.getChildren().add(partInfo);
				}
			}
			infoBox.getChildren().addAll(partTitle, partBox);
		}
		card.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				try {
					// Pega o mount point principal do disco
					String path = null;

					if (!d.partitions().isEmpty()) {
						path = d.partitions().get(0).mountPoint();
					} else if (containsRoot(d)) {
						path = d.name();
					}

					if (path == null || path.isEmpty())
						return;

					File diskRoot = new File(path);
					if (!diskRoot.exists())
						return;

					String os = System.getProperty("os.name").toLowerCase();

					if (os.contains("win")) {
						// Windows
						Runtime.getRuntime().exec(new String[] { "explorer.exe", diskRoot.getAbsolutePath() });
					} else if (os.contains("mac")) {
						// macOS
						Runtime.getRuntime().exec(new String[] { "open", diskRoot.getAbsolutePath() });
					} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
						// Linux/Unix
						Runtime.getRuntime().exec(new String[] { "xdg-open", diskRoot.getAbsolutePath() });
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		card.getChildren().add(infoBox);
		return card;
	}

	public boolean containsRoot(FilesSystemMonitor.DiskInfo d) {
		for (FilesSystemMonitor.PartitionInfo p : d.partitions()) {
			String mount = p.mountPoint();
			if (mount != null) {
				mount = mount.trim();
				if (mount.equals("/"))
					return true; // Linux
				if (mount.matches("^[A-Z]:\\\\$")) { // Windows
					String systemDrive = System.getenv("SystemDrive"); // normalmente "C:"
					if (systemDrive != null && mount.toUpperCase().startsWith(systemDrive.toUpperCase())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isInternal(FilesSystemMonitor.DiskInfo d) {
		return containsRoot(d) || (!d.type().toLowerCase().contains("usb") && !d.name().toLowerCase().contains("sd"));
	}

	public int externalPriority(FilesSystemMonitor.DiskInfo d) {
		String type = d.type().toLowerCase();
		String name = d.name().toLowerCase();
		long size = d.sizeBytes();

		if (!containsRoot(d) && size > 50L * 1_000_000_000)
			return 1;
		if (type.contains("usb") || type.contains("removable"))
			return 2;
		if (name.contains("sd") || name.contains("mmc"))
			return 3;
		return 4;
	}

	private Image loadIcon(String path) {
		var stream = getClass().getResourceAsStream(path);
		if (stream == null) {
			return new Image(getClass().getResourceAsStream("/icons/systemFiles/" + iconTheme + "/usb.png")); // fallback
		}
		return new Image(stream);
	}

	// Nova assinatura de getIconForDevice
	private Image getIconForDevice(FilesSystemMonitor.DiskInfo d, String theme) {
		String type = d.type().toLowerCase();
		String name = d.name().toLowerCase();

		if (containsRoot(d))
			return loadIcon("/icons/systemFiles/" + theme + "/storage.png");
		if (name.contains("sd") || name.contains("mmc"))
			return loadIcon("/icons/systemFiles/" + theme + "/usb.png");
		if (type.contains("usb") || type.contains("removable"))
			return loadIcon("/icons/systemFiles/" + theme + "/usb.png");
		if (type.contains("hdd") || type.contains("disk"))
			return loadIcon("/icons/systemFiles/" + theme + "/usb.png");

		return loadIcon("/icons/systemFiles/" + theme + "/usb.png");
	}
}
