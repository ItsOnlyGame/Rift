package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.ConnectionUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Stop extends BaseCommand {
	
	public Stop() {
		super(
			new String[]{"stop"},
			ApplicationCommandRequest.builder()
				.name("stop")
				.description("Stop playback")
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
			
			manager.getScheduler().getQueue().clear();
			manager.getPlayer().stopTrack();
			message.getChannel().subscribe(channel -> channel.createMessage("Queue cleared and playback stopped").subscribe());
		});
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		final GuildAudioManager manager = GuildAudioManager.of(guildId);
		Member member = interaction.getInteraction().getMember().orElseThrow();
		
		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guildId)) {
			interaction.editReply("You have to be in the same voice channel as I").subscribe();
			return;
		}
		
		manager.getScheduler().getQueue().clear();
		manager.getPlayer().stopTrack();
		interaction.editReply("Queue cleared and playback stopped").subscribe();
	}
}
