package br.com.mk.components.menu;

import java.awt.Desktop;
import java.net.URI;

public class Help {

	public static void openLink(String url) {
		new Thread(() -> {
			try {
				// 1. Tenta via Desktop
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					Desktop.getDesktop().browse(new URI(url));
					return;
				}

				// 2. Fallback para sistemas específicos
				String os = System.getProperty("os.name").toLowerCase();

				if (os.contains("win")) {
					new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
				} else if (os.contains("mac")) {
					new ProcessBuilder("open", url).start();
				} else if (os.contains("nix") || os.contains("nux")) {
					new ProcessBuilder("xdg-open", url).start();
				} else {
					System.err.println("Sistema operacional não suportado: " + os);
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Erro ao abrir link: " + e.getMessage());
			}
		}).start(); // sempre em thread separada
	}
}
