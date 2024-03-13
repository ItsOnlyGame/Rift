package com.iog.MusicPlayer;

import com.iog.Handlers.GuildSettings;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GuildAudioManager {
	
	private static final Map<Long, GuildAudioManager> MANAGERS = new ConcurrentHashMap<>();
	private final AudioPlayer player;
	private final TrackScheduler scheduler;
	private final AudioPlayerSendHandler provider;
	
	private GuildAudioManager(final long id) {
		player = PlayerManager.getInstance().playerManager.createPlayer();
		scheduler = new TrackScheduler(player);
		provider = new AudioPlayerSendHandler(player);
		
		player.setVolume(GuildSettings.of(id).getVolume());
		player.addListener(scheduler);
	}
	
	public static GuildAudioManager of(Guild guild) {
		if (!MANAGERS.containsKey(guild.getIdLong())) {
			GuildAudioManager audioManager = new GuildAudioManager(guild.getIdLong());
			guild.getAudioManager().setSendingHandler(audioManager.getSendHandler());
			MANAGERS.put(guild.getIdLong(), audioManager);
			return audioManager;
		}

		return MANAGERS.get(guild.getIdLong());
	}

	public static void remove(Guild guild) {
		GuildAudioManager audioManager = MANAGERS.get(guild.getIdLong());
		if (audioManager != null) {
			audioManager.getScheduler().getQueue().clear();
			MANAGERS.remove(guild.getIdLong());
		}
		guild.getAudioManager().setSendingHandler(null);
	}
	
	public AudioPlayer getPlayer() {
		return player;
	}
	
	public TrackScheduler getScheduler() {
		return scheduler;
	}
	
	public AudioPlayerSendHandler getSendHandler() {
		return provider;
	}
}