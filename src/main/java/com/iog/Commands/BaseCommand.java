package com.iog.Commands;

import com.iog.Utils.CommandExecutionException;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandRequest;

public abstract class BaseCommand {

    private final String[] aliases;
    private final ApplicationCommandRequest applicationCommand;

    /**
     * Base class for Commands
     * @param aliases The values that are connected to this command
     */
    public BaseCommand(String[] aliases, ApplicationCommandRequest applicationCommand) {
        this.aliases = aliases;
        this.applicationCommand = applicationCommand;
    }

    /**
     * This function executes the command
     * @param message Message
     */
    public abstract void run(Message message, String[] args);

    /**
     * This function executes the command
     * @param interaction ChatInputInteractionEvent
     */
    public abstract void run(ChatInputInteractionEvent interaction);


    /**
     * Checks whether this command has the same alias connected to it
     * @param alias The alias
     * @return True if it does and false if it doesn't
     */
    public boolean hasAlias(String alias) {
        for (String a : this.aliases) {
            if (a.equals(alias)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the name of this class, which is usually just the class name
     * @return Command name
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * @return All aliases connected to this command
     */
    public String[] getAliases() {
        return aliases;
    }

    public ApplicationCommandRequest getApplicationCommand() {
        return applicationCommand;
    }

}
