package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;
import com.iog.Utils.Settings;
import com.sedmelluq.discord.lavaplayer.source.spotify.SpotifyAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.Message;

public class Info extends MusicCommand {

    public Info() {
        super(new String[]{"info"}, "Gives info about the track playing");
    }

    @Override
    public void run(Message message, String[] args) throws CommandExecutionException {
        GuildAudioManager audioManager = GuildAudioManager.of(message.getGuildId().orElseThrow());
        AudioTrack track = audioManager.getPlayer().getPlayingTrack();

        if (track == null) {
            message.getChannel().subscribe(channel -> channel.createMessage("No tracks playing").subscribe());
            return;
        }

        message.getChannel().subscribe(channel -> channel.createEmbed(spec -> {
                    spec.setAuthor(track.getInfo().title, null, message.getAuthor().orElseThrow().getAvatarUrl());
                    spec.setColor(Settings.getSettings().getNormalColor());
                    spec.setThumbnail(track.getInfo().artwork);
                    spec.addField("Author", track.getInfo().author, true);
                    spec.addField("Duration", Format.millisecondsToString(track.getPosition()) +" - "+ Format.millisecondsToString(track.getDuration()), true);
                    spec.addField("URL", track.getInfo().uri, false);
                    if (track instanceof SpotifyAudioTrack) {
                        spec.addField("Youtube Url", ((SpotifyAudioTrack) track).getYoutubeInfo().uri, false);
                        spec.addField("Youtube Author", ((SpotifyAudioTrack) track).getYoutubeInfo().author, false);
                    }
                }).subscribe());


    }
}
