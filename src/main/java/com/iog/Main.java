package com.iog;

import com.iog.Handlers.CommandHandler;
import com.iog.Handlers.EventHandler;
import com.iog.Utils.Settings;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Main {
    private static final transient Logger logger = Logger.getLogger(Main.class);

    public static GatewayDiscordClient gateway;
    public static final String Version = readVersion();

    public static void main(final String[] args) {
        Settings settings = Settings.getSettings();

        /*
         * Login to Discord API
         */
        logger.info("Started Rift with Version: " + Version);
        final DiscordClient client = DiscordClient.create(settings.DiscordToken);
        Main.gateway = client.login().block();
        assert gateway != null;

        /*
         * Sets Discord API events and creates CommandHandler
         */
        CommandHandler commandHandler = new CommandHandler();
        gateway.on(MessageCreateEvent.class).subscribe(commandHandler::handle);
        gateway.on(VoiceStateUpdateEvent.class).subscribe(EventHandler::VoiceStateUpdate);
        gateway.on(MemberJoinEvent.class).subscribe(EventHandler::MemberJoin);
        gateway.onDisconnect().block();
    }

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH.mm");
        System.setProperty("AppStartTime", dateFormat.format(new Date()));

        Properties properties = new Properties();

        properties.put("log4j.rootLogger", "INFO, console, file");
        properties.put("log4j.logger.org.mongodb.driver", "ERROR");
        properties.put("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
        properties.put("log4j.appender.file.File", "logs/${AppStartTime}.log");
        properties.put("log4j.appender.file.MaxFileSize", "5MB");
        properties.put("log4j.appender.file.MaxBackupIndex", "10");
        properties.put("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
        properties.put("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %5p [%t] (%F:%L) - %m%n");

        properties.put("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
        properties.put("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
        properties.put("log4j.appender.console.layout.conversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %5p [%t] (%F:%L) - %m%n");

        PropertyConfigurator.configure(properties);
    }

    /**
     * Gets the current version that gradle generates, so I don't have to do manual labor every time I update
     * @return The current version from version.txt
     */
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
