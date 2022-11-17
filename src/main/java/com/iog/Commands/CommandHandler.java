package com.iog.Commands;

import com.iog.Handlers.GuildSettings;
import com.iog.Main;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Settings;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.reflections.Reflections;
import org.tinylog.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CommandHandler {
    public List<BaseCommand> commands = new ArrayList<>();

    public CommandHandler(DiscordClient client) {
        Reflections reflections = new Reflections("com.iog.Commands");
        Set<Class<? extends BaseCommand>> classes = reflections.getSubTypesOf(BaseCommand.class);

        for (Class<?> c : classes) {
            try {
                Constructor<?> constructor = c.getConstructor();
                BaseCommand command = (BaseCommand) constructor.newInstance();

                commands.add(command);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        Settings settings = Settings.getSettings();
        
        if (settings.refreshSlashCommands) {
            List<ApplicationCommandRequest> commandRequestList = commands.stream().map(BaseCommand::getApplicationCommand).toList();

            client.getApplicationService()
                .bulkOverwriteGlobalApplicationCommand(484004377841893396L, commandRequestList)
                .subscribe();
            
            settings.refreshSlashCommands = false;
            Settings.saveSettings(settings);
        }
    
        if (!settings.guildIdSlashCommandsRefresh.equals("")) {
            List<ApplicationCommandRequest> commandRequestList = commands.stream().map(BaseCommand::getApplicationCommand).toList();
            String guildId = settings.guildIdSlashCommandsRefresh;
            
            client.getApplicationService()
                .bulkOverwriteGuildApplicationCommand(484004377841893396L, Snowflake.of(guildId).asLong(), commandRequestList)
                .subscribe();
        }

    }

    public BaseCommand getCommand(String alias) {
        for (BaseCommand command : commands) {
            if (command.hasAlias(alias)) {
                return command;
            }
        }
        return null;
    }

    public BaseCommand getApplicationCommand(String name) {
        for (BaseCommand command : commands) {
            if (command.getApplicationCommand().name().equals(name)) {
                return command;
            }
        }
        return null;
    }


    public void messageCreateEvent(MessageCreateEvent event) throws CommandExecutionException {
        final Message message = event.getMessage();
        final GuildSettings settings = GuildSettings.of(event.getGuildId().orElseThrow().asLong());

        String content = event.getMessage().getContent();
        if (!content.startsWith(settings.getPrefix())) {
            return;
        }

        String alias = content.substring(settings.getPrefix().length()).split(" ")[0];
        String[] arguments = content.substring(settings.getPrefix().length() + alias.length()).trim().split(" ");

        if (arguments.length == 1) {
            if (arguments[0].equals("")) {
                arguments = new String[0];
            }
        }

        BaseCommand command = getCommand(alias);
        if (command != null) {
            try {
                command.run(message, arguments);
            } catch (Exception e) {
                Logger.error(e, e.getMessage());
            }
            if (settings.isAutoClean()) {
                message.delete().subscribe();
            }
            return;
        }

        message.getChannel().subscribe(textChannel -> textChannel.createMessage("Command \"" + alias + "\" not found").subscribe());
    }

    public void interactionInputEvent(ChatInputInteractionEvent event) throws CommandExecutionException {
        BaseCommand command = getApplicationCommand(event.getCommandName());
        if (command != null) {
            event.deferReply().block();
            try {
                command.run(event);
            } catch (Exception e) {
                Logger.error(e, e.getMessage());
                event.editReply("Error occurred: \n" + e.getMessage()).subscribe();
            }
        }
    }
}
