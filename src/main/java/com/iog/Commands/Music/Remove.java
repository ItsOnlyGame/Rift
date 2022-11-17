package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.TrackQueue;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.ConnectionUtils;
import com.iog.Utils.Format;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
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

public class Remove extends BaseCommand {
	
	public Remove() {
		super(
			new String[]{"remove", "rm"},
			ApplicationCommandRequest.builder()
				.name("remove")
				.description("Removes the track at that the given index")
				.addOption(ApplicationCommandOptionData.builder()
					.name("index")
					.description("The index of a track in queue")
					.type(ApplicationCommandOption.Type.INTEGER.getValue())
					.required(true)
					.build())
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		message.getAuthorAsMember().subscribe(member -> {
			
			if (!ConnectionUtils.botIsInSameVoiceChannel(member, message.getGuildId().orElseThrow())) {
				message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
				return;
			}
			
			GuildAudioManager musicManager = GuildAudioManager.of(message.getGuildId().orElseThrow());
			
			if (musicManager.getPlayer().getPlayingTrack() == null) {
				message.getChannel().subscribe(channel -> channel.createMessage("Queue is empty").subscribe());
				return;
			}
			
			TrackQueue queue = new TrackQueue(musicManager.getScheduler().getQueue());
			
			Integer index = Format.toInteger(args[0]);
			
			if (index == null) {
				message.getChannel().subscribe(channel -> channel.createMessage("Give a valid position in the queue to remove it").subscribe());
				return;
			}
			
			if (index > queue.size() || index <= 0) {
				message.getChannel().subscribe(channel -> channel.createMessage("Give a valid position in the queue to remove it").subscribe());
				return;
			}
			
			AudioTrackInfo info = queue.get(index - 1).getInfo();
			musicManager.getScheduler().getQueue().remove(index - 1);
			
			
			message.getChannel().subscribe(channel -> channel.createMessage("Removed ``" + info.title + "`` from the queue").subscribe());
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
			interaction.editReply("Queue is empty").subscribe();
			return;
		}
		
		TrackQueue queue = new TrackQueue(musicManager.getScheduler().getQueue());
		
		
		boolean indexExists = interaction.getOption("index").orElseThrow().getValue().isPresent();
		if (!indexExists) {
			interaction.editReply("Give a valid position in the queue to remove it").subscribe();
			return;
		}
		int index = (int)interaction.getOption("index").orElseThrow().getValue().get().asLong();
		
		
		if (index > queue.size() || index <= 0) {
			interaction.editReply("Give a valid position in the queue to remove it").subscribe();
			return;
		}
		
		AudioTrackInfo info = queue.get(index - 1).getInfo();
		musicManager.getScheduler().getQueue().remove(index - 1);
		
		interaction.editReply("Removed ``" + info.title + "`` from the queue").subscribe();
	}
}
