package com.iog.Commands.Other;

import com.iog.Commands.BaseCommand;
import com.iog.Handlers.GuildSettings;
import com.iog.Utils.Format;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.time.Instant;

public class Delete extends BaseCommand {
	
	public Delete() {
		super(
			new String[]{"delete", "del"},
			ApplicationCommandRequest.builder()
				.name("delete")
				.description("Deletes message(s)")
				.addOption(ApplicationCommandOptionData.builder()
					.name("amount")
					.description("Amount of messages to delete")
					.type(ApplicationCommandOption.Type.INTEGER.getValue())
					.required(false)
					.build())
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) {
		if (!hasPermission(message.getAuthorAsMember().blockOptional().orElseThrow())) {
			message.getChannel().subscribe(channel -> channel.createMessage("You don't have permissions to use this command").subscribe());
			return;
		}
		
		Integer amount;
		if (args.length == 0) {
			amount = 1;
		} else {
			amount = Format.toInteger(args[0]);
			
			if (amount == null) {
				message.getChannel().subscribe(channel -> channel.createMessage("Given argument wasn't a valid number").subscribe());
				return;
			}
		}
		
		message.getChannel().cast(GuildMessageChannel.class).subscribe(messageChannel -> {
			messageChannel.getMessagesBefore(Snowflake.of(Instant.now())).take(amount).map(Message::getId).transform(messageChannel::bulkDelete).subscribe();
		});
		
		if (!GuildSettings.of(message.getGuildId().orElseThrow().asLong()).isAutoClean()) {
			message.delete().subscribe();
		}
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		if (!hasPermission(interaction.getInteraction().getMember().orElseThrow())) {
			interaction.editReply("You don't have permissions to use this command").subscribe();
			return;
		}
		
		long amount = 1;
		boolean amountExists = interaction.getOption("amount").orElseThrow().getValue().isPresent();
		if (amountExists) {
			amount = interaction.getOption("amount").orElseThrow().getValue().get().asLong();
		}
		
		long finalAmount = amount;
		interaction.getInteraction().getChannel().cast(GuildMessageChannel.class).subscribe(messageChannel -> {
			messageChannel.getMessagesBefore(Snowflake.of(Instant.now())).take(finalAmount).map(Message::getId).transform(messageChannel::bulkDelete).subscribe();
		});
		
		interaction.deleteReply().subscribe();
		
	}
	
	private boolean hasPermission(Member member) {
		PermissionSet set = member.getBasePermissions().blockOptional().orElseThrow();
		return set.contains(Permission.ADMINISTRATOR) || set.contains(Permission.MANAGE_MESSAGES);
	}
	
}
