package com.iog.Handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GuildSettings {
	/**
	 * GuildHandler
	 */
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	private static boolean initialized = false;
	private final long id;
	private String prefix = "r!";
	private int volume = 50;
	private boolean autoClean = true;
	
	public GuildSettings(long id) {
		this.id = id;
	}
	
	public static GuildSettings of(long id) {
		try (FileReader reader = new FileReader("./config/guilds/" + id + ".json")) {
			return gson.fromJson(reader, GuildSettings.class);
		} catch (IOException ignored) {
		}
		GuildSettings settings = new GuildSettings(id);
		settings.save();
		return settings;
	}
	
	public static void save(GuildSettings settings) {
		try (FileWriter writer = new FileWriter("./config/guilds/" + settings.getId() + ".json")) {
			gson.toJson(settings, writer);
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
		}
	}
	
	public long getId() {
		return id;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public GuildSettings setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public GuildSettings setVolume(int volume) {
		this.volume = volume;
		return this;
	}
	
	public boolean isAutoClean() {
		return autoClean;
	}
	
	public GuildSettings setAutoClean(boolean autoClean) {
		this.autoClean = autoClean;
		return this;
	}
	
	public void save() {
		GuildSettings.save(this);
	}
}
