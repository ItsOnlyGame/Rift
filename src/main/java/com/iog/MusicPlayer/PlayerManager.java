package com.iog.MusicPlayer;

import java.awt.Color;

import com.iog.MusicPlayer.Spotify.SpotifyAudioSourceManager;
import com.iog.Utils.Format;
import com.iog.Utils.Settings;
import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidWithThumbnail;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import dev.lavalink.youtube.clients.skeleton.Client;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

public class PlayerManager {
	private static PlayerManager INSTANCE;
	public final AudioPlayerManager playerManager;
	
	public PlayerManager() {
		this.playerManager = new DefaultAudioPlayerManager();
		
		// Setup YouTube, SoundCloud and Twitch audio source, needs a token
		playerManager.registerSourceManager(new YoutubeAudioSourceManager(true, new MusicWithThumbnail(), new WebWithThumbnail(), new AndroidWithThumbnail()));
		playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
		playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());

		// Setup Spotify audio source, needs a token
		if (Settings.getSettings().spotifyClientId != null && Settings.getSettings().spotifyClientSecret != null) {
			String clientSecret = Settings.getSettings().spotifyClientSecret;
			String clientId = Settings.getSettings().spotifyClientId;
			String spotifyCountryCode = Settings.getSettings().spotifyCountryCode;
			
			CountryCode countryCode = CountryCode.valueOf(spotifyCountryCode);
			SpotifyAudioSourceManager spotifyAudioSourceManager = new SpotifyAudioSourceManager(clientId, clientSecret, countryCode);
			playerManager.registerSourceManager(spotifyAudioSourceManager);
		}

		playerManager.registerSourceManager(new HttpAudioSourceManager());

		AudioSourceManagers.registerRemoteSources(this.playerManager);
	}
	
	public static synchronized PlayerManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PlayerManager();
		}
		return INSTANCE;
	}


	public void loadAndPlay(@NotNull SlashCommandInteractionEvent event, String query) {
		GuildAudioManager musicManager = GuildAudioManager.of(event.getGuild());
		Logger.info("Used search term \"" + query + "\"");
		
		this.playerManager.loadItemOrdered(this, query.trim(), new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				// audioTrack.setUserData(new TrackUserData(message));
				Member member = event.getMember();
                Settings settings = Settings.getSettings();

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("Playing", audioTrack.getInfo().uri, member.getAvatarUrl());
                embedBuilder.setColor(Color.decode(settings.defaultColors.get("success")));
                embedBuilder.setThumbnail(audioTrack.getInfo().artworkUrl);
                embedBuilder.addField("Title", audioTrack.getInfo().title, true);
                embedBuilder.addField("Duration", Format.millisecondsToString(audioTrack.getDuration()), true);
                if (musicManager.getPlayer().getPlayingTrack() != null) {
                    embedBuilder.setAuthor("Added to queue", null,  member.getAvatarUrl());
                }

				event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
				musicManager.getScheduler().queue(audioTrack);
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				if (audioPlaylist.isSearchResult()) {
					this.trackLoaded(audioPlaylist.getTracks().getFirst());
					return;
				}
				
				Member member = event.getMember();
				assert member != null;

				Settings settings = Settings.getSettings();

				EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("Playing", null, member.getAvatarUrl());
                embedBuilder.setColor(Color.decode(settings.defaultColors.get("success")));
                embedBuilder.setThumbnail(audioPlaylist.getTracks().getFirst().getInfo().artworkUrl);
                embedBuilder.addField("Playlist name", audioPlaylist.getName(), false);
                embedBuilder.addField("First track", audioPlaylist.getTracks().getFirst().getInfo().title, false);
                embedBuilder.addField("Playlist size", String.valueOf(audioPlaylist.getTracks().size()), false);
                if (musicManager.getPlayer().getPlayingTrack() != null) {
                    embedBuilder.setAuthor("Added to queue", null, member.getAvatarUrl());
                }

				event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
				
				for (AudioTrack track : audioPlaylist.getTracks()) {
					musicManager.getScheduler().queue(track);
				}
			}
			
			@Override
			public void noMatches() {
				event.getHook().editOriginal("No tracks found with that query: " + query).queue();
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				event.getHook().editOriginal("Failed to load track...").queue();
				exception.printStackTrace();
			}
		});
	}
}
