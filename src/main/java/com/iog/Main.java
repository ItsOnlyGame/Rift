package com.iog;

import com.iog.Commands.SlashCommandListener;
import com.iog.Handlers.GuildVoiceEvent;
import com.iog.Utils.Settings;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {

    public static final String Version = readVersion();

    public static void main(String[] args) throws InterruptedException {
        Settings settings = Settings.getSettings();

        Logger.info("Started Rift with Version: " + Version);

        final var jda = JDABuilder.createDefault(settings.discordToken)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new SlashCommandListener())
                .addEventListeners(new GuildVoiceEvent())
                .build()
                .awaitReady();
    }

    private static String readVersion() {
        InputStream inputStream = Main.class.getResourceAsStream("version.txt");

        if (inputStream  != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.readLine();
            } catch (IOException e) {
                Logger.error("Version file read error");
            }
        }

        return "UNKNOWN";
    }
}