package com.iog.Handlers.Commands;

import com.iog.Handlers.GuildSettings;
import com.iog.Utils.CommandExecutionException;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;

public abstract class MusicCommand extends BaseCommand {

    public MusicCommand(String[] aliases, String description) {
        super(aliases, description);
    }

    @Override
    public void execute(Message message, String[] args) {
        GuildSettings server = GuildSettings.of(message.getGuildId().orElseThrow().asLong());
        Role role = message.getGuild().blockOptional().orElseThrow().getRoles().filter(r -> r.getId().asLong() == server.getDj()).blockFirst();

        if (role != null) {
            if (!this.hasRole(message.getAuthorAsMember().blockOptional().orElseThrow(), server.getDj())) {
                return;
            }
        }

        super.execute(message, args);
    }

    /**
     * This function executes the command
     * @param message CommandData
     */
    @Override
    public abstract void run(Message message, String[] args) throws CommandExecutionException;

    private boolean hasRole(Member author, long roleId) {
        Role role = author.getRoles().filter(r -> r.getId().asLong() == roleId).blockFirst();
        return role != null;
    }

}
