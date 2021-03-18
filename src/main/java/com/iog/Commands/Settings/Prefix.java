package com.iog.Commands.Settings;

import com.iog.Handlers.Commands.SettingsCommand;
import com.iog.Handlers.GuildSettings;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;

public class Prefix extends SettingsCommand {

    public Prefix() {
        super(new String[]{"prefix"}, "Changes server prefix", new Permission[]{ Permission.ADMINISTRATOR});
    }

    @Override
    public void run(Message message, String[] args) {
        GuildSettings settings = GuildSettings.of(message.getGuildId().orElseThrow().asLong());
        if (args.length == 0) {
            message.getChannel().subscribe(channel -> channel.createMessage("Server prefix is " + settings.getPrefix()).subscribe());
            return;
        }

        String newprefix = args[0].trim();
        message.getChannel().subscribe(channel -> channel.createMessage("New server prefix is " + newprefix).subscribe());
        settings.setPrefix(newprefix).save();
    }
}
