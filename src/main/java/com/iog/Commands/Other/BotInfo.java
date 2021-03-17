package com.iog.Commands.Other;

import com.iog.Handlers.Commands.BasicCommand;
import com.iog.Main;
import discord4j.core.object.entity.Message;

public class BotInfo extends BasicCommand {

    public BotInfo() {
        super(new String[]{"botinfo","bot", "this"}, "Gives information about the bot");
    }

    @Override
    public void run(Message message, String[] args) {
        String builder = "```" +
                "Rift: "+"\n"+
                "Version: " + Main.Version + "\n" +
                "ID: " + Main.gateway.getSelfId().asLong()+ "\n" +
                "Github: " + "**https://github.com/ItsOnlyGame/Rift**" + "\n\n" +
                "You can report you issues to github\n```";

        message.getChannel().subscribe(channel -> channel.createMessage(builder).subscribe());
    }

    @Override
    public String getName() {
        return "Bot Info";
    }
}
