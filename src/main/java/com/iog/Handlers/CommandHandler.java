package com.iog.Handlers;

import com.iog.Commands.Other.Help;
import com.iog.Handlers.Commands.BaseCommand;
import com.iog.Utils.CommandExecutionException;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandHandler {

    public List<BaseCommand> commands = new ArrayList<>();

    public CommandHandler() {
        Reflections reflections = new Reflections("com.iog.Commands");
        Set<Class<? extends BaseCommand>> classes = reflections.getSubTypesOf(BaseCommand.class);
        classes.removeIf(c ->
                c.getSimpleName().equals("MusicCommand") ||
                        c.getSimpleName().equals("BasicCommand") ||
                        c.getSimpleName().equals("DebugCommand") ||
                        c.getSimpleName().equals("SettingsCommand") ||
                        c.getSimpleName().equals("ModerationCommand")
        );

        for (Class<?> c : classes) {
            try {
                Constructor<?> constructor;
                BaseCommand command;

                if (c.getSimpleName().equals("Help")) {
                    constructor = c.getConstructor(CommandHandler.class);
                    command = (Help) constructor.newInstance(this);
                } else {
                    constructor = c.getConstructor();
                    command = (BaseCommand) constructor.newInstance();
                }

                commands.add(command);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
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


    public void handle(MessageCreateEvent event) throws CommandExecutionException {
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
            command.execute(message, arguments);
            if (settings.isAutoClean()) {
                message.delete().subscribe();
            }
            return;
        }

        message.getChannel().subscribe(textChannel -> textChannel.createMessage("Command \"" + alias + "\" not found").subscribe());
    }
}
