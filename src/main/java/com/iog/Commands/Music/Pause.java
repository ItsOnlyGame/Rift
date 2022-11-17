package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.ConnectionUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.voice.VoiceConnection;

public class Pause extends BaseCommand {
	
	public Pause() {
		super(
			new String[]{"pause"},
			ApplicationCommandRequest.builder()
				.name("pause")
				.description("Pauses the current track")
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
		
		message.getAuthorAsMember().subscribe(member -> {
			if (!ConnectionUtils.botIsInSameVoiceChannel(member, message.getGuildId().orElseThrow())) {
				message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
				return;
			}
			
			if (!manager.getPlayer().isPaused()) {
				manager.getPlayer().setPaused(true);
				message.getChannel().subscribe(channel -> channel.createMessage("Paused track").subscribe());
			} else {
				message.getChannel().subscribe(channel -> channel.createMessage("Tracks has already been paused").subscribe());
			}
		});
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		final Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		final GuildAudioManager manager = GuildAudioManager.of(guildId);
		final Member member = interaction.getInteraction().getMember().orElseThrow();
		
		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guildId)) {
			interaction.editReply("You have to be in the same voice channel as I").subscribe();
			return;
		}
		
		if (!manager.getPlayer().isPaused()) {
			manager.getPlayer().setPaused(true);
			interaction.editReply("Paused track").subscribe();
		} else {
			interaction.editReply("Tracks has already been paused").subscribe();
		}
		
	}
}

