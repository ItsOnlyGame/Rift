package com.iog.Commands.Other;

import com.iog.Handlers.CommandHandler;
import com.iog.Handlers.Commands.BaseCommand;
import com.iog.Handlers.GuildSettings;
import discord4j.core.object.entity.Message;

public class Help extends BaseCommand {

    private final CommandHandler handler;

    public Help(CommandHandler handler) {
        super(
                new String[]{"help"},
                "This command give help on other commands"
        );
        this.handler = handler;
    }

    @Override
    public void run(Message message, String[] args) {
        String prefix = GuildSettings.of(message.getGuildId().orElseThrow().asLong()).getPrefix();

        StringBuilder builder = new StringBuilder();
        builder.append("```");
        for (BaseCommand command : this.handler.commands) {
            builder.append(command.getName()).append(": ").append(prefix).append(command.getAliases()[0]).append(" | ")
                    .append(command.getDescription()).append("\n");
        }
        builder.append("```");

        message.getChannel().subscribe(channel -> channel.createMessage(builder.toString()).subscribe());
    }
}
