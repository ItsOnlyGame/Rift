package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.ConnectionUtils;
import com.iog.Utils.Format;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.voice.VoiceConnection;

public class Skip extends BaseCommand {
	
	public Skip() {
		super(
			new String[]{"skip", "s"},
			ApplicationCommandRequest.builder()
				.type(ApplicationCommand.Type.CHAT_INPUT.getValue())
				.name("skip")
				.description("Removes the track at that the given index")
				.addOption(ApplicationCommandOptionData.builder()
					.name("index")
					.description("The index of a track in queue")
					.type(ApplicationCommandOption.Type.INTEGER.getValue())
					.required(false)
					.build())
				.build());
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
		
		message.getAuthorAsMember().subscribe(member -> {
			if (!ConnectionUtils.botIsInSameVoiceChannel(member, message.getGuildId().orElseThrow())) {
				message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
				return;
			}
			
			if (args.length > 0) {
				Integer index = Format.toInteger(args[0].trim());
				if (index == null) {
					message.getChannel().subscribe(channel -> channel.createMessage("Please give a index of where in the queue you want to land").subscribe());
					return;
				}
				manager.getScheduler().skip(index);
			} else {
				manager.getScheduler().skip(0);
				message.getChannel().subscribe(channel -> channel.createMessage("Skipping track...").subscribe());
			}
			
		});
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		Member member = interaction.getInteraction().getMember().orElseThrow();
		Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		final GuildAudioManager manager = GuildAudioManager.of(guildId);
		
		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guildId)) {
			interaction.editReply("You have to be in the same voice channel as I").subscribe();
			return;
		}

		boolean amountExists = interaction.getOption("amount").orElseThrow().getValue().isPresent();
		if (!amountExists) {
			manager.getScheduler().skip(0);
			interaction.deleteReply().subscribe();
			return;
		}
		
		int amount = (int)interaction.getOption("amount").orElseThrow().getValue().get().asLong();
		if (amount > 0) {
			manager.getScheduler().skip(amount);
			interaction.deleteReply().subscribe();
			return;
		}
		
		interaction.editReply("Please give a index of where in the queue you want to land").subscribe();
	}
}


