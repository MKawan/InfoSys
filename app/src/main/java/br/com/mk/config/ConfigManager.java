package br.com.mk.config;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
	private static final String FILE_NAME = "config.properties";
	private static Properties props = new Properties();

	static {
		// Carrega o arquivo de configuração existente ou cria um novo
		try (FileInputStream fis = new FileInputStream(FILE_NAME)) {
			props.load(fis);
		} catch (IOException e) {
			System.out.println("Config não encontrada, criando nova...");
		}
	}

	// ====================== GETTERS ======================
	public static boolean getBoolean(String key, boolean defaultValue) {
		return Boolean.parseBoolean(props.getProperty(key, Boolean.toString(defaultValue)));
	}

	public static double getDouble(String key, double defaultValue) {
		return Double.parseDouble(props.getProperty(key, Double.toString(defaultValue)));
	}

	public static String getString(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	// ====================== SETTERS ======================
	public static void setBoolean(String key, boolean value) {
		props.setProperty(key, Boolean.toString(value));
		saveProperties();
	}

	public static void setDouble(String key, double value) {
		props.setProperty(key, Double.toString(value));
		saveProperties();
	}

	public static void setString(String key, String value) {
		props.setProperty(key, value);
		saveProperties();
	}

	// ====================== SALVAR ======================
	private static void saveProperties() {
		try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
			props.store(fos, "User Preferences");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
