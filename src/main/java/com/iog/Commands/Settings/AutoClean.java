package com.iog.Commands.Settings;

import com.iog.Handlers.Commands.SettingsCommand;
import com.iog.Handlers.GuildSettings;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;

public class AutoClean extends SettingsCommand {

    public AutoClean() {
        super(new String[]{"autoclean"}, "Whether to delete the user command message", new Permission[]{ Permission.ADMINISTRATOR});
    }

    @Override
    public void run(Message message, String[] args) {
        GuildSettings settings = GuildSettings.of(message.getGuildId().orElseThrow().asLong());
        if (args.length == 0) {
            message.getChannel().subscribe(channel -> channel.createMessage("Auto Clean is " + settings.isAutoClean()).subscribe());
            return;
        }

        boolean newAutoClean;
        try {
            newAutoClean = Boolean.parseBoolean(args[0].trim());
        } catch (Exception e) {
            message.getChannel().subscribe(channel -> channel.createMessage("Invalid argument, should be a boolean value (true/false)").subscribe());
            return;
        }
        message.getChannel().subscribe(channel -> channel.createMessage("Auto clean set to " + newAutoClean).subscribe());
        settings.setAutoClean(newAutoClean).save();
    }
}
