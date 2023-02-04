package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.ConnectionUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class ClearQueue extends BaseCommand {
	
	public ClearQueue() {
		super(
			new String[]{"clearqueue", "clear"},
			ApplicationCommandRequest.builder()
				.type(ApplicationCommand.Type.CHAT_INPUT.getValue())
				.name("clear-queue")
				.description("Clears the track queue")
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) {
		message.getAuthorAsMember().subscribe(member -> {
			if (!ConnectionUtils.botIsInSameVoiceChannel(member, message.getGuildId().orElseThrow())) {
				message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
				return;
			}
			
			GuildAudioManager musicManager = GuildAudioManager.of(message.getGuildId().orElseThrow());
			if (musicManager.getPlayer().getPlayingTrack() == null) {
				message.getChannel().subscribe(channel -> channel.createMessage("Queue is already empty").subscribe());
				return;
			}
			
			musicManager.getScheduler().getQueue().clear();
			message.getChannel().subscribe(channel -> channel.createMessage("Queue cleared").subscribe());
		});
		
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		Member member = interaction.getInteraction().getMember().orElseThrow();
		Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		
		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guildId)) {
			interaction.editReply("You have to be in the same voice channel as I").subscribe();
			return;
		}
		
		GuildAudioManager musicManager = GuildAudioManager.of(guildId);
		
		if (musicManager.getPlayer().getPlayingTrack() == null) {
			interaction.editReply("Queue is already empty").subscribe();
			return;
		}
		
		musicManager.getScheduler().getQueue().clear();
		interaction.editReply("Queue cleared").subscribe();
	}
}
