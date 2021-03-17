package com.iog.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {
    private static final transient Logger logger = Logger.getLogger(Settings.class);

    public String DiscordToken;
    public String SpotifyToken;

    public String CountryCode;

    private final String NormalColorHex = String.format("#%02x%02x%02x", 130, 180, 160);
    private final String ErrorColorHex = String.format("#%02x%02x%02x", 255, 100, 100);

    public String getDiscordToken() {
        return DiscordToken;
    }

    public String getSpotifyToken() {
        return SpotifyToken;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public discord4j.rest.util.Color getNormalColor() {
        Color color = Color.decode(NormalColorHex);
        return discord4j.rest.util.Color.of(color.getRGB());
    }

    public discord4j.rest.util.Color getErrorColor() {
        Color color = Color.decode(ErrorColorHex);
        return discord4j.rest.util.Color.of(color.getRGB());
    }


    private static Settings SETTINGS;

    public static Settings getSettings() {
        if (Settings.SETTINGS == null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

            String file_GlobalSettings = "./GlobalSettings.json";
            File file = new File(file_GlobalSettings);

            if (!file.exists()) {
                try (FileWriter writer = new FileWriter(file_GlobalSettings)) {
                    gson.toJson(new Settings(), writer);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                String e = "-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n-\n";
                logger.info(e+"Fill in the info at the GlobalSettings file");
                System.exit(10);
            }
            try (FileReader reader = new FileReader(file_GlobalSettings)) {
                return gson.fromJson(reader, Settings.class);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                System.exit(11);
            }
        }
        return Settings.SETTINGS;
    }





}
