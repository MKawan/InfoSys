package br.com.mk.utils;

import br.com.mk.config.ConfigManager;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ThemeManager {

	private static String currentTheme = ConfigManager.getString("themes", "Default");

	// Scenes registradas
	private static final List<Scene> registeredScenes = new ArrayList<>();

	// Listeners de mudança de tema (qualquer componente pode registrar)
	private static final List<Consumer<String>> themeChangeListeners = new ArrayList<>();

	// Registra uma Scene para atualização automática
	public static void registerScene(Scene scene) {
		if (!registeredScenes.contains(scene)) {
			registeredScenes.add(scene);
			applyTheme(scene); // aplica tema atual imediatamente
		}
	}

	// Registra listener para mudanças de tema
	public static void addThemeChangeListener(Consumer<String> listener) {
		if (!themeChangeListeners.contains(listener)) {
			themeChangeListeners.add(listener);
			// chama uma vez imediatamente para alinhar estado
			listener.accept(currentTheme);
		}
	}

	// Remove listener se não precisar mais
	public static void removeThemeChangeListener(Consumer<String> listener) {
		themeChangeListeners.remove(listener);
	}

	// Define novo tema e atualiza todas as Scenes e Listeners
	public static void setTheme(String theme) {
		currentTheme = theme;
		ConfigManager.setString("themes", theme);

		// Atualiza todas as scenes
		for (Scene scene : registeredScenes) {
			applyTheme(scene);
		}

		// Notifica os listeners
		for (Consumer<String> listener : themeChangeListeners) {
			listener.accept(currentTheme);
		}
	}

	// Aplica tema em uma Scene específica
	public static void applyTheme(Scene scene) {
	    if (scene != null) {
	        scene.getStylesheets().clear();
	        scene.getStylesheets().add(
	            Objects.requireNonNull(
	                ThemeManager.class.getResource(
	                    "/css/" + ConfigManager.getString("themes", "Default") + ".css"
	                )
	            ).toExternalForm()
	        );
	    }
	}


	public static String getCurrentTheme() {
		return currentTheme;
	}
}
