package com.iog.Handlers.Commands;

import com.iog.Utils.CommandExecutionException;
import discord4j.core.object.entity.Message;

public abstract class BaseCommand {

    private final String[] aliases;
    private final String description;

    /**
     * Base class for Commands
     * @param aliases The values that are connected to this command
     * @param description Description on what this commands does
     */
    public BaseCommand(String[] aliases, String description) {
        this.aliases = aliases;
        this.description = description;
    }

    /**
     * Ran before the main command execution. Used for verifying permissions or roles
     * @param message Message that will be passed on to the command
     * @param args Arguments that will be passed on to the command
     */
    public void execute(Message message, String[] args) {
        try {
            run(message, args);
        } catch (CommandExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function executes the command
     * @param message CommandData
     */
    public abstract void run(Message message, String[] args);

    /**
     * Checks whether this commands has the same alias connected to it
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

    /**
     * @return Command description
     */
    public String getDescription() {
        return description;
    }
}
