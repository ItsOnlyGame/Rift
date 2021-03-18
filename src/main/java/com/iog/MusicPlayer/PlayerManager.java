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
import discord4j.core.object.entity.Message;
import org.apache.log4j.Logger;

public class PlayerManager {
    private static final transient Logger logger = Logger.getLogger(Settings.class);

    public final AudioPlayerManager playerManager;

    private static PlayerManager INSTANCE;
    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public PlayerManager() {
        this.playerManager = new DefaultAudioPlayerManager();
        this.playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        this.playerManager.setUseSeekGhosting(true);
        this.playerManager.setFrameBufferDuration(1000);

        // Setup Youtube, SoundCloud and Twitch audio source, needs a token
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());

        // Setup Spotify audio source, needs a token
        if (!Settings.getSettings().getSpotifyToken().equals("")) {
            final String[] logInData = Settings.getSettings().getSpotifyToken().split(":");
            CountryCode countryCode = CountryCode.valueOf(Settings.getSettings().getCountryCode());
            SpotifyAudioSourceManager spotifyAudioSourceManager = new SpotifyAudioSourceManager(logInData[0], logInData[1], countryCode);
            playerManager.registerSourceManager(spotifyAudioSourceManager);
        }

        // Setup local and http audio sources
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());

        AudioSourceManagers.registerRemoteSources(this.playerManager);
        AudioSourceManagers.registerLocalSource(this.playerManager);
    }

    public void loadAndPlay(Message message, String query) {
        GuildAudioManager musicManager = GuildAudioManager.of(message.getGuildId().orElseThrow());
        logger.info("Used search term \"" + query + "\"");

        this.playerManager.loadItemOrdered(this, query.trim(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                audioTrack.setUserData(message);
                message.getChannel().subscribe(channel -> {
                    if (musicManager.getPlayer().getPlayingTrack() == null) {
                        channel.createEmbed(spec ->
                                spec.setAuthor("Playing", null, message.getAuthor().orElseThrow().getAvatarUrl())
                                        .setColor(Settings.getSettings().getNormalColor())
                                        .setThumbnail(audioTrack.getInfo().artwork)
                                        .addField("Title", audioTrack.getInfo().title, true)
                                        .addField("Duration", Format.millisecondsToString(audioTrack.getDuration()), true)
                        ).subscribe();
                    } else {
                        channel.createEmbed(spec ->
                                spec.setAuthor("Added to queue", null, message.getAuthor().orElseThrow().getAvatarUrl())
                                        .setColor(Settings.getSettings().getNormalColor())
                                        .setThumbnail(audioTrack.getInfo().artwork)
                                        .addField("Title", audioTrack.getInfo().title, true)
                                        .addField("Duration", Format.millisecondsToString(audioTrack.getDuration()), true)
                        ).subscribe();
                    }
                });

                musicManager.getScheduler().queue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.isSearchResult()) {
                    this.trackLoaded(audioPlaylist.getTracks().get(0));
                    return;
                }

                message.getChannel().subscribe(channel -> {
                    if (musicManager.getPlayer().getPlayingTrack() == null) {

                        channel.createEmbed(spec ->
                                spec.setAuthor("Playing", null, message.getAuthor().orElseThrow().getAvatarUrl())
                                        .setColor(Settings.getSettings().getNormalColor())
                                        .setThumbnail(audioPlaylist.getArtwork())
                                        .addField("Playlist name", audioPlaylist.getName(), false)
                                        .addField("First track", audioPlaylist.getTracks().get(0).getInfo().title, false)
                                        .addField("Playlist size", String.valueOf(audioPlaylist.getTracks().size()), false)
                        ).subscribe();
                    } else {
                        channel.createEmbed(spec ->
                                spec.setAuthor("Added to queue", null, message.getAuthor().orElseThrow().getAvatarUrl())
                                        .setColor(Settings.getSettings().getNormalColor())
                                        .setThumbnail(audioPlaylist.getArtwork())
                                        .addField("Playlist name", audioPlaylist.getName(), false)
                                        .addField("First track", audioPlaylist.getTracks().get(0).getInfo().title, false)
                                        .addField("Playlist size", String.valueOf(audioPlaylist.getTracks().size()), false)
                        ).subscribe();
                    }
                });

                for (AudioTrack track : audioPlaylist.getTracks()) {
                    musicManager.getScheduler().queue(track);
                }

            }

            @Override
            public void noMatches() {
                message.getChannel().subscribe(channel -> {
                    channel.createMessage("No tracks found with that query: " + query).subscribe();
                });
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                message.getChannel().subscribe(channel -> {
                    channel.createMessage("Track failed to load: " + exception.getMessage()).subscribe();
                });
                exception.printStackTrace();
            }
        });
    }

}
