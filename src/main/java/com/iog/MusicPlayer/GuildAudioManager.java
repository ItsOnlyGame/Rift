package com.iog.MusicPlayer;

import com.iog.Handlers.GuildSettings;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord4j.common.util.Snowflake;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GuildAudioManager {

    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private final AudioPlayerSendHandler provider;

    private static final Map<Snowflake, GuildAudioManager> MANAGERS = new ConcurrentHashMap<>();

    public static GuildAudioManager of(final Snowflake id) {
        return MANAGERS.computeIfAbsent(id, ignored -> new GuildAudioManager(id));
    }

    private GuildAudioManager(final Snowflake id) {
        player = PlayerManager.getInstance().playerManager.createPlayer();
        scheduler = new TrackScheduler(player);
        provider = new AudioPlayerSendHandler(player);

        player.setVolume(GuildSettings.of(id.asLong()).getVolume());
        player.addListener(scheduler);
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public AudioPlayerSendHandler getProvider() {
        return provider;
    }
}