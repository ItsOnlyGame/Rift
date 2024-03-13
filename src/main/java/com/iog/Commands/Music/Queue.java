package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.TrackQueue;
import com.iog.Utils.Format;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Queue extends BaseCommand {
	
	public Queue() {
		super(
            Commands.slash("queue", "Show the queue in this guild")
		);
	}
	
	@Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		final GuildAudioManager manager = GuildAudioManager.of(event.getGuild());
		AudioTrack playing = manager.getPlayer().getPlayingTrack();
		List<String> parts = createQueueMessage(manager);
		
		if (playing == null) {
			event.reply("Queue is empty").queue();
			return;
		}
		
		for (String part : parts) {
			event.reply(part).queue();
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


