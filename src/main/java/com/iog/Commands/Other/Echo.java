package com.iog.Commands.Other;

import com.iog.Commands.BaseCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Echo extends BaseCommand {
	
	public Echo() {
		super(
			new String[]{"echo", "e"},
			ApplicationCommandRequest.builder()
				.name("echo")
				.description("Echoes your message")
				.addOption(ApplicationCommandOptionData.builder()
					.name("text")
					.description("Text to echo")
					.type(ApplicationCommandOption.Type.STRING.getValue())
					.required(true)
					.build()
				).build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) {
		String echo = String.join(" ", args);
		message.getChannel().subscribe(messageChannel -> messageChannel.createMessage(echo).subscribe());
		message.delete().block();
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		String echo = interaction.getOption("text").orElseThrow().getValue().orElseThrow().asString();
		interaction.deleteReply().block();
		interaction.getInteraction().getChannel().subscribe(messageChannel -> messageChannel.createMessage(echo).subscribe());
	}
}
