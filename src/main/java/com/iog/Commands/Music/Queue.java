package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.TrackQueue;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;
import java.util.List;

public class Queue extends BaseCommand {
	
	public Queue() {
		super(
			new String[]{"queue", "q"},
			ApplicationCommandRequest.builder()
				.name("queue")
				.description("Show the queue in this guild")
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
		AudioTrack playing = manager.getPlayer().getPlayingTrack();
		List<String> parts = createQueueMessage(manager);
		
		if (playing == null) {
			message.getChannel().subscribe(channel -> channel.createMessage("Queue is empty").subscribe());
			return;
		}
		
		for (String part: parts) {
			message.getChannel().subscribe(channel -> channel.createMessage(part).subscribe());
		}
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		final GuildAudioManager manager = GuildAudioManager.of(interaction.getInteraction().getGuildId().orElseThrow());
		AudioTrack playing = manager.getPlayer().getPlayingTrack();
		List<String> parts = createQueueMessage(manager);
		
		if (playing == null) {
			interaction.editReply("Queue is empty").subscribe();
			return;
		}
		
		for (String part: parts) {
			interaction.createFollowup(part).subscribe();
		}
	}
	
	
	private List<String> createQueueMessage(GuildAudioManager manager) {
		TrackQueue queue = manager.getScheduler().getQueue();
		AudioTrack playing = manager.getPlayer().getPlayingTrack();
		
		List<String> parts = new ArrayList<>();
		for (int times = 0; times < Math.ceil(queue.size() / 50f); times++) {
			StringBuilder builder = new StringBuilder();
			
			builder.append("```");
			if (times == 0) {
				builder.append("Now playing: ").append(playing.getInfo().title).append(" ").append(Format.millisecondsToString(playing.getDuration())).append("\n");
			}
			
			for (int i = times * 50; i < (times * 50) + 50; i++) {
				if (queue.size() <= i) break;
				AudioTrack t = queue.get(i);
				builder.append((i + 1)).append(": ").append(t.getInfo().title).append(" | ").append(Format.millisecondsToString(t.getDuration())).append("\n");
			}
			
			builder.append("```");
			parts.add(builder.toString());
		}
		
		return parts;
	}
	
}


