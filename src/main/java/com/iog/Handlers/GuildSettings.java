package com.iog.Handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tinylog.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GuildSettings {
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	private final long id;
	private int volume = 50;

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
	
	public int getVolume() {
		return volume;
	}
	
	public GuildSettings setVolume(int volume) {
		this.volume = volume;
		return this;
	}
	
	public void save() {
		GuildSettings.save(this);
	}
}
