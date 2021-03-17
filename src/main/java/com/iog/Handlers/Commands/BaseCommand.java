package com.iog.Handlers.Commands;

import com.iog.Utils.CommandExecutionException;
import discord4j.core.object.entity.Message;

public abstract class BaseCommand {

    private final String[] aliases;
    private final String description;

    public BaseCommand(String[] aliases, String description) {
        this.aliases = aliases;
        this.description = description;
    }

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

    public boolean hasAlias(String alias) {
        for (String a : this.aliases) {
            if (a.equals(alias)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }
}
