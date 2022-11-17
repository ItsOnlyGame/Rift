package com.iog;

import com.iog.Commands.CommandHandler;
import com.iog.Handlers.EventHandler;
import com.iog.Utils.Settings;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.io.IOUtils;
import org.tinylog.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Main {

    public static GatewayDiscordClient gateway;
    public static final String Version = readVersion();

    public static void main(String[] args) {
        Settings settings = Settings.getSettings();

        // Login to Discord API
        Logger.info("Started Rift with Version: " + Version);
    
        final DiscordClient client = DiscordClient.create(settings.discordToken);
        Main.gateway = client.login().block();
        assert gateway != null;

        // Sets Discord API events and creates CommandHandler
        CommandHandler commandHandler = new CommandHandler(client);
        gateway.on(MessageCreateEvent.class).subscribe(commandHandler::messageCreateEvent);
        gateway.on(ChatInputInteractionEvent.class).subscribe(commandHandler::interactionInputEvent);
        gateway.on(VoiceStateUpdateEvent.class).subscribe(EventHandler::VoiceStateUpdate);
        gateway.onDisconnect().block();
    }


    private static String readVersion() {
        InputStream stream = Main.class.getResourceAsStream("version.txt");
        try {
            if (stream != null) {
                return IOUtils.toString(stream, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            // Something went wrong.
        }

        return "UNKNOWN";
    }
}