package com.iog.MusicPlayer;

import com.iog.Utils.Format;
import com.iog.Utils.Settings;
import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.spotify.SpotifyAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import org.tinylog.Logger;

public class PlayerManager {
	private static PlayerManager INSTANCE;
	public final AudioPlayerManager playerManager;
	
	public PlayerManager() {
		this.playerManager = new DefaultAudioPlayerManager();
		this.playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		this.playerManager.setUseSeekGhosting(true);
		this.playerManager.setFrameBufferDuration(1000);
		
		// Setup YouTube, SoundCloud and Twitch audio source, needs a token
		playerManager.registerSourceManager(new YoutubeAudioSourceManager());
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
		
		
		// Setup local and http audio sources
		playerManager.registerSourceManager(new LocalAudioSourceManager());
		playerManager.registerSourceManager(new HttpAudioSourceManager());
		
		AudioSourceManagers.registerRemoteSources(this.playerManager);
		AudioSourceManagers.registerLocalSource(this.playerManager);
	}
	
	public static synchronized PlayerManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PlayerManager();
		}
		return INSTANCE;
	}
	
	/**
	 * Loads and plays the track found with variable query
	 *
	 * @param message     Message object
	 * @param interaction Interaction object
	 * @param query       Link or a search query
	 */
	public void loadAndPlay(Message message, ChatInputInteractionEvent interaction, String query) {
		GuildAudioManager musicManager = GuildAudioManager.of(getGuildId(message, interaction));
		Logger.info("Used search term \"" + query + "\"");
		
		this.playerManager.loadItemOrdered(this, query.trim(), new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				// audioTrack.setUserData(new TrackUserData(message));
				
				Member member = getMember(message, interaction);
				EmbedCreateSpec.Builder specBuilder = EmbedCreateSpec.builder()
					.author("Playing", audioTrack.getInfo().uri, member.getAvatarUrl())
					.color(Format.hexToColor(Settings.getSettings().defaultColors.get("success")))
					.thumbnail(audioTrack.getInfo().artworkUrl)
					.addField("Title", audioTrack.getInfo().title, true)
					.addField("Duration", Format.millisecondsToString(audioTrack.getDuration()), true);
				
				if (musicManager.getPlayer().getPlayingTrack() != null) {
					specBuilder.author("Added to queue", null, message.getAuthor().orElseThrow().getAvatarUrl());
				}
				
				sendMessage(message, interaction, specBuilder.build());
				musicManager.getScheduler().queue(audioTrack);
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				if (audioPlaylist.isSearchResult()) {
					this.trackLoaded(audioPlaylist.getTracks().get(0));
					return;
				}
				
				
				Member member = getMember(message, interaction);
				EmbedCreateSpec.Builder specBuilder = EmbedCreateSpec.builder()
					.author("Playing", audioPlaylist.getPlaylistUrl(), member.getAvatarUrl())
					.color(Format.hexToColor(Settings.getSettings().defaultColors.get("success")))
					.thumbnail(audioPlaylist.getTracks().get(0).getInfo().artworkUrl)
					.addField("Playlist name", audioPlaylist.getName(), false)
					.addField("First track", audioPlaylist.getTracks().get(0).getInfo().title, false)
					.addField("Playlist size", String.valueOf(audioPlaylist.getTracks().size()), false);
				
				if (musicManager.getPlayer().getPlayingTrack() != null) {
					specBuilder.author("Added to queue", null, member.getAvatarUrl());
				}
				
				sendMessage(message, interaction, specBuilder.build());
				
				for (AudioTrack track : audioPlaylist.getTracks()) {
					musicManager.getScheduler().queue(track);
				}
			}
			
			@Override
			public void noMatches() {
				sendMessage(message, interaction, "No tracks found with that query: " + query);
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				sendMessage(message, interaction, "No tracks found with that query: " + query);
				exception.printStackTrace();
			}
		});
	}
	
	public void sendMessage(Message message, ChatInputInteractionEvent interactionEvent, EmbedCreateSpec embed) {
		if (interactionEvent != null) {
			interactionEvent.editReply().withEmbeds(embed).subscribe();
			return;
		}
		
		message.getChannel().subscribe(channel -> channel.createMessage(embed).subscribe());
	}
	
	public void sendMessage(Message message, ChatInputInteractionEvent interactionEvent, String content) {
		if (interactionEvent != null) {
			interactionEvent.editReply(content).subscribe();
			return;
		}
		
		message.getChannel().subscribe(channel -> channel.createMessage(content).subscribe());
	}
	
	public Member getMember(Message message, ChatInputInteractionEvent interactionEvent) {
		if (interactionEvent != null) {
			return interactionEvent.getInteraction().getMember().orElseThrow();
		}
		
		return message.getAuthorAsMember().blockOptional().orElseThrow();
	}
	
	public Snowflake getGuildId(Message message, ChatInputInteractionEvent interactionEvent) {
		if (interactionEvent != null) {
			return interactionEvent.getInteraction().getGuildId().orElseThrow();
		}
		
		return message.getGuildId().orElseThrow();
	}
	
	
}
