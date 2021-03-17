package com.iog.Handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iog.Main;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Role;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GuildSettings {

    private String name = "";
    private long id;
    private String prefix = "r!";

    private long dj_role = 0L;
    private int volume = 50;

    private boolean deleteMessageAfterCommand = false;

    public GuildSettings(long id) {
        this.id = id;
        Main.gateway.getGuildById(Snowflake.of(id)).subscribe(guild -> {
            this.name = guild.getName();
            Role role = guild.getRoles().filter(p -> p.getName().equals("dj")).blockFirst();
            if (role != null) this.dj_role = role.getId().asLong();
        });
    }

    public String getName() {
        return name;
    }

    public GuildSettings setName(String name) {
        this.name = name;
        return this;
    }

    public long getId() {
        return id;
    }

    public GuildSettings setId(long id) {
        this.id = id;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public GuildSettings setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public long getDj() {
        return dj_role;
    }

    public GuildSettings setDj_role(long dj_role) {
        this.dj_role = dj_role;
        return this;
    }

    public int getVolume() {
        return volume;
    }

    public GuildSettings setVolume(int volume) {
        this.volume = volume;
        return this;
    }

    public boolean isDeleteMessageAfterCommand() {
        return deleteMessageAfterCommand;
    }

    public GuildSettings setDeleteMessageAfterCommand(boolean deleteMessageAfterCommand) {
        this.deleteMessageAfterCommand = deleteMessageAfterCommand;
        return this;
    }

    public void save() {
        GuildSettings.save(this);
    }


    /**
     * GuildHandler
     */
    private static final transient Logger logger = Logger.getLogger(GuildHandler.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        File folder = new File("./guilds");
        if (folder.exists()) {
            if (folder.isDirectory()) {
                return;
            }
        }

        try {
            if (!folder.mkdir()) {
                logger.error("Couldn't create a folder for the guild settings. Check file write permissions!");
                System.exit(12);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static GuildSettings of(long id) {
        init();

        try (FileReader reader = new FileReader("./guilds/"+id+".json")) {
            return gson.fromJson(reader, GuildSettings.class);
        } catch (IOException ignored) { }
        GuildSettings settings = new GuildSettings(id);
        settings.save();
        return settings;
    }


    public static void save(GuildSettings settings) {
        init();

        try (FileWriter writer = new FileWriter("./guilds/"+settings.getId()+".json")) {
            gson.toJson(settings, writer);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
