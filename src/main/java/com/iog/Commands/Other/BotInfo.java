package com.iog.Commands.Other;

import com.iog.Commands.BaseCommand;
import com.iog.Main;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class BotInfo extends BaseCommand {
	
	private final String text = "```" +
		"Rift: " + "\n" +
		"Version: " + Main.Version + "\n" +
		"ID: " + Main.gateway.getSelfId().asLong() + "\n" +
		"Github: " + "**https://github.com/ItsOnlyGame/Rift**" + "\n\n" +
		"You can report you issues to github\n```";
	
	public BotInfo() {
		super(
			new String[]{"botinfo", "bot", "this"},
			ApplicationCommandRequest.builder()
				.name("bot-info")
				.description("Tells information about the bot")
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) {
		message.getChannel().subscribe(channel -> channel.createMessage(text).subscribe());
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		interaction.editReply(text).subscribe();
	}
}
