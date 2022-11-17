package com.iog.Commands.Other;

import com.iog.Commands.BaseCommand;
import com.iog.Utils.Format;
import com.iog.Utils.Settings;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Help extends BaseCommand {
	
	private final EmbedCreateSpec embed;
	
	public Help() {
		super(
			new String[]{"help"},
			ApplicationCommandRequest.builder()
				.name("help")
				.description("Gives help about this bots functions")
				.build()
		);
		
		Settings settings = Settings.getSettings();
		this.embed = EmbedCreateSpec.builder()
			.author("Help", null, null)
			.color(Format.hexToColor(settings.defaultColors.get("success")))
			.description("Every command is listed under this link: \nhttps://github.com/ItsOnlyGame/Rift#commands")
			.build();
		
	}
	
	@Override
	public void run(Message message, String[] args) {
		message.getChannel().subscribe(channel -> channel.createMessage(this.embed).subscribe());
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		interaction.editReply().withEmbeds(this.embed).subscribe();
	}
}
