package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.lyrics.LyricsInfo;
import com.sedmelluq.discord.lavaplayer.track.lyrics.LyricsManager;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lyrics extends BaseCommand {
	
	public Lyrics() {
		super(
			new String[]{"lyrics"},
			ApplicationCommandRequest.builder()
				.type(ApplicationCommand.Type.CHAT_INPUT.getValue())
				.name("lyrics")
				.description("Finds lyrics for a song")
				.addOption(ApplicationCommandOptionData.builder()
					.name("song-title")
					.description("Title of a song")
					.type(ApplicationCommandOption.Type.STRING.getValue())
					.required(false)
					.build())
				.build()
		);
		
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		AudioTrack audioTrack = GuildAudioManager.of(message.getGuildId().orElseThrow()).getPlayer().getPlayingTrack();
		
		String searchQuery;
		if (args.length > 0) {
			searchQuery = String.join(" ", args);
		} else if (audioTrack != null) {
			if (audioTrack.getSourceManager().getSourceName().equalsIgnoreCase("Spotify")) {
				searchQuery = audioTrack.getInfo().author + " " + audioTrack.getInfo().title;
			} else {
				searchQuery = audioTrack.getInfo().title;
			}
		} else {
			message.getChannel().subscribe(channel -> channel.createMessage("Nothing playing, so no lyrics to fetch").subscribe());
			return;
		}
		
		
		LyricsInfo lyrics;
		try {
			lyrics = LyricsManager.getLyrics(searchQuery);
		} catch (IOException e) {
			throw new CommandExecutionException(e.getMessage(), CommandExecutionException.Severity.COMMON, e.getCause());
		}
		
		if (lyrics.getLyrics().equals("")) {
			message.getChannel().subscribe(channel -> channel.createMessage("No lyrics found").subscribe());
			return;
		}
		
		List<String> parts = getParts(lyrics.getLyrics(), searchQuery, lyrics.getUrl());
		
		for (String part : parts) {
			message.getChannel().subscribe(channel -> channel.createMessage(part).subscribe());
		}
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		AudioTrack audioTrack = GuildAudioManager.of(guildId).getPlayer().getPlayingTrack();
		
		String searchQuery;
		boolean searchQueryExists = interaction.getOption("song-title").orElseThrow().getValue().isPresent();
		if (searchQueryExists) {
			searchQuery = interaction.getOption("song-title").orElseThrow().getValue().get().asString();
		} else if (audioTrack != null) {
			if (audioTrack.getSourceManager().getSourceName().equalsIgnoreCase("Spotify")) {
				searchQuery = audioTrack.getInfo().author + " " + audioTrack.getInfo().title;
			} else {
				searchQuery = audioTrack.getInfo().title;
			}
		} else {
			interaction.editReply("Nothing playing, so no lyrics to fetch").subscribe();
			return;
		}
		
		
		LyricsInfo lyrics;
		try {
			lyrics = LyricsManager.getLyrics(searchQuery);
		} catch (IOException e) {
			throw new CommandExecutionException(e.getMessage(), CommandExecutionException.Severity.COMMON, e.getCause());
		}
		
		if (lyrics.getLyrics().equals("")) {
			interaction.editReply("No lyrics found").subscribe();
			return;
		}
		
		
		List<String> parts = getParts(lyrics.getLyrics(), searchQuery, lyrics.getUrl());
		
		for (String part : parts) {
			interaction.editReply(part).block();
		}
	}

	private List<String> getParts(String lyricsString, String searchQuery, String lyricsUrl) {
		int partitionSize = 2000;
		
		List<String> parts = new ArrayList<>();
		
		String[] array = lyricsString.split("\n\n");
		if (array.length == 1)
			array = lyricsString.split("\n");
		
		StringBuilder temp = new StringBuilder();
		int len = 0;
		
		for (String str : array) {
			len += str.length() + 4;
			
			if (len >= partitionSize) {
				parts.add(temp.toString());
				temp = new StringBuilder();
				temp.append(str);
				len = 0;
			} else {
				temp.append(str).append("\n\n");
			}
		}
		if (len != 0) {
			parts.add(temp.toString());
		}
		
		// Format the parts and add them to an arraylist
		List<String> formattedParts = new ArrayList<>();
		
		for (int i = 0; i < parts.size(); i++) {
			StringBuilder builder = new StringBuilder();
			builder.append("```");
			String part = parts.get(i);
			if (i == 0) {
				builder.append("Lyrics for ").append(searchQuery).append("\n\n");
			}
			builder.append(part);
			if (i + 1 == parts.size()) {
				builder.append("Lyrics URL: ").append(lyricsUrl);
			}
			builder.append("```");
			formattedParts.add(builder.toString());
		}
		
		return formattedParts;
	}
}