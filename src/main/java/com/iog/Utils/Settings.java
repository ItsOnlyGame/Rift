package com.iog.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Settings {
	private static Settings SETTINGS;
	public String botId;
	public String discordToken;
	public String spotifyClientId;
	public String spotifyClientSecret;
	public String spotifyCountryCode;
	public Map<String, String> defaultColors;
	public boolean refreshSlashCommands;
	public String guildIdSlashCommandsRefresh;
	
	public Settings() {
		this.defaultColors = new LinkedHashMap<>();
		this.defaultColors.put("success", String.format("#%02x%02x%02x", 77, 192, 58));
		this.defaultColors.put("error", String.format("#%02x%02x%02x", 188, 39, 39));
	}
	
	public static Settings getSettings() {
		if (Settings.SETTINGS != null) {
			return SETTINGS;
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		
		String file_GlobalSettings = "./config/settings.json";
		File file = new File(file_GlobalSettings);
		
		if (!file.exists()) {
			try (FileWriter writer = new FileWriter(file_GlobalSettings)) {
				gson.toJson(new Settings(), writer);
			} catch (IOException e) {
				Logger.error(e, e.getMessage());
			}
			String filler = "-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n";
			Logger.info(filler + "Fill in the info at the GlobalSettings file");
			
			System.exit(10);
		}
		
		try (FileReader reader = new FileReader(file_GlobalSettings)) {
			Settings.SETTINGS = gson.fromJson(reader, Settings.class);
			Settings.saveSettings(Settings.SETTINGS);
			return Settings.SETTINGS;
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
			System.exit(11);
		}
		
		return null;
	}
	
	public static void saveSettings(Settings settings) {
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		
		String file_GlobalSettings = "./GlobalSettings.json";
		
		try (FileWriter writer = new FileWriter(file_GlobalSettings)) {
			gson.toJson(settings, writer);
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
		}
	}
	
}
